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

#ifndef X10AUX_RAIL_UTILS_H
#define X10AUX_RAIL_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10aux {

    void throwArrayIndexOutOfBoundsException(x10_int index, x10_int length) X10_PRAGMA_NORETURN;
    
    inline void checkRailBounds(x10_int index, x10_int length) {
        #ifndef NO_BOUNDS_CHECKS
        // Since we know length is non-negative and Rails are zero-based,
        // the bounds check can be optimized to a single unsigned comparison.
        // The C++ compiler won't do this for us, since it doesn't know that length is non-negative.
        if (((x10_uint)index) >= ((x10_uint)length)) {
            x10aux::throwArrayIndexOutOfBoundsException(index, length);
        }
        #endif
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
