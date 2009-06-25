#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>

using namespace x10::lang;
using namespace x10aux;

namespace x10 {
    namespace util {
        const RuntimeType*
        _initRTTHelper_GrowableRail(const RuntimeType **location, const RuntimeType *element) {
            const char *name = alloc_printf("x10.lang.GrowableRail[%s]", element->name());
            const RuntimeType *p1 = Ref::getRTT();
            const RuntimeType *cand = new (alloc<RuntimeType >()) RuntimeType(name, 1, p1);
            return RuntimeType::installRTT(location, cand);
        }
    }
}
