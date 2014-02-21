/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#include <x10aux/config.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>
#include <x10aux/alloc.h>

#include <assert.h>

#include <stdio.h>

using namespace x10aux;
using namespace x10::lang;

DeserializationDispatcher *DeserializationDispatcher::it;

// does not use GC
template<class T> T *zrealloc (T *buf, size_t was, size_t now) {
    was *= sizeof(T);
    now *= sizeof(T);
    buf = (T*) ::realloc(buf, now);
    ::memset(((char*)buf)+was, 0, now-was);
    return buf;
}

serialization_id_t DeserializationDispatcher::addDeserializer (Deserializer deser) {
    if (NULL == it) {
        it = new (system_alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addDeserializer_(deser);
}

static void ensure_data_size (DeserializationDispatcher::Data *&data_v,
                              size_t newsz, size_t &data_c) {
    if (data_c >= newsz) return;
    // do not use GC
    data_v = zrealloc(data_v, data_c, newsz);
    data_c = newsz;
}

serialization_id_t DeserializationDispatcher::addDeserializer_ (Deserializer deser) {
    // grow slowly as this is init phase and we don't want to take
    // up RAM unnecessarily
    ensure_data_size(data_v, next_id+1, data_c);
    serialization_id_t r = next_id++;
    _S_("DeserializationDispatcher "<<this<<" "<<(this==it?"(the system one) ":"")<<"registered the following handler for id: "
        <<r<<": "<<std::hex<<(size_t)deser<<std::dec);
    data_v[r].deser = deser;
    data_v[r].sid = r;
    return r;
}

Reference* DeserializationDispatcher::create_(deserialization_buffer &buf, serialization_id_t id) {
    _S_("Dispatching deserialisation using id: "<<id);
    return data_v[id].deser(buf);
}

Reference* DeserializationDispatcher::create_(deserialization_buffer &buf) {
    serialization_id_t id = buf.read<serialization_id_t>();
    return create_(buf, id);
}

// vim:tabstop=4:shiftwidth=4:expandtab
