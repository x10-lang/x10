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
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>
#include <x10aux/alloc.h>

#include <assert.h>

#include <stdio.h>

using namespace x10aux;

DeserializationDispatcher *DeserializationDispatcher::it;

serialization_id_t DeserializationDispatcher::addDeserializer (Deserializer deser) {
    if (NULL == it) {
        it = new (system_alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addDeserializer_(deser);
}

serialization_id_t DeserializationDispatcher::addDeserializer_ (Deserializer deser) {
    serialization_id_t sid = next_id++;
    _S_("DeserializationDispatcher registered id: "<<sid<<" to handler: "<<std::hex<<(size_t)deser<<std::dec);
    data.ensure_capacity(sid);
    data[sid] = deser;
    return sid;
}

x10::lang::Reference* DeserializationDispatcher::create_(deserialization_buffer &buf, serialization_id_t id) {
    _S_("Dispatching deserialisation using id: "<<id);
    return data[id](buf);
}

// vim:tabstop=4:shiftwidth=4:expandtab
