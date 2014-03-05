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

#ifndef X10AUX_THROW_H
#define X10AUX_THROW_H

#include <cstdlib>
#include <x10aux/config.h>

namespace x10aux {

    /*
     * Helper function for arithmetic exceptions
     */
    
    void throwArithmeticException() X10_PRAGMA_NORETURN;

    inline x10_byte zeroCheck(x10_byte val) {
        #if !defined(NO_CHECKS)
        if (0 == val) throwArithmeticException();
        #endif
        return val;
    }
    inline x10_ubyte zeroCheck(x10_ubyte val) {
        #if !defined(NO_CHECKS)
        if (0 == val) throwArithmeticException();
        #endif
        return val;
    }
    inline x10_short zeroCheck(x10_short val) {
        #if !defined(NO_CHECKS)
        if (0 == val) throwArithmeticException();
        #endif
        return val;
    }
    inline x10_ushort zeroCheck(x10_ushort val) {
        #if !defined(NO_CHECKS)
        if (0 == val) throwArithmeticException();
        #endif
        return val;
    }
    inline x10_int zeroCheck(x10_int val) {
        #if !defined(NO_CHECKS)
        if (0 == val) throwArithmeticException();
        #endif
        return val;
    }
    inline x10_uint zeroCheck(x10_uint val) {
        #if !defined(NO_CHECKS)
        if (0 == val) throwArithmeticException();
        #endif
        return val;
    }
    inline x10_long zeroCheck(x10_long val) {
        #if !defined(NO_CHECKS)
        if (0 == val) throwArithmeticException();
        #endif
        return val;
    }
    inline x10_ulong zeroCheck(x10_ulong val) {
        #if !defined(NO_CHECKS)
        if (0 == val) throwArithmeticException();
        #endif
        return val;
    }

    /*
     * Helper function for null pointer exceptions
     */
    void throwNPE() X10_PRAGMA_NORETURN;

    template <class T> inline T* nullCheck(T* obj) {
        #if !defined(NO_NULL_CHECKS)
        if (NULL == obj) throwNPE();
        #endif
        return obj;
    }

    // A no-op for non-pointers (they can't be null)
    template <class T> inline T nullCheck(T x) {
        return x;
    }


    /*
     * Helper function for class cast exceptions
     */
    class RuntimeType;

    extern void throwClassCastException(const RuntimeType *from, const RuntimeType *to) X10_PRAGMA_NORETURN;
    extern void throwClassCastException(const char *msg) X10_PRAGMA_NORETURN;

    /*
     * Helper function for UnsupportedOperationException
     */
    extern void throwUnsupportedOperationException(const char *msg) X10_PRAGMA_NORETURN;

    /*
     * Helper function for NotSerializableException
     */
    extern void throwNotSerializableException(const char *msg) X10_PRAGMA_NORETURN;
}

#endif /* X10AUX_THROW_H */



#ifndef X10AUX_THROW_H_NODEPS
#define X10AUX_THROW_H_NODEPS

#include <x10/lang/CheckedThrowable.h>

namespace x10aux {

    template<class T> void throwException() X10_PRAGMA_NORETURN;

    void throwException(::x10::lang::CheckedThrowable* e) X10_PRAGMA_NORETURN;

    inline void throwException(::x10::lang::CheckedThrowable* e) {
        throw e->fillInStackTrace();
    }

    template<class T> void throwException() {
        throwException(T::_make());
    }

}

#endif /* X10AUX_THROW_H_NODEPS */

// vim:tabstop=4:shiftwidth=4:expandtab
