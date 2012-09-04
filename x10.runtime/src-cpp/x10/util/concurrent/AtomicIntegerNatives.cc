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

#include <x10aux/atomic_ops.h>
#include <x10/util/concurrent/AtomicIntegerNatives.h>
#include <x10/util/concurrent/AtomicInteger.h>

using namespace x10aux;
using namespace x10::util::concurrent;

x10_boolean AtomicIntegerNatives::compareAndSet(AtomicInteger* obj, x10_int expect, x10_int update) {
    return atomic_ops::compareAndSet_32(&(obj->FMGL(value)), expect, update) == expect;
}

x10_boolean AtomicIntegerNatives::weakCompareAndSet(AtomicInteger* obj, x10_int expect, x10_int update) {
    // TODO: for minor optimization on ppc we could add a weakCompareAndSet in atomic_ops and use that here
    return atomic_ops::compareAndSet_32(&(obj->FMGL(value)), expect, update) == expect;
}

x10_int AtomicIntegerNatives::getAndAdd(AtomicInteger *obj, x10_int delta) {
    x10_int oldValue = obj->FMGL(value);
    while (atomic_ops::compareAndSet_32(&(obj->FMGL(value)), oldValue, oldValue+delta) != oldValue) {
        oldValue = obj->FMGL(value);
    }
    return oldValue;
}

x10_int AtomicIntegerNatives::addAndGet(AtomicInteger* obj, x10_int delta) {
    x10_int oldValue = obj->FMGL(value);
    while (atomic_ops::compareAndSet_32(&(obj->FMGL(value)), oldValue, oldValue+delta) != oldValue) {
        oldValue = obj->FMGL(value);
    }
    return oldValue + delta;
}

// vim:tabstop=4:shiftwidth=4:expandtab
