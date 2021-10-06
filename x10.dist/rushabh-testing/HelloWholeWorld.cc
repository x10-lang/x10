/*************************************************/
/* START of HelloWholeWorld */
#include <HelloWholeWorld.h>

#include <x10/lang/Rail.h>
#include <x10/lang/String.h>
#include <x10/lang/Long.h>
#include <x10/lang/Boolean.h>
#include <x10/io/Printer.h>
#include <x10/io/Console.h>
#include <x10/lang/Any.h>
#include <x10/xrx/Runtime.h>
#include <x10/xrx/FinishState.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/Place.h>
#include <x10/lang/Iterable.h>
#include <x10/lang/PlaceGroup.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/compiler/AsyncClosure.h>
#include <x10/xrx/Runtime__Profile.h>
#include <x10/lang/CheckedThrowable.h>
#include <x10/compiler/Synthetic.h>
#include <x10/lang/String.h>
#ifndef HELLOWHOLEWORLD__CLOSURE__7_CLOSURE
#define HELLOWHOLEWORLD__CLOSURE__7_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/VoidFun_0_0.h>
class HelloWholeWorld__closure__7 : public ::x10::lang::Closure {
    public:
    
    static ::x10::lang::VoidFun_0_0::itable<HelloWholeWorld__closure__7> _itable;
    static ::x10aux::itable_entry _itables[2];
    
    virtual ::x10aux::itable_entry* _getITables() { return _itables; }
    
    void __apply(){
        ::x10::io::Console::FMGL(OUT__get)()->x10::io::Printer::println(reinterpret_cast< ::x10::lang::Any*>(::x10::lang::String::__plus(::x10::lang::String::__plus(::x10::lang::Place::_make(::x10aux::here), (&::HelloWholeWorld_Strings::sl__225)), ::x10aux::nullCheck(args)->x10::lang::Rail< ::x10::lang::String* >::__apply(
                                                                                                                                                                                                                                                          ((x10_long)0ll)))));
    }
    
    // captured environment
    ::x10::lang::Rail< ::x10::lang::String* >* args;
    
    ::x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    ::x10aux::serialization_id_t _get_network_id() {
        return _network_id;
    }
    
    void _serialize_body(::x10aux::serialization_buffer &buf) {
        buf.write(this->args);
    }
    
    static x10::lang::Reference* _deserialize(::x10aux::deserialization_buffer &buf) {
        HelloWholeWorld__closure__7* storage = ::x10aux::alloc_z<HelloWholeWorld__closure__7>();
        buf.record_reference(storage);
        ::x10::lang::Rail< ::x10::lang::String* >* that_args = buf.read< ::x10::lang::Rail< ::x10::lang::String* >*>();
        HelloWholeWorld__closure__7* this_ = new (storage) HelloWholeWorld__closure__7(that_args);
        return this_;
    }
    
    HelloWholeWorld__closure__7(::x10::lang::Rail< ::x10::lang::String* >* args) : args(args) { }
    
    HelloWholeWorld__closure__7(::x10::lang::Rail< ::x10::lang::String* >* args,x10_long zzztemp){ 
                                                                                                 this->args = args;
                                                                                                  }
                                                                                                 
                                                                                                 static const ::x10aux::serialization_id_t _serialization_id;
                                                                                                 
                                                                                                 static const ::x10aux::serialization_id_t _network_id;
                                                                                                 
                                                                                                 static const ::x10aux::RuntimeType* getRTT() { return ::x10aux::getRTT< ::x10::lang::VoidFun_0_0>(); }
                                                                                                 virtual const ::x10aux::RuntimeType *_type() const { return ::x10aux::getRTT< ::x10::lang::VoidFun_0_0>(); }
                                                                                                 
                                                                                                 const char* toNativeString() {
                                                                                                     return "HelloWholeWorld.x10:38";
                                                                                                 }
    
    };
    
    #endif // HELLOWHOLEWORLD__CLOSURE__7_CLOSURE
    
