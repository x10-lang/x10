#include <x10aux/config.h>
#include <x10aux/async_switch.h>
#include <x10aux/pgas.h>

#include <x10/runtime/Thread.h>

using namespace x10aux;
using namespace x10::lang;

AsyncSwitch *AsyncSwitch::it;

extern "C" {
    void __x10_callback_asyncswitch(x10_async_closure_t* cl, x10_clock_t* clocks, int num_clocks) {
        (void) clocks; (void) num_clocks;
        x10aux::serialization_buffer buf;
        buf.set(reinterpret_cast<char*>(cl));
        x10aux::AsyncSwitch::dispatch(buf);
        buf.set(NULL); // hack since the memory was allocated by pgas, not by buf
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
