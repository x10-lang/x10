/*
 * (c) Copyright IBM Corporation 2009
 * 
 * $Id$
 *
 * This file is part of X10 Runtime on MPI layer implementation.
 */

/*
 * C++ Style Guide:
 * http://google-styleguide.googlecode.com/svn/trunk/cppguide.xml
 *
 * C++ Lint:
 * http://google-styleguide.googlecode.com/svn/trunk/cpplint/cpplint.py
 *
 */


/* MPICH2 mpi.h wants to not have SEEK_SET etc defined for C++ bindings */
#include <mpi.h>

#include <new>

#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cassert>

#include <pthread.h>
#include <errno.h>

#include <x10rt_api.h>

/* Init time constants */
#define X10RT_REQ_FREELIST_INIT_LEN     (256)
#define X10RT_REQ_BUMP                  (32)
#define X10RT_CB_TBL_SIZE               (128)
#define X10RT_MAX_PEEK_DEPTH            (16)
#define X10RT_MAX_OUTSTANDING_SENDS     (256)

/* Generic utility funcs */
template <class T> T* ChkAlloc(size_t len) {
    if (0 == len) return NULL;
    T * ptr;
    ptr = static_cast <T*> (malloc(len));
    if (NULL == ptr) {
        fprintf(stderr, "[%s:%d] No more memory\n",
                 __FILE__, __LINE__);
        abort();
    }
    return ptr;
}
template <class T> T* ChkRealloc(T * ptr, size_t len) {
    if (0 == len) {
        free(ptr);
        return NULL;
    }
    T * ptr2;
    ptr2 = static_cast <T*> (realloc(ptr, len));
    if (NULL == ptr2) {
        fprintf(stderr, "[%s:%d] No more memory\n", __FILE__, __LINE__);
        abort();
    }
    return ptr2;
}

/**
 * Get count of bytes received from MPI_Status
 */
static inline int get_recvd_bytes(MPI_Status * msg_status) {
    int received_bytes;
    MPI_Get_count(msg_status,
            MPI_BYTE,
            &received_bytes);
    return received_bytes;
}

static inline void get_lock(pthread_mutex_t * lock) {
    if(pthread_mutex_lock(lock)) {
        perror("pthead_mutex_lock");
        abort();
    }
}

static inline void release_lock(pthread_mutex_t * lock) {
    if(pthread_mutex_unlock(lock)) {
        perror("pthread_mutex_unlock");
        abort();
    }
}

/**
 * Each X10RT API call is broken down into
 * a X10RT request. Each request of either 
 * one of the following types
 */

typedef enum {
    X10RT_REQ_TYPE_SEND                 = 1,
    X10RT_REQ_TYPE_RECV                 = 2,
    X10RT_REQ_TYPE_GET_INCOMING_DATA    = 3,
    X10RT_REQ_TYPE_GET_OUTGOING_REQ     = 4,
    X10RT_REQ_TYPE_GET_INCOMING_REQ     = 5,
    X10RT_REQ_TYPE_GET_OUTGOING_DATA    = 6,
    X10RT_REQ_TYPE_PUT_OUTGOING_DATA    = 7,
    X10RT_REQ_TYPE_PUT_OUTGOING_REQ     = 8,
    X10RT_REQ_TYPE_PUT_INCOMING_REQ     = 9,
    X10RT_REQ_TYPE_PUT_INCOMING_DATA    = 10,
    X10RT_REQ_TYPE_UNDEFINED            = -1
} X10RT_REQ_TYPES;

