#ifndef X10_LANG_CLASSCASTEXCEPTION_H
#define X10_LANG_CLASSCASTEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/RuntimeException.h>

namespace x10 {

    namespace lang {

        class ClassCastException : public RuntimeException {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<x10::lang::RuntimeException>());}
                virtual std::string name() const { return "x10.lang.ClassCastException"; } 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<ClassCastException>();
            }

            ClassCastException() : RuntimeException() { }

            ClassCastException(x10aux::ref<String> message) : RuntimeException(message) { }

            ClassCastException(x10aux::ref<String> message, Cause cause)
              : RuntimeException(message,cause) {}

            ClassCastException(Cause cause) : RuntimeException(cause) { }

            ClassCastException(x10aux::SERIALIZATION_MARKER m) : RuntimeException(m) { }

        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
