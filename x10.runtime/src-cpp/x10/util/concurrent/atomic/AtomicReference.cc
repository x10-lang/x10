/*
 * (c) Copyright IBM Corporation 2009
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#include <x10/util/concurrent/atomic/AtomicReference.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::util::concurrent::atomic;

namespace x10 {
    namespace util {
        namespace concurrent {
            namespace atomic {
                RuntimeType AtomicReference<void>::rtt;

                void _initRTTHelper_AtomicReference(RuntimeType *location, const RuntimeType *rtt) {
                    const RuntimeType* parents[1] = { Object::getRTT() };
                    const RuntimeType* params[1] = { rtt };
                    RuntimeType::Variance variances[1] = { RuntimeType::invariant };
                    const RuntimeType *canonical = x10aux::getRTT<AtomicReference<void> >();
                    location->init(canonical, "x10.util.concurrent.atomic.AtomicReference", 1, parents, 1, params, variances);
                }
            }
        }
    }
}


