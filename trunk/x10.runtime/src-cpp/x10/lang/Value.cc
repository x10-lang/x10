#include <sstream>

#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Value.h>
#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t Value::_serialization_id =
    DeserializationDispatcher::addDeserializer(Value::_deserializer<Ref>);

void Value::_serialize(x10aux::ref<Value> this_,
                       x10aux::serialization_buffer &buf,
                       x10aux::addr_map &m) 
{
    x10aux::serialization_id_t id = this_->_get_serialization_id();
    _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"value id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(id,m);
    _S_("Serializing the "<<ANSI_SER<<"value body"<<ANSI_RESET<<" to buf: "<<&buf);
    this_->_serialize_body(buf, m);
}           

x10aux::ref<Value> Value::_make() {
    return (new (x10aux::alloc<Value>()) Value())->_constructor();
}

x10aux::ref<x10::lang::String> x10::lang::Value::toString() {
    return String::Lit("Value without toString defined.");
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