//#line 31 "HelloWholeWorld.x10"
void HelloWholeWorld::main(::x10::lang::Rail< ::x10::lang::String* >* args) {
    
    //#line 32 "HelloWholeWorld.x10"
    if ((((x10_long)(::x10aux::nullCheck(args)->FMGL(size))) < (((x10_long)1ll))))
    {
        
        //#line 33 "HelloWholeWorld.x10"
        ::x10::io::Console::FMGL(OUT__get)()->x10::io::Printer::println(
          reinterpret_cast< ::x10::lang::Any*>((&::HelloWholeWorld_Strings::sl__224)));
        
        //#line 34 "HelloWholeWorld.x10"
        return;
    }
    {
        
        //#line 37 "HelloWholeWorld.x10"
        ::x10::xrx::Runtime::ensureNotInAtomic();
        ::x10::xrx::FinishState* fs__187 = ::x10::xrx::Runtime::startFinish();
        try {
            {
                {
                    ::x10::lang::Iterator< ::x10::lang::Place>* p__181;
                    for (p__181 = ::x10::lang::Place::places()->iterator();
                         ::x10::lang::Iterator< ::x10::lang::Place>::hasNext(::x10aux::nullCheck(p__181));
                         ) {
                        ::x10::lang::Place p = ::x10::lang::Iterator< ::x10::lang::Place>::next(::x10aux::nullCheck(p__181));
                        
                        //#line 38 "HelloWholeWorld.x10"
                        ::x10::xrx::Runtime::runAsync(p, reinterpret_cast< ::x10::lang::VoidFun_0_0*>((new (::x10aux::alloc< ::x10::lang::VoidFun_0_0>(sizeof(HelloWholeWorld__closure__7)))HelloWholeWorld__closure__7(args))),
                                                      ::x10aux::class_cast_unchecked< ::x10::xrx::Runtime__Profile*>(reinterpret_cast< ::x10::lang::NullType*>(X10_NULL)));
                    }
                }
                
            }
        }
        catch (::x10::lang::CheckedThrowable* __exc25) {
            {
                ::x10::lang::CheckedThrowable* ct__185 = __exc25;
                {
                    ::x10::xrx::Runtime::pushException(ct__185);
                }
            }
        }
        ::x10::xrx::Runtime::stopFinish(fs__187);
    }
    
    //#line 40 "HelloWholeWorld.x10"
    ::x10::io::Console::FMGL(OUT__get)()->x10::io::Printer::println(
      reinterpret_cast< ::x10::lang::Any*>((&::HelloWholeWorld_Strings::sl__226)));
}

//#line 30 "HelloWholeWorld.x10"
::HelloWholeWorld* HelloWholeWorld::HelloWholeWorld____this__HelloWholeWorld(
  ) {
    return this;
    
}
void HelloWholeWorld::_constructor() {
    this->HelloWholeWorld::__fieldInitializers_HelloWholeWorld();
}
::HelloWholeWorld* HelloWholeWorld::_make() {
    ::HelloWholeWorld* this_ = new (::x10aux::alloc_z< ::HelloWholeWorld>()) ::HelloWholeWorld();
    this_->_constructor();
    return this_;
}


void HelloWholeWorld::__fieldInitializers_HelloWholeWorld(
  ) {
 
}
const ::x10aux::serialization_id_t HelloWholeWorld::_serialization_id = 
    ::x10aux::DeserializationDispatcher::addDeserializer(::HelloWholeWorld::_deserializer);

void HelloWholeWorld::_serialize_body(::x10aux::serialization_buffer& buf) {
    
}

::x10::lang::Reference* ::HelloWholeWorld::_deserializer(::x10aux::deserialization_buffer& buf) {
    ::HelloWholeWorld* this_ = new (::x10aux::alloc_z< ::HelloWholeWorld>()) ::HelloWholeWorld();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void HelloWholeWorld::_deserialize_body(::x10aux::deserialization_buffer& buf) {
    
}

::x10aux::RuntimeType HelloWholeWorld::rtt;
void HelloWholeWorld::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const ::x10aux::RuntimeType** parents = NULL; 
    rtt.initStageTwo("HelloWholeWorld",::x10aux::RuntimeType::class_kind, 0, parents, 0, NULL, NULL);
}

::x10::lang::String HelloWholeWorld_Strings::sl__225(" says hello and ");
::x10::lang::String HelloWholeWorld_Strings::sl__224("Usage: HelloWholeWorld message");
::x10::lang::String HelloWholeWorld_Strings::sl__226("Goodbye");

::x10::lang::VoidFun_0_0::itable<HelloWholeWorld__closure__7>HelloWholeWorld__closure__7::_itable(&::x10::lang::Reference::equals, &::x10::lang::Closure::hashCode, &HelloWholeWorld__closure__7::__apply, &HelloWholeWorld__closure__7::toString, &::x10::lang::Closure::typeName);
::x10aux::itable_entry HelloWholeWorld__closure__7::_itables[2] = {::x10aux::itable_entry(&::x10aux::getRTT< ::x10::lang::VoidFun_0_0>, &HelloWholeWorld__closure__7::_itable),::x10aux::itable_entry(NULL, NULL)};

const ::x10aux::serialization_id_t HelloWholeWorld__closure__7::_serialization_id = 
    ::x10aux::DeserializationDispatcher::addDeserializer(HelloWholeWorld__closure__7::_deserialize);
const ::x10aux::serialization_id_t HelloWholeWorld__closure__7::_network_id = 
    ::x10aux::NetworkDispatcher::addNetworkDeserializer(HelloWholeWorld__closure__7::_deserialize,::x10aux::CLOSURE_KIND_ASYNC_CLOSURE);

/* END of HelloWholeWorld */
/*************************************************/
