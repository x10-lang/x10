/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#include <x10aux/config.h>
#include <x10aux/bootstrap.h>
#include <x10aux/place_local.h>
#include <x10aux/alloc.h>

#include <pthread.h>
#include <stdio.h>
#include <unistd.h>

#include <x10/xrx/Thread.h>
#include <x10/lang/Rail.h>
#include <x10/lang/String.h>
#include <x10/util/Team.h>

using namespace x10aux;

volatile x10_int x10aux::exitCode = 0;

x10::lang::VoidFun_0_0::itable<BootStrapClosure> BootStrapClosure::_itable(&BootStrapClosure::equals, &BootStrapClosure::hashCode,
                                                                           &BootStrapClosure::__apply,
                                                                           &BootStrapClosure::toString, &BootStrapClosure::typeName);

x10aux::itable_entry BootStrapClosure::_itables[2] = {
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::VoidFun_0_0>, &_itable),
    x10aux::itable_entry(NULL, NULL)
};

void x10aux::initialize_xrx() {
    x10::xrx::Runtime::FMGL(staticMonitor__do_init)();
//    x10::xrx::Runtime::FMGL(env__do_init)();
    x10::xrx::Runtime::FMGL(STRICT_FINISH__do_init)();
    x10::xrx::Runtime::FMGL(NTHREADS__do_init)();
    x10::xrx::Runtime::FMGL(MAX_THREADS__do_init)();
    x10::xrx::Runtime::FMGL(STATIC_THREADS__do_init)();
    x10::xrx::Runtime::FMGL(NUM_IMMEDIATE_THREADS__do_init)();
    x10::xrx::Runtime::FMGL(WARN_ON_THREAD_CREATION__do_init)();
    x10::xrx::Runtime::FMGL(BUSY_WAITING__do_init)();
    x10::xrx::Runtime::FMGL(CANCELLABLE__do_init)();
    x10::xrx::Runtime::FMGL(RESILIENT_MODE__do_init)();
    x10::util::Team::FMGL(WORLD__do_init)();
//    x10::lang::Place::FMGL(places__do_init)();
//    x10::lang::Place::FMGL(FIRST_PLACE__do_init)();
}

struct x10_main_args {
    int ac;
    char **av;
    ApplicationMainFunction mainFunc;    
};

static x10::lang::Rail<x10::lang::String*>* convert_args(int ac, char **av) {
    assert(ac>=1);
    x10_int x10_argc = ac  - 1;
    x10::lang::Rail<x10::lang::String*>* arr(x10::lang::Rail<x10::lang::String*>::_make(x10_argc));
    for (int i = 1; i < ac; i++) {
        x10::lang::String* val = x10::lang::String::Lit(av[i]);
        arr->__set(i-1, val);
    }
    return arr;
}

static void* real_x10_main_inner(void* args);

void x10aux::apgas_main(int argc, char** argv) {
    x10_main_args args;
    args.ac = argc;
    args.av = argv;
    args.mainFunc = NULL;
    real_x10_main_inner(&args);

    x10::xrx::Activity::FMGL(DEALLOC_BODY__get)();
    x10::xrx::Activity::FMGL(DEALLOC_BODY) = false;
}

int x10aux::real_x10_main(int ac, char **av, ApplicationMainFunction mainFunc) {
#if defined(__bg__)
    x10_main_args args;
    args.ac = ac;
    args.av = av;
    args.mainFunc = mainFunc;
    real_x10_main_inner(&args);
#else
    x10_main_args* args = x10aux::system_alloc<x10_main_args>();
    args->ac = ac;
    args->av = av;
    args->mainFunc = mainFunc;

    pthread_t* xthread = x10aux::system_alloc<pthread_t>();
    pthread_attr_t* xthread_attr = x10aux::system_alloc<pthread_attr_t>();

    (void)pthread_attr_init(xthread_attr);
    x10::xrx::Thread::initAttributes(xthread_attr);
    
    int err = pthread_create(xthread, xthread_attr,
                             &real_x10_main_inner, (void *)args);
    if (err) {
        ::fprintf(stderr,"Could not create first worker thread: %s\n", ::strerror(err));
        ::abort();
    }

    pthread_join(*xthread, NULL);

#endif
    
    return x10aux::exitCode;
}    

