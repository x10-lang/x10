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

#ifndef X10AUX_LONG_UTILS_H
#define X10AUX_LONG_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    class long_utils {
    public:
        static x10::lang::String* toString(x10_long value, x10_int radix);
        static x10::lang::String* toString(x10_long value);
        static x10::lang::String* toString(x10_ulong value, x10_int radix);
        static x10::lang::String* toString(x10_ulong value);
        static x10_long parseLong(x10::lang::String* s, x10_int radix);
        static x10_long parseLong(x10::lang::String* s) { return parseLong(s, 10); }
        static x10_ulong parseULong(x10::lang::String* s, x10_int radix);
        static x10_ulong parseULong(x10::lang::String* s) { return parseULong(s, 10); }
        static x10_long highestOneBit(x10_long value);
        static x10_long lowestOneBit(x10_long value);
        static x10_int numberOfLeadingZeros(x10_long value);
        static x10_int numberOfTrailingZeros(x10_long value);
        static x10_int bitCount(x10_long value);
        static x10_long rotateLeft(x10_long value, x10_int distance);
        static x10_long rotateRight(x10_long value, x10_int distance);
        static x10_long reverse(x10_long value);
        static x10_int signum(x10_long value) {
            return (value >> 63) | (((x10_ulong)(-value)) >> 63);
        }
        static x10_long reverseBytes(x10_long value);
        static x10_int compareTo(x10_long v1, x10_long v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
        static x10_int compareTo(x10_ulong v1, x10_ulong v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
