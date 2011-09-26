/*************************************************/
/* START of FmmBox */
#include <au/edu/anu/mm/FmmBox.h>

#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/GlobalRef.h>
#include <x10/array/Array.h>
#include <x10/array/Point.h>
#include <au/edu/anu/mm/MultipoleExpansion.h>
#include <au/edu/anu/mm/LocalExpansion.h>
#include <x10/lang/Double.h>
#include <x10x/vector/Point3d.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/Math.h>
#include <x10/compiler/Inline.h>
#include <x10/util/ArrayList.h>
#include <x10/lang/String.h>

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::FmmBox::getOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::FmmBox.X10$class_lock_id1");
    x10_int __var311__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var311__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var312__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var312__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::FmmBox.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::FmmBox.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__313 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__deserialize));


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::FmmBox::getStaticOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId48585 = au::edu::anu::mm::FmmBox::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId48585);
    }))
    ;
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c

//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c

//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c

//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c

//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c
/** 
     * The V-list consists of the children of those boxes 
     * not well-separated from this box's parent.
     */
                                                                                                                         //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c
                                                                                                                         /** The multipole expansion of the charges within this box. */
                                                                                                                                                                                       //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldDecl_c
                                                                                                                                                                                       /** The Taylor expansion of the potential within this box due to particles in well separated boxes. */

//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::FmmBox::_constructor(
  x10_int level,
  x10_int x,
  x10_int y,
  x10_int z,
  x10_int numTerms,
  x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent)
{
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::FmmBox> this4858648618 =
          ((x10aux::ref<au::edu::anu::mm::FmmBox>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4858648618)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4858648618)->
              FMGL(vList) =
              X10_NULL;
        }
        
    }))
    ;
    
    //#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(level) =
      level;
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(x) =
      x;
    
    //#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(y) =
      y;
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(z) =
      z;
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(parent) =
      parent;
    
    //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(multipoleExp) =
      (__extension__ ({
        
        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> alloc40308 =
          
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
        ;
        
        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
        (alloc40308)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
          numTerms,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc40308;
    }))
    ;
    
    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(localExp) =
      (__extension__ ({
        
        //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::LocalExpansion> alloc40309 =
          
        x10aux::ref<au::edu::anu::mm::LocalExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion()))
        ;
        
        //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
        (alloc40309)->::au::edu::anu::mm::LocalExpansion::_constructor(
          numTerms,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc40309;
    }))
    ;
    
}
x10aux::ref<au::edu::anu::mm::FmmBox> au::edu::anu::mm::FmmBox::_make(
  x10_int level,
  x10_int x,
  x10_int y,
  x10_int z,
  x10_int numTerms,
  x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent)
{
    x10aux::ref<au::edu::anu::mm::FmmBox> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::FmmBox>(), 0, sizeof(au::edu::anu::mm::FmmBox))) au::edu::anu::mm::FmmBox();
    this_->_constructor(level,
    x,
    y,
    z,
    numTerms,
    parent);
    return this_;
}



//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::FmmBox::_constructor(
  x10_int level,
  x10_int x,
  x10_int y,
  x10_int z,
  x10_int numTerms,
  x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::FmmBox> this4858948619 =
          ((x10aux::ref<au::edu::anu::mm::FmmBox>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4858948619)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4858948619)->
              FMGL(vList) =
              X10_NULL;
        }
        
    }))
    ;
    
    //#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(level) =
      level;
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(x) =
      x;
    
    //#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(y) =
      y;
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(z) =
      z;
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(parent) =
      parent;
    
    //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(multipoleExp) =
      (__extension__ ({
        
        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> alloc40310 =
          
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
        ;
        
        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
        (alloc40310)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
          numTerms,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc40310;
    }))
    ;
    
    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(localExp) =
      (__extension__ ({
        
        //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::LocalExpansion> alloc40311 =
          
        x10aux::ref<au::edu::anu::mm::LocalExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion()))
        ;
        
        //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
        (alloc40311)->::au::edu::anu::mm::LocalExpansion::_constructor(
          numTerms,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc40311;
    }))
    ;
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::FmmBox> au::edu::anu::mm::FmmBox::_make(
  x10_int level,
  x10_int x,
  x10_int y,
  x10_int z,
  x10_int numTerms,
  x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::FmmBox> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::FmmBox>(), 0, sizeof(au::edu::anu::mm::FmmBox))) au::edu::anu::mm::FmmBox();
    this_->_constructor(level,
    x,
    y,
    z,
    numTerms,
    parent,
    paramLock);
    return this_;
}



