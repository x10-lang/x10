/*************************************************/
/* START of MMAtom */
#include <au/edu/anu/chem/mm/MMAtom.h>

#include <au/edu/anu/chem/Atom.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10x/vector/Vector3d.h>
#include <x10/lang/Double.h>
#include <x10/lang/String.h>
#include <x10x/vector/Point3d.h>
#include <x10/lang/Math.h>
#include <au/edu/anu/chem/mm/MMAtom__PackedRepresentation.h>
#include <x10/lang/Zero.h>

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldDecl_c

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::mm::MMAtom::getOrderedLock(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1);
void au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::mm::MMAtom.X10$class_lock_id1");
    x10_int __var36__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var36__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var37__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var37__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::chem::mm::MMAtom.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::chem::mm::MMAtom.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__38 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__deserialize));


//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::chem::mm::MMAtom::getStaticOrderedLock(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId25594 = au::edu::anu::chem::mm::MMAtom::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId25594);
    }))
    ;
    
}

//#line 26 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldDecl_c
/** The current force acting upon this atom. */
                                               //#line 29 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldDecl_c
                                               /** The current velocity of this atom. */
                                                                                        //#line 32 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldDecl_c
                                                                                        /** The effective charge in atomic units. */

//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom::_constructor(
  x10aux::ref<x10::lang::String> symbol,
  x10x::vector::Point3d centre,
  x10_double charge) {
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::chem::Atom>)this))->::au::edu::anu::chem::Atom::_constructor(
      symbol,
      centre);
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->au::edu::anu::chem::mm::MMAtom::__fieldInitializers25366();
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(charge) = charge;
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(force) = x10x::vector::Vector3d::
                      FMGL(NULL__get)();
    
    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(velocity) = x10x::vector::Vector3d::
                         FMGL(NULL__get)();
    
}
x10aux::ref<au::edu::anu::chem::mm::MMAtom> au::edu::anu::chem::mm::MMAtom::_make(
  x10aux::ref<x10::lang::String> symbol,
  x10x::vector::Point3d centre,
  x10_double charge) {
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    this_->_constructor(symbol, centre, charge);
    return this_;
}



//#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom::_constructor(
  x10aux::ref<x10::lang::String> symbol,
  x10x::vector::Point3d centre,
  x10_double charge,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::chem::Atom>)this))->::au::edu::anu::chem::Atom::_constructor(
      symbol,
      centre);
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->au::edu::anu::chem::mm::MMAtom::__fieldInitializers25366();
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(charge) =
      charge;
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(force) =
      x10x::vector::Vector3d::
        FMGL(NULL__get)();
    
    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(velocity) =
      x10x::vector::Vector3d::
        FMGL(NULL__get)();
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::chem::mm::MMAtom> au::edu::anu::chem::mm::MMAtom::_make(
  x10aux::ref<x10::lang::String> symbol,
  x10x::vector::Point3d centre,
  x10_double charge,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    this_->_constructor(symbol,
    centre,
    charge,
    paramLock);
    return this_;
}



//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom::_constructor(
  x10x::vector::Point3d centre,
  x10_double charge) {
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::chem::Atom>)this))->::au::edu::anu::chem::Atom::_constructor(
      centre);
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->au::edu::anu::chem::mm::MMAtom::__fieldInitializers25366();
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(charge) = charge;
    
    //#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(force) = x10x::vector::Vector3d::
                      FMGL(NULL__get)();
    
    //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(velocity) = x10x::vector::Vector3d::
                         FMGL(NULL__get)();
    
}
x10aux::ref<au::edu::anu::chem::mm::MMAtom> au::edu::anu::chem::mm::MMAtom::_make(
  x10x::vector::Point3d centre,
  x10_double charge) {
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    this_->_constructor(centre, charge);
    return this_;
}



//#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom::_constructor(
  x10x::vector::Point3d centre,
  x10_double charge,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::chem::Atom>)this))->::au::edu::anu::chem::Atom::_constructor(
      centre);
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->au::edu::anu::chem::mm::MMAtom::__fieldInitializers25366();
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(charge) =
      charge;
    
    //#line 44 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(force) =
      x10x::vector::Vector3d::
        FMGL(NULL__get)();
    
    //#line 45 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(velocity) =
      x10x::vector::Vector3d::
        FMGL(NULL__get)();
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::chem::mm::MMAtom> au::edu::anu::chem::mm::MMAtom::_make(
  x10x::vector::Point3d centre,
  x10_double charge,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    this_->_constructor(centre,
    charge,
    paramLock);
    return this_;
}



