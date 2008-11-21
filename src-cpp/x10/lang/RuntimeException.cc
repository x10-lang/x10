#include <x10aux/config.h>

#include <x10/lang/RuntimeException.h>

using namespace x10::lang;
using namespace x10aux;


void RuntimeException::_serialize_fields(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    Exception::_serialize_fields(buf, m);
}

void RuntimeException::_deserialize_fields(x10aux::serialization_buffer& buf) {
    (void) buf;
}

DEFINE_RTT(RuntimeException);
// vim:tabstop=4:shiftwidth=4:expandtab
