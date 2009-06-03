#include <x10aux/config.h>

#include <x10/lang/ClassCastException.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t ClassCastException::_serialization_id =
    DeserializationDispatcher::addDeserializer(ClassCastException::_deserializer<Object>);

x10aux::ref<ClassCastException>
ClassCastException::_make() {
    return (new (x10aux::alloc<ClassCastException>()) ClassCastException())->_constructor();
}

x10aux::ref<ClassCastException>
ClassCastException::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<ClassCastException>()) ClassCastException())->_constructor(message);
}

x10aux::ref<ClassCastException>
ClassCastException::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<ClassCastException>()) ClassCastException())->_constructor(cause);
}
    
x10aux::ref<ClassCastException>
ClassCastException::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<ClassCastException>()) ClassCastException())->_constructor(message, cause);
}

RTT_CC_DECLS1(ClassCastException, "x10.lang.ClassCastException", RuntimeException)

// vim:tabstop=4:shiftwidth=4:expandtab