static void* real_x10_main_inner(void* _main_args) {
    x10_main_args* main_args = (x10_main_args*)_main_args;

    setlinebuf(stdout);

    x10aux::num_local_cores = sysconf(_SC_NPROCESSORS_ONLN);

#ifdef X10_USE_BDWGC
    GC_INIT();
#endif

    x10aux::network_init(main_args->ac, main_args->av);

    x10aux::RuntimeType::initializeForMultiThreading();

    try {
        x10aux::place_local::initialize();

        // Initialize a few key fields of XRX that must be set before any X10 code can execute
        x10aux::initialize_xrx();

        // NOTE: this statement must match the one setting workers.multiplace in Pool.this()
        bool multiplace = x10rt_nplaces() > 1 || x10::xrx::Configuration::resilient_mode() != x10::xrx::Configuration::FMGL(RESILIENT_MODE_NONE);
        if (x10::xrx::Configuration::num_immediate_threads() == 0 || !multiplace){
        	// Initialise enough state to make this 'main' thread look like a normal x10 thread
        	// (e.g. make Thread::CurrentThread work properly).
        	x10::xrx::Worker::_make((x10_int)0);
        }
        else {
        	// initialize this thread as the first immediate thread
        	x10::xrx::Worker::_make(x10::xrx::Configuration::nthreads(), true, x10::lang::String::_make("@ImmediateWorker-0", false));
        }

        // Get the args into an X10 Rail[String]
        x10::lang::Rail<x10::lang::String*>* args = convert_args(main_args->ac, main_args->av);

        // Bootup the network message handling code
        x10aux::NetworkDispatcher::registerHandlers();
        x10rt_registration_complete();

        if (NULL == main_args->mainFunc) {
            // Actually start up the runtime. Returns as soon as Runtime in thie Place has started.
            x10::xrx::Runtime::start();
        } else {
            // Construct closure to invoke the user's "public static def main(Rail[String]) : void"
            // if at place 0 otherwise wait for asyncs.
            x10::lang::VoidFun_0_0* main_closure =
                reinterpret_cast<x10::lang::VoidFun_0_0*>(new (x10aux::alloc<x10::lang::VoidFun_0_0>(sizeof(x10aux::BootStrapClosure))) x10aux::BootStrapClosure(main_args->mainFunc, args));

            // Actually start up the runtime and execute the program.
            // When this function returns, the program will have exited.
            x10::xrx::Runtime::start(main_closure);
        }
    } catch(int exitCode) {

        x10aux::exitCode = exitCode;

    } catch(x10::lang::CheckedThrowable* e) {
        fprintf(stderr, "Uncaught exception at place %ld: %s\n", (long)x10aux::here, e->toString()->c_str());

        e->printStackTrace();

        x10aux::exitCode = 1;

    } catch(...) {

        fprintf(stderr, "Caught unrecognised exception at place %ld\n", (long)x10aux::here);
        x10aux::exitCode = 1;

    }

    if (NULL != main_args->mainFunc) {
        // We're done with a normal X10 execution.  Shutdown the place.
        x10aux::shutdown();

        if (x10aux::trace_rxtx)
            fprintf(stderr, "Place: %ld   rx: %lld/%lld   tx: %lld/%lld\n",
                    (long)x10aux::here,
                    (long long)x10aux::deserialized_bytes, (long long)x10aux::asyncs_received,
                    (long long)x10aux::serialized_bytes, (long long)x10aux::asyncs_sent);
    }

    return NULL;
}

// vim:tabstop=4:shiftwidth=4:expandtab
