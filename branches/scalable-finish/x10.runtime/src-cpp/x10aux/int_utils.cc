#include <x10aux/int_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::int_utils::toString(x10_int value, x10_int radix) {
    assert(radix>=2);
    assert(radix<=36);
    static char numerals[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
                               'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                               'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                               'x', 'y', 'z' };
    // worst case is binary -- needs 32 digits and a '\0'
    char buf[33] = ""; //zeroes entire buffer (S6.7.8.21)
    x10_long value2 = 0;
    if (value<0) {
        value2 = 0x80000000;
        value &= 0x7FFFFFFF;
    }
    value2 += value;
    char *b;
    // start on the '\0', will predecrement so will not clobber it
    for (b=&buf[32] ; value2>0 ; value2/=radix) {
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
    (void) s;
    assert(false); /* FIXME: STUBBED NATIVE */
    return radix; /* Bogus, but use radix to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::parseInt(ref<String> s) {
    // FIXME: what about null?
    // FIXME: NumberFormatException?
    return atoi(nullCheck(s)->c_str());
}

x10_int x10aux::int_utils::highestOneBit(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::lowestOneBit(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::numberOfLeadingZeros(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::numberOfTrailingZeros(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::bitCount(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::rotateLeft(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::rotateRight(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::reverse(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}

x10_int x10aux::int_utils::signum(x10_int x) {
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
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
