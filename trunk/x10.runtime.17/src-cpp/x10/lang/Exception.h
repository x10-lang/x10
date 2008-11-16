#ifndef X10_LANG_EXCEPTION_H
#define X10_LANG_EXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Value.h>

namespace x10 {

    namespace lang {

        class Exception : public Value {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static const RTT* const it; 
            
                RTT() : RuntimeType() { }
                
                virtual std::string name() const {
                    return "x10.lang.Exception";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Exception>();
            }

        };

    }
}


#endif
