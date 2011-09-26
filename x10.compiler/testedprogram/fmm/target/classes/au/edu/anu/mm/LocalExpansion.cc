/*************************************************/
/* START of LocalExpansion */
#include <au/edu/anu/mm/LocalExpansion.h>

#include <au/edu/anu/mm/Expansion.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10x/vector/Tuple3d.h>
#include <x10/array/Array.h>
#include <x10/lang/Complex.h>
#include <x10x/polar/Polar3d.h>
#include <x10/lang/Double.h>
#include <au/edu/anu/mm/AssociatedLegendrePolynomial.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/array/RectLayout.h>
#include <x10/lang/Math.h>
#include <x10/lang/Boolean.h>
#include <au/edu/anu/mm/MultipoleExpansion.h>
#include <x10x/vector/Vector3d.h>
#include <x10/array/Region.h>
#include <x10/lang/IntRange.h>
#include <x10/array/RectRegion1D.h>
#include <au/edu/anu/mm/WignerRotationMatrix.h>
#include <au/edu/anu/mm/Factorial.h>

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::LocalExpansion::getOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::LocalExpansion.X10$class_lock_id1");
    x10_int __var450__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var450__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var451__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var451__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::LocalExpansion.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::LocalExpansion.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__452 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::LocalExpansion::FMGL(X10__class_lock_id1__deserialize));


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::LocalExpansion::getStaticOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId55619 = au::edu::anu::mm::LocalExpansion::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId55619);
    }))
    ;
    
}

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::LocalExpansion::_constructor(
  x10_int p) {
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Expansion>)this))->::au::edu::anu::mm::Expansion::_constructor(
      p);
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::LocalExpansion> this5562056094 =
          ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5562056094)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
}
x10aux::ref<au::edu::anu::mm::LocalExpansion> au::edu::anu::mm::LocalExpansion::_make(
  x10_int p) {
    x10aux::ref<au::edu::anu::mm::LocalExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion();
    this_->_constructor(p);
    return this_;
}



//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::LocalExpansion::_constructor(
  x10_int p,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Expansion>)this))->::au::edu::anu::mm::Expansion::_constructor(
      p);
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::LocalExpansion> this5562356095 =
          ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5562356095)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::LocalExpansion> au::edu::anu::mm::LocalExpansion::_make(
  x10_int p,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::LocalExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion();
    this_->_constructor(p,
    paramLock);
    return this_;
}



//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::LocalExpansion::_constructor(
  x10aux::ref<au::edu::anu::mm::LocalExpansion> source)
{
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Expansion>)this))->::au::edu::anu::mm::Expansion::_constructor(
      x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(source));
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::LocalExpansion> this5562656096 =
          ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5562656096)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
}
x10aux::ref<au::edu::anu::mm::LocalExpansion> au::edu::anu::mm::LocalExpansion::_make(
  x10aux::ref<au::edu::anu::mm::LocalExpansion> source)
{
    x10aux::ref<au::edu::anu::mm::LocalExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion();
    this_->_constructor(source);
    return this_;
}



//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::LocalExpansion::_constructor(
  x10aux::ref<au::edu::anu::mm::LocalExpansion> source,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Expansion>)this))->::au::edu::anu::mm::Expansion::_constructor(
      x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(source));
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::LocalExpansion> this5562956097 =
          ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5562956097)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::LocalExpansion> au::edu::anu::mm::LocalExpansion::_make(
  x10aux::ref<au::edu::anu::mm::LocalExpansion> source,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::LocalExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion();
    this_->_constructor(source,
    paramLock);
    return this_;
}



//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::LocalExpansion>
  au::edu::anu::mm::LocalExpansion::getMlm(
  x10aux::ref<x10x::vector::Tuple3d> v,
  x10_int p) {
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<au::edu::anu::mm::LocalExpansion> exp =
      
    x10aux::ref<au::edu::anu::mm::LocalExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion()))
    ;
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
    (exp)->::au::edu::anu::mm::LocalExpansion::_constructor(
      p,
      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10::lang::Complex> > terms =
      exp->
        FMGL(terms);
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10x::polar::Polar3d v_pole = x10x::polar::Polar3d::getPolar3d(
                                    v);
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10_double> > pplm =
      au::edu::anu::mm::AssociatedLegendrePolynomial::getPlk(
        v_pole->
          FMGL(theta),
        p);
    
    //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double rfac0 = ((1.0) / (v_pole->
                                   FMGL(r)));
    
    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10::lang::Complex v55647 = (__extension__ ({
            
            //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10::lang::Complex alloc38966 =
              
            x10::lang::Complex::_alloc();
            
            //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
                x10_double real55642 = ((rfac0) * ((__extension__ ({
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret55634;
                    {
                        
                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret55634 = (x10aux::nullCheck(pplm)->
                                      FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this55639 =
                              x10aux::nullCheck(pplm)->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret55640;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset55638 =
                                  ((x10_int) ((((x10_int)0)) - (this55639->
                                                                  FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset55638 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset55638) * (this55639->
                                                                                           FMGL(delta1))))) + (((x10_int)0))))) - (this55639->
                                                                                                                                     FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret55640 =
                                  offset55638;
                            }
                            ret55640;
                        }))
                        );
                    }
                    ret55634;
                }))
                ));
                {
                    
                    //#line 53 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
                    alloc38966->FMGL(re) =
                      real55642;
                    
                    //#line 54 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
                    alloc38966->FMGL(im) =
                      0.0;
                }
                
            }))
            ;
            alloc38966;
        }))
        ;
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10::lang::Complex ret55648;
        {
            
            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
            (x10aux::nullCheck(terms)->FMGL(raw))->__set((__extension__ ({
                
                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this55653 =
                  x10aux::nullCheck(terms)->
                    FMGL(layout);
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                x10_int ret55654;
                {
                    
                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int offset55652 =
                      ((x10_int) ((((x10_int)0)) - (this55653->
                                                      FMGL(min0))));
                    
                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    offset55652 = ((x10_int) ((((x10_int) ((((x10_int) ((offset55652) * (this55653->
                                                                                           FMGL(delta1))))) + (((x10_int)0))))) - (this55653->
                                                                                                                                     FMGL(min1))));
                    
                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    ret55654 = offset55652;
                }
                ret55654;
            }))
            , v55647);
            
            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret55648 = v55647;
        }
        ret55648;
    }))
    ;
    
    //#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10::lang::Complex phifac0 =  x10::lang::Complex::_alloc();
    
    //#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
        x10_double real55656 = x10aux::math_utils::cos(v_pole->
                                                         FMGL(phi));
        
        //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
        x10_double imaginary55657 = x10aux::math_utils::sin(v_pole->
                                                              FMGL(phi));
        {
            
            //#line 53 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
            phifac0->FMGL(re) = real55656;
            
            //#line 54 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
            phifac0->FMGL(im) = imaginary55657;
        }
        
    }))
    ;
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double rfac = ((rfac0) * (rfac0));
    
    //#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double il = 1.0;
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10_int i39003max3900556158 = p;
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
    {
        x10_int i3900356159;
        for (
             //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
             i3900356159 = ((x10_int)1); ((i3900356159) <= (i39003max3900556158));
             
             //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
             i3900356159 =
               ((x10_int) ((i3900356159) + (((x10_int)1)))))
        {
            
            //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int l56160 =
              i3900356159;
            
            //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
            il =
              ((il) * (((x10_double) (l56160))));
            
            //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_double ilm56141 =
              il;
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10::lang::Complex phifac56142 =
              x10::lang::Complex::_make(1.0,0.0);
            
            //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05566956143 =
                  l56160;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::lang::Complex v5567156144 =
                  x10aux::nullCheck(phifac56142)->x10::lang::Complex::__times(
                    ((((rfac) * ((__extension__ ({
                        
                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05565956145 =
                          l56160;
                        
                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double ret5566156146;
                        {
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5566156146 =
                              (x10aux::nullCheck(pplm)->
                                 FMGL(raw))->__apply((__extension__ ({
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5566656147 =
                                  x10aux::nullCheck(pplm)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05566356148 =
                                  i05565956145;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5566756149;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5566556150 =
                                      ((x10_int) ((i05566356148) - (this5566656147->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5566556150 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5566556150) * (this5566656147->
                                                                                                    FMGL(delta1))))) + (((x10_int)0))))) - (this5566656147->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5566756149 =
                                      offset5566556150;
                                }
                                ret5566756149;
                            }))
                            );
                        }
                        ret5566156146;
                    }))
                    ))) * (ilm56141)));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::lang::Complex ret5567256151;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (x10aux::nullCheck(terms)->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this5567756152 =
                          x10aux::nullCheck(terms)->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i05567456153 =
                          i05566956143;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret5567856154;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset5567656155 =
                              ((x10_int) ((i05567456153) - (this5567756152->
                                                              FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset5567656155 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5567656155) * (this5567756152->
                                                                                            FMGL(delta1))))) + (((x10_int)0))))) - (this5567756152->
                                                                                                                                      FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret5567856154 =
                              offset5567656155;
                        }
                        ret5567856154;
                    }))
                    , v5567156144);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5567256151 =
                      v5567156144;
                }
                ret5567256151;
            }))
            ;
            
            //#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_boolean m_sign56156 =
              false;
            
            //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38971max3897356134 =
              l56160;
            
            //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3897156135;
                for (
                     //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                     i3897156135 =
                       ((x10_int)1);
                     ((i3897156135) <= (i38971max3897356134));
                     
                     //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                     i3897156135 =
                       ((x10_int) ((i3897156135) + (((x10_int)1)))))
                {
                    
                    //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m56136 =
                      i3897156135;
                    
                    //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                    ilm56141 =
                      ((ilm56141) / (((x10_double) (((x10_int) ((((x10_int) ((l56160) + (((x10_int)1))))) - (m56136)))))));
                    
                    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                    phifac56142 =
                      x10aux::nullCheck(phifac56142)->x10::lang::Complex::__times(
                        phifac0);
                    
                    //#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10::lang::Complex M_lm56098 =
                      x10aux::nullCheck(phifac56142)->x10::lang::Complex::__times(
                        ((((rfac) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05568056099 =
                              l56160;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15568156100 =
                              m56136;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret5568256101;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5568256101 =
                                  (x10aux::nullCheck(pplm)->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5568756102 =
                                      x10aux::nullCheck(pplm)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05568456103 =
                                      i05568056099;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15568556104 =
                                      i15568156100;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5568856105;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5568656106 =
                                          ((x10_int) ((i05568456103) - (this5568756102->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5568656106 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5568656106) * (this5568756102->
                                                                                                        FMGL(delta1))))) + (i15568556104)))) - (this5568756102->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5568856105 =
                                          offset5568656106;
                                    }
                                    ret5568856105;
                                }))
                                );
                            }
                            ret5568256101;
                        }))
                        ))) * (ilm56141)));
                    
                    //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05569056107 =
                          l56160;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15569156108 =
                          m56136;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5569256109 =
                          M_lm56098;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5569356110;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(terms)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5569856111 =
                                  x10aux::nullCheck(terms)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05569556112 =
                                  i05569056107;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15569656113 =
                                  i15569156108;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5569956114;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5569756115 =
                                      ((x10_int) ((i05569556112) - (this5569856111->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5569756115 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5569756115) * (this5569856111->
                                                                                                    FMGL(delta1))))) + (i15569656113)))) - (this5569856111->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5569956114 =
                                      offset5569756115;
                                }
                                ret5569956114;
                            }))
                            , v5569256109);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5569356110 =
                              v5569256109;
                        }
                        ret5569356110;
                    }))
                    ;
                    
                    //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                    m_sign56156 =
                      !(m_sign56156);
                }
            }
            
            //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38987min3898856137 =
              ((x10_int) -(l56160));
            
            //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3898756139;
                for (
                     //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                     i3898756139 =
                       i38987min3898856137;
                     ((i3898756139) <= (((x10_int)-1)));
                     
                     //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                     i3898756139 =
                       ((x10_int) ((i3898756139) + (((x10_int)1)))))
                {
                    
                    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m56140 =
                      i3898756139;
                    
                    //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05571156116 =
                          l56160;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15571256117 =
                          m56140;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5571356118 =
                          x10aux::nullCheck(x10aux::nullCheck((__extension__ ({
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05570156119 =
                                                  l56160;
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15570256120 =
                                                  ((x10_int) -(m56140));
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::lang::Complex ret5570356121;
                                                {
                                                    
                                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                    ret5570356121 =
                                                      (x10aux::nullCheck(terms)->
                                                         FMGL(raw))->__apply((__extension__ ({
                                                        
                                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10::array::RectLayout this5570856122 =
                                                          x10aux::nullCheck(terms)->
                                                            FMGL(layout);
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i05570556123 =
                                                          i05570156119;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i15570656124 =
                                                          i15570256120;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int ret5570956125;
                                                        {
                                                            
                                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                            x10_int offset5570756126 =
                                                              ((x10_int) ((i05570556123) - (this5570856122->
                                                                                              FMGL(min0))));
                                                            
                                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            offset5570756126 =
                                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5570756126) * (this5570856122->
                                                                                                                            FMGL(delta1))))) + (i15570656124)))) - (this5570856122->
                                                                                                                                                                      FMGL(min1))));
                                                            
                                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            ret5570956125 =
                                                              offset5570756126;
                                                        }
                                                        ret5570956125;
                                                    }))
                                                    );
                                                }
                                                ret5570356121;
                                            }))
                                            )->x10::lang::Complex::conjugate())->x10::lang::Complex::__times(
                            ((x10_double) (((x10_int) ((((x10_int)1)) - (((x10_int) ((((x10_int)2)) * (((x10_int) ((((x10_int) -(m56140))) % x10aux::zeroCheck(((x10_int)2)))))))))))));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5571456127;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(terms)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5571956128 =
                                  x10aux::nullCheck(terms)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05571656129 =
                                  i05571156116;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15571756130 =
                                  i15571256117;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5572056131;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5571856132 =
                                      ((x10_int) ((i05571656129) - (this5571956128->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5571856132 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5571856132) * (this5571956128->
                                                                                                    FMGL(delta1))))) + (i15571756130)))) - (this5571956128->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5572056131 =
                                      offset5571856132;
                                }
                                ret5572056131;
                            }))
                            , v5571356118);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5571456127 =
                              v5571356118;
                        }
                        ret5571456127;
                    }))
                    ;
                }
            }
            
            //#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
            rfac =
              ((rfac) * (rfac0));
        }
    }
    
    //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Return_c
    return exp;
    
}

