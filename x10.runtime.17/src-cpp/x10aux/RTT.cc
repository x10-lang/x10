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

void BooleanType::init() { initParents(1,getRTT<Object>()); }
void ByteType::init() { initParents(1,getRTT<Object>()); }
void CharType::init() { initParents(1,getRTT<Object>()); }
void ShortType::init() { initParents(1,getRTT<Object>()); }
void IntType::init() { initParents(1,getRTT<Object>()); }
void LongType::init() { initParents(1,getRTT<Object>()); }
void FloatType::init() { initParents(1,getRTT<Object>()); }
void DoubleType::init() { initParents(1,getRTT<Object>()); }

#define PRIMITIVE_RTT(RTTNAME,PRIMNAME) \
template<> const RuntimeType *getRTT<PRIMNAME>() { return RTTNAME::it; }

PRIMITIVE_RTT(BooleanType,x10_boolean)
PRIMITIVE_RTT(ByteType,x10_byte)
PRIMITIVE_RTT(CharType,x10_char)
PRIMITIVE_RTT(ShortType,x10_short)
PRIMITIVE_RTT(IntType,x10_int)
PRIMITIVE_RTT(LongType,x10_long)
PRIMITIVE_RTT(FloatType,x10_float)
PRIMITIVE_RTT(DoubleType,x10_double)

DEFINE_SPECIAL_RTT(BooleanType);
DEFINE_SPECIAL_RTT(ByteType);
DEFINE_SPECIAL_RTT(CharType);
DEFINE_SPECIAL_RTT(ShortType);
DEFINE_SPECIAL_RTT(IntType);
DEFINE_SPECIAL_RTT(LongType);
DEFINE_SPECIAL_RTT(FloatType);
DEFINE_SPECIAL_RTT(DoubleType);

// vim:tabstop=4:shiftwidth=4:expandtab
