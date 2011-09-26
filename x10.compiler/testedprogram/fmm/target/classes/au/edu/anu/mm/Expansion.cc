/*************************************************/
/* START of Expansion */
#include <au/edu/anu/mm/Expansion.h>

#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/array/Array.h>
#include <x10/lang/Complex.h>
#include <au/edu/anu/mm/ExpansionRegion.h>
#include <x10/array/Region.h>
#include <x10/array/RectLayout.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/Runtime.h>
#include <x10/compiler/Finalization.h>
#include <x10/compiler/Abort.h>
#include <x10/compiler/CompilerFlags.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/String.h>
#include <x10/util/StringBuilder.h>
#include <x10/lang/Double.h>
#include <x10/array/RectRegion1D.h>
#include <x10/lang/IntRange.h>
#include <x10/lang/Math.h>

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldDecl_c

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::Expansion::getOrderedLock(
  ) {
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::Expansion.X10$class_lock_id1");
    x10_int __var424__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var424__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var425__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var425__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::Expansion.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::Expansion.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__426 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::Expansion::FMGL(X10__class_lock_id1__deserialize));


//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::Expansion::getStaticOrderedLock(
  ) {
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId54327 = au::edu::anu::mm::Expansion::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId54327);
    }))
    ;
    
}

//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldDecl_c
/** The terms X_{lm} (with m >= 0) in this expansion */
                                                       //#line 31 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldDecl_c
                                                       /** The number of terms in the expansion. */

//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::Expansion::_constructor(
  x10_int p) {
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::Expansion> this5432855270 =
          ((x10aux::ref<au::edu::anu::mm::Expansion>)this);
        {
            
            //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5432855270)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<au::edu::anu::mm::ExpansionRegion> expRegion =
      
    x10aux::ref<au::edu::anu::mm::ExpansionRegion>((new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion))) au::edu::anu::mm::ExpansionRegion()))
    ;
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorCall_c
    (expRegion)->::au::edu::anu::mm::ExpansionRegion::_constructor(
      p,
      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(terms) = (__extension__ ({
        
        //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc38210 =
          
        x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
        ;
        
        //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> reg54774 =
              x10aux::class_cast_unchecked<x10aux::ref<x10::array::Region> >(expRegion);
            {
                
                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc38210)->::x10::lang::Object::_constructor();
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38210)->
                  FMGL(region) = (reg54774);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38210)->
                  FMGL(rank) = x10aux::nullCheck(reg54774)->
                                 FMGL(rank);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38210)->
                  FMGL(rect) = x10aux::nullCheck(reg54774)->
                                 FMGL(rect);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38210)->
                  FMGL(zeroBased) = x10aux::nullCheck(reg54774)->
                                      FMGL(zeroBased);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38210)->
                  FMGL(rail) = x10aux::nullCheck(reg54774)->
                                 FMGL(rail);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38210)->
                  FMGL(size) = x10aux::nullCheck(reg54774)->size();
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38210)->
                  FMGL(layout) = (__extension__ ({
                    
                    //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1995454775 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1995454775)->::x10::array::RectLayout::_constructor(
                      reg54774);
                    alloc1995454775;
                }))
                ;
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n54776 = (__extension__ ({
                    
                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this54778 =
                      x10aux::nullCheck(alloc38210)->
                        FMGL(layout);
                    this54778->FMGL(size);
                }))
                ;
                
                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38210)->
                  FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n54776, 8, false, true);
            }
            
        }))
        ;
        alloc38210;
    }))
    ;
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(p) = p;
    
}
x10aux::ref<au::edu::anu::mm::Expansion> au::edu::anu::mm::Expansion::_make(
  x10_int p) {
    x10aux::ref<au::edu::anu::mm::Expansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::Expansion>(), 0, sizeof(au::edu::anu::mm::Expansion))) au::edu::anu::mm::Expansion();
    this_->_constructor(p);
    return this_;
}



//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::Expansion::_constructor(
  x10_int p,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::Expansion> this5477955271 =
          ((x10aux::ref<au::edu::anu::mm::Expansion>)this);
        {
            
            //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5477955271)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<au::edu::anu::mm::ExpansionRegion> expRegion =
      
    x10aux::ref<au::edu::anu::mm::ExpansionRegion>((new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion))) au::edu::anu::mm::ExpansionRegion()))
    ;
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorCall_c
    (expRegion)->::au::edu::anu::mm::ExpansionRegion::_constructor(
      p,
      x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(terms) =
      (__extension__ ({
        
        //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc38211 =
          
        x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
        ;
        
        //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> reg54782 =
              x10aux::class_cast_unchecked<x10aux::ref<x10::array::Region> >(expRegion);
            {
                
                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc38211)->::x10::lang::Object::_constructor();
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38211)->
                  FMGL(region) =
                  (reg54782);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38211)->
                  FMGL(rank) =
                  x10aux::nullCheck(reg54782)->
                    FMGL(rank);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38211)->
                  FMGL(rect) =
                  x10aux::nullCheck(reg54782)->
                    FMGL(rect);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38211)->
                  FMGL(zeroBased) =
                  x10aux::nullCheck(reg54782)->
                    FMGL(zeroBased);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38211)->
                  FMGL(rail) =
                  x10aux::nullCheck(reg54782)->
                    FMGL(rail);
                
                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38211)->
                  FMGL(size) =
                  x10aux::nullCheck(reg54782)->size();
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38211)->
                  FMGL(layout) =
                  (__extension__ ({
                    
                    //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1995454783 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1995454783)->::x10::array::RectLayout::_constructor(
                      reg54782);
                    alloc1995454783;
                }))
                ;
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n54784 =
                  (__extension__ ({
                    
                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this54786 =
                      x10aux::nullCheck(alloc38211)->
                        FMGL(layout);
                    this54786->
                      FMGL(size);
                }))
                ;
                
                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38211)->
                  FMGL(raw) =
                  x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n54784, 8, false, true);
            }
            
        }))
        ;
        alloc38211;
    }))
    ;
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(p) =
      p;
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::Expansion> au::edu::anu::mm::Expansion::_make(
  x10_int p,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::Expansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::Expansion>(), 0, sizeof(au::edu::anu::mm::Expansion))) au::edu::anu::mm::Expansion();
    this_->_constructor(p,
    paramLock);
    return this_;
}



