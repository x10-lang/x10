#ifndef X10_LANG_RAIL_H
#define X10_LANG_RAIL_H

#include <sstream>


#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>


#include <x10/lang/Value.h>


namespace x10 {

    namespace lang {

        template<class T> class Rail : public x10aux::AnyRail<T> {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static const RTT* it;

                virtual std::string name() const {
                    std::stringstream ss;
                    ss << "Rail[" << x10aux::getRTT<T>()->name() << "]";
                    return ss.str();
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Rail<T> >();
            }

            private:

            Rail(const Rail<T>& arr); // disabled

            public:

            Rail(x10_int length_) : x10aux::AnyRail<T>(length_) { }

            public:

            ~Rail() { }

        };

        template<class T> const typename Rail<T>::RTT *Rail<T>::RTT::it =
            new typename Rail<T>::RTT();

    }
}


#endif
