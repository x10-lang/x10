#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>
#include <x10/lang/RailIterator.h>

using namespace x10aux;

namespace x10 {
    namespace lang {

        RuntimeType RailIterator<void>::rtt;

        void
        _initRTTHelper_RailIterator(RuntimeType *location, const RuntimeType *element,
                                    const RuntimeType *p1) {
            const RuntimeType *parents[2] = { Ref::getRTT(), p1 };
            const RuntimeType *params[1] = { element };
            RuntimeType::Variance variances[1] = { RuntimeType::covariant };
            const RuntimeType *canonical = x10aux::getRTT<RailIterator<void> >();
            const char *name = alloc_printf("x10.lang.Rail.Iterator[+%s]", element->name());
            location->init(canonical, name, 2, parents, 1, params, variances);
        }
    }
}
