#include <x10aux/config.h>

#include <x10/lang/BadPlaceException.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t BadPlaceException::_serialization_id =
    DeserializationDispatcher::addDeserializer(BadPlaceException::_deserializer<Object>);

RTT_CC_DECLS1(BadPlaceException, "x10.lang.BadPlaceException", x10::lang::RuntimeException)

// vim:tabstop=4:shiftwidth=4:expandtab
