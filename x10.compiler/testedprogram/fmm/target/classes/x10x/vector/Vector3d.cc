/*************************************************/
/* START of Vector3d */
#include <x10x/vector/Vector3d.h>


//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.PropertyDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.PropertyDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.PropertyDecl_c
namespace x10x { namespace vector { 
class Vector3d_ibox0 : public x10::lang::IBox<x10x::vector::Vector3d> {
public:
    static x10::lang::Any::itable<Vector3d_ibox0 > itable;
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return this->value->equals(arg0);
    }
    x10_int hashCode() {
        return this->value->hashCode();
    }
    x10aux::ref<x10::lang::String> toString() {
        return this->value->toString();
    }
    x10aux::ref<x10::lang::String> typeName() {
        return this->value->typeName();
    }
    
};
x10::lang::Any::itable<Vector3d_ibox0 >  Vector3d_ibox0::itable(&Vector3d_ibox0::equals, &Vector3d_ibox0::hashCode, &Vector3d_ibox0::toString, &Vector3d_ibox0::typeName);
} } 
x10::lang::Any::itable<x10x::vector::Vector3d >  x10x::vector::Vector3d::_itable_0(&x10x::vector::Vector3d::equals, &x10x::vector::Vector3d::hashCode, &x10x::vector::Vector3d::toString, &x10x::vector::Vector3d::typeName);
namespace x10x { namespace vector { 
class Vector3d_ibox1 : public x10::lang::IBox<x10x::vector::Vector3d> {
public:
    static x10x::vector::Tuple3d::itable<Vector3d_ibox1 > itable;
    x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
        return this->value->equals(arg0);
    }
    x10_int hashCode() {
        return this->value->hashCode();
    }
    x10_double i() {
        return this->value->i();
    }
    x10_double j() {
        return this->value->j();
    }
    x10_double k() {
        return this->value->k();
    }
    x10aux::ref<x10::lang::String> toString() {
        return this->value->toString();
    }
    x10aux::ref<x10::lang::String> typeName() {
        return this->value->typeName();
    }
    
};
x10x::vector::Tuple3d::itable<Vector3d_ibox1 >  Vector3d_ibox1::itable(&Vector3d_ibox1::equals, &Vector3d_ibox1::hashCode, &Vector3d_ibox1::i, &Vector3d_ibox1::j, &Vector3d_ibox1::k, &Vector3d_ibox1::toString, &Vector3d_ibox1::typeName);
} } 
x10x::vector::Tuple3d::itable<x10x::vector::Vector3d >  x10x::vector::Vector3d::_itable_1(&x10x::vector::Vector3d::equals, &x10x::vector::Vector3d::hashCode, &x10x::vector::Vector3d::i, &x10x::vector::Vector3d::j, &x10x::vector::Vector3d::k, &x10x::vector::Vector3d::toString, &x10x::vector::Vector3d::typeName);
x10aux::itable_entry x10x::vector::Vector3d::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &x10x::vector::Vector3d::_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10x::vector::Tuple3d>, &x10x::vector::Vector3d::_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10x::vector::Vector3d>())};
x10aux::itable_entry x10x::vector::Vector3d::_iboxitables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &x10x::vector::Vector3d_ibox0::itable), x10aux::itable_entry(&x10aux::getRTT<x10x::vector::Tuple3d>, &x10x::vector::Vector3d_ibox1::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10x::vector::Vector3d>())};

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> x10x::vector::Vector3d::getOrderedLock(
  ) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock((*this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldDecl_c
x10_int x10x::vector::Vector3d::FMGL(X10__class_lock_id1);
void x10x::vector::Vector3d::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: x10x::vector::Vector3d.X10$class_lock_id1");
    x10_int __var262__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var262__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void x10x::vector::Vector3d::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var263__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var263__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(X10__class_lock_id1__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(X10__class_lock_id1),
                                                                    FMGL(X10__class_lock_id1__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
                                                                       x10aux::StaticInitBroadcastDispatcher::lock();
                                                                       _SI_("WAITING for field: x10x::vector::Vector3d.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: x10x::vector::Vector3d.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__264 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(x10x::vector::Vector3d::FMGL(X10__class_lock_id1__init));

volatile x10aux::status x10x::vector::Vector3d::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> x10x::vector::Vector3d::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    x10x::vector::Vector3d::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t x10x::vector::Vector3d::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(x10x::vector::Vector3d::FMGL(X10__class_lock_id1__deserialize));


//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> x10x::vector::Vector3d::getStaticOrderedLock(
  ) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId48543 = x10x::vector::Vector3d::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId48543);
    }))
    ;
    
}

