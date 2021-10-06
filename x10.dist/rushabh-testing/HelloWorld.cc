/*************************************************/
/* START of HelloWorld */
#include <HelloWorld.h>

#include <x10/lang/Rail.h>
#include <x10/lang/String.h>
#include <x10/io/Printer.h>
#include <x10/io/Console.h>
#include <x10/lang/Any.h>
#include <x10/compiler/Synthetic.h>
#include <x10/lang/String.h>

//#line 18 "HelloWorld.x10"
void HelloWorld::main(::x10::lang::Rail< ::x10::lang::String* >* id__0) {
    
    //#line 19 "HelloWorld.x10"
    ::x10::io::Console::FMGL(OUT__get)()->x10::io::Printer::println(reinterpret_cast< ::x10::lang::Any*>((&::HelloWorld_Strings::sl__17)));
}

//#line 17 "HelloWorld.x10"
::HelloWorld* HelloWorld::HelloWorld____this__HelloWorld() {
    return this;
    
}
void HelloWorld::_constructor() {
    this->HelloWorld::__fieldInitializers_HelloWorld();
}
::HelloWorld* HelloWorld::_make() {
    ::HelloWorld* this_ = new (::x10aux::alloc_z< ::HelloWorld>()) ::HelloWorld();
    this_->_constructor();
    return this_;
}


void HelloWorld::__fieldInitializers_HelloWorld() {
 
}
const ::x10aux::serialization_id_t HelloWorld::_serialization_id = 
    ::x10aux::DeserializationDispatcher::addDeserializer(::HelloWorld::_deserializer);

void HelloWorld::_serialize_body(::x10aux::serialization_buffer& buf) {
    
}

::x10::lang::Reference* ::HelloWorld::_deserializer(::x10aux::deserialization_buffer& buf) {
    ::HelloWorld* this_ = new (::x10aux::alloc_z< ::HelloWorld>()) ::HelloWorld();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void HelloWorld::_deserialize_body(::x10aux::deserialization_buffer& buf) {
    
}

::x10aux::RuntimeType HelloWorld::rtt;
void HelloWorld::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const ::x10aux::RuntimeType** parents = NULL; 
    rtt.initStageTwo("HelloWorld",::x10aux::RuntimeType::class_kind, 0, parents, 0, NULL, NULL);
}

::x10::lang::String HelloWorld_Strings::sl__17("Hello World!");

/* END of HelloWorld */
/*************************************************/
