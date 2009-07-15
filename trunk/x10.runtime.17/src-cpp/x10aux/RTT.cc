#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/alloc.h>

#include <x10/lang/Object.h>

#include <cstdarg>

using namespace x10aux;
using namespace x10::lang;

bool RuntimeType::subtypeOf(const RuntimeType * const other) const {
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
}
    
void
RuntimeType::bootstrap() {
    /* Initialize RTTs for Object and builtin primitive types */
    ObjectType.init("x10.lang.Object", 0);
    BooleanType.init("x10.lang.Boolean", 1, &ObjectType);
    ByteType.init("x10.lang.Byte", 1, &ObjectType);
    CharType.init("x10.lang.Char", 1, &ObjectType);
    ShortType.init("x10.lang.Short", 1, &ObjectType);
    IntType.init("x10.lang.Int", 1, &ObjectType);
    FloatType.init("x10.lang.Float", 1, &ObjectType);
    LongType.init("x10.lang.Long", 1, &ObjectType);
    DoubleType.init("x10.lang.Double", 1, &ObjectType);
    UByteType.init("x10.lang.UByte", 1, &ObjectType);
    UShortType.init("x10.lang.UShort", 1, &ObjectType);
    UIntType.init("x10.lang.UInt", 1, &ObjectType);
    ULongType.init("x10.lang.ULong", 1, &ObjectType);
}

RuntimeType RuntimeType::ObjectType;
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
