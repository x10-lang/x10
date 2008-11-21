#ifndef X10_LANG_CLASSCASTEXCEPTION_H
#define X10_LANG_CLASSCASTEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/RuntimeException.h>

namespace x10 {

    namespace lang {

        class NullPointerException : public RuntimeException {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<x10::lang::RuntimeException>());}
                virtual std::string name() const { return "x10.lang.NullPointerException"; } 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<NullPointerException>();
            }

            NullPointerException() : RuntimeException() { }

            NullPointerException(x10aux::SERIALIZATION_MARKER m) : RuntimeException(m) { }

        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
