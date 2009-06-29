#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>

using namespace x10aux;

namespace x10 {
    namespace lang {
        const RuntimeType*
        _initRTTHelper_Rail(const RuntimeType **location, const RuntimeType *element,
                            const RuntimeType *p1, const RuntimeType *p2) {
            const char *name = alloc_printf("x10.lang.Rail[%s]", element->name());
            const RuntimeType *p0 = Ref::getRTT();
            const RuntimeType *cand = new (alloc<RuntimeType >()) RuntimeType(name, 3, p0, p1, p2);
            return RuntimeType::installRTT(location, cand);
        }

        const RuntimeType* 
        _initRTTHelper_RailIterator(const RuntimeType **location, const RuntimeType *element,
                                    const RuntimeType *p1) {
            const char *name = alloc_printf("x10.lang.Rail.Iterator[%s]", element->name());
            const RuntimeType *p0 = Ref::getRTT();
            const RuntimeType *cand = new (alloc<RuntimeType >()) RuntimeType(name, 2, p0, p1);
            return RuntimeType::installRTT(location, cand);
        }
    }
}
