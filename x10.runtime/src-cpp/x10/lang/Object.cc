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
    x10aux::ref<Object> res = (new (x10aux::alloc<Object>()) Object());
    res->_constructor();
    return res;
}

x10aux::ref<x10::lang::String> x10::lang::Object::toString() {
    void *v = (void*)this; 
    return String::Lit(alloc_printf("%s@%p",this->_type()->name(),v));
}

x10aux::ref<x10::lang::String> x10::lang::Object::typeName() {
    return x10::lang::String::Lit(_type()->name());
}

const serialization_id_t Object::_serialization_id =
    DeserializationDispatcher::addDeserializer(Object::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

x10aux::ref<Reference> Object::_deserializer(x10aux::deserialization_buffer &buf) {
    x10aux::ref<Object> this_ = new (x10aux::alloc<Object>()) Object();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void Object::dealloc_object(Object* obj) {
    _M_("Attempting to dealloc object "<<(void*)obj);
    obj->_destructor();
    dealloc(obj);
}

x10aux::RuntimeType x10::lang::Object::rtt;

void x10::lang::Object::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("x10.lang.Object", RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}

itable_entry Object::_itables[1] = { itable_entry(NULL,  (void*)x10aux::getRTT<Object>()) };

// vim:tabstop=4:shiftwidth=4:expandtab
