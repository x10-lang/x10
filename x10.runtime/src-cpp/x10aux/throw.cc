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

#include <x10aux/throw.h>

#include <x10/lang/ArithmeticException.h>
#include <x10/lang/ClassCastException.h>
#include <x10/lang/NullPointerException.h>
#include <x10/lang/UnsupportedOperationException.h>
#include <x10/lang/String.h>
#include <x10/io/NotSerializableException.h>

using namespace x10::lang;

void x10aux::throwArithmeticException() {
    throwException(ArithmeticException::_make(String::Lit("divide by zero")));
}

void x10aux::throwNPE() {
    throwException<NullPointerException>();
}

void x10aux::throwClassCastException(const char *msg_) {
    String* msg = String::Lit(msg_);
    throwException(ClassCastException::_make(msg));
}

void x10aux::throwClassCastException(const RuntimeType *from, const RuntimeType *to) {
    String* msg;
    if (x10_native_debug_messages) {
        msg = String::Steal(x10aux::alloc_printf("tried to cast an instance of %s to a %s ", from->name(), to->name()));
    } else {
        (void) from; // match Managed X10 message format, which does not have 'from' to put in the message
        msg = String::Steal(x10aux::alloc_printf("%s", to->name()));
    }
    throwException(ClassCastException::_make(msg));
}

void x10aux::throwUnsupportedOperationException(const char *msg_) {
    String *msg = String::Lit(msg_);
    throwException(UnsupportedOperationException::_make(msg));
}

void x10aux::throwNotSerializableException(const char *msg_) {
    String *msg = String::Lit(msg_);
    throwException(x10::io::NotSerializableException::_make(msg));
}
