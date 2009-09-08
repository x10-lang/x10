#include <new>

#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cassert>

// from cstdio -- on BGP mpi.h wants these to be undefined.
#undef SEEK_SET
#undef SEEK_CUR
#undef SEEK_END

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
        exit(-1);
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
        exit(-1);
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

class x10rt_req {
        MPI_Request           mpi_req;
        x10rt_req           * next;
        x10rt_req           * prev;
        void                * buf;
    public:
        x10rt_req()  { next = prev = NULL;  buf = NULL; }
        ~x10rt_req() { next = prev = NULL;  buf = NULL; }
        MPI_Request * toMPI() { return &mpi_req; }
        void setBuf(void * buf) { this->buf = buf; }
        void * getBuf() { return buf; }
        friend class x10rt_req_queue;
};

class x10rt_req_queue {
        x10rt_req           * head;
        x10rt_req           * tail;
        int                   len;
    public:
        x10rt_req_queue()  { 
            head = tail = NULL; len = 0;
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
        void addRequests(int num) {
            for(int i=0; i<num; ++i) {
                char * mem = ChkAlloc<char>(sizeof(x10rt_req));
                x10rt_req * r = new(mem) x10rt_req();
                enqueue(r);
            }
        }
        void enqueue(x10rt_req * r) {
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
        }
        x10rt_req * pop() {
            x10rt_req * r = head;
            if(NULL != head) { 
                head = head->next; 
                len --; 
                if(NULL == head) {
                    tail = NULL; 
                    ASSERT(0 == len);
                }
            }
            return r;
        }
        void remove(x10rt_req * r) {
            ASSERT((NULL != head) && (NULL != tail) && (len > 0));
            if(r->prev) r->prev->next = r->next;
            if(r->next) r->next->prev = r->prev;
            if(r == head) head = r->next;
            if(r == tail) tail = r->prev;
            r->next = r->prev = NULL; 
            len --;
        }
        x10rt_req * popNoFail() {
            x10rt_req * r = pop();
            if(NULL == r) {
                addRequests(X10RT_REQ_BUMP);
                r = pop();
            }
            return r;
        }
};

typedef void (*amSendCb)(const x10rt_msg_params &);

class x10rt_internal_state {
    public:
        bool                init;
        int                 rank;
        int                 nprocs;
        int                 callbackTblSize;
        int                 argc;
        char             ** argv;
        amSendCb          * callbackTbl;
        x10rt_req_queue     free_req_list;
        x10rt_req_queue     pending_send_req_list;
        x10rt_req_queue     pending_recv_req_list;

