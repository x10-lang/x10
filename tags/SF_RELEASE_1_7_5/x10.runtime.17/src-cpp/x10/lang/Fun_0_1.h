#ifndef X10_LANG_FUN_0_1_H
#define X10_LANG_FUN_0_1_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_Fun_0_1(const x10aux::RuntimeType **location,
                                                                 const x10aux::RuntimeType *rtt0,
                                                                 const x10aux::RuntimeType *rtt1);

        template<class P1, class R> class Fun_0_1 : public virtual Object {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT();
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            virtual ~Fun_0_1() { }
            virtual R apply(P1 p1) = 0;
        };

        template<class P1, class R> const x10aux::RuntimeType* Fun_0_1<P1,R>::_initRTT() {
            return x10::lang::_initRTTHelper_Fun_0_1(&rtt, x10aux::getRTT<P1>(), x10aux::getRTT<R>());
        }

        template<class P1, class R> const x10aux::RuntimeType* Fun_0_1<P1,R>::rtt = NULL;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
