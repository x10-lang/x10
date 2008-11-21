#include <cstdlib>

#ifndef X10AUX_THROW_H
#define X10AUX_THROW_H

#include <x10/lang/Throwable.h>
#include <x10aux/ref.h>

namespace x10aux {

    inline void throwException(x10aux::ref<x10::lang::Throwable> e) {
        throw e->fillInStackTrace();
    }

    template<class T> void throwException() {
        throwException(new (alloc<T>()) T());
    }

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
