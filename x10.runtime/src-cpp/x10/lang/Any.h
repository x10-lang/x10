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

#ifndef X10_LANG_ANY_H
#define X10_LANG_ANY_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/ref.h>
#include <x10/lang/Reference.h>

#define X10_LANG_PLACE_H_NODEPS
#include <x10/lang/Place.struct_h>
#undef X10_LANG_PLACE_H_NODEPS

namespace x10 {
    namespace lang {
        class Object;
        class String;
        
        class Any {
        public:
            RTT_H_DECLS_INTERFACE

            template <class I> struct itable {
                itable(x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>),
                       x10_int (I::*hashCode)(),
                       x10aux::ref<x10::lang::String> (I::*toString)(),
                       x10aux::ref<x10::lang::String> (I::*typeName)()) : equals(equals), hashCode(hashCode),
                                                                          toString(toString), typeName(typeName) {}

                x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>);
                x10_int (I::*hashCode)();
                x10aux::ref<x10::lang::String> (I::*toString)();
                x10aux::ref<x10::lang::String> (I::*typeName)();
            };

            static void _serialize(x10aux::ref<Any> this_,
                                   x10aux::serialization_buffer &buf) {
                x10::lang::Reference::_serialize(this_, buf);
            }

            template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf) {
                return x10::lang::Reference::_deserialize<T>(buf);
            }
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
