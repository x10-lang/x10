#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>

using namespace x10aux;

namespace x10 {
    namespace lang {
        void
        _initRTTHelper_Box(RuntimeType *location, const RuntimeType *rtt) {
            const char *name = alloc_printf("x10.lang.Box[%s]",rtt->name());
            const RuntimeType *parent = Ref::getRTT();
            location->init(name, 1, parent);
        }
    }
}

