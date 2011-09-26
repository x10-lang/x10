/*************************************************/
/* START of MultipoleExpansion */
#include <au/edu/anu/mm/MultipoleExpansion.h>

#include <au/edu/anu/mm/Expansion.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Double.h>
#include <x10x/vector/Tuple3d.h>
#include <x10x/polar/Polar3d.h>
#include <x10/array/Array.h>
#include <au/edu/anu/mm/AssociatedLegendrePolynomial.h>
#include <x10/lang/Complex.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/array/RectLayout.h>
#include <x10/lang/Math.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/Runtime.h>
#include <x10/compiler/Finalization.h>
#include <x10/compiler/Abort.h>
#include <x10/compiler/CompilerFlags.h>
#include <x10x/vector/Vector3d.h>
#include <x10/array/Region.h>
#include <x10/lang/IntRange.h>
#include <x10/array/RectRegion1D.h>
#include <au/edu/anu/mm/Factorial.h>
#include <au/edu/anu/mm/WignerRotationMatrix.h>
#include <x10/lang/Iterator.h>
#include <x10/array/Point.h>

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::MultipoleExpansion::getOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::MultipoleExpansion.X10$class_lock_id1");
    x10_int __var409__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var409__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var410__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var410__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::MultipoleExpansion.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::MultipoleExpansion.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__411 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__deserialize));


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::MultipoleExpansion::getStaticOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId52317 = au::edu::anu::mm::MultipoleExpansion::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId52317);
    }))
    ;
    
}

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MultipoleExpansion::_constructor(
  x10_int p) {
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Expansion>)this))->::au::edu::anu::mm::Expansion::_constructor(
      p);
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this5231854068 =
          ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5231854068)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
}
x10aux::ref<au::edu::anu::mm::MultipoleExpansion> au::edu::anu::mm::MultipoleExpansion::_make(
  x10_int p) {
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion();
    this_->_constructor(p);
    return this_;
}



//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MultipoleExpansion::_constructor(
  x10_int p,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Expansion>)this))->::au::edu::anu::mm::Expansion::_constructor(
      p);
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this5232154069 =
          ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5232154069)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::MultipoleExpansion> au::edu::anu::mm::MultipoleExpansion::_make(
  x10_int p,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion();
    this_->_constructor(p,
    paramLock);
    return this_;
}



//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MultipoleExpansion::_constructor(
  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source)
{
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Expansion>)this))->::au::edu::anu::mm::Expansion::_constructor(
      x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(source));
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this5232454070 =
          ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5232454070)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
}
x10aux::ref<au::edu::anu::mm::MultipoleExpansion> au::edu::anu::mm::MultipoleExpansion::_make(
  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source)
{
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion();
    this_->_constructor(source);
    return this_;
}



//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MultipoleExpansion::_constructor(
  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::Expansion>)this))->::au::edu::anu::mm::Expansion::_constructor(
      x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(source));
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this5232754071 =
          ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5232754071)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::MultipoleExpansion> au::edu::anu::mm::MultipoleExpansion::_make(
  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion();
    this_->_constructor(source,
    paramLock);
    return this_;
}



