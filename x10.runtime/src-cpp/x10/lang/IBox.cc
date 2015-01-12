/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#include <x10rt.h>
#include <x10/lang/IBox.h>
#include <x10/lang/Comparable.h>
#include <x10/lang/Arithmetic.h>
#include <x10/lang/Bitwise.h>
#include <x10/util/Ordered.h>

using namespace x10aux;

namespace x10 {
    namespace lang {

        class Boolean_iboxthunk : IBox<x10_boolean> { /* implements Any, Comparable[Boolean] */
        public:
            static Any::itable<Boolean_iboxthunk> any_itable;
            static Comparable<x10_boolean>::itable<Boolean_iboxthunk> comparable_itable;

            /* Methods of Any */
            x10_boolean equals(Any* arg0) {
                return x10aux::equals(value, arg0);
            }
            x10_int hashCode() {
                return x10aux::hash_code(value);
            }
            String* toString() {
                return x10aux::to_string(value);
            }
            String* typeName() {
                return x10aux::type_name(value);
            }
            /* Methods of Comparable */
            x10_int compareTo(x10_boolean arg0) {
                return BooleanNatives::compareTo(value, arg0);
            }
        };

        Any::itable<Boolean_iboxthunk> Boolean_iboxthunk::any_itable(&Boolean_iboxthunk::equals, &Boolean_iboxthunk::hashCode, &Boolean_iboxthunk::toString, &Boolean_iboxthunk::typeName);
        Comparable<x10_boolean>::itable<Boolean_iboxthunk> Boolean_iboxthunk::comparable_itable(&Boolean_iboxthunk::compareTo, &Boolean_iboxthunk::equals, &Boolean_iboxthunk::hashCode, &Boolean_iboxthunk::toString, &Boolean_iboxthunk::typeName);

        itable_entry itable_Boolean[3] = { itable_entry(&x10aux::getRTT<x10::lang::Any>, &Boolean_iboxthunk::any_itable),
                                           itable_entry(&x10aux::getRTT<x10::lang::Comparable<x10_boolean> >, &Boolean_iboxthunk::comparable_itable),
                                           itable_entry(NULL, (void*)"x10::lang::Boolean") };

        class Char_iboxthunk : IBox<x10_char> { /* implements Any, Comparable[Char], Ordered[Char] */
        public:
            static Any::itable<Char_iboxthunk> any_itable;
            static Comparable<x10_char>::itable<Char_iboxthunk> comparable_itable;
            static x10::util::Ordered<x10_char>::itable<Char_iboxthunk> ordered_itable;

            /* Methods of Any */
            x10_boolean equals(Any* arg0) {
                return x10aux::equals(value, arg0);
            }
            x10_int hashCode() {
                return x10aux::hash_code(value);
            }
            String* toString() {
                return x10aux::to_string(value);
            }
            String* typeName() {
                return x10aux::type_name(value);
            }
            /* Methods of Comparable */
            x10_int compareTo(x10_char arg0) {
                return CharNatives::compareTo(value, arg0);
            }
            /* Methods of Ordered */
            x10_boolean __lt(x10_char arg0) {
                return value<arg0;
            }
            x10_boolean __gt(x10_char arg0) {
                return value>arg0;
            }
            x10_boolean __le(x10_char arg0) {
                return value<=arg0;
            }
            x10_boolean __ge(x10_char arg0) {
                return value>=arg0;
            }
        };

        Any::itable<Char_iboxthunk> Char_iboxthunk::any_itable(&Char_iboxthunk::equals, &Char_iboxthunk::hashCode, &Char_iboxthunk::toString, &Char_iboxthunk::typeName);
        Comparable<x10_char>::itable<Char_iboxthunk> Char_iboxthunk::comparable_itable(&Char_iboxthunk::compareTo, &Char_iboxthunk::equals, &Char_iboxthunk::hashCode, &Char_iboxthunk::toString, &Char_iboxthunk::typeName);
        x10::util::Ordered<x10_char>::itable<Char_iboxthunk> Char_iboxthunk::ordered_itable(&Char_iboxthunk::equals, &Char_iboxthunk::hashCode, &Char_iboxthunk::__lt, &Char_iboxthunk::__le, &Char_iboxthunk::__gt, &Char_iboxthunk::__ge, &Char_iboxthunk::toString, &Char_iboxthunk::typeName);

