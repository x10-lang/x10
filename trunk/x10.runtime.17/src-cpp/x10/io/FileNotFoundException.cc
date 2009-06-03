#include <x10aux/config.h>

#include <x10/io/FileNotFoundException.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

const serialization_id_t FileNotFoundException::_serialization_id =
    DeserializationDispatcher::addDeserializer(FileNotFoundException::_deserializer<Object>);

x10aux::ref<FileNotFoundException>
FileNotFoundException::_make() {
    return (new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException())->_constructor();
}

x10aux::ref<FileNotFoundException>
FileNotFoundException::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException())->_constructor(message);
}

x10aux::ref<FileNotFoundException>
FileNotFoundException::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException())->_constructor(cause);
}
    
x10aux::ref<FileNotFoundException>
FileNotFoundException::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException())->_constructor(message, cause);
}

RTT_CC_DECLS1(FileNotFoundException, "x10.io.FileNotFoundException", IOException)

// vim:tabstop=4:shiftwidth=4:expandtab
