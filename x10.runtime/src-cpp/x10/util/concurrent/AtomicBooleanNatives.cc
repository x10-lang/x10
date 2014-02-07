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
#include <x10/util/concurrent/AtomicBooleanNatives.h>
#include <x10/util/concurrent/AtomicBoolean.h>

using namespace x10::util::concurrent;

x10_boolean AtomicBooleanNatives::compareAndSet(AtomicBoolean *obj,
                                               x10_boolean expect, x10_boolean update) {
    x10_int expectI = expect ? 1 : 0;
    x10_int updateI = update ? 1 : 0;
    x10_int oldVal = x10aux::atomic_ops::compareAndSet_32(&(obj->FMGL(value)), expectI, updateI) == expectI;

    // Memory Model: acts as both read and write of a volatile field.
    x10aux::atomic_ops::load_store_barrier();
    x10aux::atomic_ops::store_load_barrier();
    
    return oldVal == 1;
}
                    
x10_boolean AtomicBooleanNatives::weakCompareAndSet(AtomicBoolean *obj,
                                                   x10_boolean expect, x10_boolean update) {
    x10_int expectI = expect ? 1 : 0;
    x10_int updateI = update ? 1 : 0;
    x10_int oldVal = x10aux::atomic_ops::compareAndSet_32(&(obj->FMGL(value)), expectI, updateI) == expectI;

    // Weak variant has no memory model implications.
    
    return oldVal == 1;
}

// vim:tabstop=4:shiftwidth=4:expandtab
