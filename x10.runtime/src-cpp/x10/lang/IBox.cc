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

#include <x10rt.h>
#include <x10/lang/IBox.h>
#include <x10/lang/Comparable.h>

using namespace x10aux;

namespace x10 {
    namespace lang {

#define BOXED_PRIM_ITABLES(CPRIM,PRIMCLASS,UTILS)                       \
        class PRIMCLASS##_iboxthunk : IBox<CPRIM> {                       \
        public:                                                         \
            static Any::itable<PRIMCLASS##_iboxthunk> any_itable;         \
            static Comparable<CPRIM>::itable<PRIMCLASS##_iboxthunk> comparable_itable; \
                                                                        \
            /* Methods of Any */                                        \
            x10_boolean equals(ref<Any> arg0) {                         \
                return x10aux::equals(value, arg0);                     \
            }                                                           \
            x10_int hashCode() {                                        \
                return x10aux::hash_code(value);                        \
            }                                                           \
            ref<String> toString() {                                    \
                return x10aux::to_string(value);                        \
            }                                                           \
            ref<String> typeName() {                                    \
                return x10aux::type_name(value);                        \
            }                                                           \
            /* Methods of Comparable */                                 \
            x10_int compareTo(CPRIM arg0) {                             \
                return x10aux::UTILS::compareTo(value, arg0);           \
            }                                                           \
        };                                                              \
                                                                        \
                Any::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::any_itable(&PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
                Comparable<CPRIM>::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::comparable_itable(&PRIMCLASS##_iboxthunk::compareTo, &PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
                                                                        \
                itable_entry itable_##PRIMCLASS[3] = { itable_entry(&x10aux::getRTT<x10::lang::Any>, &PRIMCLASS##_iboxthunk::any_itable), \
                                                   itable_entry(&x10aux::getRTT<x10::lang::Comparable<CPRIM> >, &PRIMCLASS##_iboxthunk::comparable_itable), \
                                                   itable_entry(NULL, (void*)&x10aux::RuntimeType::PRIMCLASS##Type) }; \


        BOXED_PRIM_ITABLES(x10_boolean, Boolean, boolean_utils)
        BOXED_PRIM_ITABLES(x10_byte, Byte, byte_utils)
        BOXED_PRIM_ITABLES(x10_ubyte, UByte, byte_utils)
        BOXED_PRIM_ITABLES(x10_short, Short, short_utils)
        BOXED_PRIM_ITABLES(x10_ushort, UShort, short_utils)
        BOXED_PRIM_ITABLES(x10_char, Char, char_utils)
        BOXED_PRIM_ITABLES(x10_int, Int, int_utils)
        BOXED_PRIM_ITABLES(x10_uint, UInt, int_utils)
        BOXED_PRIM_ITABLES(x10_float, Float, float_utils)
        BOXED_PRIM_ITABLES(x10_long, Long, long_utils)
        BOXED_PRIM_ITABLES(x10_ulong, ULong, long_utils)
        BOXED_PRIM_ITABLES(x10_double, Double, double_utils)
            
    }
}