//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::FMGL(NULL);
void x10x::vector::Vector3d::FMGL(NULL__do_init)() {
    FMGL(NULL__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: x10x::vector::Vector3d.NULL");
    x10x::vector::Vector3d __var266__ = x10x::vector::Vector3d::_make(((x10_double) (((x10_int)0))),
                                                                      ((x10_double) (((x10_int)0))),
                                                                      ((x10_double) (((x10_int)0))),
                                                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(NULL) = __var266__;
    FMGL(NULL__status) = x10aux::INITIALIZED;
}
void x10x::vector::Vector3d::FMGL(NULL__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var267__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(NULL__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var267__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(NULL__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(NULL),
                                                                    FMGL(NULL__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(NULL__status) != x10aux::INITIALIZED) {
                                                        x10aux::StaticInitBroadcastDispatcher::lock();
                                                        _SI_("WAITING for field: x10x::vector::Vector3d.NULL to be initialized");
                                                        while (FMGL(NULL__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                        _SI_("CONTINUING because field: x10x::vector::Vector3d.NULL has been initialized");
                                                        x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__268 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(x10x::vector::Vector3d::FMGL(NULL__init));

volatile x10aux::status x10x::vector::Vector3d::FMGL(NULL__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> x10x::vector::Vector3d::FMGL(NULL__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(NULL) = buf.read<x10x::vector::Vector3d>();
    x10x::vector::Vector3d::FMGL(NULL__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t x10x::vector::Vector3d::FMGL(NULL__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(x10x::vector::Vector3d::FMGL(NULL__deserialize));


//#line 12 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::FMGL(UX);
void x10x::vector::Vector3d::FMGL(UX__do_init)() {
    FMGL(UX__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: x10x::vector::Vector3d.UX");
    x10x::vector::Vector3d __var269__ = x10x::vector::Vector3d::_make(1.0,
                                                                      ((x10_double) (((x10_int)0))),
                                                                      ((x10_double) (((x10_int)0))),
                                                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(UX) = __var269__;
    FMGL(UX__status) = x10aux::INITIALIZED;
}
void x10x::vector::Vector3d::FMGL(UX__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var270__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(UX__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var270__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(UX__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(UX),
                                                                    FMGL(UX__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(UX__status) != x10aux::INITIALIZED) {
                                                      x10aux::StaticInitBroadcastDispatcher::lock();
                                                      _SI_("WAITING for field: x10x::vector::Vector3d.UX to be initialized");
                                                      while (FMGL(UX__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                      _SI_("CONTINUING because field: x10x::vector::Vector3d.UX has been initialized");
                                                      x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__271 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(x10x::vector::Vector3d::FMGL(UX__init));

volatile x10aux::status x10x::vector::Vector3d::FMGL(UX__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> x10x::vector::Vector3d::FMGL(UX__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(UX) = buf.read<x10x::vector::Vector3d>();
    x10x::vector::Vector3d::FMGL(UX__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t x10x::vector::Vector3d::FMGL(UX__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(x10x::vector::Vector3d::FMGL(UX__deserialize));


//#line 13 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::FMGL(UY);
void x10x::vector::Vector3d::FMGL(UY__do_init)() {
    FMGL(UY__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: x10x::vector::Vector3d.UY");
    x10x::vector::Vector3d __var272__ = x10x::vector::Vector3d::_make(((x10_double) (((x10_int)0))),
                                                                      1.0,
                                                                      ((x10_double) (((x10_int)0))),
                                                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(UY) = __var272__;
    FMGL(UY__status) = x10aux::INITIALIZED;
}
void x10x::vector::Vector3d::FMGL(UY__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var273__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(UY__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var273__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(UY__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(UY),
                                                                    FMGL(UY__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(UY__status) != x10aux::INITIALIZED) {
                                                      x10aux::StaticInitBroadcastDispatcher::lock();
                                                      _SI_("WAITING for field: x10x::vector::Vector3d.UY to be initialized");
                                                      while (FMGL(UY__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                      _SI_("CONTINUING because field: x10x::vector::Vector3d.UY has been initialized");
                                                      x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__274 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(x10x::vector::Vector3d::FMGL(UY__init));

volatile x10aux::status x10x::vector::Vector3d::FMGL(UY__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> x10x::vector::Vector3d::FMGL(UY__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(UY) = buf.read<x10x::vector::Vector3d>();
    x10x::vector::Vector3d::FMGL(UY__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t x10x::vector::Vector3d::FMGL(UY__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(x10x::vector::Vector3d::FMGL(UY__deserialize));


//#line 14 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::FMGL(UZ);
void x10x::vector::Vector3d::FMGL(UZ__do_init)() {
    FMGL(UZ__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: x10x::vector::Vector3d.UZ");
    x10x::vector::Vector3d __var275__ = x10x::vector::Vector3d::_make(((x10_double) (((x10_int)0))),
                                                                      ((x10_double) (((x10_int)0))),
                                                                      1.0,
                                                                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(UZ) = __var275__;
    FMGL(UZ__status) = x10aux::INITIALIZED;
}
void x10x::vector::Vector3d::FMGL(UZ__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var276__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(UZ__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var276__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(UZ__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(UZ),
                                                                    FMGL(UZ__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(UZ__status) != x10aux::INITIALIZED) {
                                                      x10aux::StaticInitBroadcastDispatcher::lock();
                                                      _SI_("WAITING for field: x10x::vector::Vector3d.UZ to be initialized");
                                                      while (FMGL(UZ__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                      _SI_("CONTINUING because field: x10x::vector::Vector3d.UZ has been initialized");
                                                      x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__277 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(x10x::vector::Vector3d::FMGL(UZ__init));

volatile x10aux::status x10x::vector::Vector3d::FMGL(UZ__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> x10x::vector::Vector3d::FMGL(UZ__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(UZ) = buf.read<x10x::vector::Vector3d>();
    x10x::vector::Vector3d::FMGL(UZ__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t x10x::vector::Vector3d::FMGL(UZ__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(x10x::vector::Vector3d::FMGL(UZ__deserialize));


//#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorDecl_c


//#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorDecl_c
void x10x::vector::Vector3d::_constructor(
  x10_double i,
  x10_double j,
  x10_double k,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.AssignPropertyCall_c
    FMGL(i) = i;
    FMGL(j) = j;
    FMGL(k) = k;
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d this4854748584 =
          (*this);
        {
            
            //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldAssign_c
            this4854748584->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10x::vector::Vector3d x10x::vector::Vector3d::_make(
  x10_double i,
  x10_double j,
  x10_double k,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10x::vector::Vector3d this_; 
    this_->_constructor(i,
    j,
    k,
    paramLock);
    return this_;
}



//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorDecl_c
void x10x::vector::Vector3d::_constructor(
  x10aux::ref<x10x::vector::Tuple3d> t) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
    ((*this))->::x10x::vector::Vector3d::_constructor(
      x10x::vector::Tuple3d::i(x10aux::nullCheck(t)),
      x10x::vector::Tuple3d::j(x10aux::nullCheck(t)),
      x10x::vector::Tuple3d::k(x10aux::nullCheck(t)));
    
}
x10x::vector::Vector3d x10x::vector::Vector3d::_make(
  x10aux::ref<x10x::vector::Tuple3d> t) {
    x10x::vector::Vector3d this_; 
    this_->_constructor(t);
    return this_;
}



//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorDecl_c
void x10x::vector::Vector3d::_constructor(
  x10aux::ref<x10x::vector::Tuple3d> t,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
    ((*this))->::x10x::vector::Vector3d::_constructor(
      x10x::vector::Tuple3d::i(x10aux::nullCheck(t)),
      x10x::vector::Tuple3d::j(x10aux::nullCheck(t)),
      x10x::vector::Tuple3d::k(x10aux::nullCheck(t)));
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10x::vector::Vector3d x10x::vector::Vector3d::_make(
  x10aux::ref<x10x::vector::Tuple3d> t,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10x::vector::Vector3d this_; 
    this_->_constructor(t,
    paramLock);
    return this_;
}



//#line 24 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 26 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> x10x::vector::Vector3d::toString(
  ) {
    
    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return ((((((((((((x10aux::string_utils::lit("(")) + ((*this)->
                                                            FMGL(i)))) + (x10aux::string_utils::lit("i + ")))) + ((*this)->
                                                                                                                    FMGL(j)))) + (x10aux::string_utils::lit("j + ")))) + ((*this)->
                                                                                                                                                                            FMGL(k)))) + (x10aux::string_utils::lit("k)")));
    
}

//#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::__plus(
  x10x::vector::Vector3d that) {
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d this48558 =
          (*this);
        
        //#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d b48556 = that;
        (__extension__ ({
            
            //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Vector3d alloc2533648557 =
              
            x10x::vector::Vector3d::_alloc();
            
            //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
            (alloc2533648557)->::x10x::vector::Vector3d::_constructor(
              ((this48558->
                  FMGL(i)) + ((__extension__ ({
                  b48556->
                    FMGL(i);
              }))
              )),
              ((this48558->
                  FMGL(j)) + ((__extension__ ({
                  b48556->
                    FMGL(j);
              }))
              )),
              ((this48558->
                  FMGL(k)) + ((__extension__ ({
                  b48556->
                    FMGL(k);
              }))
              )),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            alloc2533648557;
        }))
        ;
    }))
    ;
    
}

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::add(
  x10x::vector::Vector3d b) {
    
    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc25336 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
        (alloc25336)->::x10x::vector::Vector3d::_constructor(
          (((*this)->
              FMGL(i)) + ((__extension__ ({
              b->
                FMGL(i);
          }))
          )),
          (((*this)->
              FMGL(j)) + ((__extension__ ({
              b->
                FMGL(j);
          }))
          )),
          (((*this)->
              FMGL(k)) + ((__extension__ ({
              b->
                FMGL(k);
          }))
          )),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25336;
    }))
    ;
    
}

//#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::__minus(
  x10x::vector::Vector3d x,
  x10x::vector::Vector3d y) {
    
    //#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 48 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d b48559 = y;
        (__extension__ ({
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Vector3d alloc2533748560 =
              
            x10x::vector::Vector3d::_alloc();
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
            (alloc2533748560)->::x10x::vector::Vector3d::_constructor(
              ((x->
                  FMGL(i)) - ((__extension__ ({
                  b48559->
                    FMGL(i);
              }))
              )),
              ((x->
                  FMGL(j)) - ((__extension__ ({
                  b48559->
                    FMGL(j);
              }))
              )),
              ((x->
                  FMGL(k)) - ((__extension__ ({
                  b48559->
                    FMGL(k);
              }))
              )),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            alloc2533748560;
        }))
        ;
    }))
    ;
    
}

//#line 48 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::sub(
  x10x::vector::Vector3d b) {
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc25337 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
        (alloc25337)->::x10x::vector::Vector3d::_constructor(
          (((*this)->
              FMGL(i)) - ((__extension__ ({
              b->
                FMGL(i);
          }))
          )),
          (((*this)->
              FMGL(j)) - ((__extension__ ({
              b->
                FMGL(j);
          }))
          )),
          (((*this)->
              FMGL(k)) - ((__extension__ ({
              b->
                FMGL(k);
          }))
          )),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25337;
    }))
    ;
    
}

//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 63 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::cross(
  x10x::vector::Vector3d vec) {
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc25338 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
        (alloc25338)->::x10x::vector::Vector3d::_constructor(
          (((((*this)->
                FMGL(j)) * (vec->
                              FMGL(k)))) - ((((*this)->
                                                FMGL(k)) * (vec->
                                                              FMGL(j))))),
          (((((*this)->
                FMGL(k)) * (vec->
                              FMGL(i)))) - ((((*this)->
                                                FMGL(i)) * (vec->
                                                              FMGL(k))))),
          (((((*this)->
                FMGL(i)) * (vec->
                              FMGL(j)))) - ((((*this)->
                                                FMGL(j)) * (vec->
                                                              FMGL(i))))),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25338;
    }))
    ;
    
}

//#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::__times(
  x10_double that) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 73 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d this48565 =
          (*this);
        
        //#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10_double c48563 = that;
        (__extension__ ({
            
            //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Vector3d alloc2533948564 =
              
            x10x::vector::Vector3d::_alloc();
            
            //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
            (alloc2533948564)->::x10x::vector::Vector3d::_constructor(
              ((this48565->
                  FMGL(i)) * (c48563)),
              ((this48565->
                  FMGL(j)) * (c48563)),
              ((this48565->
                  FMGL(k)) * (c48563)),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            alloc2533948564;
        }))
        ;
    }))
    ;
    
}

//#line 79 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::__times(
  x10_double x,
  x10x::vector::Vector3d y) {
    
    //#line 79 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10_double that48566 = x;
        (__extension__ ({
            
            //#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
            x10_double c48567 = that48566;
            (__extension__ ({
                
                //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                x10x::vector::Vector3d alloc2533948568 =
                  
                x10x::vector::Vector3d::_alloc();
                
                //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                (alloc2533948568)->::x10x::vector::Vector3d::_constructor(
                  ((y->
                      FMGL(i)) * (c48567)),
                  ((y->
                      FMGL(j)) * (c48567)),
                  ((y->
                      FMGL(k)) * (c48567)),
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                alloc2533948568;
            }))
            ;
        }))
        ;
    }))
    ;
    
}

//#line 81 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::mul(
  x10_double c) {
    
    //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc25339 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
        (alloc25339)->::x10x::vector::Vector3d::_constructor(
          (((*this)->
              FMGL(i)) * (c)),
          (((*this)->
              FMGL(j)) * (c)),
          (((*this)->
              FMGL(k)) * (c)),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25339;
    }))
    ;
    
}

//#line 85 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10_double x10x::vector::Vector3d::mixedProduct(
  x10x::vector::Vector3d v2,
  x10x::vector::Vector3d v3) {
    
    //#line 86 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 86 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d this48572 =
          (*this);
        
        //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d vec48571 =
          (__extension__ ({
            
            //#line 63 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Vector3d vec48569 =
              v3;
            (__extension__ ({
                
                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
                x10x::vector::Vector3d alloc2533848570 =
                  
                x10x::vector::Vector3d::_alloc();
                
                //#line 64 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
                (alloc2533848570)->::x10x::vector::Vector3d::_constructor(
                  ((((v2->
                        FMGL(j)) * (vec48569->
                                      FMGL(k)))) - (((v2->
                                                        FMGL(k)) * (vec48569->
                                                                      FMGL(j))))),
                  ((((v2->
                        FMGL(k)) * (vec48569->
                                      FMGL(i)))) - (((v2->
                                                        FMGL(i)) * (vec48569->
                                                                      FMGL(k))))),
                  ((((v2->
                        FMGL(i)) * (vec48569->
                                      FMGL(j)))) - (((v2->
                                                        FMGL(j)) * (vec48569->
                                                                      FMGL(i))))),
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                alloc2533848570;
            }))
            ;
        }))
        ;
        ((((((this48572->FMGL(i)) * (vec48571->
                                       FMGL(i)))) + (((this48572->
                                                         FMGL(j)) * (vec48571->
                                                                       FMGL(j)))))) + (((this48572->
                                                                                           FMGL(k)) * (vec48571->
                                                                                                         FMGL(k)))));
    }))
    ;
    
}

//#line 89 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10_double x10x::vector::Vector3d::length(
  ) {
    
    //#line 94 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return x10aux::math_utils::sqrt((((((((*this)->
                                            FMGL(i)) * ((*this)->
                                                          FMGL(i)))) + ((((*this)->
                                                                            FMGL(j)) * ((*this)->
                                                                                          FMGL(j)))))) + ((((*this)->
                                                                                                              FMGL(k)) * ((*this)->
                                                                                                                            FMGL(k))))));
    
}

//#line 97 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10_double x10x::vector::Vector3d::maxNorm(
  ) {
    
    //#line 98 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 343 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_double a48575 = (__extension__ ({
            
            //#line 343 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
            x10_double a48573 = ::fabs((*this)->
                                         FMGL(i));
            
            //#line 343 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
            x10_double b48574 = ::fabs((*this)->
                                         FMGL(j));
            ((a48573) < (b48574)) ? (b48574)
              : (a48573);
        }))
        ;
        
        //#line 343 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_double b48576 = ::fabs((*this)->
                                     FMGL(k));
        ((a48575) < (b48576)) ? (b48576) : (a48575);
    }))
    ;
    
}

//#line 102 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10_double x10x::vector::Vector3d::magnitude(
  ) {
    
    //#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return x10aux::math_utils::sqrt((((((((*this)->
                                            FMGL(i)) * ((*this)->
                                                          FMGL(i)))) + ((((*this)->
                                                                            FMGL(j)) * ((*this)->
                                                                                          FMGL(j)))))) + ((((*this)->
                                                                                                              FMGL(k)) * ((*this)->
                                                                                                                            FMGL(k))))));
    
}

