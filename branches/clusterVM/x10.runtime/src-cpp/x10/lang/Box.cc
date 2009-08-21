#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Box.h>

using namespace x10aux;

namespace x10 {
    namespace lang {
        void
        _initRTTHelper_Box(RuntimeType *location, const RuntimeType *rtt) {
            const RuntimeType* parents[1] = { Ref::getRTT()};
            const RuntimeType* params[1] = { rtt };
            RuntimeType::Variance variances[1] = { RuntimeType::covariant };
            const RuntimeType *canonical = x10aux::getRTT<Box<void> >();
            const char *name = alloc_printf("x10.lang.Box[+%s]",rtt->name());
            location->init(canonical, name, 1, parents, 1, params, variances);
        }
    }
}

