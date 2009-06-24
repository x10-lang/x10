/*
 * Distribed memory LU with partial pivoting
 */

#include <strings.h>
#include "lapi.h"
#include <assert.h>
#include "Sys.h"

#include <iostream>

using namespace std;

#define NDEBUG

/*--------------assert macro-------------------------*/

#define RC(statement) \
{ int rc; \
    if ((rc = statement) != LAPI_SUCCESS) { \
        printf(#statement " rc = %d, line %d\n", rc, __LINE__); \
            exit(-1); \
    } \
}

/*---------------Timing routines---------------------*/

typedef long long sint64_t;

typedef sint64_t nano_time_t;

#ifdef PROFILE
nano_time_t nanoTime() {
    struct timespec ts;
    // clock_gettime is POSIX!
    ::clock_gettime(CLOCK_REALTIME, &ts);
    return (nano_time_t)(ts.tv_sec * 1000000000LL + ts.tv_nsec);
}
#else
#define nanoTime() 0
#endif

#define HDR_HNDL_READY_BELOW_COUNT  (1)
#define HDR_HNDL_PIVOT_CANDIDATE    (2)

nano_time_t NanoTime() {
    struct timespec ts;
    // clock_gettime is POSIX!
    ::clock_gettime(CLOCK_REALTIME, &ts);
    return (nano_time_t)(ts.tv_sec * 1000000000LL + ts.tv_nsec);
}

struct AmMsg {
    void *dst;
    int  val;
};

/*--------------Communication interface---------------*/

/*
 * Base class
 */

/*dummy base class for non-blocking handles*/
class CommBaseNBH {
    public:
        virtual ~CommBaseNBH() {}
}; 

class Profiler;

class Comm {
    private:
        static Comm* obj;

        const int px, py; /*co-ordinate in processor grid*/
        int pi, pj;
    protected:
        virtual ~Comm() {} /*cannot be instiated by user*/
    public:
        static void Init(int spx, int spy); //implement after sub-class declaration
        static void Finalize() { assert(obj!=NULL); delete obj; }
        friend Comm& TheComm();

        Comm(int spx, int spy) : px(spx), py(spy) { 
            /*pi, pj cannot be init until LAPI is (that gives here() and
              nprocs(). So it is done in Comm::Init() */
        }

        virtual void Put(void *src, void *dst, int bytes, int proc) = 0;
        virtual void Get(void *src, void *dst, int bytes, int proc) = 0;

        virtual void AmSend(int tgt, void *hdr_hndl, void *data, int len) = 0;

        virtual void NbPut(void *src, void *dst, int bytes, int proc) = 0;

        virtual void NbGet(void *src, void *dst, int bytes, int proc, CommBaseNBH *nbh) = 0;

        virtual bool IsDone(CommBaseNBH *nbh)=0;
        virtual int here()=0; 
        virtual int nprocs() = 0;

        /*Allocate an array (a portion starting at addr in each proc)*/
        virtual void ArrayMalloc(int bytes, void **table) = 0; 
        virtual void ArrayAttach(void *addr, void **table) = 0; 
        virtual void ArrayFree(void **table) = 0;

        virtual void AtomicAdd(int val, int *dst, int proc) = 0;
        virtual void AtomicAdd(sint64_t val, sint64_t *dst, int proc) = 0;
        virtual void AtomicAdd(double val, double *dst, int proc) = 0;

        virtual void Barrier()=0;
        virtual void Fence()=0;
        virtual void InitCounter(CommBaseNBH *nb, int val) = 0;

        int getproc(int i, int j) const {
            assert(j>=0 && j<py);
            assert(i>=0 && i<px);
            return i*py+j;
        }

        int getpi() const { return pi; }
        int getpj() const { return pj; }

        virtual void Poll(Profiler &prof) = 0;
};

Comm *Comm::obj=NULL;

Comm& TheComm() { return *Comm::obj; }

/*
 * LAPI Communicator
 */

class LapiCommNBH : public CommBaseNBH {
    public:
        lapi_cntr_t cntr; /*completion counter*/
        LapiCommNBH();
};

void * receiveReadyBelowCount(lapi_handle_t *hndl, void *uhdr, uint *uhdr_len,
        ulong *msg_len, compl_hndlr_t **ucomp, void **uinfo)
{
    struct AmMsg *msg;
    volatile int *dst_ptr;

    lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;

    /* We really expect one packet over here */
    assert(NULL != ret_info->udata_one_pkt_ptr);

    /* We also know that this is an atomic update by 1 */
    msg = (struct AmMsg *) ret_info->udata_one_pkt_ptr;

    dst_ptr = (volatile int *) msg->dst;

    x10lib_cws::atomic_add( dst_ptr, 1);

    ret_info->ctl_flags = LAPI_BURY_MSG;
    *ucomp = NULL;
    return NULL;
}

void * receivePivotCandidate(lapi_handle_t *hndl, void *uhdr, uint *uhdr_len,
        ulong *msg_len, compl_hndlr_t **ucomp, void **uinfo)
{
    struct AmMsg *msg;
    int *dst_ptr;

    lapi_return_info_t *ret_info = (lapi_return_info_t *)msg_len;

    /* We really expect one packet over here */
    assert(NULL != ret_info->udata_one_pkt_ptr);

    msg = (struct AmMsg *) ret_info->udata_one_pkt_ptr;

    /* Apply the first two "puts" */
    dst_ptr = (int *) msg[0].dst;
    *dst_ptr = msg[0].val;

    dst_ptr = (int *) msg[1].dst;
    *dst_ptr = msg[1].val;

    /* Apply the atomic add */
    dst_ptr = (int *) msg[2].dst;

    x10lib_cws::atomic_add( dst_ptr, msg[2].val);

    ret_info->ctl_flags = LAPI_BURY_MSG;
    *ucomp = NULL;
    return NULL;
}

class LapiComm : public Comm {
    public:
        lapi_handle_t       hndl;      /*<Context handle*/
        int                 num_tasks; /*<#procs?*/
        unsigned int        my_id;     /*< proc id*/
        lapi_thread_func_t  tf;        /*< ??? */
        lapi_cntr_t         wait_cntr; /*<LAPI wait counter*/

    public:
        /*LAPI initialized. Based on x10.apps/RandomAccess/gups.cc:lapi_initialize() */
        LapiComm(int px, int py) : Comm(px,py) {
            // initialize LAPI
            lapi_info_t    lapi_info;
            memset(&lapi_info, 0, sizeof(lapi_info));
            RC( LAPI_Init(&hndl, &lapi_info) );

            // communication setup
            RC( LAPI_Addr_set(hndl, (void *)receiveReadyBelowCount, HDR_HNDL_READY_BELOW_COUNT));
            RC( LAPI_Addr_set(hndl, (void *)receivePivotCandidate, HDR_HNDL_PIVOT_CANDIDATE));
            RC( LAPI_Qenv(hndl, NUM_TASKS, &num_tasks) );
            RC( LAPI_Qenv(hndl, TASK_ID, (int*) &my_id) );
            RC( LAPI_Senv(hndl, ERROR_CHK, 0) );
            (void)LAPI_Setcntr(hndl, &wait_cntr, 0);

            // get shared lock to prevent other threads from running
            tf.Util_type = LAPI_GET_THREAD_FUNC;
            RC( LAPI_Util(hndl, (lapi_util_t *)&tf) );
            tf.mutex_lock(hndl);
        }

        ~LapiComm() {
            tf.mutex_unlock(hndl);
            RC( LAPI_Term(hndl) );
        }

        /*dst is the remote pointer here*/
        virtual void Put(void *src, void *dst, int bytes, int proc) {
            int val, orig_val;

            assert(src != NULL);
            assert(dst != NULL);
            assert(proc>=0 && proc<num_tasks);
            assert(bytes>=0);

            /* blocking operation */
            /* get the current value of the global wait counter;
             * wait on it to ensure that data transfer is complete;
             * and restore it to its original value.
             */
            (void)LAPI_Getcntr(hndl, &wait_cntr, &orig_val);
            assert(orig_val==0);
            val = orig_val + 1;
            RC(LAPI_Put(hndl, proc, bytes, dst, src, NULL, NULL,
                        &wait_cntr));
            (void)LAPI_Waitcntr(hndl, &wait_cntr, val, NULL); 
            (void)LAPI_Setcntr(hndl, &wait_cntr, orig_val);
        }

        virtual void NbPut(void *src, void *dst, int bytes, int proc) {
            LAPI_Put(hndl, proc, bytes, dst, src, NULL, NULL, NULL);
        }

        virtual void AmSend(int tgt, void *hdr_hndl, void *data, int len) {

            int val, orig_val;

            LAPI_Getcntr(hndl, &wait_cntr, &orig_val);
            val = orig_val + 1;
            RC( LAPI_Amsend(hndl, tgt, hdr_hndl, NULL, 0, data, len,
                        NULL, &wait_cntr, NULL) );

            LAPI_Waitcntr(hndl, &wait_cntr, val, NULL);
            LAPI_Setcntr(hndl, &wait_cntr, orig_val);
        }

        /*note that src is the remote pointer here*/
        virtual void Get(void *src, void *dst, int bytes, int proc) {
            int val, orig_val, cur_val;

            assert(src != NULL);
            assert(dst != NULL);
            assert(proc>=0 && proc<num_tasks);
            assert(bytes>=0);

            /* blocking operation */
            /* get the current value of the global wait counter;
             * wait on it to ensure that data transfer is complete;
             * and restore it to its original value.
             */
            (void)LAPI_Getcntr(hndl, &wait_cntr, &orig_val);
            assert(orig_val==0);
            val = orig_val + 1;
            RC(LAPI_Get(hndl, proc, bytes, src, dst, NULL, &wait_cntr));
            (void)LAPI_Waitcntr(hndl, &wait_cntr, val, NULL); 
            (void)LAPI_Setcntr(hndl, &wait_cntr, orig_val);
        }

        virtual void NbGet(void *src, void *dst, int bytes, int proc, CommBaseNBH *nb) {
            assert(src != NULL);
            assert(dst != NULL);
            assert(proc>=0 && proc<num_tasks);
            assert(bytes>=0);

            //     LapiCommNBH *nbh = dynamic_cast<LapiCommNBH *>(nb);
            LapiCommNBH *nbh = (LapiCommNBH *)(nb);

            RC(LAPI_Get(hndl, proc, bytes, src, dst, NULL, &nbh->cntr));
        }

        virtual bool IsDone(CommBaseNBH *nb) {
            int cur_val;
            lapi_msg_info_t info;
            LapiCommNBH *nbh = dynamic_cast<LapiCommNBH *>(nb);
            /* SS: Introduce some polling here */
            LAPI_Msgpoll(hndl, 1, &info);
            LAPI_Getcntr(hndl, &nbh->cntr, &cur_val);
            if(cur_val == 1) {
                LAPI_Setcntr(hndl, &nbh->cntr, 0);
                return true;
            }
            return false;
        }

        virtual int here() { return my_id; }
        virtual int nprocs() { return num_tasks; }

        virtual void ArrayMalloc(int bytes, void **table) {
            assert(bytes>=0);
            void *addr = malloc(bytes);
            ArrayAttach(addr, table);
        }

        virtual void ArrayAttach(void *addr, void **table) {
            assert(table != NULL);
            assert(addr != NULL);
            LAPI_Address_init(hndl, addr, table);    
            for(int i=0; i<nprocs(); i++) {
                assert(table[i] != NULL);
            }
        }

        virtual void ArrayFree(void **table) {
            free(table[here()]);
        }

        virtual void AtomicAdd(int v, int *dst, int proc) {
            int new_val, orig_val;

            (void)LAPI_Getcntr(hndl, &wait_cntr, &orig_val);
            new_val = orig_val + 1;
            LAPI_Rmw(hndl, FETCH_AND_ADD, proc, dst, &v, NULL, &wait_cntr);
            (void)LAPI_Waitcntr(hndl, &wait_cntr, new_val, NULL); 
            (void)LAPI_Setcntr(hndl, &wait_cntr, orig_val);
        }

        virtual void AtomicAdd(sint64_t v, sint64_t *dst, int proc) {
            int new_val, orig_val;

            (void)LAPI_Getcntr(hndl, &wait_cntr, &orig_val);
            new_val = orig_val + 1;
            LAPI_Rmw64(hndl, FETCH_AND_ADD, proc, dst, &v, NULL, &wait_cntr);
            (void)LAPI_Waitcntr(hndl, &wait_cntr, new_val, NULL); 
            (void)LAPI_Setcntr(hndl, &wait_cntr, orig_val);
        }

        virtual void AtomicAdd(double v, double *dst, int proc) {
            int new_val, orig_val;

            (void)LAPI_Getcntr(hndl, &wait_cntr, &orig_val);
            new_val = orig_val + 1;
            LAPI_Rmw64(hndl, FETCH_AND_ADD, proc, (long long *)dst, 
                    (long long *) &v, NULL, &wait_cntr);
            (void)LAPI_Waitcntr(hndl, &wait_cntr, new_val, NULL); 
            (void)LAPI_Setcntr(hndl, &wait_cntr, orig_val);
        }
        
        virtual void InitCounter(CommBaseNBH *nb, int val) {
            LapiCommNBH *nbh = dynamic_cast<LapiCommNBH *>(nb);
            RC(LAPI_Setcntr(hndl, &nbh->cntr, val));
        }

        virtual void Barrier() { LAPI_Gfence(hndl); }
        virtual void Fence() { LAPI_Fence(hndl); }
        virtual void Poll(Profiler &prof);
};

/*delayed definition of LapiCommNBH constructor*/
LapiCommNBH::LapiCommNBH() {
    LapiComm *comm = dynamic_cast<LapiComm*>(&TheComm());
    LAPI_Setcntr(comm->hndl, &cntr, 0);
}


/*
 * Initializer that selects among the implementations of Comm (in theory)
 */
void 
Comm::Init(int px, int py) {
    obj = new LapiComm(px, py);
    obj->pi = obj->here()/py;
    obj->pj = obj->here()%py;
}

/*----------------Profiling variables-----------------*/

#define FIELD_OFFSET(st,fld) ((int)((st*)0)->fld)
#define REMOTE_FIELD(st,fld,table,p,T) ((T*)(((char *)(table[p])) + FIELD_OFFSET(st,fld)))

class Profiler {
    private:
        void **addr;
        void **flop_addrs;
    public:

        nano_time_t runT; /*Time spent in Worker::run()*/
        nano_time_t dgemmT; /*Time spent in DGEMM*/
        nano_time_t mulsubT; /*Time spend in Block::mulsub()*/
        nano_time_t luT; /*Time spent in Block::LU()*/
        nano_time_t bsT; /*Time in Block::backSolve()*/
        nano_time_t lwrT; /*Time in Block::lower()*/
        nano_time_t getT; /*Time in get() or getLocal() */
        nano_time_t permuteT; /*Time in permute()*/
        nano_time_t pollT; /*Time in Comm::Poll()*/
        nano_time_t isdoneT; /*Time in Comm::IsDone()*/
        nano_time_t newT; /*Time in Block::mulsub()'s mem alloc*/
        int nStep; /*#calls to Block::step()*/
        int nStepLU; /*#calls to stepLU*/
        double *mflops; /* Final result from all procs */
        int is_ok; /* Combination of pass/fail status at each proc */

        Profiler() {
            addr = new void *[TheComm().nprocs()];
            assert(addr != NULL);
            TheComm().ArrayAttach(this, addr);
            runT=dgemmT=luT=bsT=lwrT=permuteT=mulsubT=pollT=isdoneT=newT=0;
            nStep = nStepLU = 0; is_ok = 0;

            mflops = new double [TheComm().nprocs()];
            memset(mflops, 0, sizeof(double)*TheComm().nprocs());
            flop_addrs = new void *[TheComm().nprocs()];
            TheComm().ArrayAttach(mflops, flop_addrs);
        }
        ~Profiler() {
            delete [] addr;
            delete [] mflops;
            delete [] flop_addrs;
        }

        inline void log_run(nano_time_t t) { runT += t; }
        inline void log_mulsub(nano_time_t t) { mulsubT += t; }
        inline void log_dgemm(nano_time_t t) { dgemmT += t; }
        inline void log_lu(nano_time_t t) { luT += t; }
        inline void log_bsolve(nano_time_t t) { bsT += t; }
        inline void log_lower(nano_time_t t) { lwrT += t; }
        inline void log_permute(nano_time_t t) { permuteT += t; }
        inline void log_poll(nano_time_t t) { pollT += t; }
        inline void log_isdone(nano_time_t t) { isdoneT += t; }
        inline void log_new(nano_time_t t) { newT += t; }
        inline void log_step() { nStep += 1; }
        inline void log_stepLU() { nStepLU += 1; }

        void report(int root) {
            Profiler *gProf = (Profiler *)addr[root];
            if(TheComm().here() != root) {
                if(TheComm().nprocs() > 1) {
                    TheComm().AtomicAdd(runT,     &gProf->runT, root);
                    TheComm().AtomicAdd(mulsubT,  &gProf->mulsubT,root);
                    TheComm().AtomicAdd(dgemmT,   &gProf->dgemmT,root);
                    TheComm().AtomicAdd(luT,      &gProf->luT,root);
                    TheComm().AtomicAdd(bsT,      &gProf->bsT,root);
                    TheComm().AtomicAdd(lwrT,     &gProf->lwrT,root);
                    TheComm().AtomicAdd(permuteT, &gProf->permuteT,root);
                    TheComm().AtomicAdd(pollT,    &gProf->pollT,root);
                    TheComm().AtomicAdd(isdoneT,  &gProf->isdoneT,root);
                    TheComm().AtomicAdd(newT,  &gProf->newT,root);
                    TheComm().AtomicAdd(nStep,    &gProf->nStep,root);
                    TheComm().AtomicAdd(nStepLU,  &gProf->nStepLU,root);
                }
                else {
                    gProf->runT += runT;
                    gProf->mulsubT += mulsubT;
                    gProf->dgemmT += dgemmT;
                    gProf->luT += luT;
                    gProf->bsT += bsT;
                    gProf->lwrT += lwrT;
                    gProf->permuteT += permuteT;
                    gProf->pollT += pollT;
                    gProf->newT += newT;
                    gProf->isdoneT += isdoneT;
                    gProf->nStep += nStep;
                    gProf->nStepLU += nStepLU;
                }
            }
        }

        void report_results(int root, double flops, int ok_flag) {
            Profiler *gProf = (Profiler *)addr[root];
            double *remote_flop_addr = (double *) flop_addrs[root];

            remote_flop_addr += TheComm().here();

            if(TheComm().here() != root) {
                if(TheComm().nprocs() > 1) {
                    TheComm().AtomicAdd(ok_flag,  &gProf->is_ok, root);
                    TheComm().Put(&flops, 
                            (void *)remote_flop_addr, 
                            sizeof(double), root);
                } else {
                    gProf->is_ok += ok_flag;
                }
            } 
        }

        void display_results(int proc, double this_flops, int correct) {
            if(TheComm().here() == proc) {
                bool result_verified = false;
                double total_flops = 0.0;

                if( (is_ok == TheComm().nprocs() - 1) && correct) {
                    result_verified = true;
                }

                total_flops += this_flops;

                for(int i = 0; i < TheComm().nprocs(); i++) {
                    if(i == TheComm().here())
                        continue;
                    total_flops += mflops[i];
                }

                cout << endl;
                cout << "NPROCS " << TheComm().nprocs()
                     << " Avg MFlops " << total_flops/TheComm().nprocs()
                     << " Verfied " << (result_verified ? "ok" : "fail")
                     << endl;
            }
        }

        void display(int proc) {
            if(TheComm().here() == proc) {
                cout << endl;
                cout << "Profile Report: " << endl;
                cout<<"Worker::run() = "<<runT/1000000.0<<" ms"
                    <<"\t mulsub="<<mulsubT/1000000.0<<" ms"
                    <<"\t dgemm="<<dgemmT/1000000.0<<" ms"
                    <<"\t LU="<<luT/1000000.0<<" ms"
                    <<"\t bsolve="<<bsT/1000000.0<<" ms"
                    <<"\t lower="<<lwrT/1000000.0<<" ms"
                    <<"\t permute="<<permuteT/1000000.0<<" ms"
                    <<"\t poll="<<pollT/1000000.0<<" ms"
                    <<"\t isdone="<<isdoneT/1000000.0<<" ms"
                    <<"\t new="<<newT/1000000.0<<" ms"
                    <<"\t nStep="<<nStep
                    <<"\t nStepLU="<<nStepLU
                    <<endl;
            }
        }
};


/*virtual*/
void LapiComm::Poll(Profiler &prof) { 
    lapi_msg_info_t info; 
    nano_time_t s, t;
    
    s = nanoTime();
    LAPI_Msgpoll(hndl, 1, &info); 
    t = nanoTime();

    prof.log_poll(t - s);
}

/*--------------numerics declarations---------------*/

/*Need to actually match the fortran integer. Assume int for now and
  pray. Google blas dgemm to understand the arguments.  */ 
// typedef long Integer;
typedef int Integer;

#define DGEMM dgemm
#define DTRSM dtrsm
#define DGEMV dgemv
#define DSCAL dscal

extern "C" {
    void DGEMM(char *TRANSA, char *TRANSB, 
            Integer *M, Integer *N, Integer *K, 
            double *ALPHA, double* A, Integer *LDA,
            double *B, Integer *LDB, 
            double *BETA, double *C, Integer *LDC);

    void DTRSM(char *SIDE, char *UPLO, char *TRANSA,
            char *DIAG, Integer *M, Integer *N, double *ALPHA,
            double *A, Integer *LDA, double *B, Integer *LDB);

    void DGEMV(char *TRANS, Integer *M, Integer *N,
            double *ALPHA, 
            double *A, Integer *LDA,
            double *X, Integer *INCX, 
            double *BETA, double *Y, Integer *INCY);

    void DSCAL(Integer *N, double *DA, double *DX, Integer *INCX);
}



/*---------------support routines--------------------*/

static double format(double v, int precision){
    int scale=1;
    for(int i=0; i<precision; i++)
        scale *= 10;
    return ((int)(v*scale))*1.0/scale;
}
static  int max(int a, int b) {
    return a > b ? a : b;
}
static  double max(double a, double b) {
    return a > b ? a : b;
}
static double fabs(double v){
    return  v > 0 ? v : -v;
}
static  int min(int a, int b) {
    return a > b ? b : a;
}
static double flops(int n) {
    return ((4.0 *  n - 3.0) *  n - 1.0) * n / 6.0;
}

/*---------2-D block-cyclic array of blocks-------------*/

class Block;
class ReadyVariable;
class ColumnVariables;
class PivotVariable;

class TwoDBlockCyclicArray {
    private:
        void *buf_to_free; /* Keep track of the blocks
                              allocated so that we can
                              free them later */
    public:
        Block **A; /*array of block pointers. Note that we are using a 1-d
                     array of all the blocks. The processor dimension is
                     merged with the per-process data*/ 
        const int px,py, nx,ny,B;
        const int N;

        const int pi, pj; /*This worker's co-ordinates in the processor grid*/

        double **globalData; /*remote pointers to base of data buffers in all procs*/

        ReadyVariable   *rv;
        ColumnVariables *cv;
        PivotVariable   *pv;

    public:
        TwoDBlockCyclicArray(int spx, int spy, int snx, int sny,int sB); 
        TwoDBlockCyclicArray(const TwoDBlockCyclicArray &arr); 

        ~TwoDBlockCyclicArray();

        void init() {}
        int pord(int i, int j) const {
            assert(j>=0 && j<py);
            assert(i>=0 && i<px);
            return i*py+j;
        }
        int lord(int i, int j) const {
            assert(j>=0 && j<ny);
            if(!(i>=0 && i<nx)) {
                cerr<<"ERROR: lord(i,j). i="<<i<<" j="<<j<<endl;
                assert(i>=0 && i<nx);
            }
            return i*ny+j;
        }

        Block *get(int i, int j) {
            assert(i>=0 && i<nx*px);
            assert(i>=0 && i%px == pi);
            assert(j>=0 && j%py == pj);
            //     return A[pord(i % px, j%py)*nx*ny + lord(i/px,j/py)];
            return A[lord(i/px,j/py)];
        }

        bool IsLocal(int i, int j) {
            return (i%px==pi && j%py==pj);
        }

        Block *getLocal(int pi, int pj, int i, int j) {
            assert(pi == this->pi);
            assert(pj == this->pj);
            assert(i>=0 && i<nx);
            assert(j>=0 && j<ny);
            return A[lord(i,j)];
        }

        double *getRemotePtr(int I, int J) {
            assert(I>=0 && I<nx*px);
            assert(J>=0 && J<nx*px);
            int proc = TheComm().getproc(I%px, J%py);
            return globalData[proc] + lord(I/px,J/py)*B*B;
        }

        void getData(int I, int J, double *buf) { 
            assert(I>=0 && I<nx*px);
            assert(J>=0 && J<nx*px);
            int proc = TheComm().getproc(I%px, J%py);
            double *src = globalData[proc] + lord(I/px,J/py)*B*B;
            TheComm().Get(src, buf, B*B*sizeof(double), proc);
        }

        void getData(int I, int J, double *buf, CommBaseNBH *nbh) { 
            assert(I>=0 && I<nx*px);
            assert(J>=0 && J<nx*px);
            int proc = TheComm().getproc(I%px, J%py);
            double *src = globalData[proc] + lord(I/px,J/py)*B*B;
            TheComm().NbGet(src, buf, B*B*sizeof(double), proc, nbh);
        }

        void getDataCol(int I, int J, int col, double *buf) {
            assert(I>=0 && I<nx*px);
            assert(J>=0 && J<nx*px);
            /* SS: This should not be used now! */
            assert(0);
            int proc = TheComm().getproc(I%px, J%py);
            double *src = globalData[proc] + lord(I/px,J/py)*B*B + col*B;
            TheComm().Get(src, buf, B*sizeof(double), proc);    
        }

        void getDataCol(int I, int J, int col, double *buf, CommBaseNBH *nbh) {
            assert(I>=0 && I<nx*px);
            assert(J>=0 && J<nx*px);
            int proc = TheComm().getproc(I%px, J%py);
            double *src = globalData[proc] + lord(I/px,J/py)*B*B + col*B;
            TheComm().NbGet(src, buf, B*sizeof(double), proc, nbh);    
        }

        TwoDBlockCyclicArray *copy() {
            TwoDBlockCyclicArray *r = new TwoDBlockCyclicArray(*this);
            TheComm().Barrier();
            return r;
        }

        void global_permute(int I, int J, int row1, int row2);

        void gatherPivots(int *pivots, int root);

        void applyLowerPivots(int *pivots, int root);

        void applyPivots(int *pivots, int root);

        void display(const char *msg);
};

/*------------Coupled variables---------------------*/

/*These are implementation equivalents of volatile variables in shared
  memory code. Local changes are appropriately updated in the global
  code. Note that the implementation is slightly different to reduce storage requirements */

/*Manages communication of ready status of blocks*/
class ReadyVariable {
    private:
        TwoDBlockCyclicArray *M;
        int *loc_ready;            /*<ready variables for blocked owned by M (local blocks)*/
        int *shadow_x, *shadow_y;  /*<shadow variables for remote blocks used by any block in M*/
        int **neigh_x, **neigh_y;  /*<pointers to shadow_x, &shadow_y for all neighbor workers along x & y*/

        const int px, py, pi, pj;

    public:
        ReadyVariable(TwoDBlockCyclicArray *sM) : M(sM), px(sM->px), py(sM->py), pi(sM->pi), pj(sM->pj) {
            loc_ready = new int [M->nx*M->ny]; /*a value for each column*/
            assert(loc_ready != NULL);

            int *buf = new int [M->nx*M->ny*(M->py-1) + M->ny*M->nx*(M->px-1)];
            assert (buf != NULL);
            shadow_x = buf;
            shadow_y = buf + M->nx*M->ny*(M->py-1);

            bzero(loc_ready, M->nx*M->ny*sizeof(int));
            bzero(shadow_x, M->nx*M->ny*(M->py-1)*sizeof(int));
            bzero(shadow_y, M->ny*M->nx*(M->px-1)*sizeof(int));

            int **table = new int *[M->px*M->py];
            assert(table != NULL);

            TheComm().ArrayAttach(buf, (void **)table);

            for(int i=0; i<M->px*M->py; i++) {
                assert(table[i] != NULL);
            }

            neigh_x = new int *[M->py];
            neigh_y = new int *[M->px];
            assert(neigh_x!=NULL);
            assert(neigh_y!=NULL);

            for(int j=0; j<py; j++) {
                int proc = TheComm().getproc(pi, j);
                neigh_x[j] = table[proc];
                assert(neigh_x[j] != NULL);
            }

            for(int i=0; i<px; i++) {
                int proc = TheComm().getproc(i, pj);
                neigh_y[i] = table[proc] + M->nx*M->ny*(M->py-1);
                assert(neigh_y[i] != NULL);
            }
            delete [] table;
        }
        ~ReadyVariable() { 
            delete [] loc_ready; 
            delete [] shadow_x; /*also deletes shadow_y*/
            delete [] neigh_x;
            delete [] neigh_y;
        }

        int getOffset(int I, int J) volatile {
            int pi = I%px, pj=J%py;
            int loff = M->lord(I/M->px,J/M->py); /*offset of block within blocks owned by a proc (which proc that is)*/
            int poff=0;

            if(pi==M->pi && pj==M->pj) 
                poff=0;
            else if(pi==M->pi) 
                poff = M->nx * M->ny * (pj>M->pj ? (pj-1) : pj);
            else if(pj==M->pj) 
                poff = M->nx * M->ny * (pi>M->pi ? (pi-1) : pi);
            else 
                assert(0);
            return poff+loff;
        }

        /*offset in proc (pi, pj) of local block (I,J)*/
        int getOffset(int I, int J, int pi, int pj) volatile {
            assert(I%M->px!=pi || J%M->py!=pj); /*It is not a local block*/
            assert(I%M->px==M->pi && J%M->py==M->pj); /*It is a local block here()*/

            int loff = M->lord(I/M->px,J/M->py); /*offset of block within blocks owned by a proc (which proc that is)*/
            int poff=0;

            if(pi==M->pi && pj==M->pj) {
                assert(0); /*shouldn't be here*/
            }
            else if(pi==M->pi) 
                poff = M->nx * M->ny * (M->pj>pj ? (M->pj-1) : M->pj);
            else if(pj==M->pj) 
                poff = M->nx * M->ny * (M->pi>pi ? (M->pi-1) : M->pi);
            else 
                assert(0);
            return poff+loff;
        }

        /*pointer to local block of shadow block (I,J) from this proc's data*/
        int *getPtr(int I, int J) volatile {
            int pi = I%px, pj=J%py;
            int off = getOffset(I, J);
            int *rval;

            if(pi==M->pi && pj==M->pj) 
                rval = &loc_ready[off];
            else if(pi==M->pi) 
                rval = &shadow_x[off];
            else if(pj==M->pj) 
                rval = &shadow_y[off];
            else 
                assert(0);
            assert(rval != NULL);
            return rval;
        }

        /*pointer to shadow copy of local block (I,J) at proc (pi,pj)*/
        int *getPtr(int I, int J, int pi, int pj) volatile {
            assert(I%px==M->pi); 
            assert(J%py==M->pj); /*it is a local block*/
            assert(pi>=0 && pi<px);
            assert(pj>=0 && pj<py);
            assert(pi==M->pi || pj==M->pj);
            int off = getOffset(I,J, pi, pj); 
            int *rval=NULL;

            if(pi==M->pi && pj==M->pj) {
                assert(0); /*no remote ptr for local brick: why did we get here?*/
                rval = getPtr(I,J); /* it is a local block*/
            }  
            else if(pi==M->pi) {
                rval = neigh_x[pj] + off;
                assert(rval != NULL);
            }
            else if(pj==M->pj) {
                rval = neigh_y[pi] + off;
                assert(rval != NULL);
            }
            else
                assert(0);
            assert(rval != NULL);
            return rval;
        }

        bool ready(int I, int J) volatile {
            return *getPtr(I,J)>0;
        }

        /*FIXME: optimize to use non-blocking calls*/
        void signalReady(int I, int J) {
            assert(I%M->px==M->pi); assert(J%M->py==M->pj); /*it is a local block*/
            assert(*getPtr(I,J)==0); /*not already set*/
            *getPtr(I,J) = 1;

            //     cerr<<"SignalReady. I="<<I<<" J="<<J<<endl;

            for(int j=0; j<M->py; j++) {
                if(j == M->pj) continue;
                int src=1;
                int *dst = getPtr(I, J, M->pi, j);
                int bytes = sizeof(int);
                int proc = TheComm().getproc(M->pi, j);
                //TheComm().Put(&src, dst, bytes, proc);
                TheComm().NbPut(&src, dst, bytes, proc);
            }
            for(int i=0; i<M->px; i++) {
                if(i == M->pi) continue;
                int src=1;
                int *dst = getPtr(I, J, i, M->pj);
                int bytes = sizeof(int);
                int proc = TheComm().getproc(i, M->pj);
                //       TheComm().Put(&src, dst, bytes, proc);
                TheComm().NbPut(&src, dst, bytes, proc);
            }
        }
};

/*Manages communication of computed pivots across a row of blocks*/
class PivotVariable {
    private:
        TwoDBlockCyclicArray *M;
        int *pivots; /*[nx*B]*//*pivots of interest for blocks in this proc*/
        int *pready; /*[nx]*/ /*ready[i] > 0 of pivots for row block i are available*/
        int **remote_pivots; /*[py][nx*B]*/ /*pointer to remote pivot arrays on procs of interest*/
        int **remote_pready; /*[py][1]*/ /*pointer to remote ready arrays on procs of interest*/

    public:
        PivotVariable(TwoDBlockCyclicArray *sM) : M(sM) {
            pivots = new int [M->nx * M->B + M->nx];
            assert(pivots != NULL);
            pready = pivots + M->nx*M->B;

            bzero(pready, M->nx*sizeof(int));

            //     cerr<<"PivotVariable. ["<<TheComm().here()<<"] ptr="<<pivots<<endl;

            remote_pivots = new int *[M->py];
            remote_pready = new int * [M->py];
            assert(remote_pivots!=NULL);
            assert(remote_pready != NULL);

            int **table = new int *[M->px*M->py];
            assert(table != NULL);
            TheComm().Barrier();
            TheComm().ArrayAttach(pivots, (void **)table);

            for(int i=0; i<M->px*M->py; i++) {
                assert(table[i] != NULL);
            }

            for(int j=0; j<M->py; j++) {
                int proc = TheComm().getproc(M->pi, j);
                remote_pivots[j] = table[proc];
                remote_pready[j] = remote_pivots[j] + M->nx*M->B;
                assert(remote_pivots[j] != NULL);
                assert(remote_pready[j] != NULL);
            }

            //     for(int j=0; j<M->py; j++) {
            //       cerr<<TheComm().here()<<":: pi="<<M->pi<<" pj="<<j<<" ptr="<<remote_pivots[j]<<endl;
            //     }

            delete [] table;
        }

        ~PivotVariable() { 
            delete [] pivots; /*also delete pready*/ 
            delete [] remote_pivots;
            delete [] remote_pready;
        }

        bool ready(int I, int J) {
            assert(I%M->px==M->pi);
            assert(J%M->py==M->pj);
            return pready[I/M->px]>0;
        }
        void signalReady(int I, int J) {
            assert(I==J); /*can only be done by diagonal blocks*/
            assert(I>=0 && I<M->nx*M->px);
            assert(I%M->px==M->pi);
            assert(J%M->py==M->pj);
            assert(pready[I/M->px] == 0);

            const int i = I/M->px;

            pready[i] = 1;

            /*FIXME: broadcast only to procs on the forward half?*/
            for(int j=0; j<M->py; j++) {
                if(j==M->pj) continue;

                int proc = TheComm().getproc(M->pi, j);
                int one = 1;
                void *dst = remote_pivots[j]+i*M->B;
                if(dst == NULL) {
                    cerr<<"ERROR: here="<<TheComm().here()<<" proc="<<proc
                        <<" I="<<I<<" i="<<i<<" rpivots="<<remote_pivots[proc]
                        <<" dst="<<dst<<endl;
                    assert(dst != NULL);
                }
                assert(remote_pready[j]+i != NULL);

                TheComm().Put(&pivots[i*M->B], dst, M->B*sizeof(int), proc);
                TheComm().NbPut(&one, remote_pready[j]+i, sizeof(int), proc);
            }
        }

        int getPivot(int I, int J, int row) {
            assert(I%M->px == M->pi);
            assert(J%M->py == M->pj);
            assert(row>=I*M->B);
            assert(row<(I+1)*M->B);
            return pivots[(I/M->px)*M->B + (row%M->B)];
        }

        void setPivot(int I, int J, int row, int val) {
            const int B = M->B;
            assert(I%M->px == M->pi);
            assert(J%M->py == M->pj);
            assert(row>=I*B);
            assert(row<(I+1)*B);
            pivots[(I/M->px)*B + (row%B)] = val;
        }

        int *getLocalPivots() {
            return pivots;
        }
};


/*variables for panel factorization*/
class ColumnVariables {
    private:
        TwoDBlockCyclicArray *M;

        /*following are only one for each column block. As we can have
          LU/backSolve on only any one block in a column at a time.*/

        int *readyBelowCount; /*[ny]*/ /*#blocks below with enough
                                         mulsubs for this block to do
                                         LU*/
        int **diag_readyBelowCounts; /*[px][ny]*/ /*pointers to remote counters of
                                                    interest for all local blocks*/ 

        /*following are only for one panel fact (at a time) and not for all
          blocks or all columns*/

        int *shadow_LU_col; /*[1]*/ /*Shadow copies of corresponding
                                      diagonal LU_col for local reading*/

        int **neigh_LU_cols; /*[px][1]*/ /*points to the shadow_LU_cols so that they can
                                           be updated*/

        volatile int *pivotCount; /*[1]*/ /*#maxes reported for this column across all
                                            procs. This is updated by other procs on
                                            signalling*/
        int **diag_pivotCount; /*[px][1]*/ /*Remote pointers to pivotCount (one
                                             for each block) for the
                                             corresponding diagonal block*/

        /*The following two -- only diagonals blocks use all entries. Others
          use just one*/
        double *maxColV; /*[px]*/ /*Maximum value for LU_col computed by */
        int *maxRow;  /*[px]*/ /*The row with the corresponding maxColV value*/
        double **diag_maxColV; /*[px][px]*/ /*Remote pointers to the
                                              corres. diag. maxColV*/
        int **diag_maxRow; /*[px][px]*/ /*Remote pointers to
                                          corres. diag. maxRow*/ 
    public:
        void resetLU_col(int I, int J) { 
            assert(I==J);
            signalLU_col(0, 0, -1);
        }

        void resetReadyBelowCount(int I, int J) {
            assert(I%M->px==M->pi);
            assert(J%M->py == M->pj);
            readyBelowCount[J/M->py] = 0;
        }

        void initReadyBelowCount() {
            for(int j=0; j<M->ny; j++) {
                readyBelowCount[j] = 0;
            }
        }

        ColumnVariables(TwoDBlockCyclicArray *sM) : M(sM) {
            int nels;

            nels = M->ny /*readyBelowCount*/
                + 1 /*shadow_LU_col*/
                + 1 /*pivotCount*/ 
                + M->px  /*maxRow*/
                ;

            //       + (M->px+1) *sizeof(double) /*maxColV -- 1 more for padding*/
            int *buf = new int[nels];
            assert(buf != NULL);
            bzero(buf, nels*sizeof(int));

            readyBelowCount = buf;
            shadow_LU_col =   readyBelowCount + M->ny;
            pivotCount =      shadow_LU_col + 1;
            maxRow =          (int *)(pivotCount + 1);

            int **table = new int *[M->px*M->py];
            assert(table != NULL);
            TheComm().ArrayAttach(buf, (void **)table);

            for(int i=0; i<M->px*M->py; i++) {
                assert(table[i] != NULL);
            }

            diag_readyBelowCounts = new int *[M->px];
            neigh_LU_cols = new int*[M->px];
            diag_pivotCount = new int *[M->px];
            diag_maxRow = new int *[M->px];

            for(int i=0; i<M->px; i++) {
                int proc = TheComm().getproc(i, M->pj);
                diag_readyBelowCounts[i] = table[proc];
                neigh_LU_cols[i] = diag_readyBelowCounts[i] + M->ny;
                diag_pivotCount[i] = neigh_LU_cols[i] + 1;
                diag_maxRow[i] = diag_pivotCount[i] + 1;
            }

            maxColV = new double[M->px];
            assert(maxColV != NULL);
            bzero(maxColV, M->px*sizeof(double));

            diag_maxColV = new double *[M->px];
            assert(diag_maxColV != NULL);
            TheComm().ArrayAttach(maxColV, (void **)table);
            for(int i=0; i<M->px*M->py; i++) {
                assert(table[i] != NULL);
            }
            for(int i=0; i<M->px; i++) {
                int proc = TheComm().getproc(i, M->pj);
                diag_maxColV[i] = (double *)table[proc];
            }

            delete [] table;

            if(M->pi==0) {
                /*reset by top proc in each column of the proc grid*/
                resetLU_col(0, 0); /*dummy block co-ordinates*/
            }
            initReadyBelowCount();
            resetPivotCandidates(0, 0);
        }

        ~ColumnVariables() {
            delete [] readyBelowCount; /*rest deleted by this deletion*/
            delete [] maxColV;
        }

        void signalLU_col(int I, int J, int LU_col) {
            assert(I==J); /*only diagonal blocks have access*/

            for(int i=0; i<M->px; i++) {
                int *dst = neigh_LU_cols[i];
                int proc = TheComm().getproc(i, M->pj);
                assert(dst != NULL);
                //       TheComm().Put(&LU_col, dst, sizeof(int), proc);
                TheComm().NbPut(&LU_col, dst, sizeof(int), proc);
            }
        }

        int getShadowLU_col(int I, int J) {
            assert(I==J); /*should be a diagonal. Why else are you interested?*/
            return *shadow_LU_col;
        }

        int getReadyBelowCount(int I, int J) { 
            assert(I%M->px==M->pi);
            assert(J%M->py == M->pj);
            return readyBelowCount[J/M->py];
        }

        /*FIXME: optimize the updates through diag_readyBelowCount to happen
          only when all blocks in this proc have reached done mulsubs for
          this column*/
        void reportReadyBelowCount(int I, int J, int mulSubCount, Profiler &prof) {
            assert(mulSubCount <= min(I,J));

            int one=1;
            struct AmMsg msg;
            int *dst = diag_readyBelowCounts[mulSubCount%M->px] + (J/M->py);
            int proc = TheComm().getproc(mulSubCount%M->px, M->pj);
            //TheComm().AtomicAdd(one, dst, proc, prof);
            /* Replace Atomic with AmSend */
            msg.dst = dst;
            msg.val = 1;
            TheComm().AmSend(proc, (void *) HDR_HNDL_READY_BELOW_COUNT, 
                    &msg, sizeof(struct AmMsg));
        }

        /*FIXME: optimize to report pivot candidate for all blocks in this
          column at this proc*/
        void reportPivotCandidate(int I, int J, double mColV, int mRow, Profiler &prof) {
            assert(I>=J); /*is a block on or below diagonal*/
            assert((mRow/M->B)>=J);
            assert((mRow/M->B)<=M->px*M->nx);

            //     cerr<<TheComm().here()<<"::Reporting pivot candidate I="<<I<<" J="<<J
            //  <<" mColV="<<mColV<<" mRow="<<mRow<<endl;

            int diag = min(I, J);
            int proc = TheComm().getproc(diag%M->px, diag%M->py);
            int me = TheComm().getproc(M->pi, M->pj);
            double *dstColV = diag_maxColV[diag%M->px] + M->pi;
            int *dstRow = diag_maxRow[diag%M->px] + M->pi;
            int *dstpivotCount = diag_pivotCount[diag%M->px];

            double val = fabs(mColV);    

            if(maxColV[M->pi] < val) {
                maxColV[M->pi] = val; //record locally for comparison
                maxRow[M->pi]  = mRow;
            }

            //     cerr<<TheComm().here()<<"::New pivot candidate I="<<I<<" J="<<J
            //  <<" maxColV[pi]="<<maxColV[M->pi]<<" maxRow[pi]="<<maxRow[M->pi]<<endl;

            /*need to fix this version*/
            /* SS: what does that mean? */
            x10lib_cws::atomic_add(pivotCount, 1);

            if(proc != me) {
                /*#pivots (in terms of blocks) computed in this proc*/
                int nupdates = M->nx - (J/M->px) - ((J%M->px) > M->pi ? 1 : 0); 

                if(!(*pivotCount <= nupdates)) {
                    cerr<<TheComm().here()<<"::ERROR: I="<<I<<" J="<<J<<" nupdates="<<nupdates
                        <<" pivotCount="<<*pivotCount<<endl;
                    assert(*pivotCount <= nupdates);
                }

                if(*pivotCount == nupdates)  {
                    //      cerr<<TheComm().here()<<":: reporting pivot for diag="<<diag
                    //          <<" val="<<val<<" row="<<mRow<<" nupdates="<<nupdates<<endl;

                    double colv=maxColV[M->pi];
                    int row = maxRow[M->pi];
                    struct AmMsg msg[3];
                    assert(colv>0.0);

                    /*The proc handling diag will do it after computing the pivot*/
                    *pivotCount=0;
                    maxColV[M->pi] = -1;
                    maxRow[M->pi]=-1;

                    //SS: Replace the following blocking PUTs by
                    //combining the messages into the AmSend
                    //TheComm().Put(&colv, dstColV, sizeof(double), proc);
                    //TheComm().Put(&row, dstRow, sizeof(int), proc);

                    msg[0].dst = dstColV;
                    msg[0].val = colv;

                    msg[1].dst = dstRow;
                    msg[1].val = row;
                    
                    //TheComm().AtomicAdd(nupdates, dstpivotCount, proc, prof);         
                    /* Replace Atomic with AmSend */
                    msg[2].dst = dstpivotCount;
                    msg[2].val = nupdates;
                    TheComm().AmSend(proc, (void *) HDR_HNDL_PIVOT_CANDIDATE, &msg, 
                            sizeof(struct AmMsg) * 3);         
                }
            }
        }

        void resetPivotCandidates(int I, int J) {
            *pivotCount = 0;
            MEM_BARRIER();
            for(int i=0; i<M->px; i++) {
                maxColV[i] = -1.0; /*all pivots are stored as absolute. So this is
                                     never chosen*/
                maxRow[i] = -1;
            }
            MEM_BARRIER();
        }

        int getPivotCount(int I, int J) volatile {
            assert(I==J);
            assert(I%M->px == M->pi);
            assert(J%M->py == M->pj);

            return *pivotCount;
        }

        int *getMaxRows(int I, int J) {
            assert(*pivotCount = M->px*M->nx-I);
            return maxRow;
        }

        double *getMaxColV(int I, int J) {
            assert(*pivotCount = M->px*M->nx-I);
            return maxColV;
        }
};


#if 0
/*began implemenation of caching for inputs blocks to mulsub. They can
  be reused across mulsub-s within a process. To be
  completed. Commented for now. */

/*-------------------Cached ready blocks-------------------*/

/*Read-only cache. Simple scheme. User assumed to issue matching
  request and release for blocks. A particular cache-block cannot
  handle multiple outstanding requests for distincts blocks. All inits
  are expected to be waited upon. */

class CachedBlock {
    private:
        enum CachedBlockState { BLOCK_INVALID, /*no valid data*/
            BLOCK_PENDING, /*a request for the block is pending*/
            BLOCK_READY  /*valid data available in buffer*/
        };

        double * const buf; /*buffer containing the data*/
        const int B; /*Block size*/
        CachedBlockState state; /*state of this block*/
        TwoDBlockCyclicArray * const M; /*Array whose blocks are cached*/
        int I, J; /*The block being cached*/
        int nrefs; /*Counter on #outstanding references*/

        LapiCommNBH nbh; /*non-blocking handle for outstanding block get*/

    public:
        CachedBlock(int sB, TwoDBlockCyclicArray *sM) 
            : B(sB), buf(new double [sB*sB]), state(BLOCK_INVALID), nrefs(0) {
                assert(buf != NULL);
            }

        ~CachedBlock() { delete [] buf; }

        void RequestInit(int I, int J) {
            if(this->I==I && this->J==J) {
                if(state == BLOCK_INVALID) {
                    M->getData(I, J, buf, &nbh);
                }
                return;
            }
            assert(nrefs==0); 
            I=this->I;
            J=this->J;
            M->getData(I, J, buf, &nbh);
            state = BLOCK_PENDING;
            ++nrefs;
        }

        double *RequestWait(int I, int J) {
            assert(I==this->I);
            assert(J==this->J);
            assert(state != BLOCK_INVALID);
            assert(nrefs>0); /*should have init-ed before*/
            if(state == BLOCK_PENDING) {
                TheComm().Wait(&nbh);
            }
            return buf;
        }

        bool IsRequestReady(int I, int J) {
            bool rval = false;

            assert(I==this->I);
            assert(J==this->J);
            assert(nrefs>0); /*should have init-ed before*/

            switch(state) {
                case BLOCK_INVALID:
                    assert(0);
                    break;
                case BLOCK_PENDING:
                    if(TheComm().IsDone(&nbh)) {
                        state = BLOCK_VALID;
                        rval = true;
                    }
                    rval = false;
                    break;
                case BLOCK_VALID:
                    rval = true;
                    break;
                default:
                    assert(0);
            }
            return rval;
        }

        void Release(int I, int J) {
            assert(this->I == I);
            assert(this->J==J);
            assert(nrefs>0);
            assert(state == BLOCK_VALID);
            --nrefs;
        }  

        bool IsBlockDone(int I, int J) {
            assert(this->I==I);
            assert(this->J==J);
            assert(I!=J); /*for now; actually why would this block enter
                            cache for mulsub (backsolve -- yes)*/
            if(I>=J) {

            }
        }
};
#endif


/*--------Dense 2-D block and operations on it-------------*/

typedef struct mulsub_frame_t {
    int PC;
    double *left, *upper;
    LapiCommNBH nbh_l, nbh_u;

    mulsub_frame_t() : PC(0) {}
} MULSUB_FRAME;

typedef struct backsolve_frame_t {
    int PC;
    double *diag;
    LapiCommNBH nbh_d;

    backsolve_frame_t() : PC(0) {}
} BACKSOLVE_FRAME;

typedef struct step_lu_frame_t {
    double *block; /* Need to use this memory for non-blocking Get */
    LapiCommNBH nbh_s;
    bool nbget_started; /* Check if we started a get on this block! */
} STEPLU_FRAME;

/**
 * A B*B array of doubles, whose top left coordinate is i,j).
 * get/set operate on the local coordinate system, i.e.
 * (i,j) is treated as (0,0).
 * @author VijaySaraswat
 *
 * The data within the blocks is stored in column-major order to
 * reduce the hassle in actually using fortran blas routine
 *
 */
class Block {
    public:
        double *A;
        TwoDBlockCyclicArray * M; //Array of which this block is part

        bool ready; //for convenience stored here as well

        void setReady() { 
            assert(ready == false);
            M->cv->resetReadyBelowCount(I, J);
            if(I==J) {
                M->pv->signalReady(I, J);
            }
            ready=true; 
            M->rv->signalReady(I, J); 
        }

    private:
        // counts the number of phases left for this
        // block to finish its processing;
        const int maxCount;
        volatile int count; //In PLU, other threads read count

        int LU_col; /*The column within a block being panel factorized*/

        /*proto-threads like implementation for communication
          overlap. Instead of doing this for every method, we make the Block
          object itself a thread that stores state for all methods it
          invokes that need this facility.

          Also instead of being generic and stuff, I just use gotos. 
          */
        enum StateLabels { LABEL_0=0, LABEL_1, LABEL_2, LABEL_3, LABEL_4 };

        MULSUB_FRAME msf;
        BACKSOLVE_FRAME bsf;
        STEPLU_FRAME slu;

        void updateLU_col(int val) {
            LU_col = val;
            if(I==J)
                M->cv->signalLU_col(I, J, LU_col);
        }

        void computeMax(int col, Profiler &prof) {
            computeMax(col, 0, prof);
        }

        void computeMax(int col, int start_row, Profiler &prof) {
            int maxRow;
            double maxColV;
            assert(B > 0);
            assert(col>=0 && col<B);
            assert(start_row>=0 && start_row<B);
            maxColV = get(start_row,col);
            maxRow = I*B+start_row;
            for(int i=start_row; i<B; i++) {
                if(fabs(get(i,col)) > fabs(maxColV)) {
                    maxColV = get(i,col);
                    maxRow = I*B+i;
                }
            }               
            M->cv->reportPivotCandidate(I, J, fabs(maxColV), maxRow, prof);
        }

        bool stepLU(Profiler &prof);

    public:
        const int I,J, B;

        Block(int sI, int sJ, int sB, TwoDBlockCyclicArray *const sM, double *data)
            : I(sI), J(sJ), B(sB), M(sM), maxCount(min(sI,sJ)), ready(false), 
        count(0), A(data), LU_col(-1) {
            assert( A != NULL);
            /* SS: Initialize NBH communication handles for sanity */
            TheComm().InitCounter(&bsf.nbh_d, 0);
            TheComm().InitCounter(&msf.nbh_u, 0);
            TheComm().InitCounter(&msf.nbh_l, 0);
            TheComm().InitCounter(&slu.nbh_s, 0);
            slu.block = new double [B];
            slu.nbget_started = false;
        }

        Block(const Block &b, double *data) 
            : I(b.I), J(b.J), B(b.B), M(b.M), maxCount(b.maxCount), ready(b.ready), 
        count(0), A(data), LU_col(-1)  {
            assert(maxCount == min(I,J));
            assert(A != NULL);
            //also copy the data
            for(int i=0; i<B*B; i++)
                A[i] = b.A[i];
        }
        ~Block() { 
            delete [] slu.block;
        }

        Block *copy(double *data) {
            return new Block(*this, data);
        }
        /*printing in row-major order for laymen like me*/
        void display() {
            //cout<<"I="<<I<<" J="<<J<<endl;
            for(int i=0; i<B; i++) {
                for(int j=0; j<B; j++) {
                    cout<<format(A[i+j*B],6) << " ";
                }
                //cout<<endl;;
            }
        }
        void init() {
            for (int i=0; i < B*B; i++)
                A[i] = format(rand()*2.0/RAND_MAX + 1.0, 4);
        }
        bool step(Profiler &prof);

        int  ord(int i, int j) {
            return i+j*B;
        }
        double  get(int i, int j) {
            return A[ord(i,j)];
        }
        void  set(int i, int j, double v) {
            A[ord(i,j)] = v;
        }
        void  negAdd(int i, int j, double v) {
            A[ord(i,j)] -= v;
        }
        void  posAdd(int i, int j, double v) {
            A[ord(i,j)] += v;
        }


        //permute, for the columns in this block, 
        //row1 in this block with row2 (in potentially some other block)*/
        //not timed: for use outside profiler
        void permute(int row1, int row2) {
            assert (row1 != row2); //why was this called then?
            assert (row1>=I*B && row1<(I+1)*B); //should be a row in this block
            if(!(row2>=0 & (row2/B)<M->nx*M->px)) {
                //       cerr<<"ERROR: permute. I="<<I<<" J="<<J<<" row1="<<row1<<" row2="<<row2<<endl;
                assert(row2>=0 & (row2/M->B)<M->nx*M->px);
            }
            assert(row2>=row1);
            //Block *b = M->get(row2/B, J); //the other block

            //     cerr<<"Permuting rows "<<row1<<" and "<<row2<<endl;

            const int base1 = row1%B;
            const int base2 = row2%B;

            const int proc = TheComm().getproc((row2/B)%M->px, J%M->py);
            double * const remote_ptr = M->getRemotePtr(row2/B, J);

            double vtmp1[B], vtmp2[B];

            {
                lapi_vec_t org_vec, tgt_vec;

                org_vec.vec_type = tgt_vec.vec_type = LAPI_GEN_STRIDED_XFER;
                org_vec.num_vecs = tgt_vec.num_vecs = M->B;
                org_vec.len = tgt_vec.len = NULL; /*ignored*/
                unsigned long *srct = new unsigned long[3];
                srct[0] =(unsigned long) vtmp1;
                srct[1] = sizeof(double);
                srct[2] = sizeof(double);
                org_vec.info = (void **)srct;

                unsigned long *tgtt = new unsigned long[3];
                tgtt[0] = (unsigned long)(remote_ptr+base2);
                tgtt[1] = sizeof(double);
                tgtt[2] = B*sizeof(double);
                tgt_vec.info = (void **)tgtt;

                LapiComm* lcomm = (LapiComm*)&TheComm();

                lapi_cntr_t wait_cntr;
                int orig_val;
                (void)LAPI_Getcntr(lcomm->hndl, &wait_cntr, &orig_val);
                int val = orig_val + 1;
                LAPI_Getv(lcomm->hndl, proc, &tgt_vec, &org_vec, NULL, &wait_cntr);
                LAPI_Waitcntr(lcomm->hndl, &wait_cntr, val, NULL);

                delete [] org_vec.info;
                delete [] tgt_vec.info;
            }

            for(int j=0; j<B; j++) {
                vtmp2[j] = this->A[j*B+base1];
                this->A[j*B+base1] = vtmp1[j];
            }

            {
                lapi_vec_t org_vec, tgt_vec;

                org_vec.vec_type = tgt_vec.vec_type = LAPI_GEN_STRIDED_XFER;
                org_vec.num_vecs = tgt_vec.num_vecs = M->B;
                org_vec.len = tgt_vec.len = NULL; /*ignored*/
                unsigned long *srct = new unsigned long[3];
                srct[0] = (unsigned long) vtmp2;
                srct[1] = sizeof(double);
                srct[2] = sizeof(double);
                org_vec.info = (void **)srct;

                unsigned long *tgtt = new unsigned long[3];
                tgtt[0] = (unsigned long)(remote_ptr+base2);
                tgtt[1] = sizeof(double);
                tgtt[2] = B*sizeof(double);
                tgt_vec.info = (void **)tgtt;

                LapiComm* lcomm = (LapiComm*)&TheComm();

                lapi_cntr_t wait_cntr;
                int orig_val;
                (void)LAPI_Getcntr(lcomm->hndl, &wait_cntr, &orig_val);
                int val = orig_val + 1;
                LAPI_Putv(lcomm->hndl, proc, &tgt_vec, &org_vec, NULL, NULL, &wait_cntr);
                LAPI_Waitcntr(lcomm->hndl, &wait_cntr, val, NULL);

                delete [] org_vec.info;
                delete [] tgt_vec.info;
            }

            return;
        }

        void permute(int row1, int row2, Profiler& prof) {
            nano_time_t s = nanoTime();
            permute(row1, row2);
            nano_time_t t = nanoTime();
            prof.log_permute(t-s);
        }

        void lower_nb(int col, int done, Profiler &prof) {

            nano_time_t s = nanoTime();

            if (done) {
                /* We had started data transfer before,
                 * now we have to process it */
                {      
                    char TRANSA = 'N';
                    Integer M = B;
                    Integer N = col;
                    double ALPHA = -1.0;
                    double *mA = this->A;
                    Integer LDA = B;
                    double *mX = slu.block;
                    Integer INCX = 1;
                    double BETA = 1.0;
                    double *mY = this->A+ (B*col);
                    Integer INCY = 1;

                    DGEMV(&TRANSA, &M, &N, &ALPHA, mA, &LDA,
                            mX, &INCX, &BETA, mY, &INCY);
                }

                {   
                    Integer N = B;
                    double pivot = slu.block[col]; 
                    double DA = 1.0/pivot;
                    double *DX = this->A + (B*col);
                    Integer INCX = 1;
                    DSCAL(&N, &DA, DX, &INCX);
                }

            } else {
                /* Start data transfer */
                const int diag = min(I,J);

                assert(NULL != slu.block);

                M->getDataCol(diag, diag, col, slu.block, &slu.nbh_s);    
            }

            nano_time_t t = nanoTime();
            prof.log_lower(t-s);
        }

        void lower(int col, Profiler &prof) {
            nano_time_t s = nanoTime();

            /* SS: This routine should not be used now! */
            assert(0);

            double *buf= new double[B];
            assert(buf != NULL);

            const int diag = min(I,J);
            M->getDataCol(diag, diag, col, buf);    

            /*DGEMV+DSCAL: compute 
              this(0..B-1,col) -= this(0..B-1,0..col-1)*diag(0..col-1,col)
              this(0..B-1,col) /= diag(col,col)*/

            {      
                char TRANSA = 'N';
                Integer M = B;
                Integer N = col;
                double ALPHA = -1.0;
                double *mA = this->A;
                Integer LDA = B;
                double *mX = /*diag->A  + (B*col)*/  buf;
                Integer INCX = 1;
                double BETA = 1.0;
                double *mY = this->A+ (B*col);
                Integer INCY = 1;

                DGEMV(&TRANSA, &M, &N, &ALPHA, mA, &LDA,
                        mX, &INCX, &BETA, mY, &INCY);
            }

            {   
                Integer N = B;
                double pivot = /*diag->get(col, col)*/ buf[col]; 
                double DA = 1.0/pivot;
                double *DX = this->A + (B*col);
                Integer INCX = 1;
                DSCAL(&N, &DA, DX, &INCX);
            }
            delete [] buf;
            nano_time_t t = nanoTime();
            prof.log_lower(t-s);
        }

        bool backSolve(Profiler &prof) {
            nano_time_t s = nanoTime();

            const int diag = min(I, J);

            switch(bsf.PC) {
                case LABEL_0:
                    if(M->IsLocal(diag, diag)) {
                        bsf.diag = M->get(diag, diag)->A;
                    }
                    else {
                        bsf.diag = new double[B*B];
                        assert(bsf.diag != NULL);

                        M->getData(diag, diag, bsf.diag, &bsf.nbh_d);
                    }
                    bsf.PC = LABEL_1;

                case LABEL_1:
                    if(!M->IsLocal(diag, diag) && !TheComm().IsDone(&bsf.nbh_d)) 
                        break;
                    bsf.PC = LABEL_2;

                case LABEL_2:
                    if(!M->pv->ready(I,J))
                        break;
                    bsf.PC = LABEL_3;

                case LABEL_3:
                    for(int i=I*B; i<(I+1)*B; i++) {
                        int pivot = M->pv->getPivot(I, J, i);
                        assert(pivot>=0);
                        assert(pivot>=i);
                        if(pivot != i)
                            permute(i, pivot, prof);
                    }

                    {
                        /*DTRSM: solve diag*X = 1.0*this; this is overwritten with X*/
                        char SIDE = 'L'; //diag is to left of X
                        char UPLO = 'L'; //diag is lower-triangular
                        char TRANSA = 'N'; //No transpose of diag
                        char DIAG = 'U'; //Unit-diagonal for diag
                        Integer M = B; //#rows of diag
                        Integer N = B; //#cols of diag
                        double ALPHA = 1.0; //scale on right-hand size
                        double *mA = /*diag->A*/ /*buf*/ bsf.diag;
                        Integer LDA = B;
                        double *mB = this->A;
                        Integer LDB = B;

                        DTRSM(&SIDE, &UPLO, &TRANSA, &DIAG, &M, &N, &ALPHA,
                                mA, &LDA, mB, &LDB);
                    }
                    if(!M->IsLocal(diag, diag)) {
                        delete [] bsf.diag;
                    }
                    bsf.PC = LABEL_0;
                    break;
                default:
                    assert(0);
            }
            nano_time_t t = nanoTime();
            prof.log_bsolve(t-s);
            return (bsf.PC == LABEL_0? true:false);
        }

        bool mulsub(int count, Profiler &prof) {
            nano_time_t s1, t1, s2, t2, s3, t3;
            //     cerr<<"mulsub. I="<<I<<" J="<<J<<" count="<<count<<endl;

            s2 = nanoTime();
            switch(msf.PC) {
                case LABEL_0:
                    assert(count>=0);
                    assert(count <= min(I,J));

                    if(M->IsLocal(I, count)) {
                        msf.left = M->get(I, count)->A;
                    }
                    else {
                        s3 = nanoTime();
                        msf.left = new double[B*B];
                        assert(msf.left != NULL);
                        M->getData(I, count, msf.left, &msf.nbh_l);
                        prof.log_new(nanoTime()-s3);
                    }

                    if(M->IsLocal(count, J)) {
                        msf.upper = M->get(count, J)->A;
                    }
                    else {
                        s3 = nanoTime();
                        msf.upper = new double[B*B];
                        assert(msf.upper != NULL);
                        M->getData(count, J, msf.upper, &msf.nbh_u);
                        prof.log_new(nanoTime()-s3);
                    }      
                    msf.PC = LABEL_1;

                case LABEL_1:
                    s3 = nanoTime(); 
                    if(!M->IsLocal(I, count) && !TheComm().IsDone(&msf.nbh_l)) {
                        prof.log_isdone(nanoTime()-s3);
                        break;
                    }
                    prof.log_isdone(nanoTime()-s3);
                    msf.PC = LABEL_2;

                case LABEL_2:
                    s3 = nanoTime(); 
                    if(!M->IsLocal(count, J) && !TheComm().IsDone(&msf.nbh_u)) {
                        prof.log_isdone(nanoTime()-s3);
                        break;
                    }
                    prof.log_isdone(nanoTime()-s3);
                    msf.PC = LABEL_3;

                case LABEL_3:
                    s1 = nanoTime();
                    {
                        double *mA = /*left->A*/ msf.left;
                        double *mB = /*upper->A*/ msf.upper;
                        double *mC = A;

                        char transa = 'N', transb = 'N';
                        Integer m=B, n=B, k=B;
                        double alpha = -1.0;
                        Integer lda = B, ldb = B, ldc = B;
                        double beta = 1.0;

                        DGEMM(&transa, &transb, &m, &n, &k, &alpha, mA, &lda,
                                mB, &ldb, &beta, mC, &ldc);
                    }
                    t1 = nanoTime();
                    prof.log_dgemm(t1-s1);

                    if(!M->IsLocal(count, J))
                        delete [] msf.upper;
                    if(!M->IsLocal(I, count))
                        delete [] msf.left;
                    msf.PC = LABEL_0; /*for use with the next count. NOTE: We use
                                        that the fact there is only one outstanding
                                        mulsub on any Block */
                    break;
                default:
                    assert(0); //why are we here?
            }
            t2 = nanoTime();
            prof.log_mulsub(t2-s2);
            return (msf.PC==LABEL_0 ? true : false);
        }
        /*return true on completion*/
        bool LU(int col, Profiler &prof) {
            assert(fabs(get(col, col)) >0); /*valid pivot*/
            nano_time_t s = nanoTime();
            for (int i = 0; i < B; i++) {
                double r = 0.0;
                for(int k=0; k<min(i,col); k++)
                    r += get(i,k) * get(k,col);
                negAdd(i,col, r);
                if(i>col) set(i,col, get(i,col)/get(col,col));
            }               
            nano_time_t t = nanoTime();
            prof.log_lu(t-s);
            return true;
        }
};

/*---------Definitions after necessary forward declarations-------------*/

TwoDBlockCyclicArray::TwoDBlockCyclicArray(int spx, int spy, int snx, int sny,int sB) 
    : px(spx), py(spy), nx(snx), ny(sny), B(sB), N(spx*snx*sB),
    pi(TheComm().getpi()), pj(TheComm().getpj()) {

        assert(px*nx==py*ny);
        A = new Block* [nx*ny];
        assert(A != NULL);

        double *buf = new double[nx*ny*B*B];
        assert(buf != NULL);

        globalData = new double *[px*py];
        assert(globalData != NULL);

        buf_to_free = buf;

        TheComm().ArrayAttach(buf, (void **)globalData);

        for(int i=0; i<px*py; i++) {
            assert(globalData[i] != NULL);
        }

        srand(32+TheComm().here());

        int ctr=0;
        for(int i=0; i<nx; i++) {
            for(int j=0; j<ny; j++) {
                A[ctr] = new Block(i*px+pi, j*py+pj, B, this, buf);
                assert(A[ctr] != NULL);
                A[ctr]->init();
                ++ctr;
                buf += B*B;
            }
        }

        rv = new ReadyVariable(this);
        cv = new ColumnVariables(this);
        pv = new PivotVariable(this);
    }

TwoDBlockCyclicArray::TwoDBlockCyclicArray(const TwoDBlockCyclicArray &arr) 
    : px(arr.px), py(arr.py), nx(arr.nx), ny(arr.ny), B(arr.B), N(arr.N), pi(arr.pi), pj(arr.pj)  {

        assert(px*nx==py*ny);
        A = new Block *[nx*ny];
        assert(A != NULL);
        double *buf = new double[nx*ny*B*B];
        assert(buf != NULL);

        globalData = new double *[px*py];
        assert(globalData != NULL);

        buf_to_free = buf;

        TheComm().ArrayAttach(buf, (void **)globalData);
        for(int i=0; i<px*py; i++) {
            assert(globalData[i] != NULL);
        }

        int ctr=0;
        for(int i=0; i<nx; i++) {
            for(int j=0; j<ny; j++) {
                A[ctr] = arr.A[ctr]->copy(buf);
                assert( A[ctr] != NULL );
                A[ctr]->M = this;
                ++ctr;
                buf += B*B;
            }
        }

        rv = new ReadyVariable(this);
        cv = new ColumnVariables(this);
        pv = new PivotVariable(this);
    }

TwoDBlockCyclicArray::~TwoDBlockCyclicArray() {
    double *data = A[0]->A;
    for(int i=0; i<nx*ny; i++) {
        delete A[i];
    }
    delete [] A;
    delete [] data; /*deletes all data buffer in blocks*/
    delete [] globalData;
    delete [] buf_to_free;
    delete rv;
    delete cv;
    delete pv;
}

void 
TwoDBlockCyclicArray::global_permute(int I, int J, int row1, int row2) {
    assert (row1 != row2); //why was this called then?
    assert (row1>=I*B && row1<(I+1)*B); //should be a row in this block
    assert(row2>=0);
    assert(row2>=row1);

    const int base1 = row1%B;
    const int base2 = row2%B;

    const int proc1 = TheComm().getproc((row1/B)%px, J%py);
    const int proc2 = TheComm().getproc((row2/B)%px, J%py);

    double * const rptr1 = getRemotePtr(row1/B, J);
    double * const rptr2 = getRemotePtr(row2/B, J);

    double vtmp1[B], vtmp2[B];

    /*FIXME: Implement strided gets/puts later?*/
    for(int j=0; j<B; j++) {
        TheComm().Get(rptr1+j*B+base1, &vtmp1[j], sizeof(double), proc1);
        TheComm().Get(rptr2+j*B+base2, &vtmp2[j], sizeof(double), proc2);
    }

    for(int j=0; j<B; j++) {
        TheComm().Put(&vtmp2[j], rptr1+j*B+base1,  sizeof(double), proc1);
        TheComm().Put(&vtmp1[j], rptr2+j*B+base2,  sizeof(double), proc2);
    }  
    return;
}


void
TwoDBlockCyclicArray::gatherPivots(int *pivots, int root) {
    int **table = new int *[px*py];

    /*for verification*/
    if(TheComm().here()==root) {
        assert(pivots != NULL);
        for(int i=0; i<nx*px*B; i++) {
            pivots[i] = -1; 
        }
    }

    TheComm().ArrayAttach(pivots, (void **)table);

    if(TheComm().getpj()==0) {
        int *localPivots = pv->getLocalPivots();

        for(int i=0; i<nx; i++) {
            int off = (i*px+pi)*B;
            void *dst = table[root] + off;
            TheComm().Put(localPivots+i*B, dst, B*sizeof(int), root);
        }
    }

    TheComm().Barrier();  

    if(TheComm().here()==root) {
        for(int i=0; i<px*nx*B; i++) {
            assert(pivots[i] != -1);
        }
    }
    delete [] table;
}

void
TwoDBlockCyclicArray::applyLowerPivots(int *pivots, int root) {
    if(TheComm().here() == root) {
        for(int I=0; I<px*nx; I++) {
            for(int J=0; J<py*ny;  J++) {

                if(I<=J) continue;

                for(int r=I*B; r<(I+1)*B; r++) {
                    assert(pivots[r]>=r);
                    assert(pivots[r]<px*nx*B);
                    if(r != pivots[r]) {
                        global_permute(I, J, r, pivots[r]);
                    }
                }
            }
        }
    }
}

void 
TwoDBlockCyclicArray::applyPivots(int *pivots, int root) {
    if(TheComm().here() == root) {
        for(int I=0; I<px*nx; I++) {
            for(int J=0; J<py*ny;  J++) {

                for(int r=I*B; r<(I+1)*B; r++) {
                    assert(pivots[r]>=r);
                    assert(pivots[r]<px*nx*B);
                    if(r != pivots[r]) {
                        global_permute(I, J, r, pivots[r]);
                    }
                }
            }
        }
    }
}

void 
TwoDBlockCyclicArray::display(const char *msg) {
    if(TheComm().here()==0) {
        cout<<msg<<endl;;
        cout<<"px="<<px<<" py="<<py<<" nx="<<nx<<" ny="<<ny<<" B="<<B<<endl;;

        double *buf = new double [B*B];
        assert(buf != NULL);

        for(int I=0; I<px*nx; I++) {
            for(int J=0; J<py*ny; J++) {
                getData(I,J, buf);

                for(int i=0; i<B; i++) {
                    for(int j=0; j<B; j++) {
                        cout<<format(buf[i+j*B], 6)<<" ";
                    }
                }
                //get(I,J)->display();
            }
            //cout<<endl;
        }
        cout<<endl;
        delete [] buf;
    }
}

/*------------Definition after necessary forward-declarations---------------*/

/*Try to step through the next ready operation in given priority
  order. An operation is LU, backSolve, lower, or mulSub on a block. */
bool 
Block::step(Profiler &prof) {
    prof.log_step();

    if(ready) return false;

    if (count == maxCount) {
        if(I<J && M->rv->ready(I,I)) {
            if( (I==0 || M->cv->getReadyBelowCount(I,J) == M->px*M->nx-I)
                    && M->pv->ready(I, J)) {
                if(backSolve(prof)) {
                    setReady();
                    return true;
                }
                return false;
            }
            return false;
        }
        else if (I >=J) {
            return stepLU(prof);
        }
        else
            return false;
    }
    if(M->rv->ready(I, count) && M->rv->ready(count, J)) {
        if(mulsub(count, prof)) {
            count++;
            M->cv->reportReadyBelowCount(I,J,count, prof);
            return true;
        }
        return false;
    }
    return false;
}

//stepping through to perform panel factorization
bool
Block::stepLU(Profiler &prof) {
    prof.log_stepLU();
    assert (I >= J);
    assert (count == maxCount);
    assert (ready == false);
    assert (LU_col < B);

    if(I==J) {
        if(I>0 && M->cv->getReadyBelowCount(I,J) < M->px*M->nx-I)
            return false;
    }

    if(I == J) {
        if(LU_col>=0) {
            int pivotCount = M->cv->getPivotCount(I,J);

            if(!(pivotCount <= M->px*M->nx - I)) {
                cerr<<"error: I="<<I<<" J="<<J<<" pivotCount="<<pivotCount<<endl;
                assert(pivotCount <= M->px*M->nx - I);
            }

            if(pivotCount < M->px*M->nx - I) {
                return false;
            }

            int *maxRow = M->cv->getMaxRows(I, J);
            double *maxColV = M->cv->getMaxColV(I, J);

            int mrow=-1;
            double maxv = -1.0;
            for(int i=0; i<M->px; i++) {
                if(maxColV[i] > maxv) {
                    maxv = maxColV[i];
                    mrow = maxRow[i];
                }
            }

            assert(mrow>=0);
            assert(mrow>=I*B+LU_col);
            M->cv->resetPivotCandidates(I, J);

            M->pv->setPivot(I, J, I*B+LU_col, mrow);
            if(I*B+LU_col != mrow) {
                permute(I*B+LU_col, mrow, prof);
            }
            LU(LU_col, prof);
            if(LU_col==B-1)  {
                setReady();
                M->cv->resetLU_col(I, J);
            }
        }

        int LU_col1 = (LU_col==-1? 0 : LU_col+1);

        if(LU_col1<=B-1)    {
            updateLU_col(LU_col1);
            computeMax(LU_col1, LU_col1, prof);
        }
    }
    else {
        if(LU_col>=0) {
            int diag_LU_col = M->cv->getShadowLU_col(J, J);

            if(!(diag_LU_col > LU_col) && !M->rv->ready(J,J)) {
                return false;
            }

#if 1
            if(slu.nbget_started) {
                /* We're waiting on data to come to us */

                if(TheComm().IsDone(&slu.nbh_s)) {
                    /* Got the data! */
                    lower_nb(LU_col, 1, prof);
                    if(LU_col==B-1) {
                        setReady();
                    } 
                    slu.nbget_started = false;
                } else {
                    /* Nopes, still waiting on it ... */
                    return false;
                }
            } else {
                /* Start non-blocking get */
                lower_nb(LU_col, 0, prof);
                slu.nbget_started = true;
                return false;
            }
#else
            /* This version of "lower" was blocking --
             * waiting on getDataCol */
            lower(LU_col, prof);
            if(LU_col==B-1) {
                setReady();
            } 
#endif
        }
        int LU_col1 = (LU_col==-1?0:LU_col+1);
        if(LU_col1 <= B-1) {
            computeMax(LU_col1, prof);
            updateLU_col(LU_col1);
        }
    }

    return true;
}


/*----------------LU partial pivoting----------------------*/

/*----------------Wrapper class for non-pivoting LU------------------*/

class PLU {
    public: 
        TwoDBlockCyclicArray *M;

        const int nx,ny,px,py,B;

    public:
        /**
          Iterative version, with pivoting.
          */
        PLU(int spx, int spy, int snx, int sny, int sB) 
            :  nx(snx), ny(sny), px(spx), py(spy), B(sB) {
                M = new TwoDBlockCyclicArray(px,py,nx,ny,B);
                assert( M != NULL);
            }


        void run(Profiler &prof) {
            const int nx = M->nx;
            const int ny = M->ny;
            const int pi = TheComm().getpi();
            const int pj = TheComm().getpj();
            //Profiler prof;

            assert(nx>=1);
            assert(ny>=1);

            nano_time_t s = nanoTime();

            //     Block *lastBlock = M->getLocal(pi, pj, nx-1, ny-1);
            int starty = 0;
            int readyCount=0;

            while(/*!lastBlock->ready*/ starty<ny) {
                //MEM_BARRIER();

                assert(ny-1>=starty);
                readyCount=0;

                for(int j=starty; j<min(starty+6,ny); j++) {
                    bool doneForNow = false;
                    for(int i=0; i<nx; i++) {
                        Block *block = M->getLocal(pi, pj, i,j);
                        bool rval = block->step(prof);
                        if(j==starty && block->ready) readyCount += 1;
                        doneForNow = doneForNow | rval;
                    }
                    if(!doneForNow) TheComm().Poll(prof);
                    if(doneForNow) break;
                }
                TheComm().Poll(prof);
                if(readyCount == nx) starty+=1;
            }

            nano_time_t t = nanoTime();
            prof.log_run(t-s);
        }

        nano_time_t plu(Profiler *gProf) {
            run(*gProf);
            return 0;
        }

        int blord(int i, int j) {
            return M->A[0]->ord(i,j);
        }

        void dgemm(double *C, double *A, double *B, int m, int n, int k, int alpha, int beta) {
            for(int a=0; a<m; a++) {
                for(int b=0; b<n; b++) {
                    C[blord(a,b)] *= beta;
                    double v= 0.0;
                    for(int c=0; c<k; c++) {
                        v += A[blord(a,c)] * B[blord(c,b)];
                    }
                    C[blord(a,b)] += alpha*v;
                }
            }
        }

        bool verify(TwoDBlockCyclicArray *Input) {
            int k;
            /* Initialize test. */
            double max_diff = 0.0;

            double *BL = new double [B*B];
            double *BU = new double [B*B];
            double *BLU = new double[B*B];
            assert(BL!=NULL);
            assert(BU!=NULL);
            assert(BLU!=NULL);

            for(int i=0; i<nx; i++) {
                for(int j=0; j<ny; j++) {
                    const int I = i*px + M->pi;
                    const int J = j*py + M->pj;

                    Block *BI = Input->getLocal(M->pi, M->pj, i, j);
                    bzero(BLU, B*B*sizeof(double));

                    int K;
                    for(K=0; K<I && K<J; K++) {
                        M->getData(I, K, BL);
                        M->getData(K, J, BU);
                        dgemm(BLU, BL, BU, B, B, B, 1.0, 1.0);
                    }
                    M->getData(I, K, BL);
                    M->getData(K, J, BU);

                    for(int Ii=0; Ii<B; Ii++) {
                        for(int Jj=0; Jj<B; Jj++) {
                            int Kk;
                            for(Kk=0; K*B+Kk<I*B+Ii && K*B+Kk<=J*B+Jj; Kk++) {
                                BLU[blord(Ii,Jj)] += BL[blord(Ii,Kk)]*BU[blord(Kk,Jj)];
                            }
                            if(K*B+Kk==I*B+Ii && K*B+Kk<=J*B+Jj) { 
                                BLU[blord(Ii,Jj)] += BU[blord(Ii,Jj)];
                            }
                        }
                    }

                    for(int x=0; x<B*B; x++) {
                        double diff = fabs(BLU[x] - BI->A[x]);
                        max_diff = max(diff, max_diff);
                    }
                }
            }

            delete [] BLU;
            delete [] BU;
            delete [] BL;

            /* Check maximum difference against threshold. */
            if (max_diff > 0.0000001)
                return false;
            else
                return true;
        }  
};


/*---------------------Driver---------------------------*/

int main(int argc, char *argv[]) {
    if (argc != 5) {
        cout<<"Usage: LU N b px py"<<endl;
        return 0;
    }
    const int N = atoi(argv[1]);
    const int B = atoi(argv[2]);
    const int px= atoi(argv[3]);
    const int py= atoi(argv[4]);
    const int nx = N / (px*B), ny = N/(py*B);
    assert (N % (px*B) == 0 && N % (py*B) == 0);

    Comm::Init(px, py);

    Profiler gProf; /*all procs simultaneously create a copy*/

    //cout<<TheComm().here()<<"::N="<<N<<" B="<<B<<" px="<<px<<" py="<<py<<endl;
    if(0 == TheComm().here()) {
        cout<<TheComm().nprocs()<<"::N="<<N<<" B="<<B<<" px="<<px<<" py="<<py<<endl;
    }

    PLU *plu = new PLU(px,py,nx,ny,B);

    TheComm().Barrier();
    TwoDBlockCyclicArray *A = plu->M->copy();

    TheComm().Barrier();
    long long s = NanoTime();
    plu->plu(&gProf);
    long long t = NanoTime();
    TheComm().Barrier();

    gProf.report(0);

    /* SS: This barrier is added so that rank 0 is polling
     * inside LAPI to ensure that the Atomic ops by other
     * processes complete in a timely fashion at 0 */
    TheComm().Barrier();


    int *pivots = new int[N];
    assert(pivots!=NULL);
    plu->M->gatherPivots(pivots, 0);
    plu->M->applyLowerPivots(pivots, 0);
    A->applyPivots(pivots, 0);

    TheComm().Barrier();
    bool correct = plu->verify(A);

#if 0
    cout<<"N="<<N<<" px="<<px<<" py="<<py<<" B="<<B
        <<(correct?" ok":" fail")
        <<" rt time="<<(t-s)/1000000.0<<"ms"
        <<" rt Rate="<< format(flops(N)/(t-s)*1000, 3)<<"MFLOPS"
        <<endl;
#endif

    double rate =  format(flops(N)/(t-s)*1000, 3);

    /* Combine all results at root */
    gProf.report_results(0, rate, correct? 1:0);
    
    TheComm().Barrier();

    gProf.display_results(0, rate, correct? 1:0);

    TheComm().Barrier();

    gProf.display(0);

    TheComm().Barrier();

    delete plu;
    delete A;

    Comm::Finalize();
}
