#ifndef X10_LANG_THROWABLE_H
#define X10_LANG_THROWABLE_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>

namespace x10 {

    namespace lang {

        class Throwable : public Object {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static const RTT* const it; 
            
                RTT() : RuntimeType() { }
                
                virtual std::string name() const {
                    return "x10.lang.Throwable";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Throwable>();
            }

        };

    }
}


#endif
