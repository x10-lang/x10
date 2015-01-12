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

#ifndef X10_LANG_FLOAT_H
#define X10_LANG_FLOAT_H

#include <x10aux/config.h>
#include <cmath>

namespace x10 {
    namespace lang {
        class String;
        
        /* Use to move bits between x10_float/x10_int without confusing the compiler */
        typedef union TypePunner_IF {
            x10_int i;
            x10_float f;
        } TypePunner_IF;

        class FloatNatives {
        public:
            static String* toHexString(x10_float value);
            static String* toString(x10_float value);
            static x10_float parseFloat(String* s);
            static x10_boolean isNaN(x10_float value) {
#if defined(_AIX) || defined(__FCC_VERSION)
				return isnan(value);
#else
				return std::isnan(value);
#endif
            }
            static x10_boolean isInfinite(x10_float value) {
#if defined(_AIX) || defined(__FCC_VERSION)
				return isinf(value);
#else
				return std::isinf(value);
#endif
            }
            static x10_int toIntBits(x10_float value) {
                // Check for NaN and return canonical NaN value
                return isNaN(value) ? 0x7fc00000 : toRawIntBits(value);
            }
            static x10_int toRawIntBits(x10_float value) {
                TypePunner_IF tmp;
                tmp.f = value;
                return tmp.i;
            }
            static x10_float fromIntBits(x10_int value) {
                TypePunner_IF tmp;
                tmp.i = value;
                return tmp.f;
            }
            static inline x10_int compareTo(x10_float v1, x10_float v2) {
                return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
            }
            static x10_byte toByte(x10_float value) {
                if (value > (x10_float)((x10_byte)0x7f)) return (x10_byte)0x7f;
                if (value < (x10_float)((x10_byte)0x80)) return (x10_byte)0x80;
                return (x10_byte)value;
            }
            static x10_ubyte toUByte(x10_float value) {
                if (value > (x10_float)((x10_ubyte)0xff)) return (x10_byte)0xff;
                if (value < 0) return (x10_ubyte)0;
                return (x10_ubyte)value;
            }
            static x10_short toShort(x10_float value) {
                if (value > (x10_float)((x10_short)0x7fff)) return (x10_short)0x7fff;
                if (value < (x10_float)((x10_short)0x8000)) return (x10_short)0x8000;
                return (x10_short)value;
            }
            static x10_ushort toUShort(x10_float value) {
                if (value > (x10_float)((x10_ushort)0xffff)) return (x10_ushort)0xffff;
                if (value < 0) return (x10_ushort)0;
                return (x10_ushort)value;
            }
            static x10_int toInt(x10_float value) {
                if (value > (x10_float)((x10_int)0x7fffffff)) return (x10_int)0x7fffffff;
                if (value < (x10_float)((x10_int)0x80000000)) return (x10_int)0x80000000;
                return (x10_int)value;
            }
            static x10_uint toUInt(x10_float value) {
                if (value > (x10_float)((x10_uint)0xffffffff)) return (x10_uint)0xffffffff;
                if (value < 0) return (x10_uint)0;
                return (x10_uint)value;
            }
            static x10_long toLong(x10_float value) {
                if (value > (x10_float)((x10_long)0x7fffffffffffffffLL)) return (x10_long)0x7fffffffffffffffLL;
                if (value < (x10_float)((x10_long)0x8000000000000000LL)) return (x10_long)0x8000000000000000LL;
                return (x10_long)value;
            }
            static x10_ulong toULong(x10_float value) {
                if (value > (x10_float)((x10_ulong)0xffffffffffffffffLL)) return (x10_ulong)0xffffffffffffffffLL;
                if (value < 0) return (x10_ulong)0;
                return (x10_ulong)value;
            }
            static inline x10_float mod(x10_float a, x10_float b) {
                return (x10_float)::fmodf(a, b);
            }
        };
    }
}

#endif /* X10_LANG_FLOAT_H */

