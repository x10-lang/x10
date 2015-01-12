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
#include <x10aux/network_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>
#include <x10aux/alloc.h>

#include <assert.h>

#include <stdio.h>

using namespace x10aux;
using namespace x10::lang;

NetworkDispatcher *NetworkDispatcher::it;

serialization_id_t NetworkDispatcher::addNetworkDeserializer (Deserializer deser,
                                                              ClosureKind kind,
                                                              CUDAPre cuda_pre,
                                                              CUDAPost cuda_post,
                                                              const char *cubin,
                                                              const char *kernel) {
    if (NULL == it) {
        it = new (system_alloc<NetworkDispatcher>()) NetworkDispatcher();
    }
    return it->addNetworkDeserializer_(deser, kind, cuda_pre, cuda_post, cubin, kernel);
}

serialization_id_t NetworkDispatcher::addNetworkDeserializer_ (Deserializer deser, ClosureKind kind,
                                                               CUDAPre cuda_pre,
                                                               CUDAPost cuda_post,
                                                               const char *cubin,
                                                               const char *kernel)
{
    data.ensure_capacity(next_id);
    serialization_id_t r = next_id++;
    _S_("NetworkDispatcher registered the following handler for id: "
        <<r<<": "<<std::hex<<(size_t)deser<<std::dec<<" kind: "<<kind);
    data[r].async.deser = deser;
    data[r].async.cuda_pre = cuda_pre;
    data[r].async.cuda_post = cuda_post;
    data[r].async.cubin = cubin;
    data[r].async.kernel = kernel;
    data[r].async.closure_kind = kind;
    data[r].tag = ASYNC;
    return r;
}

CUDAPre NetworkDispatcher::getCUDAPre_(serialization_id_t id){
    assert(data[id].tag == ASYNC);
    return data[id].async.cuda_pre;
}

CUDAPost NetworkDispatcher::getCUDAPost_(serialization_id_t id) {
    assert(data[id].tag == ASYNC);
    return data[id].async.cuda_post;
}

x10aux::ClosureKind NetworkDispatcher::getClosureKind_ (serialization_id_t id) {
    assert(data[id].tag == ASYNC);
    return data[id].async.closure_kind;
}

serialization_id_t NetworkDispatcher::addPutFunctions (BufferFinder bfinder,
                                                       Notifier notifier,
                                                       BufferFinder cuda_bfinder,
                                                       Notifier cuda_notifier) {
    if (NULL == it) {
        it = new (alloc<NetworkDispatcher>()) NetworkDispatcher();
    }
    return it->addPutFunctions_(bfinder, notifier, cuda_bfinder, cuda_notifier);
}

serialization_id_t NetworkDispatcher::addPutFunctions_ (BufferFinder bfinder,
                                                        Notifier notifier,
                                                        BufferFinder cuda_bfinder,
                                                        Notifier cuda_notifier) {
    data.ensure_capacity(next_id);
    serialization_id_t r = next_id++;
    _S_("NetworkDispatcher registered the following put handler for id: "
        <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    data[r].put.put_bfinder = bfinder;
    data[r].put.put_notifier = notifier;
    data[r].put.cuda_put_bfinder = cuda_bfinder;
    data[r].put.cuda_put_notifier = cuda_notifier;
    data[r].tag = PUT;
    return r;
}

BufferFinder NetworkDispatcher::getPutBufferFinder_ (serialization_id_t id) {
    assert(data[id].tag == PUT);
    return data[id].put.put_bfinder;
}

Notifier NetworkDispatcher::getPutNotifier_ (serialization_id_t id) {
    assert(data[id].tag == PUT);
    return data[id].put.put_notifier;
}

BufferFinder NetworkDispatcher::getCUDAPutBufferFinder_ (serialization_id_t id) {
    assert(data[id].tag == PUT);
    return data[id].put.cuda_put_bfinder;
}

Notifier NetworkDispatcher::getCUDAPutNotifier_ (serialization_id_t id) {
    assert(data[id].tag == PUT);
    return data[id].put.cuda_put_notifier;
}

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
    data.ensure_capacity(next_id);
    serialization_id_t r = next_id++;
    _S_("NetworkDispatcher registered the following get handler for id: "
        <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    data[r].get.get_bfinder = bfinder;
    data[r].get.get_notifier = notifier;
    data[r].get.cuda_get_bfinder = cuda_bfinder;
    data[r].get.cuda_get_notifier = cuda_notifier;
    data[r].tag = GET;
    return r;
}

BufferFinder NetworkDispatcher::getGetBufferFinder_ (serialization_id_t id) {
    assert(data[id].tag == GET);
    return data[id].get.get_bfinder;
}

Notifier NetworkDispatcher::getGetNotifier_ (serialization_id_t id) {
    assert(data[id].tag == GET);
    return data[id].get.get_notifier;
}

BufferFinder NetworkDispatcher::getCUDAGetBufferFinder_ (serialization_id_t id) {
    assert(data[id].tag == GET);
    return data[id].get.cuda_get_bfinder;
}

Notifier NetworkDispatcher::getCUDAGetNotifier_ (serialization_id_t id) {
    assert(data[id].tag == GET);
    return data[id].get.cuda_get_notifier;
}

x10aux::msg_type NetworkDispatcher::getMsgType_ (serialization_id_t id) {
    assert(data[id].tag != INVALID);
    return data[id].mt;
}

serialization_id_t NetworkDispatcher::getNetworkId_ (x10aux::msg_type id) {
    serialization_id_t nid = mt_to_nid[id];
    assert(data[nid].tag != INVALID);
    assert(data[nid].mt == id);
    return nid;
}

void NetworkDispatcher::registerHandlers () {
    if (NULL == it) return; // nothing registered
    it->registerHandlers_();
}

void NetworkDispatcher::registerHandlers_ () {
    for (size_t i=0 ; i<next_id ; ++i) {
        Data &d = data[i];
        msg_type id;
        switch (d.tag) {
            case INVALID:
                continue; // unused entry; ignore
            case ASYNC:
                id = x10aux::register_async_handler(d.async.cubin, d.async.kernel);
                _X_("NetworkDispatcher registered nid "<<i<<" as an async id "<<id);
                if (d.async.cubin) {
                    _X_("    cubin: "<<(d.async.cubin?d.async.cubin:"null"));
                }
                if (d.async.kernel) {
                    _X_("    kernel: "<<(d.async.kernel?d.async.kernel:"null"));
                }
                break;
            case PUT:
                id = x10aux::register_put_handler();
                _X_("NetworkDispatcher registered nid "<<i<<" as a put id "<<id);
                break;
            case GET:
                id = x10aux::register_get_handler();
                _X_("NetworkDispatcher registered nid "<<i<<" as a get id "<<id);
                break;
            default:
                fprintf(stderr, "Ingnoring unknown tag in registerHandlers %d\n", d.tag);
                continue;
                break;
        }
        d.mt = id;
        mt_to_nid.ensure_capacity(id);
        mt_to_nid[id] = i;
    }
    x10aux::registration_complete();
}


Reference* NetworkDispatcher::create_(deserialization_buffer &buf, serialization_id_t id) {
    _S_("Dispatching network deserialisation using nid: "<<id);
    assert(data[id].tag == ASYNC);
    return data[id].async.deser(buf);
}

// vim:tabstop=4:shiftwidth=4:expandtab
