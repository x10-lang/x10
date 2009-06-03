#include <x10aux/config.h>

#include <x10/lang/Error.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t Error::_serialization_id =
    DeserializationDispatcher::addDeserializer(Error::_deserializer<Object>);

x10aux::ref<Error>
Error::_make() {
    return (new (x10aux::alloc<Error>()) Error())->_constructor();
}

x10aux::ref<Error>
Error::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<Error>()) Error())->_constructor(message);
}

x10aux::ref<Error>
Error::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<Error>()) Error())->_constructor(cause);
}
    
x10aux::ref<Error>
Error::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<Error>()) Error())->_constructor(message, cause);
}


RTT_CC_DECLS1(Error, "x10.lang.Error", Throwable)

// vim:tabstop=4:shiftwidth=4:expandtab
