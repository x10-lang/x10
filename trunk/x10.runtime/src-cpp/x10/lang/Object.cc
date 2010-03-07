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

#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Object.h>
#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

x10aux::ref<Object> Object::_make() {
    return (new (x10aux::alloc<Object>()) Object())->_constructor();
}

x10_int x10::lang::Object::hashCode() {
    // STEP 1: Figure out the address to use as for the object.
    void *v;
    if (this->location == x10aux::here) {
        v = (void*)this;
    } else {
        v = (void*)x10aux::get_remote_ref(this);
    }

    // STEP 2: Combine the bits of the pointer into a 32 bit integer.
    //         Note: intentionally not doing some type-punning pointer thing here as
    //         the behavior of that is somewhat underdefined and tends to expose
    //         "interesting" behavior in C++ compilers (especially at high optimization level).
    uint64_t v2 = (uint64_t)v;
    x10_int lower = (x10_int)(v2 & 0xffffffff);
    x10_int upper = (x10_int)(v2 >> 32);
    x10_int hc = lower ^ upper;
    return hc;
}

x10aux::ref<x10::lang::String> x10::lang::Object::toString() {
    return String::Lit(alloc_printf("%s@%p",this->_type()->name(),(void*)this));
}

x10aux::ref<x10::lang::String> x10::lang::Object::typeName() {
    return x10::lang::String::Lit(_type()->name());
}

const serialization_id_t Object::_serialization_id =
    DeserializationDispatcher::addDeserializer(Object::_deserializer<Object>);

void Object::_serialize(ref<Object> this_, serialization_buffer &buf)
{
    serialization_id_t id = this_.isNull() ? 0 : this_->_get_serialization_id();
    _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"class id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(id);
    // FIXME: maybe optimize nulls by moving the call below into the conditional?
    _serialize_reference(this_, buf);
    if (!this_.isNull()) {
        _S_("Serializing the "<<ANSI_SER<<"class body"<<ANSI_RESET<<" to buf: "<<&buf);
        this_->_serialize_body(buf);
    }
}

const serialization_id_t Object::_interface_serialization_id =
    DeserializationDispatcher::addDeserializer(Object::_deserialize<Object>);

void Object::_serialize_interface(serialization_buffer &buf)
{
    _serialize(this, buf);
}

void Object::_serialize_reference(ref<Object> this_, serialization_buffer &buf)
{
    bool isNull = this_.isNull();
    x10_int loc = isNull ? 0 : this_->location;
    buf.write(loc);
    if (isNull) {
        _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" to buf: "<<&buf);
        buf.write((x10_addr_t)0);
    } else if (loc == x10aux::here) {
        _S_("Serialising a "<<ANSI_SER<<ANSI_BOLD<<"local Object"<<ANSI_RESET<<
                " object of type "<<this_->_type()->name());
        buf.write((x10_addr_t)(size_t)this_.operator->());
    } else {
        _S_("Serialising a "<<ANSI_SER<<ANSI_BOLD<<"remote Object"<<ANSI_RESET<<
                " object of type "<<this_->_type()->name()<<" (loc="<<loc<<")");
        x10_addr_t tmp = get_remote_ref(this_.operator->());
        buf.write(tmp);
    }
}

void Object::dealloc_object(Object* obj) {
    _M_("Attempting to dealloc object "<<(void*)obj<<", location="<<obj->location);
    obj->_destructor();
    if (obj->location == x10aux::here)
        dealloc(obj);
    else
        dealloc_remote(obj);
}

x10aux::RuntimeType x10::lang::Object::rtt;

void x10::lang::Object::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    rtt.initStageTwo("x10.lang.Object", 0, NULL, 0, NULL, NULL);
}

itable_entry Object::_itables[1] = { itable_entry(NULL,  (void*)x10aux::getRTT<Object>()) };

// vim:tabstop=4:shiftwidth=4:expandtab
