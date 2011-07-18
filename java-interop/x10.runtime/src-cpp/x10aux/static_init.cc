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

ref<Reference> StaticInitBroadcastDispatcher::dispatch(deserialization_buffer &buf) {
    assert (NULL != it);
    serialization_id_t init_id = buf.read<serialization_id_t>();
    return it->create_(buf, init_id);
}

serialization_id_t const StaticInitBroadcastDispatcher::STATIC_BROADCAST_ID =
    DeserializationDispatcher::addDeserializer(StaticInitBroadcastDispatcher::dispatch, x10aux::CLOSURE_KIND_GENERAL_ASYNC);

void StaticInitBroadcastDispatcher::doBroadcast(serialization_id_t id, char* the_buf, x10_uint sz) {
    assert (the_buf != NULL);
    for (x10aux::place place = 1; place < x10aux::num_hosts ; place++) {
        x10rt_msg_params p = {place, DeserializationDispatcher::getMsgType(id), the_buf, sz, 0};
        x10rt_send_msg(&p);
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
