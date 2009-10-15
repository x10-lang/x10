/*
 * (c) Copyright IBM Corporation 2009
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#include <x10/util/concurrent/atomic/AtomicBoolean.h>

using namespace x10::lang;
using namespace x10::util::concurrent::atomic;

x10aux::ref<AtomicBoolean>
AtomicBoolean::_make() {
    x10aux::ref<AtomicBoolean> this_ = new (x10aux::alloc<AtomicBoolean>()) AtomicBoolean();
    this_->_constructor(0);
    return this_;
}

x10aux::ref<AtomicBoolean>
AtomicBoolean::_make(x10_boolean val) {
    x10aux::ref<AtomicBoolean> this_ = new (x10aux::alloc<AtomicBoolean>()) AtomicBoolean();
    this_->_constructor(val);
    return this_;
}

const x10aux::serialization_id_t AtomicBoolean::_serialization_id =
    x10aux::DeserializationDispatcher::addDeserializer(AtomicBoolean::_deserializer<Object>);

RTT_CC_DECLS1(AtomicBoolean, "x10.util.concurrent.atomic.AtomicBoolean", Ref)

// vim:tabstop=4:shiftwidth=4:expandtab
