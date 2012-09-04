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

#ifndef X10AUX_FLOAT_UTILS_H
#define X10AUX_FLOAT_UTILS_H

#include <x10aux/config.h>
#include <x10aux/math.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    class float_utils {
    public:
        static x10::lang::String* toHexString(x10_float value);
        static x10::lang::String* toString(x10_float value);
        static x10_float parseFloat(x10::lang::String* s);
        static x10_boolean isNaN(x10_float value);
        static x10_boolean isInfinite(x10_float value);
        static x10_int toIntBits(x10_float value);
        static x10_int toRawIntBits(x10_float value);
        static x10_float fromIntBits(x10_int value);
        static inline x10_int compareTo(x10_float v1, x10_float v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
        static x10_byte toByte(x10_float value);
        static x10_ubyte toUByte(x10_float value);
        static x10_short toShort(x10_float value);
        static x10_ushort toUShort(x10_float value);
        static x10_int toInt(x10_float value);
        static x10_uint toUInt(x10_float value);
        static x10_long toLong(x10_float value);
        static x10_ulong toULong(x10_float value);
    };

    inline x10_float mod(x10_float a, x10_float b) {
        x10_float d = (x10_float)::fmodf(a, b);
        return d;
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