//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::LocalExpansion::translateAndAddLocal(
  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> shift,
  x10aux::ref<au::edu::anu::mm::LocalExpansion> source) {
    
    //#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10_int i39067max3906956213 = ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                    FMGL(p);
    
    //#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
    {
        x10_int i3906756214;
        for (
             //#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
             i3906756214 = ((x10_int)0); ((i3906756214) <= (i39067max3906956213));
             
             //#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
             i3906756214 =
               ((x10_int) ((i3906756214) + (((x10_int)1)))))
        {
            
            //#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int l56215 =
              i3906756214;
            
            //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i39051min3905256208 =
              ((x10_int) -(l56215));
            
            //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i39051max3905356209 =
              l56215;
            
            //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3905156210;
                for (
                     //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                     i3905156210 =
                       i39051min3905256208;
                     ((i3905156210) <= (i39051max3905356209));
                     
                     //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                     i3905156210 =
                       ((x10_int) ((i3905156210) + (((x10_int)1)))))
                {
                    
                    //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m56211 =
                      i3905156210;
                    
                    //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i39035min3903656204 =
                      l56215;
                    
                    //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i39035max3903756205 =
                      ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                        FMGL(p);
                    
                    //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                    {
                        x10_int i3903556206;
                        for (
                             //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                             i3903556206 =
                               i39035min3903656204;
                             ((i3903556206) <= (i39035max3903756205));
                             
                             //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                             i3903556206 =
                               ((x10_int) ((i3903556206) + (((x10_int)1)))))
                        {
                            
                            //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int j56207 =
                              i3903556206;
                            
                            //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int i39019min3902056200 =
                              ((x10_int) ((((x10_int) ((l56215) - (j56207)))) + (m56211)));
                            
                            //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int i39019max3902156201 =
                              ((x10_int) ((((x10_int) ((((x10_int) -(l56215))) + (j56207)))) + (m56211)));
                            
                            //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                            {
                                x10_int i3901956202;
                                for (
                                     //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                     i3901956202 =
                                       i39019min3902056200;
                                     ((i3901956202) <= (i39019max3902156201));
                                     
                                     //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                     i3901956202 =
                                       ((x10_int) ((i3901956202) + (((x10_int)1)))))
                                {
                                    
                                    //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10_int k56203 =
                                      i3901956202;
                                    
                                    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Complex C_lmjk56161 =
                                      (__extension__ ({
                                        
                                        //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5572456162 =
                                          x10aux::nullCheck(shift)->
                                            FMGL(terms);
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05572256163 =
                                          ((x10_int) ((j56207) - (l56215)));
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15572356164 =
                                          ((x10_int) ((k56203) - (m56211)));
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex ret5572556165;
                                        {
                                            
                                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5572556165 =
                                              (x10aux::nullCheck(this5572456162)->
                                                 FMGL(raw))->__apply((__extension__ ({
                                                
                                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::array::RectLayout this5573056166 =
                                                  x10aux::nullCheck(this5572456162)->
                                                    FMGL(layout);
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05572756167 =
                                                  i05572256163;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15572856168 =
                                                  i15572356164;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int ret5573156169;
                                                {
                                                    
                                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int offset5572956170 =
                                                      ((x10_int) ((i05572756167) - (this5573056166->
                                                                                      FMGL(min0))));
                                                    
                                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    offset5572956170 =
                                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5572956170) * (this5573056166->
                                                                                                                    FMGL(delta1))))) + (i15572856168)))) - (this5573056166->
                                                                                                                                                              FMGL(min1))));
                                                    
                                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    ret5573156169 =
                                                      offset5572956170;
                                                }
                                                ret5573156169;
                                            }))
                                            );
                                        }
                                        ret5572556165;
                                    }))
                                    ;
                                    
                                    //#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Complex O_jk56171 =
                                      (__extension__ ({
                                        
                                        //#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5573556172 =
                                          x10aux::nullCheck(source)->
                                            FMGL(terms);
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05573356173 =
                                          j56207;
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15573456174 =
                                          k56203;
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex ret5573656175;
                                        {
                                            
                                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5573656175 =
                                              (x10aux::nullCheck(this5573556172)->
                                                 FMGL(raw))->__apply((__extension__ ({
                                                
                                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::array::RectLayout this5574156176 =
                                                  x10aux::nullCheck(this5573556172)->
                                                    FMGL(layout);
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05573856177 =
                                                  i05573356173;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15573956178 =
                                                  i15573456174;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int ret5574256179;
                                                {
                                                    
                                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int offset5574056180 =
                                                      ((x10_int) ((i05573856177) - (this5574156176->
                                                                                      FMGL(min0))));
                                                    
                                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    offset5574056180 =
                                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5574056180) * (this5574156176->
                                                                                                                    FMGL(delta1))))) + (i15573956178)))) - (this5574156176->
                                                                                                                                                              FMGL(min1))));
                                                    
                                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    ret5574256179 =
                                                      offset5574056180;
                                                }
                                                ret5574256179;
                                            }))
                                            );
                                        }
                                        ret5573656175;
                                    }))
                                    ;
                                    
                                    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                                    (__extension__ ({
                                        
                                        //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5575856181 =
                                          ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                            FMGL(terms);
                                        
                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05575556182 =
                                          l56215;
                                        
                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15575656183 =
                                          m56211;
                                        
                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex v5575756184 =
                                          x10aux::nullCheck((__extension__ ({
                                              
                                              //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                              x10aux::ref<x10::array::Array<x10::lang::Complex> > this5574656185 =
                                                ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                  FMGL(terms);
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int i05574456186 =
                                                l56215;
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int i15574556187 =
                                                m56211;
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10::lang::Complex ret5574756188;
                                              {
                                                  
                                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                  ret5574756188 =
                                                    (x10aux::nullCheck(this5574656185)->
                                                       FMGL(raw))->__apply((__extension__ ({
                                                      
                                                      //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                      x10::array::RectLayout this5575256189 =
                                                        x10aux::nullCheck(this5574656185)->
                                                          FMGL(layout);
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i05574956190 =
                                                        i05574456186;
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i15575056191 =
                                                        i15574556187;
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int ret5575356192;
                                                      {
                                                          
                                                          //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                          x10_int offset5575156193 =
                                                            ((x10_int) ((i05574956190) - (this5575256189->
                                                                                            FMGL(min0))));
                                                          
                                                          //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                          offset5575156193 =
                                                            ((x10_int) ((((x10_int) ((((x10_int) ((offset5575156193) * (this5575256189->
                                                                                                                          FMGL(delta1))))) + (i15575056191)))) - (this5575256189->
                                                                                                                                                                    FMGL(min1))));
                                                          
                                                          //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                          ret5575356192 =
                                                            offset5575156193;
                                                      }
                                                      ret5575356192;
                                                  }))
                                                  );
                                              }
                                              ret5574756188;
                                          }))
                                          )->x10::lang::Complex::__plus(
                                            x10aux::nullCheck(C_lmjk56161)->x10::lang::Complex::__times(
                                              O_jk56171));
                                        
                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex ret5575956194;
                                        {
                                            
                                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                            (x10aux::nullCheck(this5575856181)->
                                               FMGL(raw))->__set((__extension__ ({
                                                
                                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::array::RectLayout this5576456195 =
                                                  x10aux::nullCheck(this5575856181)->
                                                    FMGL(layout);
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05576156196 =
                                                  i05575556182;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15576256197 =
                                                  i15575656183;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int ret5576556198;
                                                {
                                                    
                                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int offset5576356199 =
                                                      ((x10_int) ((i05576156196) - (this5576456195->
                                                                                      FMGL(min0))));
                                                    
                                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    offset5576356199 =
                                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5576356199) * (this5576456195->
                                                                                                                    FMGL(delta1))))) + (i15576256197)))) - (this5576456195->
                                                                                                                                                              FMGL(min1))));
                                                    
                                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    ret5576556198 =
                                                      offset5576356199;
                                                }
                                                ret5576556198;
                                            }))
                                            , v5575756184);
                                            
                                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5575956194 =
                                              v5575756184;
                                        }
                                        ret5575956194;
                                    }))
                                    ;
                                }
                            }
                            
                        }
                    }
                    
                }
            }
            
        }
    }
    
}

