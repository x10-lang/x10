#ifndef X10AUX_NETWORK_H
#define X10AUX_NETWORK_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

#include <x10/x10rt_api.h> //x10rt

namespace x10 { namespace lang { class VoidFun_0_0; } }

namespace x10aux {

    void run_at(x10_uint place, x10aux::ref<x10::lang::Object> body);
   
    // caches to avoid repeatedly calling into x10rt for trivial things
    extern x10_int num_places;
    extern x10_int num_hosts;
    extern x10_int here;

    inline x10_int num_children(x10_int place) {
        #ifdef X10RT_SUPPORTS_ACCELERATORS
        return x10rt_nchildren(place);
        #else
        return 0;
        #endif
    }

    inline x10_boolean is_host(x10_int place) {
        #ifdef X10RT_SUPPORTS_ACCELERATORS
        return x10rt_is_host(place);
        #else
        return true;
        #endif
    }

    inline x10_int parent(x10_int place) {
        #ifdef X10RT_SUPPORTS_ACCELERATORS
        return x10rt_parent(place);
        #else
        return place;
        #endif
    }

    inline x10_int child(x10_int place, x10_int index) {
        #ifdef X10RT_SUPPORTS_ACCELERATORS
        return x10rt_child(place, index);
        #else
        abort();
        #endif
    }

    inline x10_boolean is_spe(x10_int place) {
        #ifdef X10RT_SUPPORTS_CELL
        return x10rt_is_spe(place);
        #else
        return false;
        #endif
    }

    inline x10_boolean is_cuda(x10_int place) {
        #ifdef X10RT_SUPPORTS_CUDA
        return x10rt_is_spe(place);
        #else
        return false;
        #endif
    }

    inline x10_ulong remote_alloc (x10_int place, size_t sz) {
        #ifdef X10RT_SUPPORTS_CUDA
        return x10rt_remote_alloc(place, sz);
        #else
        return 0;
        #endif
    }

    inline void remote_free (x10_int place, x10_ulong ptr) {
        #ifdef X10RT_SUPPORTS_CUDA
        x10rt_remote_free(place, ptr);
        #endif
    }

    void receive_async (void*);

    inline void register_async_handler (unsigned id) {
        x10rt_register_msg_receiver(id, receive_async);
        // [DC] these belong in registration_complete but currently statics are broken
        here = x10rt_here();
        num_places = x10rt_nplaces();
        #ifdef X10RT_SUPPORTS_ACCELERATORS
        num_hosts = x10rt_nhosts();
        #else
        num_hosts = num_places;
        #endif
    }

    inline void registration_complete (void) {
        x10rt_registration_complete();
    }


    inline void event_probe() {
        x10rt_probe();
    }

    extern volatile x10_long asyncs_sent;
    extern volatile x10_long asyncs_received;
    extern volatile x10_long serialized_bytes;
    extern volatile x10_long deserialized_bytes;

    x10_int num_threads();

    x10_boolean no_steals();

    inline void shutdown() {
        _X_("X10RT shutdown starting");
        x10rt_finalize();
        _X_("X10RT shutdown complete");
    }

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
