#ifndef X10_LANG_BOX_H
#define X10_LANG_BOX_H


#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/basic_functions.h>


#include <x10/lang/Ref.h>


namespace x10 {

    namespace lang {

        class String;

        template<class T> class Box : public Ref {
        public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT() {
                const char *name =
                    x10aux::alloc_printf("x10.lang.Box[%s]",x10aux::getRTT<T>()->name());
                const x10aux::RuntimeType *parent = x10::lang::Ref::getRTT();
                const x10aux::RuntimeType *cand = new (x10aux::alloc<x10aux::RuntimeType >()) x10aux::RuntimeType(name, 1, parent);
                return x10aux::RuntimeType::installRTT(&rtt, cand);
            }
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            static x10aux::ref<Box<T> > _make(T contents_) {
                return (new (x10aux::alloc<Box<T> >())Box<T>())->_constructor(contents_);
            }

            x10aux::ref<Box<T> > _constructor(T contents_) {
                FMGL(value) = contents_;
                return this;
            }

            virtual T get() {
                return FMGL(value);
            }

            virtual x10aux::ref<String> toString() {
                 return x10aux::to_string(FMGL(value));
            }

            public:

            T FMGL(value);

        };

        template<class T> const x10aux::RuntimeType *Box<T>::rtt = NULL;
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
