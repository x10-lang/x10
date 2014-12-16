/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#include <x10/util/concurrent/AtomicReference.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::util::concurrent;

namespace x10 {
    namespace util {
        namespace concurrent {
                RuntimeType AtomicReference<void>::rtt;

                void _initRTTHelper_AtomicReference(RuntimeType *location, const RuntimeType *rtt) {
                    const RuntimeType* params[1] = { rtt };
                    RuntimeType::Variance variances[1] = { RuntimeType::invariant };
                    location->initStageTwo("x10.util.concurrent.AtomicReference", RuntimeType::class_kind, 0, NULL, 1, params, variances);
                }
        }
    }
}