//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom::_constructor(
  x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom)
{
    
    //#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::chem::Atom>)this))->::au::edu::anu::chem::Atom::_constructor(
      x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::chem::Atom> >(atom));
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->au::edu::anu::chem::mm::MMAtom::__fieldInitializers25366();
    
    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(charge) =
      x10aux::nullCheck(atom)->
        FMGL(charge);
    
    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(force) =
      x10aux::nullCheck(atom)->
        FMGL(force);
    
    //#line 56 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(velocity) =
      x10aux::nullCheck(atom)->
        FMGL(velocity);
    
}
x10aux::ref<au::edu::anu::chem::mm::MMAtom> au::edu::anu::chem::mm::MMAtom::_make(
  x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom)
{
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    this_->_constructor(atom);
    return this_;
}



//#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom::_constructor(
  x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::chem::Atom>)this))->::au::edu::anu::chem::Atom::_constructor(
      x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::chem::Atom> >(atom));
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->au::edu::anu::chem::mm::MMAtom::__fieldInitializers25366();
    
    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(charge) =
      x10aux::nullCheck(atom)->
        FMGL(charge);
    
    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(force) =
      x10aux::nullCheck(atom)->
        FMGL(force);
    
    //#line 56 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(velocity) =
      x10aux::nullCheck(atom)->
        FMGL(velocity);
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::chem::mm::MMAtom> au::edu::anu::chem::mm::MMAtom::_make(
  x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    this_->_constructor(atom,
    paramLock);
    return this_;
}



//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom::_constructor(
  x10x::vector::Point3d centre) {
    
    //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this))->::au::edu::anu::chem::mm::MMAtom::_constructor(
      centre,
      0.0);
    
}
x10aux::ref<au::edu::anu::chem::mm::MMAtom> au::edu::anu::chem::mm::MMAtom::_make(
  x10x::vector::Point3d centre) {
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    this_->_constructor(centre);
    return this_;
}



//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom::_constructor(
  x10x::vector::Point3d centre,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this))->::au::edu::anu::chem::mm::MMAtom::_constructor(
      centre,
      0.0);
    
    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::chem::mm::MMAtom> au::edu::anu::chem::mm::MMAtom::_make(
  x10x::vector::Point3d centre,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    this_->_constructor(centre,
    paramLock);
    return this_;
}



//#line 63 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10_double au::edu::anu::chem::mm::MMAtom::pairEnergy(
  x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom2) {
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return ((((((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
                 FMGL(charge)) * (x10aux::nullCheck(atom2)->
                                    FMGL(charge)))) / ((__extension__ ({
        
        //#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d this25742 =
          ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
            FMGL(centre);
        
        //#line 66 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d b25741 = x10aux::nullCheck(atom2)->
                                         FMGL(centre);
        x10aux::math_utils::sqrt((__extension__ ({
            
            //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Point3d b25743 =
              b25741;
            
            //#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10_double ret25747;
            {
                
                //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                x10_double di25744 = ((this25742->
                                         FMGL(i)) - (b25743->
                                                       FMGL(i)));
                
                //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                x10_double dj25745 = ((this25742->
                                         FMGL(j)) - (b25743->
                                                       FMGL(j)));
                
                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
                x10_double dk25746 = ((this25742->
                                         FMGL(k)) - (b25743->
                                                       FMGL(k)));
                
                //#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalAssign_c
                ret25747 = ((((((di25744) * (di25744))) + (((dj25745) * (dj25745))))) + (((dk25746) * (dk25746))));
            }
            ret25747;
        }))
        );
    }))
    ));
    
}

