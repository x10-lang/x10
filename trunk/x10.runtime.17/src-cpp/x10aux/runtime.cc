#include <x10aux/config.h>

#include <x10aux/runtime.h>

#include <x10/x10.h>

void x10aux::init() {
    x10_init();
    _X_("x10.lib initialization complete");
}

void x10aux::shutdown() {
    /*
    if (x10_here() == 0) x10::finish_start(TERMINATE_COMPUTATION);
    else x10::finish_start(CS);
    */
    x10_finalize();
    _X_("x10.lib shutdown complete");
}

int x10aux::__init__::count = 0;

