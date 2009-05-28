#ifndef X10_LANG_SETTABLE_H
#define X10_LANG_SETTABLE_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {
        template<class I, class V> class Settable : public virtual Object {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT() {
                const char *name =
                    x10aux::alloc_printf("x10.lang.Settable[%s,%s]",
                                         x10aux::getRTT<I>()->name(),
                                         x10aux::getRTT<V>()->name());
                const x10aux::RuntimeType *parent = x10::lang::Object::getRTT();
                const x10aux::RuntimeType *cand = new (x10aux::alloc<x10aux::RuntimeType >()) x10aux::RuntimeType(name, 1, parent);
                return x10aux::RuntimeType::installRTT(&rtt, cand);
            }
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            virtual V set(V v, I i) = 0;
        };

        template<class I, class V> const x10aux::RuntimeType *Settable<I, V>::rtt = NULL;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
