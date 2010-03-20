#include <cstdlib>
#include <cstdio>
#include <cassert>
#include <cstring>
#include <cstdarg>
#include <new>

#include <pthread.h>

#include <x10rt_types.h>
#include <x10rt_internal.h>
#include <x10rt_cuda.h>

#ifdef ENABLE_CUDA

#include <cuda.h> // Proprietory nvidia header

//#define TRACE 1

namespace {

    // TODO: fine grained synchronisation, lock free datastructures
    pthread_mutex_t big_lock_of_doom;

    inline void DEBUG(const char *fmt, ...) {
        (void) fmt;
        va_list ap;
        va_start(ap, fmt);
        #ifdef TRACE
        vfprintf(stderr, fmt, ap);
        #endif
        va_end(ap);
    }


    /* Macros to check return codes {{{ */
    #define CU_SAFE(x) cu_safe(x,__FILE__,__LINE__)
    static void cu_safe (CUresult x, const char *file, int line)
    {
        /* One could add a macro to turn off this test if needed. */
        const char *errstr = "unmapped error code";
        switch (x) {
            case CUDA_SUCCESS: return;

            case CUDA_ERROR_INVALID_VALUE:
                errstr = "CUDA_ERROR_INVALID_VALUE"; break;
            case CUDA_ERROR_OUT_OF_MEMORY:
                errstr = "CUDA_ERROR_OUT_OF_MEMORY"; break;
            case CUDA_ERROR_NOT_INITIALIZED:
                errstr = "CUDA_ERROR_NOT_INITIALIZED"; break;
            case CUDA_ERROR_DEINITIALIZED:
                errstr = "CUDA_ERROR_DEINITIALIZED"; break;

            case CUDA_ERROR_NO_DEVICE:
                errstr = "CUDA_ERROR_NO_DEVICE"; break;
            case CUDA_ERROR_INVALID_DEVICE:
                errstr = "CUDA_ERROR_INVALID_DEVICE"; break;

            case CUDA_ERROR_INVALID_IMAGE:
                errstr = "CUDA_ERROR_INVALID_IMAGE"; break;
            case CUDA_ERROR_INVALID_CONTEXT:
                errstr = "CUDA_ERROR_INVALID_CONTEXT"; break;
            case CUDA_ERROR_CONTEXT_ALREADY_CURRENT:
                errstr = "CUDA_ERROR_CONTEXT_ALREADY_CURRENT"; break;
            case CUDA_ERROR_MAP_FAILED:
                errstr = "CUDA_ERROR_MAP_FAILED"; break;
            case CUDA_ERROR_UNMAP_FAILED:
                errstr = "CUDA_ERROR_UNMAP_FAILED"; break;
            case CUDA_ERROR_ARRAY_IS_MAPPED:
                errstr = "CUDA_ERROR_ARRAY_IS_MAPPED"; break;
            case CUDA_ERROR_ALREADY_MAPPED:
                errstr = "CUDA_ERROR_ALREADY_MAPPED"; break;
            case CUDA_ERROR_NO_BINARY_FOR_GPU:
                errstr = "CUDA_ERROR_NO_BINARY_FOR_GPU"; break;
            case CUDA_ERROR_ALREADY_ACQUIRED:
                errstr = "CUDA_ERROR_ALREADY_ACQUIRED"; break;
            case CUDA_ERROR_NOT_MAPPED:
                errstr = "CUDA_ERROR_NOT_MAPPED"; break;

            case CUDA_ERROR_INVALID_SOURCE:
                errstr = "CUDA_ERROR_INVALID_SOURCE"; break;
            case CUDA_ERROR_FILE_NOT_FOUND:
                errstr = "CUDA_ERROR_FILE_NOT_FOUND"; break;

            case CUDA_ERROR_INVALID_HANDLE:
                errstr = "CUDA_ERROR_INVALID_HANDLE"; break;

            case CUDA_ERROR_NOT_FOUND:
                errstr = "CUDA_ERROR_NOT_FOUND"; break;

            case CUDA_ERROR_NOT_READY:
                errstr = "CUDA_ERROR_NOT_READY"; break;

            case CUDA_ERROR_LAUNCH_FAILED:
                errstr = "CUDA_ERROR_LAUNCH_FAILED"; break;
            case CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES:
                errstr = "CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES"; break;
            case CUDA_ERROR_LAUNCH_TIMEOUT:
                errstr = "CUDA_ERROR_LAUNCH_TIMEOUT"; break;
            case CUDA_ERROR_LAUNCH_INCOMPATIBLE_TEXTURING:
                errstr = "CUDA_ERROR_LAUNCH_INCOMPATIBLE_TEXTURING"; break;
            case CUDA_ERROR_UNKNOWN:
                errstr = "CUDA_ERROR_UNKNOWN"; break;
        }
        fprintf(stderr,"%s (At %s:%d)\n",errstr,file,line);
        abort();
    }
    /* }}} */


