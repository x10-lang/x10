#ifndef X10_LANG_VOIDFUN_0_1_H
#define X10_LANG_VOIDFUN_0_1_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/fun_utils.h>

namespace x10 {
    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_VoidFun_0_1(const x10aux::RuntimeType **location,
                                                                     const x10aux::RuntimeType *rtt1);

        template<class P1> class VoidFun_0_1 : public x10aux::AnyFun {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT();

            struct itable {
                itable(void(*apply)(x10aux::ref<VoidFun_0_1<P1> >, P1)) : apply(apply) {}
                void (*apply)(x10aux::ref<VoidFun_0_1<P1> >, P1);
            };
        };

        template<class P1> const x10aux::RuntimeType* VoidFun_0_1<P1>::_initRTT() {
            return x10::lang::_initRTTHelper_VoidFun_0_1(&rtt, x10aux::getRTT<P1>());
        }

        template<class P1> const x10aux::RuntimeType* VoidFun_0_1<P1>::rtt = NULL;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
