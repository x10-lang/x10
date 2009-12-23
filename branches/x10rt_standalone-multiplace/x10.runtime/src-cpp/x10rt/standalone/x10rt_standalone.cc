#include <new>

#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <cassert>

#include <unistd.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <pthread.h>

#include <x10rt_net.h>

/* Init time constants */
#define X10RT_REQ_FREELIST_INIT_LEN     (256)
#define X10RT_REQ_BUMP                  (32)
#define X10RT_CB_TBL_SIZE               (128)
#define X10RT_MAX_PEEK_DEPTH            (16)

#define X10RT_NUM_PROCESSES "X10RT_NUM_PROCESSES"

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

/* Internal data structs */

typedef enum {
    X10RT_SEND                  = 1,
    X10RT_RECV                  = 2,
    X10RT_GET_INCOMING_DATA     = 3,
    X10RT_GET_OUTGOING_REQ      = 4,
    X10RT_GET_INCOMING_REQ      = 5,
    X10RT_GET_OUTGOING_DATA     = 6,
    X10RT_PUT_OUTGOING_DATA     = 7,
    X10RT_PUT_OUTGOING_REQ      = 8,
    X10RT_PUT_INCOMING_REQ      = 9,
    X10RT_PUT_INCOMING_DATA     = 10
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
        x10rt_req           * next;
        x10rt_req           * prev;
        void                * buf;
        union {
            x10rt_get_req         get_req;
            x10rt_put_req         put_req;
        } u;
    public:
        x10rt_req()  { next = prev = NULL;  buf = NULL; type = -1; }
        ~x10rt_req() { next = prev = NULL;  buf = NULL; type = -1; }
        void setType(int t) { type = t; }
        int  getType() { return type; }
        void setBuf(void * buf) { this->buf = buf; }
        void * getBuf() { return buf; }
        void setGetReq(x10rt_get_req * r) {
            u.get_req.type       = r->type;
            u.get_req.dest_place = r->dest_place;
            u.get_req.msg        = r->msg;
            u.get_req.msg_len    = r->msg_len;
            u.get_req.len        = r->len;
        }
        void setPutReq(x10rt_put_req * r) {
            u.put_req.type    = r->type;
            u.put_req.msg     = r->msg;
            u.put_req.msg_len = r->msg_len;
            u.put_req.len     = r->len;
        }
        x10rt_get_req * getGetReq() {
            assert(X10RT_GET_INCOMING_DATA == type);
            return &u.get_req;
        }
        x10rt_put_req * getPutReq() {
            assert(X10RT_PUT_INCOMING_DATA == type);
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
            assert((NULL == head) && (NULL == tail) && (0 == len));
            if(pthread_mutex_destroy(&lock)) {
                perror("pthread_mutex_destroy");
                exit(EXIT_FAILURE);
            }
        }
        x10rt_req * start() {
            x10rt_req * r;
            if(pthread_mutex_lock(&lock)) {
                perror("pthread_mutex_lock");
                exit(EXIT_FAILURE);
            }
            r = head;
            if(pthread_mutex_unlock(&lock)) {
                perror("pthread_mutex_unlock");
                exit(EXIT_FAILURE);
            }
            return r;
        }
        x10rt_req * next(x10rt_req * r) {
            x10rt_req * n;
            if(pthread_mutex_lock(&lock)) {
                perror("pthread_mutex_lock");
                exit(EXIT_FAILURE);
            }
            n = r->next;
            if(pthread_mutex_unlock(&lock)) {
                perror("pthread_mutex_unlock");
                exit(EXIT_FAILURE);
            }
            return n;
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
                assert(NULL != tail);
                tail->next = r;
                r->prev = tail;
                tail = r;
            } else {
                assert(NULL == tail);
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
                    assert(0 == len);
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

            assert((NULL != head) && (NULL != tail) && (len > 0));
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
typedef void *(*putCb1)(const x10rt_msg_params &, x10rt_copy_sz);
typedef void (*putCb2)(const x10rt_msg_params &, x10rt_copy_sz);
typedef void *(*getCb1)(const x10rt_msg_params &, x10rt_copy_sz);
typedef void (*getCb2)(const x10rt_msg_params &, x10rt_copy_sz);

class x10rt_internal_state {
    public:
        bool                init;
        bool                finalized;
        pthread_mutex_t     lock;
        unsigned long       rank;
        unsigned long       nprocs;
        pid_t             * procs;
        int               * pipes;
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
        x10rt_req_queue     pending_list;

        x10rt_internal_state() {
            init                = false;
            finalized           = false;
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
            free(putCb1Tbl);
            free(putCb2Tbl);
            free(getCb1Tbl);
            free(getCb2Tbl);
            if(pthread_mutex_destroy(&lock)) {
                perror("pthread_mutex_destroy");
                exit(EXIT_FAILURE);
            }
        }
};

static x10rt_internal_state     global_state;

static void propagate_sigint(int signum) {
    fprintf(stderr,"%lu: Caught SIGINT (%d)\n", global_state.rank, signum);
    if (global_state.rank != 0) exit(128+signum);
    for (unsigned long i = 1; i < global_state.nprocs; i++) {
        fprintf(stderr,"%lu: !!!Propagating SIGINT (%d) to %lu (%d)\n", global_state.rank, signum, i, global_state.procs[i]);
        kill(global_state.procs[i], signum);
    }
}

void x10rt_net_init (int &, char **&, x10rt_msg_type &)
{
    assert(!global_state.finalized);
    assert(!global_state.init);
    global_state.init = true;
    char* NPROCS = getenv(X10RT_NUM_PROCESSES);
    if (NPROCS == NULL) {
        fprintf(stderr, X10RT_NUM_PROCESSES " not set\n");
        abort();
    }
    global_state.nprocs = atol(NPROCS);
    global_state.rank = 0;
    global_state.procs = (pid_t*)malloc(global_state.nprocs*sizeof(pid_t));
    global_state.pipes = (int*)malloc(global_state.nprocs*2*sizeof(int));
    for (unsigned long i = 0; i < global_state.nprocs; i++) {
      if (pipe(&global_state.pipes[i*2])) {
        fprintf(stderr,"Pipe creation failed!\n");
        abort();
      }
      int flags;
      flags = fcntl(global_state.pipes[i*2], F_GETFL, 0);
      (void)fcntl(global_state.pipes[i*2], F_SETFL, flags | O_NDELAY);
      flags = fcntl(global_state.pipes[i*2+1], F_GETFL, 0);
      (void)fcntl(global_state.pipes[i*2+1], F_SETFL, flags | O_NDELAY);
      //fprintf(stderr,"!!!Created pipes from/to %d: (%d,%d)\n", i, global_state.pipes[i*2], global_state.pipes[i*2+1]);
    }
    for (unsigned long i = 1; i < global_state.nprocs; i++) {
        pid_t p = fork();
        if (p == -1) {
            fprintf(stderr,"Fork failed!\n");
            abort();
        } else if (p == 0) {
            global_state.rank = i;
            break;
        } else {
            global_state.procs[i] = p;
        }
    }
    signal(2, propagate_sigint);
    for (unsigned long i = 0; i < global_state.nprocs; i++) {
      if (i == global_state.rank) {
        close(global_state.pipes[i*2+1]); // write pipe
      } else {
        close(global_state.pipes[i*2+0]); // read pipe
      }
    }
    fprintf(stderr, "My id is %lu of %lu\n", x10rt_net_here(), x10rt_net_nhosts());
// TODO: barrier?
//    for (unsigned long i = 0; i < global_state.nprocs; i++) {
//      if (i == global_state.rank) continue;
//      char v = '\0';
//      write(global_state.pipes[i*2+1], &v, sizeof(char));
//    }
//    for (unsigned long i = 0; i < global_state.nprocs; i++) {
//      if (i == global_state.rank) continue;
//      char v;
//      read(global_state.pipes[i*2+0], &v, sizeof(char));
//    }
}

void x10rt_net_register_msg_receiver (x10rt_msg_type msg_type,
                                  void (*cb)(const x10rt_msg_params &))
{
    assert(global_state.init);
    assert(!global_state.finalized);
    if(msg_type >= global_state.amCbTblSize) {
        global_state.amCbTbl     = 
            ChkRealloc<amSendCb>(global_state.amCbTbl, sizeof(amSendCb)*(msg_type+1));
        global_state.amCbTblSize = msg_type+1;
    }

    global_state.amCbTbl[msg_type] = cb;
}

void x10rt_net_register_put_receiver (x10rt_msg_type msg_type,
                                  void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz),
                                  void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz))
{
    assert(global_state.init);
    assert(!global_state.finalized);
    if(msg_type >= global_state.putCbTblSize) {
        global_state.putCb1Tbl     = 
            ChkRealloc<putCb1>(global_state.putCb1Tbl, sizeof(putCb1)*(msg_type+1));
        global_state.putCb2Tbl     = 
            ChkRealloc<putCb2>(global_state.putCb2Tbl, sizeof(putCb2)*(msg_type+1));
        global_state.putCbTblSize  = msg_type+1;
    }

    global_state.putCb1Tbl[msg_type] = cb1;
    global_state.putCb2Tbl[msg_type] = cb2;
}

