#include <UTS__SHA1Rand.h>


#include "UTS__SHA1Rand.inc"

class UTS__SHA1Rand_ithunk0 : public UTS__SHA1Rand {
public:
    static x10::lang::Any::itable<UTS__SHA1Rand_ithunk0 > itable;
    x10_boolean at(x10aux::ref<x10::lang::Object> arg0) {
        return UTS__SHA1Rand_methods::at(*this, arg0);
    }
    x10_boolean at(x10::lang::Place arg0) {
        return UTS__SHA1Rand_methods::at(*this, arg0);
    }
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return UTS__SHA1Rand_methods::equals(*this, arg0);
    }
    x10_int hashCode() {
        return UTS__SHA1Rand_methods::hashCode(*this);
    }
    x10::lang::Place home() {
        return UTS__SHA1Rand_methods::home(*this);
    }
    x10aux::ref<x10::lang::String> toString() {
        return UTS__SHA1Rand_methods::toString(*this);
    }
    x10aux::ref<x10::lang::String> typeName() {
        return UTS__SHA1Rand_methods::typeName(*this);
    }
    
};
x10::lang::Any::itable<UTS__SHA1Rand_ithunk0 >  UTS__SHA1Rand_ithunk0::itable(&UTS__SHA1Rand_ithunk0::at, &UTS__SHA1Rand_ithunk0::at, &UTS__SHA1Rand_ithunk0::equals, &UTS__SHA1Rand_ithunk0::hashCode, &UTS__SHA1Rand_ithunk0::home, &UTS__SHA1Rand_ithunk0::toString, &UTS__SHA1Rand_ithunk0::typeName);
class UTS__SHA1Rand_iboxithunk0 : public x10::lang::IBox<UTS__SHA1Rand> {
public:
    static x10::lang::Any::itable<UTS__SHA1Rand_iboxithunk0 > itable;
    x10_boolean at(x10aux::ref<x10::lang::Object> arg0) {
        return UTS__SHA1Rand_methods::at(this->value, arg0);
    }
    x10_boolean at(x10::lang::Place arg0) {
        return UTS__SHA1Rand_methods::at(this->value, arg0);
    }
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return UTS__SHA1Rand_methods::equals(this->value, arg0);
    }
    x10_int hashCode() {
        return UTS__SHA1Rand_methods::hashCode(this->value);
    }
    x10::lang::Place home() {
        return UTS__SHA1Rand_methods::home(this->value);
    }
    x10aux::ref<x10::lang::String> toString() {
        return UTS__SHA1Rand_methods::toString(this->value);
    }
    x10aux::ref<x10::lang::String> typeName() {
        return UTS__SHA1Rand_methods::typeName(this->value);
    }
    
};
x10::lang::Any::itable<UTS__SHA1Rand_iboxithunk0 >  UTS__SHA1Rand_iboxithunk0::itable(&UTS__SHA1Rand_iboxithunk0::at, &UTS__SHA1Rand_iboxithunk0::at, &UTS__SHA1Rand_iboxithunk0::equals, &UTS__SHA1Rand_iboxithunk0::hashCode, &UTS__SHA1Rand_iboxithunk0::home, &UTS__SHA1Rand_iboxithunk0::toString, &UTS__SHA1Rand_iboxithunk0::typeName);
x10aux::itable_entry UTS__SHA1Rand::_itables[2] = {x10aux::itable_entry(x10aux::getRTT<x10::lang::Any>(), &UTS__SHA1Rand_ithunk0::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<UTS__SHA1Rand>())};
x10aux::itable_entry UTS__SHA1Rand::_iboxitables[2] = {x10aux::itable_entry(x10aux::getRTT<x10::lang::Any>(), &UTS__SHA1Rand_iboxithunk0::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<UTS__SHA1Rand>())};
void UTS__SHA1Rand_methods::_instance_init(UTS__SHA1Rand& this_) {
    _I_("Doing initialisation for class: UTS__SHA1Rand");
    
}


//#line 7 "/Users/pkambadu/x10-trunk/x10.tests/examples/Benchmarks/UTS/UTS.x10": x10.ast.X10ConstructorDecl_c


//#line 12 "/Users/pkambadu/x10-trunk/x10.tests/examples/Benchmarks/UTS/UTS.x10": x10.ast.X10ConstructorDecl_c


//#line 17 "/Users/pkambadu/x10-trunk/x10.tests/examples/Benchmarks/UTS/UTS.x10": x10.ast.X10MethodDecl_c
x10_boolean UTS__SHA1Rand::_struct_equals(UTS__SHA1Rand that) {
    if (!this->x10::lang::Struct::_struct_equals(that))
        return false;
    return true;
}
void UTS__SHA1Rand::_serialize(UTS__SHA1Rand this_, x10aux::serialization_buffer& buf) {
    x10::lang::Struct::_serialize(this_, buf);
    
}

void UTS__SHA1Rand::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Struct::_deserialize_body(buf);
    
}


x10_boolean UTS__SHA1Rand::equals(x10aux::ref<x10::lang::Any> that) {
    x10aux::ref<x10::lang::Reference> thatAsRef(that);
    if (thatAsRef->_type()->equals(x10aux::getRTT<UTS__SHA1Rand >())) {
        x10aux::ref<x10::lang::IBox<UTS__SHA1Rand > > thatAsIBox(that);
        return _struct_equals(thatAsIBox->value);
    }
    return false;
}

x10_boolean UTS__SHA1Rand::equals(UTS__SHA1Rand that) {
    return _struct_equals(that);
}

x10aux::ref<x10::lang::String> UTS__SHA1Rand::toString() {
    return x10::lang::String::Lit("Struct without toString defined.");
}

x10_int UTS__SHA1Rand::hashCode() {
    x10_int result = 0;
    result = x10::lang::Struct::hashCode();
    return result;
    
}

x10aux::ref<x10::lang::String> UTS__SHA1Rand::typeName() {
    return x10aux::type_name(*this);
    
}

x10aux::RuntimeType UTS__SHA1Rand::rtt;
void UTS__SHA1Rand::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Struct>(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("UTS$SHA1Rand", 2, parents, 0, NULL, NULL);
    rtt.containsPtrs = false;
}