typedef struct _x10rt_get_req {
    int                       type;
    int                       dest_place;
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

/* differentiate from x10rt_{get|put}_req
 * to save precious bytes from packet size
 * for each PUT/GET */
typedef struct _x10rt_nw_req {
    int                       type;
    int                       msg_len;
    int                       len;
} x10rt_nw_req;

class x10rt_req {
        int                   type;
        MPI_Request           mpi_req;
        x10rt_req           * next;
        x10rt_req           * prev;
        void                * buf;
        union {
            x10rt_get_req         get_req;
            x10rt_put_req         put_req;
        } u;
    public:
        x10rt_req()  {
            next = prev = NULL;
            buf = NULL;
            type = X10RT_REQ_TYPE_UNDEFINED;
        }
        ~x10rt_req() {
            next = prev = NULL;
            buf = NULL;
            type = X10RT_REQ_TYPE_UNDEFINED;
        }
        void setType(int t) { this->type = t; }
        int  getType() { return this->type; }
        MPI_Request * getMPIRequest() { return &this->mpi_req; }
        void setBuf(void * buf) { this->buf = buf; }
        void * getBuf() { return this->buf; }
        void setUserGetReq(x10rt_get_req * r) {
            u.get_req.type       = r->type;
            u.get_req.dest_place = r->dest_place;
            u.get_req.msg        = r->msg;
            u.get_req.msg_len    = r->msg_len;
            u.get_req.len        = r->len;
        }
        void setUserPutReq(x10rt_put_req * r) {
            u.put_req.type    = r->type;
            u.put_req.msg     = r->msg;
            u.put_req.msg_len = r->msg_len;
            u.put_req.len     = r->len;
        }
        x10rt_get_req * getUserGetReq() {
            assert(X10RT_REQ_TYPE_GET_INCOMING_DATA == type);
            return &u.get_req;
        }
        x10rt_put_req * getUserPutReq() {
            assert(X10RT_REQ_TYPE_PUT_INCOMING_DATA == type);
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
            len = 0;
            head = tail = NULL;
            if (pthread_mutex_init(&lock, NULL)) {
                perror("pthread_mutex_init");
                abort();
            }
        }
        ~x10rt_req_queue() {
            while (len > 0) {
                x10rt_req * r = pop();
                r->~x10rt_req();
            }
            assert((NULL == head) && (NULL == tail) && (0 == len));
            if (pthread_mutex_destroy(&lock)) {
                perror("pthread_mutex_destroy");
                abort();
            }
        }
        int length() { return len; }
        x10rt_req * start() {
            x10rt_req * r;
            get_lock(&this->lock);
            {
                r = head;
            }
            release_lock(&this->lock);
            return r;
        }
        x10rt_req * next(x10rt_req * r) {
            x10rt_req * n;
            get_lock(&this->lock);
            {
                n = r->next;
            }
            release_lock(&this->lock);
            return n;
        }
        /**
         * Append a few empty requests to queue
         */
        void addRequests(int num) {
            /* wrap around enqueue (which is thread safe) */
            for (int i = 0; i < num; ++i) {
                char * mem = ChkAlloc<char>(sizeof(x10rt_req));
                x10rt_req * r = new(mem) x10rt_req();
                enqueue(r);
            }
        }
        /**
         * Appends to end of queue
         */
        void enqueue(x10rt_req * r) {
            /* thread safe */

            get_lock(&this->lock);

            r->next     = NULL;
            if (head) {
                assert(NULL != tail);
                tail->next = r;
                r->prev = tail;
                tail = r;
            } else {
                assert(NULL == tail);
                r->prev = NULL;
                head = tail = r;
            }
            len++;

            release_lock(&this->lock);
        }
        /**
         * Removes first element from queue
         */
        x10rt_req * pop() {
            /* thread safe */
            get_lock(&this->lock);

            x10rt_req * r = head;
            if (NULL != head) {
                head = head->next;
                len--;
                if (NULL == head) {
                    tail = NULL;
                    assert(0 == len);
                }
            }

            release_lock(&this->lock);
            return r;
        }
        /**
         * Removes a request from any location
         * in the queue
         */
        void remove(x10rt_req * r) {
            /* thread safe */
            get_lock(&this->lock);

            assert((NULL != head) && (NULL != tail) && (len > 0));
            if (r->prev) r->prev->next = r->next;
            if (r->next) r->next->prev = r->prev;
            if (r == head) head = r->next;
            if (r == tail) tail = r->prev;
            r->next = r->prev = NULL;
            len--;

            release_lock(&this->lock);
        }
        /**
         * Always returns a request from queue.
         * If queue is empty, adds more requests
         * to queue an returns first request
         */
        x10rt_req * popNoFail() {
            /* wrap around pop (which is thread safe) */
            x10rt_req * r = pop();
            if (NULL == r) {
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
        bool                init;
        bool                finalized;
        pthread_mutex_t     lock;
        bool                is_mpi_multithread;
        int                 rank;
        int                 nprocs;
        MPI_Comm            mpi_comm;
        amSendCb          * amCbTbl;
        unsigned            amCbTblSize;
        putCb1            * putCb1Tbl;
        putCb2            * putCb2Tbl;
        unsigned            putCbTblSize;
        getCb1            * getCb1Tbl;
        getCb2            * getCb2Tbl;
        unsigned            getCbTblSize;
        int                 _reserved_tag_get_data;
        int                 _reserved_tag_get_req;
        int                 _reserved_tag_put_data;
        int                 _reserved_tag_put_req;
        x10rt_req_queue     free_list;
        x10rt_req_queue     pending_send_list;
        x10rt_req_queue     pending_recv_list;

        x10rt_internal_state() {
            init                = false;
            finalized           = false;
            is_mpi_multithread  = false;
        }
        void Init() {
            init          = true;
            amCbTbl       =
                ChkAlloc<amSendCb>(sizeof(amSendCb) * X10RT_CB_TBL_SIZE);
            amCbTblSize   = X10RT_CB_TBL_SIZE;
            putCb1Tbl     =
                ChkAlloc<putCb1>(sizeof(putCb1) * X10RT_CB_TBL_SIZE);
            putCb2Tbl     =
                ChkAlloc<putCb2>(sizeof(putCb2) * X10RT_CB_TBL_SIZE);
            putCbTblSize  = X10RT_CB_TBL_SIZE;
            getCb1Tbl     =
                ChkAlloc<getCb1>(sizeof(getCb1) * X10RT_CB_TBL_SIZE);
            getCb2Tbl     =
                ChkAlloc<getCb2>(sizeof(getCb2) * X10RT_CB_TBL_SIZE);
            getCbTblSize  = X10RT_CB_TBL_SIZE;

            free_list.addRequests(X10RT_REQ_FREELIST_INIT_LEN);
            if (pthread_mutex_init(&lock, NULL)) {
                perror("pthread_mutex_init");
                abort();
            }
        }
        ~x10rt_internal_state() {
            free(amCbTbl);
            free(putCb1Tbl);
            free(putCb2Tbl);
            free(getCb1Tbl);
            free(getCb2Tbl);
            if (pthread_mutex_destroy(&lock)) {
                perror("pthread_mutex_destroy");
                abort();
            }
        }
};

static x10rt_internal_state     global_state;

void x10rt_init(int &argc, char ** &argv) {
    assert(!global_state.finalized);
    assert(!global_state.init);

    global_state.Init();

    int provided, required;
    if(NULL != getenv("X10RT_MPI_THREAD_MULTIPLE")) {
        global_state.is_mpi_multithread = true;
        if (MPI_SUCCESS != MPI_Init_thread(&argc, &argv, 
                    MPI_THREAD_MULTIPLE, &provided)) {
            fprintf(stderr, "[%s:%d] Error in MPI_Init\n", __FILE__, __LINE__);
            abort();
        }
        MPI_Comm_rank(MPI_COMM_WORLD, &global_state.rank);
        if (0 == global_state.rank) {
            if (MPI_THREAD_MULTIPLE != provided) {
                fprintf(stderr, "[%s:%d] Underlying MPI implementation"
                        " needs to provide MPI_THREAD_MULTIPLE threading level\n",
                        __FILE__, __LINE__);
                fprintf(stderr, "[%s:%d] Alternatively, you could unset env var"
                        " X10RT_MPI_THREAD_MULTIPLE from you environment\n",
                        __FILE__, __LINE__);
            }
        }
        if (MPI_SUCCESS != MPI_Finalize()) {
            fprintf(stderr, "[%s:%d] Error in MPI_Finalize\n",
                    __FILE__, __LINE__);
            abort();
        }
    } else {
        global_state.is_mpi_multithread = false;
        if (MPI_SUCCESS != MPI_Init(&argc, &argv)) {
            fprintf(stderr, "[%s:%d] Error in MPI_Init\n", __FILE__, __LINE__);
            abort();
        }
    }
    if (MPI_SUCCESS != MPI_Comm_size(MPI_COMM_WORLD, &global_state.nprocs)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Comm_size\n",
                __FILE__, __LINE__);
        abort();
    }
    if (MPI_SUCCESS != MPI_Comm_rank(MPI_COMM_WORLD, &global_state.rank)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Comm_rank\n",
                __FILE__, __LINE__);
        abort();
    }
}

void x10rt_register_msg_receiver(unsigned msg_type,
                                  void (*cb)(const x10rt_msg_params &)) {
    assert(global_state.init);
    assert(!global_state.finalized);
    if (msg_type >= global_state.amCbTblSize) {
        global_state.amCbTbl     =
            ChkRealloc<amSendCb>(global_state.amCbTbl,
                    sizeof(amSendCb)*(msg_type+1));
        global_state.amCbTblSize = msg_type+1;
    }

    global_state.amCbTbl[msg_type] = cb;
}

void x10rt_register_put_receiver(unsigned msg_type,
        void *(*cb1)(const x10rt_msg_params &, unsigned long len),
        void (*cb2)(const x10rt_msg_params &, unsigned long len)) {
    assert(global_state.init);
    assert(!global_state.finalized);
    if (msg_type >= global_state.putCbTblSize) {
        global_state.putCb1Tbl     =
            ChkRealloc<putCb1>(global_state.putCb1Tbl,
                    sizeof(putCb1)*(msg_type+1));
        global_state.putCb2Tbl     =
            ChkRealloc<putCb2>(global_state.putCb2Tbl,
                    sizeof(putCb2)*(msg_type+1));
        global_state.putCbTblSize  = msg_type+1;
    }

    global_state.putCb1Tbl[msg_type] = cb1;
    global_state.putCb2Tbl[msg_type] = cb2;
}

void x10rt_register_get_receiver(unsigned msg_type,
        void *(*cb1)(const x10rt_msg_params &),
        void (*cb2)(const x10rt_msg_params &, unsigned long len)) {
    assert(global_state.init);
    assert(!global_state.finalized);
    if (msg_type >= global_state.getCbTblSize) {
        global_state.getCb1Tbl     =
            ChkRealloc<getCb1>(global_state.getCb1Tbl,
                    sizeof(getCb1)*(msg_type+1));
        global_state.getCb2Tbl     =
            ChkRealloc<getCb2>(global_state.getCb2Tbl,
                    sizeof(getCb2)*(msg_type+1));
        global_state.getCbTblSize  = msg_type+1;
    }

    global_state.getCb1Tbl[msg_type] = cb1;
    global_state.getCb2Tbl[msg_type] = cb2;
}

void x10rt_registration_complete(void) {
    assert(global_state.init);
    assert(!global_state.finalized);
    /* Reserve tags for internal use */
    global_state._reserved_tag_put_req  = global_state.amCbTblSize + 1;
    global_state._reserved_tag_put_data = global_state.amCbTblSize + 2;
    global_state._reserved_tag_get_req  = global_state.amCbTblSize + 3;
    global_state._reserved_tag_get_data = global_state.amCbTblSize + 4;

    /* X10RT uses its own communicator so user messages don't
     * collide with internal messages (Mixed mode programming,
     * using MPI libraries ...) */
    if (MPI_Comm_split(MPI_COMM_WORLD, 0, global_state.rank,
                &global_state.mpi_comm)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Comm_split\n",
                __FILE__, __LINE__);
        abort();
    }

    if (MPI_SUCCESS != MPI_Barrier(global_state.mpi_comm)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Barrier\n",
                __FILE__, __LINE__);
        abort();
    }
}

