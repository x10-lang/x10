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

#include <errno.h>

#include <x10aux/math.h>

#include <x10/lang/Double.h>
#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>

using namespace x10::lang;
using namespace x10aux;

/* Use to move bits between x10_long/x10_double without confusing the compiler */
typedef union TypePunner {
    x10_long l;
    x10_double d;
} TypePunner;

String* DoubleNatives::toHexString(x10_double value) {
    (void) value;
    UNIMPLEMENTED("toHexString");
    return NULL;
}

String* DoubleNatives::toString(x10_double value) {
    return to_string(value);
}

x10_double DoubleNatives::parseDouble(String* s) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_double ans = strtod(start, &end);
    if (errno != 0 || ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return ans;
}

x10_boolean DoubleNatives::isNaN(x10_double x) {
    return x10aux::math::isnan(x);
}

x10_boolean DoubleNatives::isInfinite(x10_double x) {
    return x10aux::math::isinf(x);
}

x10_long DoubleNatives::toLongBits(x10_double x) {
    return isNaN(x) ? 0x7ff8000000000000LL : toRawLongBits(x);
}

x10_long DoubleNatives::toRawLongBits(x10_double x) {
    TypePunner tmp;
    tmp.d = x;
    return tmp.l;
}

x10_double DoubleNatives::fromLongBits(x10_long x) {
    TypePunner tmp;
    tmp.l = x;
    return tmp.d;
}

x10_byte DoubleNatives::toByte(x10_double value) {
    if (value > (x10_double)((x10_byte)0x7f)) return (x10_byte)0x7f;
    if (value < (x10_double)((x10_byte)0x80)) return (x10_byte)0x80;
    return (x10_byte)value;
}

x10_ubyte DoubleNatives::toUByte(x10_double value) {
    if (value > (x10_double)((x10_ubyte)0xff)) return (x10_byte)0xff;
    if (value < 0) return (x10_ubyte)0;
    return (x10_ubyte)value;
}

x10_short DoubleNatives::toShort(x10_double value) {
    if (value > (x10_double)((x10_short)0x7fff)) return (x10_short)0x7fff;
    if (value < (x10_double)((x10_short)0x8000)) return (x10_short)0x8000;
    return (x10_short)value;
}

x10_ushort DoubleNatives::toUShort(x10_double value) {
    if (value > (x10_double)((x10_ushort)0xffff)) return (x10_ushort)0xffff;
    if (value < 0) return (x10_ushort)0;
    return (x10_ushort)value;
}

x10_int DoubleNatives::toInt(x10_double value) {
    if (value > (x10_double)((x10_int)0x7fffffff)) return (x10_int)0x7fffffff;
    if (value < (x10_double)((x10_int)0x80000000)) return (x10_int)0x80000000;
    return (x10_int)value;
}

x10_uint DoubleNatives::toUInt(x10_double value) {
    if (value > (x10_double)((x10_uint)0xffffffff)) return (x10_uint)0xffffffff;
    if (value < 0) return (x10_uint)0;
    return (x10_uint)value;
}

x10_long DoubleNatives::toLong(x10_double value) {
    if (value > (x10_double)((x10_long)0x7fffffffffffffffLL)) return (x10_long)0x7fffffffffffffffLL;
    if (value < (x10_double)((x10_long)0x8000000000000000LL)) return (x10_long)0x8000000000000000LL;
    return (x10_long)value;
}

x10_ulong DoubleNatives::toULong(x10_double value) {
    if (value > (x10_double)((x10_ulong)0xffffffffffffffffLL)) return (x10_ulong)0xffffffffffffffffLL;
    if (value < 0) return (x10_ulong)0;
    return (x10_ulong)value;
}
// vim:tabstop=4:shiftwidth=4:expandtab
