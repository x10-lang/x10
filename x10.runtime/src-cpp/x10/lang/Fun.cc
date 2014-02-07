/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Fun_0_0.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/lang/Fun_0_2.h>
#include <x10/lang/Fun_0_3.h>
#include <x10/lang/Fun_0_4.h>
#include <x10/lang/Fun_0_5.h>
#include <x10/lang/Fun_0_6.h>
#include <x10/lang/Fun_0_7.h>
#include <x10/lang/Fun_0_8.h>
#include <x10/lang/Fun_0_9.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/VoidFun_0_1.h>
#include <x10/lang/VoidFun_0_2.h>
#include <x10/lang/VoidFun_0_3.h>
#include <x10/lang/VoidFun_0_4.h>
#include <x10/lang/VoidFun_0_5.h>
#include <x10/lang/VoidFun_0_6.h>
#include <x10/lang/VoidFun_0_7.h>
#include <x10/lang/VoidFun_0_8.h>
#include <x10/lang/VoidFun_0_9.h>

using namespace x10::lang;
using namespace x10aux;

x10aux::RuntimeType Fun_0_0<void>::rtt;
x10aux::RuntimeType Fun_0_1<void,void>::rtt;
x10aux::RuntimeType Fun_0_2<void,void,void>::rtt;
x10aux::RuntimeType Fun_0_3<void,void,void,void>::rtt;
x10aux::RuntimeType Fun_0_4<void,void,void,void,void>::rtt;
x10aux::RuntimeType Fun_0_5<void,void,void,void,void,void>::rtt;
x10aux::RuntimeType Fun_0_6<void,void,void,void,void,void,void>::rtt;
x10aux::RuntimeType Fun_0_7<void,void,void,void,void,void,void,void>::rtt;
x10aux::RuntimeType Fun_0_8<void,void,void,void,void,void,void,void,void>::rtt;
x10aux::RuntimeType Fun_0_9<void,void,void,void,void,void,void,void,void,void>::rtt;

x10aux::RuntimeType VoidFun_0_0::rtt;
x10aux::RuntimeType VoidFun_0_1<void>::rtt;
x10aux::RuntimeType VoidFun_0_2<void,void>::rtt;
x10aux::RuntimeType VoidFun_0_3<void,void,void>::rtt;
x10aux::RuntimeType VoidFun_0_4<void,void,void,void>::rtt;
x10aux::RuntimeType VoidFun_0_5<void,void,void,void,void>::rtt;
x10aux::RuntimeType VoidFun_0_6<void,void,void,void,void,void>::rtt;
x10aux::RuntimeType VoidFun_0_7<void,void,void,void,void,void,void>::rtt;
x10aux::RuntimeType VoidFun_0_8<void,void,void,void,void,void,void,void>::rtt;
x10aux::RuntimeType VoidFun_0_9<void,void,void,void,void,void,void,void,void>::rtt;

namespace x10 {
    namespace lang {

        void
        VoidFun_0_0::_initRTT() {
            if (rtt.initStageOne(&rtt)) return;
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            rtt.initStageTwo("()=>void", RuntimeType::interface_kind, 1, parents, 0, NULL, NULL);
        }

        void
        _initRTTHelper_Fun_0_0(RuntimeType *location, const RuntimeType *rtt0) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[1] = { rtt0 };
            RuntimeType::Variance variances[1] = { RuntimeType::covariant };
            location->initStageTwo("()=>R", RuntimeType::interface_kind, 1, parents, 1, params, variances);
        }

