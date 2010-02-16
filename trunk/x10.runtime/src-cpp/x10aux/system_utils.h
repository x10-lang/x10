#ifndef X10AUX_SYSTEM_UTILS_H
#define X10AUX_SYSTEM_UTILS_H

#include <x10aux/config.h>

namespace x10aux {

    extern x10_int exitCode;

    class system_utils {
    public:
        static void exit(x10_int code);

        /** Milliseconds since the Epoch: midnight, Jan 1, 1970. */
        static x10_long currentTimeMillis();

        /** Current value of the system timer, in nanoseconds.  May be rounded if system timer does not have nanosecond precision. */
        static x10_long nanoTime();

        /** Low-level println to stderr; intended only for low-level debugging of XRX */
        static void println(const char *msg);

        /** Low-level printf to stderr; intended only for low-level debugging of XRX */
        template<class T> static void printf(const char* fmt, const T& t) {
            fprintf(stderr, fmt, t);
        }

    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
