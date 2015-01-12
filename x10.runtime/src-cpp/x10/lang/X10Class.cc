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

#include <x10/lang/X10Class.h>
#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

x10::lang::String* x10::lang::X10Class::typeName() {
    return x10::lang::String::Lit(_type()->name());
}

void x10::lang::X10Class::dealloc_object(X10Class *obj) {
    obj->_destructor();
    x10aux::dealloc(obj);
}

x10aux::RuntimeType x10::lang::X10Class::rtt;

void x10::lang::X10Class::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("x10.lang.X10Class", RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}

itable_entry x10::lang::X10Class::empty_itable[1] = { itable_entry(NULL,  (void*)"x10.lang.X10Class") };

// vim:tabstop=4:shiftwidth=4:expandtab
