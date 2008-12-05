#include <x10aux/config.h>
#include <x10aux/alloc.h>

#include <x10/lang/Object.h>
#include <x10/lang/Ref.h>


using namespace x10::lang;
using namespace x10aux;

void Object::_serialize(x10aux::ref<Object> this_,
                        x10aux::serialization_buffer &buf,
                        x10aux::addr_map &m) 
{
    if (this_.isRemote() || this_.isNull()) {
        // cannot dispatch for these "addresses", handle here
        buf.write(Ref::serialization_id,m);
        buf.write(x10_ref_serialize(reinterpret_cast<x10_addr_t>(this_.get())),m);
        return;
    }
    _S_("Serializing an "ANSI_SER ANSI_BOLD"interface"ANSI_RESET
        " (i.e. serializing the id) to buf: "<<&buf);
    this_->_serialize_id(buf, m);
    _S_("Serializing the "ANSI_SER"body"ANSI_RESET" of the interface to buf: "<<&buf);
    this_->_serialize_body(buf, m);
}

DEFINE_RTT(Object);
// vim:tabstop=4:shiftwidth=4:expandtab
