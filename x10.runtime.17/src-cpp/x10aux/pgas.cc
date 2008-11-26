#include <x10aux/config.h>
#include <x10aux/pgas.h>
#include <x10aux/closure.h>

#include <x10aux/serialization.h>

#include <x10/lang/VoidFun_0_0.h>

using namespace x10::lang;
using namespace x10aux;

volatile bool x10aux::ready_to_receive_asyncs = false;


int PGASInitializer::count = 0;

void x10aux::run_at(x10_int place, ref<VoidFun_0_0> body) {

    assert(place!=x10_here()); // this case should be handled earlier
    assert(place<x10_nplaces()); // this is ensured by XRX runtime

    serialization_buffer buf;

    ref<AnyClosure> body_value = body;

    addr_map m;
    _serialize_value_ref(buf, m, body_value);

    
    const x10_async_closure_t *cl =
        reinterpret_cast<const x10_async_closure_t*>(static_cast<const char*>(buf));
    x10_comm_handle_t handle = x10_async_spawn((x10_place_t)place, cl, buf.length(), NULL, 0);
    x10_async_spawn_wait(handle);

}

// vim:tabstop=4:shiftwidth=4:expandtab
