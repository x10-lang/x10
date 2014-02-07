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

#ifndef X10_LANG_DOUBLE_H
#define X10_LANG_DOUBLE_H

#include <x10aux/config.h>
#include <cmath>

namespace x10 {
    namespace lang {
        class String;

        /* Use to move bits between x10_long/x10_double without confusing the compiler */
        typedef union TypePunner_LD {
            x10_long l;
            x10_double d;
        } TypePunner_LD;
        
        class DoubleNatives {
        public:
            static String* toHexString(x10_double value);
            static String* toString(x10_double value);
            static x10_double parseDouble(String* s);
            static x10_boolean isNaN(x10_double value) {
#if defined(_AIX) || defined(__FCC_VERSION)
				return isnan(value);
#else
				return std::isnan(value);
#endif
            }
            static x10_boolean isInfinite(x10_double value) {
#if defined(_AIX) || defined(__FCC_VERSION)
				return isinf(value);
#else
				return std::isinf(value);
#endif
            }
            static x10_long toLongBits(x10_double value) {
                return isNaN(value) ? 0x7ff8000000000000LL : toRawLongBits(value);
            }
            static x10_long toRawLongBits(x10_double value) {
                TypePunner_LD tmp;
                tmp.d = value;
                return tmp.l;
            }
            static x10_double fromLongBits(x10_long value) {
                TypePunner_LD tmp;
                tmp.l = value;
                return tmp.d;
            }
            static inline x10_int compareTo(x10_double v1, x10_double v2) {
                return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
            }
            static x10_byte toByte(x10_double value) {
                if (value > (x10_double)((x10_byte)0x7f)) return (x10_byte)0x7f;
                if (value < (x10_double)((x10_byte)0x80)) return (x10_byte)0x80;
                return (x10_byte)value;
            }
            static x10_ubyte toUByte(x10_double value) {
                if (value > (x10_double)((x10_ubyte)0xff)) return (x10_byte)0xff;
                if (value < 0) return (x10_ubyte)0;
                return (x10_ubyte)value;
            }
            static x10_short toShort(x10_double value) {
                if (value > (x10_double)((x10_short)0x7fff)) return (x10_short)0x7fff;
                if (value < (x10_double)((x10_short)0x8000)) return (x10_short)0x8000;
                return (x10_short)value;
            }
            static x10_ushort toUShort(x10_double value) {
                if (value > (x10_double)((x10_ushort)0xffff)) return (x10_ushort)0xffff;
                if (value < 0) return (x10_ushort)0;
                return (x10_ushort)value;
            }
            static x10_int toInt(x10_double value) {
                if (value > (x10_double)((x10_int)0x7fffffff)) return (x10_int)0x7fffffff;
                if (value < (x10_double)((x10_int)0x80000000)) return (x10_int)0x80000000;
                return (x10_int)value;
            }
            static x10_uint toUInt(x10_double value) {
                if (value > (x10_double)((x10_uint)0xffffffff)) return (x10_uint)0xffffffff;
                if (value < 0) return (x10_uint)0;
                return (x10_uint)value;
            }
            static x10_long toLong(x10_double value) {
                if (value > (x10_double)((x10_long)0x7fffffffffffffffLL)) return (x10_long)0x7fffffffffffffffLL;
                if (value < (x10_double)((x10_long)0x8000000000000000LL)) return (x10_long)0x8000000000000000LL;
                return (x10_long)value;
            }
            static x10_ulong toULong(x10_double value) {
                if (value > (x10_double)((x10_ulong)0xffffffffffffffffLL)) return (x10_ulong)0xffffffffffffffffLL;
                if (value < 0) return (x10_ulong)0;
                return (x10_ulong)value;
            }
            static inline x10_double mod(x10_double a, x10_double b) {
                return (x10_double)::fmod(a, b);
            }
        };
    }
}

#endif /* X10_LANG_DOUBLE_H */