//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10x::vector::Point3d au::edu::anu::mm::FmmBox::getCentre(
  x10_double size) {
    
    //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int dim = (__extension__ ({
        
        //#line 376 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int i48592 = ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                           FMGL(level);
        ((x10_int) ((((x10_int)1)) << (0x1f & (i48592))));
    }))
    ;
    
    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_double sideLength = ((size) / (((x10_double) (dim))));
    
    //#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_double offset = ((0.5) * (size));
    
    //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
        x10x::vector::Point3d alloc40312 =
          
        x10x::vector::Point3d::_alloc();
        
        //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
        (alloc40312)->::x10x::vector::Point3d::_constructor(
          ((((((((x10_double) (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                 FMGL(x)))) + (0.5))) * (sideLength))) - (offset)),
          ((((((((x10_double) (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                 FMGL(y)))) + (0.5))) * (sideLength))) - (offset)),
          ((((((((x10_double) (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                 FMGL(z)))) + (0.5))) * (sideLength))) - (offset)),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc40312;
    }))
    ;
    
}

//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::FmmBox::wellSeparated(
  x10_int ws,
  x10_int x2,
  x10_int y2,
  x10_int z2) {
    
    //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return ((::abs(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                  FMGL(x)) - (x2))))) > (ws)) ||
    ((::abs(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                           FMGL(y)) - (y2))))) > (ws)) ||
    ((::abs(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                           FMGL(z)) - (z2))))) > (ws));
    
}

//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::FmmBox::wellSeparated(
  x10_int ws,
  x10aux::ref<au::edu::anu::mm::FmmBox> box2) {
    
    //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return ((::abs(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                  FMGL(x)) - (x10aux::nullCheck(box2)->
                                                FMGL(x)))))) > (ws)) ||
    ((::abs(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                           FMGL(y)) - (x10aux::nullCheck(box2)->
                                         FMGL(y)))))) > (ws)) ||
    ((::abs(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                           FMGL(z)) - (x10aux::nullCheck(box2)->
                                         FMGL(z)))))) > (ws));
    
}

//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Point> au::edu::anu::mm::FmmBox::getTranslationIndex(
  x10aux::ref<x10::array::Point> boxIndex2) {
    
    //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
        x10_int i048593 = ((x10_int) ((x10aux::nullCheck(boxIndex2)->x10::array::Point::__apply(
                                         ((x10_int)0))) - (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                             FMGL(x))));
        
        //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
        x10_int i148594 = ((x10_int) ((x10aux::nullCheck(boxIndex2)->x10::array::Point::__apply(
                                         ((x10_int)1))) - (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                             FMGL(y))));
        
        //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
        x10_int i248595 = ((x10_int) ((x10aux::nullCheck(boxIndex2)->x10::array::Point::__apply(
                                         ((x10_int)2))) - (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                             FMGL(z))));
        (__extension__ ({
            
            //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Point> alloc2994148596 =
              
            x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
            ;
            
            //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
            (alloc2994148596)->::x10::array::Point::_constructor(
              i048593,
              i148594,
              i248595);
            alloc2994148596;
        }))
        ;
    }))
    ;
    
}

//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > >
  au::edu::anu::mm::FmmBox::getVList(
  ) {
    
    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
             FMGL(vList);
    
}

//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmBox::setVList(x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > vList) {
    
    //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(vList) = vList;
}

