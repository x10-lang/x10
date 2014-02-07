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
            static x10_long highestOneBit(x10_long x) {
                x |= (x >> 1);
                x |= (x >> 2);
                x |= (x >> 4);
                x |= (x >> 8);
                x |= (x >> 16);
                x |= (x >> 32);
                return x & ~(((x10_ulong)x) >> 1);
            }
            static x10_long lowestOneBit(x10_long x) {
                return x & (-x);
            }
            static x10_int numberOfLeadingZeros(x10_long x) {
                x |= (x >> 1);
                x |= (x >> 2);
                x |= (x >> 4);
                x |= (x >> 8);
                x |= (x >> 16);
                x |= (x >> 32);
                return bitCount(~x);
            }
            static x10_int numberOfTrailingZeros(x10_long x) {
                return bitCount(~x & (x-1));
            }
            static x10_int bitCount(x10_long x) {
                x10_ulong ux = (x10_ulong)x;
                ux = ux - ((ux >> 1) & 0x5555555555555555LL);
                ux = (ux & 0x3333333333333333LL) + ((ux >> 2) & 0x3333333333333333LL);
                ux = (ux & 0x0F0F0F0F0F0F0F0FLL) + ((ux >> 4) & 0x0F0F0F0F0F0F0F0FLL);
                ux = (ux & 0x00FF00FF00FF00FFLL) + ((ux >> 8) & 0x00FF00FF00FF00FFLL);
                ux = ux + (ux >> 16);
                ux = ux + (ux >> 32);
                return (x10_int)(ux & 0x7F);
            }                
            static x10_long rotateLeft(x10_long x, x10_int distance) {
                return (x << distance) | (((x10_ulong)x) >> (64 - distance));
            }
            static x10_long rotateRight(x10_long x, x10_int distance) {
                return (((x10_ulong)x) >> distance) | (x << (64 - distance));
            }
            static x10_long reverse(x10_long x) {
                x10_ulong ux = (x10_ulong)x;
                ux = ((ux & 0x5555555555555555LL) << 1) | ((ux >> 1) & 0x5555555555555555LL);
                ux = ((ux & 0x3333333333333333LL) << 2) | ((ux >> 2) & 0x3333333333333333LL);
                ux = ((ux & 0x0F0F0F0F0F0F0F0FLL) << 4) | ((ux >> 4) & 0x0F0F0F0F0F0F0F0FLL);
                return reverseBytes((x10_long)ux);
            }
            static inline x10_int signum(x10_long x) {
                return (x >> 63) | (((x10_ulong)(-x)) >> 63);
            }
            static x10_long reverseBytes(x10_long x) {
                x10_ulong ux = x;
                x10_ulong ans = ux << 56; 
                ans |= (ux & 0xFF00L) << 40;
                ans |= (ux & 0xFF0000L) << 24;
                ans |= (ux & 0xFF000000L) << 8;
                ans |= (ux >> 8) & 0xFF000000L;
                ans |= (ux >> 24) & 0xFF0000L;
                ans |= (ux >> 40) & 0xFF00L;
                ans |= (ux >> 56);
                return (x10_long)ans;
            }
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
