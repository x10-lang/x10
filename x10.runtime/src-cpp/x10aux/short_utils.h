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

#ifndef X10AUX_SHORT_UTILS_H
#define X10AUX_SHORT_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}


namespace x10aux {
    class short_utils {
    public:
        static x10::lang::String* toString(x10_short value, x10_int radix);
        static x10::lang::String* toString(x10_short value);
        static x10::lang::String* toString(x10_ushort value, x10_int radix);
        static x10::lang::String* toString(x10_ushort value);
        static x10_short parseShort(x10::lang::String* s, x10_int radix);
        static x10_short parseShort(x10::lang::String* s) { return parseShort(s, 10); }
        static x10_ushort parseUShort(x10::lang::String* s, x10_int radix);
        static x10_ushort parseUShort(x10::lang::String* s) { return parseUShort(s, 10); }
        static x10_short reverseBytes(x10_short value);
        static x10_int compareTo(x10_short v1, x10_short v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
        static x10_int compareTo(x10_ushort v1, x10_ushort v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
