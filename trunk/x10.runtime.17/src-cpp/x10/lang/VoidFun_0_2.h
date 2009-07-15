#ifndef X10_LANG_VOIDFUN_0_2_H
#define X10_LANG_VOIDFUN_0_2_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_VoidFun_0_2(x10aux::RuntimeType *location,
                                        const x10aux::RuntimeType *rtt1,
                                        const x10aux::RuntimeType *rtt2);

        template<class P1, class P2> class VoidFun_0_2 : public virtual Object {
            public:
            RTT_H_DECLS

            virtual ~VoidFun_0_2() { }
            virtual void apply(P1 p1, P2 p2) = 0;
        };

        template<class P1, class P2> void VoidFun_0_2<P1,P2>::_initRTT() {
            rtt.parentsc = -2;
            x10::lang::_initRTTHelper_VoidFun_0_2(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<P2>());
        }

        template<class P1, class P2> x10aux::RuntimeType VoidFun_0_2<P1,P2>::rtt;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