unsigned long x10rt_nplaces(void) {
    assert(global_state.init);
    assert(!global_state.finalized);
    return global_state.nprocs;
}

unsigned long x10rt_here(void) {
    assert(global_state.init);
    assert(!global_state.finalized);
    return global_state.rank;
}

void *x10rt_msg_realloc(void *old, size_t old_sz, size_t new_sz) {
    return ChkRealloc<void>(old, new_sz);
}
void *x10rt_get_realloc(void *old, size_t old_sz, size_t new_sz) {
    return ChkRealloc<void>(old, new_sz);
}
void *x10rt_put_realloc(void *old, size_t old_sz, size_t new_sz) {
    return ChkRealloc<void>(old, new_sz);
}

void x10rt_send_msg(x10rt_msg_params & p) {
    assert(global_state.init);
    assert(!global_state.finalized);
    assert(p.type > 0);

    x10rt_req * req;
    req = global_state.free_list.popNoFail();
    static bool in_recursion = false;

    if (global_state.is_mpi_multithread) get_lock(&global_state.lock);
    if (MPI_SUCCESS != MPI_Isend(p.msg,
                p.len, MPI_BYTE,
                p.dest_place,
                p.type,
                global_state.mpi_comm,
                req->getMPIRequest())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        abort();
    }
    if (global_state.is_mpi_multithread) release_lock(&global_state.lock);

    if ((global_state.pending_send_list.length() > 
            X10RT_MAX_OUTSTANDING_SENDS) && !in_recursion) {
        /* Block this send until all pending sends
         * and receives have been completed. It is
         * OK as per X10RT semantics to block a send,
         * as long as we don't block x10rt_probe() */
        in_recursion = true;
        int complete = 0;
        MPI_Status msg_status;
        do {
            if (MPI_SUCCESS != MPI_Test(req->getMPIRequest(),
                        &complete,
                        &msg_status)) {
            }
            x10rt_probe();
        } while (!complete);
        global_state.free_list.enqueue(req);
        in_recursion = false;
    } else {
        req->setBuf(p.msg);
        req->setType(X10RT_REQ_TYPE_SEND);
        global_state.pending_send_list.enqueue(req);
    }
}

