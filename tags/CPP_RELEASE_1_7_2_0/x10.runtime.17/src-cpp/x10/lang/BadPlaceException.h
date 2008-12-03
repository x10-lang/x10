#ifndef X10_LANG_BADPLACEEXCEPTION_H
#define X10_LANG_BADPLACEEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/RuntimeException.h>

namespace x10 {

    namespace lang {

        class BadPlaceException : public RuntimeException {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<x10::lang::RuntimeException>());}
                virtual std::string name() const { return "x10.lang.BadPlaceException"; } 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<BadPlaceException>();
            }

            BadPlaceException() : RuntimeException() { }
            BadPlaceException(x10aux::ref<String> message) : RuntimeException(message) {   }

            BadPlaceException(x10aux::SERIALIZATION_MARKER m) : RuntimeException(m) { }

        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