//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
  au::edu::anu::mm::MultipoleExpansion::getOlm(
  x10_double q,
  x10aux::ref<x10x::vector::Tuple3d> v,
  x10_int p) {
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> exp =
      
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
    ;
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
    (exp)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
      p,
      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10x::polar::Polar3d v_pole = x10x::polar::Polar3d::getPolar3d(
                                    v);
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10_double> > pplm =
      au::edu::anu::mm::AssociatedLegendrePolynomial::getPlk(
        v_pole->
          FMGL(theta),
        p);
    
    //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > this53666 =
          exp->
            FMGL(terms);
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10::lang::Complex v53665 = (__extension__ ({
            
            //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10::lang::Complex alloc37748 =
              
            x10::lang::Complex::_alloc();
            
            //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
                x10_double real53660 = ((q) * ((__extension__ ({
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret52749;
                    {
                        
                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret52749 = (x10aux::nullCheck(pplm)->
                                      FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this52754 =
                              x10aux::nullCheck(pplm)->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret52755;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset52753 =
                                  ((x10_int) ((((x10_int)0)) - (this52754->
                                                                  FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset52753 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset52753) * (this52754->
                                                                                           FMGL(delta1))))) + (((x10_int)0))))) - (this52754->
                                                                                                                                     FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret52755 =
                                  offset52753;
                            }
                            ret52755;
                        }))
                        );
                    }
                    ret52749;
                }))
                ));
                {
                    
                    //#line 53 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
                    alloc37748->FMGL(re) =
                      real53660;
                    
                    //#line 54 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
                    alloc37748->FMGL(im) =
                      0.0;
                }
                
            }))
            ;
            alloc37748;
        }))
        ;
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10::lang::Complex ret53667;
        {
            
            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
            (x10aux::nullCheck(this53666)->
               FMGL(raw))->__set((__extension__ ({
                
                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this53672 =
                  x10aux::nullCheck(this53666)->
                    FMGL(layout);
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                x10_int ret53673;
                {
                    
                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int offset53671 =
                      ((x10_int) ((((x10_int)0)) - (this53672->
                                                      FMGL(min0))));
                    
                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    offset53671 = ((x10_int) ((((x10_int) ((((x10_int) ((offset53671) * (this53672->
                                                                                           FMGL(delta1))))) + (((x10_int)0))))) - (this53672->
                                                                                                                                     FMGL(min1))));
                    
                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    ret53673 = offset53671;
                }
                ret53673;
            }))
            , v53665);
            
            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret53667 = v53665;
        }
        ret53667;
    }))
    ;
    
    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10::lang::Complex phifac0 =  x10::lang::Complex::_alloc();
    
    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
        x10_double real53675 = x10aux::math_utils::cos((-(v_pole->
                                                            FMGL(phi))));
        
        //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
        x10_double imaginary53676 = x10aux::math_utils::sin((-(v_pole->
                                                                 FMGL(phi))));
        {
            
            //#line 53 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
            phifac0->FMGL(re) = real53675;
            
            //#line 54 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
            phifac0->FMGL(im) = imaginary53676;
        }
        
    }))
    ;
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double rfac = v_pole->FMGL(r);
    
    //#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double il = 1.0;
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_int i37785max3778754135 = p;
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
    {
        x10_int i3778554136;
        for (
             //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
             i3778554136 = ((x10_int)1); ((i3778554136) <= (i37785max3778754135));
             
             //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
             i3778554136 =
               ((x10_int) ((i3778554136) + (((x10_int)1)))))
        {
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int l54137 =
              i3778554136;
            
            //#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
            il =
              ((il) * (((x10_double) (l54137))));
            
            //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_double ilm54118 =
              il;
            
            //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10::lang::Complex phifac54119 =
              x10::lang::Complex::_make(1.0,0.0);
            
            //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10::lang::Complex> > this5369154120 =
                  exp->
                    FMGL(terms);
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05368854121 =
                  l54137;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::lang::Complex v5369054122 =
                  x10aux::nullCheck(x10aux::nullCheck(phifac54119)->x10::lang::Complex::__over(
                                      ilm54118))->x10::lang::Complex::__times(
                    ((((q) * (rfac))) * ((__extension__ ({
                        
                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05367854123 =
                          l54137;
                        
                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double ret5368054124;
                        {
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5368054124 =
                              (x10aux::nullCheck(pplm)->
                                 FMGL(raw))->__apply((__extension__ ({
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5368554125 =
                                  x10aux::nullCheck(pplm)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05368254126 =
                                  i05367854123;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5368654127;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5368454128 =
                                      ((x10_int) ((i05368254126) - (this5368554125->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5368454128 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5368454128) * (this5368554125->
                                                                                                    FMGL(delta1))))) + (((x10_int)0))))) - (this5368554125->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5368654127 =
                                      offset5368454128;
                                }
                                ret5368654127;
                            }))
                            );
                        }
                        ret5368054124;
                    }))
                    )));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::lang::Complex ret5369254129;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (x10aux::nullCheck(this5369154120)->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this5369754130 =
                          x10aux::nullCheck(this5369154120)->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i05369454131 =
                          i05368854121;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret5369854132;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset5369654133 =
                              ((x10_int) ((i05369454131) - (this5369754130->
                                                              FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset5369654133 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5369654133) * (this5369754130->
                                                                                            FMGL(delta1))))) + (((x10_int)0))))) - (this5369754130->
                                                                                                                                      FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret5369854132 =
                              offset5369654133;
                        }
                        ret5369854132;
                    }))
                    , v5369054122);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5369254129 =
                      v5369054122;
                }
                ret5369254129;
            }))
            ;
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i37753max3775554111 =
              l54137;
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3775354112;
                for (
                     //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                     i3775354112 =
                       ((x10_int)1);
                     ((i3775354112) <= (i37753max3775554111));
                     
                     //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                     i3775354112 =
                       ((x10_int) ((i3775354112) + (((x10_int)1)))))
                {
                    
                    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m54113 =
                      i3775354112;
                    
                    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                    ilm54118 =
                      ((ilm54118) * (((x10_double) (((x10_int) ((l54137) + (m54113)))))));
                    
                    //#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                    phifac54119 =
                      x10aux::nullCheck(phifac54119)->x10::lang::Complex::__times(
                        phifac0);
                    
                    //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10::lang::Complex O_lm54072 =
                      x10aux::nullCheck(x10aux::nullCheck(phifac54119)->x10::lang::Complex::__over(
                                          ilm54118))->x10::lang::Complex::__times(
                        ((((q) * (rfac))) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05370054073 =
                              l54137;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15370154074 =
                              m54113;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret5370254075;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5370254075 =
                                  (x10aux::nullCheck(pplm)->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5370754076 =
                                      x10aux::nullCheck(pplm)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05370454077 =
                                      i05370054073;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15370554078 =
                                      i15370154074;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5370854079;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5370654080 =
                                          ((x10_int) ((i05370454077) - (this5370754076->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5370654080 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5370654080) * (this5370754076->
                                                                                                        FMGL(delta1))))) + (i15370554078)))) - (this5370754076->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5370854079 =
                                          offset5370654080;
                                    }
                                    ret5370854079;
                                }))
                                );
                            }
                            ret5370254075;
                        }))
                        )));
                    
                    //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5371354081 =
                          exp->
                            FMGL(terms);
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05371054082 =
                          l54137;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15371154083 =
                          m54113;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5371254084 =
                          O_lm54072;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5371454085;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(this5371354081)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5371954086 =
                                  x10aux::nullCheck(this5371354081)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05371654087 =
                                  i05371054082;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15371754088 =
                                  i15371154083;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5372054089;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5371854090 =
                                      ((x10_int) ((i05371654087) - (this5371954086->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5371854090 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5371854090) * (this5371954086->
                                                                                                    FMGL(delta1))))) + (i15371754088)))) - (this5371954086->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5372054089 =
                                      offset5371854090;
                                }
                                ret5372054089;
                            }))
                            , v5371254084);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5371454085 =
                              v5371254084;
                        }
                        ret5371454085;
                    }))
                    ;
                }
            }
            
            //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i37769min3777054114 =
              ((x10_int) -(l54137));
            
            //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3776954116;
                for (
                     //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                     i3776954116 =
                       i37769min3777054114;
                     ((i3776954116) <= (((x10_int)-1)));
                     
                     //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                     i3776954116 =
                       ((x10_int) ((i3776954116) + (((x10_int)1)))))
                {
                    
                    //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m54117 =
                      i3776954116;
                    
                    //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5373654091 =
                          exp->
                            FMGL(terms);
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05373354092 =
                          l54137;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15373454093 =
                          m54117;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5373554094 =
                          x10aux::nullCheck(x10aux::nullCheck((__extension__ ({
                                                
                                                //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                                x10aux::ref<x10::array::Array<x10::lang::Complex> > this5372454095 =
                                                  exp->
                                                    FMGL(terms);
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05372254096 =
                                                  l54137;
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15372354097 =
                                                  ((x10_int) -(m54117));
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::lang::Complex ret5372554098;
                                                {
                                                    
                                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                    ret5372554098 =
                                                      (x10aux::nullCheck(this5372454095)->
                                                         FMGL(raw))->__apply((__extension__ ({
                                                        
                                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10::array::RectLayout this5373054099 =
                                                          x10aux::nullCheck(this5372454095)->
                                                            FMGL(layout);
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i05372754100 =
                                                          i05372254096;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i15372854101 =
                                                          i15372354097;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int ret5373154102;
                                                        {
                                                            
                                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                            x10_int offset5372954103 =
                                                              ((x10_int) ((i05372754100) - (this5373054099->
                                                                                              FMGL(min0))));
                                                            
                                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            offset5372954103 =
                                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5372954103) * (this5373054099->
                                                                                                                            FMGL(delta1))))) + (i15372854101)))) - (this5373054099->
                                                                                                                                                                      FMGL(min1))));
                                                            
                                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            ret5373154102 =
                                                              offset5372954103;
                                                        }
                                                        ret5373154102;
                                                    }))
                                                    );
                                                }
                                                ret5372554098;
                                            }))
                                            )->x10::lang::Complex::conjugate())->x10::lang::Complex::__times(
                            ((x10_double) (((x10_int) ((((x10_int)1)) - (((x10_int) ((((x10_int)2)) * (((x10_int) ((((x10_int) -(m54117))) % x10aux::zeroCheck(((x10_int)2)))))))))))));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5373754104;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(this5373654091)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5374254105 =
                                  x10aux::nullCheck(this5373654091)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05373954106 =
                                  i05373354092;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15374054107 =
                                  i15373454093;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5374354108;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5374154109 =
                                      ((x10_int) ((i05373954106) - (this5374254105->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5374154109 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5374154109) * (this5374254105->
                                                                                                    FMGL(delta1))))) + (i15374054107)))) - (this5374254105->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5374354108 =
                                      offset5374154109;
                                }
                                ret5374354108;
                            }))
                            , v5373554094);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5373754104 =
                              v5373554094;
                        }
                        ret5373754104;
                    }))
                    ;
                }
            }
            
            //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
            rfac =
              ((rfac) * (v_pole->
                           FMGL(r)));
        }
    }
    
    //#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Return_c
    return exp;
    
}