//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::Expansion::_constructor(
  x10aux::ref<au::edu::anu::mm::Expansion> e)
{
    
    //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::Expansion> this5478755272 =
          ((x10aux::ref<au::edu::anu::mm::Expansion>)this);
        {
            
            //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5478755272)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(terms) =
      (__extension__ ({
        
        //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc38212 =
          
        x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
        ;
        
        //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 314 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<x10::lang::Complex> > init54790 =
              x10aux::nullCheck(e)->
                FMGL(terms);
            {
                
                //#line 314 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc38212)->::x10::lang::Object::_constructor();
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38212)->
                  FMGL(region) =
                  x10aux::nullCheck(init54790)->
                    FMGL(region);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38212)->
                  FMGL(rank) =
                  x10aux::nullCheck(init54790)->
                    FMGL(rank);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38212)->
                  FMGL(rect) =
                  x10aux::nullCheck(init54790)->
                    FMGL(rect);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38212)->
                  FMGL(zeroBased) =
                  x10aux::nullCheck(init54790)->
                    FMGL(zeroBased);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38212)->
                  FMGL(rail) =
                  x10aux::nullCheck(init54790)->
                    FMGL(rail);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38212)->
                  FMGL(size) =
                  x10aux::nullCheck(init54790)->
                    FMGL(size);
                
                //#line 317 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38212)->
                  FMGL(layout) =
                  (__extension__ ({
                    
                    //#line 317 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1996654791 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 317 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1996654791)->::x10::array::RectLayout::_constructor(
                      x10aux::nullCheck(alloc38212)->
                        FMGL(region));
                    alloc1996654791;
                }))
                ;
                
                //#line 318 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n54792 =
                  (__extension__ ({
                    
                    //#line 318 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this54795 =
                      x10aux::nullCheck(alloc38212)->
                        FMGL(layout);
                    this54795->
                      FMGL(size);
                }))
                ;
                
                //#line 319 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::util::IndexedMemoryChunk<x10::lang::Complex > r54793 =
                  x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n54792, 8, false, false);
                
                //#line 320 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                x10::util::IndexedMemoryChunk<void>::copy<x10::lang::Complex >(x10aux::nullCheck(init54790)->
                                                                                 FMGL(raw),((x10_int)0),r54793,((x10_int)0),n54792);
                
                //#line 321 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38212)->
                  FMGL(raw) =
                  r54793;
            }
            
        }))
        ;
        alloc38212;
    }))
    ;
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(p) =
      x10aux::nullCheck(e)->
        FMGL(p);
    
}
x10aux::ref<au::edu::anu::mm::Expansion> au::edu::anu::mm::Expansion::_make(
  x10aux::ref<au::edu::anu::mm::Expansion> e)
{
    x10aux::ref<au::edu::anu::mm::Expansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::Expansion>(), 0, sizeof(au::edu::anu::mm::Expansion))) au::edu::anu::mm::Expansion();
    this_->_constructor(e);
    return this_;
}



//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::Expansion::_constructor(
  x10aux::ref<au::edu::anu::mm::Expansion> e,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::Expansion> this5479655273 =
          ((x10aux::ref<au::edu::anu::mm::Expansion>)this);
        {
            
            //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5479655273)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(terms) =
      (__extension__ ({
        
        //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc38213 =
          
        x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
        ;
        
        //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 314 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<x10::lang::Complex> > init54799 =
              x10aux::nullCheck(e)->
                FMGL(terms);
            {
                
                //#line 314 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc38213)->::x10::lang::Object::_constructor();
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38213)->
                  FMGL(region) =
                  x10aux::nullCheck(init54799)->
                    FMGL(region);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38213)->
                  FMGL(rank) =
                  x10aux::nullCheck(init54799)->
                    FMGL(rank);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38213)->
                  FMGL(rect) =
                  x10aux::nullCheck(init54799)->
                    FMGL(rect);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38213)->
                  FMGL(zeroBased) =
                  x10aux::nullCheck(init54799)->
                    FMGL(zeroBased);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38213)->
                  FMGL(rail) =
                  x10aux::nullCheck(init54799)->
                    FMGL(rail);
                
                //#line 316 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38213)->
                  FMGL(size) =
                  x10aux::nullCheck(init54799)->
                    FMGL(size);
                
                //#line 317 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38213)->
                  FMGL(layout) =
                  (__extension__ ({
                    
                    //#line 317 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1996654800 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 317 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1996654800)->::x10::array::RectLayout::_constructor(
                      x10aux::nullCheck(alloc38213)->
                        FMGL(region));
                    alloc1996654800;
                }))
                ;
                
                //#line 318 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n54801 =
                  (__extension__ ({
                    
                    //#line 318 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this54804 =
                      x10aux::nullCheck(alloc38213)->
                        FMGL(layout);
                    this54804->
                      FMGL(size);
                }))
                ;
                
                //#line 319 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::util::IndexedMemoryChunk<x10::lang::Complex > r54802 =
                  x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n54801, 8, false, false);
                
                //#line 320 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                x10::util::IndexedMemoryChunk<void>::copy<x10::lang::Complex >(x10aux::nullCheck(init54799)->
                                                                                 FMGL(raw),((x10_int)0),r54802,((x10_int)0),n54801);
                
                //#line 321 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc38213)->
                  FMGL(raw) =
                  r54802;
            }
            
        }))
        ;
        alloc38213;
    }))
    ;
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(p) =
      x10aux::nullCheck(e)->
        FMGL(p);
    
    //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::Expansion> au::edu::anu::mm::Expansion::_make(
  x10aux::ref<au::edu::anu::mm::Expansion> e,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::Expansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::Expansion>(), 0, sizeof(au::edu::anu::mm::Expansion))) au::edu::anu::mm::Expansion();
    this_->_constructor(e,
    paramLock);
    return this_;
}



