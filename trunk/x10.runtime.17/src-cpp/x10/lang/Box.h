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
                static RTT *it;

                virtual void init() { initParents(1,x10aux::getRTT<Ref>()); }

                virtual std::string name() const {
                    std::stringstream ss;
                    ss<<"x10.lang.Box["<<x10aux::getRTT<T>()->name()<<"]";
                    return ss.str();
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Box<T> >();
            }

            Box(T contents_)
              : Ref(), contents(contents_) { }

            ~Box() { }

            virtual T get() {
                return contents;
            }

            virtual bool equals (x10aux::ref<Object> other) {
                return other==x10aux::ref<Box<T> >(this);
            }

            protected:

            T contents;

        };

        template<class T> typename Box<T>::RTT *Box<T>::RTT::it =
            new typename Box<T>::RTT();

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
