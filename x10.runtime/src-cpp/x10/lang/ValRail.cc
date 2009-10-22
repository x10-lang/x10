#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>
#include <x10/lang/ValRail.h>

using namespace x10aux;

namespace x10 {
    namespace lang {

        RuntimeType ValRail<void>::rtt;

        void
        _initRTTHelper_ValRail(RuntimeType *location, const RuntimeType *element,
                               const RuntimeType *p1, const RuntimeType *p2) {
            const RuntimeType *parents[3] = { Ref::getRTT(), p1, p2 };
            const RuntimeType *params[1] = { element };
            RuntimeType::Variance variances[1] = { RuntimeType::covariant };
            const RuntimeType *canonical = x10aux::getRTT<ValRail<void> >();
            const char *name = alloc_printf("x10.lang.ValRail[+%s]", element->name());
            location->init(canonical, name, 3, parents, 1, params, variances);
        }
    }
}

