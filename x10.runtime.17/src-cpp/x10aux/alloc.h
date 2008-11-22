#ifndef X10AUX_ALLOC_H
#define X10AUX_ALLOC_H

#include <cstdlib>
#include <cstring>

#include <x10/x10.h> // pgas

#include <x10aux/RTT.h>

namespace x10aux {

    template<class T> T* alloc(size_t size = sizeof(T)) {
        _M_("Allocating " << size << " bytes of type " << getRTT<T>()->name());
        T* ret = (T*)x10_alloc(size);
        _M_("\t-> " << ret);
        if (ret == NULL && size > 0) {
            _M_("Out of memory allocating " << size << " bytes");
        }
        return ret;
    }

    // there should probably be an optimise x10_realloc function but never mind
    template<class T> T* realloc(T* src, size_t ssz = sizeof(T), size_t dsz = sizeof(T)) {
        T *dest = alloc<T>(dsz);
        if (dest!=NULL && src!=NULL)
            memcpy(dest, src, ssz<dsz ? ssz : dsz);
        dealloc(src);
        return dest;
    }

    template<class T> void dealloc(T* obj) {
        _M_("Freeing chunk " << obj << " of type " << getRTT<T>()->name());
        x10_free((x10_addr_t) obj);
    }

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