//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
  au::edu::anu::mm::MultipoleExpansion::getOlm(
  x10aux::ref<x10x::vector::Tuple3d> v,
  x10_int p) {
    
    //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> exp =
      
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
    ;
    
    //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
    (exp)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
      p,
      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    
    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10x::polar::Polar3d v_pole = x10x::polar::Polar3d::getPolar3d(
                                    v);
    
    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10_double> > pplm =
      au::edu::anu::mm::AssociatedLegendrePolynomial::getPlk(
        v_pole->
          FMGL(theta),
        p);
    
    //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > this53761 =
          exp->
            FMGL(terms);
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10::lang::Complex v53760 = (__extension__ ({
            
            //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10::lang::Complex alloc37749 =
              
            x10::lang::Complex::_alloc();
            
            //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
                x10_double real53755 = (__extension__ ({
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret53747;
                    {
                        
                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret53747 = (x10aux::nullCheck(pplm)->
                                      FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this53752 =
                              x10aux::nullCheck(pplm)->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret53753;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset53751 =
                                  ((x10_int) ((((x10_int)0)) - (this53752->
                                                                  FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset53751 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset53751) * (this53752->
                                                                                           FMGL(delta1))))) + (((x10_int)0))))) - (this53752->
                                                                                                                                     FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret53753 =
                                  offset53751;
                            }
                            ret53753;
                        }))
                        );
                    }
                    ret53747;
                }))
                ;
                {
                    
                    //#line 53 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
                    alloc37749->FMGL(re) =
                      real53755;
                    
                    //#line 54 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
                    alloc37749->FMGL(im) =
                      0.0;
                }
                
            }))
            ;
            alloc37749;
        }))
        ;
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10::lang::Complex ret53762;
        {
            
            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
            (x10aux::nullCheck(this53761)->
               FMGL(raw))->__set((__extension__ ({
                
                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this53767 =
                  x10aux::nullCheck(this53761)->
                    FMGL(layout);
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                x10_int ret53768;
                {
                    
                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int offset53766 =
                      ((x10_int) ((((x10_int)0)) - (this53767->
                                                      FMGL(min0))));
                    
                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    offset53766 = ((x10_int) ((((x10_int) ((((x10_int) ((offset53766) * (this53767->
                                                                                           FMGL(delta1))))) + (((x10_int)0))))) - (this53767->
                                                                                                                                     FMGL(min1))));
                    
                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    ret53768 = offset53766;
                }
                ret53768;
            }))
            , v53760);
            
            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret53762 = v53760;
        }
        ret53762;
    }))
    ;
    
    //#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10::lang::Complex phifac0 =  x10::lang::Complex::_alloc();
    
    //#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
        x10_double real53770 = x10aux::math_utils::cos((-(v_pole->
                                                            FMGL(phi))));
        
        //#line 52 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10LocalDecl_c
        x10_double imaginary53771 = x10aux::math_utils::sin((-(v_pole->
                                                                 FMGL(phi))));
        {
            
            //#line 53 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
            phifac0->FMGL(re) = real53770;
            
            //#line 54 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10": x10.ast.X10FieldAssign_c
            phifac0->FMGL(im) = imaginary53771;
        }
        
    }))
    ;
    
    //#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double rfac = v_pole->FMGL(r);
    
    //#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double il = 1.0;
    
    //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_int i37833max3783554202 = p;
    
    //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
    {
        x10_int i3783354203;
        for (
             //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
             i3783354203 = ((x10_int)1); ((i3783354203) <= (i37833max3783554202));
             
             //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
             i3783354203 =
               ((x10_int) ((i3783354203) + (((x10_int)1)))))
        {
            
            //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int l54204 =
              i3783354203;
            
            //#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
            il =
              ((il) * (((x10_double) (l54204))));
            
            //#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_double ilm54184 =
              il;
            
            //#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10::lang::Complex phifac54185 =
              x10::lang::Complex::_make(1.0,0.0);
            
            //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10::lang::Complex> > this5378654186 =
                  exp->
                    FMGL(terms);
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05378354187 =
                  l54204;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::lang::Complex v5378554188 =
                  x10aux::nullCheck(x10aux::nullCheck(phifac54185)->x10::lang::Complex::__over(
                                      ilm54184))->x10::lang::Complex::__times(
                    ((rfac) * ((__extension__ ({
                        
                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05377354189 =
                          l54204;
                        
                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double ret5377554190;
                        {
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5377554190 =
                              (x10aux::nullCheck(pplm)->
                                 FMGL(raw))->__apply((__extension__ ({
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5378054191 =
                                  x10aux::nullCheck(pplm)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05377754192 =
                                  i05377354189;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5378154193;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5377954194 =
                                      ((x10_int) ((i05377754192) - (this5378054191->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5377954194 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5377954194) * (this5378054191->
                                                                                                    FMGL(delta1))))) + (((x10_int)0))))) - (this5378054191->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5378154193 =
                                      offset5377954194;
                                }
                                ret5378154193;
                            }))
                            );
                        }
                        ret5377554190;
                    }))
                    )));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::lang::Complex ret5378754195;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (x10aux::nullCheck(this5378654186)->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this5379254196 =
                          x10aux::nullCheck(this5378654186)->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i05378954197 =
                          i05378354187;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret5379354198;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset5379154199 =
                              ((x10_int) ((i05378954197) - (this5379254196->
                                                              FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset5379154199 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5379154199) * (this5379254196->
                                                                                            FMGL(delta1))))) + (((x10_int)0))))) - (this5379254196->
                                                                                                                                      FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret5379354198 =
                              offset5379154199;
                        }
                        ret5379354198;
                    }))
                    , v5378554188);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5378754195 =
                      v5378554188;
                }
                ret5378754195;
            }))
            ;
            
            //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_boolean m_sign54200 =
              false;
            
            //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i37801max3780354177 =
              l54204;
            
            //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3780154178;
                for (
                     //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                     i3780154178 =
                       ((x10_int)1);
                     ((i3780154178) <= (i37801max3780354177));
                     
                     //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                     i3780154178 =
                       ((x10_int) ((i3780154178) + (((x10_int)1)))))
                {
                    
                    //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m54179 =
                      i3780154178;
                    
                    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                    ilm54184 =
                      ((ilm54184) * (((x10_double) (((x10_int) ((l54204) + (m54179)))))));
                    
                    //#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                    phifac54185 =
                      x10aux::nullCheck(phifac54185)->x10::lang::Complex::__times(
                        phifac0);
                    
                    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10::lang::Complex O_lm54138 =
                      x10aux::nullCheck(x10aux::nullCheck(phifac54185)->x10::lang::Complex::__over(
                                          ilm54184))->x10::lang::Complex::__times(
                        ((rfac) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05379554139 =
                              l54204;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15379654140 =
                              m54179;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret5379754141;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5379754141 =
                                  (x10aux::nullCheck(pplm)->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5380254142 =
                                      x10aux::nullCheck(pplm)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05379954143 =
                                      i05379554139;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15380054144 =
                                      i15379654140;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5380354145;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5380154146 =
                                          ((x10_int) ((i05379954143) - (this5380254142->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5380154146 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5380154146) * (this5380254142->
                                                                                                        FMGL(delta1))))) + (i15380054144)))) - (this5380254142->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5380354145 =
                                          offset5380154146;
                                    }
                                    ret5380354145;
                                }))
                                );
                            }
                            ret5379754141;
                        }))
                        )));
                    
                    //#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5380854147 =
                          exp->
                            FMGL(terms);
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05380554148 =
                          l54204;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15380654149 =
                          m54179;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5380754150 =
                          O_lm54138;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5380954151;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(this5380854147)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5381454152 =
                                  x10aux::nullCheck(this5380854147)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05381154153 =
                                  i05380554148;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15381254154 =
                                  i15380654149;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5381554155;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5381354156 =
                                      ((x10_int) ((i05381154153) - (this5381454152->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5381354156 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5381354156) * (this5381454152->
                                                                                                    FMGL(delta1))))) + (i15381254154)))) - (this5381454152->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5381554155 =
                                      offset5381354156;
                                }
                                ret5381554155;
                            }))
                            , v5380754150);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5380954151 =
                              v5380754150;
                        }
                        ret5380954151;
                    }))
                    ;
                    
                    //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                    m_sign54200 =
                      !(m_sign54200);
                }
            }
            
            //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i37817min3781854180 =
              ((x10_int) -(l54204));
            
            //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3781754182;
                for (
                     //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                     i3781754182 =
                       i37817min3781854180;
                     ((i3781754182) <= (((x10_int)-1)));
                     
                     //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                     i3781754182 =
                       ((x10_int) ((i3781754182) + (((x10_int)1)))))
                {
                    
                    //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m54183 =
                      i3781754182;
                    
                    //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5383154157 =
                          exp->
                            FMGL(terms);
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05382854158 =
                          l54204;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15382954159 =
                          m54183;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5383054160 =
                          x10aux::nullCheck(x10aux::nullCheck((__extension__ ({
                                                
                                                //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                                x10aux::ref<x10::array::Array<x10::lang::Complex> > this5381954161 =
                                                  exp->
                                                    FMGL(terms);
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05381754162 =
                                                  l54204;
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15381854163 =
                                                  ((x10_int) -(m54183));
                                                
                                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::lang::Complex ret5382054164;
                                                {
                                                    
                                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                    ret5382054164 =
                                                      (x10aux::nullCheck(this5381954161)->
                                                         FMGL(raw))->__apply((__extension__ ({
                                                        
                                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10::array::RectLayout this5382554165 =
                                                          x10aux::nullCheck(this5381954161)->
                                                            FMGL(layout);
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i05382254166 =
                                                          i05381754162;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i15382354167 =
                                                          i15381854163;
                                                        
                                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int ret5382654168;
                                                        {
                                                            
                                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                            x10_int offset5382454169 =
                                                              ((x10_int) ((i05382254166) - (this5382554165->
                                                                                              FMGL(min0))));
                                                            
                                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            offset5382454169 =
                                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5382454169) * (this5382554165->
                                                                                                                            FMGL(delta1))))) + (i15382354167)))) - (this5382554165->
                                                                                                                                                                      FMGL(min1))));
                                                            
                                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                            ret5382654168 =
                                                              offset5382454169;
                                                        }
                                                        ret5382654168;
                                                    }))
                                                    );
                                                }
                                                ret5382054164;
                                            }))
                                            )->x10::lang::Complex::conjugate())->x10::lang::Complex::__times(
                            ((x10_double) (((x10_int) ((((x10_int)1)) - (((x10_int) ((((x10_int)2)) * (((x10_int) ((((x10_int) -(m54183))) % x10aux::zeroCheck(((x10_int)2)))))))))))));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5383254170;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(this5383154157)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5383754171 =
                                  x10aux::nullCheck(this5383154157)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05383454172 =
                                  i05382854158;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15383554173 =
                                  i15382954159;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5383854174;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5383654175 =
                                      ((x10_int) ((i05383454172) - (this5383754171->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5383654175 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5383654175) * (this5383754171->
                                                                                                    FMGL(delta1))))) + (i15383554173)))) - (this5383754171->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5383854174 =
                                      offset5383654175;
                                }
                                ret5383854174;
                            }))
                            , v5383054160);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5383254170 =
                              v5383054160;
                        }
                        ret5383254170;
                    }))
                    ;
                }
            }
            
            //#line 100 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
            rfac =
              ((rfac) * (v_pole->
                           FMGL(r)));
        }
    }
    
    //#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Return_c
    return exp;
    
}

