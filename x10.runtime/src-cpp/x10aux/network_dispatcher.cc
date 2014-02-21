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
#include <x10aux/network_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>
#include <x10aux/alloc.h>

#include <assert.h>

#include <stdio.h>

using namespace x10aux;
using namespace x10::lang;

NetworkDispatcher *NetworkDispatcher::it;

// does not use GC
template<class T> T *zrealloc (T *buf, size_t was, size_t now)
{
    was *= sizeof(T);
    now *= sizeof(T);
    buf = (T*) ::realloc(buf, now);
    ::memset(((char*)buf)+was, 0, now-was);
    return buf;
}

serialization_id_t NetworkDispatcher::addNetworkDeserializer (Deserializer deser, ClosureKind kind,
                                                              CUDAPre cuda_pre,
                                                              CUDAPost cuda_post,
                                                              const char *cubin,
                                                              const char *kernel)
{
    if (NULL == it) {
        it = new (system_alloc<NetworkDispatcher>()) NetworkDispatcher();
    }
    return it->addNetworkDeserializer_(deser, kind, cuda_pre, cuda_post, cubin, kernel);
}

static void ensure_data_size (NetworkDispatcher::Data *&data_v,
                              size_t newsz, size_t &data_c)
{
    if (data_c >= newsz) return;
    // do not use GC
    data_v = zrealloc(data_v, data_c, newsz);
    data_c = newsz;
}

serialization_id_t NetworkDispatcher::addNetworkDeserializer_ (Deserializer deser, ClosureKind kind,
                                                               CUDAPre cuda_pre,
                                                               CUDAPost cuda_post,
                                                               const char *cubin,
                                                               const char *kernel)
{
    // grow slowly as this is init phase and we don't want to take
    // up RAM unnecessarily
    ensure_data_size(data_v, next_id+1, data_c);
    serialization_id_t r = next_id++;
    _S_("NetworkDispatcher "<<this<<" "<<(this==it?"(the system one) ":"")<<"registered the following handler for id: "
        <<r<<": "<<std::hex<<(size_t)deser<<std::dec<<" kind: "<<kind);
    data_v[r].deser = deser;
    data_v[r].closure_kind = kind;
    data_v[r].cuda_pre = cuda_pre;
    data_v[r].cuda_post = cuda_post;
    data_v[r].cubin = cubin;
    data_v[r].kernel = kernel;
    data_v[r].nid = r;
    return r;
}

CUDAPre NetworkDispatcher::getCUDAPre_(serialization_id_t id)
{ return data_v[id].cuda_pre; }


CUDAPost NetworkDispatcher::getCUDAPost_(serialization_id_t id)
{ return data_v[id].cuda_post; }




serialization_id_t NetworkDispatcher::addPutFunctions (BufferFinder bfinder,
                                                       Notifier notifier,
                                                       BufferFinder cuda_bfinder,
                                                       Notifier cuda_notifier)
{
    if (NULL == it) {
        it = new (alloc<NetworkDispatcher>()) NetworkDispatcher();
    }
    return it->addPutFunctions_(bfinder, notifier, cuda_bfinder, cuda_notifier);
}

