#include <x10aux/config.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>

#include <assert.h>

using namespace x10aux;
using namespace x10::lang;

DeserializationDispatcher *DeserializationDispatcher::it;

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
        deser_v = (Deserializer*)::realloc(deser_v, newsz*sizeof(Deserializer));
        ::memset(&deser_v[deser_sz], 0,
                 ((char*)&deser_v[newsz])-((char*)&deser_v[deser_sz]));
        deser_sz = newsz;
    }
    deser_v[next_id] = deser;
    serialization_id_t r = next_id++;
    _S_("DeserializationDispatcher registered the following handler for id: "
        <<r<<": "<<std::hex<<(size_t)deser<<std::dec);
    (void) is_async; // TODO: use two numbering schemes
    return r;
}

serialization_id_t DeserializationDispatcher::addPutBufferFinder (BufferFinder bfinder) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addPutBufferFinder_(bfinder);
}

serialization_id_t DeserializationDispatcher::addPutBufferFinder_ (BufferFinder bfinder) {
    if (put_bfinder_sz<=(size_t)next_id) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = next_id+1;
        // do not use GC
        put_bfinder_v = (BufferFinder*)::realloc(put_bfinder_v, newsz*sizeof(BufferFinder));
        ::memset(&put_bfinder_v[put_bfinder_sz], 0,
                 ((char*)&put_bfinder_v[newsz])-((char*)&put_bfinder_v[put_bfinder_sz]));
        put_bfinder_sz = newsz;
    }
    put_bfinder_v[next_id] = bfinder;
    serialization_id_t r = next_id++;
    _S_("DeserializationDispatcher registered the following put handler for id: "
        <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    return r;
}

BufferFinder DeserializationDispatcher::getPutBufferFinder_ (serialization_id_t id) {
    return put_bfinder_v[id];
}

serialization_id_t DeserializationDispatcher::addGetBufferFinder (BufferFinder bfinder) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addGetBufferFinder_(bfinder);
}

serialization_id_t DeserializationDispatcher::addGetBufferFinder_ (BufferFinder bfinder) {
    if (get_bfinder_sz<=(size_t)next_id) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = next_id+1;
        // do not use GC
        get_bfinder_v = (BufferFinder*)::realloc(get_bfinder_v, newsz*sizeof(BufferFinder));
        ::memset(&get_bfinder_v[get_bfinder_sz], 0,
                 ((char*)&get_bfinder_v[newsz])-((char*)&get_bfinder_v[get_bfinder_sz]));
        get_bfinder_sz = newsz;
    }
    get_bfinder_v[next_id] = bfinder;
    serialization_id_t r = next_id++;
    _S_("DeserializationDispatcher registered the following get handler for id: "
        <<r<<": "<<std::hex<<(size_t)bfinder<<std::dec);
    return r;
}

BufferFinder DeserializationDispatcher::getGetBufferFinder_ (serialization_id_t id) {
    return get_bfinder_v[id];
}

void DeserializationDispatcher::registerHandlers () {
    if (NULL == it) return; // nothing registered
    it->registerHandlers_();
}

void DeserializationDispatcher::registerHandlers_ () {
    for (int i=0 ; i<next_id ; ++i) {
        if (i<deser_sz && deser_v[i]) {
            _S_("(DeserializationDispatcher registered id "<<i<<" as an async)");
            x10aux::register_async_handler(i);
        }
        if (i<put_bfinder_sz && put_bfinder_v[i]) {
            _S_("(DeserializationDispatcher registered id "<<i<<" as a put)"); 
            x10aux::register_put_handler(i);
        }
        if (i<get_bfinder_sz && get_bfinder_v[i]) {
            _S_("(DeserializationDispatcher registered id "<<i<<" as a get)"); 
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