    size_t dma_slice_sz (void) {
        static size_t sz = 0;
        if (sz == 0) {
            const char *env_var = "X10RT_CUDA_DMA_SLICE";
            const char *str = getenv(env_var);
            sz = str!=NULL ? strtoll(str,NULL,0) : 1024*1024;
            if (sz == 0) {
                fprintf(stderr, "Invalid value for %s: \"%s\"\n", env_var, str);
                abort();
            }
        }
        return sz;
    }

    typedef void *x10rt_cuda_kptr;

    // State machine
    template<class T> struct x10rt_cuda_base_op {
        bool begun;
        x10rt_msg_params p;
        T *next;
        x10rt_cuda_base_op (const x10rt_msg_params &p_)
          : begun(false), p(p_), next(NULL)
        { }
        virtual ~x10rt_cuda_base_op (void) { }
        virtual bool is_kernel() { return false; }
        virtual bool is_put() { return false; }
        virtual bool is_get() { return false; }
        virtual bool is_copy() { return false; }
    };

    #define CUDA_PARAM_SZ 80
    struct x10rt_cuda_kernel : x10rt_cuda_base_op<x10rt_cuda_kernel> {
        size_t blocks;
        size_t threads;
        size_t shm;
        size_t argc;
        char param_data[CUDA_PARAM_SZ];
        char *argv;
        size_t cmemc;
        char *cmemv;
        x10rt_cuda_kernel (x10rt_msg_params &p_)
          : x10rt_cuda_base_op<x10rt_cuda_kernel>(p_), blocks(0), threads(0), shm(0),
            argc(CUDA_PARAM_SZ), argv(param_data), cmemc(0), cmemv(0)
        { }
        virtual bool is_kernel() { return true; }
    };

    struct x10rt_cuda_copy : x10rt_cuda_base_op<x10rt_cuda_copy> {
        void *dst; 
        void *src; // 1 of {dst,src} known at start, the other discovered via callback
        size_t len; // known at start
        size_t started;
        size_t finished; // when equal to len, call on_comp and clean up
        x10rt_cuda_copy (x10rt_msg_params &p_, void *dst_, void *src_, size_t len_)
            : x10rt_cuda_base_op<x10rt_cuda_copy>(p_),
              dst(dst_), src(src_), len(len_), started(0), finished(0) { }
        virtual bool is_copy() { return true; }
    };

    struct x10rt_cuda_put : x10rt_cuda_copy {
        x10rt_cuda_put (x10rt_msg_params &p_, void *src_, size_t len_)
            : x10rt_cuda_copy(p_, NULL, src_, len_) { }
        virtual bool is_put() { return true; }
    };
    struct x10rt_cuda_get : x10rt_cuda_copy {
        x10rt_cuda_get (x10rt_msg_params &p_, void *dst_, size_t len_)
            : x10rt_cuda_copy(p_, dst_, NULL, len_) { }
        virtual bool is_get() { return true; }
    };

