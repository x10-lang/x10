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

#ifndef X10AUX_FUN_UTILS_H
#define X10AUX_FUN_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10/lang/Reference.h>

namespace x10aux {
    class serialization_buffer;

    class AnyFun {
    public:

        static void _serialize(ref<AnyFun> this_,
                               x10aux::serialization_buffer &buf) {
            x10::lang::Reference::_serialize(this_, buf);
        }

        template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf) {
            return x10::lang::Reference::_deserialize<T>(buf);
        }
    };
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
