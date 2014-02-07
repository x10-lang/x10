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

#ifndef X10_LANG_UINT_H
#define X10_LANG_UINT_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        class String;
        
        class UIntNatives {
        public:
            static String* toString(x10_uint value, x10_int radix);
            static String* toString(x10_uint value);
            static x10_uint parseUInt(String* s, x10_int radix);
            static x10_uint parseUInt(String* s) { return parseUInt(s, 10); }
            static inline x10_int compareTo(x10_uint v1, x10_uint v2) {
                return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
            }
        };
    }
}

#endif /* X10_LANG_UINT_H */

/*
 * Empty header file for NativeRep class that doesn't require a
 * C++ implementation
 */