void x10rt_send_get(x10rt_msg_params &p,
        void *buf, unsigned long len) {
    int                 get_msg_len;
    x10rt_req         * req;
    x10rt_nw_req      * get_msg;
    x10rt_get_req       get_req;

    assert(global_state.init);
    assert(!global_state.finalized);
    get_req.type       = p.type;
    get_req.dest_place = p.dest_place;
    get_req.msg        = p.msg;
    get_req.msg_len    = p.len;
    get_req.len        = len;

    /*      GET Message
     * +-------------------------------------+
     * | type | msg_len | len | <- msg ... ->|
     * +-------------------------------------+
     *  <--- x10rt_nw_req --->
     */
    get_msg_len         = sizeof(*get_msg) + p.len;
    get_msg             = ChkAlloc<x10rt_nw_req>(get_msg_len);
    get_msg->type       = p.type;
    get_msg->msg_len    = p.len;
    get_msg->len        = len;

    /* pre-post a recv that matches the GET request */
    req = global_state.free_list.popNoFail();
    if (global_state.is_mpi_multithread) get_lock(&global_state.lock);
    if (MPI_Irecv(buf, len,
                MPI_BYTE,
                p.dest_place,
                global_state._reserved_tag_get_data,
                global_state.mpi_comm,
                req->getMPIRequest())) {
    }
    if (global_state.is_mpi_multithread) release_lock(&global_state.lock);
    req->setBuf(NULL);
    req->setUserGetReq(&get_req);
    req->setType(X10RT_REQ_TYPE_GET_INCOMING_DATA);
    global_state.pending_recv_list.enqueue(req);

    /* send the GET request */
    req = global_state.free_list.popNoFail();
    memcpy(static_cast <void *> (&get_msg[1]), p.msg, p.len);

    if (global_state.is_mpi_multithread) get_lock(&global_state.lock);
    if (MPI_SUCCESS != MPI_Isend(get_msg,
                get_msg_len,
                MPI_BYTE,
                p.dest_place,
                global_state._reserved_tag_get_req,
                global_state.mpi_comm,
                req->getMPIRequest())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        abort();
    }
    if (global_state.is_mpi_multithread) release_lock(&global_state.lock);
    req->setBuf(get_msg);
    req->setType(X10RT_REQ_TYPE_GET_OUTGOING_REQ);

    global_state.pending_send_list.enqueue(req);
}


