/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 *  (C) Copyright Australian National University 2011.
 */

#ifndef __X10_LANG_ARITHMETIC_H
#define __X10_LANG_ARITHMETIC_H

#include <x10rt.h>


#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 {
    namespace lang { 

        template<class TPMGL(T)> class Arithmetic   {
        public:
            RTT_H_DECLS_INTERFACE
    
            template <class I> struct itable {
                itable(x10_boolean (I::*equals) (x10aux::ref<x10::lang::Any>), x10_int (I::*hashCode) (), TPMGL(T) (I::*__times) (TPMGL(T)), TPMGL(T) (I::*_m3____plus) (), TPMGL(T) (I::*_m4____plus) (TPMGL(T)), TPMGL(T) (I::*_m5____minus) (), TPMGL(T) (I::*_m6____minus) (TPMGL(T)), TPMGL(T) (I::*__over) (TPMGL(T)), x10aux::ref<x10::lang::String> (I::*toString) (), x10aux::ref<x10::lang::String> (I::*typeName) ()) : equals(equals), hashCode(hashCode), __times(__times), _m3____plus(_m3____plus), _m4____plus(_m4____plus), _m5____minus(_m5____minus), _m6____minus(_m6____minus), __over(__over), toString(toString), typeName(typeName) {}
                x10_boolean (I::*equals) (x10aux::ref<x10::lang::Any>);
                x10_int (I::*hashCode) ();
                TPMGL(T) (I::*__times) (TPMGL(T));
                TPMGL(T) (I::*_m3____plus) ();
                TPMGL(T) (I::*_m4____plus) (TPMGL(T));
                TPMGL(T) (I::*_m5____minus) ();
                TPMGL(T) (I::*_m6____minus) (TPMGL(T));
                TPMGL(T) (I::*__over) (TPMGL(T));
                x10aux::ref<x10::lang::String> (I::*toString) ();
                x10aux::ref<x10::lang::String> (I::*typeName) ();
            };
    
            template <class R> static x10_boolean equals(x10aux::ref<R> _recv, x10aux::ref<x10::lang::Any> arg0) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->equals))(arg0);
            }
            template <class R> static x10_boolean equals(R _recv, x10aux::ref<x10::lang::Any> arg0) {
                return _recv->equals(arg0);
            }
            template <class R> static x10_int hashCode(x10aux::ref<R> _recv) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->hashCode))();
            }
            template <class R> static x10_int hashCode(R _recv) {
                return _recv->hashCode();
            }
            template <class R> static TPMGL(T) __times(x10aux::ref<R> _recv, TPMGL(T) arg0) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->__times))(arg0);
            }
            template <class R> static TPMGL(T) __times(R _recv, TPMGL(T) arg0) {
                return _recv->__times(arg0);
            }
            template <class R> static TPMGL(T) _m3____plus(x10aux::ref<R> _recv) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m3____plus))();
            }
            template <class R> static TPMGL(T) _m3____plus(R _recv) {
                return _recv->__plus();
            }
            template <class R> static TPMGL(T) _m4____plus(x10aux::ref<R> _recv, TPMGL(T) arg0) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m4____plus))(arg0);
            }
            template <class R> static TPMGL(T) _m4____plus(R _recv, TPMGL(T) arg0) {
                return _recv->__plus(arg0);
            }
            template <class R> static TPMGL(T) _m5____minus(x10aux::ref<R> _recv) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m5____minus))();
            }
            template <class R> static TPMGL(T) _m5____minus(R _recv) {
                return _recv->__minus();
            }
            template <class R> static TPMGL(T) _m6____minus(x10aux::ref<R> _recv, TPMGL(T) arg0) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m6____minus))(arg0);
            }
            template <class R> static TPMGL(T) _m6____minus(R _recv, TPMGL(T) arg0) {
                return _recv->__minus(arg0);
            }
            template <class R> static TPMGL(T) __over(x10aux::ref<R> _recv, TPMGL(T) arg0) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->__over))(arg0);
            }
            template <class R> static TPMGL(T) __over(R _recv, TPMGL(T) arg0) {
                return _recv->__over(arg0);
            }
            template <class R> static x10aux::ref<x10::lang::String> toString(x10aux::ref<R> _recv) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->toString))();
            }
            template <class R> static x10aux::ref<x10::lang::String> toString(R _recv) {
                return _recv->toString();
            }
            template <class R> static x10aux::ref<x10::lang::String> typeName(x10aux::ref<R> _recv) {
                x10aux::ref<x10::lang::Reference> _refRecv(_recv);
                return (_refRecv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->typeName))();
            }
            template <class R> static x10aux::ref<x10::lang::String> typeName(R _recv) {
                return _recv->typeName();
            }
    
        };

        template <> class Arithmetic<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return & rtt; }
        };






