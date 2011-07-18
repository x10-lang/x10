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

#include <x10aux/float_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>
#include <errno.h>
#include <math.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

/* Use to move bits between x10_float/x10_int without confusing the compiler */
typedef union TypePunner {
    x10_int i;
    x10_float f;
} TypePunner;

const ref<String> x10aux::float_utils::toHexString(x10_float value) {
    (void) value;
    UNIMPLEMENTED("toHexString");
    return X10_NULL;
}

const ref<String> x10aux::float_utils::toString(x10_float value) {
    return to_string(value);
}

x10_float x10aux::float_utils::parseFloat(ref<String> s) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_float ans = strtof(start, &end);
    if (errno != 0 || ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return ans;
}

x10_boolean x10aux::float_utils::isNaN(x10_float x) {
    return x10aux::math::isnan(x);
}

x10_boolean x10aux::float_utils::isInfinite(x10_float x) {
    return x10aux::math::isinf(x);
}

x10_int x10aux::float_utils::toIntBits(x10_float x) {
    // Check for NaN and return canonical NaN value
    return isNaN(x) ? 0x7fc00000 : toRawIntBits(x);
}

x10_int x10aux::float_utils::toRawIntBits(x10_float x) {
    TypePunner tmp;
    tmp.f = x;
    return tmp.i;
}

x10_float x10aux::float_utils::fromIntBits(x10_int x) {
    TypePunner tmp;
    tmp.i = x;
    return tmp.f;
}

x10_byte x10aux::float_utils::toByte(x10_float value) {
    if (value > (x10_float)((x10_byte)0x7f)) return (x10_byte)0x7f;
    if (value < (x10_float)((x10_byte)0x80)) return (x10_byte)0x80;
    return (x10_byte)value;
}

x10_ubyte x10aux::float_utils::toUByte(x10_float value) {
    if (value > (x10_float)((x10_ubyte)0xff)) return (x10_byte)0xff;
    if (value < 0) return (x10_ubyte)0;
    return (x10_ubyte)value;
}

x10_short x10aux::float_utils::toShort(x10_float value) {
    if (value > (x10_float)((x10_short)0x7fff)) return (x10_short)0x7fff;
    if (value < (x10_float)((x10_short)0x8000)) return (x10_short)0x8000;
    return (x10_short)value;
}

x10_ushort x10aux::float_utils::toUShort(x10_float value) {
    if (value > (x10_float)((x10_ushort)0xffff)) return (x10_ushort)0xffff;
    if (value < 0) return (x10_ushort)0;
    return (x10_ushort)value;
}

x10_int x10aux::float_utils::toInt(x10_float value) {
    if (value > (x10_float)((x10_int)0x7fffffff)) return (x10_int)0x7fffffff;
    if (value < (x10_float)((x10_int)0x80000000)) return (x10_int)0x80000000;
    return (x10_int)value;
}

x10_uint x10aux::float_utils::toUInt(x10_float value) {
    if (value > (x10_float)((x10_uint)0xffffffff)) return (x10_uint)0xffffffff;
    if (value < 0) return (x10_uint)0;
    return (x10_uint)value;
}

x10_long x10aux::float_utils::toLong(x10_float value) {
    if (value > (x10_float)((x10_long)0x7fffffffffffffffLL)) return (x10_long)0x7fffffffffffffffLL;
    if (value < (x10_float)((x10_long)0x8000000000000000LL)) return (x10_long)0x8000000000000000LL;
    return (x10_long)value;
}

x10_ulong x10aux::float_utils::toULong(x10_float value) {
    if (value > (x10_float)((x10_ulong)0xffffffffffffffffLL)) return (x10_ulong)0xffffffffffffffffLL;
    if (value < 0) return (x10_ulong)0;
    return (x10_ulong)value;
}

// vim:tabstop=4:shiftwidth=4:expandtab
