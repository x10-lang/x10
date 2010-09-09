#include <x10aux/byte_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>                    \

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::byte_utils::toString(x10_byte value, x10_int radix) {
    (void) value; (void) radix;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
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

x10_byte x10aux::byte_utils::parseByte(const ref<String>& s, x10_int radix) {
    (void) s;
    assert(false); /* FIXME: STUBBED NATIVE */
    return radix; /* Bogus, but use radix to avoid warning about unused parameter */
}

x10_byte x10aux::byte_utils::parseByte(const ref<String>& s) {
    (void) s;
    assert(false); /* FIXME: STUBBED NATIVE */
    return 0; 
}

// vim:tabstop=4:shiftwidth=4:expandtab
