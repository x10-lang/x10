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

#ifndef X10AUX_DOUBLE_UTILS_H
#define X10AUX_DOUBLE_UTILS_H

#include <x10aux/config.h>
#include <x10aux/math.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    class double_utils {
    public:
        static x10::lang::String* toHexString(x10_double value);
        static x10::lang::String* toString(x10_double value);
        static x10_double parseDouble(x10::lang::String* s);
        static x10_boolean isNaN(x10_double value);
        static x10_boolean isInfinite(x10_double value);
        static x10_long toLongBits(x10_double value);
        static x10_long toRawLongBits(x10_double value);
        static x10_double fromLongBits(x10_long value);
        static inline x10_int compareTo(x10_double v1, x10_double v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
        static x10_byte toByte(x10_double value);
        static x10_ubyte toUByte(x10_double value);
        static x10_short toShort(x10_double value);
        static x10_ushort toUShort(x10_double value);
        static x10_int toInt(x10_double value);
        static x10_uint toUInt(x10_double value);
        static x10_long toLong(x10_double value);
        static x10_ulong toULong(x10_double value);
    };

    inline x10_double mod(x10_double a, x10_double b) {
        x10_double d = (x10_double)::fmod(a, b);
        return d;
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
