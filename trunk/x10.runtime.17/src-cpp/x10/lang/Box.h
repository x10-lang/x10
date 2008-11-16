#ifndef X10_LANG_BOX_H
#define X10_LANG_BOX_H

#include <sstream>


#include <x10aux/config.h>
#include <x10aux/RTT.h>


#include <x10/lang/Ref.h>


namespace x10 {

    namespace lang {

        template<class T> class Box : public Ref {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static const RTT *it;

                RTT() : RuntimeType(1,x10aux::getRTT<Ref>()) { }

                virtual std::string name() {
                    std::stringstream ss;
                    ss<<"x10.lang.Box["<<x10aux::getRTT<T>()->name()<<"]";
                    return ss.str();
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Box<T> >();
            }

            Box(const x10aux::ref<T>& contents_)
              : Value(), contents(contents_) { }

            ~Box() { }

            T& get() const {
                return *contents;
            }

            bool equals (const x10aux::ref<Object> &other) {
                return other==x10aux::ref<Box<T> >(this);
            }

            protected:

            x10aux::ref<T> contents;

        };

        template<class T> const typename Box<T>::RTT *Box<T>::RTT::it =
            new typename Box<T>::RTT(x10aux::getRTT<T>());

    }
}


#endif
