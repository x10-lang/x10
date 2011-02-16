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
#include <x10aux/fun_utils.h>

#include <x10/lang/Any.h>

namespace x10 {
    namespace lang {
        class VoidFun_0_0 : public x10aux::AnyFun {
            public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { if (!rtt.isInitialized) _initRTT(); return &rtt; }
            static void _initRTT();

            template <class I> struct itable {
                itable(x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>),
                       x10_int (I::*hashCode)(),
                       void(I::*__apply)(),
                       x10aux::ref<x10::lang::String> (I::*toString)(),
                       x10aux::ref<x10::lang::String> (I::*typeName)()
                    ) : equals(equals), hashCode(hashCode), __apply(__apply), toString(toString), typeName(typeName) {}
                x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>);
                x10_int (I::*hashCode)();
                void (I::*__apply)();
                x10aux::ref<x10::lang::String> (I::*toString)();
                x10aux::ref<x10::lang::String> (I::*typeName)();
            };

            static void __apply(x10aux::ref<x10::lang::Reference> recv) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->__apply))();
            }
            static x10_boolean equals(x10aux::ref<x10::lang::Reference> recv, x10aux::ref<x10::lang::Any> arg1) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->equals))(arg1);
            }
            static x10_int hashCode(x10aux::ref<x10::lang::Reference> recv) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->hashCode))();
            }
            static x10aux::ref<x10::lang::String> toString(x10aux::ref<x10::lang::Reference> recv) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->toString))();
            }
            static x10aux::ref<x10::lang::String> typeName(x10aux::ref<x10::lang::Reference> recv) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_0>(recv->_getITables())->typeName))();
            }
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
