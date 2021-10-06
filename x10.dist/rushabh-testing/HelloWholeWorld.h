#ifndef __HELLOWHOLEWORLD_H
#define __HELLOWHOLEWORLD_H

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
namespace x10 { namespace xrx { 
class Runtime;
} } 
namespace x10 { namespace xrx { 
class FinishState;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace x10 { namespace lang { 
class Place;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterable;
} } 
namespace x10 { namespace lang { 
class PlaceGroup;
} } 
namespace x10 { namespace lang { 
class VoidFun_0_0;
} } 
namespace x10 { namespace compiler { 
class AsyncClosure;
} } 
namespace x10 { namespace xrx { 
class Runtime__Profile;
} } 
namespace x10 { namespace lang { 
class CheckedThrowable;
} } 
namespace x10 { namespace compiler { 
class Synthetic;
} } 

class HelloWholeWorld_Strings {
  public:
    static ::x10::lang::String sl__225;
    static ::x10::lang::String sl__224;
    static ::x10::lang::String sl__226;
};

class HelloWholeWorld : public ::x10::lang::X10Class   {
    public:
    RTT_H_DECLS_CLASS
    
    static void main(::x10::lang::Rail< ::x10::lang::String* >* args);
    virtual ::HelloWholeWorld* HelloWholeWorld____this__HelloWholeWorld();
    void _constructor();
    
    static ::HelloWholeWorld* _make();
    
    virtual void __fieldInitializers_HelloWholeWorld();
    
    // Serialization
    public: static const ::x10aux::serialization_id_t _serialization_id;
    
    public: virtual ::x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(::x10aux::serialization_buffer& buf);
    
    public: static ::x10::lang::Reference* _deserializer(::x10aux::deserialization_buffer& buf);
    
    public: void _deserialize_body(::x10aux::deserialization_buffer& buf);
    
};

#endif // HELLOWHOLEWORLD_H

class HelloWholeWorld;

#ifndef HELLOWHOLEWORLD_H_NODEPS
#define HELLOWHOLEWORLD_H_NODEPS
#ifndef HELLOWHOLEWORLD_H_GENERICS
#define HELLOWHOLEWORLD_H_GENERICS
#endif // HELLOWHOLEWORLD_H_GENERICS
#endif // __HELLOWHOLEWORLD_H_NODEPS