    template<class T> struct op_queue {
        bool initialized;
        CUstream stream;
        int size; // number of ops held here
        T *fifo_b, *fifo_e; // begin/end of fifo
        T *current; // op currently associated with the stream
        op_queue ()
          : initialized(false), size(0), fifo_b(NULL), fifo_e(NULL), current(NULL)
        { }
        ~op_queue ()
        {
            assert(!initialized);
        }
        void init (void)
        {
            assert(!initialized);
            CU_SAFE(cuStreamCreate(&stream,0));
            initialized = true;
        }
        void shutdown (void)
        {
            assert(initialized);
            CU_SAFE(cuStreamDestroy(stream));
            initialized = false;
        }

        void push_back (T *op)
        {
            if (fifo_e == NULL) {
                fifo_b = op;
                fifo_e = op;
            } else {
                fifo_e->next = op;
                fifo_e = op;
            }
            size++;
        }

        // pop from front
        T *pop_op (void)
        {
            T *op = fifo_b;
            if (op==NULL) return op;
            fifo_b = op->next;
            // case for empty queue
            if (fifo_b == NULL) fifo_e = NULL;
            size--;
            return op;
        }

    };

    struct x10rt_functions {
        union {
            struct {
                x10rt_cuda_pre *pre;
                CUfunction kernel;
                CUmodule module;
                x10rt_cuda_post *post;
            } kernel_cbs;
            struct {
                x10rt_finder *hh;
                x10rt_notifier *ch;
            } copy_cbs;
        };
    };

    void ensure_initialized (void)
    {
        // only do once per process
        static int done = 0;
        if (!done) {
            CU_SAFE(cuInit(0));
            done = 1;
        }
    }

    bool stream_ready (CUstream s)
    {
        CUresult r = cuStreamQuery(s);
        if (r==CUDA_ERROR_NOT_READY) return false;
        CU_SAFE(r);
        return true;
    }

    int round_up (int x, int y) { return (x + (y-1)) / y * y; }

}

struct x10rt_cuda_ctx {
    CUdevice hw;
    CUcontext ctx;
    void *pinned_mem1;
    void *pinned_mem2;
    void *front;
    void *back;
    size_t commit;
    op_queue<x10rt_cuda_kernel> kernel_q;
    op_queue<x10rt_cuda_copy> dma_q;
    Table<x10rt_functions> cbs;
    
    x10rt_cuda_ctx (unsigned device) {
        CU_SAFE(cuDeviceGet(&hw, device));
        /* other options are SPIN and AUTO. */
        /* TODO: export this choice with env var */
        CU_SAFE(cuCtxCreate(&ctx, CU_CTX_SCHED_AUTO, hw));
        CU_SAFE(cuMemAllocHost(&pinned_mem1, dma_slice_sz()));
        CU_SAFE(cuMemAllocHost(&pinned_mem2, dma_slice_sz()));
        front = pinned_mem1;
        back = pinned_mem2;
        kernel_q.init();
        dma_q.init();
        CU_SAFE(cuCtxPopCurrent(NULL));
    }
    ~x10rt_cuda_ctx (void) {
        CU_SAFE(cuMemFreeHost(pinned_mem1));
        CU_SAFE(cuMemFreeHost(pinned_mem2));
        kernel_q.shutdown();
        dma_q.shutdown();
        CU_SAFE(cuCtxPopCurrent(NULL));
        CU_SAFE(cuCtxDestroy(ctx));
    }

    void swapBuffers (void) { void *tmp = front; front = back; back = tmp; }
};


#endif


unsigned x10rt_cuda_ndevs (void)
{
#ifdef ENABLE_CUDA
    int available_devs;
    ensure_initialized();
    CU_SAFE(cuDeviceGetCount(&available_devs));
    assert(available_devs > 0);
    return available_devs;
#else
    return 0;
#endif
}


x10rt_cuda_ctx *x10rt_cuda_init (unsigned device)
{
#ifdef ENABLE_CUDA
    assert(device<x10rt_cuda_ndevs());
    pthread_mutex_init(&big_lock_of_doom, NULL);
    return new (safe_malloc<x10rt_cuda_ctx>()) x10rt_cuda_ctx(device);
#else
    // x10rt_ndevs would have returned 0 so x10rt_cuda_init should never have been called
    (void) device;
    abort();
    return NULL;
#endif
}


void x10rt_cuda_finalize (x10rt_cuda_ctx *ctx)
{
#ifdef ENABLE_CUDA
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));

    int n = 0; //x10rt_cuda_outstanding_ops(ctx);
    if (n > 0) {
        fprintf(stderr, "Shutdown warning: %d outstanding operation(s)!\n", n);
    }
    ctx->~x10rt_cuda_ctx();
    free(ctx);
    pthread_mutex_destroy(&big_lock_of_doom);
