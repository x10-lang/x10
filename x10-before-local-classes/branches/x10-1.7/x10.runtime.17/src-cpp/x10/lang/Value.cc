#include <sstream>

#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Value.h>
#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t Value::_serialization_id =
    DeserializationDispatcher::addDeserializer(Value::_deserializer<Object>);

x10aux::ref<Value> Value::_make() {
    return (new (x10aux::alloc<Value>()) Value())->_constructor();
}

x10aux::ref<x10::lang::String> x10::lang::Value::toString() {
    return String::Lit("Value without toString defined.");
}

x10_boolean
Value::equals(x10aux::ref<Value> other) {
    if (other == x10aux::ref<Value>(this)) return true;
    if (!x10aux::instanceof<Value>(other)) return false;
    return this->_struct_equals(other);
}

x10_boolean
Value::_struct_equals(x10aux::ref<Object> other) {
    if (!_type()->concreteInstanceOf(other))
        return false;
    // now compare fields but there aren't any
    return true;
}

RTT_CC_DECLS1(Value, "x10.lang.Value", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