        x10rt_internal_state() {
            init    = false;
            callbackTbl = ChkAlloc<amSendCb>(sizeof(amSendCb) * X10RT_CB_TBL_SIZE);
            callbackTblSize = X10RT_CB_TBL_SIZE;
            free_req_list.addRequests(X10RT_REQ_FREELIST_INIT_LEN);
        }
        ~x10rt_internal_state() {
            free(callbackTbl);
        }
};

static x10rt_internal_state     global_state;

void x10rt_set_args(int argc, char ** argv)
{
    global_state.argc = argc;
    global_state.argv = argv;
}

static void x10rt_ensure_init ()
{
    if(!global_state.init) {
        if(MPI_SUCCESS != MPI_Init(&global_state.argc, &global_state.argv)) {
            fprintf(stderr, "[%s:%d] Error in MPI_Init\n", __FILE__, __LINE__);
            exit(-1);
        }
        MPI_Comm_size(MPI_COMM_WORLD, &global_state.nprocs);
        MPI_Comm_rank(MPI_COMM_WORLD, &global_state.rank);
        global_state.init = true;
    }
}

void x10rt_register_msg_receiver (unsigned msg_type,
                                  void (*cb)(const x10rt_msg_params &))
{
    x10rt_ensure_init();

    if(msg_type >= global_state.callbackTblSize) {
        global_state.callbackTbl     = 
            ChkRealloc<amSendCb>(global_state.callbackTbl, sizeof(amSendCb)*msg_type);
        global_state.callbackTblSize = msg_type;
    }

    global_state.callbackTbl[msg_type] = cb;
}

void x10rt_register_put_receiver (unsigned msg_type,
                                  void *(*cb1)(const x10rt_msg_params &, unsigned long len),
                                  void (*cb2)(const x10rt_msg_params &, unsigned long len))
{
    x10rt_ensure_init();
}

void x10rt_register_get_receiver (unsigned msg_type,
                                  void *(*cb1)(const x10rt_msg_params &),
                                  void (*cb2)(const x10rt_msg_params &, unsigned long len))
{
    x10rt_ensure_init();
}

void x10rt_registration_complete (void)
{
    x10rt_ensure_init();
    MPI_Barrier(MPI_COMM_WORLD);
}

unsigned long x10rt_nplaces (void)
{
    ASSERT(global_state.init);
    return global_state.nprocs;
}

unsigned long x10rt_here (void)
{
    ASSERT(global_state.init);
    return global_state.rank;
}

void *x10rt_msg_realloc (void *old, size_t old_sz, size_t new_sz)
{
    return ChkRealloc<void>(old, new_sz);
}

void x10rt_send_msg (x10rt_msg_params & msg_params)
{
    x10rt_req * req;

    req = global_state.free_req_list.popNoFail();

    if(MPI_SUCCESS != MPI_Isend(msg_params.msg, 
                msg_params.len, MPI_BYTE, 
                msg_params.dest_place, 
                msg_params.type,
                MPI_COMM_WORLD, 
                req->toMPI())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        exit(-1);
    }
    req->setBuf(msg_params.msg);
    global_state.pending_send_req_list.enqueue(req);
}

void *x10rt_get_realloc (void *old, size_t old_sz, size_t new_sz)
{
}

void x10rt_send_get (x10rt_msg_params &, void *buf, unsigned long len)
{
}

void *x10rt_put_realloc (void *old, size_t old_sz, size_t new_sz)
{
}

void x10rt_send_put (x10rt_msg_params &, void *buf, unsigned long len)
{
}

static void check_pending_sends()
{
    int num_checked = 0;
    MPI_Status msg_status;
    x10rt_req_queue * q = &global_state.pending_send_req_list;
    x10rt_req * req = q->start();
    while((NULL != req) &&
            num_checked < X10RT_MAX_PEEK_DEPTH) {
        int complete = 0;
        x10rt_req * req_copy = req;
        if(MPI_SUCCESS != MPI_Test(req->toMPI(),
                    &complete,
                    &msg_status)) {
            fprintf(stderr, "[%s:%d] Error in MPI_Test\n", __FILE__, __LINE__);
            exit(-1);
        }
        req = q->next(req);
        if(complete) {
            free(req_copy->getBuf());
            q->remove(req_copy);
        } else {
            num_checked ++;
        }
    }
}

static void check_pending_recvs()
{
    int num_checked = 0;
    MPI_Status msg_status;
    x10rt_req_queue * q = &global_state.pending_recv_req_list;
    x10rt_req * req = q->start();
    while((NULL != req) &&
            num_checked < X10RT_MAX_PEEK_DEPTH) {
        int complete = 0;
        x10rt_req * req_copy = req;
        if(MPI_SUCCESS != MPI_Test(req->toMPI(),
                    &complete,
                    &msg_status)) {
            fprintf(stderr, "[%s:%d] Error in MPI_Test\n", __FILE__, __LINE__);
            exit(-1);
        }
        req = q->next(req);
        if(complete) {
            int ix = msg_status.MPI_TAG;
            amSendCb cb = global_state.callbackTbl[ix];
            x10rt_msg_params p = { x10rt_here(),
                                   ix,
                                   req_copy->getBuf(),
                                   get_recvd_bytes(&msg_status)
                                 };
            cb(p);
            free(req_copy->getBuf());
            q->remove(req_copy);
        } else {
            num_checked ++;
        }
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
        exit(-1);
    }

    if(arrived) {
        void * recv_buf = ChkAlloc<char>(get_recvd_bytes(&msg_status));
        x10rt_req * req = global_state.free_req_list.popNoFail();
        req->setBuf(recv_buf);
        if(MPI_Irecv(recv_buf, get_recvd_bytes(&msg_status), 
                    MPI_BYTE, 
                    msg_status.MPI_SOURCE, 
                    msg_status.MPI_TAG,
                    MPI_COMM_WORLD, 
                    req->toMPI())) {
        }
        global_state.pending_recv_req_list.enqueue(req);
    } else {
        check_pending_sends();
        check_pending_recvs();
    }
}

void x10rt_finalize (void)
{
    ASSERT(global_state.init);
    if(MPI_SUCCESS != MPI_Barrier(MPI_COMM_WORLD)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Barrier\n", __FILE__, __LINE__);
        exit(-1);
    }
    if(MPI_SUCCESS != MPI_Finalize()) {
        fprintf(stderr, "[%s:%d] Error in MPI_Finalize\n", __FILE__, __LINE__);
        exit(-1);
    }
}
