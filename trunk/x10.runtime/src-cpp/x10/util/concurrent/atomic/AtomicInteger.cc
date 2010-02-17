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

#include <x10/util/concurrent/atomic/AtomicInteger.h>

using namespace x10::lang;
using namespace x10::util::concurrent::atomic;

x10aux::ref<AtomicInteger>
AtomicInteger::_make() {
    x10aux::ref<AtomicInteger> this_ = new (x10aux::alloc<AtomicInteger>()) AtomicInteger();
    this_->_constructor(0);
    return this_;
}

x10aux::ref<AtomicInteger>
AtomicInteger::_make(x10_int val) {
    x10aux::ref<AtomicInteger> this_ = new (x10aux::alloc<AtomicInteger>()) AtomicInteger();
    this_->_constructor(val);
    return this_;
}

void AtomicInteger::_serialize_body(x10aux::serialization_buffer &buf) {
    this->Object::_serialize_body(buf);
}

void AtomicInteger::_deserialize_body(x10aux::deserialization_buffer& buf) {
    this->Object::_deserialize_body(buf);
}

const x10aux::serialization_id_t AtomicInteger::_serialization_id =
    x10aux::DeserializationDispatcher::addDeserializer(AtomicInteger::_deserializer<Object>);

RTT_CC_DECLS1(AtomicInteger, "x10.util.concurrent.atomic.AtomicInteger", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
