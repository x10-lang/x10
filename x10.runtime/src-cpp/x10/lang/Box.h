#ifndef X10_LANG_BOX_H
#define X10_LANG_BOX_H


#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/basic_functions.h>


#include <x10/lang/Ref.h>


namespace x10 {

    namespace lang {

        class String;

        void _initRTTHelper_Box(x10aux::RuntimeType *location, const x10aux::RuntimeType *rtt);
        
        template<class T> class Box : public Ref {
        public:
            RTT_H_DECLS_CLASS;

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

        template <> class Box<void> : public Ref {
        };

        template<class T> void Box<T>::_initRTT() {
            rtt.canonical = &rtt;
            x10::lang::_initRTTHelper_Box(&rtt, x10aux::getRTT<T>());
        }
        
        template<class T> x10aux::RuntimeType Box<T>::rtt;
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