//#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmBox::createVList(
  x10_int ws) {
    
    //#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int levelDim = (__extension__ ({
        
        //#line 376 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int i48597 = ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                           FMGL(level);
        ((x10_int) ((((x10_int)1)) << (0x1f & (i48597))));
    }))
    ;
    
    //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int xOffset = (x10aux::struct_equals(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                            FMGL(x)) % x10aux::zeroCheck(((x10_int)2)))),
                                             ((x10_int)1)))
      ? (((x10_int)-1))
      : (((x10_int)0));
    
    //#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int yOffset = (x10aux::struct_equals(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                            FMGL(y)) % x10aux::zeroCheck(((x10_int)2)))),
                                             ((x10_int)1)))
      ? (((x10_int)-1))
      : (((x10_int)0));
    
    //#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int zOffset = (x10aux::struct_equals(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                            FMGL(z)) % x10aux::zeroCheck(((x10_int)2)))),
                                             ((x10_int)1)))
      ? (((x10_int)-1))
      : (((x10_int)0));
    
    //#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::util::ArrayList<x10aux::ref<x10::array::Point> > > vList =
      
    x10aux::ref<x10::util::ArrayList<x10aux::ref<x10::array::Point> > >((new (memset(x10aux::alloc<x10::util::ArrayList<x10aux::ref<x10::array::Point> > >(), 0, sizeof(x10::util::ArrayList<x10aux::ref<x10::array::Point> >))) x10::util::ArrayList<x10aux::ref<x10::array::Point> >()))
    ;
    
    //#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
    (vList)->::x10::util::ArrayList<x10aux::ref<x10::array::Point> >::_constructor();
    
    //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int i40346min4034748638 = (__extension__ ({
        
        //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int b4859948639 = ((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                          FMGL(x)) - (((x10_int) ((((x10_int)2)) * (ws))))))) + (xOffset)));
        ((((x10_int)0)) < (b4859948639)) ? (b4859948639)
          : (((x10_int)0));
    }))
    ;
    
    //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int i40346max4034848640 = (__extension__ ({
        
        //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int a4860048641 = ((x10_int) ((levelDim) - (((x10_int)1))));
        
        //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int b4860148642 = ((x10_int) ((((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                                       FMGL(x)) + (((x10_int) ((((x10_int)2)) * (ws))))))) + (((x10_int)1))))) + (xOffset)));
        ((a4860048641) < (b4860148642)) ? (a4860048641)
          : (b4860148642);
    }))
    ;
    
    //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": polyglot.ast.For_c
    {
        x10_int i4034648643;
        for (
             //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
             i4034648643 = i40346min4034748638;
             ((i4034648643) <= (i40346max4034848640));
             
             //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalAssign_c
             i4034648643 =
               ((x10_int) ((i4034648643) + (((x10_int)1)))))
        {
            
            //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
            x10_int x48644 =
              i4034648643;
            
            //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
            x10_int i40330min4033148631 =
              (__extension__ ({
                
                //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                x10_int b4860348632 =
                  ((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                              FMGL(y)) - (((x10_int) ((((x10_int)2)) * (ws))))))) + (yOffset)));
                ((((x10_int)0)) < (b4860348632))
                  ? (b4860348632)
                  : (((x10_int)0));
            }))
            ;
            
            //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
            x10_int i40330max4033248633 =
              (__extension__ ({
                
                //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                x10_int a4860448634 =
                  ((x10_int) ((levelDim) - (((x10_int)1))));
                
                //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                x10_int b4860548635 =
                  ((x10_int) ((((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                           FMGL(y)) + (((x10_int) ((((x10_int)2)) * (ws))))))) + (((x10_int)1))))) + (yOffset)));
                ((a4860448634) < (b4860548635))
                  ? (a4860448634)
                  : (b4860548635);
            }))
            ;
            
            //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": polyglot.ast.For_c
            {
                x10_int i4033048636;
                for (
                     //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                     i4033048636 =
                       i40330min4033148631;
                     ((i4033048636) <= (i40330max4033248633));
                     
                     //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalAssign_c
                     i4033048636 =
                       ((x10_int) ((i4033048636) + (((x10_int)1)))))
                {
                    
                    //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                    x10_int y48637 =
                      i4033048636;
                    
                    //#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                    x10_int i40314min4031548624 =
                      (__extension__ ({
                        
                        //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                        x10_int b4860748625 =
                          ((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                      FMGL(z)) - (((x10_int) ((((x10_int)2)) * (ws))))))) + (zOffset)));
                        ((((x10_int)0)) < (b4860748625))
                          ? (b4860748625)
                          : (((x10_int)0));
                    }))
                    ;
                    
                    //#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                    x10_int i40314max4031648626 =
                      (__extension__ ({
                        
                        //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                        x10_int a4860848627 =
                          ((x10_int) ((levelDim) - (((x10_int)1))));
                        
                        //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                        x10_int b4860948628 =
                          ((x10_int) ((((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                                   FMGL(z)) + (((x10_int) ((((x10_int)2)) * (ws))))))) + (((x10_int)1))))) + (zOffset)));
                        ((a4860848627) < (b4860948628))
                          ? (a4860848627)
                          : (b4860948628);
                    }))
                    ;
                    
                    //#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": polyglot.ast.For_c
                    {
                        x10_int i4031448629;
                        for (
                             //#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                             i4031448629 =
                               i40314min4031548624;
                             ((i4031448629) <= (i40314max4031648626));
                             
                             //#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalAssign_c
                             i4031448629 =
                               ((x10_int) ((i4031448629) + (((x10_int)1)))))
                        {
                            
                            //#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                            x10_int z48630 =
                              i4031448629;
                            
                            //#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10If_c
                            if (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->wellSeparated(
                                  ws,
                                  x48644,
                                  y48637,
                                  z48630))
                            {
                                
                                //#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Call_c
                                vList->add(
                                  (__extension__ ({
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i04861048620 =
                                        x48644;
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i14861148621 =
                                        y48637;
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i24861248622 =
                                        z48630;
                                      (__extension__ ({
                                          
                                          //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                          x10aux::ref<x10::array::Point> alloc299414861348623 =
                                            
                                          x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
                                          ;
                                          
                                          //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
                                          (alloc299414861348623)->::x10::array::Point::_constructor(
                                            i04861048620,
                                            i14861148621,
                                            i24861248622);
                                          alloc299414861348623;
                                      }))
                                      ;
                                  }))
                                  );
                            }
                            
                        }
                    }
                    
                }
            }
            
        }
    }
    
    //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(vList) = vList->toArray();
}

