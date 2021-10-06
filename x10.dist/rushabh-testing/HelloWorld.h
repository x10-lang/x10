#ifndef __HELLOWORLD_H
#define __HELLOWORLD_H

#include <x10rt.h>


namespace x10 { namespace lang { 
template<class TPMGL(T)> class Rail;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10 { namespace io { 
class Printer;
} } 
namespace x10 { namespace io { 
class Console;
} } 
namespace x10 { namespace lang { 
class Any;
} } 
namespace x10 { namespace compiler { 
class Synthetic;
} } 

class HelloWorld_Strings {
  public:
    static ::x10::lang::String sl__17;
};

class HelloWorld : public ::x10::lang::X10Class   {
    public:
    RTT_H_DECLS_CLASS
    
    static void main(::x10::lang::Rail< ::x10::lang::String* >* id__0);
    virtual ::HelloWorld* HelloWorld____this__HelloWorld();
    void _constructor();
    
    static ::HelloWorld* _make();
    
    virtual void __fieldInitializers_HelloWorld();
    
    // Serialization
    public: static const ::x10aux::serialization_id_t _serialization_id;
    
    public: virtual ::x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(::x10aux::serialization_buffer& buf);
    
    public: static ::x10::lang::Reference* _deserializer(::x10aux::deserialization_buffer& buf);
    
    public: void _deserialize_body(::x10aux::deserialization_buffer& buf);
    
};

#endif // HELLOWORLD_H

class HelloWorld;

#ifndef HELLOWORLD_H_NODEPS
#define HELLOWORLD_H_NODEPS
#ifndef HELLOWORLD_H_GENERICS
#define HELLOWORLD_H_GENERICS
#endif // HELLOWORLD_H_GENERICS
#endif // __HELLOWORLD_H_NODEPS
