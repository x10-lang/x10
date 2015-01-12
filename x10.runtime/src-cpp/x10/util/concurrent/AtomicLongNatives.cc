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

#include <x10aux/atomic_ops.h>
#include <x10/util/concurrent/AtomicLongNatives.h>
#include <x10/util/concurrent/AtomicLong.h>

using namespace x10aux;
using namespace x10::util::concurrent;

x10_boolean AtomicLongNatives::compareAndSet(AtomicLong* obj, x10_long expect, x10_long update) {
    x10_boolean tmp = atomic_ops::compareAndSet_64(&(obj->FMGL(value)), expect, update) == expect;

    // Memory Model: acts as both read and write of a volatile field.
    x10aux::atomic_ops::load_store_barrier();
    x10aux::atomic_ops::store_load_barrier();

    return tmp;
}

x10_boolean AtomicLongNatives::weakCompareAndSet(AtomicLong* obj, x10_long expect, x10_long update) {
    // Weak variant has no memory model implications.
    return atomic_ops::compareAndSet_64(&(obj->FMGL(value)), expect, update) == expect;
}

x10_long AtomicLongNatives::getAndAdd(AtomicLong* obj, x10_long delta) {
    x10_long oldValue = obj->FMGL(value);
    while (atomic_ops::compareAndSet_64(&(obj->FMGL(value)), oldValue, oldValue+delta) != oldValue) {
        oldValue = obj->FMGL(value);
    }

    // Memory Model: acts as both read and write of a volatile field.
    x10aux::atomic_ops::load_store_barrier();
    x10aux::atomic_ops::store_load_barrier();

    return oldValue;
}
	
x10_long AtomicLongNatives::addAndGet(AtomicLong* obj, x10_long delta) {
    x10_long oldValue = obj->FMGL(value);
    while (atomic_ops::compareAndSet_64(&(obj->FMGL(value)), oldValue, oldValue+delta) != oldValue) {
        oldValue = obj->FMGL(value);
    }

    // Memory Model: acts as both read and write of a volatile field.
    x10aux::atomic_ops::load_store_barrier();
    x10aux::atomic_ops::store_load_barrier();

    return oldValue + delta;
}

// vim:tabstop=4:shiftwidth=4:expandtab
