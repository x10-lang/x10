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

#ifndef __X10_UTIL_ORDERED_H
#define __X10_UTIL_ORDERED_H

#include <x10rt.h>
#include <x10tm.h>


#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS

namespace x10 {
    namespace util { 

        template<class TPMGL(T)> class Ordered   {
        public:
            RTT_H_DECLS_INTERFACE
    
            template <class I> struct itable {
            itable(	x10_boolean (I::*equals) (x10::lang::Any*), x10_boolean (I::*equals__tm__) (x10tm::TMThread *SelfTM, x10::lang::Any*), x10_int (I::*hashCode) (), x10_int (I::*hashCode__tm__) (x10tm::TMThread *SelfTM), x10_boolean (I::*__lt) (TPMGL(T)), x10_boolean (I::*__lt__tm__) (x10tm::TMThread *SelfTM, TPMGL(T)), x10_boolean (I::*__le) (TPMGL(T)), x10_boolean (I::*__le__tm__) (x10tm::TMThread *SelfTM, TPMGL(T)), x10_boolean (I::*__gt) (TPMGL(T)), x10_boolean (I::*__gt__tm__) (x10tm::TMThread *SelfTM, TPMGL(T)), x10_boolean (I::*__ge) (TPMGL(T)), x10_boolean (I::*__ge__tm__) (x10tm::TMThread *SelfTM, TPMGL(T)), x10::lang::String* (I::*toString) (), x10::lang::String* (I::*toString__tm__) (x10tm::TMThread *SelfTM), x10::lang::String* (I::*typeName) (), x10::lang::String* (I::*typeName__tm__) (x10tm::TMThread *SelfTM)) : equals(equals), equals__tm__(equals__tm__), hashCode(hashCode), hashCode__tm__(hashCode__tm__), __lt(__lt), __lt__tm__(__lt__tm__), __le(__le), __le__tm__(__le__tm__), __gt(__gt), __gt__tm__(__gt__tm__), __ge(__ge), __ge__tm__(__ge__tm__), toString(toString), toString__tm__(toString__tm__), typeName(typeName), typeName__tm__(typeName__tm__) {}
                x10_boolean (I::*equals) (x10::lang::Any*);
                x10_boolean (I::*equals__tm__) (x10tm::TMThread *SelfTM, x10::lang::Any*);
                x10_int (I::*hashCode) ();
                x10_int (I::*hashCode__tm__) (x10tm::TMThread *SelfTM);
                x10_boolean (I::*__lt) (TPMGL(T));
                x10_boolean (I::*__lt__tm__) (x10tm::TMThread *SelfTM, TPMGL(T));
                x10_boolean (I::*__le) (TPMGL(T));
                x10_boolean (I::*__le__tm__) (x10tm::TMThread *SelfTM, TPMGL(T));
                x10_boolean (I::*__gt) (TPMGL(T));
                x10_boolean (I::*__gt__tm__) (x10tm::TMThread *SelfTM, TPMGL(T));
                x10_boolean (I::*__ge) (TPMGL(T));
                x10_boolean (I::*__ge__tm__) (x10tm::TMThread *SelfTM, TPMGL(T));
                x10::lang::String* (I::*toString) ();
                x10::lang::String* (I::*toString__tm__) (x10tm::TMThread *SelfTM);
                x10::lang::String* (I::*typeName) ();
                x10::lang::String* (I::*typeName__tm__) (x10tm::TMThread *SelfTM);
            };
    
            static x10_boolean equals(Ordered<TPMGL(T)>* _recv, x10::lang::Any* arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->equals))(arg0);
            }

