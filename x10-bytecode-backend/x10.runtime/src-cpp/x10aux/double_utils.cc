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

#include <x10aux/double_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>
#include <math.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

/* Use to move bits between x10_long/x10_double without confusing the compiler */
typedef union TypePunner {
    x10_long l;
    x10_double d;
} TypePunner;

const ref<String> x10aux::double_utils::toHexString(x10_double value) {
    (void) value;
    assert(false); /* FIXME: STUBBED NATIVE */
    return null;
}

const ref<String> x10aux::double_utils::toString(x10_double value) {
    return to_string(value);
}

x10_double x10aux::double_utils::parseDouble(const ref<String>& s) {
    // FIXME: what about null?
    // FIXME: NumberFormatException
    return strtod(nullCheck(s)->c_str(), NULL);
}

x10_boolean x10aux::double_utils::isNaN(x10_double x) {
    return x10aux::math::isnan(x);
}

x10_boolean x10aux::double_utils::isInfinite(x10_double x) {
    return x10aux::math::isinf(x);
}

x10_long x10aux::double_utils::toLongBits(x10_double x) {
    return isNaN(x) ? 0x7ff8000000000000LL : toRawLongBits(x);
}

x10_long x10aux::double_utils::toRawLongBits(x10_double x) {
    TypePunner tmp;
    tmp.d = x;
    return tmp.l;
}

x10_double x10aux::double_utils::fromLongBits(x10_long x) {
    TypePunner tmp;
    tmp.l = x;
    return tmp.d;
}
// vim:tabstop=4:shiftwidth=4:expandtab
