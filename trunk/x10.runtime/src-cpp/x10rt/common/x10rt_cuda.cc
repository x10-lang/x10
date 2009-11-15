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

    static inline void DEBUG(const char *fmt, ...) {
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


    static size_t dma_slice_sz (void) {
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
    struct x10rt_cuda_base_op {
        bool begun;
        x10rt_msg_params p;
        x10rt_cuda_base_op *next;
        x10rt_cuda_base_op (const x10rt_msg_params &p_)
          : begun(false), p(p_), next(NULL)
        { }
        virtual ~x10rt_cuda_base_op (void) { }
        virtual bool is_kernel() { return false; }
        virtual bool is_put() { return false; }
        virtual bool is_get() { return false; }
        virtual bool is_copy() { return false; }
    };

    struct x10rt_cuda_kernel : x10rt_cuda_base_op {
        int blocks;
        int threads;
        int shm;
        void *arg;
        x10rt_cuda_kernel (x10rt_msg_params &p_)
          : x10rt_cuda_base_op(p_), blocks(0), threads(0), shm(0), arg(0)
        { }
        virtual bool is_kernel() { return true; }
    };

    struct x10rt_cuda_copy : x10rt_cuda_base_op {
        void *dst; 
        void *src; // 1 of {dst,src} known at start, the other discovered via callback
        size_t len; // known at start
        size_t started;
        size_t finished; // when equal to len, call on_comp and clean up
        x10rt_cuda_copy (x10rt_msg_params &p_, void *dst_, void *src_, size_t len_)
            : x10rt_cuda_base_op(p_), dst(dst_), src(src_), len(len_), started(0), finished(0) { }
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

    struct op_queue {
        bool initialized;
        CUstream stream;
        int size; // number of ops held here
        x10rt_cuda_base_op *fifo_b, *fifo_e; // begin/end of fifo
        op_queue ()
          : initialized(false), size(0), fifo_b(NULL), fifo_e(NULL)
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

        // FIXME: this will race, use lock or CAS or something
        void push_op (x10rt_cuda_base_op *op)
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

        // FIXME: this will race, use lock or CAS or something
        x10rt_cuda_base_op *pop_op (void)
        {
            x10rt_cuda_base_op *op = fifo_b;
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
                x10rt_cuda_post *post;
            } kernel_cbs;
            struct {
                x10rt_finder *hh;
                x10rt_notifier *ch;
            } copy_cbs;
        };
    };

    static void ensure_initialized (void)
    {
        static int done = 0;
        if (!done) {
            CU_SAFE(cuInit(0));
            done = 1;
        }
    }

}

struct x10rt_cuda_ctx {
    CUdevice hw;
    CUcontext ctx;
    void *pinned_mem;
    op_queue kernel_q;
    op_queue dma_q;
    Table<x10rt_functions> cbs;
    
    x10rt_cuda_ctx (unsigned device) {
        CU_SAFE(cuDeviceGet(&hw, device));
        /* other options are SPIN and AUTO. */
        /* TODO: export this choice with env var */
        CU_SAFE(cuCtxCreate(&ctx, CU_CTX_SCHED_AUTO, hw));
        CU_SAFE(cuMemAllocHost(&pinned_mem, dma_slice_sz()));
        kernel_q.init();
        dma_q.init();
        CU_SAFE(cuCtxPopCurrent(NULL));
    }
    ~x10rt_cuda_ctx (void) {
        CU_SAFE(cuMemFreeHost(pinned_mem));
        kernel_q.shutdown();
        dma_q.shutdown();
        CU_SAFE(cuCtxPopCurrent(NULL));
        CU_SAFE(cuCtxDestroy(ctx));
    }

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
    fs.kernel_cbs.post = post;
    ctx->cbs.reg(msg_type,fs);
#else
    (void) ctx; (void) msg_type; (void) pre_cb; (void) post_cb; (void) cubin; (void) kernel_name;
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
    CU_SAFE(cuMemFree((CUdeviceptr)ptr));
    CU_SAFE(cuCtxPopCurrent(NULL));
    pthread_mutex_unlock(&big_lock_of_doom);
#else
    (void) ctx; (void) ptr;
    abort();
#endif
}


void x10rt_cuda_send_get (x10rt_cuda_ctx *ctx, x10rt_msg_params &p, void *buf, x10rt_copy_sz len)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);

    if (ctx->cbs.arrc <= p.type) {
        fprintf(stderr,"X10RT: Get %llu is invalid.\n", (unsigned long long)p.type);
        abort();
    }
    if (ctx->cbs[p.type].copy_cbs.hh == NULL) {
        fprintf(stderr,"X10RT: Get %llu has no 'hh' registered.\n", (unsigned long long)p.type);
        abort();
    }
    if (ctx->cbs[p.type].copy_cbs.ch == NULL) {
        fprintf(stderr,"X10RT: Get %llu has no 'ch' registered.\n", (unsigned long long)p.type);
        abort();
    }

    x10rt_cuda_get *op = new (safe_malloc<x10rt_cuda_get>()) x10rt_cuda_get(p,buf,len);
    ctx->dma_q.push_op(op);
    pthread_mutex_unlock(&big_lock_of_doom);

    x10rt_cuda_probe(ctx);

