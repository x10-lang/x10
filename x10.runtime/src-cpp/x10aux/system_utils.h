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

#include <cstdio>

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 { namespace lang {
    class Reference;
    class String;
} }

namespace x10aux {


    extern volatile x10_int exitCode;

    namespace system_utils {

        void exit(x10_int code);

        /** Milliseconds since the Epoch: midnight, Jan 1, 1970. */
        x10_long currentTimeMillis();

        /** Current value of the system timer, in nanoseconds.  May be rounded if system timer does not have nanosecond precision. */
        x10_long nanoTime();

        /** Low-level println to stderr; intended only for low-level debugging of XRX */
        void println(const char *msg);

        /** Low-level printf to stderr; intended only for low-level debugging of XRX */
        template<class T> static inline void printf(const char* fmt, const T& t) {
            fprintf(stderr, fmt, t);
        }
    }

    /******* hash_code ********/
    inline static x10_int identity_hash_code (x10aux::ref<x10::lang::Reference> ptr)
    {

        // STEP 2: Combine the bits of the pointer into a 32 bit integer.
        //         Note: intentionally not doing some type-punning pointer thing here as
        //         the behavior of that is somewhat underdefined and tends to expose
        //         "interesting" behavior in C++ compilers (especially at high optimization
        //         level).
        uint64_t v2 = (uint64_t)&*ptr;
        x10_int lower = (x10_int)(v2 & 0xffffffff);
        x10_int upper = (x10_int)(v2 >> 32);
        x10_int hc = lower ^ upper;
        return hc; 
    }

    x10aux::ref<x10::lang::String> identity_type_name (x10aux::ref<x10::lang::Reference> ptr);

    x10aux::ref<x10::lang::String> identity_to_string (x10aux::ref<x10::lang::Reference> ptr);
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
