#include <x10aux/config.h>

#include <x10/io/IOException.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;


const serialization_id_t IOException::_serialization_id =
        DeserializationDispatcher::addDeserializer(IOException::_deserializer<Object>);

RTT_CC_DECLS1(IOException,"x10.io.IOException",Exception)

// vim:tabstop=4:shiftwidth=4:expandtab