void x10rt_send_put(x10rt_msg_params &p,
        void *buf, unsigned long len) {
    int put_msg_len;
    x10rt_put_req * put_msg;
    assert(global_state.init);
    assert(!global_state.finalized);

    x10rt_req * req = global_state.free_list.popNoFail();

    /*      PUT Message
     * +-------------------------------------------+
     * | type | msg | msg_len | len | <- msg ... ->|
     * +-------------------------------------------+
     *  <------ x10rt_put_req ----->
     */
    put_msg_len         = sizeof(*put_msg) + p.len;
    put_msg             = ChkAlloc<x10rt_put_req>(put_msg_len);
    put_msg->type       = p.type;
    put_msg->msg        = p.msg;
    put_msg->msg_len    = p.len;
    put_msg->len        = len;
    memcpy(static_cast <void *> (&put_msg[1]), p.msg, p.len);

    if (global_state.is_mpi_multithread) get_lock(&global_state.lock);
    if (MPI_SUCCESS != MPI_Isend(put_msg,
                put_msg_len,
                MPI_BYTE,
                p.dest_place,
                global_state._reserved_tag_put_req,
                global_state.mpi_comm,
                req->getMPIRequest())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        abort();
    }
    if (global_state.is_mpi_multithread) release_lock(&global_state.lock);
    req->setBuf(put_msg);
    req->setType(X10RT_REQ_TYPE_PUT_OUTGOING_REQ);
    global_state.pending_send_list.enqueue(req);

    req = global_state.free_list.popNoFail();

    if (global_state.is_mpi_multithread) get_lock(&global_state.lock);
    if (MPI_SUCCESS != MPI_Isend(buf,
                len,
                MPI_BYTE,
                p.dest_place,
                global_state._reserved_tag_put_data,
                global_state.mpi_comm,
                req->getMPIRequest())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        abort();
    }
    if (global_state.is_mpi_multithread) release_lock(&global_state.lock);
    req->setBuf(NULL);
    req->setType(X10RT_REQ_TYPE_PUT_OUTGOING_DATA);
    global_state.pending_send_list.enqueue(req);
}