//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::MultipoleExpansion::translateAndAddMultipole(
  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> shift,
  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source) {
    {
        
        //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::Throwable> throwable54324 =
          X10_NULL;
        
        //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.Try_c
        try {
            {
                
                //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Call_c
                x10::lang::Runtime::enterAtomic();
                {
                    
                    //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i37897max3789954257 =
                      ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                        FMGL(p);
                    
                    //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
                    {
                        x10_int i3789754258;
                        for (
                             //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                             i3789754258 =
                               ((x10_int)0);
                             ((i3789754258) <= (i37897max3789954257));
                             
                             //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                             i3789754258 =
                               ((x10_int) ((i3789754258) + (((x10_int)1)))))
                        {
                            
                            //#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int j54259 =
                              i3789754258;
                            
                            //#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int i37881min3788254252 =
                              ((x10_int) -(j54259));
                            
                            //#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int i37881max3788354253 =
                              j54259;
                            
                            //#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
                            {
                                x10_int i3788154254;
                                for (
                                     //#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                     i3788154254 =
                                       i37881min3788254252;
                                     ((i3788154254) <= (i37881max3788354253));
                                     
                                     //#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                                     i3788154254 =
                                       ((x10_int) ((i3788154254) + (((x10_int)1)))))
                                {
                                    
                                    //#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10_int k54255 =
                                      i3788154254;
                                    
                                    //#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Complex O_jk54242 =
                                      (__extension__ ({
                                        
                                        //#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5384254243 =
                                          x10aux::nullCheck(source)->
                                            FMGL(terms);
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05384054244 =
                                          j54259;
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15384154245 =
                                          k54255;
                                        
                                        //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex ret5384354246;
                                        {
                                            
                                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5384354246 =
                                              (x10aux::nullCheck(this5384254243)->
                                                 FMGL(raw))->__apply((__extension__ ({
                                                
                                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::array::RectLayout this5384854247 =
                                                  x10aux::nullCheck(this5384254243)->
                                                    FMGL(layout);
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05384554248 =
                                                  i05384054244;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i15384654249 =
                                                  i15384154245;
                                                
                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int ret5384954250;
                                                {
                                                    
                                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int offset5384754251 =
                                                      ((x10_int) ((i05384554248) - (this5384854247->
                                                                                      FMGL(min0))));
                                                    
                                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    offset5384754251 =
                                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5384754251) * (this5384854247->
                                                                                                                    FMGL(delta1))))) + (i15384654249)))) - (this5384854247->
                                                                                                                                                              FMGL(min1))));
                                                    
                                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    ret5384954250 =
                                                      offset5384754251;
                                                }
                                                ret5384954250;
                                            }))
                                            );
                                        }
                                        ret5384354246;
                                    }))
                                    ;
                                    
                                    //#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10_int i37865min3786654238 =
                                      j54259;
                                    
                                    //#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                    x10_int i37865max3786754239 =
                                      ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                        FMGL(p);
                                    
                                    //#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
                                    {
                                        x10_int i3786554240;
                                        for (
                                             //#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                             i3786554240 =
                                               i37865min3786654238;
                                             ((i3786554240) <= (i37865max3786754239));
                                             
                                             //#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                                             i3786554240 =
                                               ((x10_int) ((i3786554240) + (((x10_int)1)))))
                                        {
                                            
                                            //#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                            x10_int l54241 =
                                              i3786554240;
                                            
                                            //#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                            x10_int i37849min3785054234 =
                                              ((x10_int) -(l54241));
                                            
                                            //#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                            x10_int i37849max3785154235 =
                                              l54241;
                                            
                                            //#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
                                            {
                                                x10_int i3784954236;
                                                for (
                                                     //#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                                     i3784954236 =
                                                       i37849min3785054234;
                                                     ((i3784954236) <= (i37849max3785154235));
                                                     
                                                     //#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                                                     i3784954236 =
                                                       ((x10_int) ((i3784954236) + (((x10_int)1)))))
                                                {
                                                    
                                                    //#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                                    x10_int m54237 =
                                                      i3784954236;
                                                    
                                                    //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10If_c
                                                    if (((::abs(((x10_int) ((m54237) - (k54255))))) <= (((x10_int) ((l54241) - (j54259))))))
                                                    {
                                                        
                                                        //#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                                        x10::lang::Complex A_lmjk54205 =
                                                          (__extension__ ({
                                                            
                                                            //#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                                            x10aux::ref<x10::array::Array<x10::lang::Complex> > this5385354206 =
                                                              x10aux::nullCheck(shift)->
                                                                FMGL(terms);
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i05385154207 =
                                                              ((x10_int) ((l54241) - (j54259)));
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i15385254208 =
                                                              ((x10_int) ((m54237) - (k54255)));
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10::lang::Complex ret5385454209;
                                                            {
                                                                
                                                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                ret5385454209 =
                                                                  (x10aux::nullCheck(this5385354206)->
                                                                     FMGL(raw))->__apply((__extension__ ({
                                                                    
                                                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                    x10::array::RectLayout this5385954210 =
                                                                      x10aux::nullCheck(this5385354206)->
                                                                        FMGL(layout);
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int i05385654211 =
                                                                      i05385154207;
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int i15385754212 =
                                                                      i15385254208;
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int ret5386054213;
                                                                    {
                                                                        
                                                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                        x10_int offset5385854214 =
                                                                          ((x10_int) ((i05385654211) - (this5385954210->
                                                                                                          FMGL(min0))));
                                                                        
                                                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                        offset5385854214 =
                                                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5385854214) * (this5385954210->
                                                                                                                                        FMGL(delta1))))) + (i15385754212)))) - (this5385954210->
                                                                                                                                                                                  FMGL(min1))));
                                                                        
                                                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                        ret5386054213 =
                                                                          offset5385854214;
                                                                    }
                                                                    ret5386054213;
                                                                }))
                                                                );
                                                            }
                                                            ret5385454209;
                                                        }))
                                                        ;
                                                        
                                                        //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                                                        (__extension__ ({
                                                            
                                                            //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                                            x10aux::ref<x10::array::Array<x10::lang::Complex> > this5387654215 =
                                                              ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                                                FMGL(terms);
                                                            
                                                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i05387354216 =
                                                              l54241;
                                                            
                                                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i15387454217 =
                                                              m54237;
                                                            
                                                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10::lang::Complex v5387554218 =
                                                              x10aux::nullCheck((__extension__ ({
                                                                  
                                                                  //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                                                  x10aux::ref<x10::array::Array<x10::lang::Complex> > this5386454219 =
                                                                    ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                                                      FMGL(terms);
                                                                  
                                                                  //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                  x10_int i05386254220 =
                                                                    l54241;
                                                                  
                                                                  //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                  x10_int i15386354221 =
                                                                    m54237;
                                                                  
                                                                  //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                  x10::lang::Complex ret5386554222;
                                                                  {
                                                                      
                                                                      //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                      ret5386554222 =
                                                                        (x10aux::nullCheck(this5386454219)->
                                                                           FMGL(raw))->__apply((__extension__ ({
                                                                          
                                                                          //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                          x10::array::RectLayout this5387054223 =
                                                                            x10aux::nullCheck(this5386454219)->
                                                                              FMGL(layout);
                                                                          
                                                                          //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                          x10_int i05386754224 =
                                                                            i05386254220;
                                                                          
                                                                          //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                          x10_int i15386854225 =
                                                                            i15386354221;
                                                                          
                                                                          //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                          x10_int ret5387154226;
                                                                          {
                                                                              
                                                                              //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                              x10_int offset5386954227 =
                                                                                ((x10_int) ((i05386754224) - (this5387054223->
                                                                                                                FMGL(min0))));
                                                                              
                                                                              //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                              offset5386954227 =
                                                                                ((x10_int) ((((x10_int) ((((x10_int) ((offset5386954227) * (this5387054223->
                                                                                                                                              FMGL(delta1))))) + (i15386854225)))) - (this5387054223->
                                                                                                                                                                                        FMGL(min1))));
                                                                              
                                                                              //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                              ret5387154226 =
                                                                                offset5386954227;
                                                                          }
                                                                          ret5387154226;
                                                                      }))
                                                                      );
                                                                  }
                                                                  ret5386554222;
                                                              }))
                                                              )->x10::lang::Complex::__plus(
                                                                x10aux::nullCheck(A_lmjk54205)->x10::lang::Complex::__times(
                                                                  O_jk54242));
                                                            
                                                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10::lang::Complex ret5387754228;
                                                            {
                                                                
                                                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                                                (x10aux::nullCheck(this5387654215)->
                                                                   FMGL(raw))->__set((__extension__ ({
                                                                    
                                                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                    x10::array::RectLayout this5388254229 =
                                                                      x10aux::nullCheck(this5387654215)->
                                                                        FMGL(layout);
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int i05387954230 =
                                                                      i05387354216;
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int i15388054231 =
                                                                      i15387454217;
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int ret5388354232;
                                                                    {
                                                                        
                                                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                        x10_int offset5388154233 =
                                                                          ((x10_int) ((i05387954230) - (this5388254229->
                                                                                                          FMGL(min0))));
                                                                        
                                                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                        offset5388154233 =
                                                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5388154233) * (this5388254229->
                                                                                                                                        FMGL(delta1))))) + (i15388054231)))) - (this5388254229->
                                                                                                                                                                                  FMGL(min1))));
                                                                        
                                                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                        ret5388354232 =
                                                                          offset5388154233;
                                                                    }
                                                                    ret5388354232;
                                                                }))
                                                                , v5387554218);
                                                                
                                                                //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                ret5387754228 =
                                                                  v5387554218;
                                                            }
                                                            ret5387754228;
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
            }
            
            //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Call_c
            x10::compiler::Finalization::plausibleThrow();
        }
        catch (x10aux::__ref& __ref__416) {
            x10aux::ref<x10::lang::Throwable>& __exc__ref__416 = (x10aux::ref<x10::lang::Throwable>&)__ref__416;
            if (true) {
                x10aux::ref<x10::lang::Throwable> formal54325 =
                  static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__416);
                {
                    
                    //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                    throwable54324 =
                      formal54325;
                }
            } else
            throw;
        }
        
        //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10If_c
        if ((!x10aux::struct_equals(X10_NULL,
                                    throwable54324)))
        {
            
            //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10If_c
            if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable54324))
            {
                
                //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.Throw_c
                x10aux::throwException(x10aux::nullCheck(throwable54324));
            }
            
        }
        
        //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10If_c
        if (true) {
            
            //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Call_c
            x10::lang::Runtime::exitAtomic();
        }
        
        //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10If_c
        if ((!x10aux::struct_equals(X10_NULL,
                                    throwable54324)))
        {
            
            //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10If_c
            if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable54324)))
            {
                
                //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.Throw_c
                x10aux::throwException(x10aux::nullCheck(throwable54324));
            }
            
        }
        
    }
}

