#include <x10aux/config.h>

#include <x10/lang/RuntimeException.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t RuntimeException::_serialization_id =
    DeserializationDispatcher::addDeserializer(RuntimeException::_deserializer<Object>);

x10aux::ref<RuntimeException>
RuntimeException::_make() {
    return (new (x10aux::alloc<RuntimeException>()) RuntimeException())->_constructor();
}

x10aux::ref<RuntimeException>
RuntimeException::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<RuntimeException>()) RuntimeException())->_constructor(message);
}

x10aux::ref<RuntimeException>
RuntimeException::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<RuntimeException>()) RuntimeException())->_constructor(cause);
}
    
x10aux::ref<RuntimeException>
RuntimeException::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<RuntimeException>()) RuntimeException())->_constructor(message, cause);
}

RTT_CC_DECLS1(RuntimeException, "x10.lang.RuntimeException", Exception)
// vim:tabstop=4:shiftwidth=4:expandtab