//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::Expansion::add(x10aux::ref<au::edu::anu::mm::Expansion> e) {
    {
        
        //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::Throwable> throwable55552 =
          X10_NULL;
        
        //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.Try_c
        try {
            {
                
                //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Call_c
                x10::util::concurrent::OrderedLock::acquireTwoLocks(
                  ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->getOrderedLock(),
                  x10aux::nullCheck(e)->getOrderedLock());
                
                //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Call_c
                x10::lang::Runtime::pushAtomic();
                
                //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                x10_int i38232max3823455307 =
                  ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                    FMGL(p);
                
                //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
                {
                    x10_int i3823255308;
                    for (
                         //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                         i3823255308 = ((x10_int)0);
                         ((i3823255308) <= (i38232max3823455307));
                         
                         //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                         i3823255308 =
                           ((x10_int) ((i3823255308) + (((x10_int)1)))))
                    {
                        
                        //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10_int l55309 =
                          i3823255308;
                        
                        //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10_int i38216min3821755302 =
                          ((x10_int) -(l55309));
                        
                        //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10_int i38216max3821855303 =
                          l55309;
                        
                        //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
                        {
                            x10_int i3821655304;
                            for (
                                 //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                 i3821655304 =
                                   i38216min3821755302;
                                 ((i3821655304) <= (i38216max3821855303));
                                 
                                 //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                                 i3821655304 =
                                   ((x10_int) ((i3821655304) + (((x10_int)1)))))
                            {
                                
                                //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                x10_int m55305 =
                                  i3821655304;
                                
                                //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                                (__extension__ ({
                                    
                                    //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                    x10aux::ref<x10::array::Array<x10::lang::Complex> > this5483055274 =
                                      ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                        FMGL(terms);
                                    
                                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05482755275 =
                                      l55309;
                                    
                                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15482855276 =
                                      m55305;
                                    
                                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Complex v5482955277 =
                                      x10aux::nullCheck((__extension__ ({
                                          
                                          //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                          x10aux::ref<x10::array::Array<x10::lang::Complex> > this5480755278 =
                                            ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                              FMGL(terms);
                                          
                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                          x10_int i05480555279 =
                                            l55309;
                                          
                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                          x10_int i15480655280 =
                                            m55305;
                                          
                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                          x10::lang::Complex ret5480855281;
                                          {
                                              
                                              //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                              ret5480855281 =
                                                (x10aux::nullCheck(this5480755278)->
                                                   FMGL(raw))->__apply((__extension__ ({
                                                  
                                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                  x10::array::RectLayout this5481355282 =
                                                    x10aux::nullCheck(this5480755278)->
                                                      FMGL(layout);
                                                  
                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                  x10_int i05481055283 =
                                                    i05480555279;
                                                  
                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                  x10_int i15481155284 =
                                                    i15480655280;
                                                  
                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                  x10_int ret5481455285;
                                                  {
                                                      
                                                      //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int offset5481255286 =
                                                        ((x10_int) ((i05481055283) - (this5481355282->
                                                                                        FMGL(min0))));
                                                      
                                                      //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                      offset5481255286 =
                                                        ((x10_int) ((((x10_int) ((((x10_int) ((offset5481255286) * (this5481355282->
                                                                                                                      FMGL(delta1))))) + (i15481155284)))) - (this5481355282->
                                                                                                                                                                FMGL(min1))));
                                                      
                                                      //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                      ret5481455285 =
                                                        offset5481255286;
                                                  }
                                                  ret5481455285;
                                              }))
                                              );
                                          }
                                          ret5480855281;
                                      }))
                                      )->x10::lang::Complex::__plus(
                                        (__extension__ ({
                                            
                                            //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                            x10aux::ref<x10::array::Array<x10::lang::Complex> > this5481855287 =
                                              x10aux::nullCheck(e)->
                                                FMGL(terms);
                                            
                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05481655288 =
                                              l55309;
                                            
                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10_int i15481755289 =
                                              m55305;
                                            
                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10::lang::Complex ret5481955290;
                                            {
                                                
                                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                ret5481955290 =
                                                  (x10aux::nullCheck(this5481855287)->
                                                     FMGL(raw))->__apply((__extension__ ({
                                                    
                                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10::array::RectLayout this5482455291 =
                                                      x10aux::nullCheck(this5481855287)->
                                                        FMGL(layout);
                                                    
                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int i05482155292 =
                                                      i05481655288;
                                                    
                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int i15482255293 =
                                                      i15481755289;
                                                    
                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int ret5482555294;
                                                    {
                                                        
                                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int offset5482355295 =
                                                          ((x10_int) ((i05482155292) - (this5482455291->
                                                                                          FMGL(min0))));
                                                        
                                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                        offset5482355295 =
                                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5482355295) * (this5482455291->
                                                                                                                        FMGL(delta1))))) + (i15482255293)))) - (this5482455291->
                                                                                                                                                                  FMGL(min1))));
                                                        
                                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                        ret5482555294 =
                                                          offset5482355295;
                                                    }
                                                    ret5482555294;
                                                }))
                                                );
                                            }
                                            ret5481955290;
                                        }))
                                        );
                                    
                                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Complex ret5483155296;
                                    {
                                        
                                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                        (x10aux::nullCheck(this5483055274)->
                                           FMGL(raw))->__set((__extension__ ({
                                            
                                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10::array::RectLayout this5483655297 =
                                              x10aux::nullCheck(this5483055274)->
                                                FMGL(layout);
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05483355298 =
                                              i05482755275;
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i15483455299 =
                                              i15482855276;
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int ret5483755300;
                                            {
                                                
                                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int offset5483555301 =
                                                  ((x10_int) ((i05483355298) - (this5483655297->
                                                                                  FMGL(min0))));
                                                
                                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                offset5483555301 =
                                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset5483555301) * (this5483655297->
                                                                                                                FMGL(delta1))))) + (i15483455299)))) - (this5483655297->
                                                                                                                                                          FMGL(min1))));
                                                
                                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                ret5483755300 =
                                                  offset5483555301;
                                            }
                                            ret5483755300;
                                        }))
                                        , v5482955277);
                                        
                                        //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret5483155296 =
                                          v5482955277;
                                    }
                                    ret5483155296;
                                }))
                                ;
                            }
                        }
                        
                    }
                }
                
            }
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Call_c
            x10::compiler::Finalization::plausibleThrow();
        }
        catch (x10aux::__ref& __ref__429) {
            x10aux::ref<x10::lang::Throwable>& __exc__ref__429 = (x10aux::ref<x10::lang::Throwable>&)__ref__429;
            if (true) {
                x10aux::ref<x10::lang::Throwable> formal55553 =
                  static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__429);
                {
                    
                    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                    throwable55552 =
                      formal55553;
                }
            } else
            throw;
        }
        
        //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10If_c
        if ((!x10aux::struct_equals(X10_NULL,
                                    throwable55552)))
        {
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10If_c
            if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable55552))
            {
                
                //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.Throw_c
                x10aux::throwException(x10aux::nullCheck(throwable55552));
            }
            
        }
        
        //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10If_c
        if (true) {
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Call_c
            x10::lang::Runtime::popAtomic();
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Call_c
            x10::util::concurrent::OrderedLock::releaseTwoLocks(
              ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->getOrderedLock(),
              x10aux::nullCheck(e)->getOrderedLock());
        }
        
        //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10If_c
        if ((!x10aux::struct_equals(X10_NULL,
                                    throwable55552)))
        {
            
            //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10If_c
            if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable55552)))
            {
                
                //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.Throw_c
                x10aux::throwException(x10aux::nullCheck(throwable55552));
            }
            
        }
        
    }
}

//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::Expansion::unsafeAdd(
  x10aux::ref<au::edu::anu::mm::Expansion> e) {
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
    x10_int i38264max3826655343 = ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                    FMGL(p);
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
    {
        x10_int i3826455344;
        for (
             //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
             i3826455344 = ((x10_int)0); ((i3826455344) <= (i38264max3826655343));
             
             //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
             i3826455344 =
               ((x10_int) ((i3826455344) + (((x10_int)1)))))
        {
            
            //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int l55345 =
              i3826455344;
            
            //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38248min3824955338 =
              ((x10_int) -(l55345));
            
            //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38248max3825055339 =
              l55345;
            
            //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
            {
                x10_int i3824855340;
                for (
                     //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                     i3824855340 =
                       i38248min3824955338;
                     ((i3824855340) <= (i38248max3825055339));
                     
                     //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                     i3824855340 =
                       ((x10_int) ((i3824855340) + (((x10_int)1)))))
                {
                    
                    //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m55341 =
                      i3824855340;
                    
                    //#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5486455310 =
                          ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                            FMGL(terms);
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05486155311 =
                          l55345;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15486255312 =
                          m55341;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5486355313 =
                          x10aux::nullCheck((__extension__ ({
                              
                              //#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                              x10aux::ref<x10::array::Array<x10::lang::Complex> > this5484155314 =
                                ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                  FMGL(terms);
                              
                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10_int i05483955315 =
                                l55345;
                              
                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10_int i15484055316 =
                                m55341;
                              
                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10::lang::Complex ret5484255317;
                              {
                                  
                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                  ret5484255317 =
                                    (x10aux::nullCheck(this5484155314)->
                                       FMGL(raw))->__apply((__extension__ ({
                                      
                                      //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                      x10::array::RectLayout this5484755318 =
                                        x10aux::nullCheck(this5484155314)->
                                          FMGL(layout);
                                      
                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                      x10_int i05484455319 =
                                        i05483955315;
                                      
                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                      x10_int i15484555320 =
                                        i15484055316;
                                      
                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                      x10_int ret5484855321;
                                      {
                                          
                                          //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                          x10_int offset5484655322 =
                                            ((x10_int) ((i05484455319) - (this5484755318->
                                                                            FMGL(min0))));
                                          
                                          //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                          offset5484655322 =
                                            ((x10_int) ((((x10_int) ((((x10_int) ((offset5484655322) * (this5484755318->
                                                                                                          FMGL(delta1))))) + (i15484555320)))) - (this5484755318->
                                                                                                                                                    FMGL(min1))));
                                          
                                          //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                          ret5484855321 =
                                            offset5484655322;
                                      }
                                      ret5484855321;
                                  }))
                                  );
                              }
                              ret5484255317;
                          }))
                          )->x10::lang::Complex::__plus(
                            (__extension__ ({
                                
                                //#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::array::Array<x10::lang::Complex> > this5485255323 =
                                  x10aux::nullCheck(e)->
                                    FMGL(terms);
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05485055324 =
                                  l55345;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15485155325 =
                                  m55341;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::lang::Complex ret5485355326;
                                {
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5485355326 =
                                      (x10aux::nullCheck(this5485255323)->
                                         FMGL(raw))->__apply((__extension__ ({
                                        
                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5485855327 =
                                          x10aux::nullCheck(this5485255323)->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05485555328 =
                                          i05485055324;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15485655329 =
                                          i15485155325;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5485955330;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5485755331 =
                                              ((x10_int) ((i05485555328) - (this5485855327->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5485755331 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5485755331) * (this5485855327->
                                                                                                            FMGL(delta1))))) + (i15485655329)))) - (this5485855327->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5485955330 =
                                              offset5485755331;
                                        }
                                        ret5485955330;
                                    }))
                                    );
                                }
                                ret5485355326;
                            }))
                            );
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5486555332;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(this5486455310)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5487055333 =
                                  x10aux::nullCheck(this5486455310)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05486755334 =
                                  i05486155311;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15486855335 =
                                  i15486255312;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5487155336;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5486955337 =
                                      ((x10_int) ((i05486755334) - (this5487055333->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5486955337 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5486955337) * (this5487055333->
                                                                                                    FMGL(delta1))))) + (i15486855335)))) - (this5487055333->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5487155336 =
                                      offset5486955337;
                                }
                                ret5487155336;
                            }))
                            , v5486355313);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5486555332 =
                              v5486355313;
                        }
                        ret5486555332;
                    }))
                    ;
                }
            }
            
        }
    }
    
}