#else
    (void) ctx; (void) p; (void) buf; (void) len;
    abort();
#endif
}


void x10rt_cuda_send_put (x10rt_cuda_ctx *ctx, x10rt_msg_params &p, void *buf, x10rt_copy_sz len)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);

    if (ctx->cbs.arrc <= p.type) {
        fprintf(stderr,"X10RT: Put %llu is invalid.\n", (unsigned long long)p.type);
        abort();
    }
    if (ctx->cbs[p.type].copy_cbs.hh == NULL) {
        fprintf(stderr,"X10RT: Put %llu has no 'hh' registered.\n", (unsigned long long)p.type);
        abort();
    }
    if (ctx->cbs[p.type].copy_cbs.ch == NULL) {
        fprintf(stderr,"X10RT: Put %llu has no 'ch' registered.\n", (unsigned long long)p.type);
        abort();
    }

    x10rt_cuda_put *op = new (safe_malloc<x10rt_cuda_put>()) x10rt_cuda_put(p,buf,len);
    ctx->dma_q.push_op(op);
    pthread_mutex_unlock(&big_lock_of_doom);

    x10rt_cuda_probe(ctx);

#else
    (void) ctx; (void) p; (void) buf; (void) len;
    abort();
#endif
}


void x10rt_cuda_send_msg (x10rt_cuda_ctx *ctx, x10rt_msg_params &p)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);

    if (ctx->cbs.arrc <= p.type) {
        fprintf(stderr,"X10RT: Kernel %llu is invalid.\n", (unsigned long long)p.type);
        abort();
    }
    if (ctx->cbs[p.type].kernel_cbs.pre == NULL) {
        fprintf(stderr,"X10RT: Kernel %llu has no 'pre' registered.\n", (unsigned long long)p.type);
        abort();
    }
    if (ctx->cbs[p.type].kernel_cbs.kernel == NULL) {
        fprintf(stderr,"X10RT: Kernel %llu has no kernel registered.\n",(unsigned long long)p.type);

        abort();
    }
    if (ctx->cbs[p.type].kernel_cbs.post == NULL) {
        fprintf(stderr,"X10RT: Kernel %llu has no 'post' registered.\n",(unsigned long long)p.type);
        abort();
    }

    x10rt_cuda_kernel *op = new (safe_malloc<x10rt_cuda_kernel>()) x10rt_cuda_kernel(p);
    ctx->kernel_q.push_op(op);
    pthread_mutex_unlock(&big_lock_of_doom);

    x10rt_cuda_probe(ctx);

#else
    (void) ctx; (void) p;
    abort();
#endif
}


#ifdef ENABLE_CUDA
static bool stream_ready (CUstream s)
{
    CUresult r = cuStreamQuery(s);
    if (r==CUDA_ERROR_NOT_READY) return false;
    CU_SAFE(r);
    return true;
}
#endif


