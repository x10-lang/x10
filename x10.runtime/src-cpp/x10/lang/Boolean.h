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

#ifndef X10_LANG_BOOLEAN_H
#define X10_LANG_BOOLEAN_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {

        class String;
        
        class BooleanNatives {
        public:
            static ::x10::lang::String* toString(x10_boolean value);
            static x10_boolean parseBoolean(String* s);
            static inline x10_int compareTo(x10_boolean v1, x10_boolean v2) {
                return v1 == v2 ? 0 : (v1 ? 1 : -1);
            }
        };
    }
}

#endif /* X10_LANG_BOOLEAN_H */
