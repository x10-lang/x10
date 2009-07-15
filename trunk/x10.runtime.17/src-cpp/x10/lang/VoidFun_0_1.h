#ifndef X10_LANG_VOIDFUN_0_1_H
#define X10_LANG_VOIDFUN_0_1_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_VoidFun_0_1(x10aux::RuntimeType *location,
                                        const x10aux::RuntimeType *rtt1);

        template<class P1> class VoidFun_0_1 : public virtual Object {
            public:
            RTT_H_DECLS

            virtual ~VoidFun_0_1() { }
            virtual void apply(P1 p1) = 0;
        };

        template<class P1> void VoidFun_0_1<P1>::_initRTT() {
            rtt.parentsc = -2;
            x10::lang::_initRTTHelper_VoidFun_0_1(&rtt, x10aux::getRTT<P1>());
        }

        template<class P1> x10aux::RuntimeType VoidFun_0_1<P1>::rtt;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
