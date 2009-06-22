#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>

using namespace x10aux;

namespace x10 {
    namespace lang {
        const RuntimeType*
        _initRTTHelper_Iterable(const RuntimeType **location, const RuntimeType *rtt) {
            const char *name = alloc_printf("x10.lang.Iterable[%s]",rtt->name());
            const RuntimeType *parent = Object::getRTT();
            const RuntimeType *cand = new (alloc<RuntimeType >()) RuntimeType(name, 1, parent);
            return RuntimeType::installRTT(location, cand);
        }
    }
}
