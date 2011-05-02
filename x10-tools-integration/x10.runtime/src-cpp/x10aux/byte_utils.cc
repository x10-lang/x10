/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 *  (C) Copyright Australian National University 2010.
 */

#include <x10aux/byte_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>
#include <errno.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::byte_utils::toString(x10_byte value, x10_int radix) {
    if (0 == value) return String::Lit("0");
    assert(radix>=2);
    assert(radix<=16);
    static char numerals[] = { '0', '1', '2', '3', '4', '5', '6', '7',  
                               '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    // worst case is binary -128: needs -, 8 digits, and a '\0'
    char buf[10] = ""; //zeroes entire buffer (S6.7.8.21)
    x10_int value2;
    if (value < 0) {
        value2 = 0x80 - (value & 0x7F);
    } else {
        value2 = value;
    }
    char *b;
    // start on the '\0', will predecrement so will not clobber it
    for (b=&buf[9] ; value2>0 ; value2/=radix) {
        *(--b) = numerals[value2 % radix];
    }
    if (value < 0) {
        *(--b) = '-';
    }
    return String::Steal(alloc_printf("%s",b));
}

const ref<String> x10aux::byte_utils::toHexString(x10_byte value) {
    return x10aux::byte_utils::toString(value, 16);
}

const ref<String> x10aux::byte_utils::toOctalString(x10_byte value) {
    return x10aux::byte_utils::toString(value, 8);
}

const ref<String> x10aux::byte_utils::toBinaryString(x10_byte value) {
    return x10aux::byte_utils::toString(value, 2);
}

const ref<String> x10aux::byte_utils::toString(x10_byte value) {
    return to_string(value);
}

x10_byte x10aux::byte_utils::parseByte(ref<String> s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_int ans = strtol(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        (ans != (x10_byte)ans) ||
        ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return (x10_byte)ans;
}

x10_ubyte x10aux::byte_utils::parseUByte(ref<String> s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_uint ans = strtoul(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        (ans != (x10_ubyte)ans) ||
        ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return (x10_ubyte)ans;
}

// vim:tabstop=4:shiftwidth=4:expandtab
