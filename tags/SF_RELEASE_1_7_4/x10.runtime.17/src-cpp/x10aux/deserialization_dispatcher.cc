#include <x10aux/config.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>


using namespace x10aux;
using namespace x10::lang;

DeserializationDispatcher *DeserializationDispatcher::it;

ref<Object> DeserializationDispatcher::create_(serialization_buffer &buf) {
    serialization_id_t id = buf.read<serialization_id_t>();
    _S_("Dispatching deserialisation using id: "<<id);

    return initv[id](buf);
}

// vim:tabstop=4:shiftwidth=4:expandtab