//#line 106 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10_double x10x::vector::Vector3d::angleWith(
  x10x::vector::Vector3d vec) {
    
    //#line 107 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
    x10_double aDotb = (__extension__ ({
        
        //#line 107 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d this48578 =
          (*this);
        
        //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d vec48577 =
          vec;
        ((((((this48578->FMGL(i)) * (vec48577->
                                       FMGL(i)))) + (((this48578->
                                                         FMGL(j)) * (vec48577->
                                                                       FMGL(j)))))) + (((this48578->
                                                                                           FMGL(k)) * (vec48577->
                                                                                                         FMGL(k)))));
    }))
    ;
    
    //#line 108 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
    x10_double ab = (((__extension__ ({
        
        //#line 108 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d this48579 =
          (*this);
        x10aux::math_utils::sqrt(((((((this48579->
                                         FMGL(i)) * (this48579->
                                                       FMGL(i)))) + (((this48579->
                                                                         FMGL(j)) * (this48579->
                                                                                       FMGL(j)))))) + (((this48579->
                                                                                                           FMGL(k)) * (this48579->
                                                                                                                         FMGL(k))))));
    }))
    ) * ((__extension__ ({
        x10aux::math_utils::sqrt(((((((vec->
                                         FMGL(i)) * (vec->
                                                       FMGL(i)))) + (((vec->
                                                                         FMGL(j)) * (vec->
                                                                                       FMGL(j)))))) + (((vec->
                                                                                                           FMGL(k)) * (vec->
                                                                                                                         FMGL(k))))));
    }))
    ));
    
    //#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return x10aux::math_utils::acos(((aDotb) / (ab)));
    
}

