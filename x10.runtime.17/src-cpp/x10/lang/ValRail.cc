#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Value.h>

using namespace x10aux;

namespace x10 {
    namespace lang {

        void
        _initRTTHelper_ValRail(RuntimeType *location, const RuntimeType *element,
                               const RuntimeType *p1, const RuntimeType *p2) {
            const char *name = alloc_printf("x10.lang.ValRail[%s]", element->name());
            const RuntimeType *p0 = Value::getRTT();
            location->init(name, 3, p0, p1, p2);
        }

        void
        _initRTTHelper_ValRailIterator(RuntimeType *location, const RuntimeType *element,
                                       const RuntimeType *p1) {
            const char *name = alloc_printf("x10.lang.ValRail.Iterator[%s]", element->name());
            const RuntimeType *p0 = Ref::getRTT();
            location->init(name, 2, p0, p1);
        }

    }
}

