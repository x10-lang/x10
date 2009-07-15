#ifndef X10_LANG_FUN_0_5_H
#define X10_LANG_FUN_0_5_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_Fun_0_5(x10aux::RuntimeType *location,
                                    const x10aux::RuntimeType *rtt0,
                                    const x10aux::RuntimeType *rtt1,
                                    const x10aux::RuntimeType *rtt2,
                                    const x10aux::RuntimeType *rtt3,
                                    const x10aux::RuntimeType *rtt4,
                                    const x10aux::RuntimeType *rtt5);

        template<class P1, class P2, class P3, class P4, class P5, class R> class Fun_0_5 : public virtual Object {
            public:
            RTT_H_DECLS

            virtual ~Fun_0_5() { };
            virtual R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) = 0;
        };

        template<class P1, class P2, class P3, class P4, class P5, class R>
            void Fun_0_5<P1,P2,P3,P4,P5,R>::_initRTT() {
            rtt.parentsc = -2;
            x10::lang::_initRTTHelper_Fun_0_5(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<P2>(), 
                                                    x10aux::getRTT<P3>(), x10aux::getRTT<P4>(), 
                                                    x10aux::getRTT<P5>(), x10aux::getRTT<R>());
        }
        
        template<class P1, class P2, class P3, class P4, class P5, class R>
            x10aux::RuntimeType Fun_0_5<P1,P2,P3,P4,P5,R>::rtt;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
