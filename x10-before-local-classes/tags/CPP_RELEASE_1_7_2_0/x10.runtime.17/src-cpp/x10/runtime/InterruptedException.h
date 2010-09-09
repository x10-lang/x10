/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

#ifndef X10_RUNTIME_INTERRUPTED_EXCEPTION_H
#define X10_RUNTIME_INTERRUPTED_EXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Exception.h>

/** InterruptedException that may be thrown by Thread.sleep(). **/

namespace x10 {

    namespace runtime {

        class InterruptedException : public x10::lang::Exception {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
            
                virtual void init() {
                    initParents(1,x10aux::getRTT<x10::lang::Exception>());
                }
                
                virtual std::string name() const {
                    return "x10.runtime.InterruptedException";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<InterruptedException>();
            }

            InterruptedException() { }

            InterruptedException(x10aux::ref<x10::lang::String> msg) {
                (void)msg; // doesn't seem to be extractable
            }

        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
