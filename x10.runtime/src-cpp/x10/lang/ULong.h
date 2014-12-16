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

#ifndef X10_LANG_ULONG_H
#define X10_LANG_ULONG_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        class String;
        
        class ULongNatives {
        public:
            static String* toString(x10_ulong value, x10_int radix);
            static String* toString(x10_ulong value);
            static x10_ulong parseULong(String* s, x10_int radix);
            static x10_ulong parseULong(String* s) { return parseULong(s, 10); }
            static x10_int compareTo(x10_ulong v1, x10_ulong v2) {
                return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
            }
        };
    }
}

#endif /* X10_LANG_ULONG_H */

/*
 * Empty header file for NativeRep class that doesn't require a
 * C++ implementation
 */
