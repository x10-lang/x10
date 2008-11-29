#include <sstream>

#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Ref.h>
#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

x10_int x10::lang::Ref::hashCode() {
    return (x10_int) (int64_t)(void*)this;
}

x10aux::ref<x10::lang::String> x10::lang::Ref::toString() {
    std::stringstream ss;
    ss << this->_type()->name() << "@" << std::hex << (std::size_t)this;
    return new (x10aux::alloc<x10::lang::String>()) x10::lang::String(ss.str());
}

const serialization_id_t Ref::serialization_id =
    DeserializationDispatcher::addDeserializer(Ref::_deserialize<Object>);

DEFINE_RTT(Ref);
// vim:tabstop=4:shiftwidth=4:expandtab
