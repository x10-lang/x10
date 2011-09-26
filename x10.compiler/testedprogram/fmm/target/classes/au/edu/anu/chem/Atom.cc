/*************************************************/
/* START of Atom */
#include <au/edu/anu/chem/Atom.h>

#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10x/vector/Point3d.h>
#include <x10/lang/String.h>
#include <x10/util/ArrayList.h>
#include <x10/util/Pair.h>
#include <au/edu/anu/chem/BondType.h>
#include <x10/util/Container.h>
#include <x10/lang/Zero.h>

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldDecl_c

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::chem::Atom::getOrderedLock(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::chem::Atom>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1);
void au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::chem::Atom.X10$class_lock_id1");
    x10_int __var327__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var327__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var328__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var328__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::chem::Atom.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::chem::Atom.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__329 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::chem::Atom::FMGL(X10__class_lock_id1__deserialize));


//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::chem::Atom::getStaticOrderedLock(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId48661 = au::edu::anu::chem::Atom::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId48661);
    }))
    ;
    
}

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldDecl_c
/** The location of the atomic nucleus. */
                                          //#line 30 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldDecl_c
                                          /** The symbol for this atom species. */
                                                                                  //#line 33 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldDecl_c
                                                                                  /** A list of atoms to which this atom is bonded. */

//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::Atom::_constructor(
  x10aux::ref<x10::lang::String> symbol,
  x10x::vector::Point3d centre) {
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->au::edu::anu::chem::Atom::__fieldInitializers25597();
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(symbol) = symbol;
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(centre) = centre;
    
}
x10aux::ref<au::edu::anu::chem::Atom> au::edu::anu::chem::Atom::_make(
  x10aux::ref<x10::lang::String> symbol,
  x10x::vector::Point3d centre) {
    x10aux::ref<au::edu::anu::chem::Atom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::Atom>(), 0, sizeof(au::edu::anu::chem::Atom))) au::edu::anu::chem::Atom();
    this_->_constructor(symbol, centre);
    return this_;
}



//#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::Atom::_constructor(
  x10aux::ref<x10::lang::String> symbol,
  x10x::vector::Point3d centre,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->au::edu::anu::chem::Atom::__fieldInitializers25597();
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(symbol) =
      symbol;
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(centre) =
      centre;
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::chem::Atom> au::edu::anu::chem::Atom::_make(
  x10aux::ref<x10::lang::String> symbol,
  x10x::vector::Point3d centre,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::chem::Atom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::Atom>(), 0, sizeof(au::edu::anu::chem::Atom))) au::edu::anu::chem::Atom();
    this_->_constructor(symbol,
    centre,
    paramLock);
    return this_;
}



//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::Atom::_constructor(
  x10x::vector::Point3d centre) {
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->au::edu::anu::chem::Atom::__fieldInitializers25597();
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(symbol) = x10aux::string_utils::lit("");
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(centre) = centre;
    
}
x10aux::ref<au::edu::anu::chem::Atom> au::edu::anu::chem::Atom::_make(
  x10x::vector::Point3d centre) {
    x10aux::ref<au::edu::anu::chem::Atom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::Atom>(), 0, sizeof(au::edu::anu::chem::Atom))) au::edu::anu::chem::Atom();
    this_->_constructor(centre);
    return this_;
}



//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::Atom::_constructor(
  x10x::vector::Point3d centre,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->au::edu::anu::chem::Atom::__fieldInitializers25597();
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(symbol) =
      x10aux::string_utils::lit("");
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(centre) =
      centre;
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::chem::Atom> au::edu::anu::chem::Atom::_make(
  x10x::vector::Point3d centre,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::chem::Atom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::Atom>(), 0, sizeof(au::edu::anu::chem::Atom))) au::edu::anu::chem::Atom();
    this_->_constructor(centre,
    paramLock);
    return this_;
}



