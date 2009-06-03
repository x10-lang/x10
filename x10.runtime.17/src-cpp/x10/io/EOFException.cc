#include <x10aux/config.h>

#include <x10/io/EOFException.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

const serialization_id_t EOFException::_serialization_id =
        DeserializationDispatcher::addDeserializer(EOFException::_deserializer<Object>);

x10aux::ref<EOFException>
EOFException::_make() {
    return (new (x10aux::alloc<EOFException>()) EOFException())->_constructor();
}

x10aux::ref<EOFException>
EOFException::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<EOFException>()) EOFException())->_constructor(message);
}

x10aux::ref<EOFException>
EOFException::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<EOFException>()) EOFException())->_constructor(cause);
}
    
x10aux::ref<EOFException>
EOFException::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<EOFException>()) EOFException())->_constructor(message, cause);
}


RTT_CC_DECLS1(EOFException, "x10.io.EOFException", IOException)

// vim:tabstop=4:shiftwidth=4:expandtab
