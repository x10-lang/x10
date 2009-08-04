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
#ifndef USE_DEFAULT_HASHCODE
    void* v = (void*)this;
    x10_int* p = (x10_int*)&v;
    x10_int hc = 0;
    for (unsigned int i = 0; i < sizeof(void*)/sizeof(x10_int); i++)
        hc ^= p[i];
    return hc;
#else
    return (x10_int) (int64_t)(void*)this;
#endif
}

x10aux::ref<x10::lang::String> x10::lang::Ref::toString() {
    return String::Lit(alloc_printf("%s@%x",this->_type()->name(),(std::size_t)this));
}

const serialization_id_t Ref::_serialization_id =
    DeserializationDispatcher::addDeserializer(Ref::_deserialize<Object>);

RTT_CC_DECLS1(Ref, "x10.lang.Ref", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