        void
        _initRTTHelper_Fun_0_1(RuntimeType *location, const RuntimeType *rtt0, const RuntimeType *rtt1) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[2] = { rtt0, rtt1 };
            RuntimeType::Variance variances[2] = { RuntimeType::contravariant, RuntimeType::covariant };
            location->initStageTwo("(P1)=>R", RuntimeType::interface_kind, 1, parents, 2, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_2(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[3] = { rtt0, rtt1, rtt2 };
            RuntimeType::Variance variances[] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::covariant };
            location->initStageTwo("(P1,P2)=>R", RuntimeType::interface_kind, 1, parents, 3, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_3(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[4] = { rtt0, rtt1, rtt2, rtt3 };
            RuntimeType::Variance variances[4] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::covariant };
            location->initStageTwo("(P1,P2,P3)=>R", RuntimeType::interface_kind, 1, parents, 4, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_4(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3,
                               const RuntimeType *rtt4) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[5] = { rtt0, rtt1, rtt2, rtt3, rtt4 };
            RuntimeType::Variance variances[5] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::covariant };
            location->initStageTwo("(P1,P2,P3,P4)=>R", RuntimeType::interface_kind, 1, parents, 5, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_5(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3,
                               const RuntimeType *rtt4,
                               const RuntimeType *rtt5) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[6] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5 };
            RuntimeType::Variance variances[6] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::covariant };
            location->initStageTwo("(P1,P2,P3,P4,P5)=>R", RuntimeType::interface_kind, 1, parents, 6, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_6(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3,
                               const RuntimeType *rtt4,
                               const RuntimeType *rtt5,
                               const RuntimeType *rtt6) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[7] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5, rtt6 };
            RuntimeType::Variance variances[7] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::covariant };
            location->initStageTwo("(P1,P2,P3,P4,P5,P6)=>R", RuntimeType::interface_kind, 1, parents, 7, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_7(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3,
                               const RuntimeType *rtt4,
                               const RuntimeType *rtt5,
                               const RuntimeType *rtt6,
                               const RuntimeType *rtt7) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[8] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7 };
            RuntimeType::Variance variances[8] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::covariant };
            location->initStageTwo("(P1,P2,P3,P4,P5,P6,P7)=>R", RuntimeType::interface_kind, 1, parents, 8, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_8(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3,
                               const RuntimeType *rtt4,
                               const RuntimeType *rtt5,
                               const RuntimeType *rtt6,
                               const RuntimeType *rtt7,
                               const RuntimeType *rtt8) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[9] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7, rtt8 };
            RuntimeType::Variance variances[9] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::covariant };
            location->initStageTwo("(P1,P2,P3,P4,P5,P6,P7,P8)=>R", RuntimeType::interface_kind, 1, parents, 9, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_9(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3,
                               const RuntimeType *rtt4,
                               const RuntimeType *rtt5,
                               const RuntimeType *rtt6,
                               const RuntimeType *rtt7,
                               const RuntimeType *rtt8,
                               const RuntimeType *rtt9) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[10] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7, rtt8, rtt9 };
            RuntimeType::Variance variances[10] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                    RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                    RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                    RuntimeType::covariant };
            location->initStageTwo("(P1,P2,P3,P4,P5,P6,P7,P8,P9)=>R", RuntimeType::interface_kind, 1, parents, 10, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_1(RuntimeType *location, const RuntimeType *rtt1) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[1] = { rtt1 };
            RuntimeType::Variance variances[] = { RuntimeType::contravariant };
            location->initStageTwo("(P1)=>void", RuntimeType::interface_kind, 1, parents, 1, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_2(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[2] = { rtt1, rtt2 };
            RuntimeType::Variance variances[2] = { RuntimeType::contravariant, RuntimeType::contravariant };
            location->initStageTwo("(P1,P2)=>void", RuntimeType::interface_kind, 1, parents, 2, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_3(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[3] = { rtt1, rtt2, rtt3 };
            RuntimeType::Variance variances[3] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            location->initStageTwo("(P1,P2,P3)=>void", RuntimeType::interface_kind, 1, parents, 3, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_4(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[4] = { rtt1, rtt2, rtt3, rtt4 };
            RuntimeType::Variance variances[4] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            location->initStageTwo("(P1,P2,P3,P4)=>void", RuntimeType::interface_kind, 1, parents, 4, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_5(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4,
                                   const RuntimeType *rtt5) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[5] = { rtt1, rtt2, rtt3, rtt4, rtt5 };
            RuntimeType::Variance variances[5] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            location->initStageTwo("(P1,P2,P3,P4,P5)=>void", RuntimeType::interface_kind, 1, parents, 5, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_6(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4,
                                   const RuntimeType *rtt5,
                                   const RuntimeType *rtt6) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[6] = { rtt1, rtt2, rtt3, rtt4, rtt5, rtt6 };
            RuntimeType::Variance variances[6] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant };
            location->initStageTwo("(P1,P2,P3,P4,P5,P6)=>void", RuntimeType::interface_kind, 1, parents, 6, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_7(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4,
                                   const RuntimeType *rtt5,
                                   const RuntimeType *rtt6,
                                   const RuntimeType *rtt7) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[7] = { rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7 };
            RuntimeType::Variance variances[7] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant };
            location->initStageTwo("(P1,P2,P3,P4,P5,P6,P7)=>void", RuntimeType::interface_kind, 1, parents, 7, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_8(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4,
                                   const RuntimeType *rtt5,
                                   const RuntimeType *rtt6,
                                   const RuntimeType *rtt7,
                                   const RuntimeType *rtt8) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[8] = { rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7, rtt8 };
            RuntimeType::Variance variances[8] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            location->initStageTwo("(P1,P2,P3,P4,P5,P6,P7,P8)=>void", RuntimeType::interface_kind, 1, parents, 8, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_9(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4,
                                   const RuntimeType *rtt5,
                                   const RuntimeType *rtt6,
                                   const RuntimeType *rtt7,
                                   const RuntimeType *rtt8,
                                   const RuntimeType *rtt9) {
            const RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
            const RuntimeType* params[9] = { rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7, rtt8, rtt9 };
            RuntimeType::Variance variances[9] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            location->initStageTwo("(P1,P2,P3,P4,P5,P6,P7,P8,P9)=>void", RuntimeType::interface_kind, 1, parents, 9, params, variances);
        }    
    }
}