        itable_entry itable_Char[4] = { itable_entry(&x10aux::getRTT<x10::lang::Any>, &Char_iboxthunk::any_itable),
                                        itable_entry(&x10aux::getRTT<x10::lang::Comparable<x10_char> >, &Char_iboxthunk::comparable_itable),
                                        itable_entry(&x10aux::getRTT<x10::util::Ordered<x10_char> >, &Char_iboxthunk::ordered_itable),
                                        itable_entry(NULL, (void*)"x10::lang::Char") };

        
        class Complex_iboxthunk : IBox<x10_complex> { /* implements Any, Arithmetic[Complex] */
        public:
            static Any::itable<Complex_iboxthunk> any_itable;
            static Arithmetic<x10_complex>::itable<Complex_iboxthunk> arithmetic_itable;

            /* Methods of Any */
            x10_boolean equals(Any* arg0) {
                return x10aux::equals(value, arg0);
            }
            x10_int hashCode() {
                return x10aux::hash_code(value);
            }
            String* toString() {
                return x10aux::to_string(value);
            }
            String* typeName() {
                return x10aux::type_name(value);
            }
            /* Methods of Arithmetic */
            x10_complex __plus() {
                return value;
            }
            x10_complex __minus() {
                return -value;
            }
            x10_complex __plus(x10_complex arg0) {
                return value + arg0;
            }
            x10_complex __minus(x10_complex arg0) {
                return value - arg0;
            }
            x10_complex __times(x10_complex arg0) {
                return value * arg0;
            }
            x10_complex __over(x10_complex arg0) {
                return value / arg0;
            }
        };

        Any::itable<Complex_iboxthunk> Complex_iboxthunk::any_itable(&Complex_iboxthunk::equals, &Complex_iboxthunk::hashCode, &Complex_iboxthunk::toString, &Complex_iboxthunk::typeName);
        Arithmetic<x10_complex>::itable<Complex_iboxthunk> Complex_iboxthunk::arithmetic_itable(&Complex_iboxthunk::equals, &Complex_iboxthunk::hashCode, &Complex_iboxthunk::__times, &Complex_iboxthunk::__plus, &Complex_iboxthunk::__plus, &Complex_iboxthunk::__minus, &Complex_iboxthunk::__minus, &Complex_iboxthunk::__over, &Complex_iboxthunk::toString, &Complex_iboxthunk::typeName);

