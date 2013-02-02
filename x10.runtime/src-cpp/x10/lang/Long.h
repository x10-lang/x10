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

#ifndef X10_LANG_LONG_H
#define X10_LANG_LONG_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        class String;
        
        class LongNatives {
        public:
            static String* toString(x10_long value, x10_int radix);
            static String* toString(x10_long value);
            static x10_long parseLong(String* s, x10_int radix);
            static x10_long parseLong(String* s) { return parseLong(s, 10); }
            static x10_long highestOneBit(x10_long value);
            static x10_long lowestOneBit(x10_long value);
            static x10_int numberOfLeadingZeros(x10_long value);
            static x10_int numberOfTrailingZeros(x10_long value);
            static x10_int bitCount(x10_long value);
            static x10_long rotateLeft(x10_long value, x10_int distance);
            static x10_long rotateRight(x10_long value, x10_int distance);
            static x10_long reverse(x10_long value);
            static inline x10_int signum(x10_long value) {
                return (value >> 63) | (((x10_ulong)(-value)) >> 63);
            }
            static x10_long reverseBytes(x10_long value);
            static inline x10_int compareTo(x10_long v1, x10_long v2) {
                return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
            }
        };
    }
}

#endif /* X10_LANG_LONG_H */

#ifndef X10_LANG_LONG_H_NODEPS
#define X10_LANG_LONG_H_NODEPS

/*
 * Must include header files for any types
 * mentioned in @Native annotations but not
 * present in method return types.
 */
#define X10_LANG_LONGRANGE_H_NODEPS
#include <x10/lang/LongRange.h>
#undef X10_LANG_LONGRANGE_H_NODEPS

#endif
