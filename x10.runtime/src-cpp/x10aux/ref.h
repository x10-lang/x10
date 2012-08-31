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

#ifndef X10REF_H
#define X10REF_H

#include <cstdlib>
#include <cassert>

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

namespace x10aux {

    void throwNPE() X10_PRAGMA_NORETURN;

    template <class T> inline T* nullCheck(T* obj) {
        #if !defined(NO_NULL_CHECKS) && !defined(NO_EXCEPTIONS)
        if (NULL == obj) throwNPE();
        #endif
        return obj;
    }

    // A no-op for non-refs
    template <class T> inline T nullCheck(T str) {
        return str;
    }

} //namespace x10aux


#endif

// vim:tabstop=4:shiftwidth=4:expandtab
