/*
 * (c) Copyright IBM Corporation 2009
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#include <x10aux/config.h>
#include <x10aux/atomic_ops.h>

#include <x10/runtime/Deque.h>

#include <errno.h>
#ifdef XRX_DEBUG
#include <iostream>
#endif /* XRX_DEBUG */

using namespace x10::lang;
using namespace x10::runtime;
using namespace x10aux;

ref<Deque> Deque::_constructor() {
    queue = x10aux::alloc<Slots>(sizeof(Slots) + (INITIAL_QUEUE_CAPACITY * sizeof(ref<Object>)));
    memset(queue->data, 0, (INITIAL_QUEUE_CAPACITY * sizeof(ref<Object>)));
    queue->capacity = INITIAL_QUEUE_CAPACITY;
    return this;
}

void Deque::_destructor() {
    x10aux::dealloc(queue);
}

void Deque::setSlot(Slots *q, int i, ref<Object> t) {
    q->data[i] = t;
    atomic_ops::store_store_barrier();
}

bool Deque::casSlotNull(Slots *q, int i, ref<Object> t) {
    void *unwrapped = (void*)t.get();
    void *oldValue = atomic_ops::compareAndSet_ptr((volatile void**)&(q->data[i]), unwrapped, NULL);
    return oldValue == unwrapped;
}

void Deque::storeSp(int s) {
    sp = s;
    atomic_ops::store_store_barrier();
}

void Deque::growQueue() {
    assert(false);
}

void Deque::pushTask(x10aux::ref<x10::lang::Object> t) {
    Slots *q = queue;
    int mask = q->capacity - 1;
    int s = sp;
    setSlot(q, s & mask, t);
    storeSp(++s);
    if ((s -= base) == 1) {
        ;
    } else if (s >= mask) {
        growQueue();
    }
}

ref<Object> Deque::deqTask() {
    ref<Object> t;
    Slots *q;
    int i;
    int b;
    if (sp != (b = base) &&
        (q = queue) != null && // must read q after b
        (t = q->data[i = (q->capacity - 1) & b]) != null &&
        casSlotNull(q, i, t)) {
        base = b + 1;
        return t;
    }
    return null;
}

ref<Object> Deque::popTask() {
    int s = sp;
    while (s != base) {
        Slots *q = queue;
        int mask = q->capacity - 1;
        int i = (s - 1) & mask;
        ref<Object> t = q->data[i];
        if (t.isNull() || !casSlotNull(q, i, t))
            break;
        storeSp(s - 1);
        return t;
    }
    return NULL;
}
                
ref<Object> Deque::peekTask() {
    Slots *q = queue;
    return q == NULL ? NULL : q->data[(sp - 1) & (q->capacity - 1)];
}

int Deque::getQueueSize() {
    int n = sp - base;
    return n < 0 ? 0 : n; // suppress momentarily negative values
}
            
DEFINE_RTT(Deque);

// vim:tabstop=4:shiftwidth=4:expandtab
