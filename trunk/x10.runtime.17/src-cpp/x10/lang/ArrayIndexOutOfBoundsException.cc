#include <x10aux/config.h>

#include <x10/lang/ArrayIndexOutOfBoundsException.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t ArrayIndexOutOfBoundsException::_serialization_id =
    DeserializationDispatcher
        ::addDeserializer(ArrayIndexOutOfBoundsException::_deserializer<Object>);

x10aux::ref<ArrayIndexOutOfBoundsException>
ArrayIndexOutOfBoundsException::_make() {
    return (new (x10aux::alloc<ArrayIndexOutOfBoundsException>()) ArrayIndexOutOfBoundsException())->_constructor();
}

x10aux::ref<ArrayIndexOutOfBoundsException>
ArrayIndexOutOfBoundsException::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<ArrayIndexOutOfBoundsException>()) ArrayIndexOutOfBoundsException())->_constructor(message);
}

x10aux::ref<ArrayIndexOutOfBoundsException>
ArrayIndexOutOfBoundsException::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<ArrayIndexOutOfBoundsException>()) ArrayIndexOutOfBoundsException())->_constructor(cause);
}
    
x10aux::ref<ArrayIndexOutOfBoundsException>
ArrayIndexOutOfBoundsException::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<ArrayIndexOutOfBoundsException>()) ArrayIndexOutOfBoundsException())->_constructor(message, cause);
}

RTT_CC_DECLS1(ArrayIndexOutOfBoundsException, "x10.lang.ArrayIndexOutOfBoundsException", RuntimeException)

// vim:tabstop=4:shiftwidth=4:expandtab
