#ifndef X10AUX_MATH_UTILS_H
#define X10AUX_MATH_UTILS_H

#include <x10aux/config.h>

namespace x10aux {
    class math_utils {
    public:
        static x10_double pow_mu(x10_double x, x10_double y);
        static x10_double exp_mu(x10_double x);
        static x10_double cos_mu(x10_double x);
        static x10_double sin_mu(x10_double x);
        static x10_double sqrt_mu(x10_double x);
    };
}

#endif
