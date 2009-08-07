#ifndef X10_LANG_FUN_0_1_H
#define X10_LANG_FUN_0_1_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/fun_utils.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_Fun_0_1(x10aux::RuntimeType *location,
                                    const x10aux::RuntimeType *rtt0,
                                    const x10aux::RuntimeType *rtt1);

        template<class P1, class R> class Fun_0_1 : public x10aux::AnyFun {
            public:
            RTT_H_DECLS_INTERFACE

            template <class I> struct itable {
                itable(R (I::*apply)(P1)) : apply(apply) {}
                R (I::*apply)(P1);
            };
        };

        template<class P1, class R> void Fun_0_1<P1,R>::_initRTT() {
            rtt.canonical = &rtt;
            x10::lang::_initRTTHelper_Fun_0_1(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<R>());
        }

        template<class P1, class R> x10aux::RuntimeType Fun_0_1<P1,R>::rtt;

        template<> class Fun_0_1<void, void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
