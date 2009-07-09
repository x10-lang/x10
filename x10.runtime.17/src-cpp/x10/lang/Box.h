#ifndef X10_LANG_BOX_H
#define X10_LANG_BOX_H


#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/basic_functions.h>


#include <x10/lang/Ref.h>


namespace x10 {

    namespace lang {

        class String;

        extern const x10aux::RuntimeType* _initRTTHelper_Box(const x10aux::RuntimeType **location, const x10aux::RuntimeType *rtt);
        
        template<class T> class Box : public Ref {
        public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT();
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

        template <> class Box<void> : public Ref {
        public:
            static void _static_init() { }
        };

        template<class T> const x10aux::RuntimeType* Box<T>::_initRTT() {
            return x10::lang::_initRTTHelper_Box(&rtt, x10aux::getRTT<T>());
        }
        
        template<class T> const x10aux::RuntimeType *Box<T>::rtt = NULL;
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
