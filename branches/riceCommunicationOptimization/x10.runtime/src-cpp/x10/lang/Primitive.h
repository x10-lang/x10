// HACK! HACK! HACK!
// NativeRep Primitive to be a sub-class of Value

#ifndef __X10_LANG_PRIMITIVE_H
#define __X10_LANG_PRIMITIVE_H

#include <x10rt17.h>

#define X10_LANG_VALUE_H_NODEPS
#include <x10/lang/Value.h>
#undef X10_LANG_VALUE_H_NODEPS
namespace x10 { namespace lang { 

class Primitive : public x10::lang::Value  {
    public:
    RTT_H_DECLS_CLASS
    
    void _instance_init();
    
    static x10aux::ref<x10::lang::Primitive> _make() {
        x10aux::ref<x10::lang::Primitive> this_ = new (x10aux::alloc<x10::lang::Primitive>()) x10::lang::Primitive();
        this_->_constructor();
        return this_;
    }
    void _constructor();
    
    public: virtual x10_boolean _struct_equals(x10aux::ref<x10::lang::Object> p0);
    public : static void _static_init();
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: virtual x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
    
    public: template<class __T> static x10aux::ref<__T> _deserializer(x10aux::deserialization_buffer& buf) {
        x10aux::ref<x10::lang::Primitive> this_ = new (x10aux::alloc<x10::lang::Primitive >()) x10::lang::Primitive();
        this_->_deserialize_body(buf);
        return this_;
    }
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } 
#endif // X10_LANG_PRIMITIVE_H

namespace x10 { namespace lang {
        class Primitive;
} } 

#ifndef X10_LANG_PRIMITIVE_H_NODEPS
#define X10_LANG_PRIMITIVE_H_NODEPS
#include <x10/lang/Value.h>
#ifndef X10_LANG_PRIMITIVE_H_GENERICS
#define X10_LANG_PRIMITIVE_H_GENERICS
#endif // X10_LANG_PRIMITIVE_H_GENERICS
#endif // __X10_LANG_PRIMITIVE_H_NODEPS
