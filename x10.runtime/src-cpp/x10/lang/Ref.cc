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
    // STEP 1: Figure out the address to use as for the object.
    void *v;
    if (this->location == x10aux::here) {
        v = (void*)this;
    } else {
        v = (void*)x10aux::get_remote_ref(this);
    }

    // STEP 2: Combine the bits of the pointer into a 32 bit integer.
    //         Note: intentionally not doing some type-punning pointer thing here as
    //         the behavior of that is somewhat underdefined and tends to expose
    //         "interesting" behavior in C++ compilers (especially at high optimization level).
    uint64_t v2 = (uint64_t)v;
    x10_int lower = (x10_int)(v2 & 0xffffffff);
    x10_int upper = (x10_int)(v2 >> 32);
    x10_int hc = lower ^ upper;
    return hc;
}

x10aux::ref<x10::lang::String> x10::lang::Ref::toString() {
    return String::Lit(alloc_printf("%s@%p",this->_type()->name(),(void*)this));
}

x10aux::ref<x10::lang::String> x10::lang::Ref::typeName() {
    return x10::lang::String::Lit(_type()->name());
}

const serialization_id_t Ref::_serialization_id =
    DeserializationDispatcher::addDeserializer(Ref::_deserializer<Ref>);

void Ref::_serialize(ref<Ref> this_, serialization_buffer &buf, addr_map &m)
{
    serialization_id_t id = this_.isNull() ? 0 : this_->_get_serialization_id();
    _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"class id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(id, m);
    // FIXME: maybe optimize nulls by moving the call below into the conditional?
    _serialize_reference(this_, buf, m);
    if (!this_.isNull()) {
        _S_("Serializing the "<<ANSI_SER<<"class body"<<ANSI_RESET<<" to buf: "<<&buf);
        this_->_serialize_body(buf, m);
    }
}

const serialization_id_t Ref::_interface_serialization_id =
    DeserializationDispatcher::addDeserializer(Ref::_deserialize<Ref>);

void Ref::_serialize_interface(serialization_buffer &buf, addr_map &m)
{
    _serialize(this, buf, m);
}

void Ref::_serialize_reference(ref<Ref> this_, serialization_buffer &buf, addr_map &m)
{
    bool isNull = this_.isNull();
    x10_int loc = isNull ? 0 : this_->location;
    buf.write(loc, m);
    if (isNull) {
        _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" to buf: "<<&buf);
        buf.write((x10_addr_t)0, m);
    } else if (loc == x10aux::here) {
        _S_("Serialising a "<<ANSI_SER<<ANSI_BOLD<<"local Ref"<<ANSI_RESET<<
                " object of type "<<this_->_type()->name());
        buf.write((x10_addr_t)(size_t)this_.operator->(), m);
    } else {
        _S_("Serialising a "<<ANSI_SER<<ANSI_BOLD<<"remote Ref"<<ANSI_RESET<<
                " object of type "<<this_->_type()->name()<<" (loc="<<loc<<")");
        x10_addr_t tmp = get_remote_ref(this_.operator->());
        buf.write(tmp, m);
    }
}

void Ref::dealloc_object(Ref* obj) {
    _M_("Attempting to dealloc object "<<(void*)obj<<", location="<<obj->location);
    obj->_destructor();
    if (obj->location == x10aux::here)
        dealloc(obj);
    else
        dealloc_remote(obj);
}


RTT_CC_DECLS1(Ref, "x10.lang.Ref", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
