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

#ifndef X10AUX_BOOLEAN_UTILS_H
#define X10AUX_BOOLEAN_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}


namespace x10aux {
    class boolean_utils {
    public:
        static x10::lang::String* toString(x10_boolean value);
        static x10_boolean parseBoolean(const x10::lang::String* s);
        static x10_int compareTo(x10_boolean v1, x10_boolean v2) {
            return v1 == v2 ? 0 : (v1 ? 1 : -1);
        }
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
