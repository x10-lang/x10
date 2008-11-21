
#ifndef X10AUX_THROW_H
#define X10AUX_THROW_H

#include <cstdlib>
#include <x10aux/config.h>
#include <x10aux/ref.h>

#include <x10/lang/Throwable.h>

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
