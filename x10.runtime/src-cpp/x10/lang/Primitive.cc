#include <x10/lang/Primitive.h>

void x10::lang::Primitive::_instance_init() {
    _I_("Doing initialisation for class: x10::lang::Primitive");
    
}


//#line 3 "/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Primitive.x10"
void x10::lang::Primitive::_constructor() {
    this->x10::lang::Value::_constructor();
    
}

x10_boolean x10::lang::Primitive::_struct_equals(x10aux::ref<x10::lang::Object> p0) {
    if (p0.operator->() == this) return true; // short-circuit trivial equality
    if (!this->x10::lang::Value::_struct_equals(p0))
        return false;
    x10aux::ref<x10::lang::Primitive> that = (x10aux::ref<x10::lang::Primitive>) p0;
    return true;
}
void x10::lang::Primitive::_static_init() {
    static bool done = false;
    if (done) return;
    done = true;
    _I_("Doing static initialisation for class: x10::lang::Primitive");
    x10::lang::Value::_static_init();
    
}

const x10aux::serialization_id_t x10::lang::Primitive::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(x10::lang::Primitive::_deserializer<x10::lang::Object>);

void x10::lang::Primitive::_serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    x10::lang::Value::_serialize_body(buf, m);
    
}

void x10::lang::Primitive::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Value::_deserialize_body(buf);
    
}

x10aux::RuntimeType x10::lang::Primitive::rtt;
void x10::lang::Primitive::_initRTT() {
    rtt.canonical = &rtt;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Value>()};
    rtt.init(&rtt, "x10.lang.Primitive", 1, parents, 0, NULL, NULL);
}

extern "C" const char* LNMAP_x10_lang_Primitive_cc = "N{\"x10/lang/Primitive.cc\"} F{0:\"/home/dgrove/x10-trunk/x10.runtime/src-x10/x10/lang/Primitive.x10\",1:\"x10.lang.Primitive\",2:\"this\",3:\"\",4:\"x10::lang::Primitive\",5:\"_constructor\",6:\"void\",} L{13->0:3,} M{6 4.5()->3 1.2();}";
