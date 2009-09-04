#include <x10aux/config.h>

#include <x10aux/network.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>

#include <x10aux/cuda/cuda_utils.h>

#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>

#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/String.h> // for debug output

using namespace x10::lang;
using namespace x10aux;

// caches to avoid repeatedly calling into x10rt for trivial things
x10_int x10aux::num_places = -1;
x10_int x10aux::num_hosts = -1;
x10_int x10aux::here = -1;

// keep a counter for the session.
volatile x10_long x10aux::asyncs_sent = 0;
volatile x10_long x10aux::asyncs_received = 0;
volatile x10_long x10aux::serialized_bytes = 0;
volatile x10_long x10aux::deserialized_bytes = 0;

void x10aux::run_at(x10_uint place, x10aux::ref<Object> body) {

    assert(place!=x10rt_here()); // this case should be handled earlier
    assert(place<x10rt_nplaces()); // this is ensured by XRX runtime

    serialization_buffer buf;

    addr_map m;
    _X_(ANSI_BOLD<<ANSI_X10RT<<"Transmitting an async: "<<ANSI_RESET
        <<ref<Object>(body)->toString()->c_str()<<" to place: "<<place);

    body->_serialize_body(buf, m);

    unsigned long sz = buf.length();
    _X_(ANSI_BOLD<<ANSI_X10RT<<"async size: "<<ANSI_RESET<<sz);
    serialized_bytes += sz; asyncs_sent++;

    x10rt_msg_params p = {place, body->_get_serialization_id(), buf.steal(), sz};
    // avoid giving x10rt a NULL message to keep things simple for implementers
    if (p.msg==NULL) p.msg = x10rt_msg_realloc(NULL,0,16);
    x10rt_send_msg(p);
}

x10_int x10aux::num_threads() {
	const char* env = getenv("X10_NTHREADS");
    if (env==NULL) return 2;
    x10_int num = strtol(env, NULL, 10);
    assert (num > 0); 
    return num;
}

x10_boolean x10aux::no_steals() { return getenv("X10_NO_STEALS") != NULL; }

static void receive_async (const x10rt_msg_params &p) {
    _X_(ANSI_X10RT<<"Receiving an async, deserialising..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p.msg));
    // note: high bytes thrown away in implicit conversion
    ref<Object> async(x10aux::DeserializationDispatcher::create<VoidFun_0_0>(buf, p.type));
    assert(buf.consumed() <= p.len);
    _X_("The deserialised async was: "<<async->toString());
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
    (async.operator->()->*(findITable<VoidFun_0_0>(async->_getITables())->apply))();
}

void x10aux::register_async_handler (unsigned id) {
    x10rt_register_msg_receiver(id, receive_async);
    // [DC] these belong in x10aux::registration_complete but currently statics are broken
    here = x10rt_here();
    num_places = x10rt_nplaces();
    #ifdef X10RT_SUPPORTS_ACCELERATORS
    num_hosts = x10rt_nhosts();
    #else
    num_hosts = num_places;
    #endif
}

// vim:tabstop=4:shiftwidth=4:expandtab
