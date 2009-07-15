#ifndef X10_LANG_VOIDFUN_0_3_H
#define X10_LANG_VOIDFUN_0_3_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_VoidFun_0_3(x10aux::RuntimeType *location,
                                        const x10aux::RuntimeType *rtt1,
                                        const x10aux::RuntimeType *rtt2,
                                        const x10aux::RuntimeType *rtt3);

        template<class P1, class P2, class P3> class VoidFun_0_3 : public virtual Object {
            public:
            RTT_H_DECLS

            virtual ~VoidFun_0_3() { }
            virtual void apply(P1 p1, P2 p2, P3 p3) = 0;
        };

        template<class P1, class P2, class P3> void VoidFun_0_3<P1,P2,P3>::_initRTT() {
            rtt.parentsc = -2;
            x10::lang::_initRTTHelper_VoidFun_0_3(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<P2>(), 
                                                        x10aux::getRTT<P3>());
        }

        template<class P1, class P2, class P3> x10aux::RuntimeType VoidFun_0_3<P1,P2,P3>::rtt;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
