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
#include <x10aux/RTT.h>
#include <x10aux/basic_functions.h>

#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>

#include <x10/lang/RuntimeNatives.h>

#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/String.h> // for debug output

#include <x10/lang/Closure.h> // for x10_runtime_Runtime__closure__6

#include <x10/lang/Runtime.h>
#include <x10/lang/FinishState.h>


#include <strings.h>

#ifdef __MACH__
#include <crt_externs.h>
#endif

using namespace x10::lang;
using namespace x10aux;

// caches to avoid repeatedly calling into x10rt for trivial things
x10aux::place x10aux::num_places = 0;
x10aux::place x10aux::num_hosts = 0;
x10aux::place x10aux::here = -1;
bool x10aux::x10rt_initialized = false;
x10_int x10aux::num_local_cores = 1; // this will be set in template_main

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

void x10aux::blocks_threads (place p, msg_type t, x10_int shm, x10_ubyte &bs, x10_ubyte &ts, const int *cfgs)
{ x10_int a,b; x10rt_blocks_threads(p,t,shm,&a,&b,cfgs); bs=a,ts=b; }
void x10aux::blocks_threads (place p, msg_type t, x10_int shm, x10_byte &bs, x10_byte &ts, const int *cfgs)
{ x10_int a,b; x10rt_blocks_threads(p,t,shm,&a,&b,cfgs); bs=a,ts=b; }
void x10aux::blocks_threads (place p, msg_type t, x10_int shm, x10_ushort &bs, x10_ushort &ts, const int *cfgs)
{ x10_int a,b; x10rt_blocks_threads(p,t,shm,&a,&b,cfgs); bs=a,ts=b; }
void x10aux::blocks_threads (place p, msg_type t, x10_int shm, x10_short &bs, x10_short &ts, const int *cfgs)
{ x10_int a,b; x10rt_blocks_threads(p,t,shm,&a,&b,cfgs); bs=a,ts=b; }
void x10aux::blocks_threads (place p, msg_type t, x10_int shm, x10_uint &bs, x10_uint &ts, const int *cfgs)
{ x10_int a,b; x10rt_blocks_threads(p,t,shm,&a,&b,cfgs); bs=a,ts=b; }
void x10aux::blocks_threads (place p, msg_type t, x10_int shm, x10_int &bs, x10_int &ts, const int *cfgs)
{ x10rt_blocks_threads(p,t,shm,&bs,&ts,cfgs); }
void x10aux::blocks_threads (place p, msg_type t, x10_int shm, x10_ulong &bs, x10_ulong &ts, const int *cfgs)
{ x10_int a,b; x10rt_blocks_threads(p,t,shm,&a,&b,cfgs); bs=a,ts=b; }
void x10aux::blocks_threads (place p, msg_type t, x10_int shm, x10_long &bs, x10_long &ts, const int *cfgs)
{ x10_int a,b; x10rt_blocks_threads(p,t,shm,&a,&b,cfgs); bs=a,ts=b; }



void *kernel_put_finder (const x10rt_msg_params *p, x10rt_copy_sz)
{
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
    buf.read<x10_ulong>();
    x10_ulong remote_addr = buf.read<x10_ulong>();
    assert(buf.consumed() <= p->len);
    _X_(ANSI_X10RT<<"CUDA kernel populating: "<<remote_addr<<ANSI_RESET);
    return (void*)(size_t)remote_addr;
}

