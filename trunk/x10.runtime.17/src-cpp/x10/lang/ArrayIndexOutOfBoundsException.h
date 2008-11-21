#ifndef X10_LANG_ARRAYINDEXOUTOFBOUNDSEXCEPTION_H
#define X10_LANG_ARRAYINDEXOUTOFBOUNDSEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/RuntimeException.h>

namespace x10 {

    namespace lang {

        class ArrayIndexOutOfBoundsException : public RuntimeException {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<x10::lang::RuntimeException>());}
                virtual std::string name() const {return "x10.lang.ArrayIndexOutOfBoundsException";}
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<ArrayIndexOutOfBoundsException>();
            }

            ArrayIndexOutOfBoundsException() : RuntimeException() { }

            ArrayIndexOutOfBoundsException(x10aux::ref<String> message)
              : RuntimeException(message) { }

            ArrayIndexOutOfBoundsException(x10aux::ref<String> message, Cause cause)
              : RuntimeException(message,cause) {}

            ArrayIndexOutOfBoundsException(Cause cause) : RuntimeException(cause) { }

            ArrayIndexOutOfBoundsException(x10aux::SERIALIZATION_MARKER m) : RuntimeException(m) { }

        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
