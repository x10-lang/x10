#include <x10aux/config.h>

#include <x10/lang/OutOfMemoryError.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t OutOfMemoryError::_serialization_id =
    DeserializationDispatcher::addDeserializer(OutOfMemoryError::_deserializer<Object>);

x10aux::ref<OutOfMemoryError>
OutOfMemoryError::_make() {
    return (new (x10aux::alloc<OutOfMemoryError>()) OutOfMemoryError())->_constructor();
}

x10aux::ref<OutOfMemoryError>
OutOfMemoryError::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<OutOfMemoryError>()) OutOfMemoryError())->_constructor(message);
}


RTT_CC_DECLS1(OutOfMemoryError, "x10.lang.OutOfMemoryError", Error)

// vim:tabstop=4:shiftwidth=4:expandtab