void x10rt_net_register_get_receiver (x10rt_msg_type msg_type,
                                  void *(*cb1)(const x10rt_msg_params &, x10rt_copy_sz),
                                  void (*cb2)(const x10rt_msg_params &, x10rt_copy_sz))
{
    assert(global_state.init);
    assert(!global_state.finalized);
    if(msg_type >= global_state.getCbTblSize) {
        global_state.getCb1Tbl     = 
            ChkRealloc<getCb1>(global_state.getCb1Tbl, sizeof(getCb1)*(msg_type+1));
        global_state.getCb2Tbl     = 
            ChkRealloc<getCb2>(global_state.getCb2Tbl, sizeof(getCb2)*(msg_type+1));
        global_state.getCbTblSize  = msg_type+1;
    }

    global_state.getCb1Tbl[msg_type] = cb1;
    global_state.getCb2Tbl[msg_type] = cb2;
}

void x10rt_net_internal_barrier (void)
{
    assert(global_state.init);
    assert(!global_state.finalized);
    /* Reserve tags for internal use */
    global_state._reserved_tag_put_req  = global_state.amCbTblSize + 1;
    global_state._reserved_tag_put_data = global_state.amCbTblSize + 2;
    global_state._reserved_tag_get_req  = global_state.amCbTblSize + 3;
    global_state._reserved_tag_get_data = global_state.amCbTblSize + 4;

    // FIXME: barrier
}

