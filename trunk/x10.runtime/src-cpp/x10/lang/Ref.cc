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
    DeserializationDispatcher::addDeserializer(Ref::_deserializer<Object>);

// FIXME: optimize refs coming home
void Ref::_serialize(ref<Ref> this_, serialization_buffer &buf, addr_map &m)
{
    serialization_id_t id = this_.isNull() ? 0 : this_->_get_serialization_id();
    _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"class id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(id, m);
    // FIXME: maybe optimize nulls?
    //if (!this_.isNull()) {
    // FIXME: factor out common code with _serialize_reference
    bool isNull = this_.isNull();
    x10_int loc = isNull ? 0 : this_->location;
    buf.write(loc, m);
    if (isNull) {
        _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" to buf: "<<&buf);
        buf.write((x10_addr_t)0, m);
        return;
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
    _S_("Serializing the "<<ANSI_SER<<"class body"<<ANSI_RESET<<" to buf: "<<&buf);
    this_->_serialize_body(buf, m);
    //}
}

// FIXME: factor out common code with _serialize
void Ref::_serialize_reference(ref<Ref> this_, serialization_buffer &buf, addr_map &m)
{
    _S_("Serializing an untyped "<<ANSI_SER<<ANSI_BOLD<<"reference"<<ANSI_RESET<<" to buf: "<<&buf);
    bool isNull = this_.isNull();
    x10_int loc = isNull ? 0 : this_->location;
    buf.write(loc, m);
    if (isNull) {
        _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" to buf: "<<&buf);
        buf.write((x10_addr_t)0, m);
        return;
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


RTT_CC_DECLS1(Ref, "x10.lang.Ref", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
