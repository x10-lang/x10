#ifndef X10AUX_THROW_H
#define X10AUX_THROW_H

#include <cstdlib>
#include <x10aux/config.h>
#include <x10aux/ref.h>

#include <x10/lang/Throwable.h>

#ifdef __GNUC__
// stops the compiler warning about functions that don't return but do throw
#define NORETURN __attribute__ ((noreturn))
#else
#define NORETURN
#endif


namespace x10aux {

    template<class T> void throwException() NORETURN;

    void throwException(x10aux::ref<x10::lang::Throwable> e) NORETURN;

    inline void throwException(x10aux::ref<x10::lang::Throwable> e) {
        throw e->fillInStackTrace();
    }

    template<class T> void throwException() {
        throwException(new (alloc<T>()) T());
    }

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
