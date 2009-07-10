#include <x10aux/config.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>

#include <assert.h>

using namespace x10aux;
using namespace x10::lang;

DeserializationDispatcher *DeserializationDispatcher::it;

serialization_id_t DeserializationDispatcher::addDeserializer (Deserializer init) {
    if (NULL == it) {
        it = new (alloc<DeserializationDispatcher>()) DeserializationDispatcher();
    }
    return it->addDeserializer_(init);
}

serialization_id_t DeserializationDispatcher::addDeserializer_ (Deserializer init) {
    if (initsz<=(size_t)initc) {
        // grow slowly as this is init phase and we don't want to take
        // up RAM unnecessarily
        size_t newsz = initsz+1;
        initv = realloc(initv, initsz*sizeof(Deserializer), newsz*sizeof(Deserializer));
        initsz = newsz;
    }
    initv[initc] = init;
    serialization_id_t r = initc++;
    _S_("DeserializationDispatcher registered the following handler for id: "
        <<r<<": "<<std::hex<<(size_t)init<<std::dec);
    return r;
}


ref<Object> DeserializationDispatcher::create_(serialization_buffer &buf) {
    serialization_id_t id = buf.read<serialization_id_t>();
    _S_("Dispatching deserialisation using id: "<<id);

    return initv[id](buf);
}

// vim:tabstop=4:shiftwidth=4:expandtab