#else
    (void) ctx;
    abort();
#endif
}


void x10rt_cuda_register_msg_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                       x10rt_cuda_pre *pre, x10rt_cuda_post *post,
                                       const char *cubin, const char *kernel_name)
{
#ifdef ENABLE_CUDA

    CU_SAFE(cuCtxPushCurrent(ctx->ctx));

    CUmodule mod;
    CUresult r = cuModuleLoad(&mod, cubin);
    if (r==CUDA_ERROR_FILE_NOT_FOUND) {
        fprintf(stderr, "Couldn't find cubin: \"%s\"\n", cubin);
        abort();
    }
    CU_SAFE(r);

    CUfunction kernel;
    r = cuModuleGetFunction(&kernel, mod, kernel_name);
    if (r==CUDA_ERROR_NOT_FOUND) {
        fprintf(stderr, "Couldn't find kernel \"%s\" in \"%s\".\n", kernel_name, cubin);
        abort();
    }
    CU_SAFE(r);

    //TODO: re-use the same CUmodule

    CU_SAFE(cuCtxPopCurrent(NULL));

    x10rt_functions fs;
    fs.kernel_cbs.pre = pre;
    fs.kernel_cbs.kernel = kernel;
    fs.kernel_cbs.module = mod;
    fs.kernel_cbs.post = post;
    ctx->cbs.reg(msg_type,fs);
#else
    (void) ctx; (void) msg_type; (void) pre; (void) post; (void) cubin; (void) kernel_name;
#endif
}

void x10rt_cuda_register_get_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                       x10rt_finder *cb1, x10rt_notifier *cb2)
{
#ifdef ENABLE_CUDA
    x10rt_functions fs;
    fs.copy_cbs.hh = cb1;
    fs.copy_cbs.ch = cb2;
    ctx->cbs.reg(msg_type,fs);
#else
    (void) ctx; (void) msg_type; (void) cb1; (void) cb2;
#endif
}

void x10rt_cuda_register_put_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                       x10rt_finder *cb1, x10rt_notifier *cb2)

{
#ifdef ENABLE_CUDA
    x10rt_functions fs;
    fs.copy_cbs.hh = cb1;
    fs.copy_cbs.ch = cb2;
    ctx->cbs.reg(msg_type,fs);
#else
    (void) ctx; (void) msg_type; (void) cb1; (void) cb2;
#endif
}


void x10rt_cuda_registration_complete (x10rt_cuda_ctx *ctx)

{
#ifdef ENABLE_CUDA
    (void) ctx;
#else
    (void) ctx;
#endif
}

void *x10rt_cuda_device_alloc (x10rt_cuda_ctx *ctx,
                               size_t len)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));
    CUdeviceptr ptr;
    ctx->commit += len;
    //fprintf(stderr,"CUDA committed memory: %llu bytes\n", (unsigned long long)ctx->commit);
    CU_SAFE(cuMemAlloc(&ptr, len));
    CU_SAFE(cuCtxPopCurrent(NULL));
    pthread_mutex_unlock(&big_lock_of_doom);
    return (void*)ptr;
#else
    (void) ctx; (void) len;
    abort();
    return NULL;
#endif
}


void x10rt_cuda_device_free (x10rt_cuda_ctx *ctx,
                             void *ptr)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));
    CU_SAFE(cuMemFree((CUdeviceptr)(size_t)ptr));
    CU_SAFE(cuCtxPopCurrent(NULL));
    pthread_mutex_unlock(&big_lock_of_doom);
#else
    (void) ctx; (void) ptr;
    abort();
#endif
}


#ifdef ENABLE_CUDA
void *do_buffer_finder (x10rt_cuda_ctx *ctx, x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
    x10rt_msg_type type = p->type;
    x10rt_finder *hh = ctx->cbs[type].copy_cbs.hh;
    DEBUG("probe: finder callback begins\n");
    void *remote = hh(p, len); /****CALLBACK****/
    DEBUG("probe: finder callback ends\n");
    if (remote==NULL) {
        x10rt_notifier *ch = ctx->cbs[type].copy_cbs.ch;
        DEBUG("probe: finder callback returned NULL, running notifier\n");
        ch(p, len); /****CALLBACK****/
        DEBUG("probe: notifier callback ends\n");
    }
    return remote;
}
#endif

