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

#include <x10aux/config.h>
#include <x10aux/throw.h>

#include <x10/lang/Lock__ReentrantLock.h>

#include <x10/lang/IllegalOperationException.h>

#include <errno.h>
#ifdef XRX_DEBUG
#include <iostream>
#endif /* XRX_DEBUG */

using namespace x10::lang;
using namespace x10aux;

Lock__ReentrantLock* Lock__ReentrantLock::_make() {
    Lock__ReentrantLock* this_ = new (x10aux::alloc<Lock__ReentrantLock>()) Lock__ReentrantLock();
    return this_;
}

void
Lock__ReentrantLock::raiseException() {
    throwException<IllegalOperationException>();
}

RTT_CC_DECLS0(Lock__ReentrantLock, "x10.lang.Lock__ReentrantLock", RuntimeType::class_kind)

// vim:tabstop=4:shiftwidth=4:expandtab
