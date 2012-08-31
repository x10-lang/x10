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

#include <x10aux/atomic_int_funs.h>
#include <x10aux/atomic_ops.h>
#include <x10/util/concurrent/AtomicLong.h>

using namespace x10aux;
using namespace x10::util::concurrent;


x10_boolean atomic_long_funs::compareAndSet(AtomicLong* obj, x10_long expect, x10_long update) {
    return atomic_ops::compareAndSet_64(&(obj->FMGL(value)), expect, update) == expect;
}

x10_boolean atomic_long_funs::weakCompareAndSet(AtomicLong* obj, x10_long expect, x10_long update) {
    // TODO: for minor optimization on ppc we could add a weakCompareAndSet in atomic_ops and use that here
    return atomic_ops::compareAndSet_64(&(obj->FMGL(value)), expect, update) == expect;
}

x10_long atomic_long_funs::getAndAdd(AtomicLong* obj, x10_long delta) {
    x10_long oldValue = obj->FMGL(value);
    while (atomic_ops::compareAndSet_64(&(obj->FMGL(value)), oldValue, oldValue+delta) != oldValue) {
        oldValue = obj->FMGL(value);
    }
    return oldValue;
}
	
x10_long atomic_long_funs::addAndGet(AtomicLong* obj, x10_long delta) {
    x10_long oldValue = obj->FMGL(value);
    while (atomic_ops::compareAndSet_64(&(obj->FMGL(value)), oldValue, oldValue+delta) != oldValue) {
        oldValue = obj->FMGL(value);
    }
    return oldValue + delta;
}

// vim:tabstop=4:shiftwidth=4:expandtab