void x10rt_cuda_send_get (x10rt_cuda_ctx *ctx, x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);

    if (ctx->cbs.arrc <= p->type) {
        fprintf(stderr,"X10RT: Get %llu is invalid.\n", (unsigned long long)p->type);
        abort();
    }
    if (ctx->cbs[p->type].copy_cbs.hh == NULL) {
        fprintf(stderr,"X10RT: Get %llu has no 'hh' registered.\n", (unsigned long long)p->type);
        abort();
    }
    if (ctx->cbs[p->type].copy_cbs.ch == NULL) {
        fprintf(stderr,"X10RT: Get %llu has no 'ch' registered.\n", (unsigned long long)p->type);
        abort();
    }

    void *remote = do_buffer_finder(ctx, p, buf, len);

    if (remote) {
        x10rt_cuda_get *op = new (safe_malloc<x10rt_cuda_get>()) x10rt_cuda_get(*p,buf,len);
        op->src = remote;
        ctx->dma_q.push_back(op);
        pthread_mutex_unlock(&big_lock_of_doom);

        x10rt_cuda_probe(ctx);
    }

#else
    (void) ctx; (void) p; (void) buf; (void) len;
    abort();
#endif
}

void x10rt_cuda_send_put (x10rt_cuda_ctx *ctx, x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);

    if (ctx->cbs.arrc <= p->type) {
        fprintf(stderr,"X10RT: Put %llu is invalid.\n", (unsigned long long)p->type);
        abort();
    }
    if (ctx->cbs[p->type].copy_cbs.hh == NULL) {
        fprintf(stderr,"X10RT: Put %llu has no 'hh' registered.\n", (unsigned long long)p->type);
        abort();
    }
    if (ctx->cbs[p->type].copy_cbs.ch == NULL) {
        fprintf(stderr,"X10RT: Put %llu has no 'ch' registered.\n", (unsigned long long)p->type);
        abort();
    }

    void *remote = do_buffer_finder(ctx, p, buf, len);

    if (remote) {
        x10rt_cuda_put *op = new (safe_malloc<x10rt_cuda_put>()) x10rt_cuda_put(*p,buf,len);
        op->dst = remote;
        ctx->dma_q.push_back(op);
        pthread_mutex_unlock(&big_lock_of_doom);

        x10rt_cuda_probe(ctx);
    }

#else
    (void) ctx; (void) p; (void) buf; (void) len;
    abort();
#endif
}


void x10rt_cuda_send_msg (x10rt_cuda_ctx *ctx, x10rt_msg_params *p)
{
#ifdef ENABLE_CUDA

    if (ctx->cbs.arrc <= p->type) {
        fprintf(stderr,"X10RT: async %lu is invalid.\n", (unsigned long)p->type);
        abort();
    }
    if (ctx->cbs[p->type].kernel_cbs.kernel == NULL) {
        fprintf(stderr,"X10RT: async %lu is not a CUDA kernel.\n",(unsigned long)p->type);

        abort();
    }
    if (ctx->cbs[p->type].kernel_cbs.pre == NULL) {
        fprintf(stderr,"X10RT: CUDA Kernel %lu has no 'pre' registered.\n", (unsigned long)p->type);
        abort();
    }
    if (ctx->cbs[p->type].kernel_cbs.post == NULL) {
        fprintf(stderr,"X10RT: CUDA Kernel %lu has no 'post' registered.\n",(unsigned long)p->type);
        abort();
    }

    x10rt_cuda_kernel *op = new (safe_malloc<x10rt_cuda_kernel>()) x10rt_cuda_kernel(*p);

    x10rt_cuda_pre *pre = ctx->cbs[p->type].kernel_cbs.pre;
    DEBUG("x10rt_cuda_send_msg: pre callback begins\n");
    pre(p, &op->blocks, &op->threads, &op->shm,
        &op->argc, &op->argv, &op->cmemc, &op->cmemv); /****CALLBACK****/
    DEBUG("x10rt_cuda_send_msg: pre callback ends\n");

    pthread_mutex_lock(&big_lock_of_doom);
    ctx->kernel_q.push_back(op);
    pthread_mutex_unlock(&big_lock_of_doom);

    x10rt_cuda_probe(ctx);

#else
    (void) ctx; (void) p;
    abort();
#endif
}



