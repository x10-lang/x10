#include <x10aux/config.h>

#include <x10/io/EOFException.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

const serialization_id_t EOFException::_serialization_id =
        DeserializationDispatcher::addDeserializer(EOFException::_deserializer<Object>);
                
                

DEFINE_RTT(EOFException);
// vim:tabstop=4:shiftwidth=4:expandtab
