/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10AUX_ALLOC_H
#define X10AUX_ALLOC_H

#include <x10aux/config.h>

#ifdef X10_USE_BDWGC
#define GC_THREADS
#include "gc.h"
#endif

#include <cstdlib>
#include <cstring>
#include <new> // [DC] took me an hour to work out that we needed this for placement new

namespace x10aux {

#ifdef THREAD_TABLE_SZ
    // bdwgc cap on the number of threads
    // we need to cap the number of threads in XRX
    // here we assume there will be no more than 16 threads created outside of XRX (transport maybe?)
#define PLATFORM_MAX_THREADS (THREAD_TABLE_SZ - 16)
#else
#define PLATFORM_MAX_THREADS 0x7fffffff // no cap
#endif

#ifdef __GNUC__
    char *alloc_printf(const char *fmt, ...) 
                __attribute__ ((format (printf, 1, 2)));
#else
    char *alloc_printf(const char *fmt, ...);
#endif

    // if char *bob points to some allocated "bob", then realloc_printf(bob," likes %s","cats")
    // will yield either the same buffer or a replacement buffer containing "bob likes cats".
    // Any dangling pointers to the original buffer will be invalid as is standard with realloc.
#ifdef __GNUC__
    char *realloc_printf(char *buf, const char *fmt, ...) 
                __attribute__ ((format (printf, 2, 3)));
#else
    char *realloc_printf(char *buf, const char *fmt, ...);
#endif

}

// Some C stdlib functions that internally allocate
// need to be re-implemented to allocate out of the
// BDWGC managed heap.
namespace x10aux {
    namespace alloc_utils {
        char *strdup(const char*);
        char *strndup(const char*, x10_int len);
    }
}

#include <x10aux/RTT.h>

namespace x10aux {

    void reportOOM(size_t size) X10_PRAGMA_NORETURN;

    void *realloc_internal (void* src, size_t dsz);

    void dealloc_internal (const void *obj_);

    // the sequence of calls to alloc_internal_congruent must be the same (in
    // terms of size parameter) at all places for the pointers returned to be
    // actually congruent (i.e. for all places to have the same addresses)
    void *alloc_internal_congruent(size_t size);

    // given an address and the id of the src and destination places, compute the
    // congruent address in the target place
    void *compute_congruent_addr(void* addr, int src, int dst);
    
    void *alloc_internal_huge(size_t size);

#ifdef X10_USE_BDWGC
	extern bool gc_init_done;
#endif

    inline void* alloc_internal(size_t size, bool containsPtrs) {
        void *ret;
#ifdef X10_USE_BDWGC        
        if (!gc_init_done) {
            GC_INIT();
            gc_init_done = true;
        }
        if (containsPtrs) {
            ret = GC_MALLOC(size);
        } else {
            ret = GC_MALLOC_ATOMIC(size);
        }
#else
        ret = ::malloc(size);
#endif        

        if (ret == NULL && size > 0) {
            reportOOM(size);
        }
        return ret;
    }
    
    template<class T> inline T* system_alloc(size_t size = sizeof(T)) {
        T* ret = (T*)::malloc(size);
        if (ret == NULL && size > 0) {
            reportOOM(size);
        }
        return ret;
    }
    template<class T> inline T* system_alloc_z(size_t size = sizeof(T)) {
        return (T*)memset(system_alloc<T>(size), 0, size);
    }

    template<class T> inline T* system_realloc(T* src, size_t dsz) {
        T* ret = (T*)::realloc(src, dsz);
        if (ret == NULL && dsz > 0) {
            reportOOM(dsz);
        }
        return ret;
    }

    inline void system_dealloc(const void* obj_) {
        ::free(const_cast<void*>(obj_));
    }

    template<class T> inline T* alloc(size_t size = sizeof(T), bool containsPtrs = true) {
        return (T*)alloc_internal(size, containsPtrs);
    }

    template<class T> inline T* alloc_z(size_t size = sizeof(T), bool containsPtrs = true) {
        return (T*)memset(alloc_internal(size, containsPtrs), 0, size);
    }
    
    template<class T> inline T* realloc(T* src, size_t dsz) {
        return (T*)realloc_internal(src, dsz);
    }


    inline void dealloc(const void* obj_) {
        dealloc_internal(const_cast<void*>(obj_));
    }

    // Return an upper bound on the current size of the heap in bytes.
    // The accuracy of result returned by this method will vary widely depending on
    // the underlying implementation of allocation.
    size_t heap_size();

    // Trigger a GC.  No-op if no GC...
    void trigger_gc();
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