void kernel_put_notifier (const x10rt_msg_params *p, x10rt_copy_sz)
{
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
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

x10rt_remote_op_params *x10aux::opv;
size_t x10aux::opc;
size_t x10aux::remote_op_batch;

void x10aux::network_init (int ac, char **av) {
    x10rt_init(&ac, &av);
    x10aux::here = x10rt_here();
    x10aux::num_places = x10rt_nplaces();
    x10aux::num_hosts = x10rt_nhosts();
    remote_op_batch = get_remote_op_batch();
    opv = (x10rt_remote_op_params*)malloc(remote_op_batch * sizeof(*opv));
}

void x10aux::run_async_at(x10aux::place p, x10::lang::VoidFun_0_0* body_fun,
                          x10::lang::FinishState* fs, x10::lang::Runtime__Profile *prof,
                          x10aux::endpoint endpoint) {

    x10::lang::Reference* real_body = reinterpret_cast<x10::lang::Reference*>(body_fun);
    
    serialization_id_t real_sid = real_body->_get_serialization_id();
    if (!is_cuda(p)) {
        _X_(ANSI_BOLD<<ANSI_X10RT<<"Transmitting a simple async: "<<ANSI_RESET
            <<real_body->toString()->c_str()
            <<" sid "<<real_sid<<" to place: "<<p);

    } else {
        _X_(ANSI_BOLD<<ANSI_X10RT<<"This is actually a kernel: "<<ANSI_RESET
            <<real_body->toString()->c_str()
            <<" sid "<<real_sid<<" at GPU: "<<p);
    }

    x10aux::msg_type real_id = DeserializationDispatcher::getMsgType(real_sid);
    serialization_buffer buf;

    _X_(ANSI_BOLD<<ANSI_X10RT<<"Async id: "<<ANSI_RESET<<real_id);

    assert(DeserializationDispatcher::getClosureKind(real_sid)!=x10aux::CLOSURE_KIND_NOT_ASYNC);
    assert(DeserializationDispatcher::getClosureKind(real_sid)!=x10aux::CLOSURE_KIND_GENERAL_ASYNC);

    // WRITE FINISH STATE
    buf.write(fs);

    // WRITE BODY
    unsigned long long before_nanos, before_bytes;
    if (prof!=NULL) {
        before_nanos = x10::lang::RuntimeNatives::nanoTime();
        before_bytes = buf.length(); // don't include finish state
    }
    // We're playing a sleazy trick here and not following the general
    // serialization protocol. We should be calling buf.write(real_body),
    // but instead we are just calling it's serialize_body method directly.
    // This is problematic because buf.write has the responsibility for
    // creating the entry in buf's address_map to handle repeated references.
    // The _deserialize method of real_body will call record_reference.
    // Therefore we have to record the reference explicitly here to 
    // to avoid an "off by one" error in the relative back count of objects
    // that crosses this point in the address map.
    // This happens when an object is reachable from both fs and real_body.
    buf.manually_record_reference(real_body);
    real_body->_serialize_body(buf);
    if (prof!=NULL) {
        prof->FMGL(bytes) += buf.length() - before_bytes;
        prof->FMGL(serializationNanos) += x10::lang::RuntimeNatives::nanoTime() - before_nanos;
    }

    unsigned long sz = buf.length();
    serialized_bytes += sz; asyncs_sent++;

    _X_(ANSI_BOLD<<ANSI_X10RT<<"async size: "<<ANSI_RESET<<sz);

    if (prof!=NULL) {
        before_nanos = x10::lang::RuntimeNatives::nanoTime();
    }
    x10rt_msg_params params = {p, real_id, buf.borrow(), sz, endpoint};
    x10rt_send_msg(&params);
    if (prof!=NULL) {
        prof->FMGL(communicationNanos) += x10::lang::RuntimeNatives::nanoTime() - before_nanos;
    }
}

void x10aux::run_closure_at(x10aux::place p, x10::lang::VoidFun_0_0* body_fun,
                            x10::lang::Runtime__Profile *prof, x10aux::endpoint endpoint) {

    x10::lang::Reference* body = reinterpret_cast<x10::lang::Reference*>(body_fun);

    serialization_id_t sid = body->_get_serialization_id();

    _X_(ANSI_BOLD<<ANSI_X10RT<<"Transmitting a general async: "<<ANSI_RESET
        <<body->toString()->c_str()
        <<" sid "<<sid<<" to place: "<<p);

    assert(p!=here); // this case should be handled earlier
    assert(p<num_places); // this is ensured by XRX runtime

    assert(!is_cuda(p));

    serialization_buffer buf;

    assert(DeserializationDispatcher::getClosureKind(sid)!=x10aux::CLOSURE_KIND_NOT_ASYNC);
    assert(DeserializationDispatcher::getClosureKind(sid)!=x10aux::CLOSURE_KIND_SIMPLE_ASYNC);
    msg_type id = DeserializationDispatcher::getMsgType(sid);

    _X_(ANSI_BOLD<<ANSI_X10RT<<"Async id: "<<ANSI_RESET<<id);

    // We're playing a sleazy trick here and not following the general
    // serialization protocol. We should be calling buf.write(body),
    // but instead we are just calling it's serialize_body method directly.
    // This is problematic because buf.write has the responsibility for
    // creating the entry in buf's address_map to handle repeated references.
    // The _deserialize method of body will call record_reference.
    // Therefore we have to record the reference explicitly here so
    // that if body is reachable from the object graph that is reachable
    // from body itself, then we create the proper aliasing on the receiving end.
    // Unlikely, but could happen if a reference to the closure escapes into
    // the heap before the closure is serialized. Doesn't happen in practice with
    // the current XRX design, but put in the code to guard against future changes.
    buf.manually_record_reference(body);
    body->_serialize_body(buf);

    unsigned long sz = buf.length();
    serialized_bytes += sz; asyncs_sent++;

    _X_(ANSI_BOLD<<ANSI_X10RT<<"async size: "<<ANSI_RESET<<sz);

    x10rt_msg_params params = {p, id, buf.borrow(), sz, endpoint};
    x10rt_send_msg(&params);

}

void x10aux::send_get (x10aux::place place, x10aux::serialization_id_t id_,
                       serialization_buffer &buf, void *data, x10aux::copy_sz len, x10aux::endpoint endpoint)
{
    msg_type id = DeserializationDispatcher::getMsgType(id_);
    x10rt_msg_params p = { place, id, buf.borrow(), buf.length(), endpoint};
    _X_(ANSI_BOLD<<ANSI_X10RT<<"Transmitting a get: "<<ANSI_RESET<<data<<" sid "<<id_<<" id "<<id
    		<<" size "<<len<<" header "<<buf.length()<<" to place: "<<place<<" endpoint: "<<endpoint);
    x10rt_send_get(&p, data, len);
}

void x10aux::send_put (x10aux::place place, x10aux::serialization_id_t id_,
                       serialization_buffer &buf, void *data, x10aux::copy_sz len, x10aux::endpoint endpoint)
{
    msg_type id = DeserializationDispatcher::getMsgType(id_);
    x10rt_msg_params p = { place, id, buf.borrow(), buf.length(), endpoint};
    _X_(ANSI_BOLD<<ANSI_X10RT<<"Transmitting a put: "<<ANSI_RESET<<data<<" sid "<<id_<<" id "<<id
    		<<" size "<<len<<" header "<<buf.length()<<" to place: "<<place<<" endpoint: "<<endpoint);
    x10rt_send_put(&p, data, len);
}

static void receive_async (const x10rt_msg_params *p) {
    _X_(ANSI_X10RT<<"Receiving an async, id ("<<p->type<<"), deserialising..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    _X_(ANSI_X10RT<<"async sid: ("<<sid<<ANSI_RESET);
    x10aux::ClosureKind ck = DeserializationDispatcher::getClosureKind(sid);
    switch (ck) {
        case x10aux::CLOSURE_KIND_GENERAL_ASYNC: {
            Reference* body(x10aux::DeserializationDispatcher::create(buf, sid));
            assert(buf.consumed() <= p->len);
            _X_("The deserialised general async was: "<<x10aux::safe_to_string(body));
            deserialized_bytes += buf.consumed()  ; asyncs_received++;
            if (NULL == body) return;
            VoidFun_0_0::__apply(reinterpret_cast<VoidFun_0_0*>(body));
            x10aux::dealloc(body);
        } break;
        case x10aux::CLOSURE_KIND_SIMPLE_ASYNC: {
            x10::lang::FinishState* fs = buf.read<x10::lang::FinishState*>();
            Reference* body(x10aux::DeserializationDispatcher::create(buf, sid));
            assert(buf.consumed() <= p->len);
            _X_("The deserialised simple async was: "<<x10aux::safe_to_string(body));
            deserialized_bytes += buf.consumed()  ; asyncs_received++;
            if (NULL == body) return;
            x10::lang::Runtime::execute(reinterpret_cast<VoidFun_0_0*>(body), fs);
        } break;
        default: abort();
    }
}

static void cuda_pre (const x10rt_msg_params *p, size_t *blocks, size_t *threads, size_t *shm,
                      size_t *argc, char **argv, size_t *cmemc, char **cmemv)
{
    _X_(ANSI_X10RT<<"Receiving a kernel pre callback, deserialising..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
    buf.read<x10::lang::FinishState*>();
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::CUDAPre pre = x10aux::DeserializationDispatcher::getCUDAPre(sid);
    pre(buf, p->dest_place, *blocks, *threads, *shm, *argc, *argv, *cmemc, *cmemv);
    assert(buf.consumed() <= p->len);
}

static void cuda_post (const x10rt_msg_params *p, size_t blocks, size_t threads, size_t shm,
                       size_t argc, char *argv, size_t cmemc, char *cmemv)
{
    _X_(ANSI_X10RT<<"Receiving a kernel post callback, deserialising..."<<ANSI_RESET);
    {
        serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
        x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
        x10aux::CUDAPost post = x10aux::DeserializationDispatcher::getCUDAPost(sid);
        post(buf, p->dest_place, blocks, threads, shm, argc, argv, cmemc, cmemv);
    }
    {
        x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
        x10::lang::FinishState* fs = buf.read<x10::lang::FinishState*>();
        fs->notifyActivityCreation();
        fs->notifyActivityTermination();
    }
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
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::BufferFinder bf = x10aux::DeserializationDispatcher::getPutBufferFinder(sid);
    void *dropzone = bf(buf,len);
    assert(buf.consumed() <= p->len);
    return dropzone;
}

static void *cuda_receive_put (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a put, deserialising for cuda buffer finder..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::BufferFinder bf = x10aux::DeserializationDispatcher::getCUDAPutBufferFinder(sid);
    void *dropzone = bf(buf,len);
    assert(buf.consumed() <= p->len);
    return dropzone;
}

static void finished_put (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a put, deserialising for notifier..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::Notifier n = x10aux::DeserializationDispatcher::getPutNotifier(sid);
    n(buf,len);
    assert(buf.consumed() <= p->len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
}

static void cuda_finished_put (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a put, deserialising for cuda notifier..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
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
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
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
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
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
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
    // note: high bytes thrown away in implicit conversion
    serialization_id_t sid = x10aux::DeserializationDispatcher::getSerializationId(p->type);
    x10aux::Notifier n = x10aux::DeserializationDispatcher::getGetNotifier(sid);
    n(buf,len);
    assert(buf.consumed() <= p->len);
    deserialized_bytes += buf.consumed()  ; asyncs_received++;
}

static void cuda_finished_get (const x10rt_msg_params *p, x10aux::copy_sz len) {
    _X_(ANSI_X10RT<<"Receiving a get, deserialising for cuda notifier..."<<ANSI_RESET);
    x10aux::deserialization_buffer buf(static_cast<char*>(p->msg), p->len);
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
    buf.write((x10_ulong)(size_t)&finished);
    buf.write(addr);
    size_t len = buf.length();
    x10rt_msg_params p = {gpu, kernel_put, buf.borrow(), len, 0};
    x10rt_send_put(&p, var, sz);
    while (!finished) x10rt_probe();
}

// teams

void *x10aux::coll_enter() {
    x10::lang::FinishState* fs = Runtime::activity()->finishState();
    fs->notifySubActivitySpawn(x10::lang::Place::_make(x10aux::here));
    fs->notifyActivityCreation();
    return fs;
}

void x10aux::coll_handler(void *arg) {
    x10::lang::FinishState* fs = (x10::lang::FinishState*)arg;
    fs->notifyActivityTermination();
}

struct pointer_pair {
    void *fst;
    void *snd;
};
namespace x10aux {
    template<> inline const char *typeName<pointer_pair>() { return "pointer_pair"; }
}

void *x10aux::coll_enter2(void *arg) {
    struct pointer_pair *p = x10aux::system_alloc<struct pointer_pair>();
    p->fst = x10aux::coll_enter();
    p->snd = arg;
    return p;
}

void x10aux::coll_handler2(x10rt_team id, void *arg) {
    struct pointer_pair *p = (struct pointer_pair*)arg;
    x10::lang::FinishState *fs = (x10::lang::FinishState*)p->fst;
    x10rt_team *t = (x10rt_team*)p->snd;
    *t = id;
    x10aux::system_dealloc(p);
    fs->notifyActivityTermination();
}

#ifndef __MACH__
    extern char **environ;
#endif

x10::util::HashMap<x10::lang::String*,x10::lang::String*>* x10aux::loadenv() {
#ifdef __MACH__
    char** environ = *_NSGetEnviron();
#endif
    x10::util::HashMap<x10::lang::String*,x10::lang::String*>* map =
        x10::util::HashMap<x10::lang::String*, x10::lang::String*>::_make();
    for (unsigned i=0 ; environ[i]!=NULL ; ++i) {
        char *var = x10aux::alloc_utils::strdup(environ[i]);
        *strchr(var,'=') = '\0';
        char* val = getenv(var);
        assert(val!=NULL);
//        fprintf(stderr, "Loading environment variable %s=%s\n", var, val);
        map->put(x10::lang::String::Lit(var), x10::lang::String::Lit(val));
    }
    return map;
}

// vim:tabstop=4:shiftwidth=4:expandtab
