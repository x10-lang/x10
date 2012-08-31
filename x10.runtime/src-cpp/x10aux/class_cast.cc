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

#include <x10aux/alloc.h>
#include <x10aux/class_cast.h>
#include <x10aux/throw.h>

#include <x10/lang/ClassCastException.h>
#include <x10/lang/String.h>

using namespace x10aux;
using namespace x10::lang;

void x10aux::throwClassCastException(const char *msg_) {
    String* msg = String::Lit(msg_);
    throwException(x10::lang::ClassCastException::_make(msg));
}

void x10aux::throwClassCastException(const RuntimeType *from, const RuntimeType *to) {
    String* msg;
    if (x10_native_debug_messages) {
        msg = String::Steal(x10aux::alloc_printf("tried to cast an instance of %s to a %s ", from->name(), to->name()));
    } else {
        (void) from; // match Managed X10 message format, which does not have 'from' to put in the message
        msg = String::Steal(x10aux::alloc_printf("%s", to->name()));
    }
    throwException(x10::lang::ClassCastException::_make(msg));
}

// vim:tabstop=4:shiftwidth=4:expandtab
