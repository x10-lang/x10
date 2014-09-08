/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2012.
 */

#ifndef X10_LANG_RUNTIMENATIVES_H
#define X10_LANG_RUNTIMENATIVES_H

#include <x10aux/config.h>

namespace x10 {
    namespace io {
        class Reader;
        class Writer;
    }
    namespace util {
        template <class K, class V> class HashMap;
    }
}

namespace x10aux {
    extern volatile x10_int exitCode;
}
    

namespace x10 {
    namespace lang {
        class Reference;
        class String;
        
        class RuntimeNatives {
        public:
            static ::x10::io::Reader* execForRead(const char *command);
            static ::x10::io::Writer* execForWrite(const char *command);

            /* load environment variables into HashMap */
            static ::x10::util::HashMap< ::x10::lang::String*, ::x10::lang::String*>* loadenv();

            /* Exit with the given exit code */
            static void exit(x10_int code);

            /** Milliseconds since the Epoch: midnight, Jan 1, 1970. */
            static x10_long currentTimeMillis();

            /** Current value of the system timer, in nanoseconds.  May be rounded if system timer does not have nanosecond precision. */
            static x10_long nanoTime();

            /** Make an educated guess on how many CPUs are available. Intended to match Java's availableProcessors() */
            static x10_int availableProcessors();

            /** Converts a time in seconds to a string representation. */
            static ::x10::lang::String* timeToString(long seconds);

            /** Low-level println to stderr; intended only for low-level debugging of XRX */
            static void println(const char *msg);

            /** Low-level printf to stderr; intended only for low-level debugging of XRX */
            template<class T> static inline void printf(const char* fmt, const T& t) {
                fprintf(stderr, fmt, t);
            }
        };
    }
}

#endif /* X10_LANG_RUNTIMENATIVES_H */

// vim:tabstop=4:shiftwidth=4:expandtab
