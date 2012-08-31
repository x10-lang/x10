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

#include <x10aux/atomic_boolean_funs.h>
#include <x10aux/atomic_ops.h>
#include <x10/util/concurrent/AtomicBoolean.h>

using namespace x10aux;
using namespace x10::util::concurrent;

x10_boolean atomic_boolean_funs::compareAndSet(AtomicBoolean *obj,
                                               x10_boolean expect, x10_boolean update) {
    x10_int expectI = expect ? 1 : 0;
    x10_int updateI = update ? 1 : 0;
    x10_int oldVal = atomic_ops::compareAndSet_32(&(obj->FMGL(value)), expectI, updateI) == expectI;
    return oldVal == 1;
}
                    
x10_boolean atomic_boolean_funs::weakCompareAndSet(AtomicBoolean *obj,
                                                   x10_boolean expect, x10_boolean update) {
    // TODO: for minor optimization on ppc we could add a weakCompareAndSet in atomic_ops and use that here
    x10_int expectI = expect ? 1 : 0;
    x10_int updateI = update ? 1 : 0;
    x10_int oldVal = atomic_ops::compareAndSet_32(&(obj->FMGL(value)), expectI, updateI) == expectI;
    return oldVal == 1;
}

// vim:tabstop=4:shiftwidth=4:expandtab
