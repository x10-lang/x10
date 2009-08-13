#include <x10aux/config.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>

#include <assert.h>

using namespace x10aux;
using namespace x10::lang;

DeserializationDispatcher *DeserializationDispatcher::it;

serialization_id_t DeserializationDispatcher::addDeserializer (Deserializer init, bool is_async) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addDeserializer_(init, is_async);
}

serialization_id_t DeserializationDispatcher::addDeserializer_ (Deserializer init, bool is_async) {
    if (initsz<=(size_t)initc) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = initsz+1;
        // do not use GC
        initv = (Deserializer*)::realloc(initv, newsz*sizeof(Deserializer));
        initsz = newsz;
    }
    initv[initc] = init;
    serialization_id_t r = initc++;
    _S_("DeserializationDispatcher registered the following handler for id: "
        <<r<<": "<<std::hex<<(size_t)init<<std::dec);
    (void) is_async; // TODO: use two numbering schemes
    return r;
}

void DeserializationDispatcher::registerAsyncHandlers () {
    if (NULL == it) return; // nothing registered
    it->registerAsyncHandlers_();
}

void DeserializationDispatcher::registerAsyncHandlers_ () {
    for (size_t i=0 ; i<initc ; ++i) {
        x10aux::register_async_handler(i);
        _S_("(DeserializationDispatcher registered id "<<i<<" as an async)");
    }
    x10aux::registration_complete();
}


ref<Object> DeserializationDispatcher::create_(deserialization_buffer &buf, serialization_id_t id) {
    _S_("Dispatching deserialisation using id: "<<id);
    return initv[id](buf);
}

ref<Object> DeserializationDispatcher::create_(deserialization_buffer &buf) {
    serialization_id_t id = buf.read<serialization_id_t>();
    return create_(buf, id);
}

// vim:tabstop=4:shiftwidth=4:expandtab
