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

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT *it;

                virtual void init() { initParents(1,x10aux::getRTT<Ref>()); }

                virtual const char *name() const {
                    static const char *name =
                        x10aux::alloc_printf("x10.lang.Box[%s]",x10aux::getRTT<T>()->name());
                    return name;
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Box<T> >();
            }

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

        template<class T> typename Box<T>::RTT *Box<T>::RTT::it =
            new (x10aux::alloc<typename Box<T>::RTT>()) typename Box<T>::RTT();

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
