#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/alloc.h>
#include <x10aux/atomic_ops.h>

#include <x10/lang/Reference.h>
#include <x10/lang/Object.h>

#include <cstdarg>

using namespace x10aux;
using namespace x10::lang;

bool RuntimeType::subtypeOf(const RuntimeType * const other) const {
    // Checks to try to catch partially initialized RTT objects before we use them.
    assert(canonical != NULL);
    assert(other->canonical != NULL);

    if (equals(other)) return true; // trivial case
    if (paramsc > 0 && canonical->equals(other->canonical)) {
        // Different instances of the same generic type (since canonical is equal).
        // this->subtypeOf(other) will be true exactly when the type parameters
        // of have the subtyping relationship specified by the variances.
        assert(other->paramsc == paramsc);
        for (int i=0; i<paramsc; i++) {
            assert(other->variances[i] == variances[i]);
            assert(params[i] != NULL);
            assert(other->params[i] != NULL);
            switch(variances[i]) {
            case covariant:
                if (!params[i]->subtypeOf(other->params[i])) return false;
                break;
            case contravariant:
                if (!other->params[i]->subtypeOf(params[i])) return false;
                break;
            case invariant:
                if (!params[i]->equals(other->params[i])) return false;
                break;
            }
        }
        return true;
    }

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

void RuntimeType::init(const RuntimeType *canonical_, const char* typeName_,
                       int parentsc_, const RuntimeType** parents_,
                       int paramsc_, const RuntimeType** params_,
                       Variance* variances_) {
    canonical = canonical_;
    typeName = typeName_;
    parentsc = parentsc_;
    paramsc = paramsc_;
    containsPtrs = true; // TODO: Eventually the compiler should analyze structs and where possible set containsPtrs for their RTT's to false.
    if (parentsc > 0) {
        parents = alloc<const RuntimeType*>(parentsc * sizeof(const RuntimeType*));
        for (int i=0; i<parentsc; i++) {
            parents[i] = parents_[i];
        }
    } else {
        parents = NULL;
    }
    if (paramsc > 0) {
        params = alloc<const RuntimeType*>(paramsc * sizeof(const RuntimeType*));
        variances = alloc<Variance>(paramsc * sizeof(Variance));
        for (int i=0; i<paramsc; i++) {
            params[i] = params_[i];
            variances[i] = variances_[i];
        }
    } else {
        params = NULL;
        variances = NULL;
    }
    x10aux::atomic_ops::store_load_barrier();
}
    
void RuntimeType::initBooleanType() {
    BooleanType.init(&BooleanType, "x10.lang.Boolean", 0, NULL, 0, NULL, NULL);
    BooleanType.containsPtrs = false;
}
void RuntimeType::initByteType() {
    ByteType.init(&ByteType, "x10.lang.Byte", 0, NULL, 0, NULL, NULL);
    ByteType.containsPtrs = false;
}
void RuntimeType::initCharType() {
    CharType.init(&CharType, "x10.lang.Char", 0, NULL, 0, NULL, NULL);
    CharType.containsPtrs = false;
}
void RuntimeType::initShortType() {
    ShortType.init(&ShortType, "x10.lang.Short", 0, NULL, 0, NULL, NULL);
    ShortType.containsPtrs = false;
}
void RuntimeType::initIntType() {
    IntType.init(&IntType, "x10.lang.Int", 0, NULL, 0, NULL, NULL);
    IntType.containsPtrs = false;
}
void RuntimeType::initFloatType() {
    FloatType.init(&FloatType, "x10.lang.Float", 0, NULL, 0, NULL, NULL);
    FloatType.containsPtrs = false;
}
void RuntimeType::initLongType() {
    LongType.init(&LongType, "x10.lang.Long", 0, NULL, 0, NULL, NULL);
    LongType.containsPtrs = false;
}
void RuntimeType::initDoubleType() {
    DoubleType.init(&DoubleType, "x10.lang.Double", 0, NULL, 0, NULL, NULL);
    DoubleType.containsPtrs = false;
}
void RuntimeType::initUByteType() {
    UByteType.init(&UByteType, "x10.lang.UByte", 0, NULL, 0, NULL, NULL);
    UByteType.containsPtrs = false;
}
void RuntimeType::initUShortType() {
    UShortType.init(&UShortType, "x10.lang.UShort", 0, NULL, 0, NULL, NULL);
    UShortType.containsPtrs = false;
}
void RuntimeType::initUIntType() {
    UIntType.init(&UIntType, "x10.lang.UInt", 0, NULL, 0, NULL, NULL);
    UIntType.containsPtrs = false;
}
void RuntimeType::initULongType() {
    ULongType.init(&ULongType, "x10.lang.ULong", 0, NULL, 0, NULL, NULL);
    ULongType.containsPtrs = false;
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
