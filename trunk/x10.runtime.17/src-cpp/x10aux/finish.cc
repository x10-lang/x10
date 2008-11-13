#include <x10aux/config.h>
#include <x10aux/finish.h>

#include <x10/x10.h>

void x10aux::general_finish_start() {
    _X_("invoking general_finish_start()");
    x10_finish_begin();
}

void x10aux::general_wait() {
    _X_("invoking general_wait()");
    x10_wait();
}

void x10aux::general_finish_end() {
    void* exc_buf;
    int num_exc = 0;
    _X_("invoking general_finish_end()");
    x10_finish_end(&exc_buf, &num_exc);
/*
    if (num_exc > 0) {
        _X_("   got " << num_exc << " exceptions");
#ifndef NO_EXCEPTIONS
        throw x10::ref<x10::lang::MultipleExceptions>(new (x10::alloc<x10::lang::MultipleExceptions>()) x10::lang::MultipleExceptions(num_exc, exc_buf));
#endif
    }
*/
}

