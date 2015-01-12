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

#include <x10/lang/Closure.h>
#include <x10/lang/String.h>
#include <x10/lang/Place.h>

x10_int x10::lang::Closure::hashCode() {
    return x10aux::hash_code(_get_serialization_id());
}

const char* x10::lang::Closure::toNativeString() {
    return "Closure without toNativeString defined.";
}

x10::lang::String* x10::lang::Closure::typeName() {
    return x10::lang::String::Lit(_type()->name());
}


// vim:tabstop=4:shiftwidth=4:expandtab