//#line 113 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::normalize(
  ) {
    
    //#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
    x10_double norm = ((1.0) / ((__extension__ ({
        
        //#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d this48580 =
          (*this);
        x10aux::math_utils::sqrt(((((((this48580->
                                         FMGL(i)) * (this48580->
                                                       FMGL(i)))) + (((this48580->
                                                                         FMGL(j)) * (this48580->
                                                                                       FMGL(j)))))) + (((this48580->
                                                                                                           FMGL(k)) * (this48580->
                                                                                                                         FMGL(k))))));
    }))
    ));
    
    //#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc25340 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 115 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
        (alloc25340)->::x10x::vector::Vector3d::_constructor(
          (((*this)->
              FMGL(i)) * (norm)),
          (((*this)->
              FMGL(j)) * (norm)),
          (((*this)->
              FMGL(k)) * (norm)),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25340;
    }))
    ;
    
}

//#line 121 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::inverse(
  ) {
    
    //#line 122 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
    x10_double l2 = (__extension__ ({
        
        //#line 122 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d this48581 =
          (*this);
        ((((((this48581->FMGL(i)) * (this48581->
                                       FMGL(i)))) + (((this48581->
                                                         FMGL(j)) * (this48581->
                                                                       FMGL(j)))))) + (((this48581->
                                                                                           FMGL(k)) * (this48581->
                                                                                                         FMGL(k)))));
    }))
    ;
    
    //#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc25341 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
        (alloc25341)->::x10x::vector::Vector3d::_constructor(
          (((*this)->
              FMGL(i)) / (l2)),
          (((*this)->
              FMGL(j)) / (l2)),
          (((*this)->
              FMGL(k)) / (l2)),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25341;
    }))
    ;
    
}

