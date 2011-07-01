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
template<class T> T *zrealloc (T *buf, size_t was, size_t now)
{
    was *= sizeof(T);
    now *= sizeof(T);
    buf = (T*) ::realloc(buf, now);
    ::memset(((char*)buf)+was, 0, now-was);
    return buf;
}

serialization_id_t DeserializationDispatcher::addDeserializer (Deserializer deser, ClosureKind kind,
                                                               CUDAPre cuda_pre,
                                                               CUDAPost cuda_post,
                                                               const char *cubin,
                                                               const char *kernel)
{
    if (NULL == it) {
        it = new (system_alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addDeserializer_(deser, kind, cuda_pre, cuda_post, cubin, kernel);
}

static void ensure_data_size (DeserializationDispatcher::Data *&data_v,
                              size_t newsz, size_t &data_c)
{
    if (data_c >= newsz) return;
    // do not use GC
    data_v = zrealloc(data_v, data_c, newsz);
    data_c = newsz;
}

serialization_id_t DeserializationDispatcher::addDeserializer_ (Deserializer deser, ClosureKind kind,
                                                                CUDAPre cuda_pre,
                                                                CUDAPost cuda_post,
                                                                const char *cubin,
                                                                const char *kernel)
{
    // grow slowly as this is init phase and we don't want to take
    // up RAM unnecessarily
    ensure_data_size(data_v, next_id+1, data_c);
    serialization_id_t r = next_id++;
    _S_("DeserializationDispatcher "<<this<<" "<<(this==it?"(the system one) ":"")<<"registered the following handler for id: "
        <<r<<": "<<std::hex<<(size_t)deser<<std::dec<<" kind: "<<kind);
    data_v[r].deser = deser;
    data_v[r].closure_kind = kind;
    data_v[r].cuda_pre = cuda_pre;
    data_v[r].cuda_post = cuda_post;
    data_v[r].cubin = cubin;
    data_v[r].kernel = kernel;
    return r;
}

CUDAPre DeserializationDispatcher::getCUDAPre_(serialization_id_t id)
{ return data_v[id].cuda_pre; }


CUDAPost DeserializationDispatcher::getCUDAPost_(serialization_id_t id)
{ return data_v[id].cuda_post; }




serialization_id_t DeserializationDispatcher::addPutFunctions (BufferFinder bfinder,
                                                               Notifier notifier,
                                                               BufferFinder cuda_bfinder,
                                                               Notifier cuda_notifier)
{
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addPutFunctions_(bfinder, notifier, cuda_bfinder, cuda_notifier);
}

serialization_id_t DeserializationDispatcher::addPutFunctions_ (BufferFinder bfinder,
                                                                Notifier notifier,
                                                                BufferFinder cuda_bfinder,
                                                                Notifier cuda_notifier)
{
    ensure_data_size(data_v, next_id+1, data_c);
    serialization_id_t r = next_id++;
    _S_("DeserializationDispatcher registered the following put handler for id: "
        <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    data_v[r].put_bfinder = bfinder;
    data_v[r].put_notifier = notifier;
    data_v[r].cuda_put_bfinder = cuda_bfinder;
    data_v[r].cuda_put_notifier = cuda_notifier;
    data_v[r].closure_kind = x10aux::CLOSURE_KIND_SIMPLE_ASYNC;
    return r;
}

BufferFinder DeserializationDispatcher::getPutBufferFinder_ (serialization_id_t id)
{ return data_v[id].put_bfinder; }

Notifier DeserializationDispatcher::getPutNotifier_ (serialization_id_t id)
{ return data_v[id].put_notifier; }

BufferFinder DeserializationDispatcher::getCUDAPutBufferFinder_ (serialization_id_t id)
{ return data_v[id].cuda_put_bfinder; }

Notifier DeserializationDispatcher::getCUDAPutNotifier_ (serialization_id_t id)
{ return data_v[id].cuda_put_notifier; }

serialization_id_t DeserializationDispatcher::addGetFunctions (BufferFinder bfinder,
                                                               Notifier notifier,
                                                               BufferFinder cuda_bfinder,
                                                               Notifier cuda_notifier) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addGetFunctions_(bfinder, notifier, cuda_bfinder, cuda_notifier);
}

serialization_id_t DeserializationDispatcher::addGetFunctions_ (BufferFinder bfinder,
                                                                Notifier notifier,
                                                                BufferFinder cuda_bfinder,
                                                                Notifier cuda_notifier) {
    ensure_data_size(data_v, next_id+1, data_c);
    serialization_id_t r = next_id++;
    _S_("DeserializationDispatcher registered the following get handler for id: "
        <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    data_v[r].get_bfinder = bfinder;
    data_v[r].get_notifier = notifier;
    data_v[r].cuda_get_bfinder = cuda_bfinder;
    data_v[r].cuda_get_notifier = cuda_notifier;
    data_v[r].closure_kind = x10aux::CLOSURE_KIND_SIMPLE_ASYNC;
    return r;
}

BufferFinder DeserializationDispatcher::getGetBufferFinder_ (serialization_id_t id)
{ return data_v[id].get_bfinder; }

Notifier DeserializationDispatcher::getGetNotifier_ (serialization_id_t id)
{ return data_v[id].get_notifier; }

BufferFinder DeserializationDispatcher::getCUDAGetBufferFinder_ (serialization_id_t id)
{ return data_v[id].cuda_get_bfinder; }

Notifier DeserializationDispatcher::getCUDAGetNotifier_ (serialization_id_t id)
{ return data_v[id].cuda_get_notifier; }

x10aux::ClosureKind DeserializationDispatcher::getClosureKind_ (serialization_id_t id) {
    return data_v[id].closure_kind;
}

x10aux::msg_type DeserializationDispatcher::getMsgType_ (serialization_id_t id) {
    if (data_v[id].closure_kind == x10aux::CLOSURE_KIND_NOT_ASYNC) {
        fprintf(stderr, "This serialization id does not have a message id: %llu\n",
                        (unsigned long long)id);
        abort();
    }
    return data_v[id].mt;
}

serialization_id_t DeserializationDispatcher::getSerializationId_ (x10aux::msg_type id) {
    serialization_id_t sid = data_v[id].sid;
    if (data_v[sid].mt!=id) {
        fprintf(stderr, "This async id was unrecognised: %llu\n",
                        (unsigned long long)id);
        abort();
    }
    if (data_v[sid].closure_kind == x10aux::CLOSURE_KIND_NOT_ASYNC) {
        fprintf(stderr, "This async id maps to a non-async closure: %llu\n",
                        (unsigned long long)id);
        abort();
    }
    return sid;
}

void DeserializationDispatcher::registerHandlers () {
    if (NULL == it) return; // nothing registered
    it->registerHandlers_();
}

void DeserializationDispatcher::registerHandlers_ () {
    for (size_t i=0 ; i<next_id ; ++i) {
        Data &d = data_v[i];
        if (d.closure_kind != x10aux::CLOSURE_KIND_NOT_ASYNC) {
            msg_type id;
            if (d.deser!=NULL) {
                id = x10aux::register_async_handler(d.cubin, d.kernel);
                _X_("DeserializationDispatcher registered sid "<<i<<" as an async id "<<id);
                if (d.cubin) {
                    _X_("    cubin: "<<(d.cubin?d.cubin:"null"));
                }
                if (d.kernel) {
                    _X_("    kernel: "<<(d.kernel?d.kernel:"null"));
                }
            } else if (d.put_bfinder!=NULL && d.put_notifier!=NULL) {
                id = x10aux::register_put_handler();
                _X_("DeserializationDispatcher registered sid "<<i<<" as a put id "<<id); 
            } else if (d.get_bfinder!=NULL && d.get_notifier!=NULL) {
                id = x10aux::register_get_handler();
                _X_("DeserializationDispatcher registered sid "<<i<<" as a get id "<<id); 
            } else {
                continue;
            }
            d.mt = id;
            ensure_data_size(data_v, id+1, data_c);
            data_v[id].sid = i;
        }
    }
    x10aux::registration_complete();
}


ref<Reference> DeserializationDispatcher::create_(deserialization_buffer &buf, serialization_id_t id) {
    _S_("Dispatching deserialisation using id: "<<id);
    return data_v[id].deser(buf);
}

ref<Reference> DeserializationDispatcher::create_(deserialization_buffer &buf) {
    serialization_id_t id = buf.read<serialization_id_t>();
    return create_(buf, id);
}

// vim:tabstop=4:shiftwidth=4:expandtab
