/*
 * (c) Copyright IBM Corporation 2009
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#include <x10/util/concurrent/atomic/AtomicLong.h>

using namespace x10::lang;
using namespace x10::util::concurrent::atomic;

x10aux::ref<AtomicLong>
AtomicLong::_make() {
    x10aux::ref<AtomicLong> this_ = new (x10aux::alloc<AtomicLong>()) AtomicLong();
    this_->_constructor(0);
    return this_;
}

x10aux::ref<AtomicLong>
AtomicLong::_make(x10_long val) {
    x10aux::ref<AtomicLong> this_ = new (x10aux::alloc<AtomicLong>()) AtomicLong();
    this_->_constructor(val);
    return this_;
}

const x10aux::serialization_id_t AtomicLong::_serialization_id =
    x10aux::DeserializationDispatcher::addDeserializer(AtomicLong::_deserializer<Object>);

RTT_CC_DECLS1(AtomicLong, "x10.util.concurrent.atomic.AtomicLong", Ref)

// vim:tabstop=4:shiftwidth=4:expandtab
