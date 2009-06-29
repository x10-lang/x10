#ifndef X10_LANG_VOIDFUN_0_9_H
#define X10_LANG_VOIDFUN_0_9_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_VoidFun_0_9(const x10aux::RuntimeType **location,
                                                                     const x10aux::RuntimeType *rtt1,
                                                                     const x10aux::RuntimeType *rtt2,
                                                                     const x10aux::RuntimeType *rtt3,
                                                                     const x10aux::RuntimeType *rtt4,
                                                                     const x10aux::RuntimeType *rtt5,
                                                                     const x10aux::RuntimeType *rtt6,
                                                                     const x10aux::RuntimeType *rtt7,
                                                                     const x10aux::RuntimeType *rtt8,
                                                                     const x10aux::RuntimeType *rtt9);

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7, class P8, class P9> class VoidFun_0_9 : public virtual Object {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT(); 
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            virtual ~VoidFun_0_9() { }
            virtual void apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9) = 0;
        };

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7, class P8, class P9>
            const x10aux::RuntimeType* VoidFun_0_9<P1,P2,P3,P4,P5,P6,P7,P8,P9>::_initRTT() {
            return x10::lang::_initRTTHelper_VoidFun_0_9(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<P2>(), 
                                                         x10aux::getRTT<P3>(), x10aux::getRTT<P4>(), 
                                                         x10aux::getRTT<P5>(), x10aux::getRTT<P6>(),
                                                         x10aux::getRTT<P7>(), x10aux::getRTT<P8>(),
                                                         x10aux::getRTT<P9>());
        }

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7, class P8, class P9>
            const x10aux::RuntimeType* VoidFun_0_9<P1,P2,P3,P4,P5,P6,P7,P8,P9>::rtt = NULL;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
