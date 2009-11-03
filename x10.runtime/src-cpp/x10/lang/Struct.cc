#include <x10/lang/Struct.h>

using namespace x10aux;
using namespace x10::lang;

void Struct_methods::_instance_init(Struct *this_) {
    _I_("Doing initialisation for class: x10::lang::Struct");
}

void Struct_methods::_constructor(Struct *this_) {
}

x10_boolean Struct::_struct_equals(Struct that) {
    return true;
}

void Struct::_serialize(Struct this_, serialization_buffer& buf, addr_map& m) {
}

void Struct::_deserialize_body(deserialization_buffer& buf) {
}

x10_int Struct::hashCode() {
    return 0;
}

RuntimeType Struct::rtt;
void Struct::_initRTT() {
    rtt.canonical = &rtt;
    rtt.init(&rtt, "x10.lang.Struct", 0, NULL, 0, NULL, NULL);
}

// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
