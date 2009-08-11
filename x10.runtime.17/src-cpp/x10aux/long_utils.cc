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
    (void) value;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
}

const ref<String> x10aux::long_utils::toOctalString(x10_long value) {
    (void) value;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
}

const ref<String> x10aux::long_utils::toBinaryString(x10_long value) {
    (void) value;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
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