serialization_id_t NetworkDispatcher::addPutFunctions_ (BufferFinder bfinder,
                                                        Notifier notifier,
                                                        BufferFinder cuda_bfinder,
                                                        Notifier cuda_notifier)
{
    ensure_data_size(data_v, next_id+1, data_c);
    serialization_id_t r = next_id++;
    _S_("NetworkDispatcher registered the following put handler for id: "
        <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    data_v[r].put_bfinder = bfinder;
    data_v[r].put_notifier = notifier;
    data_v[r].cuda_put_bfinder = cuda_bfinder;
    data_v[r].cuda_put_notifier = cuda_notifier;
    data_v[r].closure_kind = x10aux::CLOSURE_KIND_SIMPLE_ASYNC;
    return r;
}

BufferFinder NetworkDispatcher::getPutBufferFinder_ (serialization_id_t id)
{ return data_v[id].put_bfinder; }

Notifier NetworkDispatcher::getPutNotifier_ (serialization_id_t id)
{ return data_v[id].put_notifier; }

BufferFinder NetworkDispatcher::getCUDAPutBufferFinder_ (serialization_id_t id)
{ return data_v[id].cuda_put_bfinder; }

Notifier NetworkDispatcher::getCUDAPutNotifier_ (serialization_id_t id)
{ return data_v[id].cuda_put_notifier; }

serialization_id_t NetworkDispatcher::addGetFunctions (BufferFinder bfinder,
                                                       Notifier notifier,
                                                       BufferFinder cuda_bfinder,
                                                       Notifier cuda_notifier) {
    if (NULL == it) {
        it = new (alloc<NetworkDispatcher>()) NetworkDispatcher();
    }
    return it->addGetFunctions_(bfinder, notifier, cuda_bfinder, cuda_notifier);
}

serialization_id_t NetworkDispatcher::addGetFunctions_ (BufferFinder bfinder,
                                                        Notifier notifier,
                                                        BufferFinder cuda_bfinder,
                                                        Notifier cuda_notifier) {
    ensure_data_size(data_v, next_id+1, data_c);
    serialization_id_t r = next_id++;
    _S_("NetworkDispatcher registered the following get handler for id: "
        <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    data_v[r].get_bfinder = bfinder;
    data_v[r].get_notifier = notifier;
    data_v[r].cuda_get_bfinder = cuda_bfinder;
    data_v[r].cuda_get_notifier = cuda_notifier;
    data_v[r].closure_kind = x10aux::CLOSURE_KIND_SIMPLE_ASYNC;
    return r;
}

BufferFinder NetworkDispatcher::getGetBufferFinder_ (serialization_id_t id)
{ return data_v[id].get_bfinder; }

Notifier NetworkDispatcher::getGetNotifier_ (serialization_id_t id)
{ return data_v[id].get_notifier; }

BufferFinder NetworkDispatcher::getCUDAGetBufferFinder_ (serialization_id_t id)
{ return data_v[id].cuda_get_bfinder; }

Notifier NetworkDispatcher::getCUDAGetNotifier_ (serialization_id_t id)
{ return data_v[id].cuda_get_notifier; }

x10aux::ClosureKind NetworkDispatcher::getClosureKind_ (serialization_id_t id) {
    return data_v[id].closure_kind;
}

x10aux::msg_type NetworkDispatcher::getMsgType_ (serialization_id_t id) {
    if (data_v[id].closure_kind == x10aux::CLOSURE_KIND_NOT_ASYNC) {
        fprintf(stderr, "This serialization id does not have a message id: %llu\n",
                        (unsigned long long)id);
        abort();
    }
    return data_v[id].mt;
}

serialization_id_t NetworkDispatcher::getNetworkId_ (x10aux::msg_type id) {
    serialization_id_t nid = data_v[id].nid;
    if (data_v[nid].mt!=id) {
        fprintf(stderr, "This async id was unrecognised: %llu\n",
                        (unsigned long long)id);
        abort();
    }
    if (data_v[nid].closure_kind == x10aux::CLOSURE_KIND_NOT_ASYNC) {
        fprintf(stderr, "This async id maps to a non-async closure: %llu\n",
                        (unsigned long long)id);
        abort();
    }
    return nid;
}

void NetworkDispatcher::registerHandlers () {
    if (NULL == it) return; // nothing registered
    it->registerHandlers_();
}

void NetworkDispatcher::registerHandlers_ () {
    for (size_t i=0 ; i<next_id ; ++i) {
        Data &d = data_v[i];
        if (d.closure_kind != x10aux::CLOSURE_KIND_NOT_ASYNC) {
            msg_type id;
            if (d.deser!=NULL) {
                id = x10aux::register_async_handler(d.cubin, d.kernel);
                _X_("NetworkDispatcher registered nid "<<i<<" as an async id "<<id);
                if (d.cubin) {
                    _X_("    cubin: "<<(d.cubin?d.cubin:"null"));
                }
                if (d.kernel) {
                    _X_("    kernel: "<<(d.kernel?d.kernel:"null"));
                }
            } else if (d.put_bfinder!=NULL && d.put_notifier!=NULL) {
                id = x10aux::register_put_handler();
                _X_("NetworkDispatcher registered nid "<<i<<" as a put id "<<id); 
            } else if (d.get_bfinder!=NULL && d.get_notifier!=NULL) {
                id = x10aux::register_get_handler();
                _X_("NetworkDispatcher registered nid "<<i<<" as a get id "<<id); 
            } else {
                continue;
            }
            d.mt = id;
            ensure_data_size(data_v, id+1, data_c);
            data_v[id].nid = i;
        }
    }
    x10aux::registration_complete();
}


Reference* NetworkDispatcher::create_(deserialization_buffer &buf, serialization_id_t id) {
    _S_("Dispatching network deserialisation using nid: "<<id);
    return data_v[id].deser(buf);
}

// vim:tabstop=4:shiftwidth=4:expandtab
