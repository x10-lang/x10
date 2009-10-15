#include <x10aux/config.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>
#include <x10aux/alloc.h>

#include <assert.h>

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

serialization_id_t DeserializationDispatcher::addDeserializer (Deserializer deser, bool is_async) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addDeserializer_(deser, is_async);
}

serialization_id_t DeserializationDispatcher::addDeserializer_ (Deserializer deser, bool is_async) {
    if (deser_sz<=(size_t)next_id) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = next_id+1;
        
        // do not use GC
        deser_v = zrealloc(deser_v, deser_sz, newsz);
        deser_sz = newsz;
    }
    deser_v[next_id] = deser;
    serialization_id_t r = next_id++;
    //_S_("DeserializationDispatcher registered the following handler for id: "
    //    <<r<<": "<<std::hex<<(size_t)deser<<std::dec);
    (void) is_async; // TODO: use two numbering schemes
    return r;
}

serialization_id_t DeserializationDispatcher::addPutFunctions (BufferFinder bfinder,
                                                               Notifier notifier) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addPutFunctions_(bfinder, notifier);
}

serialization_id_t DeserializationDispatcher::addPutFunctions_ (BufferFinder bfinder,
                                                                Notifier notifier) {
    if (put_sz<=(size_t)next_id) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = next_id+1;
        // do not use GC
        put_bfinder_v = zrealloc(put_bfinder_v, put_sz, newsz);
        put_notifier_v = zrealloc(put_notifier_v, put_sz, newsz);
        put_sz = newsz;
    }
    put_bfinder_v[next_id] = bfinder;
    put_notifier_v[next_id] = notifier;
    serialization_id_t r = next_id++;
    //_S_("DeserializationDispatcher registered the following put handler for id: "
    //    <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    return r;
}

BufferFinder DeserializationDispatcher::getPutBufferFinder_ (serialization_id_t id) {
    return put_bfinder_v[id];
}

Notifier DeserializationDispatcher::getPutNotifier_ (serialization_id_t id) {
    return put_notifier_v[id];
}

serialization_id_t DeserializationDispatcher::addGetFunctions (BufferFinder bfinder,
                                                               Notifier notifier) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addGetFunctions_(bfinder, notifier);
}

serialization_id_t DeserializationDispatcher::addGetFunctions_ (BufferFinder bfinder,
                                                                Notifier notifier) {
    if (get_sz<=(size_t)next_id) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = next_id+1;
        // do not use GC
        get_bfinder_v = zrealloc(get_bfinder_v, get_sz, newsz);
        get_notifier_v = zrealloc(get_notifier_v, get_sz, newsz);
        get_sz = newsz;
    }
    get_bfinder_v[next_id] = bfinder;
    get_notifier_v[next_id] = notifier;
    serialization_id_t r = next_id++;
    //_S_("DeserializationDispatcher registered the following get handler for id: "
    //    <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    return r;
}

BufferFinder DeserializationDispatcher::getGetBufferFinder_ (serialization_id_t id) {
    return get_bfinder_v[id];
}

Notifier DeserializationDispatcher::getGetNotifier_ (serialization_id_t id) {
    return get_notifier_v[id];
}

void DeserializationDispatcher::registerHandlers () {
    if (NULL == it) return; // nothing registered
    it->registerHandlers_();
}

void DeserializationDispatcher::registerHandlers_ () {
    for (size_t i=0 ; i<next_id ; ++i) {
        if (i<deser_sz && deser_v[i]) {
            //_S_("(DeserializationDispatcher registered id "<<i<<" as an async)");
            x10aux::register_async_handler(i);
        }
        if (i<put_sz && put_bfinder_v[i]) {
            //_S_("(DeserializationDispatcher registered id "<<i<<" as a put)"); 
            x10aux::register_put_handler(i);
        }
        if (i<get_sz && get_bfinder_v[i]) {
            //_S_("(DeserializationDispatcher registered id "<<i<<" as a get)"); 
            x10aux::register_get_handler(i);
        }
    }
    x10aux::registration_complete();
}


ref<Object> DeserializationDispatcher::create_(deserialization_buffer &buf, serialization_id_t id) {
    _S_("Dispatching deserialisation using id: "<<id);
    return deser_v[id](buf);
}

ref<Object> DeserializationDispatcher::create_(deserialization_buffer &buf) {
    serialization_id_t id = buf.read<serialization_id_t>();
    return create_(buf, id);
}

// vim:tabstop=4:shiftwidth=4:expandtab
