#include <x10aux/config.h>
#include <x10aux/bootstrap.h>

#include <x10/lang/Place.h>
#include <x10/runtime/Runtime.h>
#include <x10/io/Console.h>

using namespace x10aux;

x10_int x10aux::exitCode = 0;

void x10aux::initialize_xrx() {
    x10::lang::Place::_static_init();
    x10::runtime::Runtime::_static_init();
    x10::io::Console::_static_init();
}


// vim:tabstop=4:shiftwidth=4:expandtab
