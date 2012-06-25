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

#include <x10aux/config.h>
#include <x10aux/static_init.h>
#include <x10aux/alloc.h>
#include <x10aux/network.h>

#include <x10/lang/Runtime.h>

#include <assert.h>
#include <time.h>

using namespace x10aux;
using namespace x10::lang;

DeserializationDispatcher *StaticInitBroadcastDispatcher::it;

serialization_id_t StaticInitBroadcastDispatcher::addRoutine(Deserializer init) {
    if (NULL == it) {
        it = new (system_alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addDeserializer_(init, x10aux::CLOSURE_KIND_GENERAL_ASYNC);
}



serialization_id_t const StaticInitBroadcastDispatcher::STATIC_BROADCAST_NAIVE_ID =
    DeserializationDispatcher::addDeserializer(StaticInitBroadcastDispatcher::dispatchNaive, x10aux::CLOSURE_KIND_GENERAL_ASYNC);

ref<Reference> StaticInitBroadcastDispatcher::dispatchNaive(deserialization_buffer &buf) {
    assert (NULL != it);
    serialization_id_t init_id = buf.read<serialization_id_t>();
    return it->create_(buf, init_id);
}

void StaticInitBroadcastDispatcher::doBroadcastNaive(char* the_buf, x10_uint sz) {
    assert (the_buf != NULL);
    x10aux::msg_type msg_id = DeserializationDispatcher::getMsgType(STATIC_BROADCAST_NAIVE_ID);
    for (x10aux::place place = 1; place < x10aux::num_hosts ; place++) {
        x10rt_msg_params p = {place, msg_id, the_buf, sz, 0};
        x10rt_send_msg(&p);
    }
}


// functions that define the shape of a balanced binary tree

// return role that acts as parent to a given role r
// currently not needed as we do not do termination detection for static field broadcasts
//static x10aux::place get_parent (x10aux::place r)
//{
//    return  (long(r) - 1)/2;
//}

// given role r and size sz, provide the number of children under r and their identities
static x10aux::place get_children (x10aux::place r, x10aux::place sz,
                                 x10aux::place &left, x10aux::place &right)
{
    assert(r<sz);
    left = r*2 + 1;
    right = r*2 + 2;
    return x10rt_place(left<sz) + x10rt_place(right<sz);
}

serialization_id_t const StaticInitBroadcastDispatcher::STATIC_BROADCAST_TREE_ID =
    DeserializationDispatcher::addDeserializer(StaticInitBroadcastDispatcher::dispatchTree, x10aux::CLOSURE_KIND_GENERAL_ASYNC);

ref<Reference> StaticInitBroadcastDispatcher::dispatchTree(deserialization_buffer &buf) {
    assert (NULL != it);

    // recurse as appropriate
    doBroadcastTree(buf.getBuffer(), static_cast<x10_uint>(buf.getLen()));

    serialization_id_t init_id = buf.read<serialization_id_t>();
    return it->create_(buf, init_id);
}

void StaticInitBroadcastDispatcher::doBroadcastTree(char* the_buf, x10_uint sz) {
    assert (the_buf != NULL);
    // send to 2 children
    x10aux::place child1, child2;
    x10aux::place num_children = get_children(x10aux::here, x10aux::num_places, child1, child2);
    x10aux::msg_type msg_id = DeserializationDispatcher::getMsgType(STATIC_BROADCAST_TREE_ID);
    switch (num_children) {
        case 2: {
            x10rt_msg_params p = {child2, msg_id, the_buf, sz, 0};
            x10rt_send_msg(&p);
        }
        case 1: {
            x10rt_msg_params p = {child1, msg_id, the_buf, sz, 0};
            x10rt_send_msg(&p);
        }
        case 0: {
        }
    }
}



void StaticInitBroadcastDispatcher::lock() {
    x10::lang::Runtime::StaticInitBroadcastDispatcherLock();
}

void StaticInitBroadcastDispatcher::await() {
    x10::lang::Runtime::StaticInitBroadcastDispatcherAwait();
}

void StaticInitBroadcastDispatcher::unlock() {
    x10::lang::Runtime::StaticInitBroadcastDispatcherUnlock();
}

void StaticInitBroadcastDispatcher::notify() {
    x10::lang::Runtime::StaticInitBroadcastDispatcherNotify();
}

// vim:tabstop=4:shiftwidth=4:expandtab
