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

#ifndef X10AUX_INT_UTILS_H
#define X10AUX_INT_UTILS_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    class int_utils {
    public:
        static x10::lang::String* toString(x10_int value, x10_int radix);
        static x10::lang::String* toString(x10_int value);
        static x10::lang::String* toString(x10_uint value, x10_int radix);
        static x10::lang::String* toString(x10_uint value);
        static x10_int parseInt(x10::lang::String* s, x10_int radix);
        static x10_int parseInt(x10::lang::String* s) { return parseInt(s, 10); }
        static x10_uint parseUInt(x10::lang::String* s, x10_int radix);
        static x10_uint parseUInt(x10::lang::String* s) { return parseUInt(s, 10); }
        static x10_int highestOneBit(x10_int value);
        static x10_int lowestOneBit(x10_int value);
        static x10_int numberOfLeadingZeros(x10_int value);
        static x10_int numberOfTrailingZeros(x10_int value);
        static x10_int bitCount(x10_int value);
        static x10_int rotateLeft(x10_int value, x10_int distance);
        static x10_int rotateRight(x10_int value, x10_int distance);
        static x10_int reverse(x10_int value);
        static inline x10_int signum(x10_int value) {
            return (value >> 31) | (((x10_uint)(-value)) >> 31);
        }
        static x10_int reverseBytes(x10_int value);
        static inline x10_int compareTo(x10_int v1, x10_int v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
        static inline x10_int compareTo(x10_uint v1, x10_uint v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
            
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
