#include <x10aux/RTT.h>

#include <x10/lang/Object.h>

using namespace x10aux;
using namespace x10::lang;

bool x10aux::RuntimeType::instanceOf (const ref<Object> &other) const {
    return other->_type()->subtypeOf(this);
}

bool x10aux::RuntimeType::concreteInstanceOf (const ref<Object> &other) const {
    return other->_type()->equals(this);
}

IntType::IntType() : RuntimeType(1,getRTT<Object>()) { }
ShortType::ShortType() : RuntimeType(1,getRTT<Object>()) { }
CharType::CharType() : RuntimeType(1,getRTT<Object>()) { }

const x10aux::RuntimeType * const x10aux::IntType::it = new x10aux::IntType();
const x10aux::RuntimeType * const x10aux::ShortType::it=new x10aux::ShortType();
const x10aux::RuntimeType * const x10aux::CharType::it = new x10aux::CharType();
