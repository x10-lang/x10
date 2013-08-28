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
                itable(
                x10_boolean (I::*equals) (x10::lang::Any*),
                x10_boolean (I::*equals__tm__) (x10tm::TMThread *SelfTM, x10::lang::Any*),
                x10_int (I::*hashCode) (),
                x10_int (I::*hashCode__tm__) (x10tm::TMThread *SelfTM),
                TPMGL(T) (I::*__times) (TPMGL(T)),
                TPMGL(T) (I::*__times__tm__) (x10tm::TMThread *SelfTM, TPMGL(T)),
                TPMGL(T) (I::*_m3____plus) (),
                TPMGL(T) (I::*_m3____plus__tm__) (x10tm::TMThread *SelfTM),
                TPMGL(T) (I::*_m4____plus) (TPMGL(T)),
                TPMGL(T) (I::*_m4____plus__tm__) (x10tm::TMThread *SelfTM, TPMGL(T)),
                TPMGL(T) (I::*_m5____minus) (),
                TPMGL(T) (I::*_m5____minus__tm__) (x10tm::TMThread *SelfTM),
                TPMGL(T) (I::*_m6____minus) (TPMGL(T)),
                TPMGL(T) (I::*_m6____minus__tm__) (x10tm::TMThread *SelfTM, TPMGL(T)),
                TPMGL(T) (I::*__over) (TPMGL(T)),
                TPMGL(T) (I::*__over__tm__) (x10tm::TMThread *SelfTM, TPMGL(T)),
                x10::lang::String* (I::*toString) (),
                x10::lang::String* (I::*toString__tm__) (x10tm::TMThread *SelfTM),
                x10::lang::String* (I::*typeName) (),
                x10::lang::String* (I::*typeName__tm__) (x10tm::TMThread *SelfTM)) :
																equals(equals),
																equals__tm__(equals__tm__),
																hashCode(hashCode),
																hashCode__tm__(hashCode__tm__),
																__times(__times),
																__times__tm__(__times__tm__),
																_m3____plus(_m3____plus),
																_m3____plus__tm__(_m3____plus__tm__),
																_m4____plus(_m4____plus),
																_m4____plus__tm__(_m4____plus__tm__),
																_m5____minus(_m5____minus),
																_m5____minus__tm__(_m5____minus__tm__),
																_m6____minus(_m6____minus),
																_m6____minus__tm__(_m6____minus__tm__),
																__over(__over),
																__over__tm__(__over__tm__),
																toString(toString),
																toString__tm__(toString__tm__),
																typeName(typeName),
																typeName__tm__(typeName__tm__) {}
                x10_boolean (I::*equals) (x10::lang::Any*);
                x10_boolean (I::*equals__tm__) (x10tm::TMThread *SelfTM, x10::lang::Any*);
                x10_int (I::*hashCode) ();
                x10_int (I::*hashCode__tm__) (x10tm::TMThread *SelfTM);
                TPMGL(T) (I::*__times) (TPMGL(T));
                TPMGL(T) (I::*__times__tm__) (x10tm::TMThread *SelfTM, TPMGL(T));
                TPMGL(T) (I::*_m3____plus) ();
                TPMGL(T) (I::*_m3____plus__tm__) (x10tm::TMThread *SelfTM);
                TPMGL(T) (I::*_m4____plus) (TPMGL(T));
                TPMGL(T) (I::*_m4____plus__tm__) (x10tm::TMThread *SelfTM, TPMGL(T));
                TPMGL(T) (I::*_m5____minus) ();
                TPMGL(T) (I::*_m5____minus__tm__) (x10tm::TMThread *SelfTM);
                TPMGL(T) (I::*_m6____minus) (TPMGL(T));
                TPMGL(T) (I::*_m6____minus__tm__) (x10tm::TMThread *SelfTM, TPMGL(T));
                TPMGL(T) (I::*__over) (TPMGL(T));
                TPMGL(T) (I::*__over__tm__) (x10tm::TMThread *SelfTM, TPMGL(T));
                x10::lang::String* (I::*toString) ();
                x10::lang::String* (I::*toString__tm__) (x10tm::TMThread *SelfTM);
                x10::lang::String* (I::*typeName) ();
                x10::lang::String* (I::*typeName__tm__) (x10tm::TMThread *SelfTM);
            };
    
            static x10_boolean equals(Arithmetic<TPMGL(T)>* _recv, x10::lang::Any* arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->equals))(arg0);
            }
            template <class R> static x10_boolean equals(R _recv, x10::lang::Any* arg0) {
                return _recv->equals(arg0);
            }
            static x10_int hashCode(Arithmetic<TPMGL(T)>* _recv) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->hashCode))();
            }
            template <class R> static x10_int hashCode(R _recv) {
                return _recv->hashCode();
            }
            static TPMGL(T) __times(Arithmetic<TPMGL(T)>* _recv, TPMGL(T) arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->__times))(arg0);
            }
            template <class R> static TPMGL(T) __times(R _recv, TPMGL(T) arg0) {
                return _recv->__times(arg0);
            }
            static TPMGL(T) _m3____plus(Arithmetic<TPMGL(T)>* _recv) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m3____plus))();
            }
            template <class R> static TPMGL(T) _m3____plus(R _recv) {
                return _recv->__plus();
            }
            static TPMGL(T) _m4____plus(Arithmetic<TPMGL(T)>* _recv, TPMGL(T) arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m4____plus))(arg0);
            }
            template <class R> static TPMGL(T) _m4____plus(R _recv, TPMGL(T) arg0) {
                return _recv->__plus(arg0);
            }
            static TPMGL(T) _m5____minus(Arithmetic<TPMGL(T)>* _recv) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m5____minus))();
            }
            template <class R> static TPMGL(T) _m5____minus(R _recv) {
                return _recv->__minus();
            }
            static TPMGL(T) _m6____minus(Arithmetic<TPMGL(T)>* _recv, TPMGL(T) arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m6____minus))(arg0);
            }
            template <class R> static TPMGL(T) _m6____minus(R _recv, TPMGL(T) arg0) {
                return _recv->__minus(arg0);
            }
            static TPMGL(T) __over(Arithmetic<TPMGL(T)>* _recv, TPMGL(T) arg0) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->__over))(arg0);
            }
            template <class R> static TPMGL(T) __over(R _recv, TPMGL(T) arg0) {
                return _recv->__over(arg0);
            }
            static x10::lang::String* toString(Arithmetic<TPMGL(T)>* _recv) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->toString))();
            }
            template <class R> static x10::lang::String* toString(R _recv) {
                return _recv->toString();
            }
            static x10::lang::String* typeName(Arithmetic<TPMGL(T)>* _recv) {
                x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
                return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->typeName))();
            }
            template <class R> static x10::lang::String* typeName(R _recv) {
                return _recv->typeName();
            }

            static x10_boolean equals__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv, x10::lang::Any* arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->equals__tm__))(SelfTM, arg0);
			}
			template <class R> static x10_boolean equals__tm__(x10tm::TMThread *SelfTM, R _recv, x10::lang::Any* arg0) {
				return _recv->equals__tm__(SelfTM, arg0);
			}
			static x10_int hashCode__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->hashCode__tm__))(SelfTM);
			}
			template <class R> static x10_int hashCode__tm__(x10tm::TMThread *SelfTM, R _recv) {
				return _recv->hashCode__tm__(SelfTM);
			}
			static TPMGL(T) __times__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv, TPMGL(T) arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->__times__tm__))(SelfTM, arg0);
			}
			template <class R> static TPMGL(T) __times__tm__(x10tm::TMThread *SelfTM, R _recv, TPMGL(T) arg0) {
				return _recv->__times__tm__(SelfTM, arg0);
			}
			static TPMGL(T) _m3____plus__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m3____plus__tm__))(SelfTM);
			}
			template <class R> static TPMGL(T) _m3____plus__tm__(x10tm::TMThread *SelfTM, R _recv) {
				return _recv->__plus__tm__(SelfTM);
			}
			static TPMGL(T) _m4____plus__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv, TPMGL(T) arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m4____plus__tm__))(SelfTM, arg0);
			}
			template <class R> static TPMGL(T) _m4____plus__tm__(x10tm::TMThread *SelfTM, R _recv, TPMGL(T) arg0) {
				return _recv->__plus__tm__(SelfTM, arg0);
			}
			static TPMGL(T) _m5____minus__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m5____minus__tm__))(SelfTM);
			}
			template <class R> static TPMGL(T) _m5____minus__tm__(x10tm::TMThread *SelfTM, R _recv) {
				return _recv->__minus__tm__(SelfTM);
			}
			static TPMGL(T) _m6____minus__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv, TPMGL(T) arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->_m6____minus__tm__))(SelfTM, arg0);
			}
			template <class R> static TPMGL(T) _m6____minus__tm__(x10tm::TMThread *SelfTM, R _recv, TPMGL(T) arg0) {
				return _recv->__minus__tm__(SelfTM, arg0);
			}
			static TPMGL(T) __over__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv, TPMGL(T) arg0) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->__over__tm__))(SelfTM, arg0);
			}
			template <class R> static TPMGL(T) __over__tm__(x10tm::TMThread *SelfTM, R _recv, TPMGL(T) arg0) {
				return _recv->__over__tm__(arg0);
			}
			static x10::lang::String* toString__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->toString__tm__))(SelfTM);
			}
			template <class R> static x10::lang::String* toString__tm__(x10tm::TMThread *SelfTM, R _recv) {
				return _recv->toString__tm__();
			}
			static x10::lang::String* typeName__tm__(x10tm::TMThread *SelfTM, Arithmetic<TPMGL(T)>* _recv) {
				x10::lang::Reference* _refRecv = reinterpret_cast<x10::lang::Reference*>(_recv);
				return (_refRecv->*(x10aux::findITable<x10::lang::Arithmetic<TPMGL(T)> >(_refRecv->_getITables())->typeName__tm__))(SelfTM);
			}
			template <class R> static x10::lang::String* typeName__tm__(x10tm::TMThread *SelfTM, R _recv) {
				return _recv->typeName__tm__(SelfTM);
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
                itable(\
        		x10_boolean (I::*equals) (x10::lang::Any*), \
        		x10_boolean (I::*equals__tm__) (x10tm::TMThread *SelfTM, x10::lang::Any*), \
        		x10_int (I::*hashCode) (), \
        		x10_int (I::*hashCode__tm__) (x10tm::TMThread *SelfTM), \
        		PRIM (I::*__times) (PRIM), \
        		PRIM (I::*__times__tm__) (x10tm::TMThread *SelfTM, PRIM), \
        		PRIM (I::*_m3____plus) (), \
        		PRIM (I::*_m3____plus__tm__) (x10tm::TMThread *SelfTM), \
        		PRIM (I::*_m4____plus) (PRIM),\
        		PRIM (I::*_m4____plus__tm__) (x10tm::TMThread *SelfTM, PRIM),\
        		PRIM (I::*_m5____minus) (), \
        		PRIM (I::*_m5____minus__tm__) (x10tm::TMThread *SelfTM), \
        		PRIM (I::*_m6____minus) (PRIM),\
        		PRIM (I::*_m6____minus__tm__) (x10tm::TMThread *SelfTM, PRIM),\
        		PRIM (I::*__over) (PRIM), \
        		PRIM (I::*__over__tm__) (x10tm::TMThread *SelfTM, PRIM), \
        		x10::lang::String* (I::*toString) (),\
        		x10::lang::String* (I::*toString__tm__) (x10tm::TMThread *SelfTM),\
        		x10::lang::String* (I::*typeName) (),\
        		x10::lang::String* (I::*typeName__tm__) (x10tm::TMThread *SelfTM)) :	equals(equals), equals__tm__(equals__tm__), hashCode(hashCode),hashCode__tm__(hashCode__tm__), __times(__times), __times__tm__(__times__tm__), _m3____plus(_m3____plus), _m3____plus__tm__(_m3____plus__tm__), _m4____plus(_m4____plus), _m4____plus__tm__(_m4____plus__tm__), _m5____minus(_m5____minus), _m5____minus__tm__(_m5____minus__tm__), _m6____minus(_m6____minus), _m6____minus__tm__(_m6____minus__tm__), __over(__over), __over__tm__(__over__tm__), toString(toString), toString__tm__(toString__tm__), typeName(typeName), typeName__tm__(typeName__tm__) {} \
                x10_boolean (I::*equals) (x10::lang::Any*); \
                x10_boolean (I::*equals__tm__) (x10tm::TMThread *SelfTM, x10::lang::Any*); \
                x10_int (I::*hashCode) ();                              \
                x10_int (I::*hashCode__tm__) (x10tm::TMThread *SelfTM);                              \
                PRIM (I::*__times) (PRIM);                              \
                PRIM (I::*__times__tm__) (x10tm::TMThread *SelfTM, PRIM);                              \
                PRIM (I::*_m3____plus) ();                              \
                PRIM (I::*_m3____plus__tm__) (x10tm::TMThread *SelfTM);                              \
                PRIM (I::*_m4____plus) (PRIM);                          \
                PRIM (I::*_m4____plus__tm__) (x10tm::TMThread *SelfTM, PRIM);                          \
                PRIM (I::*_m5____minus) ();                             \
                PRIM (I::*_m5____minus__tm__) (x10tm::TMThread *SelfTM);                             \
                PRIM (I::*_m6____minus) (PRIM);                         \
                PRIM (I::*_m6____minus__tm__) (x10tm::TMThread *SelfTM, PRIM);                         \
                PRIM (I::*__over) (PRIM);                               \
                PRIM (I::*__over__tm__) (x10tm::TMThread *SelfTM, PRIM);                               \
                x10::lang::String* (I::*toString) ();       \
                x10::lang::String* (I::*toString__tm__) (x10tm::TMThread *SelfTM);       \
                x10::lang::String* (I::*typeName) ();       \
                x10::lang::String* (I::*typeName__tm__) (x10tm::TMThread *SelfTM);       \
                };                                                      \
            static x10_boolean equals(PRIM recv, PRIM arg0) { return x10aux::equals(recv, arg0); } \
            static x10_int hashCode(PRIM recv) { return x10aux::hash_code(recv); } \
            static inline PRIM __times(PRIM recv, PRIM arg0) {          \
                return recv * arg0;                                     \
            }                                                           \
            static PRIM __times(Arithmetic<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->__times))(arg0); \
            }                                                           \
            static inline PRIM _m3____plus(PRIM recv) {                 \
                return recv;                                            \
            }                                                           \
            static PRIM _m3____plus(Arithmetic<PRIM>* _recv) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m3____plus))(); \
            }                                                           \
            static inline PRIM _m4____plus(PRIM recv, PRIM arg0) {      \
                return recv + arg0;                                     \
            }                                                           \
            static PRIM _m4____plus(Arithmetic<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m4____plus))(arg0); \
            }                                                           \
            static inline PRIM _m5____minus(PRIM recv) {                \
                return -recv;                                           \
            }                                                           \
            static PRIM _m5____minus(Arithmetic<PRIM>* _recv) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m5____minus))(); \
            }                                                           \
            static inline PRIM _m6____minus(PRIM recv, PRIM arg0) {     \
                return recv - arg0;                                     \
            }                                                           \
            static PRIM _m6____minus(Arithmetic<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m6____minus))(arg0); \
            }                                                           \
            static inline PRIM __over(PRIM recv, PRIM arg0) {           \
                return recv / arg0;                                     \
            }                                                           \
            static PRIM __over(Arithmetic<PRIM>* _recv, PRIM arg0) { \
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
                return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->__over))(arg0); \
            }                                                           \
            static x10::lang::String* toString(PRIM recv) { return x10aux::to_string(recv); } \
            static x10::lang::String* typeName(PRIM recv) { return x10aux::type_name(recv); } \
            \
            static x10_boolean equals__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) { return x10aux::equals(recv, arg0); } \
			static x10_int hashCode__tm__(x10tm::TMThread *SelfTM, PRIM recv) { return x10aux::hash_code(recv); } \
			static inline PRIM __times__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) {          \
				return recv * arg0;                                     \
			}                                                           \
			static PRIM __times__tm__(x10tm::TMThread *SelfTM, Arithmetic<PRIM>* _recv, PRIM arg0) { \
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
				return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->__times__tm__))(SelfTM, arg0); \
			}                                                           \
			static inline PRIM _m3____plus__tm__(x10tm::TMThread *SelfTM, PRIM recv) {                 \
				return recv;                                            \
			}                                                           \
			static PRIM _m3____plus__tm__(x10tm::TMThread *SelfTM, Arithmetic<PRIM>* _recv) { \
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
				return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m3____plus__tm__))(SelfTM); \
			}                                                           \
			static inline PRIM _m4____plus__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) {      \
				return recv + arg0;                                     \
			}                                                           \
			static PRIM _m4____plus__tm__(x10tm::TMThread *SelfTM, Arithmetic<PRIM>* _recv, PRIM arg0) { \
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
				return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m4____plus__tm__))(SelfTM, arg0); \
			}                                                           \
			static inline PRIM _m5____minus__tm__(x10tm::TMThread *SelfTM, PRIM recv) {                \
				return -recv;                                           \
			}                                                           \
			static PRIM _m5____minus__tm__(x10tm::TMThread *SelfTM, Arithmetic<PRIM>* _recv) { \
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
				return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m5____minus__tm__))(SelfTM); \
			}                                                           \
			static inline PRIM _m6____minus__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) {     \
				return recv - arg0;                                     \
			}                                                           \
			static PRIM _m6____minus__tm__(x10tm::TMThread *SelfTM, Arithmetic<PRIM>* _recv, PRIM arg0) { \
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
				return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->_m6____minus__tm__))(SelfTM, arg0); \
			}                                                           \
			static inline PRIM __over__tm__(x10tm::TMThread *SelfTM, PRIM recv, PRIM arg0) {           \
				return recv / arg0;                                     \
			}                                                           \
			static PRIM __over__tm__(x10tm::TMThread *SelfTM, Arithmetic<PRIM>* _recv, PRIM arg0) { \
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(_recv); \
				return (recv->*(x10aux::findITable<x10::lang::Arithmetic<PRIM> >(recv->_getITables())->__over__tm__))(SelfTM, arg0); \
			}                                                           \
			static x10::lang::String* toString__tm__(x10tm::TMThread *SelfTM, PRIM recv) { return x10aux::to_string(recv); } \
			static x10::lang::String* typeName__tm__(x10tm::TMThread *SelfTM, PRIM recv) { return x10aux::type_name(recv); } \
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
