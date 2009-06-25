#ifdef X10_USE_CUDA_HOST

#ifndef X10AUX_CUDA_CUDA_UTILS_H
#define X10AUX_CUDA_CUDA_UTILS_H

#include <x10aux/config.h>

#include <cuda_runtime.h>

#define CU_ASSERT(x) x10aux::cu_assert(x,__FILE__,__LINE__)
#define CU_CHECK() CU_ASSERT(cudaGetLastError())

namespace x10aux {

    inline void cu_assert(cudaError_t x, const char *file, size_t line) {
        if (x==cudaSuccess) return;
        fprintf(stderr,"%s (At %s:%d)\n",cudaGetErrorString(x),file,line);
        abort();
    }

    extern cudaStream_t cuda_base_stream;
    extern cudaStream_t cuda_read_stream;

    inline void cuda_init (void) {
        CU_ASSERT(cudaStreamCreate(&cuda_base_stream));
        CU_ASSERT(cudaStreamCreate(&cuda_read_stream));
    }

    inline bool cuda_stream_busy (cudaStream_t stream)
    {
        cudaError_t result = cudaStreamQuery(stream);
        if (result == cudaErrorNotReady) return true;
        // cudaStreamQuery can also report errors
        CU_ASSERT(result);
        return false;
    }

    void the_kernel (void);
#if 0
    template<class T> T *cu_hacky_alloc_rail(std::size_t length) {
        void *mem;
        CU_ASSERT(cudaMalloc(&mem, sizeof(T) * length));
        CU_ASSERT(cudaMemset(mem, 0, sizeof(T) * length));
        return (T*) mem;
    }

    template<class T> class CudaRail {

        protected:

        cudaArray *data_array;

        texture<T> accessor;


        public:

        CudaRail (x10_int length) {
            cudaChannelFormatDesc desc = cudaCreateChannelDesc<T>();
            cudaArray *mem;
            CU_ASSERT(cudaMallocArray(&mem, &desc, num, 1));
            CU_ASSERT(cudaBindTextureToArray(&write_back,mem,&desc));
        }

        R &operator[] () { }
    }

    template<class T> void cu_hacky_exec(T *closure) {
        closure->T::apply();
    }

    template<class T> T *cu_hacky_alloc_valrail(std::size_t sz) {
        //
        return NULL;
    }
#endif
}

#endif // X10AUX_CUDA_H

#endif // X10_USE_CUDA_HOST

// vim: shiftwidth=4:tabstop=4:textwidth=100:expandtab
