/*************************************************/
/* START of BondType */
#include <au/edu/anu/chem/BondType.h>


//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.PropertyDecl_c

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.PropertyDecl_c
namespace au { namespace edu { namespace anu { namespace chem { 
class BondType_ibox0 : public x10::lang::IBox<au::edu::anu::chem::BondType> {
public:
    static x10::lang::Any::itable<BondType_ibox0 > itable;
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
x10::lang::Any::itable<BondType_ibox0 >  BondType_ibox0::itable(&BondType_ibox0::equals, &BondType_ibox0::hashCode, &BondType_ibox0::toString, &BondType_ibox0::typeName);
} } } } 
x10::lang::Any::itable<au::edu::anu::chem::BondType >  au::edu::anu::chem::BondType::_itable_0(&au::edu::anu::chem::BondType::equals, &au::edu::anu::chem::BondType::hashCode, &au::edu::anu::chem::BondType::toString, &au::edu::anu::chem::BondType::typeName);
x10aux::itable_entry au::edu::anu::chem::BondType::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::chem::BondType::_itable_0), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::chem::BondType>())};
x10aux::itable_entry au::edu::anu::chem::BondType::_iboxitables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::chem::BondType_ibox0::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::chem::BondType>())};

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::BondType::getOrderedLock(
  ) {
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock((*this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1);
void au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.X10$class_lock_id1");
    x10_int __var367__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var367__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var368__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var368__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::chem::BondType.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::chem::BondType.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__369 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__deserialize));


//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::BondType::getStaticOrderedLock(
  ) {
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId52308 = au::edu::anu::chem::BondType::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId52308);
    }))
    ;
    
}

