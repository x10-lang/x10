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

RuntimeType::RuntimeType(const char* n, int pc, ...) : parentsc(pc) {
    typeName = n;
    parents = alloc<const RuntimeType*>(parentsc * sizeof(const RuntimeType*));
    va_list parentsv;
    va_start(parentsv, pc);
    for (int i=0 ; i<parentsc ; ++i)
        parents[i] = va_arg(parentsv,const RuntimeType*);
    va_end(parentsv);
}
    
const RuntimeType*
RuntimeType::installRTT(const RuntimeType **location, const RuntimeType *rtt) {
    pthread_mutex_lock(&installLock);
    if (NULL == *location) {
        *location = rtt;
    }
    pthread_mutex_unlock(&installLock);
    return *location;
}

void
RuntimeType::bootstrap() {
    /* Initialize mutex used to install RTT objects */
	(void)pthread_mutexattr_init(&installLockAttr);
	pthread_mutexattr_settype(&installLockAttr, PTHREAD_MUTEX_RECURSIVE);
    (void)pthread_mutex_init(&installLock, &installLockAttr);

    /* Initialize RTTs for Object and builtin primitive types */
    ObjectType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Object", 0);
    BooleanType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Boolean", 1, ObjectType);
    ByteType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Byte", 1, ObjectType);
    CharType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Char", 1, ObjectType);
    ShortType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Short", 1, ObjectType);
    IntType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Int", 1, ObjectType);
    FloatType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Float", 1, ObjectType);
    LongType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Long", 1, ObjectType);
    DoubleType = new (alloc<RuntimeType >()) RuntimeType("x10.lang.Double", 1, ObjectType);
}

const RuntimeType*
RuntimeType::allocAndInstallRTT(const RuntimeType **location, const char* name, const RuntimeType *p1) {
    return installRTT(location, new (alloc<RuntimeType >()) RuntimeType(name, 1, p1));
}

const RuntimeType*
RuntimeType::allocAndInstallRTT(const RuntimeType **location, const char* name, const RuntimeType *p1,
                                const RuntimeType *p2) {
    return installRTT(location, new (alloc<RuntimeType >()) RuntimeType(name, 2, p1, p2));
}
    
const RuntimeType*
RuntimeType::allocAndInstallRTT(const RuntimeType **location, const char* name, const RuntimeType *p1,
                                const RuntimeType *p2, const RuntimeType *p3) {
    return installRTT(location, new (alloc<RuntimeType >()) RuntimeType(name, 3, p1, p2, p3));
}

const RuntimeType*
RuntimeType::allocAndInstallRTT(const RuntimeType **location, const char* name, const RuntimeType *p1,
                                const RuntimeType *p2, const RuntimeType *p3, const RuntimeType *p4) {
    return installRTT(location, new (alloc<RuntimeType >()) RuntimeType(name, 4, p1, p2, p3, p4));
}

const RuntimeType*
RuntimeType::allocAndInstallRTT(const RuntimeType **location, const char* name, const RuntimeType *p1,
                                const RuntimeType *p2, const RuntimeType *p3, const RuntimeType *p4,
                                const RuntimeType *p5) {
    return installRTT(location, new (alloc<RuntimeType >()) RuntimeType(name, 5, p1, p2, p3, p4, p5));
}

pthread_mutex_t RuntimeType::installLock;
pthread_mutexattr_t RuntimeType::installLockAttr;

const RuntimeType* RuntimeType::ObjectType = NULL;
const RuntimeType* RuntimeType::BooleanType= NULL;
const RuntimeType* RuntimeType::ByteType = NULL;
const RuntimeType* RuntimeType::CharType = NULL;
const RuntimeType* RuntimeType::ShortType = NULL;
const RuntimeType* RuntimeType::IntType = NULL;
const RuntimeType* RuntimeType::FloatType = NULL;
const RuntimeType* RuntimeType::LongType = NULL;
const RuntimeType* RuntimeType::DoubleType = NULL;

// vim:tabstop=4:shiftwidth=4:expandtab
