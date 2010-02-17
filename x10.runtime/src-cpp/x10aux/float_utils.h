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

#ifndef X10AUX_FLOAT_UTILS_H
#define X10AUX_FLOAT_UTILS_H

#include <x10aux/config.h>
#include <x10aux/math.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    class float_utils {
    public:
        static const ref<x10::lang::String> toHexString(x10_float value);
        static const ref<x10::lang::String> toString(x10_float value);
        static x10_float parseFloat(const ref<x10::lang::String>& s);
        static x10_boolean isNaN(x10_float value);
        static x10_boolean isInfinite(x10_float value);
        static x10_int toIntBits(x10_float value);
        static x10_int toRawIntBits(x10_float value);
        static x10_float fromIntBits(x10_int value);
    };

    inline x10_float mod(x10_float a, x10_float b) {
        x10_float d = (x10_float)::fmodf(a, b);
        return d;
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