//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::BondType::_constructor(
  x10aux::ref<x10::lang::String> description,
  x10_double bondOrder) {
    
    //#line 18 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.AssignPropertyCall_c
    FMGL(description) = description;
    FMGL(bondOrder) = bondOrder;
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::BondType this5230952315 =
          (*this);
        {
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldAssign_c
            this5230952315->FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
}
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::_make(
  x10aux::ref<x10::lang::String> description,
  x10_double bondOrder) {
    au::edu::anu::chem::BondType this_; 
    this_->_constructor(description, bondOrder);
    return this_;
}



//#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::BondType::_constructor(
  x10aux::ref<x10::lang::String> description,
  x10_double bondOrder,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 18 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.AssignPropertyCall_c
    FMGL(description) = description;
    FMGL(bondOrder) = bondOrder;
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::BondType this5231252316 =
          (*this);
        {
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldAssign_c
            this5231252316->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::_make(
  x10aux::ref<x10::lang::String> description,
  x10_double bondOrder,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    au::edu::anu::chem::BondType this_; 
    this_->_constructor(description,
    bondOrder,
    paramLock);
    return this_;
}



//#line 21 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(WEAK_BOND);
void au::edu::anu::chem::BondType::FMGL(WEAK_BOND__do_init)() {
    FMGL(WEAK_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.WEAK_BOND");
    au::edu::anu::chem::BondType __var371__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("Weak bond"),
                                          0.0,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(WEAK_BOND) = __var371__;
    FMGL(WEAK_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(WEAK_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var372__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(WEAK_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var372__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(WEAK_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(WEAK_BOND),
                                                                    FMGL(WEAK_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(WEAK_BOND__status) != x10aux::INITIALIZED) {
                                                             x10aux::StaticInitBroadcastDispatcher::lock();
                                                             _SI_("WAITING for field: au::edu::anu::chem::BondType.WEAK_BOND to be initialized");
                                                             while (FMGL(WEAK_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                             _SI_("CONTINUING because field: au::edu::anu::chem::BondType.WEAK_BOND has been initialized");
                                                             x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__373 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(WEAK_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(WEAK_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(WEAK_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(WEAK_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(WEAK_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(WEAK_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(WEAK_BOND__deserialize));


//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(NO_BOND);
void au::edu::anu::chem::BondType::FMGL(NO_BOND__do_init)() {
    FMGL(NO_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.NO_BOND");
    au::edu::anu::chem::BondType __var374__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("No bond"),
                                          0.0,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(NO_BOND) = __var374__;
    FMGL(NO_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(NO_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var375__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(NO_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var375__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(NO_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(NO_BOND),
                                                                    FMGL(NO_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(NO_BOND__status) != x10aux::INITIALIZED) {
                                                           x10aux::StaticInitBroadcastDispatcher::lock();
                                                           _SI_("WAITING for field: au::edu::anu::chem::BondType.NO_BOND to be initialized");
                                                           while (FMGL(NO_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                           _SI_("CONTINUING because field: au::edu::anu::chem::BondType.NO_BOND has been initialized");
                                                           x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__376 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(NO_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(NO_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(NO_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(NO_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(NO_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(NO_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(NO_BOND__deserialize));


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(SINGLE_BOND);
void au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__do_init)() {
    FMGL(SINGLE_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.SINGLE_BOND");
    au::edu::anu::chem::BondType __var377__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("Single bond"),
                                          1.0,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(SINGLE_BOND) = __var377__;
    FMGL(SINGLE_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var378__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(SINGLE_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var378__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(SINGLE_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(SINGLE_BOND),
                                                                    FMGL(SINGLE_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(SINGLE_BOND__status) != x10aux::INITIALIZED) {
                                                               x10aux::StaticInitBroadcastDispatcher::lock();
                                                               _SI_("WAITING for field: au::edu::anu::chem::BondType.SINGLE_BOND to be initialized");
                                                               while (FMGL(SINGLE_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                               _SI_("CONTINUING because field: au::edu::anu::chem::BondType.SINGLE_BOND has been initialized");
                                                               x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__379 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(SINGLE_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__deserialize));


//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND);
void au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__do_init)() {
    FMGL(DOUBLE_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.DOUBLE_BOND");
    au::edu::anu::chem::BondType __var380__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("Double bond"),
                                          2.0,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(DOUBLE_BOND) = __var380__;
    FMGL(DOUBLE_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var381__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(DOUBLE_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var381__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(DOUBLE_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(DOUBLE_BOND),
                                                                    FMGL(DOUBLE_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(DOUBLE_BOND__status) != x10aux::INITIALIZED) {
                                                               x10aux::StaticInitBroadcastDispatcher::lock();
                                                               _SI_("WAITING for field: au::edu::anu::chem::BondType.DOUBLE_BOND to be initialized");
                                                               while (FMGL(DOUBLE_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                               _SI_("CONTINUING because field: au::edu::anu::chem::BondType.DOUBLE_BOND has been initialized");
                                                               x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__382 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(DOUBLE_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__deserialize));


//#line 25 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND);
void au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__do_init)() {
    FMGL(TRIPLE_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.TRIPLE_BOND");
    au::edu::anu::chem::BondType __var383__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("Triple bond"),
                                          3.0,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(TRIPLE_BOND) = __var383__;
    FMGL(TRIPLE_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var384__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(TRIPLE_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var384__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(TRIPLE_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(TRIPLE_BOND),
                                                                    FMGL(TRIPLE_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(TRIPLE_BOND__status) != x10aux::INITIALIZED) {
                                                               x10aux::StaticInitBroadcastDispatcher::lock();
                                                               _SI_("WAITING for field: au::edu::anu::chem::BondType.TRIPLE_BOND to be initialized");
                                                               while (FMGL(TRIPLE_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                               _SI_("CONTINUING because field: au::edu::anu::chem::BondType.TRIPLE_BOND has been initialized");
                                                               x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__385 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(TRIPLE_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__deserialize));


//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND);
void au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__do_init)() {
    FMGL(QUADRUPLE_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.QUADRUPLE_BOND");
    au::edu::anu::chem::BondType __var386__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("Quadruple bond"),
                                          4.0,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(QUADRUPLE_BOND) = __var386__;
    FMGL(QUADRUPLE_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var387__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(QUADRUPLE_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var387__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(QUADRUPLE_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(QUADRUPLE_BOND),
                                                                    FMGL(QUADRUPLE_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(QUADRUPLE_BOND__status) != x10aux::INITIALIZED) {
                                                                  x10aux::StaticInitBroadcastDispatcher::lock();
                                                                  _SI_("WAITING for field: au::edu::anu::chem::BondType.QUADRUPLE_BOND to be initialized");
                                                                  while (FMGL(QUADRUPLE_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                  _SI_("CONTINUING because field: au::edu::anu::chem::BondType.QUADRUPLE_BOND has been initialized");
                                                                  x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__388 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(QUADRUPLE_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__deserialize));


//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND);
void au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__do_init)() {
    FMGL(AROMATIC_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.AROMATIC_BOND");
    au::edu::anu::chem::BondType __var389__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("Aromatic bond"),
                                          1.5,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(AROMATIC_BOND) = __var389__;
    FMGL(AROMATIC_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var390__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(AROMATIC_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var390__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(AROMATIC_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(AROMATIC_BOND),
                                                                    FMGL(AROMATIC_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(AROMATIC_BOND__status) != x10aux::INITIALIZED) {
                                                                 x10aux::StaticInitBroadcastDispatcher::lock();
                                                                 _SI_("WAITING for field: au::edu::anu::chem::BondType.AROMATIC_BOND to be initialized");
                                                                 while (FMGL(AROMATIC_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                 _SI_("CONTINUING because field: au::edu::anu::chem::BondType.AROMATIC_BOND has been initialized");
                                                                 x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__391 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(AROMATIC_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__deserialize));


//#line 28 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(AMIDE_BOND);
void au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__do_init)() {
    FMGL(AMIDE_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.AMIDE_BOND");
    au::edu::anu::chem::BondType __var392__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("Amide bond"),
                                          1.41,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(AMIDE_BOND) = __var392__;
    FMGL(AMIDE_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var393__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(AMIDE_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var393__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(AMIDE_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(AMIDE_BOND),
                                                                    FMGL(AMIDE_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(AMIDE_BOND__status) != x10aux::INITIALIZED) {
                                                              x10aux::StaticInitBroadcastDispatcher::lock();
                                                              _SI_("WAITING for field: au::edu::anu::chem::BondType.AMIDE_BOND to be initialized");
                                                              while (FMGL(AMIDE_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                              _SI_("CONTINUING because field: au::edu::anu::chem::BondType.AMIDE_BOND has been initialized");
                                                              x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__394 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(AMIDE_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__deserialize));


//#line 29 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldDecl_c
au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(IONIC_BOND);
void au::edu::anu::chem::BondType::FMGL(IONIC_BOND__do_init)() {
    FMGL(IONIC_BOND__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::BondType.IONIC_BOND");
    au::edu::anu::chem::BondType __var395__ =
      au::edu::anu::chem::BondType::_make(x10aux::string_utils::lit("Ionic bond"),
                                          0.0,
                                          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    FMGL(IONIC_BOND) = __var395__;
    FMGL(IONIC_BOND__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::BondType::FMGL(IONIC_BOND__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var396__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(IONIC_BOND__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var396__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(IONIC_BOND__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(IONIC_BOND),
                                                                    FMGL(IONIC_BOND__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(IONIC_BOND__status) != x10aux::INITIALIZED) {
                                                              x10aux::StaticInitBroadcastDispatcher::lock();
                                                              _SI_("WAITING for field: au::edu::anu::chem::BondType.IONIC_BOND to be initialized");
                                                              while (FMGL(IONIC_BOND__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                              _SI_("CONTINUING because field: au::edu::anu::chem::BondType.IONIC_BOND has been initialized");
                                                              x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__397 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::BondType::FMGL(IONIC_BOND__init));

volatile x10aux::status au::edu::anu::chem::BondType::FMGL(IONIC_BOND__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::BondType::FMGL(IONIC_BOND__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(IONIC_BOND) = buf.read<au::edu::anu::chem::BondType>();
    au::edu::anu::chem::BondType::FMGL(IONIC_BOND__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::BondType::FMGL(IONIC_BOND__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::BondType::FMGL(IONIC_BOND__deserialize));


//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c

//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::chem::BondType::typeName(
  ){
    return x10aux::type_name((*this));
}

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::chem::BondType::hashCode(
  ) {
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10LocalDecl_c
    x10_int result = ((x10_int)1);
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(description)))));
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(bondOrder)))));
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
    return result;
    
}

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::chem::BondType::equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::chem::BondType>(other)))
    {
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
    return (*this)->au::edu::anu::chem::BondType::equals(
             x10aux::class_cast<au::edu::anu::chem::BondType>(other));
    
}

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::chem::BondType::_struct_equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::chem::BondType>(other)))
    {
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
    return (*this)->au::edu::anu::chem::BondType::_struct_equals(
             x10aux::class_cast<au::edu::anu::chem::BondType>(other));
    
}

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::BondType::_serialize(au::edu::anu::chem::BondType this_, x10aux::serialization_buffer& buf) {
    buf.write(this_->FMGL(description));
    buf.write(this_->FMGL(bondOrder));
    
}

void au::edu::anu::chem::BondType::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(description) = buf.read<x10aux::ref<x10::lang::String> >();
    FMGL(bondOrder) = buf.read<x10_double>();
}


x10aux::RuntimeType au::edu::anu::chem::BondType::rtt;
void au::edu::anu::chem::BondType::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("au.edu.anu.chem.BondType",x10aux::RuntimeType::struct_kind, 2, parents, 0, NULL, NULL);
}
/* END of BondType */
/*************************************************/
