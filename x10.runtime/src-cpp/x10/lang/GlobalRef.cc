/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#include <x10/lang/GlobalRef.h>
#include <x10aux/reference_logger.h>
#include <x10/lang/Runtime.h>
#include <x10/lang/Activity.h>

x10aux::RuntimeType x10::lang::GlobalRef<void>::rtt;

namespace x10 {
    namespace lang {

        x10_long globalref_getInitialEpoch() {
            Activity* activity = x10::lang::Runtime::activity();
            if (NULL == activity) {
                return x10::lang::Runtime::epoch();
            } else {
                return activity->FMGL(epoch);
            }
        }
        
        void logGlobalReference(x10::lang::Reference* obj) {
            #if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)
            if (!obj->_isMortal()) {
                x10aux::ReferenceLogger::log(obj);
            }
            #endif
        }

        void forgetGlobalReference(x10::lang::Reference* obj) {
            #if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)
            if (!obj->_isMortal()) {
                x10aux::ReferenceLogger::forget(obj);
            }
            #endif
        }
    }
}


