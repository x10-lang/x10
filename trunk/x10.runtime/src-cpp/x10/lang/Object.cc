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
    if (x10aux::remote_ref::is_remote(this_.operator->()) || this_.isNull()) {
        // cannot dispatch for these "addresses", handle here
        buf.write(Ref::_serialization_id,m);
        buf.write(x10aux::remote_ref::make(this_.operator->()),m);
        return;
    }
    _S_("Serializing an "<<ANSI_SER<<ANSI_BOLD<<"interface id"<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(this_->_get_serialization_id(),m);
    _S_("Serializing the "<<ANSI_SER<<"interface body"<<ANSI_RESET<<" to buf: "<<&buf);
    this_->_serialize_body(buf, m);
}

x10_boolean Object::equals(x10aux::ref<Object> other) {
    return this->_struct_equals(other);
}

x10aux::RuntimeType x10::lang::Object::rtt;

void Object::_initRTT() {
    rtt.init(&rtt, "x10.lang.Object", 0, NULL, 0, NULL, NULL);
}


itable_entry Object::_itables[1] = { itable_entry(NULL,  (void*)x10aux::getRTT<Object>()) };


// vim:tabstop=4:shiftwidth=4:expandtab
