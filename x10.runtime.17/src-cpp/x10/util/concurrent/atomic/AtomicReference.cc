#include <x10/util/concurrent/atomic/AtomicReference.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::util::concurrent::atomic;

namespace x10 {
    namespace util {
        namespace concurrent {
            namespace atomic {
                void
                _initRTTHelper_AtomicReference(RuntimeType *location, const RuntimeType *rtt) {
                    const RuntimeType* parents[1] = { Ref::getRTT() };
                    const RuntimeType* params[1] = { rtt };
                    RuntimeType::Variance variances[1] = { RuntimeType::invariant };
                    const char *name = alloc_printf("x10.util.concurrent.atomic.AtomicReference[%s]",rtt->name());
                    location->init(name, 1, parents, 1, params, variances);
                }
            }
        }
    }
}


