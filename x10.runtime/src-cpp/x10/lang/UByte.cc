/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2010.
 */

#include <x10/lang/Byte.h>
#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>

#include <errno.h>

using namespace x10::lang;
using namespace x10aux;

static char numerals[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
                           'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                           'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                           'x', 'y', 'z' };

String* UByteNatives::toString(x10_ubyte value, x10_int radix) {
    if (0 == value) return String::Lit("0");
    if (radix < 2 || radix > 36) radix = 10;
    // worst case is binary: 8 digits and a '\0'
    char buf[9] = ""; //zeroes entire buffer (S6.7.8.21)
    x10_ubyte value2 = value;
    char *b;
    // start on the '\0', will predecrement so will not clobber it
    for (b=&buf[8] ; value2>0 ; value2/=radix) {
        *(--b) = numerals[value2 % radix];
    }
    return String::Steal(alloc_printf("%s",b));
}

String* UByteNatives::toString(x10_ubyte value) {
    return to_string(value);
}

x10_ubyte UByteNatives::parseUByte(String* s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_uint ans = strtoul(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        (ans != (x10_ubyte)ans) ||
        ((end-start) != s->length())) {
        throwException(NumberFormatException::_make(s));
    }
    return (x10_ubyte)ans;
}

// vim:tabstop=4:shiftwidth=4:expandtab