//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::Atom::_constructor(
  x10aux::ref<au::edu::anu::chem::Atom> atom)
{
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->au::edu::anu::chem::Atom::__fieldInitializers25597();
    
    //#line 50 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(symbol) =
      x10aux::nullCheck(atom)->
        FMGL(symbol);
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(centre) =
      x10aux::nullCheck(atom)->
        FMGL(centre);
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(x10aux::nullCheck(atom)->
                                  FMGL(bonds),
                                X10_NULL)))
    {
        
        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
        ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
          FMGL(bonds) =
          x10::util::ArrayList<void>::make<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > >(
            x10aux::class_cast_unchecked<x10aux::ref<x10::util::Container<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > > > >(x10aux::nullCheck(atom)->
                                                                                                                                                                        FMGL(bonds)));
    }
    
}
x10aux::ref<au::edu::anu::chem::Atom> au::edu::anu::chem::Atom::_make(
  x10aux::ref<au::edu::anu::chem::Atom> atom)
{
    x10aux::ref<au::edu::anu::chem::Atom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::Atom>(), 0, sizeof(au::edu::anu::chem::Atom))) au::edu::anu::chem::Atom();
    this_->_constructor(atom);
    return this_;
}



//#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::chem::Atom::_constructor(
  x10aux::ref<au::edu::anu::chem::Atom> atom,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.AssignPropertyCall_c
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->au::edu::anu::chem::Atom::__fieldInitializers25597();
    
    //#line 50 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(symbol) =
      x10aux::nullCheck(atom)->
        FMGL(symbol);
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(centre) =
      x10aux::nullCheck(atom)->
        FMGL(centre);
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(x10aux::nullCheck(atom)->
                                  FMGL(bonds),
                                X10_NULL)))
    {
        
        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
        ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
          FMGL(bonds) =
          x10::util::ArrayList<void>::make<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > >(
            x10aux::class_cast_unchecked<x10aux::ref<x10::util::Container<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > > > >(x10aux::nullCheck(atom)->
                                                                                                                                                                        FMGL(bonds)));
    }
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::chem::Atom> au::edu::anu::chem::Atom::_make(
  x10aux::ref<au::edu::anu::chem::Atom> atom,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::chem::Atom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::Atom>(), 0, sizeof(au::edu::anu::chem::Atom))) au::edu::anu::chem::Atom();
    this_->_constructor(atom,
    paramLock);
    return this_;
}



//#line 57 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::ArrayList<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > > >
  au::edu::anu::chem::Atom::getBonds(
  ) {
    
    //#line 57 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
             FMGL(bonds);
    
}

//#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::Atom::addBond(au::edu::anu::chem::BondType bondType,
                                       x10aux::ref<au::edu::anu::chem::Atom> atom) {
    
    //#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->addBondInternal(
      bondType,
      atom);
    
    //#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    x10aux::nullCheck(atom)->addBondInternal(
      bondType,
      ((x10aux::ref<au::edu::anu::chem::Atom>)this));
}

