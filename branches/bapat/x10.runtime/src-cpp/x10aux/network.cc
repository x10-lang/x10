#include <arpa/inet.h> // for htonl

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

    buf.write((x10_uint)body->_get_serialization_id(),m);
    buf.write((x10_uint)12345678, m); // this is not the real size, we fill it in properly later
    body->_serialize_body(buf, m);
    x10_uint sz = buf.length();
    _X_(ANSI_BOLD<<ANSI_X10RT<<"async size: "<<ANSI_RESET<<sz);
    serialized_bytes += sz; asyncs_sent++;

    char *the_buf = buf.steal();

    sz = htonl(sz);
    ::memcpy(the_buf+4, &sz, 4); // fill bytes [4,8) with the real size
    
    x10rt_send(place, the_buf, NULL);
}

x10_int x10aux::num_threads() {
	const char* env = getenv("X10_NTHREADS");
    if (env==NULL) return 2;
    x10_int num = strtol(env, NULL, 10);
    assert (num > 0); 
    return num;
}

x10_boolean x10aux::no_steals() { return getenv("X10_NO_STEALS") != NULL; }

void x10aux::receive_async (void *the_buf, void *) {
    _X_(ANSI_X10RT<<"Receiving an async, deserialising..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(the_buf));
    // note: high bytes thrown away in implicit conversion
    x10aux::serialization_id_t id = buf.read<x10_int>();
    x10_int sz = buf.read<x10_int>();
    ref<Object> async(x10aux::DeserializationDispatcher::create<VoidFun_0_0>(buf, id));
    _X_("The deserialised async was: "<<async->toString());
    assert(buf.consumed()==sz);
    // FIXME: assert that buf.sofar() == sz
    deserialized_bytes += sz; asyncs_received++;
    (async.operator->()->*(findITable<VoidFun_0_0>(async->_getITables())->apply))();
}

// vim:tabstop=4:shiftwidth=4:expandtab
