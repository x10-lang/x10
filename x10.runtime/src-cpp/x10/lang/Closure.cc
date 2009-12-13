#include <sstream>

#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Closure.h>
#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t Closure::_serialization_id =
    DeserializationDispatcher::addDeserializer(Closure::_deserializer<Ref>);

void Closure::_serialize(x10aux::ref<Closure> this_,
                         x10aux::serialization_buffer &buf) 
{
    x10aux::serialization_id_t id = this_->_get_serialization_id();
    _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"value id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(id);
    _S_("Serializing the "<<ANSI_SER<<"value body"<<ANSI_RESET<<" to buf: "<<&buf);
    this_->_serialize_body(buf);
}           

x10aux::ref<Closure> Closure::_make() {
    return (new (x10aux::alloc<Closure>()) Closure())->_constructor();
}

x10aux::ref<x10::lang::String> x10::lang::Closure::toString() {
    return String::Lit("Closure without toString defined.");
}

x10_boolean
Closure::_struct_equals(x10aux::ref<Object> other) {
    if (!_type()->concreteInstanceOf(other))
        return false;
    // now compare fields but there aren't any
    return true;
}

RTT_CC_DECLS1(Closure, "x10.lang.Closure", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
