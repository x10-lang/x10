#include <x10aux/config.h>
#include <x10aux/alloc.h>

#include <x10/lang/Object.h>
#include <x10/lang/Ref.h>
#include <x10/lang/Value.h>


using namespace x10::lang;
using namespace x10aux;

void Object::_serialize(x10aux::ref<Object> this_,
                        x10aux::serialization_buffer &buf,
                        x10aux::addr_map &m) 
{
    if (x10aux::remote_ref::is_remote(this_.get()) || this_.isNull()) {
        // cannot dispatch for these "addresses", handle here
        buf.write(Ref::serialization_id,m);
        buf.write(x10aux::remote_ref::make(this_.get()),m);
        return;
    }
    _S_("Serializing an "ANSI_SER ANSI_BOLD"interface"ANSI_RESET
        " (i.e. serializing the id) to buf: "<<&buf);
    this_->_serialize_id(buf, m);
    _S_("Serializing the "ANSI_SER"body"ANSI_RESET" of the interface to buf: "<<&buf);
    this_->_serialize_body(buf, m);
}

x10_boolean Object::equals(x10aux::ref<Object> other) {
    if (x10aux::instanceof<Value>(other))
        return this->equals(x10aux::ref<Value>(dynamic_cast<Value*>(other.get())));
    if (x10aux::instanceof<Ref>(other))
        return this->equals(x10aux::ref<Ref>(dynamic_cast<Ref*>(other.get())));
    assert (false && "Unknown reference type");
    return false;
}

itable_entry Object::_itables[1] = { itable_entry(0, NULL) };


// vim:tabstop=4:shiftwidth=4:expandtab
