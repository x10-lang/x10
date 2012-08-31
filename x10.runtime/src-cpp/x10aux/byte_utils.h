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

#ifndef X10AUX_BYTE_UTILS_H
#define X10AUX_BYTE_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}


namespace x10aux {
    class byte_utils {
    public:
        static x10::lang::String* toString(x10_byte value, x10_int radix);
        static x10::lang::String* toString(x10_byte value);
        static x10::lang::String* toString(x10_ubyte value, x10_int radix);
        static x10::lang::String* toString(x10_ubyte value);
        static x10_byte parseByte(x10::lang::String* s, x10_int radix);
        static x10_byte parseByte(x10::lang::String* s) { return parseByte(s, 10); }
        static x10_ubyte parseUByte(x10::lang::String* s, x10_int radix);
        static x10_ubyte parseUByte(x10::lang::String* s) { return parseUByte(s, 10); }
        static x10_int compareTo(x10_byte v1, x10_byte v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
        static x10_int compareTo(x10_ubyte v1, x10_ubyte v2) {
            return v1 == v2 ? 0 : (v1 < v2 ? -1 : 1);
        }
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