//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmBox::createVListPeriodic(
  x10_int ws) {
    
    //#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int xOffset = (x10aux::struct_equals(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                            FMGL(x)) % x10aux::zeroCheck(((x10_int)2)))),
                                             ((x10_int)1)))
      ? (((x10_int)-1))
      : (((x10_int)0));
    
    //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int yOffset = (x10aux::struct_equals(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                            FMGL(y)) % x10aux::zeroCheck(((x10_int)2)))),
                                             ((x10_int)1)))
      ? (((x10_int)-1))
      : (((x10_int)0));
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int zOffset = (x10aux::struct_equals(((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                            FMGL(z)) % x10aux::zeroCheck(((x10_int)2)))),
                                             ((x10_int)1)))
      ? (((x10_int)-1))
      : (((x10_int)0));
    
    //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::util::ArrayList<x10aux::ref<x10::array::Point> > > vList =
      
    x10aux::ref<x10::util::ArrayList<x10aux::ref<x10::array::Point> > >((new (memset(x10aux::alloc<x10::util::ArrayList<x10aux::ref<x10::array::Point> > >(), 0, sizeof(x10::util::ArrayList<x10aux::ref<x10::array::Point> >))) x10::util::ArrayList<x10aux::ref<x10::array::Point> >()))
    ;
    
    //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10ConstructorCall_c
    (vList)->::x10::util::ArrayList<x10aux::ref<x10::array::Point> >::_constructor();
    
    //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int i40394min4039548657 = ((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                              FMGL(x)) - (((x10_int) ((((x10_int)2)) * (ws))))))) + (xOffset)));
    
    //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
    x10_int i40394max4039648658 = ((x10_int) ((((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                                           FMGL(x)) + (((x10_int) ((((x10_int)2)) * (ws))))))) + (((x10_int)1))))) + (xOffset)));
    
    //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": polyglot.ast.For_c
    {
        x10_int i4039448659;
        for (
             //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
             i4039448659 = i40394min4039548657;
             ((i4039448659) <= (i40394max4039648658));
             
             //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalAssign_c
             i4039448659 =
               ((x10_int) ((i4039448659) + (((x10_int)1)))))
        {
            
            //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
            x10_int x48660 =
              i4039448659;
            
            //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
            x10_int i40378min4037948653 =
              ((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                          FMGL(y)) - (((x10_int) ((((x10_int)2)) * (ws))))))) + (yOffset)));
            
            //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
            x10_int i40378max4038048654 =
              ((x10_int) ((((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                       FMGL(y)) + (((x10_int) ((((x10_int)2)) * (ws))))))) + (((x10_int)1))))) + (yOffset)));
            
            //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": polyglot.ast.For_c
            {
                x10_int i4037848655;
                for (
                     //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                     i4037848655 =
                       i40378min4037948653;
                     ((i4037848655) <= (i40378max4038048654));
                     
                     //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalAssign_c
                     i4037848655 =
                       ((x10_int) ((i4037848655) + (((x10_int)1)))))
                {
                    
                    //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                    x10_int y48656 =
                      i4037848655;
                    
                    //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                    x10_int i40362min4036348649 =
                      ((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                  FMGL(z)) - (((x10_int) ((((x10_int)2)) * (ws))))))) + (zOffset)));
                    
                    //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                    x10_int i40362max4036448650 =
                      ((x10_int) ((((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                               FMGL(z)) + (((x10_int) ((((x10_int)2)) * (ws))))))) + (((x10_int)1))))) + (zOffset)));
                    
                    //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": polyglot.ast.For_c
                    {
                        x10_int i4036248651;
                        for (
                             //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                             i4036248651 =
                               i40362min4036348649;
                             ((i4036248651) <= (i40362max4036448650));
                             
                             //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalAssign_c
                             i4036248651 =
                               ((x10_int) ((i4036248651) + (((x10_int)1)))))
                        {
                            
                            //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10LocalDecl_c
                            x10_int z48652 =
                              i4036248651;
                            
                            //#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10If_c
                            if (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->wellSeparated(
                                  ws,
                                  x48660,
                                  y48656,
                                  z48652))
                            {
                                
                                //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Call_c
                                vList->add(
                                  (__extension__ ({
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i04861448645 =
                                        x48660;
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i14861548646 =
                                        y48656;
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i24861648647 =
                                        z48652;
                                      (__extension__ ({
                                          
                                          //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                          x10aux::ref<x10::array::Point> alloc299414861748648 =
                                            
                                          x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
                                          ;
                                          
                                          //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
                                          (alloc299414861748648)->::x10::array::Point::_constructor(
                                            i04861448645,
                                            i14861548646,
                                            i24861648647);
                                          alloc299414861748648;
                                      }))
                                      ;
                                  }))
                                  );
                            }
                            
                        }
                    }
                    
                }
            }
            
        }
    }
    
    //#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(vList) = vList->toArray();
}