//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::mm::Expansion::toString(
  ) {
    
    //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::util::StringBuilder> s =
      
    x10aux::ref<x10::util::StringBuilder>((new (memset(x10aux::alloc<x10::util::StringBuilder>(), 0, sizeof(x10::util::StringBuilder))) x10::util::StringBuilder()))
    ;
    
    //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10ConstructorCall_c
    (s)->::x10::util::StringBuilder::_constructor();
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
    x10_int i38296max3829855360 = ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                    FMGL(p);
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
    {
        x10_int i3829655361;
        for (
             //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
             i3829655361 = ((x10_int)0); ((i3829655361) <= (i38296max3829855360));
             
             //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
             i3829655361 =
               ((x10_int) ((i3829655361) + (((x10_int)1)))))
        {
            
            //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i55362 =
              i3829655361;
            
            //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38280min3828155355 =
              ((x10_int) -(i55362));
            
            //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38280max3828255356 =
              i55362;
            
            //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
            {
                x10_int i3828055357;
                for (
                     //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                     i3828055357 =
                       i38280min3828155355;
                     ((i3828055357) <= (i38280max3828255356));
                     
                     //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                     i3828055357 =
                       ((x10_int) ((i3828055357) + (((x10_int)1)))))
                {
                    
                    //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10_int j55358 =
                      i3828055357;
                    
                    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Call_c
                    s->add(
                      ((((x10aux::string_utils::lit("")) + ((__extension__ ({
                          
                          //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                          x10aux::ref<x10::array::Array<x10::lang::Complex> > this5506155346 =
                            ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                              FMGL(terms);
                          
                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10_int i05505955347 =
                            i55362;
                          
                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10_int i15506055348 =
                            j55358;
                          
                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10::lang::Complex ret5506255349;
                          {
                              
                              //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                              ret5506255349 =
                                (x10aux::nullCheck(this5506155346)->
                                   FMGL(raw))->__apply((__extension__ ({
                                  
                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                  x10::array::RectLayout this5506755350 =
                                    x10aux::nullCheck(this5506155346)->
                                      FMGL(layout);
                                  
                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                  x10_int i05506455351 =
                                    i05505955347;
                                  
                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                  x10_int i15506555352 =
                                    i15506055348;
                                  
                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                  x10_int ret5506855353;
                                  {
                                      
                                      //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                      x10_int offset5506655354 =
                                        ((x10_int) ((i05506455351) - (this5506755350->
                                                                        FMGL(min0))));
                                      
                                      //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                      offset5506655354 =
                                        ((x10_int) ((((x10_int) ((((x10_int) ((offset5506655354) * (this5506755350->
                                                                                                      FMGL(delta1))))) + (i15506555352)))) - (this5506755350->
                                                                                                                                                FMGL(min1))));
                                      
                                      //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                      ret5506855353 =
                                        offset5506655354;
                                  }
                                  ret5506855353;
                              }))
                              );
                          }
                          ret5506255349;
                      }))
                      ))) + (x10aux::string_utils::lit(" "))));
                }
            }
            
            //#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Call_c
            s->add(
              x10aux::string_utils::lit("\n"));
        }
    }
    
    //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Return_c
    return s->toString();
    
}

