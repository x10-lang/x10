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

#include "atomic_ops.h"

#include <x10aux/alloc.h>


#if !defined(_LP64)
x10aux::reentrant_lock* x10aux::atomic_ops::_longOperationLock = new (alloc<reentrant_lock>())reentrant_lock();

void x10aux::atomic_ops::lock() {
    _longOperationLock->lock();
}

void x10aux::atomic_ops::unlock() {
    _longOperationLock->unlock();
}
#endif

