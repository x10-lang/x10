#include <new>

#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cassert>

// from cstdio -- on BGP mpi.h wants these to be undefined.
#undef SEEK_SET
#undef SEEK_CUR
#undef SEEK_END

#include <pthread.h>
#include <errno.h>

#include <mpi.h>

#include <x10rt_api.h>

/* Init time constants */
#define X10RT_REQ_FREELIST_INIT_LEN     (256)
#define X10RT_REQ_BUMP                  (32)
#define X10RT_CB_TBL_SIZE               (128)
#define X10RT_MAX_PEEK_DEPTH            (16)

/* Debug macros */
#ifdef ENABLE_ASSERT
#define ASSERT(_x) assert(_x)
#else
#define ASSERT(_x)
#endif

/* Generic utility funcs */
template <class T> T* ChkAlloc (size_t len) {
    if(0 == len) return NULL;
    T * ptr;
    ptr = (T*) malloc(len);
    if(NULL == ptr) {  
        fprintf(stderr, "[%s:%d] No more memory\n", 
                 __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    return ptr;
}
template <class T> T* ChkRealloc (T * ptr, size_t len) {
    if (0 == len) {
        free(ptr);
        return NULL;
    }
    T * ptr2;
    ptr2 = (T*) realloc(ptr, len);
    if(NULL == ptr2) {  
        fprintf(stderr, "[%s:%d] No more memory\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    return ptr2;
}

static inline int get_recvd_bytes(MPI_Status * status)
{
    int recvd_bytes = 0;
    MPI_Get_count(status, 
            MPI_BYTE, 
            &recvd_bytes);
    return recvd_bytes;
}

/* Internal data structs */

enum { X10RT_SEND       = 1, 
    X10RT_RECV          = 2, 
    X10RT_PUT_SEND      = 111,
    X10RT_PUT_RECV      = 222, 
    X10RT_PUT_SEND_REQ  = 333,
    X10RT_PUT_RECV_REQ  = 444,
    X10RT_GET_SEND      = 555, 
    X10RT_GET_RECV      = 666,
    X10RT_GET_REQ       = 777,
    X10RT_GET_REQ_REPLY = 888 
};

typedef struct _x10rt_get_req {
    int                       type;
    void                    * msg;
    int                       msg_len;
    int                       len;
} x10rt_get_req;

typedef struct _x10rt_put_req {
    int                       type;
    void                    * msg;
    int                       msg_len;
    int                       len;
} x10rt_put_req;

class x10rt_req {
        MPI_Request           mpi_req;
        x10rt_req           * next;
        x10rt_req           * prev;
        void                * buf;
        union {
            x10rt_get_req         get_req;
            x10rt_put_req         put_req;
        }u;
    public:
        x10rt_req()  { next = prev = NULL;  buf = NULL; }
        ~x10rt_req() { next = prev = NULL;  buf = NULL; }
        MPI_Request * toMPI() { return &mpi_req; }
        void setBuf(void * buf) { this->buf = buf; }
        void * getBuf() { return buf; }
        void setGetReq(x10rt_get_req * r) {
            u.get_req.type    = r->type;
            u.get_req.msg     = r->msg;
            u.get_req.msg_len = r->msg_len;
            u.get_req.len     = r->len;
        }
        void setPutReq(x10rt_put_req * r) {
            u.put_req.type    = r->type;
            u.put_req.msg     = r->msg;
            u.put_req.msg_len = r->msg_len;
            u.put_req.len     = r->len;
        }
        x10rt_get_req * getGetReq() {
            return &u.get_req;
        }
        x10rt_put_req * getPutReq() {
            return &u.put_req;
        }
        friend class x10rt_req_queue;
};

class x10rt_req_queue {
        pthread_mutex_t       lock;
        x10rt_req           * head;
        x10rt_req           * tail;
        int                   len;
    public:
        x10rt_req_queue()  { 
            head = tail = NULL; len = 0;
            if(pthread_mutex_init(&lock, NULL)) {
                perror("pthread_mutex_init");
                exit(EXIT_FAILURE);
            }
        }
        ~x10rt_req_queue() { 
            while(len > 0) {
                x10rt_req * r = pop();
                r->~x10rt_req();
            }
            ASSERT((NULL == head) && (NULL == tail) && (0 == len));
        }
        x10rt_req * start() {
            return head;
        }
        x10rt_req * next(x10rt_req * r) {
            return r->next;
        }
        void addRequests(int num) {         /* wrap around enqueue (which is thread safe) */
            for(int i=0; i<num; ++i) {
                char * mem = ChkAlloc<char>(sizeof(x10rt_req));
                x10rt_req * r = new(mem) x10rt_req();
                enqueue(r);
            }
        }
        void enqueue(x10rt_req * r) {       /* thread safe */

            if(pthread_mutex_lock(&lock)) {
                perror("pthread_mutex_lock");
                exit(EXIT_FAILURE);
            }

            r->next     = NULL;
            if(head) {
                ASSERT(NULL != tail);
                tail->next = r;
                r->prev = tail;
                tail = r;
            } else {
                ASSERT(NULL == tail);
                r->prev = NULL; 
                head = tail = r;
            }
            len ++;

            if(pthread_mutex_unlock(&lock)) {
                perror("pthread_mutex_unlock");
                exit(EXIT_FAILURE);
            }
        }
        x10rt_req * pop() {                 /* thread safe */
            if(pthread_mutex_lock(&lock)) {
                perror("pthread_mutex_lock");
                exit(EXIT_FAILURE);
            }

            x10rt_req * r = head;
            if(NULL != head) { 
                head = head->next; 
                len --; 
                if(NULL == head) {
                    tail = NULL; 
                    ASSERT(0 == len);
                }
            }

            if(pthread_mutex_unlock(&lock)) {
                perror("pthread_mutex_unlock");
                exit(EXIT_FAILURE);
            }
            return r;
        }
        void remove(x10rt_req * r) {        /* thread safe */
            if(pthread_mutex_lock(&lock)) {
                perror("pthread_mutex_lock");
                exit(EXIT_FAILURE);
            }

            ASSERT((NULL != head) && (NULL != tail) && (len > 0));
            if(r->prev) r->prev->next = r->next;
            if(r->next) r->next->prev = r->prev;
            if(r == head) head = r->next;
            if(r == tail) tail = r->prev;
            r->next = r->prev = NULL; 
            len --;

            if(pthread_mutex_unlock(&lock)) {
                perror("pthread_mutex_unlock");
                exit(EXIT_FAILURE);
            }
        }
        x10rt_req * popNoFail() {           /* wrap around pop (which is thread safe) */
            x10rt_req * r = pop();
            if(NULL == r) {
                addRequests(X10RT_REQ_BUMP);
                r = pop();
            }
            return r;
        }
};

typedef void (*amSendCb)(const x10rt_msg_params &);
typedef void *(*putCb1)(const x10rt_msg_params &, unsigned long len);
typedef void (*putCb2)(const x10rt_msg_params &, unsigned long len);
typedef void *(*getCb1)(const x10rt_msg_params &);
typedef void (*getCb2)(const x10rt_msg_params &, unsigned long len);

class x10rt_internal_state {
    public:
        pthread_mutex_t     lock;
        int                 rank;
        int                 nprocs;
        amSendCb          * amCbTbl;
        int                 amCbTblSize;
        putCb1            * putCb1Tbl;
        putCb2            * putCb2Tbl;
        int                 putCbTblSize;
        getCb1            * getCb1Tbl;
        getCb2            * getCb2Tbl;
        int                 getCbTblSize;
        x10rt_req_queue     free_list;
        x10rt_req_queue     pending_send_list;
        x10rt_req_queue     pending_recv_list;
        x10rt_req_queue     pending_get_send_list;
        x10rt_req_queue     pending_get_recv_list;
        x10rt_req_queue     pending_get_req_list;
        x10rt_req_queue     pending_get_req_reply_list;
        x10rt_req_queue     pending_put_send_list;
        x10rt_req_queue     pending_put_send_req_list;
        x10rt_req_queue     pending_put_recv_list;
        x10rt_req_queue     pending_put_recv_req_list;

        x10rt_internal_state() {
            amCbTbl             = ChkAlloc<amSendCb>(sizeof(amSendCb) * X10RT_CB_TBL_SIZE);
            amCbTblSize         = X10RT_CB_TBL_SIZE;
            putCb1Tbl           = ChkAlloc<putCb1>(sizeof(putCb1) * X10RT_CB_TBL_SIZE);
            putCb2Tbl           = ChkAlloc<putCb2>(sizeof(putCb2) * X10RT_CB_TBL_SIZE);
            putCbTblSize        = X10RT_CB_TBL_SIZE;
            getCb1Tbl           = ChkAlloc<getCb1>(sizeof(getCb1) * X10RT_CB_TBL_SIZE);
            getCb2Tbl           = ChkAlloc<getCb2>(sizeof(getCb2) * X10RT_CB_TBL_SIZE);
            getCbTblSize        = X10RT_CB_TBL_SIZE;

            free_list.addRequests(X10RT_REQ_FREELIST_INIT_LEN);
            if(pthread_mutex_init(&lock, NULL)) {
                perror("pthread_mutex_init");
                exit(EXIT_FAILURE);
            }
        }
        ~x10rt_internal_state() {
            free(amCbTbl);
        }
};

static x10rt_internal_state     global_state;

void x10rt_init(int &argc, char ** &argv)
{
    if(MPI_SUCCESS != MPI_Init(&argc, &argv)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Init\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    MPI_Comm_size(MPI_COMM_WORLD, &global_state.nprocs);
    MPI_Comm_rank(MPI_COMM_WORLD, &global_state.rank);
    if(MPI_SUCCESS != MPI_Barrier(MPI_COMM_WORLD)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Barrier\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
}

void x10rt_register_msg_receiver (unsigned msg_type,
                                  void (*cb)(const x10rt_msg_params &))
{
    if(msg_type >= global_state.amCbTblSize) {
        global_state.amCbTbl     = 
            ChkRealloc<amSendCb>(global_state.amCbTbl, sizeof(amSendCb)*msg_type);
        global_state.amCbTblSize = msg_type;
    }

    global_state.amCbTbl[msg_type] = cb;
}

void x10rt_register_put_receiver (unsigned msg_type,
                                  void *(*cb1)(const x10rt_msg_params &, unsigned long len),
                                  void (*cb2)(const x10rt_msg_params &, unsigned long len))
{
    if(msg_type >= global_state.putCbTblSize) {
        global_state.putCb1Tbl     = 
            ChkRealloc<putCb1>(global_state.putCb1Tbl, sizeof(putCb1)*msg_type);
        global_state.putCb2Tbl     = 
            ChkRealloc<putCb2>(global_state.putCb2Tbl, sizeof(putCb2)*msg_type);
        global_state.putCbTblSize  = msg_type;
    }

    global_state.putCb1Tbl[msg_type] = cb1;
    global_state.putCb2Tbl[msg_type] = cb2;
}

void x10rt_register_get_receiver (unsigned msg_type,
                                  void *(*cb1)(const x10rt_msg_params &),
                                  void (*cb2)(const x10rt_msg_params &, unsigned long len))
{
    if(msg_type >= global_state.getCbTblSize) {
        global_state.getCb1Tbl     = 
            ChkRealloc<getCb1>(global_state.getCb1Tbl, sizeof(getCb1)*msg_type);
        global_state.putCb2Tbl     = 
            ChkRealloc<getCb2>(global_state.getCb2Tbl, sizeof(getCb2)*msg_type);
        global_state.getCbTblSize  = msg_type;
    }

    global_state.getCb1Tbl[msg_type] = cb1;
    global_state.getCb2Tbl[msg_type] = cb2;
}

void x10rt_registration_complete (void)
{
    if(MPI_SUCCESS != MPI_Barrier(MPI_COMM_WORLD)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Barrier\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
}

unsigned long x10rt_nplaces (void)
{
    return global_state.nprocs;
}

unsigned long x10rt_here (void)
{
    return global_state.rank;
}

void *x10rt_msg_realloc (void *old, size_t old_sz, size_t new_sz)
{
    return ChkRealloc<void>(old, new_sz);
}
void *x10rt_get_realloc (void *old, size_t old_sz, size_t new_sz)
{
    return ChkRealloc<void>(old, new_sz);
}
void *x10rt_put_realloc (void *old, size_t old_sz, size_t new_sz)
{
    return ChkRealloc<void>(old, new_sz);
}

void x10rt_send_msg (x10rt_msg_params & p)
{
    x10rt_req * req;
    req = global_state.free_list.popNoFail();

    if(MPI_SUCCESS != MPI_Isend(p.msg, 
                p.len, MPI_BYTE, 
                p.dest_place, 
                p.type,
                MPI_COMM_WORLD, 
                req->toMPI())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    req->setBuf(p.msg);
    global_state.pending_send_list.enqueue(req);
}

void x10rt_send_get (x10rt_msg_params &p, void *buf, unsigned long len)
{
    int             get_msg_len;
    x10rt_req     * req;
    x10rt_get_req * get_msg;

    /*      GET Message
     * +-------------------------------------------+
     * | type | msg | msg_len | len | <- msg ... ->|
     * +-------------------------------------------+
     *  <------ x10rt_get_req ----->
     */
    get_msg_len         = sizeof(x10rt_get_req) + p.len;
    get_msg             = ChkAlloc<x10rt_get_req>(get_msg_len);
    get_msg->type       = p.type;
    get_msg->msg        = p.msg;
    get_msg->msg_len    = p.len;
    get_msg->len        = len;

    /* pre-post a recv that matches the GET request */
    req = global_state.free_list.popNoFail();
    if(MPI_Irecv(buf, len, 
                MPI_BYTE, 
                p.dest_place, 
                X10RT_GET_RECV,
                MPI_COMM_WORLD, 
                req->toMPI())) {
    }
    req->setGetReq(get_msg);
    global_state.pending_get_recv_list.enqueue(req);

    /* send the GET request */
    req = global_state.free_list.popNoFail();
    memcpy((void *) (&get_msg[1]), p.msg, p.len);

    if(MPI_SUCCESS != MPI_Isend(get_msg, 
                get_msg_len, 
                MPI_BYTE, 
                p.dest_place, 
                X10RT_GET_SEND,
                MPI_COMM_WORLD, 
                req->toMPI())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    req->setBuf((void *) get_msg);
    global_state.pending_get_send_list.enqueue(req);
}


void x10rt_send_put (x10rt_msg_params &p, void *buf, unsigned long len)
{
    int put_msg_len;
    x10rt_put_req * put_msg;

    x10rt_req * req = global_state.free_list.popNoFail();

    /*      PUT Message
     * +-------------------------------------------+
     * | type | msg | msg_len | len | <- msg ... ->|
     * +-------------------------------------------+
     *  <------ x10rt_put_req ----->
     */
    put_msg_len         = sizeof(x10rt_put_req) + p.len;
    put_msg             = ChkAlloc<x10rt_put_req>(put_msg_len);
    put_msg->type       = p.type;
    put_msg->msg        = p.msg;
    put_msg->msg_len    = p.len;
    put_msg->len        = len;
    memcpy((void *) (&put_msg[1]), p.msg, p.len);

    if(MPI_SUCCESS != MPI_Isend(put_msg, 
                put_msg_len, 
                MPI_BYTE, 
                p.dest_place, 
                X10RT_PUT_SEND_REQ,
                MPI_COMM_WORLD, 
                req->toMPI())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    req->setBuf(put_msg);
    global_state.pending_put_send_req_list.enqueue(req);

    req = global_state.free_list.popNoFail();

    if(MPI_SUCCESS != MPI_Isend(buf, 
                len, 
                MPI_BYTE, 
                p.dest_place, 
                X10RT_PUT_SEND,
                MPI_COMM_WORLD, 
                req->toMPI())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    req->setBuf(NULL);
    global_state.pending_put_send_list.enqueue(req);
}

static void send_completion(x10rt_req_queue * q, x10rt_req * req)
{
    free(req->getBuf());
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void recv_completion(int ix, int bytes, x10rt_req_queue * q, x10rt_req * req)
{
    amSendCb cb = global_state.amCbTbl[ix];
    x10rt_msg_params p = { x10rt_here(),
                           ix,
                           req->getBuf(),
                           bytes
                         };
    cb(p);
    free(req->getBuf());
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void get_recv_completion(x10rt_req_queue * q, x10rt_req * req)
{
    x10rt_get_req * get_req = req->getGetReq();
    getCb2 cb = global_state.getCb2Tbl[get_req->type];
    x10rt_msg_params p = { x10rt_here(),
                           get_req->type,
                           get_req->msg,
                           get_req->msg_len
                         };
    cb(p, get_req->len);
    free(get_req->msg);
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void get_send_completion(x10rt_req_queue * q, x10rt_req * req)
{
    free(req->getBuf());
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void get_req_completion(int dest_place, x10rt_req_queue * q, x10rt_req * req)
{
    /*      GET Message
     * +-------------------------------------------+
     * | type | msg | msg_len | len | <- msg ... ->|
     * +-------------------------------------------+
     *  <------ x10rt_get_req ----->
     */
    x10rt_get_req * get_req = (x10rt_get_req *) req->getBuf();
    int len = get_req->len;
    getCb1 cb = global_state.getCb1Tbl[get_req->type];
    x10rt_msg_params p = { x10rt_here(),
                           get_req->type,
                           (void *) &get_req[1],
                           get_req->msg_len
                         };
    void * local = cb(p);
    q->remove(req);
    free(req->getBuf());

    /* reuse request for sending reply */
    if(MPI_SUCCESS != MPI_Isend(local, 
                len, 
                MPI_BYTE, 
                dest_place, 
                X10RT_GET_RECV,
                MPI_COMM_WORLD, 
                req->toMPI())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }

    global_state.pending_get_req_reply_list.enqueue(req);
}

static void get_req_reply_completion(x10rt_req_queue * q, x10rt_req * req)
{
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void put_send_req_completion(x10rt_req_queue * q, x10rt_req * req)
{
    free(req->getBuf());
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void put_send_completion(x10rt_req_queue * q, x10rt_req * req)
{
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void put_recv_req_completion(int src_place, x10rt_req_queue * q, x10rt_req * req)
{
    /*      PUT Message
     * +-------------------------------------------+
     * | type | msg | msg_len | len | <- msg ... ->|
     * +-------------------------------------------+
     *  <------ x10rt_put_req ----->
     */
    x10rt_put_req * put_req = (x10rt_put_req *) req->getBuf();
    int len = put_req->len;
    putCb1 cb = global_state.putCb1Tbl[put_req->type];
    x10rt_msg_params p = { x10rt_here(),
                           put_req->type,
                           (void *) &put_req[1],
                           put_req->msg_len
                         };
    void * local = cb(p, len);
    q->remove(req);

    req->setPutReq(put_req);

    /* free the recv'd buf, now that we've copied info */
    free(req->getBuf());

    /* reuse request for posting recv */
    if(MPI_SUCCESS != MPI_Irecv(local, 
                len, 
                MPI_BYTE, 
                src_place, 
                X10RT_PUT_SEND,
                MPI_COMM_WORLD, 
                req->toMPI())) {
        fprintf(stderr, "[%s:%d] Error in posting Irecv\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    global_state.pending_put_recv_list.enqueue(req);
}

static void put_recv_completion(x10rt_req_queue * q, x10rt_req * req)
{
    x10rt_put_req   * put_req = req->getPutReq();
    putCb2 cb = global_state.putCb2Tbl[put_req->type];
    x10rt_msg_params p = { x10rt_here(),
                           put_req->type,
                           put_req->msg,
                           put_req->msg_len
                         };
    cb(p, put_req->len);
    free(put_req->msg);
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void check_pending(x10rt_req_queue * q, int type)
{
    int num_checked = 0;
    MPI_Status msg_status;

    if(NULL == q->start()) return;

    /* Only one thread looks at the pending queues at a time */

    if(pthread_mutex_lock(&global_state.lock)) {
        perror("pthread_mutex_lock");
        exit(EXIT_FAILURE);
    }

    x10rt_req * req = q->start();
    while((NULL != req) &&
            num_checked < X10RT_MAX_PEEK_DEPTH) {
        int complete = 0;
        x10rt_req * req_copy = req;
        if(MPI_SUCCESS != MPI_Test(req->toMPI(),
                    &complete,
                    &msg_status)) {
            fprintf(stderr, "[%s:%d] Error in MPI_Test\n", __FILE__, __LINE__);
            exit(EXIT_FAILURE);
        }
        req = q->next(req);
        if(complete) {
            switch (type) {
                case X10RT_SEND:
                    send_completion(q, req_copy);
                    break;
                case X10RT_RECV:
                    recv_completion(msg_status.MPI_TAG, 
                            get_recvd_bytes(&msg_status), q, req_copy);
                    break;
                case X10RT_GET_SEND:
                    get_send_completion(q, req_copy);
                    break;
                case X10RT_GET_REQ:
                    get_req_completion(msg_status.MPI_SOURCE, q, req_copy);
                    break;
                case X10RT_GET_REQ_REPLY:
                    get_req_reply_completion(q, req_copy);
                    break;
                case X10RT_GET_RECV:
                    get_recv_completion(q, req_copy);
                    break;
                case X10RT_PUT_SEND_REQ:
                    put_send_req_completion(q, req_copy);
                    break;
                case X10RT_PUT_SEND:
                    put_send_completion(q, req_copy);
                    break;
                case X10RT_PUT_RECV_REQ:
                    put_recv_req_completion(msg_status.MPI_SOURCE, q, req_copy);
                    break;
                case X10RT_PUT_RECV:
                    put_recv_completion(q, req_copy);
                    break;
                default:
                    fprintf(stderr, "Unknown completion!\n");
                    exit(EXIT_FAILURE);
                    break;
            };
        } else {
            num_checked ++;
        }
    }

    if(pthread_mutex_unlock(&global_state.lock)) {
        perror("pthread_mutex_lock");
        exit(EXIT_FAILURE);
    }
}

void x10rt_probe (void)
{
    int arrived = 0;
    MPI_Status msg_status;

    if(MPI_SUCCESS != MPI_Iprobe(MPI_ANY_SOURCE, 
                MPI_ANY_TAG, MPI_COMM_WORLD, &arrived, 
                &msg_status)) {
        fprintf(stderr, "[%s:%d] Error probing MPI\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }

    if(arrived && 
            X10RT_PUT_SEND != msg_status.MPI_TAG) {
        /* Don't need to post recv for incoming puts, they
         * will be matched by X10RT_PUT_SEND_REQ handler */
        void * recv_buf = ChkAlloc<char>(get_recvd_bytes(&msg_status));
        x10rt_req * req = global_state.free_list.popNoFail();
        req->setBuf(recv_buf);
        if(MPI_SUCCESS != MPI_Irecv(recv_buf, get_recvd_bytes(&msg_status), 
                    MPI_BYTE, 
                    msg_status.MPI_SOURCE, 
                    msg_status.MPI_TAG,
                    MPI_COMM_WORLD, 
                    req->toMPI())) {
            fprintf(stderr, "[%s:%d] Error in posting Irecv\n", __FILE__, __LINE__);
            exit(EXIT_FAILURE);
        }
        switch(msg_status.MPI_TAG) {
            case X10RT_GET_SEND:
                global_state.pending_get_req_list.enqueue(req);
                break;
            case X10RT_PUT_SEND_REQ:
                global_state.pending_put_recv_req_list.enqueue(req);
                break;
            default:
                global_state.pending_recv_list.enqueue(req);
        }
    } else {
        check_pending(&global_state.pending_send_list,          X10RT_SEND);
        check_pending(&global_state.pending_recv_list,          X10RT_RECV);
        check_pending(&global_state.pending_get_send_list,      X10RT_GET_SEND);
        check_pending(&global_state.pending_get_req_list,       X10RT_GET_REQ);
        check_pending(&global_state.pending_get_req_reply_list, X10RT_GET_REQ_REPLY);
        check_pending(&global_state.pending_get_recv_list,      X10RT_GET_RECV);
        check_pending(&global_state.pending_put_send_req_list,  X10RT_PUT_SEND_REQ);
        check_pending(&global_state.pending_put_send_list,      X10RT_PUT_SEND);
        check_pending(&global_state.pending_put_recv_req_list,  X10RT_PUT_RECV_REQ);
        check_pending(&global_state.pending_put_recv_list,      X10RT_PUT_RECV);
    }
}

void x10rt_finalize (void)
{
    if(MPI_SUCCESS != MPI_Barrier(MPI_COMM_WORLD)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Barrier\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
    if(MPI_SUCCESS != MPI_Finalize()) {
        fprintf(stderr, "[%s:%d] Error in MPI_Finalize\n", __FILE__, __LINE__);
        exit(EXIT_FAILURE);
    }
}
