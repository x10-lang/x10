#ifndef X10_IO_IOEXCEPTION_H
#define X10_IO_IOEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Exception.h>

namespace x10 {

    namespace io {

        class IOException : public x10::lang::Exception {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
            
                virtual void init() {
                    initParents(1,x10aux::getRTT<x10::lang::Exception>());
                }
                
                virtual std::string name() const {
                    return "x10.io.IOException";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<IOException>();
            }

            IOException() { }

            IOException(x10aux::ref<x10::lang::String> msg) {
                (void)msg; // doesn't seem to be extractable
            }

        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
