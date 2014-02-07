/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifndef X10_MATH_LANG_MATHNATIVES_H
#define X10_MATH_LANG_MATHNATIVES_H

#include <x10aux/config.h>
#include <complex>

namespace x10 {
    namespace lang {
        class MathNatives {
        public:
            static inline x10_double pow(x10_double x, x10_double y) { return ::pow(x,y); }
            static inline x10_complex pow(x10_complex x, x10_complex y) { return std::pow(x,y); }
            static inline x10_double exp(x10_double x) { return ::exp(x); }
            static inline x10_complex exp(x10_complex x) { return std::exp(x); }
            static inline x10_double expm1(x10_double x) { return ::expm1(x); }
            static inline x10_double cos(x10_double x) { return ::cos(x); }
            static inline x10_complex cos(x10_complex x) { return std::cos(x); }
            static inline x10_double sin(x10_double x) { return ::sin(x); }
            static inline x10_complex sin(x10_complex x) { return std::sin(x); }
            static inline x10_double tan(x10_double x) { return ::tan(x); }
            static inline x10_complex tan(x10_complex x) { return std::tan(x); }
            static inline x10_double acos(x10_double x) { return ::acos(x); }
            static inline x10_double asin(x10_double x) { return ::asin(x); }
            static inline x10_double atan(x10_double x) { return ::atan(x); }
            static inline x10_double atan2(x10_double x, x10_double y) { return ::atan2(x, y); }
            static inline x10_double cosh(x10_double x) { return ::cosh(x); }
            static inline x10_complex cosh(x10_complex x) { return std::cosh(x); }
            static inline x10_double sinh(x10_double x) { return ::sinh(x); }
            static inline x10_complex sinh(x10_complex x) { return std::sinh(x); }
            static inline x10_double tanh(x10_double x) { return ::tanh(x); }
            static inline x10_complex tanh(x10_complex x) { return std::tanh(x); }
            static inline x10_double log(x10_double x) { return ::log(x); }
            static inline x10_complex log(x10_complex x) { return std::log(x); }
            static inline x10_double log10(x10_double x) { return ::log10(x); }
            static inline x10_double log1p(x10_double x) { return ::log1p(x); }
            static inline x10_double sqrt(x10_double x) { return ::sqrt(x); }
            static inline x10_double cbrt(x10_double x) { return ::cbrt(x); }
            static inline x10_double erf(x10_double x) { return ::erf(x); }
            static inline x10_double erfc(x10_double x) { return ::erfc(x); }
            static inline x10_double hypot(x10_double x, x10_double y) { return ::hypot(x, y); }
            static inline x10_double ceil(x10_double x) { return ::ceil(x); }
            static inline x10_double floor(x10_double x) { return ::floor(x); }
            static inline x10_double round(x10_double x) { return ::round(x); }
            static inline x10_double copysign(x10_double x, x10_double y) { return ::copysign(x, y); }
        };
    }
}

#endif /* X10_MATH_LANG_MATHNATIVES_H */

// vim:tabstop=4:shiftwidth=4:expandtab
