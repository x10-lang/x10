#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>

using namespace x10::lang;
using namespace x10aux;

namespace x10 {
    namespace util {
        void
        _initRTTHelper_GrowableRail(RuntimeType *location, const RuntimeType *element) {
            const RuntimeType* parents[1] = { Ref::getRTT() };
            const RuntimeType* params[1] = { element };
            RuntimeType::Variance variances[1] = { RuntimeType::invariant };
            const char *name = alloc_printf("x10.lang.GrowableRail[%s]", element->name());
            location->init(name, 1, parents, 1, params, variances);
        }
    }
}
