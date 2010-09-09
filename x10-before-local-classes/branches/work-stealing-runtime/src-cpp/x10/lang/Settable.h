#ifndef X10_LANG_SETTABLE_H
#define X10_LANG_SETTABLE_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {
        template<class I, class V> class Settable : public virtual Object {
            public:
            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * const it;
                virtual void init() { initParents(1,x10aux::getRTT<Object>()); }
                virtual const char *name() const {
                    static const char *name =
                        x10aux::alloc_printf("x10.lang.Settable[%s,%s]",
                                             x10aux::getRTT<I>()->name(),
                                             x10aux::getRTT<V>()->name());
                    return name;
                }
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<x10::lang::Settable<I, V> >();
            }

            virtual V set(V v, I i) = 0;
        };
        template<class I, class V> typename Settable<I, V>::RTT * const Settable<I, V>::RTT::it =
            new (x10aux::alloc<typename Settable<I, V>::RTT>()) typename Settable<I, V>::RTT();
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
