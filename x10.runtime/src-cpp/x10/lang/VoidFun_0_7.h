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

#ifndef X10_LANG_VOIDFUN_0_7_H
#define X10_LANG_VOIDFUN_0_7_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/fun_utils.h>

#include <x10/lang/Any.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_VoidFun_0_7(x10aux::RuntimeType *location,
                                        const x10aux::RuntimeType *rtt1,
                                        const x10aux::RuntimeType *rtt2,
                                        const x10aux::RuntimeType *rtt3,
                                        const x10aux::RuntimeType *rtt4,
                                        const x10aux::RuntimeType *rtt5,
                                        const x10aux::RuntimeType *rtt6,
                                        const x10aux::RuntimeType *rtt7);

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7> class VoidFun_0_7 : public x10aux::AnyFun {
            public:
            static x10aux::RuntimeVoidFunType rtt;
            static const x10aux::RuntimeType* getRTT() { if (!rtt.isInitialized) _initRTT(); return &rtt; }
            static void _initRTT();

            template <class I> struct itable {
                itable(void(I::*apply)(P1,P2,P3,P4,P5,P6,P7),
                       x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>),
                       x10_int (I::*hashCode)(),
                       x10aux::ref<x10::lang::String> (I::*toString)(),
                       x10aux::ref<x10::lang::String> (I::*typeName)()
                    ) : apply(apply), equals(equals), hashCode(hashCode), toString(toString), typeName(typeName) {}
                void (I::*apply)(P1,P2,P3,P4,P5,P6,P7);
                x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>);
                x10_int (I::*hashCode)();
                x10aux::ref<x10::lang::String> (I::*toString)();
                x10aux::ref<x10::lang::String> (I::*typeName)();
            };

            static void apply(x10aux::ref<x10::lang::Reference> recv, P1 arg1, P2 arg2, P3 arg3, P4 arg4, P5 arg5, P6 arg6, P7 arg7) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_7<P1,P2,P3,P4,P5,P6,P7> >(recv->_getITables())->apply))(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
            }
            static x10_boolean equals(x10aux::ref<x10::lang::Reference> recv, x10aux::ref<x10::lang::Any> arg1) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_7<P1,P2,P3,P4,P5,P6,P7> >(recv->_getITables())->equals))(arg1);
            }
            static x10_int hashCode(x10aux::ref<x10::lang::Reference> recv) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_7<P1,P2,P3,P4,P5,P6,P7> >(recv->_getITables())->hashCode))();
            }
            static x10aux::ref<x10::lang::String> toString(x10aux::ref<x10::lang::Reference> recv) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_7<P1,P2,P3,P4,P5,P6,P7> >(recv->_getITables())->toString))();
            }
            static x10aux::ref<x10::lang::String> typeName(x10aux::ref<x10::lang::Reference> recv) {
                return (recv.operator->()->*(x10aux::findITable<VoidFun_0_7<P1,P2,P3,P4,P5,P6,P7> >(recv->_getITables())->typeName))();
            }
        };

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7>
            void VoidFun_0_7<P1,P2,P3,P4,P5,P6,P7>::_initRTT() {
            if (rtt.initStageOne(x10aux::getRTT<VoidFun_0_7<void,void,void,void,void,void,void> >())) return;
            x10::lang::_initRTTHelper_VoidFun_0_7(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<P2>(), 
                                                        x10aux::getRTT<P3>(), x10aux::getRTT<P4>(), 
                                                        x10aux::getRTT<P5>(), x10aux::getRTT<P6>(),
                                                        x10aux::getRTT<P7>());
        }

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7>
            x10aux::RuntimeVoidFunType VoidFun_0_7<P1,P2,P3,P4,P5,P6,P7>::rtt;

        template<> class VoidFun_0_7<void,void,void,void,void,void,void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
