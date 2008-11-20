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

void IntType::init() { initParents(1,getRTT<Object>()); }
void ShortType::init() { initParents(1,getRTT<Object>()); }
void CharType::init() { initParents(1,getRTT<Object>()); }

DEFINE_SPECIAL_RTT(IntType);
DEFINE_SPECIAL_RTT(ShortType);
DEFINE_SPECIAL_RTT(CharType);
// vim:tabstop=4:shiftwidth=4:expandtab
