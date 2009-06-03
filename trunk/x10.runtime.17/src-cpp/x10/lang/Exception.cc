#include <x10aux/config.h>

#include <x10/lang/Exception.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t Exception::_serialization_id =
    DeserializationDispatcher::addDeserializer(Exception::_deserializer<Object>);

x10aux::ref<Exception>
Exception::_make() {
    return (new (x10aux::alloc<Exception>()) Exception())->_constructor();
}

x10aux::ref<Exception>
Exception::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<Exception>()) Exception())->_constructor(message);
}

x10aux::ref<Exception>
Exception::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<Exception>()) Exception())->_constructor(cause);
}

x10aux::ref<Exception>
Exception::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<Exception>()) Exception())->_constructor(message, cause);
}


RTT_CC_DECLS1(Exception, "x10.lang.Exception", Throwable)

// vim:tabstop=4:shiftwidth=4:expandtab
