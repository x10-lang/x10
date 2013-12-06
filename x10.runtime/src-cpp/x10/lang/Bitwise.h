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

#ifndef __X10_LANG_BITWISE_H
#define __X10_LANG_BITWISE_H

#include <x10rt.h>

#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 {
    namespace lang { 

        template<class TPMGL(T)> class Bitwise   {
        public:
            RTT_H_DECLS_INTERFACE
    
            template <class Iface> struct itable {
                itable(x10_boolean (Iface::*equals) (::x10::lang::Any*), x10_int (Iface::*hashCode) (), TPMGL(T) (Iface::*__ampersand) (TPMGL(T)), TPMGL(T) (Iface::*__left) (x10_long), TPMGL(T) (Iface::*__right) (x10_long), TPMGL(T) (Iface::*__unsigned_right) (x10_long), TPMGL(T) (Iface::*__caret) (TPMGL(T)), TPMGL(T) (Iface::*__bar) (TPMGL(T)), TPMGL(T) (Iface::*__tilde) (), ::x10::lang::String* (Iface::*toString) (), ::x10::lang::String* (Iface::*typeName) ()) : equals(equals), hashCode(hashCode), __ampersand(__ampersand), __left(__left), __right(__right), __unsigned_right(__unsigned_right), __caret(__caret), __bar(__bar), __tilde(__tilde), toString(toString), typeName(typeName) {}
                x10_boolean (Iface::*equals) (::x10::lang::Any*);
                x10_int (Iface::*hashCode) ();
                TPMGL(T) (Iface::*__ampersand) (TPMGL(T));
                TPMGL(T) (Iface::*__left) (x10_long);
                TPMGL(T) (Iface::*__right) (x10_long);
                TPMGL(T) (Iface::*__unsigned_right) (x10_long);
                TPMGL(T) (Iface::*__caret) (TPMGL(T));
                TPMGL(T) (Iface::*__bar) (TPMGL(T));
                TPMGL(T) (Iface::*__tilde) ();
                ::x10::lang::String* (Iface::*toString) ();
                ::x10::lang::String* (Iface::*typeName) ();
            };
    
            template <class R> static x10_boolean equals(R* _recv, ::x10::lang::Any* arg0) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->equals))(arg0);
            }
            template <class R> static x10_boolean equals(R _recv, ::x10::lang::Any* arg0) {
                return _recv->equals(arg0);
            }
            template <class R> static x10_int hashCode(R* _recv) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->hashCode))();
            }
            template <class R> static x10_int hashCode(R _recv) {
                return _recv->hashCode();
            }
            template <class R> static TPMGL(T) __ampersand(R* _recv, TPMGL(T) arg0) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->__ampersand))(arg0);
            }
            template <class R> static TPMGL(T) __ampersand(R _recv, TPMGL(T) arg0) {
                return _recv->__ampersand(arg0);
            }
            template <class R> static TPMGL(T) __left(R* _recv, x10_long arg0) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->__left))(arg0);
            }
            template <class R> static TPMGL(T) __left(R _recv, x10_long arg0) {
                return _recv->__left(arg0);
            }
            template <class R> static TPMGL(T) __right(R* _recv, x10_long arg0) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->__right))(arg0);
            }
            template <class R> static TPMGL(T) __right(R _recv, x10_long arg0) {
                return _recv->__right(arg0);
            }
            template <class R> static TPMGL(T) __unsigned_right(R* _recv, x10_long arg0) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->__unsigned_right))(arg0);
            }
            template <class R> static TPMGL(T) __unsigned_right(R _recv, x10_long arg0) {
                return _recv->__unsigned_right(arg0);
            }
            template <class R> static TPMGL(T) __caret(R* _recv, TPMGL(T) arg0) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->__caret))(arg0);
            }
            template <class R> static TPMGL(T) __caret(R _recv, TPMGL(T) arg0) {
                return _recv->__caret(arg0);
            }
            template <class R> static TPMGL(T) __bar(R* _recv, TPMGL(T) arg0) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->__bar))(arg0);
            }
            template <class R> static TPMGL(T) __bar(R _recv, TPMGL(T) arg0) {
                return _recv->__bar(arg0);
            }
            template <class R> static TPMGL(T) __tilde(R* _recv) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->__tilde))();
            }
            template <class R> static TPMGL(T) __tilde(R _recv) {
                return _recv->__tilde();
            }
            template <class R> static ::x10::lang::String* toString(R* _recv) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->toString))();
            }
            template <class R> static ::x10::lang::String* toString(R _recv) {
                return _recv->toString();
            }
            template <class R> static ::x10::lang::String* typeName(R* _recv) {
                ::x10::lang::Reference* _refRecv = reinterpret_cast< ::x10::lang::Reference*>(_recv);
                ::x10aux::nullCheck(_refRecv);
                return (_refRecv->*(::x10aux::findITable< ::x10::lang::Bitwise<TPMGL(T)> >(_refRecv->_getITables())->typeName))();
            }
            template <class R> static ::x10::lang::String* typeName(R _recv) {
                return _recv->typeName();
            }
    
        };

        template <> class Bitwise<void> {
        public:
            static ::x10aux::RuntimeType rtt;
            static const ::x10aux::RuntimeType* getRTT() { return & rtt; }
        };

