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
#include <x10aux/static_init.h>
#include <x10aux/alloc.h>
#include <x10aux/network.h>
#include <x10aux/atomic_ops.h>

#include <x10/lang/Runtime.h>
#include <x10/lang/ExceptionInInitializer.h>

#include <assert.h>

using namespace x10aux;
using namespace x10::lang;

void StaticInitController::initField(volatile status* flag,
                                     void (*init_func)(void),
                                     x10::lang::CheckedThrowable** exceptionHolder,
                                     const char* fname) {
    {
        status __var1__ = (status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)flag, (x10_int)UNINITIALIZED, (x10_int)INITIALIZING);
        if (__var1__ != UNINITIALIZED) goto WAIT;

        try {
            // I changed *flag from UNINITIALIZED to INITIALIZING, so I call the init_func.
            // init_func will evalute the field init expr, store the value in the field and set *flag to INITIALIZED.
            (*init_func)();
        } catch (x10::lang::CheckedThrowable* exceptObj) {
            *exceptionHolder = exceptObj;

            *flag = EXCEPTION_RAISED;
            
            // Notify all threads that are waiting on static fields to be initialized.
            lock();
            notify();

            x10aux::throwException(x10::lang::ExceptionInInitializer::_make(*exceptionHolder));
        }
            
        // Notify all threads that are waiting on static fields to be initialized.
        lock();
        notify();
    }

WAIT:
    if (*flag != INITIALIZED) {
        // Wait for the field to be initialized by some other thread
        char buffer[256];
        lock();

        if (x10aux::trace_static_init) {
            snprintf(buffer, 255, "WAITING for field: %s to be initialized", fname);
            _SI_(buffer);
        }

        while (*flag != INITIALIZED) {
            if (*flag == EXCEPTION_RAISED) {
                if (x10aux::trace_static_init) {
                    snprintf(buffer, 255, "Rethrowing ExceptionInInitializer for field: %s", fname);
                    _SI_(buffer);
                }
                unlock();
                if (NULL != exceptionHolder) {
                    x10aux::throwException(x10::lang::ExceptionInInitializer::_make(*exceptionHolder));
                } else {
                    x10aux::throwException(x10::lang::ExceptionInInitializer::_make());
                }
            }
            await();
        }

        if (x10aux::trace_static_init) {
            snprintf(buffer, 255, "CONTINUING because field: %s has been initialized", fname);
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
