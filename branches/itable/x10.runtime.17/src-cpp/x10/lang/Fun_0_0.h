#ifndef X10_LANG_FUN_0_0_H
#define X10_LANG_FUN_0_0_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/fun_utils.h>

namespace x10 {
    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_Fun_0_0(const x10aux::RuntimeType **location, const x10aux::RuntimeType *rtt0);

        template<class R> class Fun_0_0 : public x10aux::AnyFun{
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT();

            template <class I> struct itable {
                itable(R(I::*apply)()) : apply(apply) {}
                R (I::*apply)();
            };
        };

        template<class R> const x10aux::RuntimeType* Fun_0_0<R>::_initRTT() {
            return x10::lang::_initRTTHelper_Fun_0_0(&rtt, x10aux::getRTT<R>());
        }

        template<class R> const x10aux::RuntimeType* Fun_0_0<R>::rtt = NULL;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
