/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

// [DC] This file is included at the top of each .cu file.  These files contain
// CUDA kernels.  They are compiled by nvcc only.  This file should not be
// included in normal c++ or header files.

#ifndef X10AUX_KERNEL_CUH
#define X10AUX_KERNEL_CUH

#ifndef NO_IOSTREAM
    #define NO_IOSTREAM // this apparently will be fixed in a future release of cuda
#endif
#ifndef NO_CHECKS
    #define NO_CHECKS // can't abort() assert() or throw exception on the gpu
#endif
#ifndef NDEBUG
    #define NDEBUG // as above
#endif

#include <cfloat>

namespace x10aux {
    template<class T> __device__ T *nullCheck (T *v) { return v; }
    template<class T> __device__ T zeroCheck (T v) { return v; }

    template<class T, class U> __device__ x10_boolean struct_equals (T a, U b) { return a==b; }

    template<class T> struct cuda_array {
        x10_int FMGL(size);
        T *raw;
        __device__ T &__apply (x10_int i) { return raw[i]; }
        __device__ T __set (const T &v, x10_int i) { return raw[i] = v; }
    };

    template<class T> __device__ cuda_array<T> *nullCheck (cuda_array<T> &v) { return &v; }


}

__device__ void _X10_STATEMENT_HOOK() { }

namespace x10 { namespace util {

        template <class T, x10_int SZ> struct NativeVec{
                __device__ inline x10_int size() const { return SZ; }
                T arr[SZ];
                __device__ NativeVec(void) { };
                __device__ NativeVec(size_t sz) { (void) sz; };
                __device__ const T &get (int i) const { return arr[i]; }
                __device__ const T &set (const T &v, int i) { arr[i] = v; return v; }
                __device__ NativeVec(const NativeVec<T,SZ> &src)
                {
                        ::memcpy(arr, src.arr, SZ * sizeof(T));
                }
        };      
} }

#endif

// vim:tabstop=4:shiftwidth=4:expandtab
