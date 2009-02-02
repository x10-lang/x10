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

#include <x10/x10.h> // pgas

namespace x10aux {

    template<class T> T* alloc(size_t size = sizeof(T)) {
        _M_("Allocating " << size << " bytes of type " << TYPENAME(T));
#ifdef X10_USE_BDWGC        
        T* ret = (T*)GC_MALLOC(size);
#else        
        T* ret = (T*)x10_alloc(size);
#endif        
        _M_("\t-> " << (void*)ret);
        if (ret == NULL && size > 0) {
            _M_("Out of memory allocating " << size << " bytes");
        }
        return ret;
    }

    // for use in special contexts where we may need to pass the allocated memory
    // down into lower-levels of pgasrt, which is not aware of the gc managed heap.
    template<class T> T* alloc_non_gc(size_t size = sizeof(T)) {
        _M_("Allocating NGC" << size << " bytes of type " << TYPENAME(T));
        T* ret = (T*)x10_alloc(size);
        _M_("\t-> " << (void*)ret);
        if (ret == NULL && size > 0) {
            _M_("Out of memory allocating " << size << " bytes");
        }
        return ret;
    }

    // there should probably be an optimised x10_realloc function but never mind
    // FIXME:  There is a GC_REALLOC macro, which we could use when this is actually calling realloc..
    template<class T> T* realloc(T* src, size_t ssz = sizeof(T), size_t dsz = sizeof(T)) {
        T *dest = alloc<T>(dsz);
        if (dest!=NULL && src!=NULL)
            memcpy(dest, src, ssz<dsz ? ssz : dsz);
        dealloc(src);
        return dest;
    }

    template<class T> void dealloc(T* obj) {
        _M_("Freeing chunk " << (void*)obj << " of type " << TYPENAME(T));
#ifdef X10_USE_BDWGC
        GC_FREE((x10_addr_t) obj);
#else        
        x10_free((x10_addr_t) obj);
#endif        
    }

    // for use in special contexts where we may need to pass the allocated memory
    // down into lower-levels of pgasrt, which is not aware of the gc managed heap.
    template<class T> void dealloc_non_gc(T* obj) {
        _M_("Freeing NGC chunk " << (void*)obj << " of type " << TYPENAME(T));
        x10_free((x10_addr_t) obj);
    }

    const char *alloc_printf(const char *fmt, ...);

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
