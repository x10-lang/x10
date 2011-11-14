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

#ifndef X10AUX_SYSTEM_UTILS_H
#define X10AUX_SYSTEM_UTILS_H

#include <x10aux/config.h>
#include <stdio.h>

namespace x10aux {

    extern volatile x10_int exitCode;

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
