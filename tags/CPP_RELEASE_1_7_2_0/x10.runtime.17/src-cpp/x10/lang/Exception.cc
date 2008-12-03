#include <x10aux/config.h>

#include <x10/lang/Exception.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t Exception::_serialization_id =
    DeserializationDispatcher::addDeserializer(Exception::_deserializer<Object>);

DEFINE_RTT(Exception);
// vim:tabstop=4:shiftwidth=4:expandtab
