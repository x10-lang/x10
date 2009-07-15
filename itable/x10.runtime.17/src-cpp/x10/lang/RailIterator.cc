#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>

using namespace x10aux;

namespace x10 {
    namespace lang {
        void
        _initRTTHelper_RailIterator(RuntimeType *location, const RuntimeType *element,
                                    const RuntimeType *p1) {
            const char *name = alloc_printf("x10.lang.RailIterator[%s]", element->name());
            const RuntimeType *p0 = Ref::getRTT();
            location->init(name, 2, p0, p1);
        }
    }
}
