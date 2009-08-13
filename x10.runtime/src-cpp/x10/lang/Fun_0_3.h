#ifndef X10_LANG_FUN_0_3_H
#define X10_LANG_FUN_0_3_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/fun_utils.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_Fun_0_3(x10aux::RuntimeType *location,
                                    const x10aux::RuntimeType *rtt0,
                                    const x10aux::RuntimeType *rtt1,
                                    const x10aux::RuntimeType *rtt2,
                                    const x10aux::RuntimeType *rtt3);

        template<class P1, class P2, class P3, class R> class Fun_0_3 : public x10aux::AnyFun {
            public:
            RTT_H_DECLS_INTERFACE

            template <class I> struct itable {
                itable(R(I::*apply)(P1,P2,P3)) : apply(apply) {}
                R (I::*apply)(P1,P2,P3);
            };
        };

        template<class P1, class P2, class P3, class R> void Fun_0_3<P1,P2,P3,R>::_initRTT() {
            rtt.canonical = &rtt;
            x10::lang::_initRTTHelper_Fun_0_3(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<P2>(),
                                              x10aux::getRTT<P3>(), x10aux::getRTT<R>());
        }
        
        template<class P1, class P2, class P3, class R> x10aux::RuntimeType Fun_0_3<P1,P2,P3,R>::rtt;

        template<> class Fun_0_3<void, void, void, void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
