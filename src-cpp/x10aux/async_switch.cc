#include <x10aux/config.h>
#include <x10aux/async_switch.h>
#include <x10aux/pgas.h>

#include <x10/runtime/Thread.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/ValRail.h>

using namespace x10aux;
using namespace x10::lang;

AsyncSwitch *AsyncSwitch::it;

extern "C" {
    void __x10_callback_asyncswitch(x10_async_closure_t *cl, x10_clock_t *, int) {


#ifndef NO_EXCEPTIONS
        try {
#endif

        x10aux::serialization_buffer buf;

        buf.set(reinterpret_cast<char*>(cl));

        x10aux::AsyncSwitch::dispatch(buf);

        buf.set(NULL); // hack since the memory was allocated by pgas, not by buf

#ifndef NO_EXCEPTIONS
/* TODO: need some other mechanism for calling exit() from another place
        } catch(int exitCode) {
            x10aux::exitCode = exitCode;
*/

        } catch(x10aux::__ref& e) {

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
}

// vim:tabstop=4:shiftwidth=4:expandtab