void x10rt_cuda_probe (x10rt_cuda_ctx *ctx)
{
#ifdef ENABLE_CUDA
    pthread_mutex_lock(&big_lock_of_doom);
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));

    // spool kernels
    if (stream_ready(ctx->kernel_q.stream)) {
        x10rt_cuda_base_op *op = ctx->kernel_q.pop_op();
        assert(op==NULL || op->is_kernel());
        x10rt_cuda_kernel *kop = static_cast<x10rt_cuda_kernel*>(op);
        if (kop != NULL && kop->begun) {
            DEBUG("probe: kernel complete\n");
            x10rt_msg_type type = kop->p.type;
            x10rt_cuda_post *fptr = ctx->cbs[type].kernel_cbs.post;
            CU_SAFE(cuCtxPopCurrent(NULL));
            pthread_mutex_unlock(&big_lock_of_doom);
            fptr(kop->p, kop->arg); /****CALLBACK****/
            pthread_mutex_lock(&big_lock_of_doom);
            CU_SAFE(cuCtxPushCurrent(ctx->ctx));
            op->~x10rt_cuda_base_op();
            free(op);
            op = ctx->kernel_q.pop_op(); // get another one
            assert(op==NULL || op->is_kernel());
            kop = static_cast<x10rt_cuda_kernel*>(op);
        }
        if (kop != NULL) {
            DEBUG("probe: kernel invoke\n");
            x10rt_msg_type type = kop->p.type;
            x10rt_cuda_pre *pre = ctx->cbs[type].kernel_cbs.pre;
            size_t blocks=1, threads=1, shm = 0;
            CU_SAFE(cuCtxPopCurrent(NULL));
            pthread_mutex_unlock(&big_lock_of_doom);
            kop->arg = pre(kop->p, blocks, threads, shm); /****CALLBACK****/
            pthread_mutex_lock(&big_lock_of_doom);
            CU_SAFE(cuCtxPushCurrent(ctx->ctx));
            CUfunction k = ctx->cbs[type].kernel_cbs.kernel;
            // y and z params we leave as 1, as threads can vary from 1 to 512
            CU_SAFE(cuFuncSetBlockShape(k, threads, 1, 1));
            CU_SAFE(cuParamSetv(k, 0, &kop->arg, sizeof(kop->arg)));
            CU_SAFE(cuParamSetSize(k, sizeof(kop->arg)));
            CU_SAFE(cuFuncSetSharedSize(k, shm));
            CU_SAFE(cuLaunchGridAsync(k, blocks, 1, ctx->kernel_q.stream));
            op->begun = true;
            ctx->kernel_q.push_op(op);
        }
    }

    // spool DMAs
    if (stream_ready(ctx->dma_q.stream)) {

        x10rt_cuda_base_op *op = ctx->dma_q.pop_op();

        if (op != NULL && op->begun) {
            DEBUG("probe: do the final work for a previous slice\n");
            assert(op->is_copy());
            x10rt_cuda_copy *cop = static_cast<x10rt_cuda_copy *>(op);
            // We are finishing off a previous copy.
            char *dst = reinterpret_cast<char*>(cop->dst);
            size_t len = cop->len;
            size_t &finished = cop->finished;
            size_t dma_sz = len - finished;
            dma_sz = dma_sz > dma_slice_sz() ? dma_slice_sz() : dma_sz;
            assert(dma_sz <= len);
            assert(dma_sz <= len-finished);
            assert(dma_sz <= dma_slice_sz());
            assert(dma_sz != 0);
            if (cop->is_get()) {
                // For get, we need to memcpy the previous slice into the user buffer.
                DEBUG("memcpy(%p,%p,%d)\n", dst+finished, ctx->pinned_mem, dma_sz);
                ::memcpy(dst+finished, ctx->pinned_mem, dma_sz);
            }
            finished += dma_sz;
            if (finished == len) {
                x10rt_msg_type type = op->p.type;
                x10rt_notifier *ch = ctx->cbs[type].copy_cbs.ch;
                CU_SAFE(cuCtxPopCurrent(NULL));
                pthread_mutex_unlock(&big_lock_of_doom);
                ch(cop->p, len); /****CALLBACK****/
                pthread_mutex_lock(&big_lock_of_doom);
                CU_SAFE(cuCtxPushCurrent(ctx->ctx));
                // Do something else in the same probe
                op->~x10rt_cuda_base_op();
                free(op);
                op = NULL;
            }
        }

        if (op==NULL)
            op = ctx->dma_q.pop_op();

        if (op != NULL) {
            DEBUG("probe: dma slice\n");
            bool first_time = !op->begun;
            op->begun = true;

            assert(op->is_copy());
            x10rt_cuda_copy *cop = static_cast<x10rt_cuda_copy *>(op);

            // Invoke an asynchronous copy...
            // For get, we need to request another slice.
            // For put, we need to memcpy the next slice and request the transfer.
            void *&dst = cop->dst;
            void *&src = cop->src;
            size_t len = cop->len;

            size_t &started = cop->started;
            size_t finished = cop->finished;

            size_t dma_sz = len - finished;
            dma_sz = dma_sz > dma_slice_sz() ? dma_slice_sz() : dma_sz;
            assert(dma_sz <= len);
            assert(dma_sz <= len-finished);
            assert(dma_sz <= dma_slice_sz());
            assert(dma_sz != 0);

            assert(op->is_get() || op->is_put());

            x10rt_msg_type type = op->p.type;
            x10rt_finder *hh = ctx->cbs[type].copy_cbs.hh;

            void *remote = NULL; // initialise only to avoid compiler warning
            if (first_time) {
                CU_SAFE(cuCtxPopCurrent(NULL));
                pthread_mutex_unlock(&big_lock_of_doom);
                remote = hh(cop->p, len); /****CALLBACK****/
                pthread_mutex_lock(&big_lock_of_doom);
                CU_SAFE(cuCtxPushCurrent(ctx->ctx));
                if (remote==NULL) {
                    x10rt_msg_type type = op->p.type;
                    x10rt_notifier *ch = ctx->cbs[type].copy_cbs.ch;
                    CU_SAFE(cuCtxPopCurrent(NULL));
                    pthread_mutex_unlock(&big_lock_of_doom);
                    ch(cop->p, len); /****CALLBACK****/
                    pthread_mutex_lock(&big_lock_of_doom);
                    CU_SAFE(cuCtxPushCurrent(ctx->ctx));
                    op->~x10rt_cuda_base_op();
                    free(op);
                    goto landingzone;
                }
            }

            if (op->is_get()) {
                src = first_time ? remote : src;
                CU_SAFE(cuMemcpyDtoHAsync(ctx->pinned_mem,
                                          (CUdeviceptr)(((char*)src)+started),
                                          dma_sz,
                                          ctx->dma_q.stream));
            } else if (op->is_put()) {
                dst = first_time ? remote : dst;
                memcpy(ctx->pinned_mem, ((char*)src)+started, dma_sz);
                CU_SAFE(cuMemcpyHtoDAsync((CUdeviceptr)(((char*)dst)+started),
                                          ctx->pinned_mem,
                                          dma_sz,
                                          ctx->dma_q.stream));
            }

            started += dma_sz;

            ctx->dma_q.push_op(op);
            
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
