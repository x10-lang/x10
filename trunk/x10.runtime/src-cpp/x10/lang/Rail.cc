#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Rail.h>
#include <x10/runtime/Runtime.h>
#include <x10/runtime/FinishStates.h>

using namespace x10aux;

namespace x10 {
    namespace lang {

        RuntimeType Rail<void>::rtt;

        void
        _initRTTHelper_Rail(RuntimeType *location, const RuntimeType *element,
                            const RuntimeType *p1, const RuntimeType *p2) {
            const RuntimeType *parents[3] = { Ref::getRTT(), p1, p2 };
            const RuntimeType *params[1] = { element };
            RuntimeType::Variance variances[1] = { RuntimeType::invariant };
            const RuntimeType *canonical = x10aux::getRTT<Rail<void> >();
            const char *name = alloc_printf("x10.lang.Rail[%s]", element->name());
            location->init(canonical, name, 3, parents, 1, params, variances);
        }
    }
}

void x10::lang::Rail_notifyEnclosingFinish(deserialization_buffer& buf) {
    x10::runtime::RID rid = buf.read<x10::runtime::RID>();
    ref<x10::runtime::Runtime> rt = x10::runtime::Runtime::runtime();
    ref<Object> fs = rt->FMGL(finishStates)->get(rid);
    (fs.operator->()->*(findITable<x10::runtime::FinishState>(fs->_getITables())->incr))();
    (fs.operator->()->*(findITable<x10::runtime::FinishState>(fs->_getITables())->notifySubActivityTermination))();
}

void x10::lang::Rail_serializeAndSend(Place dst_place_, ref<Object> df, serialization_id_t _id, void* data, size_t size) {
    x10_int dst_place = dst_place_.FMGL(id);
    serialization_buffer buf;
    addr_map m;
    buf.realloc_func = x10aux::put_realloc;
    x10_byte code = 1;
    buf.write(code, m);
    buf.write(df, m);
    ref<x10::runtime::Runtime> rt = x10::runtime::Runtime::runtime();
    ref<Object> fs = rt->currentState();
    (fs.operator->()->*(findITable<x10::runtime::FinishState>(fs->_getITables())->notifySubActivitySpawn))(dst_place_);
    x10::runtime::Runtime::runtime()->FMGL(finishStates)->put(fs);
    x10::runtime::RID rid = (fs.operator->()->*(findITable<x10::runtime::FinishState>(fs->_getITables())->rid))();
    buf.write(rid, m);
    x10aux::send_put(dst_place, _id, buf, data, size);
}

// vim:tabstop=4:shiftwidth=4:expandtab
