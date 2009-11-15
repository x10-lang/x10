#ifndef X10AUX_NETWORK_H
#define X10AUX_NETWORK_H

#include <x10aux/config.h>
#include <x10aux/network.h>

#include <x10rt_front.h>

namespace x10 { namespace lang { class VoidFun_0_0; } }

namespace x10aux {

    typedef x10_short serialization_id_t;

    typedef x10rt_msg_type msg_type;
    typedef x10rt_copy_sz copy_sz;
    typedef x10_int place; // FIXME: should be x10rt_place, but place ids are signed everywhere

    // a message type used for putting serialised kernel data on a gpu 
    extern msg_type kernel_put;

    // caches to avoid repeatedly calling into x10rt for trivial things
    extern place num_places;
    extern place num_hosts;
    extern place here;

    // can be used to test whether the above caches contain valid data
    extern bool x10rt_initialized;

    inline place num_children (place p)       { return x10rt_nchildren(p); }
    inline x10_boolean is_host (place p)      { return x10rt_is_host(p); }
    inline place parent (place p)             { return x10rt_parent(p); }
    inline place child (place p, place index) { return x10rt_child(p, index); }
    inline place child_index (place p)        { return x10rt_child_index(p); }
    inline x10_boolean is_spe (place p)       { return x10rt_is_spe(p); }
    inline x10_boolean is_cuda (place p)      { return x10rt_is_cuda(p); }
    inline void event_probe (void)            { x10rt_probe(); }


    inline x10_ulong remote_alloc (place p, size_t sz) {
        _X_(ANSI_BOLD<<ANSI_X10RT<<"Remote alloc: "<<ANSI_RESET
            <<"size "<<sz<<" to place: "<<p);
        x10_ulong r = x10rt_remote_alloc(p, sz);
        _X_(ANSI_X10RT<<"got 0x"<<std::hex<<r<<std::dec<<" ("<<r<<")");
        return r;
    }

    inline void remote_free (place p, x10_ulong ptr) {
        _X_(ANSI_BOLD<<ANSI_X10RT<<"Remote free: "<<ANSI_RESET
            <<"ptr "<<std::hex<<ptr<<std::dec<<" to place: "<<p);
        x10rt_remote_free(p, ptr);
    }

    msg_type register_async_handler (const char *cubin=NULL, const char *kernel=NULL);
    msg_type register_put_handler (void);
    msg_type register_get_handler (void);

    void registration_complete (void);

    inline void *msg_realloc(void *old, size_t old_sz, size_t new_sz) {
        return x10rt_msg_realloc(old, old_sz, new_sz);
    }

    inline void *put_realloc(void *old, size_t old_sz, size_t new_sz) {
        return x10rt_put_realloc(old, old_sz, new_sz);
    }

    inline void *get_realloc(void *old, size_t old_sz, size_t new_sz) {
        return x10rt_get_realloc(old, old_sz, new_sz);
    }

    extern volatile x10_long asyncs_sent;
    extern volatile x10_long asyncs_received;
    extern volatile x10_long serialized_bytes;
    extern volatile x10_long deserialized_bytes;

    x10_int num_threads();

    x10_boolean no_steals();

    x10_boolean static_threads();

    inline void shutdown() {
        _X_("X10RT shutdown starting");
        x10rt_finalize();
        _X_("X10RT shutdown complete");
    }

}

#include <x10aux/ref.h>

namespace x10aux {

    void run_at (place p, x10aux::ref<x10::lang::Object> body);

    class serialization_buffer;

    void send_get (place p, serialization_id_t id,
                   serialization_buffer &buf, void *data, x10aux::copy_sz len);
   
    void send_put (place p, serialization_id_t id,
                   serialization_buffer &buf, void *data, x10aux::copy_sz len);

    void cuda_put (place gpu, x10_ulong addr, size_t &off, void *var, size_t sz);

    template<class T> void cuda_put (place gpu, x10_ulong addr, size_t &off, T var)
    { cuda_put(gpu, addr, off, &var, sizeof(var)); }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
