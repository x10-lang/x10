#include <x10aux/config.h>

#include <x10aux/pgas.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>

#include <x10aux/cuda/cuda_utils.h>

#include <x10aux/serialization.h>
#include <x10aux/deserialization_dispatcher.h>

#include <x10/lang/VoidFun_0_0.h>
#include <x10/lang/String.h> // for debug output

using namespace x10::lang;
using namespace x10aux;

// keep a counter for the session.
volatile x10_long x10aux::serialized_bytes = 0;
volatile x10_long x10aux::deserialized_bytes = 0;

volatile int PGASInitializer::count = 0;


void x10aux::run_at(x10_int place, x10aux::ref<VoidFun_0_0> body) {

    assert(place!=x10rt_here()); // this case should be handled earlier
    assert(place<x10rt_nplaces()); // this is ensured by XRX runtime

    serialization_buffer buf;

    addr_map m;
    _X_(ANSI_BOLD ANSI_PGAS"Transmitting an async: "ANSI_RESET<<body->toString()->c_str()
        <<" to place: "<<place);
    buf.write(body,m);
    serialized_bytes += buf.length();

    void *handle = x10rt_async_spawn(place, buf.get(), buf.length(), QUEUED_ASYNC);
    x10rt_async_spawn_wait(handle);

}

x10_int x10aux::num_threads() {

	x10_int num = 2;
	const char* env = getenv("X10_NTHREADS");
	if (NULL != env) {
		num = (x10_int) strtol(env, NULL, 10);
		assert (num > 0); 
	}

    return num;
}

x10_boolean x10aux::no_steals() {

	return (x10_boolean) (getenv("X10_NO_STEALS") != NULL);

}

#include <pthread.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/String.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/Rail.h>
#include <x10/lang/ValRail.h>
#include <x10/runtime/Thread.h>

#if 1
// this one for when pgas does not use an internal thread
static void deserialize_remote_closure(void *cl, int) {
        _X_(ANSI_PGAS"Receiving an async, deserialising..."ANSI_RESET);
        x10aux::serialization_buffer buf(reinterpret_cast<const char*>(cl));
        ref<VoidFun_0_0> async = x10aux::DeserializationDispatcher::create<VoidFun_0_0>(buf);
        _X_("The deserialised async was: "<<async->toString()->c_str());
        deserialized_bytes += buf.length();
        async->apply();
}
#endif
#if 0
// this one otherwise (might have to be rewritten -- contacts are Sreedhar Kodali & Dave C)
static void deserialize_remote_closure(x10_async_closure_t *cl, int) {

#ifndef NO_EXCEPTIONS
    try {
#endif

        //fprintf(stderr,"pthread: %p\n",pthread_self());

        // init XRX info for this internal pgas thread if it's not already done
        if (x10::runtime::Thread::currentThread()==x10aux::null) {
            // should happen first time we dispatch on an async or never for lapi
            (void) X10NEW(x10::runtime::Thread)(x10aux::null,
                                                String::Lit("async dispatch thread"));
        }

        _X_(ANSI_PGAS"Receiving an async, deserialising..."ANSI_RESET);
        x10aux::serialization_buffer buf;
        buf.set(reinterpret_cast<const char*>(cl));
        ref<VoidFun_0_0> async = x10aux::DeserializationDispatcher::create<VoidFun_0_0>(buf);
        _X_("The deserialised async was: "<<async->toString()->c_str());
        async->apply();
        
#ifndef NO_EXCEPTIONS
    /* TODO: need some other mechanism for calling exit() from another place
    } catch(int exitCode) {
        x10aux::exitCode = exitCode;
    */

    } catch(x10aux::__ref& e) {

        using namespace x10::lang;
        // Assume that only throwables can be thrown
        x10aux::ref<Throwable> &e_ = static_cast<x10aux::ref<Throwable>&>(e);

        fprintf(stderr, "Uncaught exception at place %d of type: %s\n",
                            (int)x10_here(), e_->_type()->name().c_str());
        fprintf(stderr, "%s\n", e_->toString()->c_str());

        x10aux::ref<ValRail<x10aux::ref<String> > > trace = e_->getStackTrace();

        x10aux::ref<Iterator<x10aux::ref<String> > > it = trace->iterator();
        while (it->hasNext()) {
            fprintf(stderr, "        at %s\n", it->next()->c_str());
        }

    } catch(...) {

        fprintf(stderr, "Caught unrecognised exception at place %d\n", (int)x10_here());

    }
#endif

}
#endif

PGASInitializer::PGASInitializer() {
    if (count++ == 0) {
#ifdef X10_USE_BDWGC
        GC_INIT();
#endif
        RuntimeType::bootstrap();
        _X_("PGAS initialization starting");
        x10rt_register_async_callback(deserialize_remote_closure);
        x10rt_init();
        _X_("PGAS initialization complete");
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
