/*
 * (c) Copyright IBM Corporation 2009
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#include <x10aux/config.h>
#include <x10aux/atomic_ops.h>

#include <x10/lang/Deque.h>

#include <errno.h>
#ifdef XRX_DEBUG
#include <iostream>
#endif /* XRX_DEBUG */

using namespace x10::lang;
using namespace x10aux;

x10aux::ref<Deque>
Deque::_make() {
    x10aux::ref<Deque> this_ = new (x10aux::alloc<Deque>()) Deque();
    this_->_constructor();
    return this_;
}

ref<Deque> Deque::_constructor() {
    this->x10::lang::Ref::_constructor();
    queue = x10aux::alloc<Slots>();
    queue->capacity = INITIAL_QUEUE_CAPACITY;
    queue->data = x10aux::alloc<volatile void*>(INITIAL_QUEUE_CAPACITY * sizeof(void*));
    memset(queue->data, 0, (INITIAL_QUEUE_CAPACITY * sizeof(void*)));
    sp = 0;
    base = 0;
    return this;
}

const serialization_id_t Deque::_serialization_id =
    DeserializationDispatcher::addDeserializer(Deque::_deserializer<Ref>);

void Deque::growQueue() {
    Slots *oldQ = queue;
    int oldSize = oldQ->capacity;
    int newSize = oldSize << 1;
    if (newSize > MAXIMUM_QUEUE_CAPACITY) {
        assert(false); /* throw new RuntimeException("Queue capacity exceeded"); */
    }
    Slots *newQ = x10aux::alloc<Slots>();
    newQ->capacity = newSize;
    newQ->data = x10aux::alloc<volatile void*>(newSize * sizeof(void*));
    memset(newQ->data, 0, (newSize * sizeof(void*)));
    queue = newQ;

    int b = base;
    int bf = b + oldSize;
    int oldMask = oldSize - 1;
    int newMask = newSize - 1;
    do {
        int oldIndex = b & oldMask;
        Object *t = (Object*)(oldQ->data[oldIndex]);
        if (t != NULL && !casSlotNull(oldQ, oldIndex, t)) {
            t = NULL;
        }
        setSlot(newQ, b & newMask, t);
    } while (++b != bf);
}

void Deque::push(x10aux::ref<x10::lang::Object> t) {
    Slots *q = queue;
    int mask = q->capacity - 1;
    int s = sp;
    setSlot(q, s & mask, t.operator->());
    storeSp(++s);
    if ((s -= base) == 1) {
        ;
    } else if (s >= mask) {
        growQueue();
    }
}

ref<Object> Deque::steal() {
    Object *t;
    Slots *q;
    int i;
    int b;
    if (sp != (b = base) &&
        (q = queue) != NULL && // must read q after b
        (t = ((Object*)q->data[i = (q->capacity - 1) & b])) != NULL &&
        casSlotNull(q, i, t)) {
        base = b + 1;
        return t;
    }
    return null;
}

ref<Object> Deque::poll() {
    int s = sp;
    while (s != base) {
        Slots *q = queue;
        int mask = q->capacity - 1;
        int i = (s - 1) & mask;
        Object *t = (Object*)(q->data[i]);
        if (t == NULL || !casSlotNull(q, i, t))
            break;
        storeSp(s - 1);
        return t;
    }
    return NULL;
}

void Deque::_serialize_body(serialization_buffer &buf) {
    this->Ref::_serialize_body(buf);
}

void Deque::_deserialize_body(deserialization_buffer& buf) {
    this->Ref::_deserialize_body(buf);
}


RTT_CC_DECLS1(Deque, "x10.lang.Deque", Ref)

// vim:tabstop=4:shiftwidth=4:expandtab
