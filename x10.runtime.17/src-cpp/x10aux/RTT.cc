#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Object.h>

using namespace x10aux;
using namespace x10::lang;

bool x10aux::RuntimeType::instanceOf (ref<Object> other) const {
    return other->_type()->subtypeOf(this);
}

bool x10aux::RuntimeType::concreteInstanceOf (ref<Object> other) const {
    return other->_type()->equals(this);
}

void x10aux::primitive_init(x10aux::RuntimeType* t) {
    t->initParents(1, getRTT<x10::lang::Object>());
}

DEFINE_PRIMITIVE_RTT(Boolean);
DEFINE_PRIMITIVE_RTT(Byte);
DEFINE_PRIMITIVE_RTT(Char);
DEFINE_PRIMITIVE_RTT(Short);
DEFINE_PRIMITIVE_RTT(Int);
DEFINE_PRIMITIVE_RTT(Long);
DEFINE_PRIMITIVE_RTT(Float);
DEFINE_PRIMITIVE_RTT(Double);

// vim:tabstop=4:shiftwidth=4:expandtab
