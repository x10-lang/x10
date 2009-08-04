#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>
#include <x10/lang/VoidFun_0_0.h>

using namespace x10::lang;
using namespace x10aux;

RTT_CC_DECLS1(VoidFun_0_0, "x10.lang.VoidFun", Object)

namespace x10 {
    namespace lang {
        void
        _initRTTHelper_Fun_0_0(RuntimeType *location, const RuntimeType *rtt0) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[1] = { rtt0 };
            RuntimeType::Variance variances[1] = { RuntimeType::covariant };
            const char *name = alloc_printf("x10.lang.Fun_0_0[%s]",rtt0->name());
            location->init(name, 1, parents, 1, params, variances);
        }

        void
        _initRTTHelper_Fun_0_1(RuntimeType *location, const RuntimeType *rtt0, const RuntimeType *rtt1) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[2] = { rtt0, rtt1 };
            RuntimeType::Variance variances[2] = { RuntimeType::covariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_1[%s,%s]", rtt0->name(), rtt1->name());
            location->init(name, 1, parents, 2, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_2(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[3] = { rtt0, rtt1, rtt2 };
            RuntimeType::Variance variances[] = { RuntimeType::covariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_2[%s,%s,%s]",
                                             rtt0->name(), rtt1->name(), rtt2->name());
            location->init(name, 1, parents, 3, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_3(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[4] = { rtt0, rtt1, rtt2, rtt3 };
            RuntimeType::Variance variances[4] = { RuntimeType::covariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_3[%s,%s,%s,%s]",
                                             rtt0->name(), rtt1->name(), rtt2->name(), rtt3->name());
            location->init(name, 1, parents, 4, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_4(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3,
                               const RuntimeType *rtt4) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[5] = { rtt0, rtt1, rtt2, rtt3, rtt4 };
            RuntimeType::Variance variances[5] = { RuntimeType::covariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_4[%s,%s,%s,%s,%s]",
                                             rtt0->name(), rtt1->name(), rtt2->name(), rtt3->name(), rtt4->name());
            location->init(name, 1, parents, 5, params, variances);
        }    

        void
        _initRTTHelper_Fun_0_5(RuntimeType *location,
                               const RuntimeType *rtt0,
                               const RuntimeType *rtt1,
                               const RuntimeType *rtt2,
                               const RuntimeType *rtt3,
                               const RuntimeType *rtt4,
                               const RuntimeType *rtt5) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[6] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5 };
            RuntimeType::Variance variances[6] = { RuntimeType::covariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                  RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_5[%s,%s,%s,%s,%s,%s]",
                                             rtt0->name(), rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name());
            location->init(name, 1, parents, 6, params, variances);
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
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[7] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5, rtt6 };
            RuntimeType::Variance variances[7] = { RuntimeType::covariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_6[%s,%s,%s,%s,%s,%s,%s]",
                                             rtt0->name(), rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name(), rtt6->name());
            location->init(name, 1, parents, 7, params, variances);
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
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[8] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7 };
            RuntimeType::Variance variances[8] = { RuntimeType::covariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_7[%s,%s,%s,%s,%s,%s,%s,%s]",
                                             rtt0->name(), rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name(), rtt6->name(), rtt7->name());
            location->init(name, 1, parents, 8, params, variances);
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
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[9] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7, rtt8 };
            RuntimeType::Variance variances[9] = { RuntimeType::covariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_8[%s,%s,%s,%s,%s,%s,%s,%s,%s]",
                                             rtt0->name(), rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name(), rtt6->name(), rtt7->name(), rtt8->name());
            location->init(name, 1, parents, 9, params, variances);
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
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[10] = { rtt0, rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7, rtt8, rtt9 };
            RuntimeType::Variance variances[10] = { RuntimeType::covariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.Fun_0_9[%s,%s,%s,%s,%s,%s,%s,%s,%s,%s]",
                                             rtt0->name(), rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name(), rtt6->name(), rtt7->name(), rtt8->name(), rtt9->name());
            location->init(name, 1, parents, 9, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_1(RuntimeType *location, const RuntimeType *rtt1) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[1] = { rtt1 };
            RuntimeType::Variance variances[] = { RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_1[%s]", rtt1->name());
            location->init(name, 1, parents, 1, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_2(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[2] = { rtt1, rtt2 };
            RuntimeType::Variance variances[2] = { RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_2[%s,%s]",
                                             rtt1->name(), rtt2->name());
            location->init(name, 1, parents, 2, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_3(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[3] = { rtt1, rtt2, rtt3 };
            RuntimeType::Variance variances[3] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_3[%s,%s,%s]",
                                             rtt1->name(), rtt2->name(), rtt3->name());
            location->init(name, 1, parents, 3, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_4(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[4] = { rtt1, rtt2, rtt3, rtt4 };
            RuntimeType::Variance variances[4] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_4[%s,%s,%s,%s]",
                                             rtt1->name(), rtt2->name(), rtt3->name(), rtt4->name());
            location->init(name, 1, parents, 4, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_5(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4,
                                   const RuntimeType *rtt5) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[5] = { rtt1, rtt2, rtt3, rtt4, rtt5 };
            RuntimeType::Variance variances[5] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_5[%s,%s,%s,%s,%s]",
                                             rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name());
            location->init(name, 1, parents, 5, params, variances);
        }    

        void
        _initRTTHelper_VoidFun_0_6(RuntimeType *location,
                                   const RuntimeType *rtt1,
                                   const RuntimeType *rtt2,
                                   const RuntimeType *rtt3,
                                   const RuntimeType *rtt4,
                                   const RuntimeType *rtt5,
                                   const RuntimeType *rtt6) {
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[6] = { rtt1, rtt2, rtt3, rtt4, rtt5, rtt6 };
            RuntimeType::Variance variances[6] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_6[%s,%s,%s,%s,%s,%s]",
                                             rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name(), rtt6->name());
            location->init(name, 1, parents, 6, params, variances);
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
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[7] = { rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7 };
            RuntimeType::Variance variances[7] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_7[%s,%s,%s,%s,%s,%s,%s]",
                                             rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name(), rtt6->name(), rtt7->name());
            location->init(name, 1, parents, 7, params, variances);
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
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[8] = { rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7, rtt8 };
            RuntimeType::Variance variances[8] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_8[%s,%s,%s,%s,%s,%s,%s,%s]",
                                             rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name(), rtt6->name(),
                                             rtt7->name(), rtt8->name());
            location->init(name, 1, parents, 8, params, variances);
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
            const RuntimeType* parents[1] = { Object::getRTT() };
            const RuntimeType* params[9] = { rtt1, rtt2, rtt3, rtt4, rtt5, rtt6, rtt7, rtt8, rtt9 };
            RuntimeType::Variance variances[9] = { RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant,
                                                   RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant, RuntimeType::contravariant };
            const char *name =  alloc_printf("x10.lang.VoidFun_0_9[%s,%s,%s,%s,%s,%s,%s,%s,%s]",
                                             rtt1->name(), rtt2->name(), rtt3->name(),
                                             rtt4->name(), rtt5->name(), rtt6->name(),
                                             rtt7->name(), rtt8->name(), rtt9->name());
            location->init(name, 1, parents, 9, params, variances);
        }    
    }
}
