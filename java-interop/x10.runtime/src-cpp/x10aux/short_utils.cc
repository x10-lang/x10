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

#include <x10aux/short_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>
#include <errno.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::short_utils::toString(x10_short value, x10_int radix) {
    (void) value; (void) radix;
    UNIMPLEMENTED("toString");
    return X10_NULL;
}

const ref<String> x10aux::short_utils::toHexString(x10_short value) {
    return x10aux::short_utils::toString(value, 16);
}

const ref<String> x10aux::short_utils::toOctalString(x10_short value) {
    return x10aux::short_utils::toString(value, 8);
}

const ref<String> x10aux::short_utils::toBinaryString(x10_short value) {
    return x10aux::short_utils::toString(value, 2);
}

const ref<String> x10aux::short_utils::toString(x10_short value) {
    return to_string(value);
}

x10_short x10aux::short_utils::parseShort(ref<String> s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_int ans = strtol(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        (ans != (x10_short)ans) ||
        ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return (x10_short)ans;
}

x10_ushort x10aux::short_utils::parseUShort(ref<String> s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_uint ans = strtoul(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        (ans != (x10_ushort)ans) ||
        ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return (x10_ushort)ans;
}

x10_short x10aux::short_utils::reverseBytes(x10_short x) {
    x10_ushort ux = (x10_ushort)x;
    x10_ushort b0 = ux & 0x0F;
    x10_ushort b1 = (ux & 0xF0) >> 8;
    ux = (b0 << 8) | b1;
    return (x10_short)ux;
}
// vim:tabstop=4:shiftwidth=4:expandtab
