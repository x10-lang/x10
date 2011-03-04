#ifndef X10_LANG_ITERATOR_H
#define X10_LANG_ITERATOR_H


#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/alloc.h>

#include <x10/lang/Object.h>



namespace x10 {

    namespace lang {

        template<class T> class Iterator : public virtual Object {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT() {
                const char *name =
                    x10aux::alloc_printf("x10.lang.Iterator[%s]",x10aux::getRTT<T>()->name());
                const x10aux::RuntimeType *parent = x10::lang::Object::getRTT();
                const x10aux::RuntimeType *cand = new (x10aux::alloc<x10aux::RuntimeType >()) x10aux::RuntimeType(name, 1, parent);
                return x10aux::RuntimeType::installRTT(&rtt, cand);
            }
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            virtual x10_boolean hasNext() = 0;

            virtual T next() = 0;

        };

        template<class T> const x10aux::RuntimeType *Iterator<T>::rtt = NULL;
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
