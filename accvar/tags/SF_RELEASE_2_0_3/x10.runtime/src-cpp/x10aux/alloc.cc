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

#include <x10aux/config.h>

#include <x10aux/alloc.h>

#include <cstdio>
#include <cstdarg>

#include <x10aux/throw.h>
#include <x10/lang/OutOfMemoryError.h>

using namespace x10aux;

#ifdef __CYGWIN__
extern "C" int vsnprintf(char *, size_t, const char *, va_list); 
#endif

void x10aux::reportOOM(size_t size) {
    _M_("Out of memory allocating " << size << " bytes");
#ifndef NO_EXCEPTIONS
    throwException<x10::lang::OutOfMemoryError>();
#else
    assert(false && "Out of memory");
#endif
}

char *x10aux::alloc_printf(const char *fmt, ...) {
    va_list args;
    char try_buf[1];
    va_start(args, fmt);
    std::size_t sz = vsnprintf(try_buf, 0, fmt, args);
    va_end(args);
    char *r = x10aux::alloc<char>(sz+1, false);
    va_start(args, fmt);
    std::size_t s1 = vsnprintf(r, sz+1, fmt, args);
    (void) s1;
    assert (s1 == sz);
    va_end(args);
    return r;
}

char *x10aux::realloc_printf(char *buf, const char *fmt, ...) {
    std::size_t original_sz = strlen(buf);
    va_list args;
    char try_buf[1];
    va_start(args, fmt);
    std::size_t sz = original_sz + vsnprintf(try_buf, 0, fmt, args);
    va_end(args);
    char *r = x10aux::realloc(buf, sz+1);
    // append the new stuff onto the original stuff
    va_start(args, fmt);
    std::size_t s1 = vsnprintf(&r[original_sz], sz+1, fmt, args);
    (void) s1;
    assert (s1 == sz);
    va_end(args);
    return r;
}

#ifdef X10_USE_BDWGC
bool x10aux::gc_init_done;
#endif        

void *x10aux::realloc_internal (void *src, size_t dsz) {
    void *ret;
#ifdef X10_USE_BDWGC
    ret = GC_REALLOC(src, dsz);
#else
    ret = ::realloc(src, dsz);
#endif
    if (ret==NULL && dsz>0) {
        reportOOM(dsz);
    }
    return ret;
}

void x10aux::dealloc_internal (const void *obj_) {
    if (!x10aux::disable_dealloc_) {
        void *obj = const_cast<void*>(obj_); // free does not take const void *
#ifdef X10_USE_BDWGC
        GC_FREE(obj);
#else
        ::free(obj);
#endif        
    }
}

size_t x10aux::heap_size() {
#ifdef X10_USE_BDWGC
    return GC_get_heap_size();
#else
    // TODO: an actual useful implementation of this function when we aren't using GC.
    return (size_t)(-1);
#endif
}

    



        