//#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
au::edu::anu::chem::mm::MMAtom__PackedRepresentation
  au::edu::anu::chem::mm::MMAtom::getPackedRepresentation(
  ) {
    
    //#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::mm::MMAtom__PackedRepresentation alloc25593 =
          
        au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_alloc();
        
        //#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorCall_c
        (alloc25593)->::au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_constructor(
          ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
            FMGL(symbol),
          ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
            FMGL(charge),
          ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
            FMGL(centre),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc25593;
    }))
    ;
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::chem::mm::MMAtom>
  au::edu::anu::chem::mm::MMAtom::au__edu__anu__chem__mm__MMAtom____au__edu__anu__chem__mm__MMAtom__this(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this);
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::mm::MMAtom::__fieldInitializers25366(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(force) = x10aux::zeroValue<x10x::vector::Vector3d >();
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::mm::MMAtom>)this)->
      FMGL(velocity) = x10aux::zeroValue<x10x::vector::Vector3d >();
}
const x10aux::serialization_id_t au::edu::anu::chem::mm::MMAtom::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::chem::mm::MMAtom::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::chem::mm::MMAtom::_serialize_body(x10aux::serialization_buffer& buf) {
    au::edu::anu::chem::Atom::_serialize_body(buf);
    buf.write(this->FMGL(force));
    buf.write(this->FMGL(velocity));
    buf.write(this->FMGL(charge));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::MMAtom::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::chem::mm::MMAtom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::mm::MMAtom>(), 0, sizeof(au::edu::anu::chem::mm::MMAtom))) au::edu::anu::chem::mm::MMAtom();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::chem::mm::MMAtom::_deserialize_body(x10aux::deserialization_buffer& buf) {
    au::edu::anu::chem::Atom::_deserialize_body(buf);
    FMGL(force) = buf.read<x10x::vector::Vector3d>();
    FMGL(velocity) = buf.read<x10x::vector::Vector3d>();
    FMGL(charge) = buf.read<x10_double>();
}

x10aux::RuntimeType au::edu::anu::chem::mm::MMAtom::rtt;
void au::edu::anu::chem::mm::MMAtom::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<au::edu::anu::chem::Atom>()};
    rtt.initStageTwo("au.edu.anu.chem.mm.MMAtom",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
/* END of MMAtom */
/*************************************************/
/*************************************************/
/* START of MMAtom$PackedRepresentation */
#include <au/edu/anu/chem/mm/MMAtom__PackedRepresentation.h>

#include <x10/lang/Any.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/String.h>
#include <x10/lang/Double.h>
#include <x10x/vector/Point3d.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/compiler/Native.h>
#include <x10/compiler/NonEscaping.h>
#include <x10/lang/Boolean.h>

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.PropertyDecl_c

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.PropertyDecl_c

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.PropertyDecl_c
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class MMAtom__PackedRepresentation_ibox0 : public x10::lang::IBox<au::edu::anu::chem::mm::MMAtom__PackedRepresentation> {
public:
    static x10::lang::Any::itable<MMAtom__PackedRepresentation_ibox0 > itable;
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
x10::lang::Any::itable<MMAtom__PackedRepresentation_ibox0 >  MMAtom__PackedRepresentation_ibox0::itable(&MMAtom__PackedRepresentation_ibox0::equals, &MMAtom__PackedRepresentation_ibox0::hashCode, &MMAtom__PackedRepresentation_ibox0::toString, &MMAtom__PackedRepresentation_ibox0::typeName);
} } } } } 
x10::lang::Any::itable<au::edu::anu::chem::mm::MMAtom__PackedRepresentation >  au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_itable_0(&au::edu::anu::chem::mm::MMAtom__PackedRepresentation::equals, &au::edu::anu::chem::mm::MMAtom__PackedRepresentation::hashCode, &au::edu::anu::chem::mm::MMAtom__PackedRepresentation::toString, &au::edu::anu::chem::mm::MMAtom__PackedRepresentation::typeName);
x10aux::itable_entry au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_itable_0), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::chem::mm::MMAtom__PackedRepresentation>())};
x10aux::itable_entry au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_iboxitables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &au::edu::anu::chem::mm::MMAtom__PackedRepresentation_ibox0::itable), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::chem::mm::MMAtom__PackedRepresentation>())};

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldDecl_c

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::mm::MMAtom__PackedRepresentation::getOrderedLock(
  ) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock((*this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1);
void au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::mm::MMAtom__PackedRepresentation.X10$class_lock_id1");
    x10_int __var45__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var45__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var46__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var46__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::chem::mm::MMAtom__PackedRepresentation.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::chem::mm::MMAtom__PackedRepresentation.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__47 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::mm::MMAtom__PackedRepresentation::FMGL(X10__class_lock_id1__deserialize));


//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::mm::MMAtom__PackedRepresentation::getStaticOrderedLock(
  ) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId25749 = au::edu::anu::chem::mm::MMAtom__PackedRepresentation::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId25749);
    }))
    ;
    
}