//#line 75 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::Atom::addBondInternal(
  au::edu::anu::chem::BondType bondType,
  x10aux::ref<au::edu::anu::chem::Atom> atom) {
    
    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(((x10aux::ref<au::edu::anu::chem::Atom>)this)->
                                 FMGL(bonds),
                               X10_NULL)))
    {
        
        //#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
        ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
          FMGL(bonds) =
          (__extension__ ({
            
            //#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::util::ArrayList<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > > > alloc25739 =
              
            x10aux::ref<x10::util::ArrayList<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > > >((new (memset(x10aux::alloc<x10::util::ArrayList<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > > >(), 0, sizeof(x10::util::ArrayList<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > >))) x10::util::ArrayList<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > >()))
            ;
            
            //#line 77 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10ConstructorCall_c
            (alloc25739)->::x10::util::ArrayList<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > >::_constructor();
            alloc25739;
        }))
        ;
    }
    
    //#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Call_c
    x10aux::nullCheck(((x10aux::ref<au::edu::anu::chem::Atom>)this)->
                        FMGL(bonds))->add(
      (__extension__ ({
          
          //#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10LocalDecl_c
          x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > alloc25740 =
            
          x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> >::_alloc();
          
          //#line 79 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.StmtExpr_c
          (__extension__ ({
              
              //#line 21 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/Pair.x10": x10.ast.X10LocalDecl_c
              au::edu::anu::chem::BondType first48922 =
                bondType;
              
              //#line 21 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/Pair.x10": x10.ast.X10LocalDecl_c
              x10aux::ref<au::edu::anu::chem::Atom> second48923 =
                atom;
              {
                  
                  //#line 22 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/Pair.x10": x10.ast.X10FieldAssign_c
                  alloc25740->
                    FMGL(first) =
                    first48922;
                  
                  //#line 23 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/Pair.x10": x10.ast.X10FieldAssign_c
                  alloc25740->
                    FMGL(second) =
                    second48923;
              }
              
          }))
          ;
          alloc25740;
      }))
      );
}

//#line 83 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldDecl_c
/** index of this atom in a molecule */

//#line 84 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::Atom::setIndex(x10_int i) {
    
    //#line 84 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(index) = i;
}

//#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::chem::Atom::getIndex(
  ) {
    
    //#line 85 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
             FMGL(index);
    
}

//#line 87 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::chem::Atom::toString(
  ) {
    
    //#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Return_c
    return ((((((((((((((x10aux::ref<au::edu::anu::chem::Atom>)this)->
                         FMGL(symbol)) + (x10aux::string_utils::lit(" ")))) + (((x10aux::ref<au::edu::anu::chem::Atom>)this)->
                                                                                 FMGL(centre)->
                                                                                 FMGL(i)))) + (x10aux::string_utils::lit(" ")))) + (((x10aux::ref<au::edu::anu::chem::Atom>)this)->
                                                                                                                                      FMGL(centre)->
                                                                                                                                      FMGL(j)))) + (x10aux::string_utils::lit(" ")))) + (((x10aux::ref<au::edu::anu::chem::Atom>)this)->
                                                                                                                                                                                           FMGL(centre)->
                                                                                                                                                                                           FMGL(k)));
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::chem::Atom> au::edu::anu::chem::Atom::au__edu__anu__chem__Atom____au__edu__anu__chem__Atom__this(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::chem::Atom>)this);
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::chem::Atom::__fieldInitializers25597(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(centre) = x10aux::zeroValue<x10x::vector::Point3d >();
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(bonds) = X10_NULL;
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/Atom.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::chem::Atom>)this)->
      FMGL(index) = ((x10_int)0);
}
const x10aux::serialization_id_t au::edu::anu::chem::Atom::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::chem::Atom::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::chem::Atom::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    buf.write(this->FMGL(centre));
    buf.write(this->FMGL(symbol));
    buf.write(this->FMGL(bonds));
    buf.write(this->FMGL(index));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::chem::Atom::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::chem::Atom> this_ = new (memset(x10aux::alloc<au::edu::anu::chem::Atom>(), 0, sizeof(au::edu::anu::chem::Atom))) au::edu::anu::chem::Atom();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::chem::Atom::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    FMGL(centre) = buf.read<x10x::vector::Point3d>();
    FMGL(symbol) = buf.read<x10aux::ref<x10::lang::String> >();
    FMGL(bonds) = buf.read<x10aux::ref<x10::util::ArrayList<x10::util::Pair<au::edu::anu::chem::BondType, x10aux::ref<au::edu::anu::chem::Atom> > > > >();
    FMGL(index) = buf.read<x10_int>();
}

x10aux::RuntimeType au::edu::anu::chem::Atom::rtt;
void au::edu::anu::chem::Atom::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
    rtt.initStageTwo("au.edu.anu.chem.Atom",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
/* END of Atom */
/*************************************************/
