/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#include <x10aux/atomic_ops.h>
#include <x10/util/concurrent/AtomicIntegerNatives.h>
#include <x10/util/concurrent/AtomicInteger.h>

using namespace x10aux;
using namespace x10::util::concurrent;

x10_boolean AtomicIntegerNatives::compareAndSet(AtomicInteger* obj, x10_int expect, x10_int update) {
    x10_boolean tmp = atomic_ops::compareAndSet_32(&(obj->FMGL(value)), expect, update) == expect;

    // Memory Model: acts as both read and write of a volatile field.
    x10aux::atomic_ops::load_store_barrier();
    x10aux::atomic_ops::store_load_barrier();    

    return tmp;
}

x10_boolean AtomicIntegerNatives::weakCompareAndSet(AtomicInteger* obj, x10_int expect, x10_int update) {
    // Weak variant has no memory model implications.

    return atomic_ops::compareAndSet_32(&(obj->FMGL(value)), expect, update) == expect;
}

x10_int AtomicIntegerNatives::getAndAdd(AtomicInteger *obj, x10_int delta) {
    x10_int oldValue = obj->FMGL(value);
    while (atomic_ops::compareAndSet_32(&(obj->FMGL(value)), oldValue, oldValue+delta) != oldValue) {
        oldValue = obj->FMGL(value);
    }

    // Memory Model: acts as both read and write of a volatile field.
    x10aux::atomic_ops::load_store_barrier();
    x10aux::atomic_ops::store_load_barrier();

    return oldValue;
}

x10_int AtomicIntegerNatives::addAndGet(AtomicInteger* obj, x10_int delta) {
    x10_int oldValue = obj->FMGL(value);
    while (atomic_ops::compareAndSet_32(&(obj->FMGL(value)), oldValue, oldValue+delta) != oldValue) {
        oldValue = obj->FMGL(value);
    }

    // Memory Model: acts as both read and write of a volatile field.
    x10aux::atomic_ops::load_store_barrier();
    x10aux::atomic_ops::store_load_barrier();

    return oldValue + delta;
}

// vim:tabstop=4:shiftwidth=4:expandtab
