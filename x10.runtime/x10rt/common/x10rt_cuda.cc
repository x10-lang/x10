/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifdef __CYGWIN__
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin
#endif

#include <cstdlib>
#include <cstdio>
#include <cassert>
#include <cstring>
#include <cstdarg>
#include <new>

#include <x10rt_types.h>
#include <x10rt_internal.h>
#include <x10rt_cuda.h>

#ifdef ENABLE_CUDA

#include <cuda.h> // Proprietary nvidia header

//#define TRACE 2

namespace {

    // TODO: fine grained synchronisation, lock free datastructures
    Lock big_lock_of_doom;

    inline void DEBUG(int level, const char *fmt, ...) {
        (void) fmt;
        va_list ap;
        va_start(ap, fmt);
        #ifdef TRACE
        if (level<=TRACE) vfprintf(stderr, fmt, ap);
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

            #if CUDA_VERSION >= 3000
            case CUDA_ERROR_NOT_MAPPED_AS_ARRAY:
                errstr = "CUDA_ERROR_NOT_MAPPED_AS_ARRAY"; break;
            case CUDA_ERROR_NOT_MAPPED_AS_POINTER:
                errstr = "CUDA_ERROR_NOT_MAPPED_AS_POINTER"; break;
            case CUDA_ERROR_ECC_UNCORRECTABLE:
                errstr = "CUDA_ERROR_ECC_UNCORRECTABLE"; break;
            #if CUDA_VERSION < 3020
            case CUDA_ERROR_POINTER_IS_64BIT:
                errstr = "CUDA_ERROR_POINTER_IS_64BIT"; break;
            case CUDA_ERROR_SIZE_IS_64BIT:
                errstr = "CUDA_ERROR_SIZE_IS_64BIT"; break;
            #endif
            #endif

            #if CUDA_VERSION >= 3010
            case CUDA_ERROR_UNSUPPORTED_LIMIT:
                errstr = "CUDA_ERROR_UNSUPPORTED_LIMIT"; break;
            case CUDA_ERROR_SHARED_OBJECT_SYMBOL_NOT_FOUND:
                errstr = "CUDA_ERROR_SHARED_OBJECT_SYMBOL_NOT_FOUND"; break;
            case CUDA_ERROR_SHARED_OBJECT_INIT_FAILED:
                errstr = "CUDA_ERROR_SHARED_OBJECT_INIT_FAILED"; break;
            #endif
        
            #if CUDA_VERSION >= 3020
            case CUDA_ERROR_OPERATING_SYSTEM:
                errstr = "CUDA_ERROR_OPERATING_SYSTEM"; break;
            #endif

            #if CUDA_VERSION >= 5050
            case CUDA_ERROR_PROFILER_DISABLED:
                errstr = "CUDA_ERROR_PROFILER_DISABLED"; break;
            case CUDA_ERROR_PROFILER_NOT_INITIALIZED:
                errstr = "CUDA_ERROR_PROFILER_NOT_INITIALIZED"; break;
            case CUDA_ERROR_PROFILER_ALREADY_STARTED:
                errstr = "CUDA_ERROR_PROFILER_ALREADY_STARTED"; break;
            case CUDA_ERROR_PROFILER_ALREADY_STOPPED:
                errstr = "CUDA_ERROR_PROFILER_ALREADY_STOPPED"; break;
            case CUDA_ERROR_CONTEXT_ALREADY_IN_USE:
                errstr = "CUDA_ERROR_CONTEXT_ALREADY_IN_USE"; break;
            case CUDA_ERROR_PEER_ACCESS_UNSUPPORTED:
                errstr = "CUDA_ERROR_PEER_ACCESS_UNSUPPORTED"; break;
            case CUDA_ERROR_PEER_ACCESS_ALREADY_ENABLED:
                errstr = "CUDA_ERROR_PEER_ACCESS_ALREADY_ENABLED"; break;
            case CUDA_ERROR_PEER_ACCESS_NOT_ENABLED:
                errstr = "CUDA_ERROR_PEER_ACCESS_NOT_ENABLED"; break;
            case CUDA_ERROR_PRIMARY_CONTEXT_ACTIVE:
                errstr = "CUDA_ERROR_PRIMARY_CONTEXT_ACTIVE"; break;
            case CUDA_ERROR_CONTEXT_IS_DESTROYED:
                errstr = "CUDA_ERROR_CONTEXT_IS_DESTROYED"; break;
            case CUDA_ERROR_ASSERT:
                errstr = "CUDA_ERROR_ASSERT"; break;
            case CUDA_ERROR_TOO_MANY_PEERS:
                errstr = "CUDA_ERROR_TOO_MANY_PEERS"; break;
            case CUDA_ERROR_HOST_MEMORY_ALREADY_REGISTERED:
                errstr = "CUDA_ERROR_HOST_MEMORY_ALREADY_REGISTERED"; break;
            case CUDA_ERROR_HOST_MEMORY_NOT_REGISTERED:
                errstr = "CUDA_ERROR_HOST_MEMORY_NOT_REGISTERED"; break;
            case CUDA_ERROR_NOT_PERMITTED:
                errstr = "CUDA_ERROR_NOT_PERMITTED"; break;
            case CUDA_ERROR_NOT_SUPPORTED:
                errstr = "CUDA_ERROR_NOT_SUPPORTED"; break;
            #endif

            #if CUDA_VERSION >= 6000
            case CUDA_ERROR_INVALID_PTX:
                errstr = "CUDA_ERROR_INVALID_PTX"; break;
            case CUDA_ERROR_ILLEGAL_ADDRESS:
                errstr = "CUDA_ERROR_ILLEGAL_ADDRESS"; break;
            case CUDA_ERROR_HARDWARE_STACK_ERROR:
                errstr = "CUDA_ERROR_HARDWARE_STACK_ERROR"; break;
            case CUDA_ERROR_ILLEGAL_INSTRUCTION:
                errstr = "CUDA_ERROR_ILLEGAL_INSTRUCTION"; break;
            case CUDA_ERROR_MISALIGNED_ADDRESS:
                errstr = "CUDA_ERROR_MISALIGNED_ADDRESS"; break;
            case CUDA_ERROR_INVALID_ADDRESS_SPACE:
                errstr = "CUDA_ERROR_INVALID_ADDRESS_SPACE"; break;
            case CUDA_ERROR_INVALID_PC:
                errstr = "CUDA_ERROR_INVALID_PC"; break;
            #endif

            #if CUDA_VERSION >= 6050
            case CUDA_ERROR_INVALID_GRAPHICS_CONTEXT:
                errstr = "CUDA_ERROR_INVALID_GRAPHICS_CONTEXT"; break;
            #endif
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
    template<class T> struct BaseOp : FifoElement<T> {
        bool begun;
        x10rt_msg_params p;
        BaseOp (const x10rt_msg_params &p_)
          : begun(false), p(p_)
        { }
        virtual ~BaseOp (void) { }
        virtual bool is_kernel() { return false; }
        virtual bool is_put() { return false; }
        virtual bool is_get() { return false; }
        virtual bool is_copy() { return false; }
    };

    #define CUDA_PARAM_SZ 256
    #define CUDA_CMEM_SZ (64*1024)
    struct BaseOpKernel : BaseOp<BaseOpKernel> {
        size_t blocks;
        size_t threads;
        size_t shm;
        size_t argc;
        char param_data[CUDA_PARAM_SZ];
        char *argv;
        char cmem_data[CUDA_CMEM_SZ]; // TODO: this may not be efficient, consider a freelist
        size_t cmemc;
        char *cmemv;
        BaseOpKernel (x10rt_msg_params &p_)
          : BaseOp<BaseOpKernel>(p_), blocks(0), threads(0), shm(0),
            argc(CUDA_PARAM_SZ), argv(param_data), cmemc(16384), cmemv(cmem_data)
        { }
        virtual bool is_kernel() { return true; }
    };

    struct BaseOpCopy : BaseOp<BaseOpCopy> {
        void *dst; 
        void *src; // 1 of {dst,src} known at start, the other discovered via callback
        size_t len; // known at start
        size_t started;
        size_t finished; // when equal to len, call on_comp and clean up
        BaseOpCopy (x10rt_msg_params &p_, void *dst_, void *src_, size_t len_)
            : BaseOp<BaseOpCopy>(p_),
              dst(dst_), src(src_), len(len_), started(0), finished(0) { }
        virtual bool is_copy() { return true; }
    };

    struct BaseOpPut : BaseOpCopy {
        BaseOpPut (x10rt_msg_params &p_, void *src_, size_t len_)
            : BaseOpCopy(p_, NULL, src_, len_) { }
        virtual bool is_put() { return true; }
    };
    struct BaseOpGet : BaseOpCopy {
        BaseOpGet (x10rt_msg_params &p_, void *dst_, size_t len_)
            : BaseOpCopy(p_, dst_, NULL, len_) { }
        virtual bool is_get() { return true; }
    };

    template<class T> struct FifoCuda : Fifo<T> {
        bool initialized;
        CUstream stream;
        FifoCuda ()
          : initialized(false)
        { }
        ~FifoCuda ()
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

    };

    struct x10rt_functions {
        union {
            struct {
                x10rt_cuda_pre *pre;
                CUfunction kernel;
                CUmodule module;
                CUdeviceptr cmem;
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
    FifoCuda<BaseOpKernel> kernel_q;
    FifoCuda<BaseOpCopy> dma_q;
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

#if CUDA_VERSION >= 3020
typedef size_t cuda_size_t;
#else
typedef unsigned int cuda_size_t;
#endif

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
#else
    (void) ctx;
    abort();
#endif
}


void x10rt_cuda_register_msg_receiver (x10rt_cuda_ctx *ctx, x10rt_msg_type msg_type,
                                       x10rt_cuda_pre *pre, x10rt_cuda_post *post,
                                       const char *cubin_, const char *kernel_name)
{
#ifdef ENABLE_CUDA

    CU_SAFE(cuCtxPushCurrent(ctx->ctx));

    int major, minor;
    CU_SAFE(cuDeviceComputeCapability(&major, &minor, ctx->hw));

    static size_t suffix_len = 20;
    char *cubin = safe_malloc<char>(strlen(cubin_)+suffix_len+1);
    sprintf(cubin,"%s_sm_%d%d.cubin",cubin_,major,minor);

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

    CUdeviceptr cmem = NULL;
    cuda_size_t cmem_sz;
    r = cuModuleGetGlobal(&cmem, &cmem_sz, mod, "__cmem");
    if (r==CUDA_ERROR_NOT_FOUND) {
        fprintf(stderr, "Couldn't find __cmem in \"%s\".\n", cubin);
        //abort();
    } else {
        CU_SAFE(r);
        assert(cmem_sz == CUDA_CMEM_SZ);
    }
    
    //TODO: re-use the same CUmodule

    CU_SAFE(cuCtxPopCurrent(NULL));

    x10rt_functions fs;
    fs.kernel_cbs.pre = pre;
    fs.kernel_cbs.kernel = kernel;
    fs.kernel_cbs.cmem = cmem;
    fs.kernel_cbs.module = mod;
    fs.kernel_cbs.post = post;
    ctx->cbs.reg(msg_type,fs);

    safe_free(cubin);
#else
    (void) ctx; (void) msg_type; (void) pre; (void) post; (void) cubin_; (void) kernel_name;
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
    big_lock_of_doom.acquire();
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));
    CUdeviceptr ptr;
    ctx->commit += len;
    CU_SAFE(cuMemAlloc(&ptr, len));
    DEBUG(1,"CUDA allocated memory: %llu bytes (%p)\n", (unsigned long long)len, ptr);
    DEBUG(2,"CUDA committed memory: %llu bytes\n", (unsigned long long)ctx->commit);
    CU_SAFE(cuCtxPopCurrent(NULL));
    big_lock_of_doom.release();
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
    big_lock_of_doom.acquire();
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));
    DEBUG(1,"CUDA free'd memory: %p\n", ptr);
    CU_SAFE(cuMemFree((CUdeviceptr)(size_t)ptr));
    CU_SAFE(cuCtxPopCurrent(NULL));
    big_lock_of_doom.release();
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
    DEBUG(2,"probe: finder callback begins\n");
    void *remote = hh(p, len); /****CALLBACK****/
    DEBUG(2,"probe: finder callback ends\n");
    if (remote==NULL) {
        x10rt_notifier *ch = ctx->cbs[type].copy_cbs.ch;
        DEBUG(2,"probe: finder callback returned NULL, running notifier\n");
        ch(p, len); /****CALLBACK****/
        DEBUG(2,"probe: notifier callback ends\n");
    }
    return remote;
}
#endif