x10rt_place x10rt_net_nhosts (void)
{
    assert(global_state.init);
    assert(!global_state.finalized);
    return global_state.nprocs;
}

x10rt_place x10rt_net_here (void)
{
    assert(global_state.init);
    assert(!global_state.finalized);
    return global_state.rank;
}

void *x10rt_net_msg_realloc (void *old, size_t old_sz, size_t new_sz)
{
    return ChkRealloc<void>(old, new_sz);
}

static unsigned seq_num = 0;
void x10rt_net_send_msg (x10rt_msg_params & p)
{
    assert(global_state.init);
    assert(!global_state.finalized);
    assert(p.type > 0);
    assert(p.dest_place != global_state.rank);

    fprintf(stderr,"%lu: Sending message number %d of type %d with %lu bytes to %lu\n", global_state.rank, seq_num, p.type, p.len, p.dest_place);
    int pipe = global_state.pipes[p.dest_place*2+1];
    //fprintf(stderr, "%d: !!!Got pipe to %d: (%d)\n", global_state.rank, p.dest_place, pipe);
    // FIXME: synchronize writes to pipes using a sync pipe with blocking reads
    write(pipe, &global_state.rank, sizeof(global_state.rank));
    write(pipe, &seq_num, sizeof(seq_num));
    seq_num++;
    write(pipe, &p.type, sizeof(p.type));
    write(pipe, &p.len, sizeof(p.len));
    write(pipe, p.msg, p.len);
}

void *x10rt_net_get_realloc (void *old, size_t old_sz, size_t new_sz)
{
    return ChkRealloc<void>(old, new_sz);
}

