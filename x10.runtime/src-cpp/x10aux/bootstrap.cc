/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#include <x10aux/config.h>
#include <x10aux/bootstrap.h>

#include <x10/lang/Place.h>
#include <x10/lang/Runtime.h>
#include <x10/io/Console.h>

using namespace x10aux;

x10_int x10aux::exitCode = 0;

x10::lang::VoidFun_0_0::itable<StaticInitClosure> StaticInitClosure::_itable(&StaticInitClosure::equals, &StaticInitClosure::hashCode,
                                                                             &StaticInitClosure::__apply,
                                                                             &StaticInitClosure::toString, &StaticInitClosure::typeName);

x10aux::itable_entry StaticInitClosure::_itables[2] = {
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &_itable),
    x10aux::itable_entry(NULL, NULL)
};

x10::lang::VoidFun_0_0::itable<BootStrapClosure> BootStrapClosure::_itable(&BootStrapClosure::equals, &BootStrapClosure::hashCode,
                                                                           &BootStrapClosure::__apply,
                                                                           &BootStrapClosure::toString, &BootStrapClosure::typeName);

x10aux::itable_entry BootStrapClosure::_itables[2] = {
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &_itable),
    x10aux::itable_entry(NULL, NULL)
};

void x10aux::initialize_xrx() {
    x10::lang::Place::FMGL(places__do_init)();
    x10::lang::Place::FMGL(FIRST_PLACE__do_init)();
}


int x10aux::real_x10_main(int ac, char **av, ApplicationMainFunction mainFunc) {

    setlinebuf(stdout);

    x10aux::num_local_cores = sysconf(_SC_NPROCESSORS_ONLN);

    x10aux::network_init(ac,av);

#ifdef X10_USE_BDWGC
    GC_INIT();
#endif

#ifndef NO_EXCEPTIONS
    try {
#endif
        x10aux::place_local::initialize();

        // Initialise enough state to make this 'main' thread look like a normal x10 thread
        // (e.g. make Thread::CurrentThread work properly).
        x10::lang::Runtime__Worker::_make((x10_int)0);

        // Initialize a few key fields of XRX that must be set before any X10 code can execute
        x10aux::initialize_xrx();

        // Get the args into an X10 Array[String]
        x10aux::ref<x10::array::Array<x10aux::ref<x10::lang::String> > > args = x10aux::convert_args(ac, av);

        // Construct closure to invoke the static initialisers at place 0
        x10aux::ref<x10::lang::VoidFun_0_0> init_closure =
            x10aux::ref<StaticInitClosure>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(x10aux::StaticInitClosure)))
                                           x10aux::StaticInitClosure());

        // Construct closure to invoke the user's "public static def main(Array[String]) : void"
        // if at place 0 otherwise wait for asyncs.
        x10aux::ref<x10::lang::VoidFun_0_0> main_closure =
            x10aux::ref<BootStrapClosure>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(x10aux::BootStrapClosure)))
                                          x10aux::BootStrapClosure(mainFunc,args));

        // Bootup the serialization/deserialization code
        x10aux::DeserializationDispatcher::registerHandlers();

        // Actually start up the runtime and execute the program.
        // When this function returns, the program will have exited.
        x10::lang::Runtime::start(init_closure, main_closure);

#ifndef NO_EXCEPTIONS
    } catch(int exitCode) {

        x10aux::exitCode = exitCode;

    } catch(x10aux::__ref& e) {

        // Assume that only throwables can be thrown
        // and things are never thrown by interface (always cast to a value/object class)
        x10aux::ref<x10::lang::Throwable> &e_ =
            static_cast<x10aux::ref<x10::lang::Throwable>&>(e);

        fprintf(stderr, "Uncaught exception at place %ld: %s\n", (long)x10aux::here,
                x10aux::string_utils::cstr(nullCheck(nullCheck(e_)->toString())));

        e_->printStackTrace();

        x10aux::exitCode = 1;

    } catch(...) {

        fprintf(stderr, "Caught unrecognised exception at place %ld\n", (long)x10aux::here);
        x10aux::exitCode = 1;

    }
#endif

    // We're done.  Shutdown the place.
    x10aux::shutdown();

    if (x10aux::trace_rxtx)
        fprintf(stderr, "Place: %ld   rx: %lld/%lld   tx: %lld/%lld\n",
                (long)x10aux::here,
                (long long)x10aux::deserialized_bytes, (long long)x10aux::asyncs_received,
                (long long)x10aux::serialized_bytes, (long long)x10aux::asyncs_sent);

    return x10aux::exitCode;
}

// vim:tabstop=4:shiftwidth=4:expandtab
