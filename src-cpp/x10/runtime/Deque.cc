/*
 * (c) Copyright IBM Corporation 2009
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#include <x10aux/config.h>

#include <x10/runtime/Deque.h>
#include <x10/runtime/Lock.h>

#include <errno.h>
#ifdef XRX_DEBUG
#include <iostream>
#endif /* XRX_DEBUG */

using namespace x10::lang;
using namespace x10::runtime;
using namespace x10aux;

ref<Deque> Deque::_constructor() {
    queue = x10aux::alloc<ref<Object> >(INITIAL_QUEUE_CAPACITY * sizeof(ref<Object>));
    queueCapacity = INITIAL_QUEUE_CAPACITY;
    lock = Lock::_make();
    return this;
}

void Deque::_destructor() {
    x10aux::dealloc(queue);
}

void Deque::setSlot(ref<Object> *q, int i, ref<Object> t) {
    lock->lock();   // TODO: using locking instead of low-level CAS for initial debugging
    queue[i] = t;
    lock->unlock(); // TODO: using locking instead of low-level CAS for initial debugging
}

bool Deque::casSlotNull(ref<Object> *q, int i, ref<Object> t) {
    bool res;
    lock->lock();   // TODO: using locking instead of low-level CAS for initial debugging

    ref<Object> oldValue = queue[i];
    if (oldValue == t) {
        res = true;
        queue[i] = NULL;
    } else {
        res = false;
    }

    lock->unlock(); // TODO: using locking instead of low-level CAS for initial debugging

    return res;
}


void Deque::storeSp(int s) {
    lock->lock();   // TODO: using locking instead of low-level CAS for initial debugging
    sp = s;
    lock->unlock(); // TODO: using locking instead of low-level CAS for initial debugging
}

void Deque::growQueue() {
    assert(false);
}

void Deque::pushTask(x10aux::ref<x10::lang::Object> t) {
    ref<Object>* q = queue;
    int mask = queueCapacity - 1;
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
    ref<Object> *q;
    int i;
    int b;
    if (sp != (b = base) &&
        (q = queue) != null && // must read q after b
        (t = q[i = (queueCapacity - 1) & b]) != null &&
        casSlotNull(q, i, t)) {
        base = b + 1;
        return t;
    }
    return null;
}

ref<Object> Deque::popTask() {
    int s = sp;
    while (s != base) {
        ref<Object> *q = queue;
        int mask = queueCapacity - 1;
        int i = (s - 1) & mask;
        ref<Object> t = q[i];
        if (t.isNull() || !casSlotNull(q, i, t))
            break;
        storeSp(s - 1);
        return t;
    }
    return NULL;
}
                
ref<Object> Deque::peekTask() {
    ref<Object> *q = queue;
    return q == NULL ? NULL : q[(sp - 1) & (queueCapacity - 1)];
}

int Deque::getQueueSize() {
    int n = sp - base;
    return n < 0 ? 0 : n; // suppress momentarily negative values
}
            
DEFINE_RTT(Deque);

// vim:tabstop=4:shiftwidth=4:expandtab