void x10rt_net_send_get (x10rt_msg_params &p, void *buf, x10rt_copy_sz len)
{
    fprintf(stderr,"Unimplemented.\n");
    abort(); // TODO
}

void *x10rt_net_put_realloc (void *old, size_t old_sz, size_t new_sz)
{
    return ChkRealloc<void>(old, new_sz);
}

void x10rt_net_send_put (x10rt_msg_params &p, void *buf, x10rt_copy_sz len)
{
    fprintf(stderr,"Unimplemented.\n");
    abort(); // TODO
}

static void recv_completion(unsigned ix, unsigned long bytes, void* buf)
{
    assert(ix>0);
    amSendCb cb = global_state.amCbTbl[ix];
    assert(cb!=NULL);
    x10rt_msg_params p = { x10rt_net_here(),
                           ix,
                           buf,
                           bytes
                         };

    if(pthread_mutex_unlock(&global_state.lock)) {
        perror("pthread_mutex_unlock");
        exit(EXIT_FAILURE);
    }
    //fprintf(stderr, "%d: !!!Executing callback %d with %d bytes\n", global_state.rank, ix, bytes);
    cb(p);
    if(pthread_mutex_lock(&global_state.lock)) {
        perror("pthread_mutex_lock");
        exit(EXIT_FAILURE);
    }
    free(buf);
}

static void read_all(int fd, void* buf, unsigned long size) {
    while (size > 0) {
        unsigned long bytes = read(fd, buf, size);
        buf = ((char*)buf) + bytes;
        size -= bytes;
    }
}

// TODO
void x10rt_net_remote_xor (unsigned long place, unsigned long long addr, long long val)
{
    fprintf(stderr,"Unimplemented.\n");
    abort(); // TODO
}

// TODO
void x10rt_net_remote_op_fence (void)
{
    fprintf(stderr,"Unimplemented.\n");
    abort(); // TODO
}

// TODO
void x10rt_register_thread (void)
{
    fprintf(stderr,"Unimplemented.\n");
    abort(); // TODO
}

void x10rt_net_probe (void)
{
    assert(global_state.init);
    assert(!global_state.finalized);

    if(pthread_mutex_lock(&global_state.lock)) {
        perror("pthread_mutex_lock");
        exit(EXIT_FAILURE);
    }

    unsigned long from = 0;
    unsigned msg_type = 0;
    unsigned long len = 0;
    void* buf = NULL;

    // try reading from the pipe
    //fprintf(stderr,"%d: Probing read pipe\n", global_state.rank);
    int pipe = global_state.pipes[global_state.rank*2];
    //fprintf(stderr, "%d: !!!Got read pipe: (%d)\n", global_state.rank, pipe);
    // FIXME: assumes only normal message requests; send message type
    int l = read(pipe, &from, sizeof(from));
    //fprintf(stderr,"%d: Probing read pipe: %d bytes\n", global_state.rank, l);
    if (l > 0) {
        unsigned num;
        read_all(pipe, &num, sizeof(num));
        fprintf(stderr,"%lu: Message number %d from %lu\n", global_state.rank, num, from);
        // now block until read
        read_all(pipe, &msg_type, sizeof(msg_type));
        read_all(pipe, &len, sizeof(len));
        if (len > 0) {
            buf = (void*)malloc(len);
            read_all(pipe, buf, len);
        }
        fprintf(stderr,"%lu: Received message number %d of type %d with %lu bytes from %lu\n", global_state.rank, num, msg_type, len, from);
        recv_completion(msg_type, len, buf);
    }

    if(pthread_mutex_unlock(&global_state.lock)) {
        perror("pthread_mutex_unlock");
        exit(EXIT_FAILURE);
    }
}

void x10rt_net_finalize (void)
{
    assert(global_state.init);
    assert(!global_state.finalized);
    free (global_state.pipes);
    if (global_state.rank == 0) {
        for (unsigned long i = 1; i < global_state.nprocs; i++) {
            int s;
            waitpid(global_state.procs[i], &s, 0);
        }
        free (global_state.procs);
    }
    global_state.finalized = true;
}