static void send_completion(x10rt_req_queue * q,
        x10rt_req * req) {
    free(req->getBuf());
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void recv_completion(int ix, int bytes,
        x10rt_req_queue * q, x10rt_req * req) {
    assert(ix>0);
    amSendCb cb = global_state.amCbTbl[ix];
    assert(cb != NULL);
    x10rt_msg_params p = { x10rt_here(),
                           ix,
                           req->getBuf(),
                           bytes
                         };

    q->remove(req);

    assert(ix > 0);

    release_lock(&global_state.lock);
    {
        cb(p);
    }
    get_lock(&global_state.lock);

    free(req->getBuf());
    global_state.free_list.enqueue(req);
}

static void get_incoming_data_completion(x10rt_req_queue * q,
        x10rt_req * req) {
    x10rt_get_req * get_req = req->getUserGetReq();
    getCb2 cb = global_state.getCb2Tbl[get_req->type];
    x10rt_msg_params p = { get_req->dest_place,
                           get_req->type,
                           get_req->msg,
                           get_req->msg_len
                         };
    cb(p, get_req->len);
    free(get_req->msg);
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void get_outgoing_req_completion(x10rt_req_queue * q, x10rt_req * req) {
    free(req->getBuf());
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void get_incoming_req_completion(int dest_place,
        x10rt_req_queue * q, x10rt_req * req) {
    /*      GET Message
     * +-------------------------------------+
     * | type | msg_len | len | <- msg ... ->|
     * +-------------------------------------+
     *  <--- x10rt_nw_req --->
     */
    x10rt_nw_req * get_nw_req = static_cast <x10rt_nw_req *> (req->getBuf());
    int len = get_nw_req->len;
    getCb1 cb = global_state.getCb1Tbl[get_nw_req->type];
    x10rt_msg_params p = { x10rt_here(),
                           get_nw_req->type,
                           static_cast <void *> (&get_nw_req[1]),
                           get_nw_req->msg_len
                         };
    void * local = cb(p);
    q->remove(req);
    free(req->getBuf());

    if (global_state.is_mpi_multithread) get_lock(&global_state.lock);
    /* reuse request for sending reply */
    if (MPI_SUCCESS != MPI_Isend(local,
                len,
                MPI_BYTE,
                dest_place,
                global_state._reserved_tag_get_data,
                global_state.mpi_comm,
                req->getMPIRequest())) {
        fprintf(stderr, "[%s:%d] Error in MPI_Isend\n", __FILE__, __LINE__);
        abort();
    }
    if (global_state.is_mpi_multithread) release_lock(&global_state.lock);
    req->setBuf(local);
    req->setType(X10RT_REQ_TYPE_GET_OUTGOING_DATA);

    global_state.pending_send_list.enqueue(req);
}

static void get_outgoing_data_completion(x10rt_req_queue * q,
        x10rt_req * req) {
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void put_outgoing_req_completion(x10rt_req_queue * q,
        x10rt_req * req) {
    free(req->getBuf());
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void put_outgoing_data_completion(x10rt_req_queue * q,
        x10rt_req * req) {
    q->remove(req);
    global_state.free_list.enqueue(req);
}

static void put_incoming_req_completion(int src_place,
        x10rt_req_queue * q,
        x10rt_req * req) {
    /*      PUT Message
     * +-------------------------------------------+
     * | type | msg | msg_len | len | <- msg ... ->|
     * +-------------------------------------------+
     *  <------ x10rt_put_req ----->
     */
    x10rt_put_req * put_req = static_cast <x10rt_put_req *> (req->getBuf());
    int len = put_req->len;
    putCb1 cb = global_state.putCb1Tbl[put_req->type];
    x10rt_msg_params p = { x10rt_here(),
                           put_req->type,
                           static_cast <void *> (&put_req[1]),
                           put_req->msg_len
                         };
    void * local = cb(p, len);
    q->remove(req);

    req->setUserPutReq(put_req);

    /* free the recv'd buf, now that we've copied info */
    free(req->getBuf());

    if (global_state.is_mpi_multithread) get_lock(&global_state.lock);
    /* reuse request for posting recv */
    if (MPI_SUCCESS != MPI_Irecv(local,
                len,
                MPI_BYTE,
                src_place,
                global_state._reserved_tag_put_data,
                global_state.mpi_comm,
                req->getMPIRequest())) {
        fprintf(stderr, "[%s:%d] Error in posting Irecv\n", __FILE__, __LINE__);
        abort();
    }
    if (global_state.is_mpi_multithread) release_lock(&global_state.lock);
    req->setType(X10RT_REQ_TYPE_PUT_INCOMING_DATA);
    global_state.pending_recv_list.enqueue(req);
}

static void put_incoming_data_completion(x10rt_req_queue * q, x10rt_req * req) {
    x10rt_put_req   * put_req = req->getUserPutReq();
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

/**
 * Checks pending sends to see if any completed.
 *
 * NOTE: This must be called with global_state.lock held
 */
static void check_pending_sends() {
    int num_checked = 0;
    MPI_Status msg_status;
    x10rt_req_queue * q = &global_state.pending_send_list;

    if (NULL == q->start()) return;

    x10rt_req * req = q->start();
    while ((NULL != req) &&
            num_checked < X10RT_MAX_PEEK_DEPTH) {
        int complete = 0;
        x10rt_req * req_copy = req;
        if (MPI_SUCCESS != MPI_Test(req->getMPIRequest(),
                    &complete,
                    &msg_status)) {
            fprintf(stderr, "[%s:%d] Error in MPI_Test\n", __FILE__, __LINE__);
            abort();
        }
        req = q->next(req);
        if (complete) {
            switch (req_copy->getType()) {
                case X10RT_REQ_TYPE_SEND:
                    send_completion(q, req_copy);
                    break;
                case X10RT_REQ_TYPE_GET_OUTGOING_REQ:
                    get_outgoing_req_completion(q, req_copy);
                    break;
                case X10RT_REQ_TYPE_GET_OUTGOING_DATA:
                    get_outgoing_data_completion(q, req_copy);
                    break;
                case X10RT_REQ_TYPE_PUT_OUTGOING_REQ:
                    put_outgoing_req_completion(q, req_copy);
                    break;
                case X10RT_REQ_TYPE_PUT_OUTGOING_DATA:
                    put_outgoing_data_completion(q, req_copy);
                    break;
                default:
                    fprintf(stderr, "[%s:%d] Unknown completion of type %d, exiting\n",
                            __FILE__, __LINE__, req_copy->getType());
                    abort();
                    break;
            };
            req = q->start();
        } else {
            num_checked++;
        }
    }
}

/**
 * Checks pending receives to see if any completed.
 *
 * NOTE: This must be called with global_state.lock held
 */
static void check_pending_receives() {
    MPI_Status msg_status;
    x10rt_req_queue * q = &global_state.pending_recv_list;

    if (NULL == q->start()) return;

    x10rt_req * req = q->start();
    while (NULL != req) {
        int complete = 0;
        x10rt_req * req_copy = req;
        if (MPI_SUCCESS != MPI_Test(req->getMPIRequest(),
                    &complete,
                    &msg_status)) {
            fprintf(stderr, "[%s:%d] Error in MPI_Test\n", __FILE__, __LINE__);
            abort();
        }
        req = q->next(req);
        if (complete) {
            switch (req_copy->getType()) {
                case X10RT_REQ_TYPE_RECV:
                    recv_completion(msg_status.MPI_TAG,
                            get_recvd_bytes(&msg_status), q, req_copy);
                    break;
                case X10RT_REQ_TYPE_GET_INCOMING_DATA:
                    get_incoming_data_completion(q, req_copy);
                    break;
                case X10RT_REQ_TYPE_GET_INCOMING_REQ:
                    get_incoming_req_completion(msg_status.MPI_SOURCE,
                            q, req_copy);
                    break;
                case X10RT_REQ_TYPE_PUT_INCOMING_REQ:
                    put_incoming_req_completion(msg_status.MPI_SOURCE,
                            q, req_copy);
                    break;
                case X10RT_REQ_TYPE_PUT_INCOMING_DATA:
                    put_incoming_data_completion(q, req_copy);
                    break;
                default:
                    fprintf(stderr, "[%s:%d] Unknown completion of type %d, exiting\n",
                            __FILE__, __LINE__, req_copy->getType());
                    abort();
                    break;
            };
            req = q->start();
        }
    }
}

void x10rt_remote_xor(unsigned long place,
                       unsigned long long addr, long long update) {
    fprintf(stderr, "x10rt_remote_xor on MPI currently unimplemented\n");
    abort();
}

void x10rt_remote_op_fence(void) {
    fprintf(stderr, "x10rt_remote_op_fence on MPI currently unimplemented\n");
    abort();
}

void x10rt_probe(void) {
    int arrived;
    MPI_Status msg_status;

    assert(global_state.init);
    assert(!global_state.finalized);

    get_lock(&global_state.lock);

    do {
        arrived = 0;
        if (MPI_SUCCESS != MPI_Iprobe(MPI_ANY_SOURCE,
                    MPI_ANY_TAG, global_state.mpi_comm,
                    &arrived, &msg_status)) {
            fprintf(stderr, "[%s:%d] Error probing MPI\n", __FILE__, __LINE__);
            abort();
        }

        /* Post recv for incoming message */
        if (arrived &&
                global_state._reserved_tag_put_data != msg_status.MPI_TAG) {
            /* Don't need to post recv for incoming puts, they
             * will be matched by X10RT_PUT_INCOMING_REQ handler */
            void * recv_buf = ChkAlloc<char>(get_recvd_bytes(&msg_status));
            int tag = msg_status.MPI_TAG;
            x10rt_req * req = global_state.free_list.popNoFail();
            req->setBuf(recv_buf);
            if (MPI_SUCCESS != MPI_Irecv(recv_buf,
                        get_recvd_bytes(&msg_status),
                        MPI_BYTE,
                        msg_status.MPI_SOURCE,
                        msg_status.MPI_TAG,
                        global_state.mpi_comm,
                        req->getMPIRequest())) {
                fprintf(stderr, "[%s:%d] Error in posting Irecv\n",
                        __FILE__, __LINE__);
                abort();
            }
            if (tag == global_state._reserved_tag_get_req) {
                req->setType(X10RT_REQ_TYPE_GET_INCOMING_REQ);
            } else if (tag == global_state._reserved_tag_put_req) {
                req->setType(X10RT_REQ_TYPE_PUT_INCOMING_REQ);
            } else {
                req->setType(X10RT_REQ_TYPE_RECV);
            }
            global_state.pending_recv_list.enqueue(req);
        } else {
            check_pending_sends();
            check_pending_receives();
        }
    } while (arrived);

    release_lock(&global_state.lock);
}

void x10rt_finalize(void) {
    assert(global_state.init);
    assert(!global_state.finalized);

    while (global_state.pending_send_list.length() > 0 ||
            global_state.pending_recv_list.length() > 0) {
        x10rt_probe();
    }
    if (global_state.is_mpi_multithread) get_lock(&global_state.lock);
    if (MPI_SUCCESS != MPI_Barrier(global_state.mpi_comm)) {
        fprintf(stderr, "[%s:%d] Error in MPI_Barrier\n", __FILE__, __LINE__);
        abort();
    }
    if (MPI_SUCCESS != MPI_Finalize()) {
        fprintf(stderr, "[%s:%d] Error in MPI_Finalize\n", __FILE__, __LINE__);
        abort();
    }
    if (global_state.is_mpi_multithread) release_lock(&global_state.lock);
    global_state.finalized = true;
}
