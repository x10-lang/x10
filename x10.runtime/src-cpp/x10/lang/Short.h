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

#ifndef X10_LANG_SHORT_H
#define X10_LANG_SHORT_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        class String;
        
        class ShortNatives {
        public:
            static String* toString(x10_short value, x10_int radix);
            static String* toString(x10_short value);
            static x10_short parseShort(String* s, x10_int radix);
            static x10_short parseShort(String* s) { return parseShort(s, 10); }
            static x10_short reverseBytes(x10_short value);
            static inline x10_int compareTo(x10_short v1, x10_short v2) {
                return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
        };
    }
}

#endif /* X10_LANG_SHORT_H */
