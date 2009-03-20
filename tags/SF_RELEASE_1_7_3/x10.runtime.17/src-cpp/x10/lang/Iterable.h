#ifndef X10_LANG_ITERABLE_H
#define X10_LANG_ITERABLE_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {
        template<class T> class Iterable : public virtual Object {
            public:
            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * const it;
                virtual void init() { initParents(1,x10aux::getRTT<Object>()); }
                virtual const char *name() const {
                    static const char *name =
                        x10aux::alloc_printf("x10.lang.Iterable[%s]",
                                             x10aux::getRTT<T>()->name());
                    return name;
                }
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Iterable<T> >();
            }

            virtual x10aux::ref<Iterator<T > > iterator() = 0;
        };
        template<class T> typename Iterable<T>::RTT * const Iterable<T>::RTT::it =
            new (x10aux::alloc<typename Iterable<T>::RTT>()) typename Iterable<T>::RTT();

    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
