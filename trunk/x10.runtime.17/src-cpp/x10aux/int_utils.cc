#include <x10aux/int_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>                    \

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::int_utils::toString(x10_int value, x10_int radix) {
    (void) value; (void) radix;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
}

const ref<String> x10aux::int_utils::toHexString(x10_int value) {
    (void) value;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
}

const ref<String> x10aux::int_utils::toOctalString(x10_int value) {
    (void) value;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
}

const ref<String> x10aux::int_utils::toBinaryString(x10_int value) {
    (void) value;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
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
    return atoi(s->c_str());
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
    assert(false); /* FIXME: STUBBED NATIVE */
    return x; /* Bogus, but use x to avoid warning about unused parameter */
}
// vim:tabstop=4:shiftwidth=4:expandtab
