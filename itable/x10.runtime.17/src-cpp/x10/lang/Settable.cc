#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>

using namespace x10aux;

namespace x10 {
    namespace lang {
        const RuntimeType*
        _initRTTHelper_Settable(const RuntimeType **location, const RuntimeType *rtt1,
                                const RuntimeType *rtt2) {
            const char *name = alloc_printf("x10.lang.Settable[%s,%s]", rtt1->name(), rtt2->name());
            const RuntimeType *parent = Object::getRTT();
            const RuntimeType *cand = new (alloc<RuntimeType >()) RuntimeType(name, 1, parent);
            return RuntimeType::installRTT(location, cand);
        }
    }
}
