#include <x10aux/config.h>
#include <x10aux/alloc.h>

#include <x10/lang/Reference.h>
#include <x10/lang/Object.h>

using namespace x10::lang;
using namespace x10aux;

void Reference::_serialize_interface(x10aux::serialization_buffer &buf)
{
    _S_("Serializing the "<<ANSI_SER<<"interface body"<<ANSI_RESET<<" to buf: "<<&buf);
    this->_serialize_body(buf);
}

void Reference::_serialize(x10aux::ref<Reference> this_,
                        x10aux::serialization_buffer &buf)
{
    bool isNull = this_.isNull();
    x10aux::serialization_id_t id = isNull ? 0 : this_->_get_interface_serialization_id();
    _S_("Serializing an "<<ANSI_SER<<ANSI_BOLD<<"interface id "<<id<<ANSI_RESET<<" to buf: "<<&buf);
    buf.write(id);
    if (!isNull) {
        this_->_serialize_interface(buf);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