//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > >
  au::edu::anu::mm::Expansion::genComplexK(
  x10_double phi,
  x10_int p) {
    
    //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > complexK =
      
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > >(), 0, sizeof(x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > >))) x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > >()))
    ;
    
    //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        {
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
            (complexK)->::x10::lang::Object::_constructor();
            
            //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> myReg55071 =
              x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::RectRegion1D> alloc1996055072 =
                  
                x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                ;
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc1996055072)->::x10::array::RectRegion1D::_constructor(
                  ((x10_int)0),
                  ((x10_int) ((((x10_int)2)) - (((x10_int)1)))));
                alloc1996055072;
            }))
            );
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            complexK->FMGL(region) = myReg55071;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            complexK->FMGL(rank) = ((x10_int)1);
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            complexK->FMGL(rect) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            complexK->FMGL(zeroBased) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            complexK->FMGL(rail) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            complexK->FMGL(size) = ((x10_int)2);
            
            //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            complexK->FMGL(layout) = (__extension__ ({
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout alloc1996155073 =
                  
                x10::array::RectLayout::_alloc();
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int _max055077 = ((x10_int) ((((x10_int)2)) - (((x10_int)1))));
                    {
                        
                        //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(rank) = ((x10_int)1);
                        
                        //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(min0) = ((x10_int)0);
                        
                        //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(delta0) = ((x10_int) ((((x10_int) ((_max055077) - (((x10_int)0))))) + (((x10_int)1))));
                        
                        //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(size) = ((alloc1996155073->
                                           FMGL(delta0)) > (((x10_int)0)))
                          ? (alloc1996155073->
                               FMGL(delta0))
                          : (((x10_int)0));
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(min1) = ((x10_int)0);
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(delta1) = ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(min2) = ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(delta2) = ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(min3) = ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(delta3) = ((x10_int)0);
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(min) = X10_NULL;
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155073->
                          FMGL(delta) = X10_NULL;
                    }
                    
                }))
                ;
                alloc1996155073;
            }))
            ;
            
            //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int n55074 = (__extension__ ({
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this55079 =
                  complexK->
                    FMGL(layout);
                this55079->FMGL(size);
            }))
            ;
            
            //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            complexK->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<x10::array::Array<x10::lang::Complex> > >(n55074, 8, false, true);
        }
        
    }))
    ;
    
    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
    {
        x10_int i3832855390;
        for (
             //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
             i3832855390 = ((x10_int)0); ((i3832855390) <= (((x10_int)1)));
             
             //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
             i3832855390 =
               ((x10_int) ((i3832855390) + (((x10_int)1)))))
        {
            
            //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int r55391 =
              i3832855390;
            
            //#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05508755378 =
                  r55391;
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10::lang::Complex> > v5508855379 =
                  (__extension__ ({
                    
                    //#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Array<x10::lang::Complex> > alloc3821455380 =
                      
                    x10aux::ref<x10::array::Array<x10::lang::Complex> >((new (memset(x10aux::alloc<x10::array::Array<x10::lang::Complex> >(), 0, sizeof(x10::array::Array<x10::lang::Complex>))) x10::array::Array<x10::lang::Complex>()))
                    ;
                    
                    //#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Region> reg5508255381 =
                          (__extension__ ({
                            
                            //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                            x10::lang::IntRange r5508055382 =
                              x10::lang::IntRange::_make(((x10_int) -(p)), p);
                            x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                
                                //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::array::RectRegion1D> alloc206315508155383 =
                                  
                                x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                ;
                                
                                //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                                (alloc206315508155383)->::x10::array::RectRegion1D::_constructor(
                                  r5508055382->
                                    FMGL(min),
                                  r5508055382->
                                    FMGL(max));
                                alloc206315508155383;
                            }))
                            );
                        }))
                        ;
                        {
                            
                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                            (alloc3821455380)->::x10::lang::Object::_constructor();
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc3821455380)->
                              FMGL(region) =
                              (reg5508255381);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc3821455380)->
                              FMGL(rank) =
                              x10aux::nullCheck(reg5508255381)->
                                FMGL(rank);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc3821455380)->
                              FMGL(rect) =
                              x10aux::nullCheck(reg5508255381)->
                                FMGL(rect);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc3821455380)->
                              FMGL(zeroBased) =
                              x10aux::nullCheck(reg5508255381)->
                                FMGL(zeroBased);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc3821455380)->
                              FMGL(rail) =
                              x10aux::nullCheck(reg5508255381)->
                                FMGL(rail);
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc3821455380)->
                              FMGL(size) =
                              x10aux::nullCheck(reg5508255381)->size();
                            
                            //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc3821455380)->
                              FMGL(layout) =
                              (__extension__ ({
                                
                                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout alloc199545508355384 =
                                  
                                x10::array::RectLayout::_alloc();
                                
                                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                                (alloc199545508355384)->::x10::array::RectLayout::_constructor(
                                  reg5508255381);
                                alloc199545508355384;
                            }))
                            ;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int n5508455385 =
                              (__extension__ ({
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5508655386 =
                                  x10aux::nullCheck(alloc3821455380)->
                                    FMGL(layout);
                                this5508655386->
                                  FMGL(size);
                            }))
                            ;
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc3821455380)->
                              FMGL(raw) =
                              x10::util::IndexedMemoryChunk<void>::allocate<x10::lang::Complex >(n5508455385, 8, false, true);
                        }
                        
                    }))
                    ;
                    alloc3821455380;
                }))
                ;
                
                //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10::lang::Complex> > ret5508955387;
                {
                    
                    //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (complexK->
                       FMGL(raw))->__set(i05508755378, v5508855379);
                    
                    //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5508955387 =
                      v5508855379;
                }
                ret5508955387;
            }))
            ;
            
            //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38312min3831355374 =
              ((x10_int) -(p));
            
            //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38312max3831455375 =
              p;
            
            //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
            {
                x10_int i3831255376;
                for (
                     //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                     i3831255376 =
                       i38312min3831355374;
                     ((i3831255376) <= (i38312max3831455375));
                     
                     //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                     i3831255376 =
                       ((x10_int) ((i3831255376) + (((x10_int)1)))))
                {
                    
                    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10_int k55377 =
                      i3831255376;
                    
                    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5510655367 =
                          (__extension__ ({
                            
                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05509655368 =
                              r55391;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10::lang::Complex> > ret5509755369;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                            goto __ret5509855370; __ret5509855370: {
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5509755369 =
                                  (complexK->
                                     FMGL(raw))->__apply(i05509655368);
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret5509855370_end_;
                            }goto __ret5509855370_end_; __ret5509855370_end_: ;
                            }
                            ret5509755369;
                            }))
                            ;
                            
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05510455371 =
                          k55377;
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5510555372 =
                          x10::lang::Math::exp(
                            x10aux::nullCheck(x10aux::nullCheck(x10aux::nullCheck(x10::lang::Complex::_make(0.0,1.0))->x10::lang::Complex::__times(
                                                                  ((x10_double) (k55377))))->x10::lang::Complex::__times(
                                                phi))->x10::lang::Complex::__times(
                              ((x10_double) ((x10aux::struct_equals(r55391,
                                                                    ((x10_int)0)))
                                ? (((x10_int)1))
                                : (((x10_int)-1))))));
                        
                        //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5510755373;
                        {
                            
                            //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(this5510655367)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5511155363 =
                                  x10aux::nullCheck(this5510655367)->
                                    FMGL(layout);
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05510955364 =
                                  i05510455371;
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5511255365;
                                {
                                    
                                    //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5511055366 =
                                      ((x10_int) ((i05510955364) - (this5511155363->
                                                                      FMGL(min0))));
                                    
                                    //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5511255365 =
                                      offset5511055366;
                                }
                                ret5511255365;
                            }))
                            , v5510555372);
                            
                            //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5510755373 =
                              v5510555372;
                        }
                        ret5510755373;
                        }))
                        ;
                    }
                }
                
            }
        }
        
    
    //#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Return_c
    return complexK;
    }
    

