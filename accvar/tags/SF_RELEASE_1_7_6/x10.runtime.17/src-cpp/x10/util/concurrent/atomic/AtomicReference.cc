#include <x10/util/concurrent/atomic/AtomicReference.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::util::concurrent::atomic;

namespace x10 {
    namespace util {
        namespace concurrent {
            namespace atomic {
                const RuntimeType*
                _initRTTHelper_AtomicReference(const RuntimeType **location, const RuntimeType *rtt) {
                    const char *name = alloc_printf("x10.util.concurrent.atomic.AtomicReference[%s]",rtt->name());
                    const RuntimeType *parent = Ref::getRTT();
                    const RuntimeType *cand = new (alloc<RuntimeType >()) RuntimeType(name, 1, parent);
                    return RuntimeType::installRTT(location, cand);
                }
            }
        }
    }
}


