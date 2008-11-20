#ifndef X10_LANG_ITERATOR_H
#define X10_LANG_ITERATOR_H

#include <sstream>


#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>



namespace x10 {

    namespace lang {

        template<class T> class Iterator : public Object {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT* it;
 
                virtual void init() {
                    initParents(1,x10aux::getRTT<Object>());
                }

                virtual std::string name() const {
                    std::stringstream ss;
                    ss<<"x10.lang.Iterator["<<x10aux::getRTT<T>()->name()<<"]";
                    return ss.str();
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Iterator<T> >();
            }

            Iterator() { }

            private:

            Iterator(const Iterator<T>& arr); // disabled

            public:

            ~Iterator() { }

            virtual x10_boolean hasNext() = 0;

            virtual T next() = 0;

        };

        template<class T>
            typename Iterator<T>::RTT *Iterator<T>::RTT::it =
                new typename Iterator<T>::RTT();

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
