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

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/alloc.h>
#include <x10aux/itables.h>

#include <x10/lang/Reference.h>
#include <x10/lang/String.h>

x10aux::RuntimeType x10::lang::NullType::rtt;

void x10::lang::NullType::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    rtt.initStageTwo("Null", x10aux::RuntimeType::class_kind, 0, NULL, 0, NULL, NULL);
}

void x10::lang::Reference::dealloc_object(Reference *obj) {
    _M_("Attempting to dealloc object "<<(void*)obj);
    obj->_destructor();
    x10aux::dealloc(obj);
}

x10aux::ref<x10::lang::String> x10::lang::Reference::typeName() {
    return x10::lang::String::Lit("Default typeName().  If you see this, it is a bug.");
}

// NULL won't match any TypeName -- seems appropriate for what is (essentailly) an empty interface
x10aux::itable_entry x10::lang::Reference::empty_itable[1] = { x10aux::itable_entry(NULL,  NULL) };
// vim:tabstop=4:shiftwidth=4:expandtab