            static x10_boolean equals__tm__(x10tm::TMThread *SelfTM, Ordered<TPMGL(T)>* _recv, x10::lang::Any* arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->equals__tm__))(SelfTM, arg0);
			}
            template <class R> static x10_boolean equals(R _recv, x10::lang::Any* arg0) {
                return _recv->equals(arg0);
            }
            template <class R> static x10_boolean equals__tm__(x10tm::TMThread *SelfTM, R _recv, x10::lang::Any* arg0) {
				return _recv->equals__tm__(SelfTM, arg0);
			}
            static x10_int hashCode(Ordered<TPMGL(T)>* _recv) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->hashCode))();
            }
            static x10_int hashCode__tm__(x10tm::TMThread *SelfTM, Ordered<TPMGL(T)>* _recv) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->hashCode__tm__))(SelfTM);
			}
            template <class R> static x10_int hashCode(R _recv) {
                return _recv->hashCode();
            }
            template <class R> static x10_int hashCode__tm__(x10tm::TMThread *SelfTM, R _recv) {
				return _recv->hashCode__tm__(SelfTM);
			}
            static x10_boolean __lt(Ordered<TPMGL(T)>* _recv, TPMGL(T) arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->__lt))(arg0);
            }
            static x10_boolean __lt__tm__(x10tm::TMThread *SelfTM, Ordered<TPMGL(T)>* _recv, TPMGL(T) arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->__lt__tm__))(SelfTM, arg0);
			}
            template <class R> static x10_boolean __lt(R _recv, TPMGL(T) arg0) {
                return _recv->__lt(arg0);
            }
            template <class R> static x10_boolean __lt__tm__(x10tm::TMThread *SelfTM, R _recv, TPMGL(T) arg0) {
				return _recv->__lt__tm__(SelfTM, arg0);
			}
            static x10_boolean __le(Ordered<TPMGL(T)>* _recv, TPMGL(T) arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->__le))(arg0);
            }
            static x10_boolean __le__tm__(x10tm::TMThread *SelfTM, Ordered<TPMGL(T)>* _recv, TPMGL(T) arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->__le__tm__))(SelfTM, arg0);
			}
            template <class R> static x10_boolean __le(R _recv, TPMGL(T) arg0) {
                return _recv->__le(arg0);
            }
            template <class R> static x10_boolean __le__tm__(x10tm::TMThread *SelfTM, R _recv, TPMGL(T) arg0) {
				return _recv->__le__tm__(SelfTM, arg0);
			}
            static x10_boolean __gt(Ordered<TPMGL(T)>* _recv, TPMGL(T) arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->__gt))(arg0);
            }
            static x10_boolean __gt__tm__(x10tm::TMThread *SelfTM, Ordered<TPMGL(T)>* _recv, TPMGL(T) arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->__gt__tm__))(SelfTM, arg0);
			}
            template <class R> static x10_boolean __gt(R _recv, TPMGL(T) arg0) {
                return _recv->__gt(arg0);
            }
            template <class R> static x10_boolean __gt__tm__(x10tm::TMThread *SelfTM, R _recv, TPMGL(T) arg0) {
				return _recv->__gt__tm__(SelfTM, arg0);
			}
            static x10_boolean __ge(Ordered<TPMGL(T)>* _recv, TPMGL(T) arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->__ge))(arg0);
            }
            static x10_boolean __ge__tm__(x10tm::TMThread *SelfTM, Ordered<TPMGL(T)>* _recv, TPMGL(T) arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->__ge__tm__))(SelfTM, arg0);
			}
            template <class R> static x10_boolean __ge(R _recv, TPMGL(T) arg0) {
                return _recv->__ge(arg0);
            }
            template <class R> static x10_boolean __ge__tm__(x10tm::TMThread *SelfTM, R _recv, TPMGL(T) arg0) {
				return _recv->__ge__tm__(SelfTM, arg0);
			}
            static x10::lang::String* toString(Ordered<TPMGL(T)>* _recv) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->toString))();
            }
            static x10::lang::String* toString__tm__(x10tm::TMThread *SelfTM, Ordered<TPMGL(T)>* _recv) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->toString__tm__))(SelfTM);
			}
            template <class R> static x10::lang::String* toString(R _recv) {
                return _recv->toString();
            }
            template <class R> static x10::lang::String* toString__tm__(x10tm::TMThread *SelfTM, R _recv) {
				return _recv->toString__tm__(SelfTM);
			}
            static x10::lang::String* typeName(Ordered<TPMGL(T)>* _recv) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->typeName))();
            }
            static x10::lang::String* typeName__tm__(x10tm::TMThread *SelfTM, Ordered<TPMGL(T)>* _recv) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::util::Ordered<TPMGL(T)> >(_refRecv->_getITables())->typeName__tm__))(SelfTM);
			}
            template <class R> static x10::lang::String* typeName(R _recv) {
                return _recv->typeName();
            }
            template <class R> static x10::lang::String* typeName__tm__(x10tm::TMThread *SelfTM, R _recv) {
				return _recv->typeName__tm__(SelfTM);
			}
    
        };

        template <> class Ordered<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return & rtt; }
        };

