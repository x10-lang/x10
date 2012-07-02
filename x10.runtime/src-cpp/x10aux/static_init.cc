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
#include <x10aux/static_init.h>
#include <x10aux/alloc.h>
#include <x10aux/network.h>

#include <x10/lang/Runtime.h>

#include <assert.h>

using namespace x10aux;
using namespace x10::lang;

void StaticInitController::initField(volatile x10aux::status* flag, void (*init_func)(void), const char* fname) {
    {
        x10aux::status __var1__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)flag, (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var1__ != x10aux::UNINITIALIZED) goto WAIT;

        // I changed *flag from UNINITIALIZED to INITIALIZING, so I call the init_func
        // init_func will initialize the field and set *flag to INITIALIZED.
        (*init_func)();

        // Notify all threads that are waiting on static fields to be initialized.
        lock();
        notify();
    }

WAIT:
    if (*flag != x10aux::INITIALIZED) {
        // Wait for the field to be initialized by some other thread
        char buffer[256];
        lock();

        if (x10aux::trace_static_init) {
            snprintf(buffer, 255, "WAITING for field: %s to be initialized\0", fname);
            _SI_(buffer);
        }

        while (*flag != x10aux::INITIALIZED) await();

        if (x10aux::trace_static_init) {
            snprintf(buffer, 255, "CONTINUING because field: %s has been initialized\0", fname);
            _SI_(buffer);
        }

        unlock();
    }
}


void StaticInitController::lock() {
    x10::lang::Runtime::StaticInitBroadcastDispatcherLock();
}

void  StaticInitController::await() {
    x10::lang::Runtime::StaticInitBroadcastDispatcherAwait();
}

void  StaticInitController::unlock() {
    x10::lang::Runtime::StaticInitBroadcastDispatcherUnlock();
}

void  StaticInitController::notify() {
    x10::lang::Runtime::StaticInitBroadcastDispatcherNotify();
}

// vim:tabstop=4:shiftwidth=4:expandtab
