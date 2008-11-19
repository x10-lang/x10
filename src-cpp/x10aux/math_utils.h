#ifndef X10AUX_MATH_UTILS_H
#define X10AUX_MATH_UTILS_H

#include <x10aux/config.h>

namespace x10aux {
    class math_utils {
    public:
        static x10_double pow(x10_double x, x10_double y);
        static x10_double exp(x10_double x);
        static x10_double cos(x10_double x);
        static x10_double sin(x10_double x);
        static x10_double sqrt(x10_double x);
    };
}

#endif
