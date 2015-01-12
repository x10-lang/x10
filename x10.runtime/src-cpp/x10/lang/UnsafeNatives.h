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

#ifndef X10_LANG_UNSAFENATIVES_H
#define X10_LANG_UNSAFENATIVES_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        template <class T> class Rail;
        
        class UnsafeNatives {
        public:
            /** Congruent memory unsafe hackery. Implements x10.lang.Unsafe.getCongruentSibling */
            template<class T> static ::x10::lang::Rail<T>* getCongruentSibling(::x10::lang::Rail<T>*r, x10_long dstId);
        };
    }
}

#endif /* X10_LANG_UNSAFENATIVES_H */

#ifndef X10_LANG_UNSAFENATIVES_H_NODEPS
#define X10_LANG_UNSAFENATIVES_H_NODEPS
#ifndef X10_LANG_UNSAFENATIVES_IMPLEMENTATION
#define X10_LANG_UNSAFENATIVES_IMPLEMENTATION
#include <x10aux/alloc.h>
#include <x10/lang/UnsafeNatives.h>

template<class T> ::x10::lang::Rail<T>* ::x10::lang::UnsafeNatives::getCongruentSibling(::x10::lang::Rail<T>*r, x10_long dstId) {
    // Pure Hackery.  Manufacture the desired remote Rail<T>* via allocater's congruent addr functions
    int src = (int)::x10aux::here;
    int dst = (int)dstId;
    void *addr = r;
    void* remoteAddr = ::x10aux::compute_congruent_addr(addr, (int)src, (int)dst);
    return (::x10::lang::Rail<T>*)remoteAddr;
}

#endif // X10_LANG_UNSAFENATIVE_IMPLEMENTATION
#endif // __X10_LANG_UNSAFENATIVES_H_NODEPS

// vim:tabstop=4:shiftwidth=4:expandtab
