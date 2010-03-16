/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#include <x10aux/config.h>

#include <x10aux/network.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/basic_functions.h>

#include <x10aux/cuda/cuda_utils.h>

#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>

#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/String.h> // for debug output

#include <x10/lang/Closure.h> // for x10_runtime_Runtime__closure__6

#include <x10/lang/Runtime.h>

using namespace x10::lang;
using namespace x10aux;

// caches to avoid repeatedly calling into x10rt for trivial things
x10aux::place x10aux::num_places = 0;
x10aux::place x10aux::num_hosts = 0;
x10aux::place x10aux::here = -1;
bool x10aux::x10rt_initialized = false;

// keep a counter for the session.
volatile x10_long x10aux::asyncs_sent = 0;
volatile x10_long x10aux::asyncs_received = 0;
volatile x10_long x10aux::serialized_bytes = 0;
volatile x10_long x10aux::deserialized_bytes = 0;


const int x10aux::cuda_cfgs[] = {
  /*1024*/ 8, 128,
  /*1024*/ 4, 256,
  /*1024*/ 2, 512,

  /*960*/ 5, 192,
  /*960*/ 3, 320,

  /*896*/ 7, 128,
  /*896*/ 2, 448,

  /*768*/ 6, 128,
  /*768*/ 4, 192,
  /*768*/ 3, 256,
  /*768*/ 2, 384,

  /*640*/ 5, 128,
  /*640*/ 2, 320,

  /*576*/ 3, 192,

  /*512*/ 8, 64,
  /*512*/ 4, 128,
  /*512*/ 2, 256,
  /*512*/ 1, 512,

  /*448*/ 7, 64,
  /*448*/ 1, 448,

  /*384*/ 6, 64,
  /*384*/ 3, 128,
  /*384*/ 2, 192,
  /*384*/ 1, 384,

  /*320*/ 5, 64,
  /*320*/ 1, 320,

  /*256*/ 4, 64,
  /*256*/ 2, 128,
  /*256*/ 1, 256,

  /*192*/ 3, 64,
  /*192*/ 1, 192,

  /*128*/ 2, 64,
  /*128*/ 1, 128,

  /*64*/ 1, 64,

  0 /* terminator */
};

void x10aux::blocks_threads (place p, msg_type t, int shm, int &bs, int &ts, const int *cfgs)
{ x10rt_blocks_threads(p,t,shm,&bs,&ts,cfgs); }



void *kernel_put_finder (const x10rt_msg_params *p, x10rt_copy_sz)
{
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    buf.read<x10_ulong>();
    x10_ulong remote_addr = buf.read<x10_ulong>();
    assert(buf.consumed() <= p->len);
    _X_(ANSI_X10RT<<"CUDA kernel populating: "<<remote_addr<<ANSI_RESET);
    return (void*)(size_t)remote_addr;
}

void kernel_put_notifier (const x10rt_msg_params *p, x10rt_copy_sz)
{
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    bool *finished = (bool*)(size_t)buf.read<x10_ulong>();
    *finished = true;
}

x10aux::msg_type x10aux::kernel_put;

void x10aux::registration_complete (void)
{
    x10aux::kernel_put =
        x10rt_register_put_receiver(NULL, NULL, kernel_put_finder, kernel_put_notifier);
    x10rt_registration_complete();
    x10aux::x10rt_initialized = true;
}

void x10aux::network_init (int ac, char **av) {
    x10rt_init(&ac, &av);
    x10aux::here = x10rt_here();
    x10aux::num_places = x10rt_nplaces();
    x10aux::num_hosts = x10rt_nhosts();
}

/*
// FIXME: this is perhaps the worst hack i've ever done
struct x10_runtime_Runtime__closure__8 : x10::lang::Closure {
    static const x10aux::serialization_id_t _serialization_id;
    x10aux::ref<x10::lang::VoidFun_0_0> body;
    x10::lang::RID rid;
};
struct x10_runtime_Runtime__closure__7 : x10::lang::Closure {
    static const x10aux::serialization_id_t _serialization_id;
    x10aux::ref<x10::lang::VoidFun_0_0> body;
    x10::lang::RID rid;
};
struct x10_runtime_Runtime__closure__6 : x10::lang::Closure {
    static const x10aux::serialization_id_t _serialization_id;
    x10aux::ref<x10::lang::VoidFun_0_0> body;
    x10::lang::RID rid;
};
struct x10_runtime_Runtime__closure__5 : x10::lang::Closure {
    static const x10aux::serialization_id_t _serialization_id;
    x10aux::ref<x10::lang::VoidFun_0_0> body;
    x10::lang::RID rid;
};
*/

struct nightmarish_hack : x10::lang::Closure {
    x10aux::ref<x10::lang::VoidFun_0_0> body;
    x10aux::ref<x10::lang::Reference> fs;
};

