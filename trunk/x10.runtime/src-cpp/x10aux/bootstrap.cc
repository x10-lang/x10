#include <x10aux/config.h>
#include <x10aux/bootstrap.h>

#include <x10/lang/Place.h>
#include <x10/lang/Runtime.h>
#include <x10/io/Console.h>

using namespace x10aux;

x10_int x10aux::exitCode = 0;

x10::lang::VoidFun_0_0::itable<StaticInitClosure> StaticInitClosure::_itable(&StaticInitClosure::apply, &StaticInitClosure::at, &StaticInitClosure::at,
                                                                             &StaticInitClosure::home, &StaticInitClosure::toString, &StaticInitClosure::typeName);

x10aux::itable_entry StaticInitClosure::_itables[2] = {
    x10aux::itable_entry(&x10::lang::VoidFun_0_0::rtt, &_itable),
    x10aux::itable_entry(NULL, NULL)
};

x10::lang::VoidFun_0_0::itable<BootStrapClosure> BootStrapClosure::_itable(&BootStrapClosure::apply, &BootStrapClosure::at, &BootStrapClosure::at,
                                                                           &BootStrapClosure::home, &BootStrapClosure::toString, &BootStrapClosure::typeName);

x10aux::itable_entry BootStrapClosure::_itables[2] = {
    x10aux::itable_entry(&x10::lang::VoidFun_0_0::rtt, &_itable),
    x10aux::itable_entry(NULL, NULL)
};

void x10aux::initialize_xrx() {
    x10::lang::Place_methods::FMGL(places__do_init)();
    x10::lang::Place_methods::FMGL(FIRST_PLACE__do_init)();
    x10::lang::Runtime::FMGL(runtime__do_init)();
    x10::io::Console::FMGL(OUT__do_init)();
    x10::io::Console::FMGL(ERR__do_init)();
    x10::io::Console::FMGL(IN__do_init)();
}


// vim:tabstop=4:shiftwidth=4:expandtab