//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_constructor(
  x10aux::ref<x10::lang::String> symbol,
  x10_double charge,
  x10x::vector::Point3d centre) {
    
    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.AssignPropertyCall_c
    FMGL(symbol) = symbol;
    FMGL(charge) = charge;
    FMGL(centre) = centre;
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::mm::MMAtom__PackedRepresentation this2575025756 =
          (*this);
        {
            
            //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
            this2575025756->FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
}
au::edu::anu::chem::mm::MMAtom__PackedRepresentation au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_make(
  x10aux::ref<x10::lang::String> symbol,
  x10_double charge,
  x10x::vector::Point3d centre) {
    au::edu::anu::chem::mm::MMAtom__PackedRepresentation this_; 
    this_->_constructor(symbol, charge, centre);
    return this_;
}



//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_constructor(
  x10aux::ref<x10::lang::String> symbol,
  x10_double charge,
  x10x::vector::Point3d centre,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.AssignPropertyCall_c
    FMGL(symbol) = symbol;
    FMGL(charge) = charge;
    FMGL(centre) = centre;
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10LocalDecl_c
        au::edu::anu::chem::mm::MMAtom__PackedRepresentation this2575325757 =
          (*this);
        {
            
            //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
            this2575325757->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10FieldAssign_c
    (*this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
au::edu::anu::chem::mm::MMAtom__PackedRepresentation au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_make(
  x10aux::ref<x10::lang::String> symbol,
  x10_double charge,
  x10x::vector::Point3d centre,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    au::edu::anu::chem::mm::MMAtom__PackedRepresentation this_; 
    this_->_constructor(symbol,
    charge,
    centre,
    paramLock);
    return this_;
}



//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::chem::mm::MMAtom__PackedRepresentation::typeName(
  ){
    return x10aux::type_name((*this));
}

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::chem::mm::MMAtom__PackedRepresentation::toString(
  ) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return ((((((((((((x10aux::string_utils::lit("struct au.edu.anu.chem.mm.MMAtom.PackedRepresentation:")) + (x10aux::string_utils::lit(" symbol=")))) + ((*this)->
                                                                                                                                                             FMGL(symbol)))) + (x10aux::string_utils::lit(" charge=")))) + ((*this)->
                                                                                                                                                                                                                              FMGL(charge)))) + (x10aux::string_utils::lit(" centre=")))) + ((*this)->
                                                                                                                                                                                                                                                                                               FMGL(centre)));
    
}

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::chem::mm::MMAtom__PackedRepresentation::hashCode(
  ) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10LocalDecl_c
    x10_int result = ((x10_int)1);
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(symbol)))));
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::hash_code((*this)->
                                                                                             FMGL(charge)))));
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10LocalAssign_c
    result = ((x10_int) ((((x10_int) ((((x10_int)8191)) * (result)))) + (x10aux::nullCheck((*this)->
                                                                                             FMGL(centre))->x10x::vector::Point3d::hashCode())));
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return result;
    
}

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::chem::mm::MMAtom__PackedRepresentation::equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::chem::mm::MMAtom__PackedRepresentation>(other)))
    {
        
        //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return (*this)->au::edu::anu::chem::mm::MMAtom__PackedRepresentation::equals(
             x10aux::class_cast<au::edu::anu::chem::mm::MMAtom__PackedRepresentation>(other));
    
}

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_struct_equals(
  x10aux::ref<x10::lang::Any> other) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<au::edu::anu::chem::mm::MMAtom__PackedRepresentation>(other)))
    {
        
        //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10Return_c
    return (*this)->au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_struct_equals(
             x10aux::class_cast<au::edu::anu::chem::mm::MMAtom__PackedRepresentation>(other));
    
}

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/MMAtom.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_serialize(au::edu::anu::chem::mm::MMAtom__PackedRepresentation this_, x10aux::serialization_buffer& buf) {
    buf.write(this_->FMGL(symbol));
    buf.write(this_->FMGL(charge));
    buf.write(this_->FMGL(centre));
    
}

void au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(symbol) = buf.read<x10aux::ref<x10::lang::String> >();
    FMGL(charge) = buf.read<x10_double>();
    FMGL(centre) = buf.read<x10x::vector::Point3d>();
}


x10aux::RuntimeType au::edu::anu::chem::mm::MMAtom__PackedRepresentation::rtt;
void au::edu::anu::chem::mm::MMAtom__PackedRepresentation::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    rtt.initStageTwo("au.edu.anu.chem.mm.MMAtom.PackedRepresentation",x10aux::RuntimeType::struct_kind, 2, parents, 0, NULL, NULL);
}
/* END of MMAtom$PackedRepresentation */
/*************************************************/
