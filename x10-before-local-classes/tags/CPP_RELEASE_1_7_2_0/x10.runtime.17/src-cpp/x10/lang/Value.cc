#include <sstream>

#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Value.h>
#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t Value::_serialization_id =
    DeserializationDispatcher::addDeserializer(Value::_deserializer<Object>);

x10aux::ref<x10::lang::String> x10::lang::Value::toString() {
    return String::Lit("The empty value");
}

DEFINE_RTT(Value);
// vim:tabstop=4:shiftwidth=4:expandtab
