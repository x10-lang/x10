#include <x10aux/config.h>
#include <x10aux/static_init.h>
#include <x10aux/alloc.h>
#include <x10aux/network.h>

#include <x10/runtime/Runtime.h>

#include <assert.h>
#include <time.h>

using namespace x10aux;
using namespace x10::lang;

DeserializationDispatcher *StaticInitBroadcastDispatcher::it;

serialization_id_t StaticInitBroadcastDispatcher::addRoutine(Deserializer init) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addDeserializer_(init, false);
}

ref<Object> StaticInitBroadcastDispatcher::dispatch(deserialization_buffer &buf) {
    assert (NULL != it);
    serialization_id_t init_id = buf.read<serialization_id_t>();
    return it->create_(buf, init_id);
}

serialization_id_t const StaticInitBroadcastDispatcher::STATIC_BROADCAST_ID =
    DeserializationDispatcher::addDeserializer(StaticInitBroadcastDispatcher::dispatch, true);

void StaticInitBroadcastDispatcher::doBroadcast(serialization_id_t id, char* the_buf, x10_uint sz) {
    for (x10_uint place = 1; place < x10rt_nplaces(); place++) {
        x10rt_msg_params p = {place, id, the_buf, sz};
        // avoid giving x10rt a NULL message to keep things simple for implementers
        if (p.msg==NULL) p.msg = x10rt_msg_realloc(NULL,0,16);
        x10rt_send_msg(p);
    }
}

void StaticInitBroadcastDispatcher::await() {
    x10::runtime::Runtime::await();
}

void StaticInitBroadcastDispatcher::notify() {
    x10::runtime::Runtime::lock();
    x10::runtime::Runtime::release();
}

// vim:tabstop=4:shiftwidth=4:expandtab
