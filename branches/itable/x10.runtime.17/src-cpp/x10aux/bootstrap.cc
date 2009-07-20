#include <x10aux/config.h>
#include <x10aux/bootstrap.h>

#include <x10/lang/Place.h>
#include <x10/runtime/Runtime.h>
#include <x10/io/Console.h>

using namespace x10aux;

x10_int x10aux::exitCode = 0;

x10::lang::VoidFun_0_0::itable<BootStrapClosure> BootStrapClosure::_itable(&BootStrapClosure::apply);

x10aux::itable_entry BootStrapClosure::_itables[2] = {
    x10aux::itable_entry(&x10::lang::VoidFun_0_0::rtt, &_itable),
    x10aux::itable_entry(NULL, NULL)
};
    
void x10aux::initialize_xrx() {
    x10::lang::Place::_static_init();
    x10::runtime::Runtime::_static_init();
    x10::io::Console::_static_init();
}


// vim:tabstop=4:shiftwidth=4:expandtab
