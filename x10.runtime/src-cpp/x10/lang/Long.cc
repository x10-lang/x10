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

#include <x10/lang/Long.h>
#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>

using namespace x10::lang;
using namespace x10aux;

static char numerals[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
                           'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                           'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                           'x', 'y', 'z' };

String* LongNatives::toString(x10_long value, x10_int radix) {
    if (0 == value) return String::Lit("0");
    if (radix < 2 || radix > 36) radix = 10;
    // worst case is binary of Long.MIN_VALUE -- - plus 64 digits and a '\0'
    char buf[66] = ""; //zeroes entire buffer (S6.7.8.21)
    x10_long value2 = value;
    char *b;
    // start on the '\0', will predecrement so will not clobber it
    for (b=&buf[65] ; value2 != 0 ; value2/=radix) {
        *(--b) = numerals[::abs((int)(value2 % radix))];
    }
    if (value < 0) {
        *(--b) = '-';
    }
    return String::Steal(alloc_printf("%s",b));
}

String* LongNatives::toString(x10_long value) {
    return to_string(value);
}

x10_long LongNatives::parseLong(String* s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_long ans = strtoll(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        ((end-start) != s->length())) {
        throwException(NumberFormatException::_make(s));
    }
    return ans;
}

x10_long LongNatives::highestOneBit(x10_long x) {
    x |= (x >> 1);
    x |= (x >> 2);
    x |= (x >> 4);
    x |= (x >> 8);
    x |= (x >> 16);
    x |= (x >> 32);
    return x & ~(((x10_ulong)x) >> 1);
}

x10_long LongNatives::lowestOneBit(x10_long x) {
    return x & (-x);
}

x10_int LongNatives::numberOfLeadingZeros(x10_long x) {
    x |= (x >> 1);
    x |= (x >> 2);
    x |= (x >> 4);
    x |= (x >> 8);
    x |= (x >> 16);
    x |= (x >> 32);
    return bitCount(~x);
}

x10_int LongNatives::numberOfTrailingZeros(x10_long x) {
    return bitCount(~x & (x-1));
}

x10_int LongNatives::bitCount(x10_long x) {
    x10_ulong ux = (x10_ulong)x;
    ux = ux - ((ux >> 1) & 0x5555555555555555LL);
    ux = (ux & 0x3333333333333333LL) + ((ux >> 2) & 0x3333333333333333LL);
    ux = (ux & 0x0F0F0F0F0F0F0F0FLL) + ((ux >> 4) & 0x0F0F0F0F0F0F0F0FLL);
    ux = (ux & 0x00FF00FF00FF00FFLL) + ((ux >> 8) & 0x00FF00FF00FF00FFLL);
    ux = ux + (ux >> 16);
    ux = ux + (ux >> 32);
    return (x10_int)(ux & 0x7F);
}

x10_long LongNatives::rotateLeft(x10_long x, x10_int distance) {
    return (x << distance) | (((x10_ulong)x) >> (64 - distance));
}

x10_long LongNatives::rotateRight(x10_long x, x10_int distance) {
    return (((x10_ulong)x) >> distance) | (x << (64 - distance));
}

x10_long LongNatives::reverse(x10_long x) {
    x10_ulong ux = (x10_ulong)x;
    ux = ((ux & 0x5555555555555555LL) << 1) | ((ux >> 1) & 0x5555555555555555LL);
    ux = ((ux & 0x3333333333333333LL) << 2) | ((ux >> 2) & 0x3333333333333333LL);
    ux = ((ux & 0x0F0F0F0F0F0F0F0FLL) << 4) | ((ux >> 4) & 0x0F0F0F0F0F0F0F0FLL);
    return reverseBytes((x10_long)ux);
}

x10_long LongNatives::reverseBytes(x10_long x) {
    x10_ulong ux = x;
    x10_ulong ans = ux << 56; 
    ans |= (ux & 0xFF00L) << 40;
    ans |= (ux & 0xFF0000L) << 24;
    ans |= (ux & 0xFF000000L) << 8;
    ans |= (ux >> 8) & 0xFF000000L;
    ans |= (ux >> 24) & 0xFF0000L;
    ans |= (ux >> 40) & 0xFF00L;
    ans |= (ux >> 56);
    return (x10_long)ans;
}
// vim:tabstop=4:shiftwidth=4:expandtab
