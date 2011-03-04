#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Ref.h>
#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

x10aux::ref<Ref>
Ref::_make() {
    return (new (x10aux::alloc<Ref>()) Ref())->_constructor();
}

x10_int x10::lang::Ref::hashCode() {
    // Combine the bits of the pointer into a 32 bit integer.
    // Note: intentionally not doing some type-punning pointer thing here as
    // the behavior of that is somewhat underdefined and tends to expose
    // "interesting" behavior in C++ compilers (especially at high optimization level).
    void *v = (void*)this;
    uint64_t v2 = (uint64_t)v;
    x10_int lower = (x10_int)(v2 & 0xffffffff);
    x10_int upper = (x10_int)(v2 >> 32);
    x10_int hc = lower ^ upper;
    return hc;
}

x10aux::ref<x10::lang::String> x10::lang::Ref::toString() {
    return String::Lit(alloc_printf("%s@%x",this->_type()->name(),(std::size_t)this));
}

const serialization_id_t Ref::serialization_id =
    DeserializationDispatcher::addDeserializer(Ref::_deserialize<Object>);

RTT_CC_DECLS1(Ref, "x10.lang.Ref", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
