#ifndef X10_LANG_FUN_0_1_H
#define X10_LANG_FUN_0_1_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {
        template<class P1, class R> class Fun_0_1 : public virtual Object {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT() {
                const char *name =
                    x10aux::alloc_printf("x10.lang.Fun_0_1[%s,%s]",
                                         x10aux::getRTT<P1>()->name(),
                                         x10aux::getRTT<R>()->name());
                const x10aux::RuntimeType *parent = x10::lang::Object::getRTT();
                const x10aux::RuntimeType *cand = new (x10aux::alloc<x10aux::RuntimeType >()) x10aux::RuntimeType(name, 1, parent);
                return x10aux::RuntimeType::installRTT(&rtt, cand);
            }
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            virtual ~Fun_0_1() { }
            virtual R apply(P1 p1) = 0;
        };

        template<class P1, class R> const x10aux::RuntimeType* Fun_0_1<P1,R>::rtt = NULL;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
