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

#include <x10aux/RTT.h>

namespace x10aux {

    void reportOOM(size_t size) X10_PRAGMA_NORETURN;

    void *realloc_internal (void* src, size_t dsz);

    void dealloc_internal (const void *obj_);

    // the sequence of calls to alloc_internal_congruent must be the same (in
    // terms of size parameter) at all places for the pointers returned to be
    // actually congruent (i.e. for all places to have the same addresses)
    void *alloc_internal_congruent(size_t size);
    
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

        _M_("\t-> " << (void*)ret);
        if (ret == NULL && size > 0) {
            reportOOM(size);
        }
        return ret;
    }
    
    template<class T>inline T* system_alloc(size_t size = sizeof(T)) {
        _M_("system_alloc: Allocating " << size << " bytes of type " << TYPENAME(T));
        return (T*)::malloc(size);
    }

    template<class T> T* system_realloc(T* src, size_t dsz) {
        _M_("system_alloc: Reallocing chunk " << (void*)src << " of type " << TYPENAME(T));
        return (T*)::realloc(src, dsz);
    }

    template<class T> void system_dealloc(const T* obj_) {
        _M_("system_alloc: Freeing chunk " << (void*)obj_ << " of type " << TYPENAME(T));
        ::free(obj_);
    }

    template<class T> inline T* alloc(size_t size = sizeof(T), bool containsPtrs = true) {
        _M_("Allocating " << size << " bytes of type " << TYPENAME(T));
        return (T*)alloc_internal(size, containsPtrs);
    }

    template<class T> T* realloc(T* src, size_t dsz) {
        _M_("Reallocing chunk " << (void*)src << " of type " << TYPENAME(T));
        return (T*)realloc_internal(src, dsz);
    }


    template<class T> void dealloc(const T* obj_) {
        _M_("Freeing chunk " << (void*)obj_ << " of type " << TYPENAME(T));
        dealloc_internal(obj_);
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