//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::MultipoleExpansion::translateAndAddMultipole(
  x10x::vector::Vector3d v,
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > complexK,
  x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source,
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > wigner) {
    
    //#line 147 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
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
    
    //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_double invB = ((((x10_double) (((x10_int)1)))) / (b));
    
    //#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10::lang::Complex> > temp =
      (__extension__ ({
        
        //#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc37750 =
          
        x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
        ;
        
        //#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> reg53887 =
              (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r53885 =
                  x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                                            FMGL(p))), ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                                                         FMGL(p));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063153886 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063153886)->::x10::array::RectRegion1D::_constructor(
                      r53885->
                        FMGL(min),
                      r53885->
                        FMGL(max));
                    alloc2063153886;
                }))
                );
            }))
            ;
            {
                
                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc37750)->::x10::lang::Object::_constructor();
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc37750)->
                  FMGL(region) =
                  (reg53887);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc37750)->
                  FMGL(rank) =
                  x10aux::nullCheck(reg53887)->
                    FMGL(rank);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc37750)->
                  FMGL(rect) =
                  x10aux::nullCheck(reg53887)->
                    FMGL(rect);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc37750)->
                  FMGL(zeroBased) =
                  x10aux::nullCheck(reg53887)->
                    FMGL(zeroBased);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc37750)->
                  FMGL(rail) =
                  x10aux::nullCheck(reg53887)->
                    FMGL(rail);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc37750)->
                  FMGL(size) =
                  x10aux::nullCheck(reg53887)->size();
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc37750)->
                  FMGL(layout) =
                  (__extension__ ({
                    
                    //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1995453888 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1995453888)->::x10::array::RectLayout::_constructor(
                      reg53887);
                    alloc1995453888;
                }))
                ;
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n53889 =
                  (__extension__ ({
                    
                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this53891 =
                      x10aux::nullCheck(alloc37750)->
                        FMGL(layout);
                    this53891->
                      FMGL(size);
                }))
                ;
                
                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc37750)->
                  FMGL(raw) =
                  x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n53889, 8, false, true);
            }
            
        }))
        ;
        alloc37750;
    }))
    ;
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> scratch =
      
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
    ;
    
    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
    (scratch)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
      source,
      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    
    //#line 152 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Call_c
    scratch->rotate(temp, (__extension__ ({
                        
                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > ret53893;
                        
                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                        goto __ret53894; __ret53894: {
                        {
                            
                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret53893 = (x10aux::nullCheck(complexK)->
                                          FMGL(raw))->__apply(((x10_int)0));
                            
                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                            goto __ret53894_end_;
                        }goto __ret53894_end_; __ret53894_end_: ;
                        }
                        ret53893;
                        }))
                        , (__extension__ ({
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret53901;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                            goto __ret53902; __ret53902: {
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret53901 =
                                  (x10aux::nullCheck(wigner)->
                                     FMGL(raw))->__apply(((x10_int)0));
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret53902_end_;
                            }goto __ret53902_end_; __ret53902_end_: ;
                            }
                            ret53901;
                            }))
                            );
    
    //#line 154 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10::lang::Complex> > targetTerms =
      scratch->
        FMGL(terms);
    
    //#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_int m_sign = ((x10_int)1);
    
    //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
    x10_int i37961max3796354321 = ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                    FMGL(p);
    
    //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
    {
        x10_int i3796154322;
        for (
             //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
             i3796154322 = ((x10_int)0); ((i3796154322) <= (i37961max3796354321));
             
             //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
             i3796154322 =
               ((x10_int) ((i3796154322) + (((x10_int)1)))))
        {
            
            //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int m54323 =
              i3796154322;
            
            //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i37913min3791454311 =
              m54323;
            
            //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i37913max3791554312 =
              ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                FMGL(p);
            
            //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3791354313;
                for (
                     //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                     i3791354313 =
                       i37913min3791454311;
                     ((i3791354313) <= (i37913max3791554312));
                     
                     //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                     i3791354313 =
                       ((x10_int) ((i3791354313) + (((x10_int)1)))))
                {
                    
                    //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int l54314 =
                      i3791354313;
                    
                    //#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05391854264 =
                          l54314;
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5391954265 =
                          (__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05390854266 =
                              l54314;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15390954267 =
                              m54323;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5391054268;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5391054268 =
                                  (x10aux::nullCheck(targetTerms)->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5391554269 =
                                      x10aux::nullCheck(targetTerms)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05391254270 =
                                      i05390854266;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15391354271 =
                                      i15390954267;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5391654272;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5391454273 =
                                          ((x10_int) ((i05391254270) - (this5391554269->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5391454273 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5391454273) * (this5391554269->
                                                                                                        FMGL(delta1))))) + (i15391354271)))) - (this5391554269->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5391654272 =
                                          offset5391454273;
                                    }
                                    ret5391654272;
                                }))
                                );
                            }
                            ret5391054268;
                        }))
                        ;
                        
                        //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5392054274;
                        {
                            
                            //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(temp)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5392454260 =
                                  x10aux::nullCheck(temp)->
                                    FMGL(layout);
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05392254261 =
                                  i05391854264;
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5392554262;
                                {
                                    
                                    //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5392354263 =
                                      ((x10_int) ((i05392254261) - (this5392454260->
                                                                      FMGL(min0))));
                                    
                                    //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5392554262 =
                                      offset5392354263;
                                }
                                ret5392554262;
                            }))
                            , v5391954265);
                            
                            //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5392054274 =
                              v5391954265;
                        }
                        ret5392054274;
                    }))
                    ;
                }
            }
            
            //#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_double b_lm_pow54319 =
              1.0;
            
            //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i37945min3794654315 =
              m54323;
            
            //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10_int i37945max3794754316 =
              ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                FMGL(p);
            
            //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
            {
                x10_int i3794554317;
                for (
                     //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                     i3794554317 =
                       i37945min3794654315;
                     ((i3794554317) <= (i37945max3794754316));
                     
                     //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                     i3794554317 =
                       ((x10_int) ((i3794554317) + (((x10_int)1)))))
                {
                    
                    //#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int l54318 =
                      i3794554317;
                    
                    //#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10::lang::Complex O_lm54286 =
                      x10::lang::Complex::_make(0.0,0.0);
                    
                    //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_double F_lm54287 =
                      ((b_lm_pow54319) / ((__extension__ ({
                        
                        //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                        x10_int i5396054288 =
                          ((x10_int) ((l54318) - (m54323)));
                        (__extension__ ({
                            
                            //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10_double> > this5396254289 =
                              au::edu::anu::mm::Factorial::
                                FMGL(factorial__get)();
                            
                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05396154290 =
                              i5396054288;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret5396354291;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                            goto __ret5396454292; __ret5396454292: {
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5396354291 =
                                  (x10aux::nullCheck(this5396254289)->
                                     FMGL(raw))->__apply(i05396154290);
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret5396454292_end_;
                            }goto __ret5396454292_end_; __ret5396454292_end_: ;
                            }
                            ret5396354291;
                            }))
                            ;
                        }))
                        ));
                        
                    
                    //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i37929min3793054282 =
                      m54323;
                    
                    //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i37929max3793154283 =
                      l54318;
                    
                    //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
                    {
                        x10_int i3792954284;
                        for (
                             //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                             i3792954284 =
                               i37929min3793054282;
                             ((i3792954284) <= (i37929max3793154283));
                             
                             //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                             i3792954284 =
                               ((x10_int) ((i3792954284) + (((x10_int)1)))))
                        {
                            
                            //#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                            x10_int j54285 =
                              i3792954284;
                            
                            //#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                            O_lm54286 =
                              x10aux::nullCheck(O_lm54286)->x10::lang::Complex::__plus(
                                x10aux::nullCheck((__extension__ ({
                                    
                                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05397054279 =
                                      j54285;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Complex ret5397154280;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                    goto __ret5397254281; __ret5397254281: {
                                    {
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret5397154280 =
                                          (x10aux::nullCheck(temp)->
                                             FMGL(raw))->__apply((__extension__ ({
                                            
                                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10::array::RectLayout this5397554275 =
                                              x10aux::nullCheck(temp)->
                                                FMGL(layout);
                                            
                                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05397354276 =
                                              i05397054279;
                                            
                                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int ret5397654277;
                                            {
                                                
                                                //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int offset5397454278 =
                                                  ((x10_int) ((i05397354276) - (this5397554275->
                                                                                  FMGL(min0))));
                                                
                                                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                ret5397654277 =
                                                  offset5397454278;
                                            }
                                            ret5397654277;
                                        }))
                                        );
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                        goto __ret5397254281_end_;
                                    }goto __ret5397254281_end_; __ret5397254281_end_: ;
                                    }
                                    ret5397154280;
                                    }))
                                    )->x10::lang::Complex::__times(
                                      F_lm54287));
                            
                            //#line 165 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                            F_lm54287 =
                              ((((F_lm54287) * (((x10_double) (((x10_int) ((l54318) - (j54285)))))))) * (invB));
                            }
                        }
                        
                    
                    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05397854293 =
                          l54318;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15397954294 =
                          m54323;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5398054295 =
                          O_lm54286;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5398154296;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(targetTerms)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5398654297 =
                                  x10aux::nullCheck(targetTerms)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05398354298 =
                                  i05397854293;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15398454299 =
                                  i15397954294;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5398754300;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5398554301 =
                                      ((x10_int) ((i05398354298) - (this5398654297->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5398554301 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5398554301) * (this5398654297->
                                                                                                    FMGL(delta1))))) + (i15398454299)))) - (this5398654297->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5398754300 =
                                      offset5398554301;
                                }
                                ret5398754300;
                            }))
                            , v5398054295);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5398154296 =
                              v5398054295;
                        }
                        ret5398154296;
                    }))
                    ;
                    
                    //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10If_c
                    if ((!x10aux::struct_equals(m54323,
                                                ((x10_int)0))))
                    {
                        
                        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05398954302 =
                              l54318;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15399054303 =
                              ((x10_int) -(m54323));
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex v5399154304 =
                              x10aux::nullCheck(x10aux::nullCheck(O_lm54286)->x10::lang::Complex::conjugate())->x10::lang::Complex::__times(
                                ((x10_double) (m_sign)));
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5399254305;
                            {
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(targetTerms)->
                                   FMGL(raw))->__set((__extension__ ({
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5399754306 =
                                      x10aux::nullCheck(targetTerms)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05399454307 =
                                      i05398954302;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15399554308 =
                                      i15399054303;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5399854309;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5399654310 =
                                          ((x10_int) ((i05399454307) - (this5399754306->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5399654310 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5399654310) * (this5399754306->
                                                                                                        FMGL(delta1))))) + (i15399554308)))) - (this5399754306->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5399854309 =
                                          offset5399654310;
                                    }
                                    ret5399854309;
                                }))
                                , v5399154304);
                                
                                //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5399254305 =
                                  v5399154304;
                            }
                            ret5399254305;
                        }))
                        ;
                    }
                    
                    //#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                    b_lm_pow54319 =
                      ((b_lm_pow54319) * (b));
                    }
                    }
                    
                
                //#line 172 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalAssign_c
                m_sign =
                  ((x10_int) -(m_sign));
            }
            }
            
        
        //#line 175 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Call_c
        scratch->backRotate(
          temp,
          (__extension__ ({
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
              x10aux::ref<x10::array::Array<x10::lang::Complex> > ret54001;
              
              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
              goto __ret54002; __ret54002: {
              {
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                  ret54001 =
                    (x10aux::nullCheck(complexK)->
                       FMGL(raw))->__apply(((x10_int)1));
                  
                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                  goto __ret54002_end_;
              }goto __ret54002_end_; __ret54002_end_: ;
              }
              ret54001;
              }))
              ,
              (__extension__ ({
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret54009;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret54010; __ret54010: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret54009 =
                        (x10aux::nullCheck(wigner)->
                           FMGL(raw))->__apply(((x10_int)1));
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret54010_end_;
                  }goto __ret54010_end_; __ret54010_end_: ;
                  }
                  ret54009;
                  }))
                  );
        
        //#line 176 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Call_c
        ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->unsafeAdd(
          x10aux::class_cast_unchecked<x10aux::ref<au::edu::anu::mm::Expansion> >(scratch));
        }
        
        //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
        void
          au::edu::anu::mm::MultipoleExpansion::translateAndAddMultipole(
          x10x::vector::Vector3d v,
          x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source) {
            
            //#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10x::polar::Polar3d polar =
              x10x::polar::Polar3d::getPolar3d(
                x10aux::class_cast_unchecked<x10aux::ref<x10x::vector::Tuple3d> >(v));
            
            //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Call_c
            ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->translateAndAddMultipole(
              v,
              au::edu::anu::mm::Expansion::genComplexK(
                polar->
                  FMGL(phi),
                ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                  FMGL(p)),
              source,
              (__extension__ ({
                  
                  //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                  x10_double theta54016 =
                    polar->
                      FMGL(theta);
                  
                  //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                  x10_int numTerms54017 =
                    ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                      FMGL(p);
                  au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
                    theta54016,
                    numTerms54017,
                    ((x10_int)0));
              }))
              );
        }
        
        //#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
        x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
          au::edu::anu::mm::MultipoleExpansion::rotate(
          x10_double theta,
          x10_double phi) {
            
            //#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion> target =
              
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
            ;
            
            //#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
            (target)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
              ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this),
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            
            //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<x10::lang::Complex> > temp =
              (__extension__ ({
                
                //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc37751 =
                  
                x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
                ;
                
                //#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Region> reg54020 =
                      (__extension__ ({
                        
                        //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                        x10::lang::IntRange r54018 =
                          x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                                                    FMGL(p))), ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                                                                 FMGL(p));
                        x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                            
                            //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::RectRegion1D> alloc2063154019 =
                              
                            x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                            ;
                            
                            //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                            (alloc2063154019)->::x10::array::RectRegion1D::_constructor(
                              r54018->
                                FMGL(min),
                              r54018->
                                FMGL(max));
                            alloc2063154019;
                        }))
                        );
                    }))
                    ;
                    {
                        
                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                        (alloc37751)->::x10::lang::Object::_constructor();
                        
                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc37751)->
                          FMGL(region) =
                          (reg54020);
                        
                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc37751)->
                          FMGL(rank) =
                          x10aux::nullCheck(reg54020)->
                            FMGL(rank);
                        
                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc37751)->
                          FMGL(rect) =
                          x10aux::nullCheck(reg54020)->
                            FMGL(rect);
                        
                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc37751)->
                          FMGL(zeroBased) =
                          x10aux::nullCheck(reg54020)->
                            FMGL(zeroBased);
                        
                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc37751)->
                          FMGL(rail) =
                          x10aux::nullCheck(reg54020)->
                            FMGL(rail);
                        
                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc37751)->
                          FMGL(size) =
                          x10aux::nullCheck(reg54020)->size();
                        
                        //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc37751)->
                          FMGL(layout) =
                          (__extension__ ({
                            
                            //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout alloc1995454021 =
                              
                            x10::array::RectLayout::_alloc();
                            
                            //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                            (alloc1995454021)->::x10::array::RectLayout::_constructor(
                              reg54020);
                            alloc1995454021;
                        }))
                        ;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int n54022 =
                          (__extension__ ({
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this54024 =
                              x10aux::nullCheck(alloc37751)->
                                FMGL(layout);
                            this54024->
                              FMGL(size);
                        }))
                        ;
                        
                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(alloc37751)->
                          FMGL(raw) =
                          x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n54022, 8, false, true);
                    }
                    
                }))
                ;
                alloc37751;
            }))
            ;
            
            //#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Call_c
            target->rotate(
              temp,
              (__extension__ ({
                  
                  //#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > this54026 =
                    au::edu::anu::mm::Expansion::genComplexK(
                      phi,
                      ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                        FMGL(p));
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                  x10aux::ref<x10::array::Array<x10::lang::Complex> > ret54027;
                  
                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                  goto __ret54028; __ret54028: {
                  {
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                      ret54027 =
                        (x10aux::nullCheck(this54026)->
                           FMGL(raw))->__apply(((x10_int)1));
                      
                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                      goto __ret54028_end_;
                  }goto __ret54028_end_; __ret54028_end_: ;
                  }
                  ret54027;
                  }))
                  ,
                  (__extension__ ({
                      
                      //#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > this54037 =
                        (__extension__ ({
                          
                          //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                          x10_double theta54034 =
                            theta;
                          
                          //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                          x10_int numTerms54035 =
                            ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                              FMGL(p);
                          au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
                            theta54034,
                            numTerms54035,
                            ((x10_int)0));
                      }))
                      ;
                      
                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret54038;
                      
                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                      goto __ret54039; __ret54039: {
                      {
                          
                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                          ret54038 =
                            (x10aux::nullCheck(this54037)->
                               FMGL(raw))->__apply(((x10_int)0));
                          
                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                          goto __ret54039_end_;
                      }goto __ret54039_end_; __ret54039_end_: ;
                      }
                      ret54038;
                      }))
                      );
            
            //#line 200 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Return_c
            return target;
            }
            
            //#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
              au::edu::anu::mm::MultipoleExpansion::getMacroscopicParent(
              ) {
                
                //#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::MultipoleExpansion> parentExpansion =
                  
                x10aux::ref<au::edu::anu::mm::MultipoleExpansion>((new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion()))
                ;
                
                //#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10ConstructorCall_c
                (parentExpansion)->::au::edu::anu::mm::MultipoleExpansion::_constructor(
                  ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                    FMGL(p),
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                
                //#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": polyglot.ast.For_c
                {
                    x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > id37977;
                    for (
                         //#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                         id37977 =
                           x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                               FMGL(terms))->
                             FMGL(region)->iterator();
                         x10::lang::Iterator<x10aux::ref<x10::array::Point> >::hasNext(x10aux::nullCheck(id37977));
                         )
                    {
                        
                        //#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Point> id2282 =
                          x10::lang::Iterator<x10aux::ref<x10::array::Point> >::next(x10aux::nullCheck(id37977));
                        
                        //#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_int l =
                          x10aux::nullCheck(id2282)->x10::array::Point::__apply(
                            ((x10_int)0));
                        
                        //#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                        x10_int m =
                          x10aux::nullCheck(id2282)->x10::array::Point::__apply(
                            ((x10_int)1));
                        
                        //#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10::lang::Complex> > this54059 =
                              parentExpansion->
                                FMGL(terms);
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i054056 =
                              l;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i154057 =
                              m;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex v54058 =
                              x10aux::nullCheck((__extension__ ({
                                  
                                  //#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10LocalDecl_c
                                  x10aux::ref<x10::array::Array<x10::lang::Complex> > this54047 =
                                    ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                                      FMGL(terms);
                                  
                                  //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                  x10_int i054045 =
                                    l;
                                  
                                  //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                  x10_int i154046 =
                                    m;
                                  
                                  //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                  x10::lang::Complex ret54048;
                                  {
                                      
                                      //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                      ret54048 =
                                        (x10aux::nullCheck(this54047)->
                                           FMGL(raw))->__apply((__extension__ ({
                                          
                                          //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                          x10::array::RectLayout this54053 =
                                            x10aux::nullCheck(this54047)->
                                              FMGL(layout);
                                          
                                          //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                          x10_int i054050 =
                                            i054045;
                                          
                                          //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                          x10_int i154051 =
                                            i154046;
                                          
                                          //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                          x10_int ret54054;
                                          {
                                              
                                              //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                              x10_int offset54052 =
                                                ((x10_int) ((i054050) - (this54053->
                                                                           FMGL(min0))));
                                              
                                              //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                              offset54052 =
                                                ((x10_int) ((((x10_int) ((((x10_int) ((offset54052) * (this54053->
                                                                                                         FMGL(delta1))))) + (i154051)))) - (this54053->
                                                                                                                                              FMGL(min1))));
                                              
                                              //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                              ret54054 =
                                                offset54052;
                                          }
                                          ret54054;
                                      }))
                                      );
                                  }
                                  ret54048;
                              }))
                              )->x10::lang::Complex::__times(
                                x10aux::math_utils::pow(3.0,((x10_double) (l))));
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret54060;
                            {
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(this54059)->
                                   FMGL(raw))->__set((__extension__ ({
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this54065 =
                                      x10aux::nullCheck(this54059)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i054062 =
                                      i054056;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i154063 =
                                      i154057;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret54066;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset54064 =
                                          ((x10_int) ((i054062) - (this54065->
                                                                     FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset54064 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset54064) * (this54065->
                                                                                                   FMGL(delta1))))) + (i154063)))) - (this54065->
                                                                                                                                        FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret54066 =
                                          offset54064;
                                    }
                                    ret54066;
                                }))
                                , v54058);
                                
                                //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret54060 =
                                  v54058;
                            }
                            ret54060;
                        }))
                        ;
                    }
                }
                
                //#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Return_c
                return parentExpansion;
                
            }
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
            x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
              au::edu::anu::mm::MultipoleExpansion::au__edu__anu__mm__MultipoleExpansion____au__edu__anu__mm__MultipoleExpansion__this(
              ) {
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10Return_c
                return ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this);
                
            }
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10MethodDecl_c
            void
              au::edu::anu::mm::MultipoleExpansion::__fieldInitializers37365(
              ) {
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10": x10.ast.X10FieldAssign_c
                ((x10aux::ref<au::edu::anu::mm::MultipoleExpansion>)this)->
                  FMGL(X10__object_lock_id0) =
                  ((x10_int)-1);
            }
            const x10aux::serialization_id_t au::edu::anu::mm::MultipoleExpansion::_serialization_id = 
                x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::MultipoleExpansion::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);
            
            void au::edu::anu::mm::MultipoleExpansion::_serialize_body(x10aux::serialization_buffer& buf) {
                au::edu::anu::mm::Expansion::_serialize_body(buf);
                
            }
            
            x10aux::ref<x10::lang::Reference> au::edu::anu::mm::MultipoleExpansion::_deserializer(x10aux::deserialization_buffer& buf) {
                x10aux::ref<au::edu::anu::mm::MultipoleExpansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MultipoleExpansion>(), 0, sizeof(au::edu::anu::mm::MultipoleExpansion))) au::edu::anu::mm::MultipoleExpansion();
                buf.record_reference(this_);
                this_->_deserialize_body(buf);
                return this_;
            }
            
            void au::edu::anu::mm::MultipoleExpansion::_deserialize_body(x10aux::deserialization_buffer& buf) {
                au::edu::anu::mm::Expansion::_deserialize_body(buf);
                
            }
            
            
        x10aux::RuntimeType au::edu::anu::mm::MultipoleExpansion::rtt;
        void au::edu::anu::mm::MultipoleExpansion::_initRTT() {
            if (rtt.initStageOne(&rtt)) return;
            const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<au::edu::anu::mm::Expansion>()};
            rtt.initStageTwo("au.edu.anu.mm.MultipoleExpansion",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
        }
        /* END of MultipoleExpansion */
/*************************************************/
