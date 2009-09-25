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
    x10rt_send_msg(p);
}

void x10aux::send_get (x10_int place, unsigned id,
                       serialization_buffer &buf, void *data, x10_int len)
{
    size_t buf_length = buf.length(); // must do this before steal();
    x10rt_msg_params p = { place, id, buf.steal(), buf_length };
    x10rt_send_get(p, data, len);
}

void x10aux::send_put (x10_int place, unsigned id,
                       serialization_buffer &buf, void *data, x10_int len)
{
    size_t buf_length = buf.length(); // must do this before steal();
    x10rt_msg_params p = { place, id, buf.steal(), buf_length };
    x10rt_send_put(p, data, len);
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
}

static void *receive_put (const x10rt_msg_params &p, unsigned long len) {
    // TODO: handle general closures like receive_async does
    _X_(ANSI_X10RT<<"Receiving a put, deserialising..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p.msg));
    // note: high bytes thrown away in implicit conversion
    x10aux::BufferFinder bf = x10aux::DeserializationDispatcher::getPutBufferFinder(p.type);
    void *dropzone = bf(buf,len);
    assert(buf.consumed() <= p.len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
    return dropzone;
}

static void finished_put (const x10rt_msg_params &p, unsigned long len) {
    // TODO: implement finish
}

void x10aux::register_put_handler (unsigned id) {
    x10rt_register_put_receiver(id, receive_put, finished_put);
}

static void *receive_get (const x10rt_msg_params &p) {
    // TODO: handle general closures like receive_async does
    _X_(ANSI_X10RT<<"Receiving a get, deserialising..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p.msg));
    // note: high bytes thrown away in implicit conversion
    x10aux::BufferFinder bf = x10aux::DeserializationDispatcher::getGetBufferFinder(p.type);
    // FIXME: we probably need to change the x10rt spec to allow passing in the
    // length of the copy here, otherwise there is nowhere where we can do an
    // ArrayBounds check.
    void *dropzone = bf(buf,0);
    assert(buf.consumed() <= p.len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
    return dropzone;
}

static void finished_get (const x10rt_msg_params &p, unsigned long len) {
    // TODO: implement finish
}

void x10aux::register_get_handler (unsigned id) {
    x10rt_register_get_receiver(id, receive_get, finished_get);
}

// vim:tabstop=4:shiftwidth=4:expandtab