void x10rt_cuda_blocks_threads (x10rt_cuda_ctx *ctx, x10rt_msg_type type, int dyn_shm,
                                int *blocks, int *threads, const int *cfg)
{
#ifdef ENABLE_CUDA
    if (ctx->cbs.arrc <= type) {
        fprintf(stderr,"X10RT: async %lu is invalid.\n", (unsigned long)type);
        abort();
    }
    if (ctx->cbs[type].kernel_cbs.kernel == NULL) {
        fprintf(stderr,"X10RT: async %lu is not a CUDA kernel.\n",(unsigned long)type);
        abort();
    }
    CUfunction k = ctx->cbs[type].kernel_cbs.kernel;

    pthread_mutex_lock(&big_lock_of_doom);
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));

    int mps, max_shm;
    CU_SAFE(cuDeviceGetAttribute(&mps, CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, ctx->hw));
    CU_SAFE(cuDeviceGetAttribute(&max_shm,CU_DEVICE_ATTRIBUTE_MAX_SHARED_MEMORY_PER_BLOCK,ctx->hw));

    CUdevprop prop;
    CU_SAFE(cuDeviceGetProperties(&prop, ctx->hw));
    int max_regs = prop.regsPerBlock;

    int major, minor;
    CU_SAFE(cuDeviceComputeCapability(&major, &minor, ctx->hw));


    int static_shm, regs;
    CU_SAFE(cuFuncGetAttribute(&static_shm, CU_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES, k));
    CU_SAFE(cuFuncGetAttribute(&regs, CU_FUNC_ATTRIBUTE_NUM_REGS, k));

    CU_SAFE(cuCtxPopCurrent(NULL));
    pthread_mutex_unlock(&big_lock_of_doom);

    // round up to 512 bytes (the granularity of shm allocation)
    int shm = round_up(dyn_shm + static_shm, 512);

    int alloc_size = (minor>=2) ? 512 : 256;
    int max_threads = (minor>=2) ? 1024 : 768;

    while (*cfg) {
        int b = *(cfg++);
        int t = *(cfg++);
        if (b*shm > max_shm) continue;
        if (t*b > max_threads) continue;
        int block_regs = round_up(regs*round_up(t,64), alloc_size);
        if (b*block_regs > max_regs) continue;
        *blocks = b * mps;
        *threads = t;
        return;
    }

    *blocks = 0;
    *threads = 0;

#else
    (void)ctx; (void)type; (void)dyn_shm; (void)blocks; (void)threads; (void)cfg;
    abort();
#endif
}


