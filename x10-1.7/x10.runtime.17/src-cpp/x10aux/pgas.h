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

extern "C" {
    void __pgasrt_tsp_barrier(int,int);
}


// Has to be first (aside from config.h which is inert and pgas itself)

namespace x10aux {

    /* callback */
    void remote_closure_callback(void *, int tag);

    class PGASInitializer {
    private:
        static volatile int count;
        static void bootstrapRTT();
    public:
        PGASInitializer();
    };

    #ifndef NO_IOSTREAM
    inline std::ostream &operator<<(std::ostream &o, const x10rt_wire_t &rr) {
        return o << "rr("<<rr.addr<<"@"<<rr.loc<<")";
    }
    #endif
}



#include <x10aux/ref.h>
#include <x10aux/reference_logger.h>

namespace x10 { namespace lang { class VoidFun_0_0; } }


namespace x10aux {

    template<class T> inline x10_int location(T* p) {
        return x10rt_ref_get_loc(p);
    }

    template<class T> inline x10_int location(ref<T> r) {
        return location(r.get());
    }

    void run_at(x10_int place, ref<x10::lang::VoidFun_0_0> body);

    inline void shutdown() {
        _X_("PGAS shutdown starting");
        x10rt_finalize();
        _X_("PGAS shutdown complete");
    }

    extern volatile x10_long serialized_bytes;
    extern volatile x10_long deserialized_bytes;

    // all places must reach the barrier before any may continue
    inline void barrier() {
        __pgasrt_tsp_barrier(0,1);
    }
   
    template<class T> x10aux::ref<T> ref_deserialize(const x10rt_wire_t &remote_ref) {
        void *flagged = x10rt_ref_deserialize(remote_ref);
        if (x10rt_ref_get_addr(flagged) == NULL) return x10aux::null;
        return (T*)flagged;
    }

    template<class T> x10rt_wire_t ref_serialize(T *remote_ref) {
        #if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)
        ReferenceLogger::log(remote_ref);
        #endif
        return x10rt_ref_serialize(remote_ref);
    }


    inline x10_int num_places() {
        return x10rt_nplaces();
    }

    x10_int num_threads();

    x10_boolean no_steals();

    inline void event_probe() {
        x10rt_probe();
    }

    inline x10_int here() {
    	return (x10_int)x10rt_here();
    }

    inline x10_boolean local(x10_int place) {
    	return (x10_boolean) (here() == place);
    }
}

namespace {
    static x10aux::PGASInitializer pgas_initializer;
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
