#ifndef X10_LANG_VALRAIL_H
#define X10_LANG_VALRAIL_H

#include <sstream>


#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>


#include <x10aux/rail_utils.h>


namespace x10 {

    namespace lang {

        template<class T> class ValRail : public x10aux::AnyRail<T> {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static const RTT* it;

                virtual std::string name() const {
                    std::stringstream ss;
                    ss << "ValRail[" << x10aux::getRTT<T>()->name() << "]";
                    return ss.str();
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Rail<T> >();
            }

            private: ValRail(const ValRail<T>& arr); // disabled

            ValRail(x10_int l) : x10aux::AnyRail<T>(l) { }

            public:

            ~ValRail() { }

        };

        template<class T> const typename ValRail<T>::RTT *ValRail<T>::RTT::it =
            new typename ValRail<T>::RTT();

    }
}


#endif