#define ORDERED_PRIM_DECL(PRIM) template<> class Ordered<PRIM> {        \
        public:                                                         \
            RTT_H_DECLS_INTERFACE                                       \
                template <class I> struct itable {                      \
                itable(x10_boolean (I::*equals) (x10::lang::Any*), x10_boolean (I::*equals__tm__) (x10tm::TMThread *SelfTM, x10::lang::Any*), x10_int (I::*hashCode) (), x10_int (I::*hashCode__tm__) (x10tm::TMThread *SelfTM), x10_boolean (I::*__lt) (PRIM), x10_boolean (I::*__lt__tm__) (x10tm::TMThread *SelfTM, PRIM), x10_boolean (I::*__le) (PRIM), x10_boolean (I::*__le__tm__) (x10tm::TMThread *SelfTM, PRIM), x10_boolean (I::*__gt) (PRIM), x10_boolean (I::*__gt__tm__) (x10tm::TMThread *SelfTM, PRIM), x10_boolean (I::*__ge) (PRIM), x10_boolean (I::*__ge__tm__) (x10tm::TMThread *SelfTM, PRIM), x10::lang::String* (I::*toString) (), x10::lang::String* (I::*toString__tm__) (x10tm::TMThread *SelfTM), x10::lang::String* (I::*typeName) (), x10::lang::String* (I::*typeName__tm__) (x10tm::TMThread *SelfTM)) : equals(equals), equals__tm__(equals__tm__), hashCode(hashCode), hashCode__tm__(hashCode__tm__), __lt(__lt), __lt__tm__(__lt__tm__), __le(__le), __le__tm__(__le__tm__), __gt(__gt), __gt__tm__(__gt__tm__), __ge(__ge), __ge__tm__(__ge__tm__), toString(toString), toString__tm__(toString__tm__), typeName(typeName), typeName__tm__(typeName__tm__) {} \
                x10_boolean (I::*equals) (x10::lang::Any*);             \
                x10_boolean (I::*equals__tm__) (x10tm::TMThread *SelfTM, x10::lang::Any*);             \
                x10_int (I::*hashCode) ();                              \
                x10_int (I::*hashCode__tm__) (x10tm::TMThread *SelfTM);                              \
                x10_boolean (I::*__lt) (PRIM);                          \
                x10_boolean (I::*__lt__tm__) (x10tm::TMThread *SelfTM, PRIM);                          \
                x10_boolean (I::*__le) (PRIM);                          \
                x10_boolean (I::*__le__tm__) (x10tm::TMThread *SelfTM, PRIM);                          \
                x10_boolean (I::*__gt) (PRIM);                          \
                x10_boolean (I::*__gt__tm__) (x10tm::TMThread *SelfTM, PRIM);                          \
                x10_boolean (I::*__ge) (PRIM);                          \
                x10_boolean (I::*__ge__tm__) (x10tm::TMThread *SelfTM, PRIM);                          \
                x10::lang::String* (I::*toString) ();                   \
                x10::lang::String* (I::*toString__tm__) (x10tm::TMThread *SelfTM);                   \
                x10::lang::String* (I::*typeName) ();                   \
                x10::lang::String* (I::*typeName__tm__) (x10tm::TMThread *SelfTM);                   \
                };                                                      \
            static x10_boolean equals(PRIM recv, PRIM arg0) { return x10aux::equals(recv, arg0); } \
            static x10_int hashCode(PRIM recv) { return x10aux::hash_code(recv); } \
            static x10_boolean __lt(Ordered<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::util::Ordered<PRIM> >(recv->_getITables())->__lt))(arg0); \
            }                                                           \
            static inline x10_boolean __lt(PRIM recv, PRIM arg0) {      \
                return recv < arg0;                                     \
            }                                                           \
            static x10_boolean __le(Ordered<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::util::Ordered<PRIM> >(recv->_getITables())->__le))(arg0); \
            }                                                           \
            static inline x10_boolean __le(PRIM recv, PRIM arg0) {      \
                return recv <= arg0;                                    \
            }                                                           \
            static x10_boolean __gt(Ordered<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::util::Ordered<PRIM> >(recv->_getITables())->__gt))(arg0); \
            }                                                           \
            static inline x10_boolean __gt(PRIM recv, PRIM arg0) {      \
                return recv > arg0;                                     \
            }                                                           \
            static x10_boolean __ge(Ordered<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::util::Ordered<PRIM> >(recv->_getITables())->__ge))(arg0); \
            }                                                           \
            static inline x10_boolean __ge(PRIM recv, PRIM arg0) {      \
                return recv >= arg0;                                    \
            }                                                           \
            static x10::lang::String* toString(PRIM recv) { return x10aux::to_string(recv); } \
            static x10::lang::String* typeName(PRIM recv) { return x10aux::type_name(recv); } \
            																					\
			static x10_boolean equals__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) { return x10aux::equals(recv, arg0); } \
            static x10_int hashCode__tm__(x10tm::TMThread *SelfTM, PRIM recv) { return x10aux::hash_code(recv); } \
            static x10_boolean __lt__tm__(x10tm::TMThread *SelfTM, Ordered<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::util::Ordered<PRIM> >(recv->_getITables())->__lt__tm__))(SelfTM, arg0); \
            }                                                           \
            static inline x10_boolean __lt__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) {      \
                return recv < arg0;                                     \
            }                                                           \
            static x10_boolean __le__tm__(x10tm::TMThread *SelfTM, Ordered<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::util::Ordered<PRIM> >(recv->_getITables())->__le__tm__))(SelfTM, arg0); \
            }                                                           \
            static inline x10_boolean __le__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) {      \
                return recv <= arg0;                                    \
            }                                                           \
            static x10_boolean __gt__tm__(x10tm::TMThread *SelfTM, Ordered<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::util::Ordered<PRIM> >(recv->_getITables())->__gt__tm__))(SelfTM, arg0); \
            }                                                           \
            static inline x10_boolean __gt__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) {      \
                return recv > arg0;                                     \
            }                                                           \
            static x10_boolean __ge__tm__(x10tm::TMThread *SelfTM, Ordered<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::util::Ordered<PRIM> >(recv->_getITables())->__ge__tm__))(SelfTM, arg0); \
            }                                                           \
            static inline x10_boolean __ge__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) {      \
                return recv >= arg0;                                    \
            }                                                           \
            static x10::lang::String* toString__tm__(x10tm::TMThread *SelfTM, PRIM recv) { return x10aux::to_string(recv); } \
            static x10::lang::String* typeName__tm__(x10tm::TMThread *SelfTM, PRIM recv) { return x10aux::type_name(recv); } \
        };

        ORDERED_PRIM_DECL(x10_char)
        ORDERED_PRIM_DECL(x10_byte)
        ORDERED_PRIM_DECL(x10_ubyte)
        ORDERED_PRIM_DECL(x10_short)
        ORDERED_PRIM_DECL(x10_ushort)
        ORDERED_PRIM_DECL(x10_int)
        ORDERED_PRIM_DECL(x10_uint)
        ORDERED_PRIM_DECL(x10_long)
        ORDERED_PRIM_DECL(x10_ulong)
        ORDERED_PRIM_DECL(x10_float)
        ORDERED_PRIM_DECL(x10_double)

#undef ORDERED_PRIM_DECL
    }
} 
#endif // X10_UTIL_ORDERED_H

#ifndef X10_UTIL_ORDERED_H_NODEPS
#define X10_UTIL_ORDERED_H_NODEPS
#include <x10/lang/Any.h>
#ifndef X10_UTIL_ORDERED_H_IMPLEMENTATION
#define X10_UTIL_ORDERED_H_IMPLEMENTATION
#include <x10/util/Ordered.h>

template<class TPMGL(T)> x10aux::RuntimeType x10::util::Ordered<TPMGL(T)>::rtt;
template<class TPMGL(T)> void x10::util::Ordered<TPMGL(T)>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::util::Ordered<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<TPMGL(T)>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.util.Ordered";
    rtt.initStageTwo(baseName, x10aux::RuntimeType::interface_kind, 1, parents, 1, params, variances);
}
#endif // X10_UTIL_ORDERED_H_IMPLEMENTATION
#endif // __X10_UTIL_ORDERED_H_NODEPS
