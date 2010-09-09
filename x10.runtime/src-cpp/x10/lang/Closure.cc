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

#include <sstream>

#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Closure.h>
#include <x10/lang/String.h>
#include <x10/lang/Place.h>

using namespace x10::lang;
using namespace x10aux;

void Closure::_serialize(x10aux::ref<Closure> this_,
                         x10aux::serialization_buffer &buf) 
{
    x10aux::serialization_id_t id = this_->_get_serialization_id();
    _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"value id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(id);
    _S_("Serializing the "<<ANSI_SER<<"value body"<<ANSI_RESET<<" to buf: "<<&buf);
    this_->_serialize_body(buf);
}           

x10_int Closure::hashCode() {
    return x10aux::hash_code(_get_serialization_id());
}

x10aux::ref<x10::lang::String> x10::lang::Closure::toString() {
    return String::Lit(this->toNativeString());
}

const char* x10::lang::Closure::toNativeString() {
    return "Closure without toNativeString defined.";
}

x10aux::ref<x10::lang::String> x10::lang::Closure::typeName() {
    return x10::lang::String::Lit(_type()->name());
}


// vim:tabstop=4:shiftwidth=4:expandtab
