#include <x10aux/config.h>

#include <x10/lang/NullPointerException.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t NullPointerException::_serialization_id =
    DeserializationDispatcher::addDeserializer(NullPointerException::_deserializer<Object>);

x10aux::ref<NullPointerException>
NullPointerException::_make() {
    return (new (x10aux::alloc<NullPointerException>()) NullPointerException())->_constructor();
}

x10aux::ref<NullPointerException>
NullPointerException::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<NullPointerException>()) NullPointerException())->_constructor(message);
}

x10aux::ref<NullPointerException>
NullPointerException::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<NullPointerException>()) NullPointerException())->_constructor(cause);
}
    
x10aux::ref<NullPointerException>
NullPointerException::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<NullPointerException>()) NullPointerException())->_constructor(message, cause);
}

RTT_CC_DECLS1(NullPointerException, "x10.lang.NullPointerException", RuntimeException)
    
// vim:tabstop=4:shiftwidth=4:expandtab
