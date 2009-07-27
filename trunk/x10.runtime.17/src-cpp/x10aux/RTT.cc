#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/alloc.h>
#include <x10aux/atomic_ops.h>

#include <x10/lang/Object.h>

#include <cstdarg>

using namespace x10aux;
using namespace x10::lang;

bool RuntimeType::subtypeOf(const RuntimeType * const other) const {
    // Checks to try to catch partially initialized RTT objects before we use them.
    assert(typeName != NULL);
    assert(other->typeName != NULL);
    assert(parentsc != 0 || this == x10::lang::Object::getRTT());
    assert(other->parentsc != 0 || other == x10::lang::Object::getRTT());

    if (equals(other)) return true; // trivial case
    for (int i = 0; i < parentsc; ++i) {
        if (parents[i]->subtypeOf(other)) return true;
    }
    return false;
}

bool RuntimeType::instanceOf (const ref<Object> &other) const {
    if (other.isNull())
        return false;
    return other->_type()->subtypeOf(this);
}

bool RuntimeType::concreteInstanceOf (const ref<Object> &other) const {
    if (other.isNull())
        return false;
    return other->_type()->equals(this);
}

void RuntimeType::init(const char* n, int pc, ...) {
    parentsc = pc;
    typeName = n;
    parents = alloc<const RuntimeType*>(parentsc * sizeof(const RuntimeType*));
    va_list parentsv;
    va_start(parentsv, pc);
    for (int i=0 ; i<parentsc ; ++i)
        parents[i] = va_arg(parentsv,const RuntimeType*);
    va_end(parentsv);
    x10aux::atomic_ops::store_load_barrier();
}
    
void RuntimeType::initBooleanType() {
    BooleanType.init("x10.lang.Boolean", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initByteType() {
    ByteType.init("x10.lang.Byte", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initCharType() {
    CharType.init("x10.lang.Char", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initShortType() {
    ShortType.init("x10.lang.Short", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initIntType() {
    IntType.init("x10.lang.Int", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initFloatType() {
    FloatType.init("x10.lang.Float", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initLongType() {
    LongType.init("x10.lang.Long", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initDoubleType() {
    DoubleType.init("x10.lang.Double", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initUByteType() {
    UByteType.init("x10.lang.UByte", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initUShortType() {
    UShortType.init("x10.lang.UShort", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initUIntType() {
    UIntType.init("x10.lang.UInt", 1, x10::lang::Object::getRTT());
}
void RuntimeType::initULongType() {
    ULongType.init("x10.lang.ULong", 1, x10::lang::Object::getRTT());
}


RuntimeType RuntimeType::BooleanType;
RuntimeType RuntimeType::ByteType;
RuntimeType RuntimeType::CharType;
RuntimeType RuntimeType::ShortType;
RuntimeType RuntimeType::IntType;
RuntimeType RuntimeType::FloatType;
RuntimeType RuntimeType::LongType;
RuntimeType RuntimeType::DoubleType;
RuntimeType RuntimeType::UByteType;
RuntimeType RuntimeType::UShortType;
RuntimeType RuntimeType::UIntType;
RuntimeType RuntimeType::ULongType;

// vim:tabstop=4:shiftwidth=4:expandtab