//#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::mm::FmmBox::toString(
  ) {
    
    //#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return ((((((((((((((((x10aux::string_utils::lit("FmmBox level ")) + (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                                            FMGL(level)))) + (x10aux::string_utils::lit(" (")))) + (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                                                                                                      FMGL(x)))) + (x10aux::string_utils::lit(",")))) + (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                                                                                                                                                           FMGL(y)))) + (x10aux::string_utils::lit(",")))) + (((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
                                                                                                                                                                                                                                                FMGL(z)))) + (x10aux::string_utils::lit(")")));
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::FmmBox> au::edu::anu::mm::FmmBox::au__edu__anu__mm__FmmBox____au__edu__anu__mm__FmmBox__this(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::FmmBox>)this);
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmBox::__fieldInitializers39675(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmBox>)this)->
      FMGL(vList) = X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::FmmBox::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::FmmBox::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::FmmBox::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    buf.write(this->FMGL(parent));
    buf.write(this->FMGL(level));
    buf.write(this->FMGL(x));
    buf.write(this->FMGL(y));
    buf.write(this->FMGL(z));
    buf.write(this->FMGL(vList));
    buf.write(this->FMGL(multipoleExp));
    buf.write(this->FMGL(localExp));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::FmmBox::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::FmmBox> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::FmmBox>(), 0, sizeof(au::edu::anu::mm::FmmBox))) au::edu::anu::mm::FmmBox();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::FmmBox::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    FMGL(parent) = buf.read<x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > >();
    FMGL(level) = buf.read<x10_int>();
    FMGL(x) = buf.read<x10_int>();
    FMGL(y) = buf.read<x10_int>();
    FMGL(z) = buf.read<x10_int>();
    FMGL(vList) = buf.read<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > >();
    FMGL(multipoleExp) = buf.read<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >();
    FMGL(localExp) = buf.read<x10aux::ref<au::edu::anu::mm::LocalExpansion> >();
}

x10aux::RuntimeType au::edu::anu::mm::FmmBox::rtt;
void au::edu::anu::mm::FmmBox::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
    rtt.initStageTwo("au.edu.anu.mm.FmmBox",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
/* END of FmmBox */
/*************************************************/
