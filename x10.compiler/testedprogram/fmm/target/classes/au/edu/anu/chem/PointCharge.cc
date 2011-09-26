/*************************************************/
/* START of PointCharge */
#include <au/edu/anu/chem/PointCharge.h>

#include <x10/lang/Any.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10x/vector/Point3d.h>
#include <x10/lang/Double.h>
#include <x10/lang/String.h>
#include <x10/compiler/Native.h>
#include <x10/compiler/NonEscaping.h>
#include <x10/lang/Boolean.h>
namespace au { namespace edu { namespace anu { namespace chem { 
class PointCharge_ibox0 : public x10::lang::IBox<au::edu::anu::chem::PointCharge> {
public:
    static x10::lang::Any::itable<PointCharge_ibox0 > itable;
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
x10::lang::Any::itable<PointCharge_ibox0 >  PointCharge_ibox0::itable(&PointCharge_ibox0::equals, &PointCharge_ibox0::hashCode, &PointCharge_ibox0::toString, &PointCharge_ibox0::typeName);
} } } } 
x10::lang::Any::itable<au::edu::anu::chem::PointCharge >  au::edu::anu::chem::PointCharge::_itable_0(&au::edu::anu::chem::PointCharge::equals, &au::edu::anu::chem::PointCharge::hashCode, &au::edu::anu::chem::PointCharge::toString, &au::edu::anu::chem::PointCharge::typeName);
x10aux::itable_entry au::edu::anu::chem::PointCharge::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::chem::PointCharge::_itable_0), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::chem::PointCharge>())};
x10aux::itable_entry au::edu::anu::chem::PointCharge::_iboxitables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::chem::PointCharge_ibox0::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::chem::PointCharge>())};

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldDecl_c

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::PointCharge::getOrderedLock(
  ) {
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock((*this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1);
void au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::PointCharge.X10$class_lock_id1");
    x10_int __var340__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var340__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var341__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var341__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::chem::PointCharge.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::chem::PointCharge.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__342 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__deserialize));


//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::PointCharge::getStaticOrderedLock(
  ) {
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId48925 = au::edu::anu::chem::PointCharge::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId48925);
    }))
    ;
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldDecl_c

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldDecl_c

//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::PointCharge::_constructor(
  x10x::vector::Point3d centre,
  x10_double charge) {
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.AssignPropertyCall_c
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::PointCharge this4892648932 =
          (*this);
        {
            
            //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldAssign_c
            this4892648932->FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldAssign_c
    (*this)->FMGL(centre) = centre;
    
    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldAssign_c
    (*this)->FMGL(charge) = charge;
    
}
au::edu::anu::chem::PointCharge au::edu::anu::chem::PointCharge::_make(
  x10x::vector::Point3d centre,
  x10_double charge) {
    au::edu::anu::chem::PointCharge this_; 
    this_->_constructor(centre, charge);
    return this_;
}



//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::PointCharge::_constructor(
  x10x::vector::Point3d centre,
  x10_double charge,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.AssignPropertyCall_c
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::PointCharge this4892948933 =
          (*this);
        {
            
            //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldAssign_c
            this4892948933->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(centre) =
      centre;
    
    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(charge) =
      charge;
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
au::edu::anu::chem::PointCharge au::edu::anu::chem::PointCharge::_make(
  x10x::vector::Point3d centre,
  x10_double charge,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    au::edu::anu::chem::PointCharge this_; 
    this_->_constructor(centre,
    charge,
    paramLock);
    return this_;
}



//#line 31 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::chem::PointCharge::toString(
  ) {
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
    return ((((((x10aux::string_utils::lit("Point charge ")) + ((*this)->
                                                                  FMGL(charge)))) + (x10aux::string_utils::lit(" ")))) + ((*this)->
                                                                                                                            FMGL(centre)));
    
}

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::chem::PointCharge::typeName(
  ){
    return x10aux::type_name((*this));
}

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::chem::PointCharge::hashCode(
  ) {
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10LocalDecl_c
    x10_int result = ((x10_int)1);
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::nullCheck((*this)->
                                                                                             FMGL(centre))->x10x::vector::Point3d::hashCode())));
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(charge)))));
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
    return result;
    
}

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::chem::PointCharge::equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::chem::PointCharge>(other)))
    {
        
        //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
    return (*this)->au::edu::anu::chem::PointCharge::equals(
             x10aux::class_cast<au::edu::anu::chem::PointCharge>(other));
    
}

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::chem::PointCharge::_struct_equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::chem::PointCharge>(other)))
    {
        
        //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
    return (*this)->au::edu::anu::chem::PointCharge::_struct_equals(
             x10aux::class_cast<au::edu::anu::chem::PointCharge>(other));
    
}

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::PointCharge::_serialize(au::edu::anu::chem::PointCharge this_, x10aux::serialization_buffer& buf) {
    buf.write(this_->FMGL(centre));
    buf.write(this_->FMGL(charge));
    
}

void au::edu::anu::chem::PointCharge::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(centre) = buf.read<x10x::vector::Point3d>();
    FMGL(charge) = buf.read<x10_double>();
}


x10aux::RuntimeType au::edu::anu::chem::PointCharge::rtt;
void au::edu::anu::chem::PointCharge::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("au.edu.anu.chem.PointCharge",x10aux::RuntimeType::struct_kind, 2, parents, 0, NULL, NULL);
    rtt.containsPtrs = false;
}
/* END of PointCharge */
/*************************************************/
