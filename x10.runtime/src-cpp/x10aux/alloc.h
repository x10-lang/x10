#ifndef X10AUX_ALLOC_H
#define X10AUX_ALLOC_H

#include <x10aux/config.h>

#ifdef X10_USE_BDWGC
#ifdef __linux__
#define GC_LINUX_THREADS
#endif 
#include "gc.h"
#endif

#include <cstdlib>
#include <cstring>
#include <new> // [DC] took me an hour to work out that we needed this for placement new

namespace x10aux {

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

    void throwOOME() X10_PRAGMA_NORETURN;

    template<class T> T* alloc(size_t size = sizeof(T)) {
        _M_("Allocating " << size << " bytes of type " << TYPENAME(T));
#ifdef X10_USE_BDWGC        
        T* ret = (T*)GC_MALLOC(size);
#else        
        T* ret = (T*)malloc(size);
#endif        
        _M_("\t-> " << (void*)ret);
        if (ret == NULL && size > 0) {
            _M_("Out of memory allocating " << size << " bytes");
            #ifndef NO_EXCEPTIONS
            throwOOME();
            #else
            assert(false && "Out of memory");
            #endif
        }
        return ret;
    }

    template<class T> T* realloc(T* src, size_t dsz) {
        _M_("Reallocing chunk " << (void*)src << " of type " << TYPENAME(T));
#ifdef X10_USE_BDWGC
        T *ret = (T*)GC_REALLOC(src, dsz);
#else
        T *ret = (T*)realloc(src, dsz);
#endif
        if (ret==NULL && dsz>0) {
            _M_("Out of memory reallocating " << dsz << " bytes");
            #ifndef NO_EXCEPTIONS
            throwOOME();
            #else
            assert(false && "Out of memory");
            #endif
        }
        return ret;
    }

    template<class T> void dealloc(const T* obj_) {
        T *obj = const_cast<T*>(obj_); // free does not take const void *
        _M_("Freeing chunk " << (void*)obj << " of type " << TYPENAME(T));
#ifdef X10_USE_BDWGC
        GC_FREE(obj);
#else        
        free(obj);
#endif        
    }

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