        itable_entry itable_Complex[3] = { itable_entry(&x10aux::getRTT<x10::lang::Any>, &Complex_iboxthunk::any_itable),
                                           itable_entry(&x10aux::getRTT<x10::lang::Arithmetic<x10_complex> >, &Complex_iboxthunk::arithmetic_itable),
                                           itable_entry(NULL, (void*)"x10::lang::Complex") };

        
#define BOXED_PRIM_ITABLES_CAO(CPRIM,PRIMCLASS,UTILS)                                  \
        class PRIMCLASS##_iboxthunk : IBox<CPRIM> { /* implements Any, Comparable[PRIMCLASS], Arithmetic[PRIMCLASS], Ordered[PRIMCLASS] */ \
        public:                                                                        \
            static Any::itable<PRIMCLASS##_iboxthunk> any_itable;                      \
            static Comparable<CPRIM>::itable<PRIMCLASS##_iboxthunk> comparable_itable; \
            static Arithmetic<CPRIM>::itable<PRIMCLASS##_iboxthunk> arithmetic_itable; \
            static x10::util::Ordered<CPRIM>::itable<PRIMCLASS##_iboxthunk> ordered_itable;       \
                                                                        \
            /* Methods of Any */                                        \
            x10_boolean equals(Any* arg0) {                             \
                return x10aux::equals(value, arg0);                     \
            }                                                           \
            x10_int hashCode() {                                        \
                return x10aux::hash_code(value);                        \
            }                                                           \
            String* toString() {                                        \
                return x10aux::to_string(value);                        \
            }                                                           \
            String* typeName() {                                        \
                return x10aux::type_name(value);                        \
            }                                                           \
            /* Methods of Comparable */                                 \
            x10_int compareTo(CPRIM arg0) {                             \
                return UTILS::compareTo(value, arg0);                   \
            }                                                           \
            /* Methods of Arithmetic */                                 \
            CPRIM __plus() {                                            \
                return value;                                           \
            }                                                           \
            CPRIM __minus() {                                           \
                return (CPRIM)(-value);                                 \
            }                                                           \
            CPRIM __plus(CPRIM arg0) {                                  \
                return (CPRIM)(value+arg0);                             \
            }                                                           \
            CPRIM __minus(CPRIM arg0) {                                 \
                return (CPRIM)(value-arg0);                             \
            }                                                           \
            CPRIM __times(CPRIM arg0) {                                 \
                return (CPRIM)(value*arg0);                             \
            }                                                           \
            CPRIM __over(CPRIM arg0) {                                  \
                return (CPRIM)(value/arg0);                             \
            }                                                           \
            /* Methods of Ordered */                                    \
            x10_boolean __lt(CPRIM arg0) {                              \
                return value<arg0;                                      \
            }                                                           \
            x10_boolean __gt(CPRIM arg0) {                              \
                return value>arg0;                                      \
            }                                                           \
            x10_boolean __le(CPRIM arg0) {                              \
                return value<=arg0;                                     \
            }                                                           \
            x10_boolean __ge(CPRIM arg0) {                              \
                return value>=arg0;                                     \
            }                                                           \
        };                                                              \
                                                                        \
        Any::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::any_itable(&PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
        Comparable<CPRIM>::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::comparable_itable(&PRIMCLASS##_iboxthunk::compareTo, &PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
        Arithmetic<CPRIM>::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::arithmetic_itable(&PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::__times, &PRIMCLASS##_iboxthunk::__plus, &PRIMCLASS##_iboxthunk::__plus, &PRIMCLASS##_iboxthunk::__minus, &PRIMCLASS##_iboxthunk::__minus, &PRIMCLASS##_iboxthunk::__over, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
        x10::util::Ordered<CPRIM>::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::ordered_itable(&PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::__lt, &PRIMCLASS##_iboxthunk::__le, &PRIMCLASS##_iboxthunk::__gt, &PRIMCLASS##_iboxthunk::__ge, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
                                                                \
        itable_entry itable_##PRIMCLASS[5] = { itable_entry(&x10aux::getRTT<x10::lang::Any>, &PRIMCLASS##_iboxthunk::any_itable), \
                                               itable_entry(&x10aux::getRTT<x10::lang::Comparable<CPRIM> >, &PRIMCLASS##_iboxthunk::comparable_itable), \
                                               itable_entry(&x10aux::getRTT<x10::lang::Arithmetic<CPRIM> >, &PRIMCLASS##_iboxthunk::arithmetic_itable), \
                                               itable_entry(&x10aux::getRTT<x10::util::Ordered<CPRIM> >, &PRIMCLASS##_iboxthunk::ordered_itable), \
                                               itable_entry(NULL, (void*)"x10.lang.PRIMCLASS") }; \

#define BOXED_PRIM_ITABLES_CABO(CPRIM,PRIMCLASS,UTILS)                                 \
        class PRIMCLASS##_iboxthunk : IBox<CPRIM> { /* implements Any, Comparable[PRIMCLASS], Arithmetic[PRIMCLASS], Bitwise[PRIMCLASS], Ordered[PRIMCLASS] */ \
        public:                                                                        \
            static Any::itable<PRIMCLASS##_iboxthunk> any_itable;                      \
            static Comparable<CPRIM>::itable<PRIMCLASS##_iboxthunk> comparable_itable; \
            static Arithmetic<CPRIM>::itable<PRIMCLASS##_iboxthunk> arithmetic_itable; \
            static Bitwise<CPRIM>::itable<PRIMCLASS##_iboxthunk> bitwise_itable; \
            static x10::util::Ordered<CPRIM>::itable<PRIMCLASS##_iboxthunk> ordered_itable;       \
                                                                        \
            /* Methods of Any */                                        \
            x10_boolean equals(Any* arg0) {                             \
                return x10aux::equals(value, arg0);                     \
            }                                                           \
            x10_int hashCode() {                                        \
                return x10aux::hash_code(value);                        \
            }                                                           \
            String* toString() {                                        \
                return x10aux::to_string(value);                        \
            }                                                           \
            String* typeName() {                                        \
                return x10aux::type_name(value);                        \
            }                                                           \
            /* Methods of Comparable */                                 \
            x10_int compareTo(CPRIM arg0) {                             \
                return UTILS::compareTo(value, arg0);                   \
            }                                                           \
            /* Methods of Arithmetic */                                 \
            CPRIM __plus() {                                            \
                return value;                                           \
            }                                                           \
            CPRIM __minus() {                                           \
                return (CPRIM)(-value);                                 \
            }                                                           \
            CPRIM __plus(CPRIM arg0) {                                  \
                return (CPRIM)(value+arg0);                             \
            }                                                           \
            CPRIM __minus(CPRIM arg0) {                                 \
                return (CPRIM)(value-arg0);                             \
            }                                                           \
            CPRIM __times(CPRIM arg0) {                                 \
                return (CPRIM)(value*arg0);                             \
            }                                                           \
            CPRIM __over(CPRIM arg0) {                                  \
                return (CPRIM)(value/arg0);                             \
            }                                                           \
            /* Methods of Bitwise */                                    \
            CPRIM __tilde() {                                           \
                return (CPRIM)(~value);                                 \
            }                                                           \
            CPRIM __ampersand(CPRIM arg0) {                             \
                return (CPRIM)(value&arg0);                             \
            }                                                           \
            CPRIM __bar(CPRIM arg0) {                                   \
                return (CPRIM)(value|arg0);                             \
            }                                                           \
            CPRIM __caret(CPRIM arg0) {                                 \
                return (CPRIM)(value^arg0);                             \
            }                                                           \
            CPRIM __left(x10_long arg0) {                               \
                return (CPRIM)(value<<(0x1f&(x10_int)arg0));            \
            }                                                           \
            CPRIM __right(x10_long arg0) {                              \
                return (CPRIM)(value>>(0x1f&(x10_int)arg0));            \
            }                                                           \
            CPRIM __unsigned_right(x10_long arg0) {                     \
                return (CPRIM)(((x10_ulong)value)>>(0x1f&(x10_int)arg0)); \
            }                                                           \
            /* Methods of Ordered */                                    \
            x10_boolean __lt(CPRIM arg0) {                              \
                return value<arg0;                                      \
            }                                                           \
            x10_boolean __gt(CPRIM arg0) {                              \
                return value>arg0;                                      \
            }                                                           \
            x10_boolean __le(CPRIM arg0) {                              \
                return value<=arg0;                                     \
            }                                                           \
            x10_boolean __ge(CPRIM arg0) {                              \
                return value>=arg0;                                     \
            }                                                           \
        };                                                              \
                                                                        \
        Any::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::any_itable(&PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
        Comparable<CPRIM>::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::comparable_itable(&PRIMCLASS##_iboxthunk::compareTo, &PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
        Arithmetic<CPRIM>::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::arithmetic_itable(&PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::__times, &PRIMCLASS##_iboxthunk::__plus, &PRIMCLASS##_iboxthunk::__plus, &PRIMCLASS##_iboxthunk::__minus, &PRIMCLASS##_iboxthunk::__minus, &PRIMCLASS##_iboxthunk::__over, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
        Bitwise<CPRIM>::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::bitwise_itable(&PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::__ampersand, &PRIMCLASS##_iboxthunk::__left, &PRIMCLASS##_iboxthunk::__right, &PRIMCLASS##_iboxthunk::__unsigned_right, &PRIMCLASS##_iboxthunk::__caret, &PRIMCLASS##_iboxthunk::__bar, &PRIMCLASS##_iboxthunk::__tilde, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
        x10::util::Ordered<CPRIM>::itable<PRIMCLASS##_iboxthunk> PRIMCLASS##_iboxthunk::ordered_itable(&PRIMCLASS##_iboxthunk::equals, &PRIMCLASS##_iboxthunk::hashCode, &PRIMCLASS##_iboxthunk::__lt, &PRIMCLASS##_iboxthunk::__le, &PRIMCLASS##_iboxthunk::__gt, &PRIMCLASS##_iboxthunk::__ge, &PRIMCLASS##_iboxthunk::toString, &PRIMCLASS##_iboxthunk::typeName); \
                                                                \
        itable_entry itable_##PRIMCLASS[6] = { itable_entry(&x10aux::getRTT<x10::lang::Any>, &PRIMCLASS##_iboxthunk::any_itable), \
                                               itable_entry(&x10aux::getRTT<x10::lang::Comparable<CPRIM> >, &PRIMCLASS##_iboxthunk::comparable_itable), \
                                               itable_entry(&x10aux::getRTT<x10::lang::Arithmetic<CPRIM> >, &PRIMCLASS##_iboxthunk::arithmetic_itable), \
                                               itable_entry(&x10aux::getRTT<x10::lang::Bitwise<CPRIM> >, &PRIMCLASS##_iboxthunk::bitwise_itable), \
                                               itable_entry(&x10aux::getRTT<x10::util::Ordered<CPRIM> >, &PRIMCLASS##_iboxthunk::ordered_itable), \
                                               itable_entry(NULL, (void*)"x10.lang.PRIMCLASS") }; \

        BOXED_PRIM_ITABLES_CABO(x10_byte, Byte, ByteNatives)
        BOXED_PRIM_ITABLES_CABO(x10_ubyte, UByte, UByteNatives)
        BOXED_PRIM_ITABLES_CABO(x10_short, Short, ShortNatives)
        BOXED_PRIM_ITABLES_CABO(x10_ushort, UShort, UShortNatives)
        BOXED_PRIM_ITABLES_CABO(x10_int, Int, IntNatives)
        BOXED_PRIM_ITABLES_CABO(x10_uint, UInt, UIntNatives)
        BOXED_PRIM_ITABLES_CAO(x10_float, Float, FloatNatives)
        BOXED_PRIM_ITABLES_CABO(x10_long, Long, LongNatives)
        BOXED_PRIM_ITABLES_CABO(x10_ulong, ULong, ULongNatives)
        BOXED_PRIM_ITABLES_CAO(x10_double, Double, DoubleNatives)
    }
}

