#include <x10aux/RTT.h>

#include <x10/lang/Object.h>

bool x10aux::RuntimeType::instanceOf (
        const x10aux::ref<x10::lang::Object> &other) const {
    return other->_type()->subtypeOf(this);
}

bool x10aux::RuntimeType::concreteInstanceOf (
        const x10aux::ref<x10::lang::Object> &other) const {
    return other->_type()->equals(this);
}


const x10aux::RuntimeType * const x10aux::IntType::it = new x10aux::IntType();
const x10aux::RuntimeType * const x10aux::ShortType::it=new x10aux::ShortType();
const x10aux::RuntimeType * const x10aux::CharType::it = new x10aux::CharType();
