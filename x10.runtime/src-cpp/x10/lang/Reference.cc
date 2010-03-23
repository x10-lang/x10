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

#include <x10/lang/Reference.h>
#include <x10/lang/Object.h>
#include <x10/lang/Place.h>

using namespace x10::lang;
using namespace x10aux;


x10_boolean Reference::at(x10aux::ref<x10::lang::Object> o) {
    return location == o->location;
}

Place Reference::home() {
    return x10::lang::Place_methods::_make(location);
}

x10aux::serialization_id_t Reference::_get_interface_serialization_id() {
    _S_("===> Reference's _get_interface_serialization_id() called");
    return _get_serialization_id();
}

void Reference::_serialize_interface(x10aux::serialization_buffer &buf)
{
    _S_("Serializing the "<<ANSI_SER<<"interface body"<<ANSI_RESET<<" to buf: "<<&buf);
    this->_serialize_body(buf);
}

void Reference::_serialize(x10aux::ref<Reference> this_,
                        x10aux::serialization_buffer &buf)
{
    bool isNull = this_.isNull();
    x10aux::serialization_id_t id = isNull ? 0 : this_->_get_interface_serialization_id();
    _S_("Serializing an "<<ANSI_SER<<ANSI_BOLD<<"interface id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(id);
    if (!isNull) {
        this_->_serialize_interface(buf);
    }
}

x10aux::RuntimeType x10::lang::NullType::rtt;

void x10::lang::NullType::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    rtt.initStageTwo("Null", 0, NULL, 0, NULL, NULL);
}

// vim:tabstop=4:shiftwidth=4:expandtab