void x10aux::run_at(x10aux::place p, x10aux::ref<Reference> body) {

    assert(p!=here); // this case should be handled earlier
    assert(p<num_places); // this is ensured by XRX runtime

    serialization_buffer buf;

    serialization_id_t sid = body->_get_serialization_id();
    msg_type id = DeserializationDispatcher::getMsgType(sid);

    _X_(ANSI_BOLD<<ANSI_X10RT<<"Transmitting an async: "<<ANSI_RESET
        <<ref<Reference>(body)->toString()->c_str()<<" id "<<id
        <<" sid "<<sid<<" to place: "<<p);

    if (!is_cuda(p)) {

        body->_serialize_body(buf);

        unsigned long sz = buf.length();
        serialized_bytes += sz; asyncs_sent++;

        _X_(ANSI_BOLD<<ANSI_X10RT<<"async size: "<<ANSI_RESET<<sz);

        x10rt_msg_params params = {p, id, buf.steal(), sz};
        x10rt_send_msg(&params);

    } else {

        // FIXME: this is a hack -- we should be doing this for all asyncs

/*
        assert (sid == x10_runtime_Runtime__closure__8::_serialization_id ||
                sid == x10_runtime_Runtime__closure__7::_serialization_id ||
                sid == x10_runtime_Runtime__closure__6::_serialization_id ||
                sid == x10_runtime_Runtime__closure__5::_serialization_id);
*/

        x10aux::ref<nightmarish_hack> body_ = body;



/*
        // This version for runAt
        x10aux::ref<nightmarish_hack> almost_there = body_->body;
        x10aux::ref<x10::lang::Reference> real_body = almost_there->body;
        x10aux::ref<x10::lang::Reference> fs = almost_there->fs;
*/

        // This version for runAt
        x10aux::ref<x10::lang::Reference> real_body = body_->body;
        x10aux::ref<x10::lang::Reference> fs = body_->fs;

        serialization_id_t real_sid = real_body->_get_serialization_id();
        msg_type real_id = DeserializationDispatcher::getMsgType(real_sid);

        _X_(ANSI_BOLD<<ANSI_X10RT<<"This is actually a kernel: "<<ANSI_RESET
            <<ref<Reference>(real_body)->toString()->c_str()<<" id "<<real_id
            <<" sid "<<real_sid<<" at GPU: "<<p);

        buf.write(fs);
        real_body->_serialize_body(buf);

        unsigned long sz = buf.length();
        serialized_bytes += sz; asyncs_sent++;

        _X_(ANSI_BOLD<<ANSI_X10RT<<"async size: "<<ANSI_RESET<<sz);

        x10rt_msg_params params = {p, real_id, buf.steal(), sz};
        x10rt_send_msg(&params);

    }
}

void x10aux::send_get (x10aux::place place, x10aux::serialization_id_t id_,
                       serialization_buffer &buf, void *data, x10aux::copy_sz len)
{
    size_t buf_length = buf.length(); // must do this before steal();
    msg_type id = DeserializationDispatcher::getMsgType(id_);
    x10rt_msg_params p = { place, id, buf.steal(), buf_length };
    _X_(ANSI_BOLD<<ANSI_X10RT<<"Transmitting a get: "<<ANSI_RESET
        <<data<<" sid "<<id_<<" id "<<id<<" size "<<len<<" header "<<buf_length<<" to place: "<<place);
    x10rt_send_get(&p, data, len);
}

void x10aux::send_put (x10aux::place place, x10aux::serialization_id_t id_,
                       serialization_buffer &buf, void *data, x10aux::copy_sz len)
{
    size_t buf_length = buf.length(); // must do this before steal();
    msg_type id = DeserializationDispatcher::getMsgType(id_);
    x10rt_msg_params p = { place, id, buf.steal(), buf_length };
    _X_(ANSI_BOLD<<ANSI_X10RT<<"Transmitting a put: "<<ANSI_RESET
        <<data<<" sid "<<id_<<" id "<<id<<" size "<<len<<" header "<<buf_length<<" to place: "<<place);
    x10rt_send_put(&p, data, len);
}

x10_int x10aux::num_threads() {
	const char* env = getenv("X10_NTHREADS");
    if (env==NULL) return 2;
    x10_int num = strtol(env, NULL, 10);
    assert (num > 0);
    return num;
}

x10_boolean x10aux::no_steals() { return getenv("X10_NO_STEALS") != NULL; }

x10_boolean x10aux::static_threads() { return (getenv("X10_STATIC_THREADS") != NULL); }

