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

#ifndef X10_LANG_VOIDFUN_0_1_H
#define X10_LANG_VOIDFUN_0_1_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Any.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_VoidFun_0_1(::x10aux::RuntimeType *location,
                                        const ::x10aux::RuntimeType *rtt1);

        template<class P1> class VoidFun_0_1 {
            public:
            static ::x10aux::RuntimeVoidFunType rtt;
            static const ::x10aux::RuntimeType* getRTT() { if (!rtt.isInitialized) _initRTT(); return &rtt; }
            static void _initRTT();

            template <class Iface> struct itable {
                itable(x10_boolean (Iface::*equals)(::x10::lang::Any*),
                       x10_int (Iface::*hashCode)(),
                       void(Iface::*__apply)(P1),
                       ::x10::lang::String* (Iface::*toString)(),
                       ::x10::lang::String* (Iface::*typeName)()
                    ) : equals(equals), hashCode(hashCode), __apply(__apply), toString(toString), typeName(typeName) {}
                x10_boolean (Iface::*equals)(::x10::lang::Any*);
                x10_int (Iface::*hashCode)();
                void (Iface::*__apply)(P1);
                ::x10::lang::String* (Iface::*toString)();
                ::x10::lang::String* (Iface::*typeName)();
            };

            static void __apply(VoidFun_0_1<P1>* fun, P1 arg1) {
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(fun);
                return (recv->*(::x10aux::findITable<VoidFun_0_1<P1> >(recv->_getITables())->__apply))(arg1);
            }
            static x10_boolean equals(VoidFun_0_1<P1>* fun, ::x10::lang::Any* arg1) {
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(fun);
                return (recv->*(::x10aux::findITable<VoidFun_0_1<P1> >(recv->_getITables())->equals))(arg1);
            }
            static x10_int hashCode(VoidFun_0_1<P1>* fun) {
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(fun);
                return (recv->*(::x10aux::findITable<VoidFun_0_1<P1> >(recv->_getITables())->hashCode))();
            }
            static ::x10::lang::String* toString(VoidFun_0_1<P1>* fun) {
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(fun);
                return (recv->*(::x10aux::findITable<VoidFun_0_1<P1> >(recv->_getITables())->toString))();
            }
            static ::x10::lang::String* typeName(VoidFun_0_1<P1>* fun) {
                ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(fun);
                return (recv->*(::x10aux::findITable<VoidFun_0_1<P1> >(recv->_getITables())->typeName))();
            }
        };

        template<class P1> void VoidFun_0_1<P1>::_initRTT() {
            if (rtt.initStageOne(::x10aux::getRTT<VoidFun_0_1<void> >())) return;
            ::x10::lang::_initRTTHelper_VoidFun_0_1(&rtt, ::x10aux::getRTT<P1>());
        }

        template<class P1> ::x10aux::RuntimeVoidFunType VoidFun_0_1<P1>::rtt;

        template<> class VoidFun_0_1<void> {
        public:
            static ::x10aux::RuntimeType rtt;
            static const ::x10aux::RuntimeType* getRTT() { return &rtt; }
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