#define BITWISE_PRIM_DECL(PRIM,UNSIGNED_PRIM,SHIFT_MASK) template<> class Bitwise<PRIM>   { \
        public:                                                         \
            RTT_H_DECLS_INTERFACE                                       \
                template <class Iface> struct itable {                  \
                itable(x10_boolean (Iface::*equals) (::x10::lang::Any*), x10_int (Iface::*hashCode) (), PRIM (Iface::*__ampersand) (PRIM), PRIM (Iface::*__left) (x10_long), PRIM (Iface::*__right) (x10_long), PRIM (Iface::*__unsigned_right) (x10_long), PRIM (Iface::*__caret) (PRIM), PRIM (Iface::*__bar) (PRIM), PRIM (Iface::*__tilde) (), ::x10::lang::String* (Iface::*toString) (), ::x10::lang::String* (Iface::*typeName) ()) : equals(equals), hashCode(hashCode), __ampersand(__ampersand), __left(__left), __right(__right), __unsigned_right(__unsigned_right), __caret(__caret), __bar(__bar), __tilde(__tilde), toString(toString), typeName(typeName) {} \
                x10_boolean (Iface::*equals) (::x10::lang::Any*); \
                x10_int (Iface::*hashCode) ();                              \
                PRIM (Iface::*__ampersand) (PRIM);                          \
                PRIM (Iface::*__left) (x10_long);                           \
                PRIM (Iface::*__right) (x10_long);                          \
                PRIM (Iface::*__unsigned_right) (x10_long);                 \
                PRIM (Iface::*__caret) (PRIM);                              \
                PRIM (Iface::*__bar) (PRIM);                                \
                PRIM (Iface::*__tilde) ();                                  \
                ::x10::lang::String* (Iface::*toString) ();       \
                ::x10::lang::String* (Iface::*typeName) ();       \
                };                                                      \
            static x10_boolean equals(PRIM recv, PRIM arg0) { return ::x10aux::equals(recv, arg0); } \
            static x10_int hashCode(PRIM recv) { return ::x10aux::hash_code(recv); } \
            template <class R> static PRIM __ampersand(R* _recv, PRIM arg0) { \
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv); \
                ::x10aux::nullCheck(_recv); \
                return (recv->*(::x10aux::findITable< ::x10::lang::Bitwise<PRIM> >(recv->_getITables())->__ampersand))(arg0); \
            }                                                           \
            static inline PRIM __ampersand(PRIM recv, PRIM arg0) {      \
                return recv & arg0;                                     \
            }                                                           \
            template <class R> static PRIM __left(R* _recv, x10_long arg0) { \
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv); \
                ::x10aux::nullCheck(_recv); \
                return (recv->*(::x10aux::findITable< ::x10::lang::Bitwise<PRIM> >(recv->_getITables())->__left))(arg0); \
            }                                                           \
            static inline PRIM __left(PRIM recv, x10_long arg0) {       \
                return recv << (SHIFT_MASK & (x10_int)arg0);           \
            }                                                           \
            template <class R> static PRIM __right(R* _recv, x10_long arg0) { \
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv); \
                ::x10aux::nullCheck(_recv); \
                return (recv->*(::x10aux::findITable< ::x10::lang::Bitwise<PRIM> >(recv->_getITables())->__right))(arg0); \
            }                                                           \
            static inline PRIM __right(PRIM recv, x10_long arg0) {      \
                return recv >> (SHIFT_MASK & (x10_int)arg0);            \
            }                                                           \
            template <class R> static PRIM __unsigned_right(R* _recv, x10_long arg0) { \
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv); \
                ::x10aux::nullCheck(_recv); \
                return (recv->*(::x10aux::findITable< ::x10::lang::Bitwise<PRIM> >(recv->_getITables())->__unsigned_right))(arg0); \
            }                                                           \
            static inline PRIM __unsigned_right(PRIM recv, x10_long arg0) { \
                return ((UNSIGNED_PRIM)recv) >> (SHIFT_MASK & (x10_int)arg0); \
            }                                                           \
            template <class R> static PRIM __caret(R* _recv, PRIM arg0) { \
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv); \
                ::x10aux::nullCheck(_recv); \
                return (recv->*(::x10aux::findITable< ::x10::lang::Bitwise<PRIM> >(recv->_getITables())->__caret))(arg0); \
            }                                                           \
            static inline PRIM __caret(PRIM recv, PRIM arg0) {          \
                return recv ^ arg0;                                     \
            }                                                           \
            template <class R> static PRIM __bar(R* _recv, PRIM arg0) { \
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv); \
                ::x10aux::nullCheck(_recv); \
                return (recv->*(::x10aux::findITable< ::x10::lang::Bitwise<PRIM> >(recv->_getITables())->__bar))(arg0); \
            }                                                           \
            static inline PRIM __bar(PRIM recv, PRIM arg0) {            \
                return recv | arg0;                                     \
            }                                                           \
            template <class R> static PRIM __tilde(R* _recv) { \
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(_recv); \
                ::x10aux::nullCheck(_recv); \
                return (recv->*(::x10aux::findITable< ::x10::lang::Bitwise<PRIM> >(recv->_getITables())->__tilde))(); \
            }                                                           \
            static inline PRIM __tilde(PRIM recv) {                     \
                return ~recv;                                           \
            }                                                           \
            static ::x10::lang::String* toString(PRIM recv) { return ::x10aux::to_string(recv); } \
            static ::x10::lang::String* typeName(PRIM recv) { return ::x10aux::type_name(recv); } \
        };

        BITWISE_PRIM_DECL(x10_byte, x10_ubyte, 0x7)
        BITWISE_PRIM_DECL(x10_ubyte, x10_ubyte, 0x7)
        BITWISE_PRIM_DECL(x10_short, x10_ushort, 0xf)
        BITWISE_PRIM_DECL(x10_ushort, x10_ushort, 0xf)
        BITWISE_PRIM_DECL(x10_int, x10_uint, 0x1f)
        BITWISE_PRIM_DECL(x10_uint, x10_uint, 0x1f)
        BITWISE_PRIM_DECL(x10_long, x10_ulong, 0x3f)
        BITWISE_PRIM_DECL(x10_ulong, x10_ulong, 0x3f)

