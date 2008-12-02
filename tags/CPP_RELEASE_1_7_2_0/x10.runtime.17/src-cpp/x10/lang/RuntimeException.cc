#include <x10aux/config.h>

#include <x10/lang/RuntimeException.h>

using namespace x10::lang;
using namespace x10aux;


const serialization_id_t RuntimeException::_serialization_id =
    DeserializationDispatcher::addDeserializer(RuntimeException::_deserializer<Object>);


DEFINE_RTT(RuntimeException);
// vim:tabstop=4:shiftwidth=4:expandtab