//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::LocalExpansion::translateAndAddLocal(
  x10x::vector::Vector3d v,
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > complexK,
  x10aux::ref<au::edu::anu::mm::LocalExpansion> source,
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > wigner) {
    
    //#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double b = (__extension__ ({
        x10aux::math_utils::sqrt(((((((v->
                                         FMGL(i)) * (v->
                                                       FMGL(i)))) + (((v->
                                                                         FMGL(j)) * (v->
                                                                                       FMGL(j)))))) + (((v->
                                                                                                           FMGL(k)) * (v->
                                                                                                                         FMGL(k))))));
    }))
    ;
    
    //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10::lang::Complex> > temp =
      (__extension__ ({
        
        //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc38967 =
          
        x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
        ;
        
        //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> reg55769 =
              (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r55767 =
                  x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                            FMGL(p))), ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                                         FMGL(p));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063155768 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063155768)->::x10::array::RectRegion1D::_constructor(
                      r55767->
                        FMGL(min),
                      r55767->
                        FMGL(max));
                    alloc2063155768;
                }))
                );
            }))
            ;
            {
                
                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc38967)->::x10::lang::Object::_constructor();
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38967)->
                  FMGL(region) =
                  (reg55769);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38967)->
                  FMGL(rank) =
                  x10aux::nullCheck(reg55769)->
                    FMGL(rank);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38967)->
                  FMGL(rect) =
                  x10aux::nullCheck(reg55769)->
                    FMGL(rect);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38967)->
                  FMGL(zeroBased) =
                  x10aux::nullCheck(reg55769)->
                    FMGL(zeroBased);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38967)->
                  FMGL(rail) =
                  x10aux::nullCheck(reg55769)->
                    FMGL(rail);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38967)->
                  FMGL(size) =
                  x10aux::nullCheck(reg55769)->size();
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38967)->
                  FMGL(layout) =
                  (__extension__ ({
                    
                    //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1995455770 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1995455770)->::x10::array::RectLayout::_constructor(
                      reg55769);
                    alloc1995455770;
                }))
                ;
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n55771 =
                  (__extension__ ({
                    
                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this55773 =
                      x10aux::nullCheck(alloc38967)->
                        FMGL(layout);
                    this55773->
                      FMGL(size);
                }))
                ;
                
                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38967)->
                  FMGL(raw) =
                  x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n55771, 8, false, true);
            }
            
        }))
        ;
        alloc38967;
    }))
    ;
    
    //#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<au::edu::anu::mm::LocalExpansion> scratch =
      
    x10aux::ref<au::edu::anu::mm::LocalExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion()))
    ;
    
    //#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
    (scratch)->::au::edu::anu::mm::LocalExpansion::_constructor(
      source,
      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    
    //#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
    x10aux::nullCheck(scratch)->rotate(temp,
                                       (__extension__ ({
                                           
                                           //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                           x10aux::ref<x10::array::Array<x10::lang::Complex> > ret55775;
                                           
                                           //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                           goto __ret55776; __ret55776: {
                                           {
                                               
                                               //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                               ret55775 =
                                                 (x10aux::nullCheck(complexK)->
                                                    FMGL(raw))->__apply(((x10_int)1));
                                               
                                               //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                               goto __ret55776_end_;
                                           }goto __ret55776_end_; __ret55776_end_: ;
                                           }
                                           ret55775;
                                           }))
                                           ,
                                           (__extension__ ({
                                               
                                               //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                               x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret55783;
                                               
                                               //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                               goto __ret55784; __ret55784: {
                                               {
                                                   
                                                   //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                   ret55783 =
                                                     (x10aux::nullCheck(wigner)->
                                                        FMGL(raw))->__apply(((x10_int)0));
                                                   
                                                   //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                   goto __ret55784_end_;
                                               }goto __ret55784_end_; __ret55784_end_: ;
                                               }
                                               ret55783;
                                               }))
                                               );
    
    //#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10::lang::Complex> > targetTerms =
      x10aux::nullCheck(scratch)->
        FMGL(terms);
    
    //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10_int m_sign = ((x10_int)1);
    
    //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
    x10_int i39131max3913356279 = ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                    FMGL(p);
    
    //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
    {
        x10_int i3913156280;
        for (
             //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
             i3913156280 = ((x10_int)0); ((i3913156280) <= (i39131max3913356279));
             
             //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
             i3913156280 =
               ((x10_int) ((i3913156280) + (((x10_int)1)))))
        {
            
            //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int m56281 =
              i3913156280;
            
            //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i39083min3908456270 =
              m56281;
            
            //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i39083max3908556271 =
              ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                FMGL(p);
            
            //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3908356272;
                for (
                     //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                     i3908356272 =
                       i39083min3908456270;
                     ((i3908356272) <= (i39083max3908556271));
                     
                     //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                     i3908356272 =
                       ((x10_int) ((i3908356272) + (((x10_int)1)))))
                {
                    
                    //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int l56273 =
                      i3908356272;
                    
                    //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05580056220 =
                          l56273;
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5580156221 =
                          (__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05579056222 =
                              l56273;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15579156223 =
                              m56281;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5579256224;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5579256224 =
                                  (x10aux::nullCheck(targetTerms)->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5579756225 =
                                      x10aux::nullCheck(targetTerms)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05579456226 =
                                      i05579056222;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15579556227 =
                                      i15579156223;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5579856228;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5579656229 =
                                          ((x10_int) ((i05579456226) - (this5579756225->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5579656229 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5579656229) * (this5579756225->
                                                                                                        FMGL(delta1))))) + (i15579556227)))) - (this5579756225->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5579856228 =
                                          offset5579656229;
                                    }
                                    ret5579856228;
                                }))
                                );
                            }
                            ret5579256224;
                        }))
                        ;
                        
                        //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5580256230;
                        {
                            
                            //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(temp)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5580656216 =
                                  x10aux::nullCheck(temp)->
                                    FMGL(layout);
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05580456217 =
                                  i05580056220;
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5580756218;
                                {
                                    
                                    //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5580556219 =
                                      ((x10_int) ((i05580456217) - (this5580656216->
                                                                      FMGL(min0))));
                                    
                                    //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5580756218 =
                                      offset5580556219;
                                }
                                ret5580756218;
                            }))
                            , v5580156221);
                            
                            //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5580256230 =
                              v5580156221;
                        }
                        ret5580256230;
                    }))
                    ;
                }
            }
            
            //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i39115min3911656274 =
              m56281;
            
            //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i39115max3911756275 =
              ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                FMGL(p);
            
            //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3911556276;
                for (
                     //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                     i3911556276 =
                       i39115min3911656274;
                     ((i3911556276) <= (i39115max3911756275));
                     
                     //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                     i3911556276 =
                       ((x10_int) ((i3911556276) + (((x10_int)1)))))
                {
                    
                    //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int l56277 =
                      i3911556276;
                    
                    //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10::lang::Complex M_lm56242 =
                      x10::lang::Complex::_make(0.0,0.0);
                    
                    //#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_double F_lm56243 =
                      1.0;
                    
                    //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i39099min3910056238 =
                      l56277;
                    
                    //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i39099max3910156239 =
                      ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                        FMGL(p);
                    
                    //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                    {
                        x10_int i3909956240;
                        for (
                             //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                             i3909956240 =
                               i39099min3910056238;
                             ((i3909956240) <= (i39099max3910156239));
                             
                             //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                             i3909956240 =
                               ((x10_int) ((i3909956240) + (((x10_int)1)))))
                        {
                            
                            //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int j56241 =
                              i3909956240;
                            
                            //#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                            M_lm56242 =
                              x10aux::nullCheck(M_lm56242)->x10::lang::Complex::__plus(
                                x10aux::nullCheck((__extension__ ({
                                    
                                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05580956235 =
                                      j56241;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Complex ret5581056236;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                    goto __ret5581156237; __ret5581156237: {
                                    {
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret5581056236 =
                                          (x10aux::nullCheck(temp)->
                                             FMGL(raw))->__apply((__extension__ ({
                                            
                                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10::array::RectLayout this5581456231 =
                                              x10aux::nullCheck(temp)->
                                                FMGL(layout);
                                            
                                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05581256232 =
                                              i05580956235;
                                            
                                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int ret5581556233;
                                            {
                                                
                                                //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int offset5581356234 =
                                                  ((x10_int) ((i05581256232) - (this5581456231->
                                                                                  FMGL(min0))));
                                                
                                                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                ret5581556233 =
                                                  offset5581356234;
                                            }
                                            ret5581556233;
                                        }))
                                        );
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                        goto __ret5581156237_end_;
                                    }goto __ret5581156237_end_; __ret5581156237_end_: ;
                                    }
                                    ret5581056236;
                                    }))
                                    )->x10::lang::Complex::__times(
                                      F_lm56243));
                            
                            //#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                            F_lm56243 =
                              ((((F_lm56243) * (b))) / (((x10_double) (((x10_int) ((((x10_int) ((j56241) - (l56277)))) + (((x10_int)1))))))));
                            }
                        }
                        
                    
                    //#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05581756244 =
                          l56277;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15581856245 =
                          m56281;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5581956246 =
                          M_lm56242;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5582056247;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(targetTerms)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5582556248 =
                                  x10aux::nullCheck(targetTerms)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05582256249 =
                                  i05581756244;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15582356250 =
                                  i15581856245;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5582656251;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5582456252 =
                                      ((x10_int) ((i05582256249) - (this5582556248->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5582456252 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5582456252) * (this5582556248->
                                                                                                    FMGL(delta1))))) + (i15582356250)))) - (this5582556248->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5582656251 =
                                      offset5582456252;
                                }
                                ret5582656251;
                            }))
                            , v5581956246);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5582056247 =
                              v5581956246;
                        }
                        ret5582056247;
                    }))
                    ;
                    
                    //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10If_c
                    if ((!x10aux::struct_equals(m56281,
                                                ((x10_int)0))))
                    {
                        
                        //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05583856253 =
                              l56277;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15583956254 =
                              ((x10_int) -(m56281));
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex v5584056255 =
                              x10aux::nullCheck(x10aux::nullCheck((__extension__ ({
                                                    
                                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10_int i05582856256 =
                                                      l56277;
                                                    
                                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10_int i15582956257 =
                                                      m56281;
                                                    
                                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10::lang::Complex ret5583056258;
                                                    {
                                                        
                                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                        ret5583056258 =
                                                          (x10aux::nullCheck(targetTerms)->
                                                             FMGL(raw))->__apply((__extension__ ({
                                                            
                                                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10::array::RectLayout this5583556259 =
                                                              x10aux::nullCheck(targetTerms)->
                                                                FMGL(layout);
                                                            
                                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i05583256260 =
                                                              i05582856256;
                                                            
                                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i15583356261 =
                                                              i15582956257;
                                                            
                                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                            x10_int ret5583656262;
                                                            {
                                                                
                                                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                x10_int offset5583456263 =
                                                                  ((x10_int) ((i05583256260) - (this5583556259->
                                                                                                  FMGL(min0))));
                                                                
                                                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                offset5583456263 =
                                                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset5583456263) * (this5583556259->
                                                                                                                                FMGL(delta1))))) + (i15583356261)))) - (this5583556259->
                                                                                                                                                                          FMGL(min1))));
                                                                
                                                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                ret5583656262 =
                                                                  offset5583456263;
                                                            }
                                                            ret5583656262;
                                                        }))
                                                        );
                                                    }
                                                    ret5583056258;
                                                }))
                                                )->x10::lang::Complex::conjugate())->x10::lang::Complex::__times(
                                ((x10_double) (m_sign)));
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5584156264;
                            {
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(targetTerms)->
                                   FMGL(raw))->__set((__extension__ ({
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5584656265 =
                                      x10aux::nullCheck(targetTerms)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05584356266 =
                                      i05583856253;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15584456267 =
                                      i15583956254;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5584756268;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5584556269 =
                                          ((x10_int) ((i05584356266) - (this5584656265->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5584556269 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5584556269) * (this5584656265->
                                                                                                        FMGL(delta1))))) + (i15584456267)))) - (this5584656265->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5584756268 =
                                          offset5584556269;
                                    }
                                    ret5584756268;
                                }))
                                , v5584056255);
                                
                                //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5584156264 =
                                  v5584056255;
                            }
                            ret5584156264;
                        }))
                        ;
                    }
                    }
                }
                
            
            //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
            m_sign =
              ((x10_int) -(m_sign));
            }
        }
        
    
    //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
    x10aux::nullCheck(scratch)->backRotate(
      temp,
      (__extension__ ({
          
          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
          x10aux::ref<x10::array::Array<x10::lang::Complex> > ret55850;
          
          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
          goto __ret55851; __ret55851: {
          {
              
              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
              ret55850 =
                (x10aux::nullCheck(complexK)->
                   FMGL(raw))->__apply(((x10_int)0));
              
              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
              goto __ret55851_end_;
          }goto __ret55851_end_; __ret55851_end_: ;
          }
          ret55850;
          }))
          ,
          (__extension__ ({
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
              x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret55858;
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
              goto __ret55859; __ret55859: {
              {
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                  ret55858 =
                    (x10aux::nullCheck(wigner)->
                       FMGL(raw))->__apply(((x10_int)1));
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                  goto __ret55859_end_;
              }goto __ret55859_end_; __ret55859_end_: ;
              }
              ret55858;
              }))
              );
    
    //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
    ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->unsafeAdd(
      x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(scratch));
    }
    
    //#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
    void au::edu::anu::mm::LocalExpansion::translateAndAddLocal(
      x10x::vector::Vector3d v,
      x10aux::ref<au::edu::anu::mm::LocalExpansion> source) {
        
        //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10x::polar::Polar3d polar = x10x::polar::Polar3d::getPolar3d(
                                       x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(v));
        
        //#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
        ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->translateAndAddLocal(
          v,
          au::edu::anu::mm::Expansion::genComplexK(
            polar->
              FMGL(phi),
            ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
              FMGL(p)),
          source,
          (__extension__ ({
              
              //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
              x10_double theta55865 =
                polar->
                  FMGL(theta);
              
              //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
              x10_int numTerms55866 =
                ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                  FMGL(p);
              au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
                theta55865,
                numTerms55866,
                ((x10_int)2));
          }))
          );
    }
    
    //#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
    void au::edu::anu::mm::LocalExpansion::transformAndAddToLocal(
      x10aux::ref<au::edu::anu::mm::LocalExpansion> transform,
      x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source) {
        
        //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10_int i39195max3919756334 = ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                        FMGL(p);
        
        //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
        {
            x10_int i3919556335;
            for (
                 //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                 i3919556335 = ((x10_int)0);
                 ((i3919556335) <= (i39195max3919756334));
                 
                 //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                 i3919556335 =
                   ((x10_int) ((i3919556335) + (((x10_int)1)))))
            {
                
                //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_int j56336 =
                  i3919556335;
                
                //#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_int i39179min3918056329 =
                  ((x10_int) -(j56336));
                
                //#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_int i39179max3918156330 =
                  j56336;
                
                //#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                {
                    x10_int i3917956331;
                    for (
                         //#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                         i3917956331 =
                           i39179min3918056329;
                         ((i3917956331) <= (i39179max3918156330));
                         
                         //#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                         i3917956331 =
                           ((x10_int) ((i3917956331) + (((x10_int)1)))))
                    {
                        
                        //#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_int k56332 =
                          i3917956331;
                        
                        //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex O_jk56319 =
                          (__extension__ ({
                            
                            //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10::lang::Complex> > this5586956320 =
                              x10aux::nullCheck(source)->
                                FMGL(terms);
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05586756321 =
                              j56336;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15586856322 =
                              k56332;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5587056323;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5587056323 =
                                  (x10aux::nullCheck(this5586956320)->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5587556324 =
                                      x10aux::nullCheck(this5586956320)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05587256325 =
                                      i05586756321;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15587356326 =
                                      i15586856322;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5587656327;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5587456328 =
                                          ((x10_int) ((i05587256325) - (this5587556324->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5587456328 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5587456328) * (this5587556324->
                                                                                                        FMGL(delta1))))) + (i15587356326)))) - (this5587556324->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5587656327 =
                                          offset5587456328;
                                    }
                                    ret5587656327;
                                }))
                                );
                            }
                            ret5587056323;
                        }))
                        ;
                        
                        //#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_int i39163max3916556316 =
                          ((x10_int) ((((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                         FMGL(p)) - (j56336)));
                        
                        //#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                        {
                            x10_int i3916356317;
                            for (
                                 //#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                 i3916356317 =
                                   ((x10_int)0);
                                 ((i3916356317) <= (i39163max3916556316));
                                 
                                 //#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                 i3916356317 =
                                   ((x10_int) ((i3916356317) + (((x10_int)1)))))
                            {
                                
                                //#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                x10_int l56318 =
                                  i3916356317;
                                
                                //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                x10_int i39147min3914856311 =
                                  ((x10_int) -(l56318));
                                
                                //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                x10_int i39147max3914956312 =
                                  l56318;
                                
                                //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                                {
                                    x10_int i3914756313;
                                    for (
                                         //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                         i3914756313 =
                                           i39147min3914856311;
                                         ((i3914756313) <= (i39147max3914956312));
                                         
                                         //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                         i3914756313 =
                                           ((x10_int) ((i3914756313) + (((x10_int)1)))))
                                    {
                                        
                                        //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                        x10_int m56314 =
                                          i3914756313;
                                        
                                        //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10If_c
                                        if (((::abs(((x10_int) ((k56332) + (m56314))))) <= (((x10_int) ((j56336) + (l56318))))))
                                        {
                                            
                                            //#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                            x10::lang::Complex B_lmjk56282 =
                                              (__extension__ ({
                                                
                                                //#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                                x10aux::ref<x10::array::Array<x10::lang::Complex> > this5588056283 =
                                                  x10aux::nullCheck(transform)->
                                                    FMGL(terms);
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05587856284 =
                                                  ((x10_int) ((j56336) + (l56318)));
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15587956285 =
                                                  ((x10_int) ((k56332) + (m56314)));
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::lang::Complex ret5588156286;
                                                {
                                                    
                                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                    ret5588156286 =
                                                      (x10aux::nullCheck(this5588056283)->
                                                         FMGL(raw))->__apply((__extension__ ({
                                                        
                                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10::array::RectLayout this5588656287 =
                                                          x10aux::nullCheck(this5588056283)->
                                                            FMGL(layout);
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i05588356288 =
                                                          i05587856284;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i15588456289 =
                                                          i15587956285;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int ret5588756290;
                                                        {
                                                            
                                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                            x10_int offset5588556291 =
                                                              ((x10_int) ((i05588356288) - (this5588656287->
                                                                                              FMGL(min0))));
                                                            
                                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            offset5588556291 =
                                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5588556291) * (this5588656287->
                                                                                                                            FMGL(delta1))))) + (i15588456289)))) - (this5588656287->
                                                                                                                                                                      FMGL(min1))));
                                                            
                                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            ret5588756290 =
                                                              offset5588556291;
                                                        }
                                                        ret5588756290;
                                                    }))
                                                    );
                                                }
                                                ret5588156286;
                                            }))
                                            ;
                                            
                                            //#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                                            (__extension__ ({
                                                
                                                //#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                                x10aux::ref<x10::array::Array<x10::lang::Complex> > this5590356292 =
                                                  ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                    FMGL(terms);
                                                
                                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05590056293 =
                                                  l56318;
                                                
                                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15590156294 =
                                                  m56314;
                                                
                                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::lang::Complex v5590256295 =
                                                  x10aux::nullCheck((__extension__ ({
                                                      
                                                      //#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                                      x10aux::ref<x10::array::Array<x10::lang::Complex> > this5589156296 =
                                                        ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                          FMGL(terms);
                                                      
                                                      //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i05588956297 =
                                                        l56318;
                                                      
                                                      //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i15589056298 =
                                                        m56314;
                                                      
                                                      //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                      x10::lang::Complex ret5589256299;
                                                      {
                                                          
                                                          //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                          ret5589256299 =
                                                            (x10aux::nullCheck(this5589156296)->
                                                               FMGL(raw))->__apply((__extension__ ({
                                                              
                                                              //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                              x10::array::RectLayout this5589756300 =
                                                                x10aux::nullCheck(this5589156296)->
                                                                  FMGL(layout);
                                                              
                                                              //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                              x10_int i05589456301 =
                                                                i05588956297;
                                                              
                                                              //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                              x10_int i15589556302 =
                                                                i15589056298;
                                                              
                                                              //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                              x10_int ret5589856303;
                                                              {
                                                                  
                                                                  //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                  x10_int offset5589656304 =
                                                                    ((x10_int) ((i05589456301) - (this5589756300->
                                                                                                    FMGL(min0))));
                                                                  
                                                                  //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                  offset5589656304 =
                                                                    ((x10_int) ((((x10_int) ((((x10_int) ((offset5589656304) * (this5589756300->
                                                                                                                                  FMGL(delta1))))) + (i15589556302)))) - (this5589756300->
                                                                                                                                                                            FMGL(min1))));
                                                                  
                                                                  //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                  ret5589856303 =
                                                                    offset5589656304;
                                                              }
                                                              ret5589856303;
                                                          }))
                                                          );
                                                      }
                                                      ret5589256299;
                                                  }))
                                                  )->x10::lang::Complex::__plus(
                                                    x10aux::nullCheck(B_lmjk56282)->x10::lang::Complex::__times(
                                                      O_jk56319));
                                                
                                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::lang::Complex ret5590456305;
                                                {
                                                    
                                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                                    (x10aux::nullCheck(this5590356292)->
                                                       FMGL(raw))->__set((__extension__ ({
                                                        
                                                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10::array::RectLayout this5590956306 =
                                                          x10aux::nullCheck(this5590356292)->
                                                            FMGL(layout);
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i05590656307 =
                                                          i05590056293;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i15590756308 =
                                                          i15590156294;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int ret5591056309;
                                                        {
                                                            
                                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                            x10_int offset5590856310 =
                                                              ((x10_int) ((i05590656307) - (this5590956306->
                                                                                              FMGL(min0))));
                                                            
                                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            offset5590856310 =
                                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5590856310) * (this5590956306->
                                                                                                                            FMGL(delta1))))) + (i15590756308)))) - (this5590956306->
                                                                                                                                                                      FMGL(min1))));
                                                            
                                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            ret5591056309 =
                                                              offset5590856310;
                                                        }
                                                        ret5591056309;
                                                    }))
                                                    , v5590256295);
                                                    
                                                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                    ret5590456305 =
                                                      v5590256295;
                                                }
                                                ret5590456305;
                                            }))
                                            ;
                                        }
                                        
                                    }
                                }
                                
                            }
                        }
                        
                    }
                }
                
            }
        }
        
    }
    
    //#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
    void au::edu::anu::mm::LocalExpansion::transformAndAddToLocal(
      x10aux::ref<au::edu::anu::mm::MultipoleExpansion> scratch,
      x10aux::ref<x10::array::Array<x10::lang::Complex> > temp,
      x10x::vector::Vector3d v,
      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > complexK,
      x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source,
      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > wigner) {
        
        //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10_double inv_b = ((((x10_double) (((x10_int)1)))) / ((__extension__ ({
            x10aux::math_utils::sqrt(((((((v->
                                             FMGL(i)) * (v->
                                                           FMGL(i)))) + (((v->
                                                                             FMGL(j)) * (v->
                                                                                           FMGL(j)))))) + (((v->
                                                                                                               FMGL(k)) * (v->
                                                                                                                             FMGL(k))))));
        }))
        ));
        
        //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
        x10::array::Array<void>::copy<x10::lang::Complex >(
          x10aux::nullCheck(source)->
            FMGL(terms),
          x10aux::nullCheck(scratch)->
            FMGL(terms));
        
        //#line 188 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
        x10aux::nullCheck(scratch)->rotate(
          temp,
          (__extension__ ({
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
              x10aux::ref<x10::array::Array<x10::lang::Complex> > ret55913;
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
              goto __ret55914; __ret55914: {
              {
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                  ret55913 =
                    (x10aux::nullCheck(complexK)->
                       FMGL(raw))->__apply(((x10_int)0));
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                  goto __ret55914_end_;
              }goto __ret55914_end_; __ret55914_end_: ;
              }
              ret55913;
              }))
              ,
              (__extension__ ({
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret55921;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret55922; __ret55922: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret55921 =
                        (x10aux::nullCheck(wigner)->
                           FMGL(raw))->__apply(((x10_int)0));
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret55922_end_;
                  }goto __ret55922_end_; __ret55922_end_: ;
                  }
                  ret55921;
                  }))
                  );
        
        //#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > targetTerms =
          x10aux::nullCheck(scratch)->
            FMGL(terms);
        
        //#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10_int m_sign =
          ((x10_int)1);
        
        //#line 193 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10_double b_m_pow =
          1.0;
        
        //#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
        x10_int i39259max3926156406 =
          ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
            FMGL(p);
        
        //#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
        {
            x10_int i3925956407;
            for (
                 //#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                 i3925956407 =
                   ((x10_int)0);
                 ((i3925956407) <= (i39259max3926156406));
                 
                 //#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                 i3925956407 =
                   ((x10_int) ((i3925956407) + (((x10_int)1)))))
            {
                
                //#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_int m56408 =
                  i3925956407;
                
                //#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_int i39211min3921256396 =
                  m56408;
                
                //#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_int i39211max3921356397 =
                  ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                    FMGL(p);
                
                //#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                {
                    x10_int i3921156398;
                    for (
                         //#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                         i3921156398 =
                           i39211min3921256396;
                         ((i3921156398) <= (i39211max3921356397));
                         
                         //#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                         i3921156398 =
                           ((x10_int) ((i3921156398) + (((x10_int)1)))))
                    {
                        
                        //#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_int l56399 =
                          i3921156398;
                        
                        //#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05593856341 =
                              l56399;
                            
                            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex v5593956342 =
                              (__extension__ ({
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05592856343 =
                                  l56399;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15592956344 =
                                  ((x10_int) -(m56408));
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::lang::Complex ret5593056345;
                                {
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5593056345 =
                                      (x10aux::nullCheck(targetTerms)->
                                         FMGL(raw))->__apply((__extension__ ({
                                        
                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5593556346 =
                                          x10aux::nullCheck(targetTerms)->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05593256347 =
                                          i05592856343;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15593356348 =
                                          i15592956344;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5593656349;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5593456350 =
                                              ((x10_int) ((i05593256347) - (this5593556346->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5593456350 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5593456350) * (this5593556346->
                                                                                                            FMGL(delta1))))) + (i15593356348)))) - (this5593556346->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5593656349 =
                                              offset5593456350;
                                        }
                                        ret5593656349;
                                    }))
                                    );
                                }
                                ret5593056345;
                            }))
                            ;
                            
                            //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5594056351;
                            {
                                
                                //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(temp)->
                                   FMGL(raw))->__set((__extension__ ({
                                    
                                    //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5594456337 =
                                      x10aux::nullCheck(temp)->
                                        FMGL(layout);
                                    
                                    //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05594256338 =
                                      i05593856341;
                                    
                                    //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5594556339;
                                    {
                                        
                                        //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5594356340 =
                                          ((x10_int) ((i05594256338) - (this5594456337->
                                                                          FMGL(min0))));
                                        
                                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5594556339 =
                                          offset5594356340;
                                    }
                                    ret5594556339;
                                }))
                                , v5593956342);
                                
                                //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5594056351 =
                                  v5593956342;
                            }
                            ret5594056351;
                        }))
                        ;
                    }
                }
                
                //#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_double b_lm1_pow56404 =
                  ((((inv_b) * (b_m_pow))) * (b_m_pow));
                
                //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_int i39243min3924456400 =
                  m56408;
                
                //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10_int i39243max3924556401 =
                  ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                    FMGL(p);
                
                //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                {
                    x10_int i3924356402;
                    for (
                         //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                         i3924356402 =
                           i39243min3924456400;
                         ((i3924356402) <= (i39243max3924556401));
                         
                         //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                         i3924356402 =
                           ((x10_int) ((i3924356402) + (((x10_int)1)))))
                    {
                        
                        //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_int l56403 =
                          i3924356402;
                        
                        //#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex M_lm56363 =
                          x10::lang::Complex::_make(0.0,0.0);
                        
                        //#line 200 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_double F_lm56364 =
                          (((__extension__ ({
                            
                            //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                            x10_int i5594756365 =
                              ((x10_int) ((l56403) + (m56408)));
                            (__extension__ ({
                                
                                //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::array::Array<x10_double> > this5594956366 =
                                  au::edu::anu::mm::Factorial::
                                    FMGL(factorial__get)();
                                
                                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05594856367 =
                                  i5594756365;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double ret5595056368;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                goto __ret5595156369; __ret5595156369: {
                                {
                                    
                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5595056368 =
                                      (x10aux::nullCheck(this5594956366)->
                                         FMGL(raw))->__apply(i05594856367);
                                    
                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                    goto __ret5595156369_end_;
                                }goto __ret5595156369_end_; __ret5595156369_end_: ;
                                }
                                ret5595056368;
                                }))
                                ;
                            }))
                            ) * (b_lm1_pow56404));
                            
                        
                        //#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_int i39227min3922856359 =
                          m56408;
                        
                        //#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_int i39227max3922956360 =
                          ((x10_int) ((((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                         FMGL(p)) - (l56403)));
                        
                        //#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                        {
                            x10_int i3922756361;
                            for (
                                 //#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                 i3922756361 =
                                   i39227min3922856359;
                                 ((i3922756361) <= (i39227max3922956360));
                                 
                                 //#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                 i3922756361 =
                                   ((x10_int) ((i3922756361) + (((x10_int)1)))))
                            {
                                
                                //#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                x10_int j56362 =
                                  i3922756361;
                                
                                //#line 202 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                M_lm56363 =
                                  x10aux::nullCheck(M_lm56363)->x10::lang::Complex::__plus(
                                    x10aux::nullCheck((__extension__ ({
                                        
                                        //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05595756356 =
                                          j56362;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex ret5595856357;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                        goto __ret5595956358; __ret5595956358: {
                                        {
                                            
                                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5595856357 =
                                              (x10aux::nullCheck(temp)->
                                                 FMGL(raw))->__apply((__extension__ ({
                                                
                                                //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::array::RectLayout this5596256352 =
                                                  x10aux::nullCheck(temp)->
                                                    FMGL(layout);
                                                
                                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05596056353 =
                                                  i05595756356;
                                                
                                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int ret5596356354;
                                                {
                                                    
                                                    //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int offset5596156355 =
                                                      ((x10_int) ((i05596056353) - (this5596256352->
                                                                                      FMGL(min0))));
                                                    
                                                    //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    ret5596356354 =
                                                      offset5596156355;
                                                }
                                                ret5596356354;
                                            }))
                                            );
                                            
                                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                            goto __ret5595956358_end_;
                                        }goto __ret5595956358_end_; __ret5595956358_end_: ;
                                        }
                                        ret5595856357;
                                        }))
                                        )->x10::lang::Complex::__times(
                                          F_lm56364));
                                
                                //#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                F_lm56364 =
                                  ((((F_lm56364) * (((x10_double) (((x10_int) ((((x10_int) ((j56362) + (l56403)))) + (((x10_int)1))))))))) * (inv_b));
                                }
                            }
                            
                        
                        //#line 205 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05596556370 =
                              l56403;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15596656371 =
                              m56408;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex v5596756372 =
                              M_lm56363;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5596856373;
                            {
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(targetTerms)->
                                   FMGL(raw))->__set((__extension__ ({
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5597356374 =
                                      x10aux::nullCheck(targetTerms)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05597056375 =
                                      i05596556370;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15597156376 =
                                      i15596656371;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5597456377;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5597256378 =
                                          ((x10_int) ((i05597056375) - (this5597356374->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5597256378 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5597256378) * (this5597356374->
                                                                                                        FMGL(delta1))))) + (i15597156376)))) - (this5597356374->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5597456377 =
                                          offset5597256378;
                                    }
                                    ret5597456377;
                                }))
                                , v5596756372);
                                
                                //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5596856373 =
                                  v5596756372;
                            }
                            ret5596856373;
                        }))
                        ;
                        
                        //#line 206 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10If_c
                        if ((!x10aux::struct_equals(m56408,
                                                    ((x10_int)0))))
                        {
                            
                            //#line 206 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                            (__extension__ ({
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05598656379 =
                                  l56403;
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15598756380 =
                                  ((x10_int) -(m56408));
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::lang::Complex v5598856381 =
                                  x10aux::nullCheck(x10aux::nullCheck((__extension__ ({
                                                        
                                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i05597656382 =
                                                          l56403;
                                                        
                                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i15597756383 =
                                                          m56408;
                                                        
                                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10::lang::Complex ret5597856384;
                                                        {
                                                            
                                                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                            ret5597856384 =
                                                              (x10aux::nullCheck(targetTerms)->
                                                                 FMGL(raw))->__apply((__extension__ ({
                                                                
                                                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                x10::array::RectLayout this5598356385 =
                                                                  x10aux::nullCheck(targetTerms)->
                                                                    FMGL(layout);
                                                                
                                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                x10_int i05598056386 =
                                                                  i05597656382;
                                                                
                                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                x10_int i15598156387 =
                                                                  i15597756383;
                                                                
                                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                x10_int ret5598456388;
                                                                {
                                                                    
                                                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int offset5598256389 =
                                                                      ((x10_int) ((i05598056386) - (this5598356385->
                                                                                                      FMGL(min0))));
                                                                    
                                                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                    offset5598256389 =
                                                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5598256389) * (this5598356385->
                                                                                                                                    FMGL(delta1))))) + (i15598156387)))) - (this5598356385->
                                                                                                                                                                              FMGL(min1))));
                                                                    
                                                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                    ret5598456388 =
                                                                      offset5598256389;
                                                                }
                                                                ret5598456388;
                                                            }))
                                                            );
                                                        }
                                                        ret5597856384;
                                                    }))
                                                    )->x10::lang::Complex::conjugate())->x10::lang::Complex::__times(
                                    ((x10_double) (m_sign)));
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::lang::Complex ret5598956390;
                                {
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                    (x10aux::nullCheck(targetTerms)->
                                       FMGL(raw))->__set((__extension__ ({
                                        
                                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5599456391 =
                                          x10aux::nullCheck(targetTerms)->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05599156392 =
                                          i05598656379;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15599256393 =
                                          i15598756380;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5599556394;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5599356395 =
                                              ((x10_int) ((i05599156392) - (this5599456391->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5599356395 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5599356395) * (this5599456391->
                                                                                                            FMGL(delta1))))) + (i15599256393)))) - (this5599456391->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5599556394 =
                                              offset5599356395;
                                        }
                                        ret5599556394;
                                    }))
                                    , v5598856381);
                                    
                                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5598956390 =
                                      v5598856381;
                                }
                                ret5598956390;
                            }))
                            ;
                        }
                        
                        //#line 207 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                        b_lm1_pow56404 =
                          ((b_lm1_pow56404) * (inv_b));
                        }
                        }
                        
                    
                    //#line 210 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                    m_sign =
                      ((x10_int) -(m_sign));
                    
                    //#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                    b_m_pow =
                      ((b_m_pow) * (inv_b));
                }
                }
                
            
            //#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
            x10aux::nullCheck(scratch)->backRotate(
              temp,
              (__extension__ ({
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10::lang::Complex> > ret55998;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret55999; __ret55999: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret55998 =
                        (x10aux::nullCheck(complexK)->
                           FMGL(raw))->__apply(((x10_int)0));
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret55999_end_;
                  }goto __ret55999_end_; __ret55999_end_: ;
                  }
                  ret55998;
                  }))
                  ,
                  (__extension__ ({
                      
                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret56006;
                      
                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                      goto __ret56007; __ret56007: {
                      {
                          
                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                          ret56006 =
                            (x10aux::nullCheck(wigner)->
                               FMGL(raw))->__apply(((x10_int)1));
                          
                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                          goto __ret56007_end_;
                      }goto __ret56007_end_; __ret56007_end_: ;
                      }
                      ret56006;
                      }))
                      );
            
            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
            ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->unsafeAdd(
              x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(scratch));
            }
            
            //#line 223 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
            void
              au::edu::anu::mm::LocalExpansion::transformAndAddToLocal(
              x10x::vector::Vector3d v,
              x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source) {
                
                //#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10x::polar::Polar3d polar =
                  x10x::polar::Polar3d::getPolar3d(
                    x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(v));
                
                //#line 225 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::MultipoleExpansion> scratch =
                  
                x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
                ;
                
                //#line 225 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
                (scratch)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
                  ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                    FMGL(p),
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                
                //#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10::lang::Complex> > temp =
                  (__extension__ ({
                    
                    //#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc38968 =
                      
                    x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
                    ;
                    
                    //#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Region> reg56015 =
                          (__extension__ ({
                            
                            //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                            x10::lang::IntRange r56013 =
                              x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                                        FMGL(p))), ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                                                     FMGL(p));
                            x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                
                                //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::array::RectRegion1D> alloc2063156014 =
                                  
                                x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                ;
                                
                                //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                                (alloc2063156014)->::x10::array::RectRegion1D::_constructor(
                                  r56013->
                                    FMGL(min),
                                  r56013->
                                    FMGL(max));
                                alloc2063156014;
                            }))
                            );
                        }))
                        ;
                        {
                            
                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                            (alloc38968)->::x10::lang::Object::_constructor();
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38968)->
                              FMGL(region) =
                              (reg56015);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38968)->
                              FMGL(rank) =
                              x10aux::nullCheck(reg56015)->
                                FMGL(rank);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38968)->
                              FMGL(rect) =
                              x10aux::nullCheck(reg56015)->
                                FMGL(rect);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38968)->
                              FMGL(zeroBased) =
                              x10aux::nullCheck(reg56015)->
                                FMGL(zeroBased);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38968)->
                              FMGL(rail) =
                              x10aux::nullCheck(reg56015)->
                                FMGL(rail);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38968)->
                              FMGL(size) =
                              x10aux::nullCheck(reg56015)->size();
                            
                            //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38968)->
                              FMGL(layout) =
                              (__extension__ ({
                                
                                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout alloc1995456016 =
                                  
                                x10::array::RectLayout::_alloc();
                                
                                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                                (alloc1995456016)->::x10::array::RectLayout::_constructor(
                                  reg56015);
                                alloc1995456016;
                            }))
                            ;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int n56017 =
                              (__extension__ ({
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this56019 =
                                  x10aux::nullCheck(alloc38968)->
                                    FMGL(layout);
                                this56019->
                                  FMGL(size);
                            }))
                            ;
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38968)->
                              FMGL(raw) =
                              x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n56017, 8, false, true);
                        }
                        
                    }))
                    ;
                    alloc38968;
                }))
                ;
                
                //#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->transformAndAddToLocal(
                  scratch,
                  temp,
                  v,
                  au::edu::anu::mm::Expansion::genComplexK(
                    polar->
                      FMGL(phi),
                    ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                      FMGL(p)),
                  source,
                  (__extension__ ({
                      
                      //#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                      x10_double theta56020 =
                        polar->
                          FMGL(theta);
                      
                      //#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                      x10_int numTerms56021 =
                        ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                          FMGL(p);
                      au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
                        theta56020,
                        numTerms56021,
                        ((x10_int)-1));
                  }))
                  );
            }
            
            //#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
            x10aux::ref<au::edu::anu::mm::LocalExpansion>
              au::edu::anu::mm::LocalExpansion::rotate(
              x10_double theta,
              x10_double phi) {
                
                //#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::LocalExpansion> target =
                  
                x10aux::ref<au::edu::anu::mm::LocalExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion()))
                ;
                
                //#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
                (target)->::au::edu::anu::mm::LocalExpansion::_constructor(
                  ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this),
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                
                //#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10::lang::Complex> > temp =
                  (__extension__ ({
                    
                    //#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc38969 =
                      
                    x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
                    ;
                    
                    //#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Region> reg56024 =
                          (__extension__ ({
                            
                            //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                            x10::lang::IntRange r56022 =
                              x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                                        FMGL(p))), ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                                                     FMGL(p));
                            x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                
                                //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::array::RectRegion1D> alloc2063156023 =
                                  
                                x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                ;
                                
                                //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                                (alloc2063156023)->::x10::array::RectRegion1D::_constructor(
                                  r56022->
                                    FMGL(min),
                                  r56022->
                                    FMGL(max));
                                alloc2063156023;
                            }))
                            );
                        }))
                        ;
                        {
                            
                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                            (alloc38969)->::x10::lang::Object::_constructor();
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38969)->
                              FMGL(region) =
                              (reg56024);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38969)->
                              FMGL(rank) =
                              x10aux::nullCheck(reg56024)->
                                FMGL(rank);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38969)->
                              FMGL(rect) =
                              x10aux::nullCheck(reg56024)->
                                FMGL(rect);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38969)->
                              FMGL(zeroBased) =
                              x10aux::nullCheck(reg56024)->
                                FMGL(zeroBased);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38969)->
                              FMGL(rail) =
                              x10aux::nullCheck(reg56024)->
                                FMGL(rail);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38969)->
                              FMGL(size) =
                              x10aux::nullCheck(reg56024)->size();
                            
                            //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38969)->
                              FMGL(layout) =
                              (__extension__ ({
                                
                                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout alloc1995456025 =
                                  
                                x10::array::RectLayout::_alloc();
                                
                                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                                (alloc1995456025)->::x10::array::RectLayout::_constructor(
                                  reg56024);
                                alloc1995456025;
                            }))
                            ;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int n56026 =
                              (__extension__ ({
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this56028 =
                                  x10aux::nullCheck(alloc38969)->
                                    FMGL(layout);
                                this56028->
                                  FMGL(size);
                            }))
                            ;
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc38969)->
                              FMGL(raw) =
                              x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n56026, 8, false, true);
                        }
                        
                    }))
                    ;
                    alloc38969;
                }))
                ;
                
                //#line 238 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Call_c
                target->rotate(
                  temp,
                  (__extension__ ({
                      
                      //#line 238 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > this56030 =
                        au::edu::anu::mm::Expansion::genComplexK(
                          phi,
                          ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                            FMGL(p));
                      
                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                      x10aux::ref<x10::array::Array<x10::lang::Complex> > ret56031;
                      
                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                      goto __ret56032; __ret56032: {
                      {
                          
                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                          ret56031 =
                            (x10aux::nullCheck(this56030)->
                               FMGL(raw))->__apply(((x10_int)0));
                          
                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                          goto __ret56032_end_;
                      }goto __ret56032_end_; __ret56032_end_: ;
                      }
                      ret56031;
                      }))
                      ,
                      (__extension__ ({
                          
                          //#line 238 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                          x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > this56041 =
                            (__extension__ ({
                              
                              //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                              x10_double theta56038 =
                                theta;
                              
                              //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                              x10_int numTerms56039 =
                                ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                  FMGL(p);
                              au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
                                theta56038,
                                numTerms56039,
                                ((x10_int)2));
                          }))
                          ;
                          
                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret56042;
                          
                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                          goto __ret56043; __ret56043: {
                          {
                              
                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                              ret56042 =
                                (x10aux::nullCheck(this56041)->
                                   FMGL(raw))->__apply(((x10_int)0));
                              
                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                              goto __ret56043_end_;
                          }goto __ret56043_end_; __ret56043_end_: ;
                          }
                          ret56042;
                          }))
                          );
                
                //#line 239 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Return_c
                return target;
                }
                
                //#line 248 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
                x10_double
                  au::edu::anu::mm::LocalExpansion::getPotential(
                  x10_double q,
                  x10aux::ref<x10x::vector::Tuple3d> v) {
                    
                    //#line 250 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> transform =
                      au::edu::anu::mm::MultipoleExpansion::getOlm(
                        q,
                        v,
                        ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                          FMGL(p));
                    
                    //#line 251 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_double potential =
                      0.0;
                    
                    //#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i39291max3929356432 =
                      ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                        FMGL(p);
                    
                    //#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                    {
                        x10_int i3929156433;
                        for (
                             //#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                             i3929156433 =
                               ((x10_int)0);
                             ((i3929156433) <= (i39291max3929356432));
                             
                             //#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                             i3929156433 =
                               ((x10_int) ((i3929156433) + (((x10_int)1)))))
                        {
                            
                            //#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int j56434 =
                              i3929156433;
                            
                            //#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int i39275min3927656427 =
                              ((x10_int) -(j56434));
                            
                            //#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int i39275max3927756428 =
                              j56434;
                            
                            //#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                            {
                                x10_int i3927556429;
                                for (
                                     //#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                     i3927556429 =
                                       i39275min3927656427;
                                     ((i3927556429) <= (i39275max3927756428));
                                     
                                     //#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                     i3927556429 =
                                       ((x10_int) ((i3927556429) + (((x10_int)1)))))
                                {
                                    
                                    //#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10_int k56430 =
                                      i3927556429;
                                    
                                    //#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                    potential =
                                      ((potential) + (x10aux::nullCheck((__extension__ ({
                                                          
                                                          //#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                                          x10aux::ref<x10::array::Array<x10::lang::Complex> > this5605156409 =
                                                            ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                              FMGL(terms);
                                                          
                                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                          x10_int i05604956410 =
                                                            j56434;
                                                          
                                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                          x10_int i15605056411 =
                                                            k56430;
                                                          
                                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                          x10::lang::Complex ret5605256412;
                                                          {
                                                              
                                                              //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                              ret5605256412 =
                                                                (x10aux::nullCheck(this5605156409)->
                                                                   FMGL(raw))->__apply((__extension__ ({
                                                                  
                                                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                  x10::array::RectLayout this5605756413 =
                                                                    x10aux::nullCheck(this5605156409)->
                                                                      FMGL(layout);
                                                                  
                                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                  x10_int i05605456414 =
                                                                    i05604956410;
                                                                  
                                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                  x10_int i15605556415 =
                                                                    i15605056411;
                                                                  
                                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                  x10_int ret5605856416;
                                                                  {
                                                                      
                                                                      //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                      x10_int offset5605656417 =
                                                                        ((x10_int) ((i05605456414) - (this5605756413->
                                                                                                        FMGL(min0))));
                                                                      
                                                                      //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                      offset5605656417 =
                                                                        ((x10_int) ((((x10_int) ((((x10_int) ((offset5605656417) * (this5605756413->
                                                                                                                                      FMGL(delta1))))) + (i15605556415)))) - (this5605756413->
                                                                                                                                                                                FMGL(min1))));
                                                                      
                                                                      //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                      ret5605856416 =
                                                                        offset5605656417;
                                                                  }
                                                                  ret5605856416;
                                                              }))
                                                              );
                                                          }
                                                          ret5605256412;
                                                      }))
                                                      )->x10::lang::Complex::__times(
                                                        (__extension__ ({
                                                            
                                                            //#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                                            x10aux::ref<x10::array::Array<x10::lang::Complex> > this5606256418 =
                                                              x10aux::nullCheck(transform)->
                                                                FMGL(terms);
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i05606056419 =
                                                              j56434;
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i15606156420 =
                                                              k56430;
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10::lang::Complex ret5606356421;
                                                            {
                                                                
                                                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                ret5606356421 =
                                                                  (x10aux::nullCheck(this5606256418)->
                                                                     FMGL(raw))->__apply((__extension__ ({
                                                                    
                                                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                    x10::array::RectLayout this5606856422 =
                                                                      x10aux::nullCheck(this5606256418)->
                                                                        FMGL(layout);
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int i05606556423 =
                                                                      i05606056419;
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int i15606656424 =
                                                                      i15606156420;
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int ret5606956425;
                                                                    {
                                                                        
                                                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                        x10_int offset5606756426 =
                                                                          ((x10_int) ((i05606556423) - (this5606856422->
                                                                                                          FMGL(min0))));
                                                                        
                                                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                        offset5606756426 =
                                                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5606756426) * (this5606856422->
                                                                                                                                        FMGL(delta1))))) + (i15606656424)))) - (this5606856422->
                                                                                                                                                                                  FMGL(min1))));
                                                                        
                                                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                        ret5606956425 =
                                                                          offset5606756426;
                                                                    }
                                                                    ret5606956425;
                                                                }))
                                                                );
                                                            }
                                                            ret5606356421;
                                                        }))
                                                        )->
                                                        FMGL(re)));
                                }
                            }
                            
                        }
                    }
                    
                    //#line 259 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Return_c
                    return potential;
                    
                }
                
                //#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
                x10aux::ref<au::edu::anu::mm::LocalExpansion>
                  au::edu::anu::mm::LocalExpansion::getMacroscopicParent(
                  ) {
                    
                    //#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::LocalExpansion> parentExpansion =
                      
                    x10aux::ref<au::edu::anu::mm::LocalExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion()))
                    ;
                    
                    //#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10ConstructorCall_c
                    (parentExpansion)->::au::edu::anu::mm::LocalExpansion::_constructor(
                      ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                        FMGL(p),
                      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                    
                    //#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i39323max3932556459 =
                      ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                        FMGL(p);
                    
                    //#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                    {
                        x10_int i3932356460;
                        for (
                             //#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                             i3932356460 =
                               ((x10_int)0);
                             ((i3932356460) <= (i39323max3932556459));
                             
                             //#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                             i3932356460 =
                               ((x10_int) ((i3932356460) + (((x10_int)1)))))
                        {
                            
                            //#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int l56461 =
                              i3932356460;
                            
                            //#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int i39307min3930856454 =
                              ((x10_int) -(l56461));
                            
                            //#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int i39307max3930956455 =
                              l56461;
                            
                            //#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": polyglot.ast.For_c
                            {
                                x10_int i3930756456;
                                for (
                                     //#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                     i3930756456 =
                                       i39307min3930856454;
                                     ((i3930756456) <= (i39307max3930956455));
                                     
                                     //#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalAssign_c
                                     i3930756456 =
                                       ((x10_int) ((i3930756456) + (((x10_int)1)))))
                                {
                                    
                                    //#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10_int m56457 =
                                      i3930756456;
                                    
                                    //#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.StmtExpr_c
                                    (__extension__ ({
                                        
                                        //#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5608556435 =
                                          parentExpansion->
                                            FMGL(terms);
                                        
                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05608256436 =
                                          l56461;
                                        
                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15608356437 =
                                          m56457;
                                        
                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex v5608456438 =
                                          x10aux::nullCheck((__extension__ ({
                                              
                                              //#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10LocalDecl_c
                                              x10aux::ref<x10::array::Array<x10::lang::Complex> > this5607356439 =
                                                ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                                                  FMGL(terms);
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int i05607156440 =
                                                l56461;
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int i15607256441 =
                                                m56457;
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10::lang::Complex ret5607456442;
                                              {
                                                  
                                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                  ret5607456442 =
                                                    (x10aux::nullCheck(this5607356439)->
                                                       FMGL(raw))->__apply((__extension__ ({
                                                      
                                                      //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                      x10::array::RectLayout this5607956443 =
                                                        x10aux::nullCheck(this5607356439)->
                                                          FMGL(layout);
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i05607656444 =
                                                        i05607156440;
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i15607756445 =
                                                        i15607256441;
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int ret5608056446;
                                                      {
                                                          
                                                          //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                          x10_int offset5607856447 =
                                                            ((x10_int) ((i05607656444) - (this5607956443->
                                                                                            FMGL(min0))));
                                                          
                                                          //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                          offset5607856447 =
                                                            ((x10_int) ((((x10_int) ((((x10_int) ((offset5607856447) * (this5607956443->
                                                                                                                          FMGL(delta1))))) + (i15607756445)))) - (this5607956443->
                                                                                                                                                                    FMGL(min1))));
                                                          
                                                          //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                          ret5608056446 =
                                                            offset5607856447;
                                                      }
                                                      ret5608056446;
                                                  }))
                                                  );
                                              }
                                              ret5607456442;
                                          }))
                                          )->x10::lang::Complex::__over(
                                            x10aux::math_utils::pow(3.0,((x10_double) (((x10_int) ((l56461) + (((x10_int)1))))))));
                                        
                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex ret5608656448;
                                        {
                                            
                                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                            (x10aux::nullCheck(this5608556435)->
                                               FMGL(raw))->__set((__extension__ ({
                                                
                                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::array::RectLayout this5609156449 =
                                                  x10aux::nullCheck(this5608556435)->
                                                    FMGL(layout);
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05608856450 =
                                                  i05608256436;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15608956451 =
                                                  i15608356437;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int ret5609256452;
                                                {
                                                    
                                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int offset5609056453 =
                                                      ((x10_int) ((i05608856450) - (this5609156449->
                                                                                      FMGL(min0))));
                                                    
                                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    offset5609056453 =
                                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5609056453) * (this5609156449->
                                                                                                                    FMGL(delta1))))) + (i15608956451)))) - (this5609156449->
                                                                                                                                                              FMGL(min1))));
                                                    
                                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    ret5609256452 =
                                                      offset5609056453;
                                                }
                                                ret5609256452;
                                            }))
                                            , v5608456438);
                                            
                                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5608656448 =
                                              v5608456438;
                                        }
                                        ret5608656448;
                                    }))
                                    ;
                                }
                            }
                            
                        }
                    }
                    
                    //#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Return_c
                    return parentExpansion;
                    
                }
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
                x10aux::ref<au::edu::anu::mm::LocalExpansion>
                  au::edu::anu::mm::LocalExpansion::au__edu__anu__mm__LocalExpansion____au__edu__anu__mm__LocalExpansion__this(
                  ) {
                    
                    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10Return_c
                    return ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this);
                    
                }
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10MethodDecl_c
                void
                  au::edu::anu::mm::LocalExpansion::__fieldInitializers38498(
                  ) {
                    
                    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10": x10.ast.X10FieldAssign_c
                    ((x10aux::ref<au::edu::anu::mm::LocalExpansion>)this)->
                      FMGL(X10__object_lock_id0) =
                      ((x10_int)-1);
                }
                const x10aux::serialization_id_t au::edu::anu::mm::LocalExpansion::_serialization_id = 
                    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::LocalExpansion::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);
                
                void au::edu::anu::mm::LocalExpansion::_serialize_body(x10aux::serialization_buffer& buf) {
                    au::edu::anu::mm::Expansion::_serialize_body(buf);
                    
                }
                
                x10aux::ref<x10::lang::Reference> au::edu::anu::mm::LocalExpansion::_deserializer(x10aux::deserialization_buffer& buf) {
                    x10aux::ref<au::edu::anu::mm::LocalExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::LocalExpansion>(), 0, sizeof(au::edu::anu::mm::LocalExpansion))) au::edu::anu::mm::LocalExpansion();
                    buf.record_reference(this_);
                    this_->_deserialize_body(buf);
                    return this_;
                }
                
                void au::edu::anu::mm::LocalExpansion::_deserialize_body(x10aux::deserialization_buffer& buf) {
                    au::edu::anu::mm::Expansion::_deserialize_body(buf);
                    
                }
                
                
            x10aux::RuntimeType au::edu::anu::mm::LocalExpansion::rtt;
            void au::edu::anu::mm::LocalExpansion::_initRTT() {
                if (rtt.initStageOne(&rtt)) return;
                const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<au::edu::anu::mm::Expansion>()};
                rtt.initStageTwo("au.edu.anu.mm.LocalExpansion",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
            }
            /* END of LocalExpansion */
/*************************************************/
