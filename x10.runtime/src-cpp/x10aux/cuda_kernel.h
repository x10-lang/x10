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

#ifndef X10AUX_CUDA_KERNEL_H
#define X10AUX_CUDA_KERNEL_H

namespace x10 { namespace array {
    template<class T> class Array;
} }

namespace x10aux {

    template<class T> struct cuda_array {
        x10_int FMGL(size);
        T *raw;
        T &__apply (x10_int i) { return raw[i]; }
        T set (const T &v, x10_int i) { return raw[i] = v; }
    };

    inline void check_shm_size(unsigned int t) {
        unsigned int limit = 16*1024;
        if (t>=limit) {
            fprintf(stderr,"ERROR: This CUDA kernel requires %d bytes of shared memory, "
                           "exceeding the limit of %d bytes.\n", t, limit);
            abort();
        }
    }

    inline void check_cmem_size(unsigned int t) {
        unsigned int limit = 64*1024;
        if (t>=limit) {
            fprintf(stderr,"ERROR: This CUDA kernel requires %d bytes of constant memory, "
                           "exceeding the limit of %d bytes.\n", t, limit);
            abort();
        }
    }

    struct CMemPopulator {
        char *ptr;
        int offset;
        CMemPopulator(char *ptr_) : ptr(ptr_), offset(0) { }

        // use extra template param S to avoid having to include headers in this file
        template<class T, class S> void populateArr (S init);
        //template<class T, class S> void populateSeq (S init);
        //template<class T> void populateClosure (int elements, x10::lang::Reference* init);
        //template<class T> void populateConstant (int elements, T init);
    };
}

template<class T, class S> void x10aux::CMemPopulator::populateArr (S init) {
    x10aux::nullCheck(init);
    size_t sz = sizeof(T) * init->FMGL(size);
    memcpy(&ptr[offset], &init->raw[0], sz);
    offset += sz;
}
/*
template<class T, class S> void x10aux::CMemPopulator::populateSeq (S init) {
    x10aux::nullCheck(init);
    int elements = init->FMGL(size); // size property
    for (int i=0 ; i<elements ; ++i) {
        T tmp = (init->*(x10aux::findITable<S>(init->_getITables())->apply))(i);
        size_t elsz = sizeof(T);
        memcpy(&ptr[offset], &tmp, elsz);
        offset += elsz;
    }
}
*/
/*
template<class T> void x10aux::CMemPopulator::populateClosure (int elements, x10::lang::Reference* init) {
    x10aux::nullCheck(init);
    for (int i=0 ; i<elements ; ++i) {
        T tmp = (init->*(x10aux::findITable<x10::lang::Fun_0_1<x10_int, T> >(init->_getITables())->apply))(i);
        size_t elsz = sizeof(T);
        memcpy(&ptr[offset], &tmp, elsz);
        offset += elsz;
    }
}
template<class T> void x10aux::CMemPopulator::populateConstant (int elements, T init) {
    for (int i=0 ; i<elements ; ++i) {
        size_t elsz = sizeof(T);
        memcpy(&ptr[offset], &init, elsz);
        offset += elsz;
    }
}
*/

#endif
