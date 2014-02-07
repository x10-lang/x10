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

#ifndef X10_LANG_INT_H
#define X10_LANG_INT_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        class String;
        
        class IntNatives {
        public:
            static String* toString(x10_int value, x10_int radix);
            static String* toString(x10_int value);
            static x10_int parseInt(String* s, x10_int radix);
            static x10_int parseInt(String* s) { return parseInt(s, 10); }
            static x10_int highestOneBit(x10_int x) {
                x |= (x >> 1);
                x |= (x >> 2);
                x |= (x >> 4);
                x |= (x >> 8);
                x |= (x >> 16);
                return x & ~(((x10_uint)x) >> 1);
            }
            static x10_int lowestOneBit(x10_int x) {
                return x & (-x);
            }
            static x10_int numberOfLeadingZeros(x10_int x) {
                x |= (x >> 1);
                x |= (x >> 2);
                x |= (x >> 4);
                x |= (x >> 8);
                x |= (x >> 16);
                return bitCount(~x);
            }
            static x10_int numberOfTrailingZeros(x10_int x) {
                return bitCount(~x & (x-1));
            }
            static x10_int bitCount(x10_int x) {
                x10_uint ux = (x10_uint)x;
                ux = ux - ((ux >> 1) & 0x55555555);
                ux = (ux & 0x33333333) + ((ux >> 2) & 0x33333333);
                ux = (ux + (ux >> 4)) & 0x0F0F0F0F;
                ux = ux + (ux >> 8);
                ux = ux + (ux >> 16);
                return (x10_int)(ux & 0x3F);
            }
            static x10_int rotateLeft(x10_int x, x10_int distance) {
                return (x << distance) | (((x10_uint)x) >> (32 - distance));
            }
            static x10_int rotateRight(x10_int x, x10_int distance) {
                return (((x10_uint)x) >> distance) | (x << (32 - distance));
            }
            static x10_int reverse(x10_int x) {
                x10_uint ux = (x10_uint)x;
                ux = ((ux & 0x55555555) << 1) | ((ux >> 1) & 0x55555555);
                ux = ((ux & 0x33333333) << 2) | ((ux >> 2) & 0x33333333);
                ux = ((ux & 0x0F0F0F0F) << 4) | ((ux >> 4) & 0x0F0F0F0F);
                ux = (ux << 24) | ((ux & 0xFF00) << 8) | ((ux >> 8) & 0xFF00) | (ux >> 24);
                return (x10_int)ux;
            }                
            static inline x10_int signum(x10_int x) {
                return (x >> 31) | (((x10_uint)(-x)) >> 31);
            }
            static x10_int reverseBytes(x10_int x) {
                x10_long value = 0;
                if (x<0) {
                    value = 0x80000000;
                    x &= 0x7FFFFFFF;
                }
                value += x;
                x10_long b0 = value & 0x000000FF;
                x10_long b1 = value & 0x0000FF00;
                x10_long b2 = value & 0x00FF0000;
                x10_long b3 = value & 0xFF000000;
                // reverse bytes
                b0 <<= 24; b1 <<= 8; b2 >>= 8; b3 >>= 24;
                return b0 | b1 | b2 | b3;
            }
            static inline x10_int compareTo(x10_int v1, x10_int v2) {
                return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
            }
        };
    }
}

#endif /* X10_LANG_INT_H */

#ifndef X10_LANG_INT_H_NODEPS
#define X10_LANG_INT_H_NODEPS

/*
 * Must include header files for any types
 * mentioned in @Native annotations but not
 * present in method return types.
 */
#define X10_LANG_INTRANGE_H_NODEPS
#include <x10/lang/IntRange.h>
#undef X10_LANG_INTRANGE_H_NODEPS

#endif
