#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/runtime/PlaceLocalHandle.h>

using namespace x10aux;
using namespace x10::lang;

namespace x10 {
    namespace runtime {

        x10aux::RuntimeType PlaceLocalHandle<void>::rtt;

        void
        _initRTTHelper_PlaceLocalHandle(RuntimeType *location, const RuntimeType *rtt) {
            const RuntimeType* params[1] = { rtt };
            RuntimeType::Variance variances[1] = { RuntimeType::invariant };
            const RuntimeType *canonical = x10aux::getRTT<PlaceLocalHandle<void> >();
            const char *name = alloc_printf("x10.runtime.PlaceLocalHandle[+%s]",rtt->name());
            location->init(canonical, name, 0, NULL, 1, params, variances);
        }
    }
}