#undef BITWISE_PRIM_DECL
        
    }
} 
#endif // X10_LANG_BITWISE_H

#ifndef X10_LANG_BITWISE_H_NODEPS
#define X10_LANG_BITWISE_H_NODEPS
#include <x10/lang/Any.h>
#ifndef X10_LANG_BITWISE_H_IMPLEMENTATION
#define X10_LANG_BITWISE_H_IMPLEMENTATION
#include <x10/lang/Bitwise.h>

template<class TPMGL(T)> ::x10aux::RuntimeType x10::lang::Bitwise<TPMGL(T)>::rtt;
template<class TPMGL(T)> void x10::lang::Bitwise<TPMGL(T)>::_initRTT() {
    const ::x10aux::RuntimeType *canonical = ::x10aux::getRTT< ::x10::lang::Bitwise<void> >();
    if (rtt.initStageOne(canonical)) return;
    const ::x10aux::RuntimeType* parents[1] = { ::x10aux::getRTT< ::x10::lang::Any>()};
    const ::x10aux::RuntimeType* params[1] = { ::x10aux::getRTT<TPMGL(T)>()};
    ::x10aux::RuntimeType::Variance variances[1] = { ::x10aux::RuntimeType::invariant};
    const char *baseName = "x10.lang.Bitwise";
    rtt.initStageTwo(baseName, ::x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances);
}
#endif // X10_LANG_BITWISE_H_IMPLEMENTATION
#endif // __X10_LANG_BITWISE_H_NODEPS
