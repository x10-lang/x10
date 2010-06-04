#include <x10aux/float_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>
#include <math.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

/* Use to move bits between x10_float/x10_int without confusing the compiler */
typedef union TypePunner {
    x10_int i;
    x10_float f;
} TypePunner;

const ref<String> x10aux::float_utils::toHexString(x10_float value) {
    (void) value;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
}

const ref<String> x10aux::float_utils::toString(x10_float value) {
    return to_string(value);
}

x10_float x10aux::float_utils::parseFloat(const ref<String>& s) {
    // FIXME: what about null?
    // FIXME: NumberFormatException
    return strtof(nullCheck(s)->c_str(), NULL);
}

x10_boolean x10aux::float_utils::isNaN(x10_float x) {
    return x10aux::math::isnan(x);
}

x10_boolean x10aux::float_utils::isInfinite(x10_float x) {
    return x10aux::math::isinf(x);
}

x10_int x10aux::float_utils::toIntBits(x10_float x) {
    // Check for NaN and return canonical NaN value
    return isNaN(x) ? 0x7fc00000 : toRawIntBits(x);
}

x10_int x10aux::float_utils::toRawIntBits(x10_float x) {
    TypePunner tmp;
    tmp.f = x;
    return tmp.i;
}

x10_float x10aux::float_utils::fromIntBits(x10_int x) {
    TypePunner tmp;
    tmp.i = x;
    return tmp.f;
}
// vim:tabstop=4:shiftwidth=4:expandtab