void x10rt_cuda_send_get (x10rt_cuda_ctx *ctx, x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
#ifdef ENABLE_CUDA
    big_lock_of_doom.acquire();

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

    x10rt_msg_params p_ = *p;
    p_.msg = safe_malloc<unsigned char>(p->len);
    memcpy(p_.msg, p->msg, p->len);

    void *remote = do_buffer_finder(ctx, p, buf, len);

    if (remote) {
        BaseOpGet *op = new (safe_malloc<BaseOpGet>()) BaseOpGet(p_,buf,len);
        op->src = remote;
        ctx->dma_q.push_back(op);
        big_lock_of_doom.release();

        x10rt_cuda_probe(ctx);
    } else {
        big_lock_of_doom.release();
    }

#else
    (void) ctx; (void) p; (void) buf; (void) len;
    abort();
#endif
}

void x10rt_cuda_send_put (x10rt_cuda_ctx *ctx, x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
#ifdef ENABLE_CUDA
    big_lock_of_doom.acquire();

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

    x10rt_msg_params p_ = *p;
    p_.msg = safe_malloc<unsigned char>(p->len);
    memcpy(p_.msg, p->msg, p->len);

    void *remote = do_buffer_finder(ctx, p, buf, len);

    if (remote) {
        BaseOpPut *op = new (safe_malloc<BaseOpPut>()) BaseOpPut(p_,buf,len);
        op->dst = remote;
        ctx->dma_q.push_back(op);
        big_lock_of_doom.release();

        x10rt_cuda_probe(ctx);
    } else {
        big_lock_of_doom.release();
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

    x10rt_msg_params p_ = *p;
    p_.msg = safe_malloc<unsigned char>(p->len);
    memcpy(p_.msg, p->msg, p->len);

    BaseOpKernel *op = new (safe_malloc<BaseOpKernel>()) BaseOpKernel(p_);

    x10rt_cuda_pre *pre = ctx->cbs[p->type].kernel_cbs.pre;
    DEBUG(2,"x10rt_cuda_send_msg: pre callback begins\n");
    pre(p, &op->blocks, &op->threads, &op->shm,
        &op->argc, &op->argv, &op->cmemc, &op->cmemv); /****CALLBACK****/
    DEBUG(2,"x10rt_cuda_send_msg: pre callback ends\n");

    big_lock_of_doom.acquire();
    ctx->kernel_q.push_back(op);
    big_lock_of_doom.release();

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

    big_lock_of_doom.acquire();
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
    big_lock_of_doom.release();

    // round up to 512 bytes (the granularity of shm allocation)
    int shm = round_up(dyn_shm + static_shm, 512);

    int alloc_size = (major>=2) ? 512 : 256;
    int max_threads = (major>=2) ? 1024 : 512;

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

// returns true if something is active in the GPU, or false if the GPU is idle
bool x10rt_cuda_probe (x10rt_cuda_ctx *ctx)
{
#ifdef ENABLE_CUDA
    bool isAnythingActive = false;
    big_lock_of_doom.acquire();
    CU_SAFE(cuCtxPushCurrent(ctx->ctx));

    // spool kernels
    if (stream_ready(ctx->kernel_q.stream)) {
        if (ctx->kernel_q.current == NULL) {
            BaseOpKernel *kop = ctx->kernel_q.pop();
            if (kop != NULL) {
                isAnythingActive = true;
                assert(kop->is_kernel());
                assert(!kop->begun);
                x10rt_msg_type type = kop->p.type;
                CUfunction k = ctx->cbs[type].kernel_cbs.kernel;
                DEBUG(1,"%p<<<%d,%d,%d>>> argc: %d  argv: %p  cmemc: %d  cmemv: %p\n",
                      (void*)k, kop->blocks, kop->threads, kop->shm, kop->argc, (void*)kop->argv, kop->cmemc, kop->cmemv);
                CUdeviceptr cmem = ctx->cbs[type].kernel_cbs.cmem;
                if (kop->cmemc > 0 && cmem!=0)
                    CU_SAFE(cuMemcpyHtoD(cmem, kop->cmemv, kop->cmemc));
                // y and z params we leave as 1, as threads can vary from 1 to 512
                CU_SAFE(cuFuncSetBlockShape(k, kop->threads, 1, 1));
                CU_SAFE(cuParamSetv(k, 0, &kop->argv[0], kop->argc));
                CU_SAFE(cuParamSetSize(k, kop->argc));
                CU_SAFE(cuFuncSetSharedSize(k, kop->shm));
                CU_SAFE(cuLaunchGridAsync(k, kop->blocks, 1, ctx->kernel_q.stream));
                kop->begun = true;
                assert(ctx->kernel_q.current == NULL);
                ctx->kernel_q.current = kop;
            }
        } else {
           	isAnythingActive = true;
            BaseOpKernel *kop = ctx->kernel_q.current;
            ctx->kernel_q.current = NULL;
            assert(kop->is_kernel());
            assert(kop->begun);
            DEBUG(2,"probe: kernel complete\n");
            x10rt_msg_type type = kop->p.type;
            x10rt_cuda_post *fptr = ctx->cbs[type].kernel_cbs.post;
            DEBUG(2,"probe: post callback begins\n");
            CU_SAFE(cuCtxPopCurrent(NULL));
            big_lock_of_doom.release();
            fptr(&kop->p, kop->blocks, kop->threads, kop->shm, kop->argc, kop->argv, kop->cmemc, kop->cmemv); /****CALLBACK****/
            big_lock_of_doom.acquire();
            CU_SAFE(cuCtxPushCurrent(ctx->ctx));
            DEBUG(2,"probe: post callback ends\n");
            safe_free(kop->p.msg);
            kop->~BaseOpKernel();
            free(kop);
        }
    }
    else
    	isAnythingActive = true;

    // spool DMAs
    if (stream_ready(ctx->dma_q.stream)) {

        BaseOpCopy *cop = ctx->dma_q.current;

        if (cop == NULL) {
            cop = ctx->dma_q.pop();
            if (cop==NULL) goto landingzone;
            assert(!cop->begun);
            cop->begun = true;
            ctx->dma_q.current = cop;
        }
       	isAnythingActive = true;
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
            DEBUG(1,"get(%p,%p,%llu,%llu,%llu)\n", src, dst, (unsigned long long)len, (unsigned long long)started, (unsigned long long)finished);
            // front buffer handled last tick... available for re-use
            if (started > 0) ctx->swapBuffers();
            // invoke async copy into back buffer
            if (dma_sz > 0) {
                DEBUG(3,"cuMemcpyDtoHAsync(%p,0x%llX,%llu, ...)\n", ctx->back, src+started, dma_sz);
                CU_SAFE(cuMemcpyDtoHAsync(ctx->back,
                                          (CUdeviceptr)(size_t)(src+started),
                                          dma_sz,
                                          ctx->dma_q.stream));
            }
            if (started > 0) {
                DEBUG(2,"memcpy(%p,%p,%d)\n", dst+finished, ctx->front, started-finished);
                ::memcpy(dst+finished, ctx->front, started-finished);
                finished = started;
            }
            started += dma_sz;
        } else if (cop->is_put()) {
            DEBUG(1,"put(%p,%p,%llu,%llu,%llu)\n", src, dst, (unsigned long long)len, (unsigned long long)started, (unsigned long long)finished);
            // back buffer has now been copied to device... available for re-use
            if (started > 0) {
                ctx->swapBuffers();
                DEBUG(3,"cuMemcpyHtoDAsync(0x%llX,%p,%llu, ...)\n", dst+finished, ctx->back, started-finished);
                CU_SAFE(cuMemcpyHtoDAsync((CUdeviceptr)(size_t)(dst+finished),
                                          ctx->back,
                                          started-finished,
                                          ctx->dma_q.stream));
                finished = started;
            }
            if (dma_sz > 0) {
                DEBUG(2,"memcpy(%p,%p,%d)\n", ctx->front, src+started, dma_sz);
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
            big_lock_of_doom.release();
            ch(&cop->p, len); /****CALLBACK****/
            safe_free(cop->p.msg);
            cop->~BaseOpCopy();
            free(cop);
            return isAnythingActive; // always true
        }
    }
    else
    	isAnythingActive = true;

    landingzone:

    CU_SAFE(cuCtxPopCurrent(NULL));
    big_lock_of_doom.release();

    return isAnythingActive;
#else
    (void) ctx;
    abort();
#endif
}

// vim: shiftwidth=4:tabstop=4:expandtab:textwidth=100
