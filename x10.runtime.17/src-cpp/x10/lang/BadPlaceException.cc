#include <x10aux/config.h>

#include <x10/lang/BadPlaceException.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t BadPlaceException::_serialization_id =
    DeserializationDispatcher::addDeserializer(BadPlaceException::_deserializer<Object>);

x10aux::ref<BadPlaceException>
BadPlaceException::_make() {
    return (new (x10aux::alloc<BadPlaceException>()) BadPlaceException())->_constructor();
}

x10aux::ref<BadPlaceException>
BadPlaceException::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<BadPlaceException>()) BadPlaceException())->_constructor(message);
}

x10aux::ref<BadPlaceException>
BadPlaceException::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<BadPlaceException>()) BadPlaceException())->_constructor(cause);
}
    
x10aux::ref<BadPlaceException>
BadPlaceException::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<BadPlaceException>()) BadPlaceException())->_constructor(message, cause);
}


RTT_CC_DECLS1(BadPlaceException, "x10.lang.BadPlaceException", x10::lang::RuntimeException)

// vim:tabstop=4:shiftwidth=4:expandtab