//#line 126 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::__minus(
  x10x::vector::Vector3d x) {
    
    //#line 126 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        (__extension__ ({
            
            //#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Vector3d alloc2534248582 =
              
            x10x::vector::Vector3d::_alloc();
            
            //#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
            (alloc2534248582)->::x10x::vector::Vector3d::_constructor(
              (-(x->
                   FMGL(i))),
              (-(x->
                   FMGL(j))),
              (-(x->
                   FMGL(k))),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            alloc2534248582;
        }))
        ;
    }))
    ;
    
}

//#line 128 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10x::vector::Vector3d x10x::vector::Vector3d::negate(
  ) {
    
    //#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Vector3d alloc25342 =
          
        x10x::vector::Vector3d::_alloc();
        
        //#line 129 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10ConstructorCall_c
        (alloc25342)->::x10x::vector::Vector3d::_constructor(
          (-((*this)->
               FMGL(i))),
          (-((*this)->
               FMGL(j))),
          (-((*this)->
               FMGL(k))),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25342;
    }))
    ;
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> x10x::vector::Vector3d::typeName(
  ){
    return x10aux::type_name((*this));
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10_int x10x::vector::Vector3d::hashCode(
  ) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalDecl_c
    x10_int result = ((x10_int)1);
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(i)))));
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(j)))));
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(k)))));
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return result;
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10_boolean x10x::vector::Vector3d::equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<x10x::vector::Vector3d>(other)))
    {
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (*this)->x10x::vector::Vector3d::equals(
             x10aux::class_cast<x10x::vector::Vector3d>(other));
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
x10_boolean x10x::vector::Vector3d::_struct_equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<x10x::vector::Vector3d>(other)))
    {
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10Return_c
    return (*this)->x10x::vector::Vector3d::_struct_equals(
             x10aux::class_cast<x10x::vector::Vector3d>(other));
    
}

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10": x10.ast.X10MethodDecl_c
void x10x::vector::Vector3d::_serialize(x10x::vector::Vector3d this_, x10aux::serialization_buffer& buf) {
    buf.write(this_->FMGL(i));
    buf.write(this_->FMGL(j));
    buf.write(this_->FMGL(k));
    
}

void x10x::vector::Vector3d::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(i) = buf.read<x10_double>();
    FMGL(j) = buf.read<x10_double>();
    FMGL(k) = buf.read<x10_double>();
}


x10aux::RuntimeType x10x::vector::Vector3d::rtt;
void x10x::vector::Vector3d::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[3] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10x::vector::Tuple3d>(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("x10x.vector.Vector3d",x10aux::RuntimeType::struct_kind, 3, parents, 0, NULL, NULL);
    rtt.containsPtrs = false;
}
/* END of Vector3d */
/*************************************************/