static void receive_async (const x10rt_msg_params *p) {
    _X_(ANSI_X10RT<<"Receiving an async, deserialising..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    ref<Reference> async(x10aux::DeserializationDispatcher::create<VoidFun_0_0>(buf, sid));
    assert(buf.consumed() <= p->len);
    _X_("The deserialised async was: "<<x10aux::safe_to_string(async));
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
    if (async.isNull()) return;
    (async.operator->()->*(findITable<VoidFun_0_0>(async->_getITables())->apply))();
    x10aux::dealloc(async.operator->());
}

static void cuda_pre (const x10rt_msg_params *p, size_t *blocks, size_t *threads, size_t *shm,
                      size_t *argc, char **argv, size_t *cmemc, char **cmemv)
{
    _X_(ANSI_X10RT<<"Receiving a kernel pre callback, deserialising..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    buf.read<x10aux::ref<x10::lang::Reference> >();
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::CUDAPre pre = x10aux::DeserializationDispatcher::getCUDAPre(sid);
    x10_ulong env = pre(buf, p->dest_place, *blocks, *threads, *shm);
    assert(buf.consumed() <= p->len);
    *argv = (char*)(size_t)env;
    *argc = sizeof(void*);
}

static void cuda_post (const x10rt_msg_params *p, void *env)
{
    _X_(ANSI_X10RT<<"Receiving a kernel post callback, deserialising..."<<ANSI_RESET);
    remote_free(p->dest_place, (x10_ulong)(size_t)env);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    x10aux::ref<x10::lang::Reference> fs = buf.read<x10aux::ref<x10::lang::Reference> >();
    x10aux::ref<x10::lang::Runtime> rt = x10::lang::PlaceLocalHandle_methods<x10aux::ref<x10::lang::Runtime> >::apply(x10::lang::Runtime::FMGL(runtime));
    (fs.operator->()->*(x10aux::findITable<x10::lang::Runtime__FinishState>(fs->_getITables())->notifyActivityCreation))();
    (fs.operator->()->*(x10aux::findITable<x10::lang::Runtime__FinishState>(fs->_getITables())->notifyActivityTermination))();
}

x10aux::msg_type x10aux::register_async_handler (const char *cubin, const char *kernel)
{
    if (cubin==NULL && kernel==NULL) {
        return x10rt_register_msg_receiver(receive_async, NULL, NULL, NULL, NULL);
    } else {
        return x10rt_register_msg_receiver(receive_async, cuda_pre, cuda_post, cubin, kernel);
    }
}

static void *receive_put (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a put, deserialising for buffer finder..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::BufferFinder bf = x10aux::DeserializationDispatcher::getPutBufferFinder(sid);
    void *dropzone = bf(buf,len);
    assert(buf.consumed() <= p->len);
    return dropzone;
}

static void *cuda_receive_put (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a put, deserialising for cuda buffer finder..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::BufferFinder bf = x10aux::DeserializationDispatcher::getCUDAPutBufferFinder(sid);
    void *dropzone = bf(buf,len);
    assert(buf.consumed() <= p->len);
    return dropzone;
}

static void finished_put (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a put, deserialising for notifier..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::Notifier n = x10aux::DeserializationDispatcher::getPutNotifier(sid);
    n(buf,len);
    assert(buf.consumed() <= p->len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
}

static void cuda_finished_put (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a put, deserialising for cuda notifier..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::Notifier n = x10aux::DeserializationDispatcher::getCUDAPutNotifier(sid);
    n(buf,len);
    assert(buf.consumed() <= p->len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
}

x10aux::msg_type x10aux::register_put_handler () {
    return x10rt_register_put_receiver(receive_put, finished_put,
                                       cuda_receive_put, cuda_finished_put);
}

static void *receive_get (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a get, deserialising for buffer finder..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::BufferFinder bf = x10aux::DeserializationDispatcher::getGetBufferFinder(sid);
    void *dropzone = bf(buf,len);
    assert(buf.consumed() <= p->len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
    return dropzone;
}

static void *cuda_receive_get (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a get, deserialising for cuda buffer finder..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::BufferFinder bf = x10aux::DeserializationDispatcher::getCUDAGetBufferFinder(sid);
    void *dropzone = bf(buf,len);
    assert(buf.consumed() <= p->len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
    return dropzone;
}

static void finished_get (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a get, deserialising for notifier..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::Notifier n = x10aux::DeserializationDispatcher::getGetNotifier(sid);
    n(buf,len);
    assert(buf.consumed() <= p->len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
}

static void cuda_finished_get (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a get, deserialising for cuda notifier..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg));
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::Notifier n = x10aux::DeserializationDispatcher::getCUDAGetNotifier(sid);
    n(buf,len);
    assert(buf.consumed() <= p->len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
}

x10aux::msg_type x10aux::register_get_handler (void) {
    return x10rt_register_get_receiver(receive_get, finished_get,
                                       cuda_receive_get, cuda_finished_get);
}

void x10aux::cuda_put (place gpu, x10_ulong addr, void *var, size_t sz)
{
    bool finished = false;
    x10aux::serialization_buffer buf;
    buf.realloc_func = x10aux::put_realloc;
    buf.write((x10_ulong)(size_t)&finished);
    buf.write(addr);
    size_t len = buf.length();
    x10rt_msg_params p = {gpu, kernel_put, buf.steal(), len};
    x10rt_send_put(&p, var, sz);
    while (!finished) x10rt_probe();
}


// vim:tabstop=4:shiftwidth=4:expandtab
