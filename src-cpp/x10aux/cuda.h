#ifdef USE_CUDA_HOST

#ifndef X10AUX_CUDA_H
#define X10AUX_CUDA_H

#include <x10aux/config.h>

#include <cuda_runtime.h>

#define CU_ASSERT(x) cu_assert(x,__FILE__,__LINE__)
#define CU_CHECK() CU_ASSERT(cudaGetLastError())

namespace x10aux {

    inline void cu_assert(cudaError_t x, const char *file, size_t line) {
        if (x==cudaSuccess) return;
        fprintf(stderr,"%s (At %s:%d)\n",cudaGetErrorString(x),file,line);
        abort();
    }

    template<class T> T *cu_hacky_alloc_rail(std::size_t length) {
        void *mem;
        CU_ASSERT(cudaMalloc(&mem, sizeof(T) * length));
        CU_ASSERT(cudaMemset(mem, 0, sizeof(T) * length));
        return (T*) mem;
    }

    template<class T> void cu_hacky_exec(T *closure) {
        closure->T::apply();
    }

    template<class T> T *cu_hacky_alloc_valrail(std::size_t sz) {
        //
    }
}

#endif // X10AUX_CUDA_H

#endif // USE_CUDA_HOST

// vim: shiftwidth=4:tabstop=4:textwidth=100:expandtab
