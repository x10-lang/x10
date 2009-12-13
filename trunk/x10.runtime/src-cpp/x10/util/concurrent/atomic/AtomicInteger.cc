/*
 * (c) Copyright IBM Corporation 2009
 *
 * This file is part of XRX/C++ native layer implementation.
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
    this->Ref::_serialize_body(buf);
}

void AtomicInteger::_deserialize_body(x10aux::deserialization_buffer& buf) {
    this->Ref::_deserialize_body(buf);
}

const x10aux::serialization_id_t AtomicInteger::_serialization_id =
    x10aux::DeserializationDispatcher::addDeserializer(AtomicInteger::_deserializer<Ref>);

RTT_CC_DECLS1(AtomicInteger, "x10.util.concurrent.atomic.AtomicInteger", Ref)

// vim:tabstop=4:shiftwidth=4:expandtab
