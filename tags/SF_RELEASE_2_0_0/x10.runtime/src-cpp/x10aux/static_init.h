#ifndef X10AUX_STATIC_INIT_H
#define X10AUX_STATIC_INIT_H

#include <x10aux/config.h>

#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>

namespace x10aux {

    class StaticInitBroadcastDispatcher {
        protected:
        static DeserializationDispatcher *it;
        static serialization_id_t const STATIC_BROADCAST_ID;
        static void doBroadcast(serialization_id_t id, char* the_buf, x10_uint sz);

        public:
        static serialization_id_t addRoutine(Deserializer init);
        static ref<x10::lang::Ref> dispatch(deserialization_buffer& buf);
        template<class C> static void broadcastStaticField(C f, serialization_id_t id);
        static void await();
        static void notify();
    };

    template<class C>
    void StaticInitBroadcastDispatcher::broadcastStaticField(C f, serialization_id_t id) {
        if (num_places == 1) return;
        serialization_buffer buf;
        addr_map m;
        buf.write(id, m);
        buf.write(f, m);
        x10_uint sz = buf.length();
        serialized_bytes += sz; asyncs_sent++;
        char *the_buf = buf.steal();
        doBroadcast(STATIC_BROADCAST_ID, the_buf, sz);
    }

    template<> inline const char *typeName<StaticInitBroadcastDispatcher>()
    { return "StaticInitBroadcastDispatcher"; }

    enum status {
        UNINITIALIZED = 0,
        INITIALIZING,
        INITIALIZED
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
