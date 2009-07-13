#ifndef X10AUX_PGAS_H
#define X10AUX_PGAS_H

#include <x10aux/config.h>

#ifdef X10_USE_BDWGC
#ifdef __linux__
#define GC_LINUX_THREADS
#endif 
#include "gc.h"
#endif

#include <x10/pgasrt_x10.h> //pgas


// Has to be first (aside from config.h which is inert and pgas itself)

namespace x10aux {

    class PGASInitializer {
    private:
        static volatile int count;
    public:
        PGASInitializer();
    };
}

#include <x10aux/ref.h>


namespace x10 { namespace lang { class VoidFun_0_0; } }

namespace x10aux {

    void run_at(x10_int place, x10aux::ref<x10::lang::VoidFun_0_0> body);

    // all places must reach the barrier before any may continue
    inline void barrier() {
        x10rt_barrier();
    }
   
    inline x10_int num_places() {
        return x10rt_nplaces();
    }

    inline void event_probe() {
        x10rt_probe();
    }

    inline x10_int here() {
    	return (x10_int)x10rt_here();
    }

    inline x10_boolean local(x10_int place) {
    	return (x10_boolean) (here() == place);
    }

    extern volatile x10_long serialized_bytes;
    extern volatile x10_long deserialized_bytes;

    x10_int num_threads();

    x10_boolean no_steals();

    inline void shutdown() {
        _X_("PGAS shutdown starting");
        x10rt_finalize();
        _X_("PGAS shutdown complete");
    }

}

namespace {
    static x10aux::PGASInitializer pgas_initializer;
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