void x10rt_cuda_probe (x10rt_cuda_ctx *ctx)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));

    // spool kernels
    if (stream_ready(ctx->kernel_q.stream)) {
        if (ctx->kernel_q.current == NULL) {
            x10rt_cuda_kernel *kop = ctx->kernel_q.pop_op();
            if (kop != NULL) {
                assert(kop->is_kernel());
                assert(!kop->begun);
                DEBUG("probe: kernel invoke\n");
                x10rt_msg_type type = kop->p.type;
                CUfunction k = ctx->cbs[type].kernel_cbs.kernel;
                // y and z params we leave as 1, as threads can vary from 1 to 512
                CU_SAFE(cuFuncSetBlockShape(k, kop->threads, 1, 1));
                //fprintf(stderr,"%p<<<%d,%d,%d>>> argc: %d  argv: %p\n", (void*)k, kop->blocks, kop->threads, kop->shm, kop->argc, *(void**)kop->argv);
                CU_SAFE(cuParamSetv(k, 0, &kop->argv[0], kop->argc));
                CU_SAFE(cuParamSetSize(k, kop->argc));
                CU_SAFE(cuFuncSetSharedSize(k, kop->shm));
                CU_SAFE(cuLaunchGridAsync(k, kop->blocks, 1, ctx->kernel_q.stream));
                kop->begun = true;
                assert(ctx->kernel_q.current == NULL);
                ctx->kernel_q.current = kop;
            }
        } else {
            x10rt_cuda_kernel *kop = ctx->kernel_q.current;
            ctx->kernel_q.current = NULL;
            assert(kop->is_kernel());
            assert(kop->begun);
            DEBUG("probe: kernel complete\n");
            x10rt_msg_type type = kop->p.type;
            x10rt_cuda_post *fptr = ctx->cbs[type].kernel_cbs.post;
            DEBUG("probe: post callback begins\n");
            CU_SAFE(cuCtxPopCurrent(NULL));
            pthread_mutex_unlock(&big_lock_of_doom);
            fptr(&kop->p, &kop->argv); /****CALLBACK****/
            pthread_mutex_lock(&big_lock_of_doom);
            CU_SAFE(cuCtxPushCurrent(ctx->ctx));
            DEBUG("probe: post callback ends\n");
            kop->~x10rt_cuda_kernel();
            free(kop);
        }
    }

    // spool DMAs
    if (stream_ready(ctx->dma_q.stream)) {

        x10rt_cuda_copy *cop = ctx->dma_q.current;

        if (cop == NULL) {
            cop = ctx->dma_q.pop_op();
            if (cop==NULL) goto landingzone;
            assert(!cop->begun);
            cop->begun = true;
            ctx->dma_q.current = cop;
        }

        assert(cop->begun);
        char *&src = reinterpret_cast<char*&>(cop->src);
        char *&dst = reinterpret_cast<char*&>(cop->dst);
        size_t len = cop->len;
        size_t &started = cop->started;
        size_t &finished = cop->finished;
        assert(started>finished || started==0);
        assert(finished<len);

        size_t dma_sz = len - started;
        dma_sz = dma_sz > dma_slice_sz() ? dma_slice_sz() : dma_sz;
        assert(dma_sz <= len);
        assert(started+dma_sz <= len);
        assert(dma_sz <= dma_slice_sz());

        if (cop->is_get()) {
            DEBUG("get(%p,%p,%llu,%llu,%llu)\n", src, dst, (unsigned long long)len, (unsigned long long)started, (unsigned long long)finished);
            // front buffer handled last tick... available for re-use
            if (started > 0) ctx->swapBuffers();
            // invoke async copy into back buffer
            if (dma_sz > 0) {
                CU_SAFE(cuMemcpyDtoHAsync(ctx->back,
                                          (CUdeviceptr)(size_t)(src+started),
                                          dma_sz,
                                          ctx->dma_q.stream));
            }
            if (started > 0) {
                DEBUG("memcpy(%p,%p,%d)\n", dst+finished, ctx->front, started-finished);
                ::memcpy(dst+finished, ctx->front, started-finished);
                finished = started;
            }
            started += dma_sz;
        } else if (cop->is_put()) {
            DEBUG("put(%p,%p,%llu,%llu,%llu)\n", src, dst, (unsigned long long)len, (unsigned long long)started, (unsigned long long)finished);
            // back buffer has now been copied to device... available for re-use
            if (started > 0) {
                ctx->swapBuffers();
                CU_SAFE(cuMemcpyHtoDAsync((CUdeviceptr)(size_t)(dst+finished),
                                          ctx->back,
                                          started-finished,
                                          ctx->dma_q.stream));
                finished = started;
            }
            if (dma_sz > 0) {
                DEBUG("memcpy(%p,%p,%d)\n", ctx->front, src+started, dma_sz);
                ::memcpy(ctx->front, src+started, dma_sz);
                started += dma_sz;
            }
        } else {
            abort();
        }

        if (finished==len) {
            ctx->dma_q.current = NULL;
            x10rt_msg_type type = cop->p.type;
            x10rt_notifier *ch = ctx->cbs[type].copy_cbs.ch;
            CU_SAFE(cuCtxPopCurrent(NULL));
            pthread_mutex_unlock(&big_lock_of_doom);
            ch(&cop->p, len); /****CALLBACK****/
            cop->~x10rt_cuda_copy();
            free(cop);
            return;
        }

    }

    landingzone:

    CU_SAFE(cuCtxPopCurrent(NULL));
    pthread_mutex_unlock(&big_lock_of_doom);

#else
    (void) ctx;
    abort();
#endif
}

// vim: shiftwidth=4:tabstop=4:expandtab:textwidth=100
