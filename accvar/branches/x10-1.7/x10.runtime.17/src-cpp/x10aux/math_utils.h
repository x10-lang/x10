#ifndef X10AUX_MATH_UTILS_H
#define X10AUX_MATH_UTILS_H

#include <x10aux/config.h>

namespace x10aux {
    class math_utils {
    public:
        static x10_double pow(x10_double x, x10_double y);
        static x10_double exp(x10_double x);
        static x10_double expm1(x10_double x);
        static x10_double cos(x10_double x);
        static x10_double sin(x10_double x);
        static x10_double tan(x10_double x);
        static x10_double acos(x10_double x);
        static x10_double asin(x10_double x);
        static x10_double atan(x10_double x);
        static x10_double atan2(x10_double x, x10_double y);
        static x10_double cosh(x10_double x);
        static x10_double sinh(x10_double x);
        static x10_double tanh(x10_double x);
        static x10_double log(x10_double x);
        static x10_double log10(x10_double x);
        static x10_double log1p(x10_double x);
        static x10_double sqrt(x10_double x);
        static x10_double cbrt(x10_double x);
        static x10_double hypot(x10_double x, x10_double y);
        static x10_double ceil(x10_double x);
        static x10_double floor(x10_double x);
        static x10_double round(x10_double x);
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
