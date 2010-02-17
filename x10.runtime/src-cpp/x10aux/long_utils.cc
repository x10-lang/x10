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

#include <x10aux/config.h>

#include <x10aux/long_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>
#ifdef __CYGWIN__
extern "C" long long atoll(const char *);
#endif

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::long_utils::toString(x10_long value, x10_int radix) {
    (void) value; (void) radix;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
}

const ref<String> x10aux::long_utils::toHexString(x10_long value) {
    return x10aux::long_utils::toString(value, 16);
}

const ref<String> x10aux::long_utils::toOctalString(x10_long value) {
    return x10aux::long_utils::toString(value, 8);
}

const ref<String> x10aux::long_utils::toBinaryString(x10_long value) {
    return x10aux::long_utils::toString(value, 2);
}

const ref<String> x10aux::long_utils::toString(x10_long value) {
    return to_string(value);
}

x10_long x10aux::long_utils::parseLong(const ref<String>& s, x10_int radix) {
    (void) s;
    assert(false); /* FIXME: STUBBED NATIVE */
    return radix; /* Bogus, but use radix to avoid warning about unused parameter */
}

x10_long x10aux::long_utils::parseLong(const ref<String>& s) {
    // FIXME: what about null?
    // FIXME: NumberFormatException
    return atoll(nullCheck(s)->c_str());
}

x10_long x10aux::long_utils::highestOneBit(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_long x10aux::long_utils::lowestOneBit(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::long_utils::numberOfLeadingZeros(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::long_utils::numberOfTrailingZeros(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::long_utils::bitCount(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_long x10aux::long_utils::rotateLeft(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_long x10aux::long_utils::rotateRight(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_long x10aux::long_utils::reverse(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::long_utils::signum(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_long x10aux::long_utils::reverseBytes(x10_long x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}
// vim:tabstop=4:shiftwidth=4:expandtab
