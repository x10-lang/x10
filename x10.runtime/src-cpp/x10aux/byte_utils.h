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
        static const ref<x10::lang::String> toString(x10_byte value, x10_int radix);
        static const ref<x10::lang::String> toHexString(x10_byte value);
        static const ref<x10::lang::String> toOctalString(x10_byte value);
        static const ref<x10::lang::String> toBinaryString(x10_byte value);
        static const ref<x10::lang::String> toString(x10_byte value);
        static x10_byte parseByte(const ref<x10::lang::String>& s, x10_int radix);
        static x10_byte parseByte(const ref<x10::lang::String>& s);
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
