#include <x10aux/config.h>

#include <x10/lang/ArrayIndexOutOfBoundsException.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t ArrayIndexOutOfBoundsException::_serialization_id =
    DeserializationDispatcher
        ::addDeserializer(ArrayIndexOutOfBoundsException::_deserializer<Object>);


DEFINE_RTT(ArrayIndexOutOfBoundsException);
// vim:tabstop=4:shiftwidth=4:expandtab