#define ARITHMETIC_PRIM_DECL(PRIM) template<> class Arithmetic<PRIM>   { \
        public:                                                         \
            RTT_H_DECLS_INTERFACE                                       \
                template <class I> struct itable {                      \
                itable(x10_boolean (I::*equals) (x10aux::ref<x10::lang::Any>), x10_int (I::*hashCode) (), PRIM (I::*__times) (PRIM), PRIM (I::*_m3____plus) (), PRIM (I::*_m4____plus) (PRIM), PRIM (I::*_m5____minus) (), PRIM (I::*_m6____minus) (PRIM), PRIM (I::*__over) (PRIM), x10aux::ref<x10::lang::String> (I::*toString) (), x10aux::ref<x10::lang::String> (I::*typeName) ()) : equals(equals), hashCode(hashCode), __times(__times), _m3____plus(_m3____plus), _m4____plus(_m4____plus), _m5____minus(_m5____minus), _m6____minus(_m6____minus), __over(__over), toString(toString), typeName(typeName) {} \
                x10_boolean (I::*equals) (x10aux::ref<x10::lang::Any>); \
                x10_int (I::*hashCode) ();                              \
                PRIM (I::*__times) (PRIM);                              \
                PRIM (I::*_m3____plus) ();                              \
                PRIM (I::*_m4____plus) (PRIM);                          \
                PRIM (I::*_m5____minus) ();                             \
                PRIM (I::*_m6____minus) (PRIM);                         \
                PRIM (I::*__over) (PRIM);                               \
                x10aux::ref<x10::lang::String> (I::*toString) ();       \
                x10aux::ref<x10::lang::String> (I::*typeName) ();       \
                };                                                      \
            static x10_boolean equals(PRIM recv, PRIM arg0) { return x10aux::equals(recv, arg0); } \
            static x10_int hashCode(PRIM recv) { return x10aux::hash_code(recv); } \
            static inline PRIM __times(PRIM recv, PRIM arg0) {          \
                return recv * arg0;                                     \
            }                                                           \
            static PRIM __times(x10aux::ref<x10::lang::Reference> recv, PRIM arg0) { \
                return (recv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->__times))(arg0); \
            }                                                           \
            static inline PRIM _m3____plus(PRIM recv) {                 \
                return recv;                                            \
            }                                                           \
            static PRIM _m3____plus(x10aux::ref<x10::lang::Reference> recv) { \
                return (recv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m3____plus))(); \
            }                                                           \
            static inline PRIM _m4____plus(PRIM recv, PRIM arg0) {      \
                return recv + arg0;                                     \
            }                                                           \
            static PRIM _m4____plus(x10aux::ref<x10::lang::Reference> recv, PRIM arg0) { \
                return (recv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m4____plus))(arg0); \
            }                                                           \
            static inline PRIM _m5____minus(PRIM recv) {                \
                return -recv;                                           \
            }                                                           \
            static PRIM _m5____minus(x10aux::ref<x10::lang::Reference> recv) { \
                return (recv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m5____minus))(); \
            }                                                           \
            static inline PRIM _m6____minus(PRIM recv, PRIM arg0) {     \
                return recv - arg0;                                     \
            }                                                           \
            static PRIM _m6____minus(x10aux::ref<x10::lang::Reference> recv, PRIM arg0) { \
                return (recv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m6____minus))(arg0); \
            }                                                           \
            static inline PRIM __over(PRIM recv, PRIM arg0) {           \
                return recv / arg0;                                     \
            }                                                           \
            static PRIM __over(x10aux::ref<x10::lang::Reference> recv, PRIM arg0) { \
                return (recv.operator->()->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->__over))(arg0); \
            }                                                           \
            static x10aux::ref<x10::lang::String> toString(PRIM recv) { return x10aux::to_string(recv); } \
            static x10aux::ref<x10::lang::String> typeName(PRIM recv) { return x10aux::type_name(recv); } \
        };

        ARITHMETIC_PRIM_DECL(x10_byte)
        ARITHMETIC_PRIM_DECL(x10_ubyte)
        ARITHMETIC_PRIM_DECL(x10_short)
        ARITHMETIC_PRIM_DECL(x10_ushort)
        ARITHMETIC_PRIM_DECL(x10_int)
        ARITHMETIC_PRIM_DECL(x10_uint)
        ARITHMETIC_PRIM_DECL(x10_long)
        ARITHMETIC_PRIM_DECL(x10_ulong)
        ARITHMETIC_PRIM_DECL(x10_float)
        ARITHMETIC_PRIM_DECL(x10_double)

#undef ARITHMETIC_PRIM_DECL

    }
} 
#endif // X10_LANG_ARITHMETIC_H

#ifndef X10_LANG_ARITHMETIC_H_NODEPS
#define X10_LANG_ARITHMETIC_H_NODEPS
#include <x10/lang/Any.h>
#ifndef X10_LANG_ARITHMETIC_H_IMPLEMENTATION
#define X10_LANG_ARITHMETIC_H_IMPLEMENTATION
#include <x10/lang/Arithmetic.h>

template<class TPMGL(T)> x10aux::RuntimeType x10::lang::Arithmetic<TPMGL(T)>::rtt;
template<class TPMGL(T)> void x10::lang::Arithmetic<TPMGL(T)>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::lang::Arithmetic<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<TPMGL(T)>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.lang.Arithmetic";
    rtt.initStageTwo(baseName, x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances);
}
#endif // X10_LANG_ARITHMETIC_H_IMPLEMENTATION
#endif // __X10_LANG_ARITHMETIC_H_NODEPS
