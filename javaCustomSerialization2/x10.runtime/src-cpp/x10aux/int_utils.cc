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

#include <x10aux/int_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>
#include <x10/lang/NumberFormatException.h>
#include <errno.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::int_utils::toString(x10_int value, x10_int radix) {
    if (0 == value) return String::Lit("0");
    assert(radix>=2);
    assert(radix<=36);
    static char numerals[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
                               'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                               'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                               'x', 'y', 'z' };
    // worst case is binary of Int.MIN_VALUE -- - plus 32 digits and a '\0'
    char buf[34] = ""; //zeroes entire buffer (S6.7.8.21)
    x10_long value2 = 0;
    if (value<0) {
        value2 = 0x80000000;
        value &= 0x7FFFFFFF;
    }
    value2 += value;
    char *b;
    // start on the '\0', will predecrement so will not clobber it
    for (b=&buf[33] ; value2>0 ; value2/=radix) {
        *(--b) = numerals[value2 % radix];
    }
    return String::Steal(alloc_printf("%s",b));
}

const ref<String> x10aux::int_utils::toHexString(x10_int value) {
    return x10aux::int_utils::toString(value, 16);
}

const ref<String> x10aux::int_utils::toOctalString(x10_int value) {
    return x10aux::int_utils::toString(value, 8);
}

const ref<String> x10aux::int_utils::toBinaryString(x10_int value) {
    return x10aux::int_utils::toString(value, 2);
}

const ref<String> x10aux::int_utils::toString(x10_int value) {
    return to_string(value);
}

x10_int x10aux::int_utils::parseInt(ref<String> s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_int ans = strtol(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return ans;
}

x10_uint x10aux::int_utils::parseUInt(ref<String> s, x10_int radix) {
    const char *start = nullCheck(s)->c_str();
    char *end;
    errno = 0;
    x10_uint ans = strtoul(start, &end, radix);
    if (errno == ERANGE || (errno != 0 && ans == 0) ||
        ((end-start) != s->length())) {
        x10aux::throwException(x10::lang::NumberFormatException::_make(s));
    }
    return ans;
}

x10_int x10aux::int_utils::highestOneBit(x10_int x) {
    x |= (x >> 1);
    x |= (x >> 2);
    x |= (x >> 4);
    x |= (x >> 8);
    x |= (x >> 16);
    return x & ~(((x10_uint)x) >> 1);
}

x10_int x10aux::int_utils::lowestOneBit(x10_int x) {
    return x & (-x);
}

x10_int x10aux::int_utils::numberOfLeadingZeros(x10_int x) {
    x |= (x >> 1);
    x |= (x >> 2);
    x |= (x >> 4);
    x |= (x >> 8);
    x |= (x >> 16);
    return bitCount(~x);
}

x10_int x10aux::int_utils::numberOfTrailingZeros(x10_int x) {
    return bitCount(~x & (x-1));
}

x10_int x10aux::int_utils::bitCount(x10_int x) {
    x10_uint ux = (x10_uint)x;
    ux = ux - ((ux >> 1) & 0x55555555);
    ux = (ux & 0x33333333) + ((ux >> 2) & 0x33333333);
    ux = (ux + (ux >> 4)) & 0x0F0F0F0F;
    ux = ux + (ux >> 8);
    ux = ux + (ux >> 16);
    return (x10_int)(ux & 0x3F);
}

x10_int x10aux::int_utils::rotateLeft(x10_int x, x10_int distance) {
    return (x << distance) | (((x10_uint)x) >> (32 - distance));
}

x10_int x10aux::int_utils::rotateRight(x10_int x, x10_int distance) {
    return (((x10_uint)x) >> distance) | (x << (32 - distance));
}

x10_int x10aux::int_utils::reverse(x10_int x) {
    x10_uint ux = (x10_uint)x;
    ux = ((ux & 0x55555555) << 1) | ((ux >> 1) & 0x55555555);
    ux = ((ux & 0x33333333) << 2) | ((ux >> 2) & 0x33333333);
    ux = ((ux & 0x0F0F0F0F) << 4) | ((ux >> 4) & 0x0F0F0F0F);
    ux = (ux << 24) | ((ux & 0xFF00) << 8) | ((ux >> 8) & 0xFF00) | (ux >> 24);
    return (x10_int)ux;
}

x10_int x10aux::int_utils::reverseBytes(x10_int x) {
    x10_long value = 0;
    if (x<0) {
        value = 0x80000000;
        x &= 0x7FFFFFFF;
    }
    value += x;
    x10_long b0 = value & 0x000000FF;
    x10_long b1 = value & 0x0000FF00;
    x10_long b2 = value & 0x00FF0000;
    x10_long b3 = value & 0xFF000000;
    // reverse bytes
    b0 <<= 24; b1 <<= 8; b2 >>= 8; b3 >>= 24;
    return b0 | b1 | b2 | b3;
}

// vim:tabstop=4:shiftwidth=4:expandtab