//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::Expansion::rotate(
  x10aux::ref<x10::array::Array<x10::lang::Complex> > temp,
  x10aux::ref<x10::array::Array<x10::lang::Complex> > complexK,
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > wigner) {
    
    //#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
    x10_int i38392max3839455469 = ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                    FMGL(p);
    
    //#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
    {
        x10_int i3839255470;
        for (
             //#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
             i3839255470 = ((x10_int)1); ((i3839255470) <= (i38392max3839455469));
             
             //#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
             i3839255470 =
               ((x10_int) ((i3839255470) + (((x10_int)1)))))
        {
            
            //#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int l55471 =
              i3839255470;
            
            //#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<x10_double> > Dl55463 =
              (__extension__ ({
                
                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05511455464 =
                  l55471;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10_double> > ret5511555465;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                goto __ret5511655466; __ret5511655466: {
                {
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5511555465 =
                      (x10aux::nullCheck(wigner)->
                         FMGL(raw))->__apply(i05511455464);
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                    goto __ret5511655466_end_;
                }goto __ret5511655466_end_; __ret5511655466_end_: ;
                }
                ret5511555465;
                }))
                ;
                
            
            //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38344min3834555455 =
              ((x10_int) -(l55471));
            
            //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38344max3834655456 =
              l55471;
            
            //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
            {
                x10_int i3834455457;
                for (
                     //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                     i3834455457 =
                       i38344min3834555455;
                     ((i3834455457) <= (i38344max3834655456));
                     
                     //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                     i3834455457 =
                       ((x10_int) ((i3834455457) + (((x10_int)1)))))
                {
                    
                    //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10_int k55458 =
                      i3834455457;
                    
                    //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05514155400 =
                          k55458;
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5514255401 =
                          x10aux::nullCheck((__extension__ ({
                              
                              //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                              x10aux::ref<x10::array::Array<x10::lang::Complex> > this5512455402 =
                                ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                  FMGL(terms);
                              
                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10_int i05512255403 =
                                l55471;
                              
                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10_int i15512355404 =
                                k55458;
                              
                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10::lang::Complex ret5512555405;
                              {
                                  
                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                  ret5512555405 =
                                    (x10aux::nullCheck(this5512455402)->
                                       FMGL(raw))->__apply((__extension__ ({
                                      
                                      //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                      x10::array::RectLayout this5513055406 =
                                        x10aux::nullCheck(this5512455402)->
                                          FMGL(layout);
                                      
                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                      x10_int i05512755407 =
                                        i05512255403;
                                      
                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                      x10_int i15512855408 =
                                        i15512355404;
                                      
                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                      x10_int ret5513155409;
                                      {
                                          
                                          //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                          x10_int offset5512955410 =
                                            ((x10_int) ((i05512755407) - (this5513055406->
                                                                            FMGL(min0))));
                                          
                                          //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                          offset5512955410 =
                                            ((x10_int) ((((x10_int) ((((x10_int) ((offset5512955410) * (this5513055406->
                                                                                                          FMGL(delta1))))) + (i15512855408)))) - (this5513055406->
                                                                                                                                                    FMGL(min1))));
                                          
                                          //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                          ret5513155409 =
                                            offset5512955410;
                                      }
                                      ret5513155409;
                                  }))
                                  );
                              }
                              ret5512555405;
                          }))
                          )->x10::lang::Complex::__times(
                            (__extension__ ({
                                
                                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05513355411 =
                                  k55458;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::lang::Complex ret5513455412;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                goto __ret5513555413; __ret5513555413: {
                                {
                                    
                                    //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5513455412 =
                                      (x10aux::nullCheck(complexK)->
                                         FMGL(raw))->__apply((__extension__ ({
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5513855392 =
                                          x10aux::nullCheck(complexK)->
                                            FMGL(layout);
                                        
                                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05513655393 =
                                          i05513355411;
                                        
                                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5513955394;
                                        {
                                            
                                            //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5513755395 =
                                              ((x10_int) ((i05513655393) - (this5513855392->
                                                                              FMGL(min0))));
                                            
                                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5513955394 =
                                              offset5513755395;
                                        }
                                        ret5513955394;
                                    }))
                                    );
                                    
                                    //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                    goto __ret5513555413_end_;
                                }goto __ret5513555413_end_; __ret5513555413_end_: ;
                                }
                                ret5513455412;
                                }))
                                );
                          
                        
                        //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5514355414;
                        {
                            
                            //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(temp)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5514755396 =
                                  x10aux::nullCheck(temp)->
                                    FMGL(layout);
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05514555397 =
                                  i05514155400;
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5514855398;
                                {
                                    
                                    //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5514655399 =
                                      ((x10_int) ((i05514555397) - (this5514755396->
                                                                      FMGL(min0))));
                                    
                                    //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5514855398 =
                                      offset5514655399;
                                }
                                ret5514855398;
                            }))
                            , v5514255401);
                            
                            //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5514355414 =
                              v5514255401;
                        }
                        ret5514355414;
                        }))
                        ;
                    }
                }
                
            
            //#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int m_sign55467 =
              ((x10_int)1);
            
            //#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
            x10_int i38376max3837855460 =
              l55471;
            
            //#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
            {
                x10_int i3837655461;
                for (
                     //#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                     i3837655461 =
                       ((x10_int)0);
                     ((i3837655461) <= (i38376max3837855460));
                     
                     //#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                     i3837655461 =
                       ((x10_int) ((i3837655461) + (((x10_int)1)))))
                {
                    
                    //#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10_int m55462 =
                      i3837655461;
                    
                    //#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10::lang::Complex O_lm55434 =
                      x10::lang::Complex::_make(0.0,0.0);
                    
                    //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i38360min3836155430 =
                      ((x10_int) -(l55471));
                    
                    //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                    x10_int i38360max3836255431 =
                      l55471;
                    
                    //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
                    {
                        x10_int i3836055432;
                        for (
                             //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                             i3836055432 =
                               i38360min3836155430;
                             ((i3836055432) <= (i38360max3836255431));
                             
                             //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                             i3836055432 =
                               ((x10_int) ((i3836055432) + (((x10_int)1)))))
                        {
                            
                            //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                            x10_int k55433 =
                              i3836055432;
                            
                            //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                            O_lm55434 =
                              x10aux::nullCheck(O_lm55434)->x10::lang::Complex::__plus(
                                x10aux::nullCheck((__extension__ ({
                                    
                                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05515055419 =
                                      k55433;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::lang::Complex ret5515155420;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                    goto __ret5515255421; __ret5515255421: {
                                    {
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret5515155420 =
                                          (x10aux::nullCheck(temp)->
                                             FMGL(raw))->__apply((__extension__ ({
                                            
                                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10::array::RectLayout this5515555415 =
                                              x10aux::nullCheck(temp)->
                                                FMGL(layout);
                                            
                                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05515355416 =
                                              i05515055419;
                                            
                                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int ret5515655417;
                                            {
                                                
                                                //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int offset5515455418 =
                                                  ((x10_int) ((i05515355416) - (this5515555415->
                                                                                  FMGL(min0))));
                                                
                                                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                ret5515655417 =
                                                  offset5515455418;
                                            }
                                            ret5515655417;
                                        }))
                                        );
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                        goto __ret5515255421_end_;
                                    }goto __ret5515255421_end_; __ret5515255421_end_: ;
                                    }
                                    ret5515155420;
                                    }))
                                    )->x10::lang::Complex::__times(
                                      (__extension__ ({
                                          
                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                          x10_int i05515855422 =
                                            m55462;
                                          
                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                          x10_int i15515955423 =
                                            k55433;
                                          
                                          //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                          x10_double ret5516055424;
                                          {
                                              
                                              //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                              ret5516055424 =
                                                (x10aux::nullCheck(Dl55463)->
                                                   FMGL(raw))->__apply((__extension__ ({
                                                  
                                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                  x10::array::RectLayout this5516555425 =
                                                    x10aux::nullCheck(Dl55463)->
                                                      FMGL(layout);
                                                  
                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                  x10_int i05516255426 =
                                                    i05515855422;
                                                  
                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                  x10_int i15516355427 =
                                                    i15515955423;
                                                  
                                                  //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                  x10_int ret5516655428;
                                                  {
                                                      
                                                      //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int offset5516455429 =
                                                        ((x10_int) ((i05516255426) - (this5516555425->
                                                                                        FMGL(min0))));
                                                      
                                                      //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                      offset5516455429 =
                                                        ((x10_int) ((((x10_int) ((((x10_int) ((offset5516455429) * (this5516555425->
                                                                                                                      FMGL(delta1))))) + (i15516355427)))) - (this5516555425->
                                                                                                                                                                FMGL(min1))));
                                                      
                                                      //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                      ret5516655428 =
                                                        offset5516455429;
                                                  }
                                                  ret5516655428;
                                              }))
                                              );
                                          }
                                          ret5516055424;
                                      }))
                                      ));
                            }
                        }
                        
                    
                    //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5517155435 =
                          ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                            FMGL(terms);
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05516855436 =
                          l55471;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15516955437 =
                          m55462;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5517055438 =
                          O_lm55434;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5517255439;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(this5517155435)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5517755440 =
                                  x10aux::nullCheck(this5517155435)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05517455441 =
                                  i05516855436;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15517555442 =
                                  i15516955437;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5517855443;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5517655444 =
                                      ((x10_int) ((i05517455441) - (this5517755440->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5517655444 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5517655444) * (this5517755440->
                                                                                                    FMGL(delta1))))) + (i15517555442)))) - (this5517755440->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5517855443 =
                                      offset5517655444;
                                }
                                ret5517855443;
                            }))
                            , v5517055438);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5517255439 =
                              v5517055438;
                        }
                        ret5517255439;
                    }))
                    ;
                    
                    //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10::lang::Complex> > this5518355445 =
                          ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                            FMGL(terms);
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05518055446 =
                          l55471;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15518155447 =
                          ((x10_int) -(m55462));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex v5518255448 =
                          x10aux::nullCheck(x10aux::nullCheck(O_lm55434)->x10::lang::Complex::conjugate())->x10::lang::Complex::__times(
                            ((x10_double) (m_sign55467)));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex ret5518455449;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (x10aux::nullCheck(this5518355445)->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5518955450 =
                                  x10aux::nullCheck(this5518355445)->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05518655451 =
                                  i05518055446;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15518755452 =
                                  i15518155447;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5519055453;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5518855454 =
                                      ((x10_int) ((i05518655451) - (this5518955450->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5518855454 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5518855454) * (this5518955450->
                                                                                                    FMGL(delta1))))) + (i15518755452)))) - (this5518955450->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5519055453 =
                                      offset5518855454;
                                }
                                ret5519055453;
                            }))
                            , v5518255448);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5518455449 =
                              v5518255448;
                        }
                        ret5518455449;
                    }))
                    ;
                    
                    //#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                    m_sign55467 =
                      ((x10_int) -(m_sign55467));
                    }
                }
                
            }
            }
            
        }
        
    
    //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
    void au::edu::anu::mm::Expansion::backRotate(
      x10aux::ref<x10::array::Array<x10::lang::Complex> > temp,
      x10aux::ref<x10::array::Array<x10::lang::Complex> > complexK,
      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > wigner) {
        
        //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
        x10_int i38456max3845855549 = ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                        FMGL(p);
        
        //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
        {
            x10_int i3845655550;
            for (
                 //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                 i3845655550 = ((x10_int)1);
                 ((i3845655550) <= (i38456max3845855549));
                 
                 //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                 i3845655550 =
                   ((x10_int) ((i3845655550) + (((x10_int)1)))))
            {
                
                //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                x10_int l55551 =
                  i3845655550;
                
                //#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10_double> > Dl55543 =
                  (__extension__ ({
                    
                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i05519255544 =
                      l55551;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Array<x10_double> > ret5519355545;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                    goto __ret5519455546; __ret5519455546: {
                    {
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret5519355545 =
                          (x10aux::nullCheck(wigner)->
                             FMGL(raw))->__apply(i05519255544);
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                        goto __ret5519455546_end_;
                    }goto __ret5519455546_end_; __ret5519455546_end_: ;
                    }
                    ret5519355545;
                    }))
                    ;
                    
                
                //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                x10_int i38408min3840955535 =
                  ((x10_int) -(l55551));
                
                //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                x10_int i38408max3841055536 =
                  l55551;
                
                //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
                {
                    x10_int i3840855537;
                    for (
                         //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                         i3840855537 =
                           i38408min3840955535;
                         ((i3840855537) <= (i38408max3841055536));
                         
                         //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                         i3840855537 =
                           ((x10_int) ((i3840855537) + (((x10_int)1)))))
                    {
                        
                        //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10_int k55538 =
                          i3840855537;
                        
                        //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05521155476 =
                              k55538;
                            
                            //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex v5521255477 =
                              (__extension__ ({
                                
                                //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::array::Array<x10::lang::Complex> > this5520255478 =
                                  ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                    FMGL(terms);
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05520055479 =
                                  l55551;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15520155480 =
                                  k55538;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::lang::Complex ret5520355481;
                                {
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5520355481 =
                                      (x10aux::nullCheck(this5520255478)->
                                         FMGL(raw))->__apply((__extension__ ({
                                        
                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5520855482 =
                                          x10aux::nullCheck(this5520255478)->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05520555483 =
                                          i05520055479;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15520655484 =
                                          i15520155480;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5520955485;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5520755486 =
                                              ((x10_int) ((i05520555483) - (this5520855482->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5520755486 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5520755486) * (this5520855482->
                                                                                                            FMGL(delta1))))) + (i15520655484)))) - (this5520855482->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5520955485 =
                                              offset5520755486;
                                        }
                                        ret5520955485;
                                    }))
                                    );
                                }
                                ret5520355481;
                            }))
                            ;
                            
                            //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5521355487;
                            {
                                
                                //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(temp)->
                                   FMGL(raw))->__set((__extension__ ({
                                    
                                    //#line 517 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5521755472 =
                                      x10aux::nullCheck(temp)->
                                        FMGL(layout);
                                    
                                    //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05521555473 =
                                      i05521155476;
                                    
                                    //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5521855474;
                                    {
                                        
                                        //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5521655475 =
                                          ((x10_int) ((i05521555473) - (this5521755472->
                                                                          FMGL(min0))));
                                        
                                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5521855474 =
                                          offset5521655475;
                                    }
                                    ret5521855474;
                                }))
                                , v5521255477);
                                
                                //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5521355487 =
                                  v5521255477;
                            }
                            ret5521355487;
                        }))
                        ;
                    }
                }
                
                //#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                x10_int m_sign55547 =
                  ((x10_int)1);
                
                //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                x10_int i38440max3844255540 =
                  l55551;
                
                //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
                {
                    x10_int i3844055541;
                    for (
                         //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                         i3844055541 =
                           ((x10_int)0);
                         ((i3844055541) <= (i38440max3844255540));
                         
                         //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                         i3844055541 =
                           ((x10_int) ((i3844055541) + (((x10_int)1)))))
                    {
                        
                        //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10_int m55542 =
                          i3844055541;
                        
                        //#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10::lang::Complex O_lm55511 =
                          x10::lang::Complex::_make(0.0,0.0);
                        
                        //#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10_int i38424min3842555507 =
                          ((x10_int) -(l55551));
                        
                        //#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                        x10_int i38424max3842655508 =
                          l55551;
                        
                        //#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": polyglot.ast.For_c
                        {
                            x10_int i3842455509;
                            for (
                                 //#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                 i3842455509 =
                                   i38424min3842555507;
                                 ((i3842455509) <= (i38424max3842655508));
                                 
                                 //#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                                 i3842455509 =
                                   ((x10_int) ((i3842455509) + (((x10_int)1)))))
                            {
                                
                                //#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                                x10_int k55510 =
                                  i3842455509;
                                
                                //#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                                O_lm55511 =
                                  x10aux::nullCheck(O_lm55511)->x10::lang::Complex::__plus(
                                    x10aux::nullCheck((__extension__ ({
                                        
                                        //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05522055492 =
                                          k55510;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::lang::Complex ret5522155493;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                        goto __ret5522255494; __ret5522255494: {
                                        {
                                            
                                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5522155493 =
                                              (x10aux::nullCheck(temp)->
                                                 FMGL(raw))->__apply((__extension__ ({
                                                
                                                //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10::array::RectLayout this5522555488 =
                                                  x10aux::nullCheck(temp)->
                                                    FMGL(layout);
                                                
                                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05522355489 =
                                                  i05522055492;
                                                
                                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int ret5522655490;
                                                {
                                                    
                                                    //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                    x10_int offset5522455491 =
                                                      ((x10_int) ((i05522355489) - (this5522555488->
                                                                                      FMGL(min0))));
                                                    
                                                    //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                    ret5522655490 =
                                                      offset5522455491;
                                                }
                                                ret5522655490;
                                            }))
                                            );
                                            
                                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                            goto __ret5522255494_end_;
                                        }goto __ret5522255494_end_; __ret5522255494_end_: ;
                                        }
                                        ret5522155493;
                                        }))
                                        )->x10::lang::Complex::__times(
                                          (__extension__ ({
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int i05522855495 =
                                                m55542;
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int i15522955496 =
                                                k55510;
                                              
                                              //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_double ret5523055497;
                                              {
                                                  
                                                  //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                  ret5523055497 =
                                                    (x10aux::nullCheck(Dl55543)->
                                                       FMGL(raw))->__apply((__extension__ ({
                                                      
                                                      //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                      x10::array::RectLayout this5523555498 =
                                                        x10aux::nullCheck(Dl55543)->
                                                          FMGL(layout);
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i05523255499 =
                                                        i05522855495;
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i15523355500 =
                                                        i15522955496;
                                                      
                                                      //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                      x10_int ret5523655501;
                                                      {
                                                          
                                                          //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                          x10_int offset5523455502 =
                                                            ((x10_int) ((i05523255499) - (this5523555498->
                                                                                            FMGL(min0))));
                                                          
                                                          //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                          offset5523455502 =
                                                            ((x10_int) ((((x10_int) ((((x10_int) ((offset5523455502) * (this5523555498->
                                                                                                                          FMGL(delta1))))) + (i15523355500)))) - (this5523555498->
                                                                                                                                                                    FMGL(min1))));
                                                          
                                                          //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                          ret5523655501 =
                                                            offset5523455502;
                                                      }
                                                      ret5523655501;
                                                  }))
                                                  );
                                              }
                                              ret5523055497;
                                          }))
                                          ));
                                }
                            }
                            
                        
                        //#line 147 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                        O_lm55511 =
                          x10aux::nullCheck(O_lm55511)->x10::lang::Complex::__times(
                            (__extension__ ({
                                
                                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05523855512 =
                                  m55542;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::lang::Complex ret5523955513;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                goto __ret5524055514; __ret5524055514: {
                                {
                                    
                                    //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5523955513 =
                                      (x10aux::nullCheck(complexK)->
                                         FMGL(raw))->__apply((__extension__ ({
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5524355503 =
                                          x10aux::nullCheck(complexK)->
                                            FMGL(layout);
                                        
                                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05524155504 =
                                          i05523855512;
                                        
                                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5524455505;
                                        {
                                            
                                            //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5524255506 =
                                              ((x10_int) ((i05524155504) - (this5524355503->
                                                                              FMGL(min0))));
                                            
                                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5524455505 =
                                              offset5524255506;
                                        }
                                        ret5524455505;
                                    }))
                                    );
                                    
                                    //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                    goto __ret5524055514_end_;
                                }goto __ret5524055514_end_; __ret5524055514_end_: ;
                                }
                                ret5523955513;
                                }))
                                );
                        
                        //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10::lang::Complex> > this5524955515 =
                              ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                FMGL(terms);
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05524655516 =
                              l55551;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15524755517 =
                              m55542;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex v5524855518 =
                              O_lm55511;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5525055519;
                            {
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(this5524955515)->
                                   FMGL(raw))->__set((__extension__ ({
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5525555520 =
                                      x10aux::nullCheck(this5524955515)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05525255521 =
                                      i05524655516;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15525355522 =
                                      i15524755517;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5525655523;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5525455524 =
                                          ((x10_int) ((i05525255521) - (this5525555520->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5525455524 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5525455524) * (this5525555520->
                                                                                                        FMGL(delta1))))) + (i15525355522)))) - (this5525555520->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5525655523 =
                                          offset5525455524;
                                    }
                                    ret5525655523;
                                }))
                                , v5524855518);
                                
                                //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5525055519 =
                                  v5524855518;
                            }
                            ret5525055519;
                        }))
                        ;
                        
                        //#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10::lang::Complex> > this5526155525 =
                              ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
                                FMGL(terms);
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05525855526 =
                              l55551;
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15525955527 =
                              ((x10_int) -(m55542));
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex v5526055528 =
                              x10aux::nullCheck(x10aux::nullCheck(O_lm55511)->x10::lang::Complex::conjugate())->x10::lang::Complex::__times(
                                ((x10_double) (m_sign55547)));
                            
                            //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::lang::Complex ret5526255529;
                            {
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                (x10aux::nullCheck(this5526155525)->
                                   FMGL(raw))->__set((__extension__ ({
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5526755530 =
                                      x10aux::nullCheck(this5526155525)->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05526455531 =
                                      i05525855526;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15526555532 =
                                      i15525955527;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5526855533;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5526655534 =
                                          ((x10_int) ((i05526455531) - (this5526755530->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5526655534 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5526655534) * (this5526755530->
                                                                                                        FMGL(delta1))))) + (i15526555532)))) - (this5526755530->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5526855533 =
                                          offset5526655534;
                                    }
                                    ret5526855533;
                                }))
                                , v5526055528);
                                
                                //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5526255529 =
                                  v5526055528;
                            }
                            ret5526255529;
                        }))
                        ;
                        
                        //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10LocalAssign_c
                        m_sign55547 =
                          ((x10_int) -(m_sign55547));
                        }
                        }
                        
                }
                }
                
            }
            
        
        //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
        x10aux::ref<au::edu::anu::mm::Expansion>
          au::edu::anu::mm::Expansion::au__edu__anu__mm__Expansion____au__edu__anu__mm__Expansion__this(
          ) {
            
            //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10Return_c
            return ((x10aux::ref<au::edu::anu::mm::Expansion>)this);
            
        }
        
        //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10MethodDecl_c
        void au::edu::anu::mm::Expansion::__fieldInitializers37996(
          ) {
            
            //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10": x10.ast.X10FieldAssign_c
            ((x10aux::ref<au::edu::anu::mm::Expansion>)this)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        const x10aux::serialization_id_t au::edu::anu::mm::Expansion::_serialization_id = 
            x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::Expansion::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);
        
        void au::edu::anu::mm::Expansion::_serialize_body(x10aux::serialization_buffer& buf) {
            x10::lang::Object::_serialize_body(buf);
            buf.write(this->FMGL(terms));
            buf.write(this->FMGL(p));
            
        }
        
        x10aux::ref<x10::lang::Reference> au::edu::anu::mm::Expansion::_deserializer(x10aux::deserialization_buffer& buf) {
            x10aux::ref<au::edu::anu::mm::Expansion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::Expansion>(), 0, sizeof(au::edu::anu::mm::Expansion))) au::edu::anu::mm::Expansion();
            buf.record_reference(this_);
            this_->_deserialize_body(buf);
            return this_;
        }
        
        void au::edu::anu::mm::Expansion::_deserialize_body(x10aux::deserialization_buffer& buf) {
            x10::lang::Object::_deserialize_body(buf);
            FMGL(terms) = buf.read<x10aux::ref<x10::array::Array<x10::lang::Complex> > >();
            FMGL(p) = buf.read<x10_int>();
        }
        
        x10aux::RuntimeType au::edu::anu::mm::Expansion::rtt;
        void au::edu::anu::mm::Expansion::_initRTT() {
            if (rtt.initStageOne(&rtt)) return;
            const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
            rtt.initStageTwo("au.edu.anu.mm.Expansion",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
        }
        /* END of Expansion */
/*************************************************/
