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

#include <x10aux/config.h>
#include <x10aux/atomic_ops.h>

#include <x10/xrx/Deque.h>

#include <errno.h>
#ifdef XRX_DEBUG
#include <iostream>
#endif /* XRX_DEBUG */

using namespace x10::xrx;
using namespace x10aux;

Deque* Deque::_make() {
    Deque* this_ = new (x10aux::alloc<Deque>()) Deque();
    this_->_constructor();
    return this_;
}

Deque* Deque::_constructor() {
    queue = x10aux::alloc<Slots>();
    queue->capacity = INITIAL_QUEUE_CAPACITY;
    queue->data = x10aux::alloc_z<volatile void*>(INITIAL_QUEUE_CAPACITY * sizeof(void*));
    sp = 0;
    base = 0;
    return this;
}

const serialization_id_t Deque::_serialization_id =
    DeserializationDispatcher::addDeserializer(Deque::_deserializer);

void Deque::growQueue() {
    Slots *oldQ = queue;
    int oldSize = oldQ->capacity;
    int newSize = oldSize << 1;
    if (newSize > MAXIMUM_QUEUE_CAPACITY) {
        UNIMPLEMENTED("Queue capacity exceeded");
    }
    Slots *newQ = x10aux::alloc<Slots>();
    newQ->capacity = newSize;
    newQ->data = x10aux::alloc_z<volatile void*>(newSize * sizeof(void*));
    queue = newQ;
    
    int b = base;
    int bf = b + oldSize;
    int oldMask = oldSize - 1;
    int newMask = newSize - 1;
    do {
        int oldIndex = b & oldMask;
        x10::lang::Any *t = (x10::lang::Any*)(oldQ->data[oldIndex]);
        if (t != NULL && !casSlotNull(oldQ, oldIndex, t)) {
            t = NULL;
        }
        setSlot(newQ, b & newMask, t);
    } while (++b != bf);

    // One might be tempted to dealloc oldQ->data and oldQ here,
    // but it would not be safe to do so.  They could still be being
    // concurrently accessed by other threads.
}

x10::lang::Any* Deque::steal() {
    x10::lang::Any *t;
    Slots *q;
    int i;
    int b;
    if (sp != (b = base) &&
        (q = queue) != NULL && // must read q after b
        (t = ((x10::lang::Any*)q->data[i = (q->capacity - 1) & b])) != NULL &&
        casSlotNull(q, i, t)) {
        base = b + 1;
        return t;
    }
    return NULL;
}

void Deque::_serialize_body(serialization_buffer &buf) {
}

void Deque::_deserialize_body(deserialization_buffer& buf) {
}

x10::lang::Reference* Deque::_deserializer(x10aux::deserialization_buffer &buf) {
    Deque* this_ = new (x10aux::alloc<Deque>()) Deque();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

RTT_CC_DECLS0(Deque, "x10.xrx.Deque", RuntimeType::class_kind)

// vim:tabstop=4:shiftwidth=4:expandtab
