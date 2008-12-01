#ifndef X10AUX_PGAS_H
#define X10AUX_PGAS_H

#include <x10aux/config.h>

#ifdef X10_USE_BDWGC
#ifdef __linux__
#define GC_LINUX_THREADS
#endif 
#include "gc.h"
#endif

#include <x10/x10.h> //pgas

// Has to be first (aside from config.h which is inert and pgas itself)

namespace x10aux {

    class PGASInitializer {
        static int count;
    public:
        PGASInitializer() {
            if (count++ == 0) {
#ifdef X10_USE_BDWGC
                GC_INIT();
#endif                
                _X_("PGAS initialization starting");
                x10_init();
                _X_("PGAS initialization complete");
            }
        }

    };

#ifndef NO_IOSTREAM
    inline std::ostream &operator<<(std::ostream &o, const x10_remote_ref_t &rr) {
        return o << "rr("<<rr.addr<<"@"<<rr.loc<<")";
    }
#endif
}



#include <x10aux/ref.h>

namespace x10 { namespace lang { class VoidFun_0_0; } }


namespace x10aux {

    //inline x10_int here() { return (x10_int) x10_here(); }

    template<class T> inline x10_int location(T* p) {
        return (x10_int) x10_ref_get_loc((x10_addr_t)p);
    }

    template<class T> inline x10_int location(ref<T> r) {
        return (x10_int) x10_ref_get_loc((x10_addr_t)r.get());
    }

    void run_at(x10_int place, ref<x10::lang::VoidFun_0_0> body);

    inline void shutdown() {
        _X_("PGAS shutdown starting");
        x10_finalize();
        _X_("PGAS shutdown complete");
    }

    extern volatile x10_long serialized_bytes;
    extern volatile x10_long deserialized_bytes;

    inline x10_int num_places() {
        return x10_nplaces();
    }

    inline void event_loop() {
        x10_wait();
    }

    inline void event_probe() {
        x10_probe();
    }

    inline x10_boolean local(x10_int place) { return (x10_boolean) x10_here() == place; }
}

namespace {
    static x10aux::PGASInitializer pgas_initializer;
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
