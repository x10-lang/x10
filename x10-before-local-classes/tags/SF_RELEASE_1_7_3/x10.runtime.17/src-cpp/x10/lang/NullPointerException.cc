#include <x10aux/config.h>

#include <x10/lang/NullPointerException.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t NullPointerException::_serialization_id =
    DeserializationDispatcher::addDeserializer(NullPointerException::_deserializer<Object>);


DEFINE_RTT(NullPointerException);
// vim:tabstop=4:shiftwidth=4:expandtab
