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

#ifndef X10_LANG_VOIDFUN_0_0_H
#define X10_LANG_VOIDFUN_0_0_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>

#include <x10/lang/Any.h>

namespace x10 {
    namespace lang {
        class VoidFun_0_0 {
            public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { if (!rtt.isInitialized) _initRTT(); return &rtt; }
            static void _initRTT();

            template <class I> struct itable {
                itable(x10_boolean (I::*equals)(x10::lang::Any*),
                		x10_boolean (I::*equals__tm__)(x10tm::TMThread *SelfTM, x10::lang::Any*),
                       x10_int (I::*hashCode)(),
                       x10_int (I::*hashCode__tm__)(x10tm::TMThread *SelfTM),
                       void(I::*__apply)(),
                       void(I::*__apply__tm__)(x10tm::TMThread *SelfTM),
                       x10::lang::String* (I::*toString)(),
                       x10::lang::String* (I::*toString__tm__)(x10tm::TMThread *SelfTM),
                       x10::lang::String* (I::*typeName)(),
                       x10::lang::String* (I::*typeName__tm__)(x10tm::TMThread *SelfTM)
                    ) : equals(equals), equals__tm__(equals__tm__), hashCode(hashCode), hashCode__tm__(hashCode__tm__), __apply(__apply), __apply__tm__(__apply__tm__), toString(toString), toString__tm__(toString__tm__), typeName(typeName), typeName__tm__(typeName__tm__) {}
                x10_boolean (I::*equals)(x10::lang::Any*);
                x10_boolean (I::*equals__tm__)(x10tm::TMThread *SelfTM, x10::lang::Any*);
                x10_int (I::*hashCode)();
                x10_int (I::*hashCode__tm__)(x10tm::TMThread *SelfTM);
                void (I::*__apply)();
                void (I::*__apply__tm__)(x10tm::TMThread *SelfTM);
                x10::lang::String* (I::*toString)();
                x10::lang::String* (I::*toString__tm__)(x10tm::TMThread *SelfTM);
                x10::lang::String* (I::*typeName)();
                x10::lang::String* (I::*typeName__tm__)(x10tm::TMThread *SelfTM);
            };

            static void __apply(VoidFun_0_0* fun) {
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
                return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->__apply))();
            }
            static x10_boolean equals(VoidFun_0_0* fun, x10::lang::Any* arg1) {
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
                return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->equals))(arg1);
            }
            static x10_int hashCode(VoidFun_0_0* fun) {
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
                return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->hashCode))();
            }
            static x10::lang::String* toString(VoidFun_0_0* fun) {
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
                return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->toString))();
            }
            static x10::lang::String* typeName(VoidFun_0_0* fun) {
                x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
                return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->typeName))();
            }

            static void __apply__tm__(x10tm::TMThread *SelfTM, VoidFun_0_0* fun) {
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
				return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->__apply__tm__))(SelfTM);
			}
			static x10_boolean equals__tm__(x10tm::TMThread *SelfTM, VoidFun_0_0* fun, x10::lang::Any* arg1) {
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
				return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->equals__tm__))(SelfTM, arg1);
			}
			static x10_int hashCode__tm__(x10tm::TMThread *SelfTM, VoidFun_0_0* fun) {
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
				return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->hashCode__tm__))(SelfTM);
			}
			static x10::lang::String* toString__tm__(x10tm::TMThread *SelfTM, VoidFun_0_0* fun) {
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
				return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->toString__tm__))(SelfTM);
			}
			static x10::lang::String* typeName__tm__(x10tm::TMThread *SelfTM, VoidFun_0_0* fun) {
				x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(fun);
				return (recv->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->typeName__tm__))(SelfTM);
			}
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
