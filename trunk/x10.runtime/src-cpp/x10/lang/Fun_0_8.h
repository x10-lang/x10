#ifndef X10_LANG_FUN_0_8_H
#define X10_LANG_FUN_0_8_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/fun_utils.h>

#include <x10/lang/Any.h>

namespace x10 {
    namespace lang {
        void _initRTTHelper_Fun_0_8(x10aux::RuntimeType *location,
                                    const x10aux::RuntimeType *rtt0,
                                    const x10aux::RuntimeType *rtt1,
                                    const x10aux::RuntimeType *rtt2,
                                    const x10aux::RuntimeType *rtt3,
                                    const x10aux::RuntimeType *rtt4,
                                    const x10aux::RuntimeType *rtt5,
                                    const x10aux::RuntimeType *rtt6,
                                    const x10aux::RuntimeType *rtt7,
                                    const x10aux::RuntimeType *rtt8);

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7, class P8, class R> class Fun_0_8 : public x10aux::AnyFun {
            public:
            RTT_H_DECLS_INTERFACE

            template <class I> struct itable {
                itable(R(I::*apply)(P1,P2,P3,P4,P5,P6,P7,P8),
                       x10_boolean (I::*_m0__at)(x10aux::ref<x10::lang::Object>),
                       x10_boolean (I::*_m1__at)(x10::lang::Place),
                       x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>),
                       x10_int (I::*hashCode)(),
                       x10::lang::Place (I::*home)(),
                       x10aux::ref<x10::lang::String> (I::*toString)(),
                       x10aux::ref<x10::lang::String> (I::*typeName)()
                    ) : apply(apply), _m0__at(_m0__at), _m1__at(_m1__at), equals(equals), hashCode(hashCode), home(home), toString(toString), typeName(typeName) {}
                R (I::*apply)(P1,P2,P3,P4,P5,P6,P7,P8);
                x10_boolean (I::*_m0__at)(x10aux::ref<x10::lang::Object>);
                x10_boolean (I::*_m1__at)(x10::lang::Place);
                x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>);
                x10_int (I::*hashCode)();
                x10::lang::Place (I::*home)();
                x10aux::ref<x10::lang::String> (I::*toString)();
                x10aux::ref<x10::lang::String> (I::*typeName)();
            };
        };                

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7, class P8, class R>
            void Fun_0_8<P1,P2,P3,P4,P5,P6,P7,P8,R>::_initRTT() {
            x10::lang::_initRTTHelper_Fun_0_8(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<P2>(), 
                                                    x10aux::getRTT<P3>(), x10aux::getRTT<P4>(), 
                                                    x10aux::getRTT<P5>(), x10aux::getRTT<P6>(),
                                                    x10aux::getRTT<P7>(), x10aux::getRTT<P8>(),
                                                    x10aux::getRTT<R>());
        }

        template<class P1, class P2, class P3, class P4, class P5, class P6, class P7, class P8, class R>
            x10aux::RuntimeType Fun_0_8<P1,P2,P3,P4,P5,P6,P7,P8,R>::rtt;

        template<> class Fun_0_8<void, void, void, void, void, void, void, void, void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
