/*************************************************/
/* START of WignerRotationMatrix */
#include <au/edu/anu/mm/WignerRotationMatrix.h>

#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Double.h>
#include <x10/array/Array.h>
#include <x10/lang/Math.h>
#include <x10/lang/IllegalArgumentException.h>
#include <x10/array/Region.h>
#include <x10/lang/IntRange.h>
#include <x10/array/RectLayout.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/array/RectRegion1D.h>
#include <au/edu/anu/mm/Factorial.h>

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldDecl_c

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::WignerRotationMatrix::getOrderedLock(
  ) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::WignerRotationMatrix>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::WignerRotationMatrix.X10$class_lock_id1");
    x10_int __var491__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var491__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var492__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var492__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::WignerRotationMatrix.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::WignerRotationMatrix.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__493 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__deserialize));


//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::WignerRotationMatrix::getStaticOrderedLock(
  ) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId57119 = au::edu::anu::mm::WignerRotationMatrix::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId57119);
    }))
    ;
    
}

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_A) =
  ((x10_int)0);


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_B) =
  ((x10_int)-1);


//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_C) =
  ((x10_int)2);


//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10_double> >
  au::edu::anu::mm::WignerRotationMatrix::getDmk(
  x10_double theta,
  x10_int l) {
    
    //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
    if (((::fabs(theta)) > (6.283185307179586)))
    {
        
        //#line 31 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.Throw_c
        x10aux::throwException(x10aux::nullCheck(x10::lang::IllegalArgumentException::_make(x10aux::string_utils::lit("abs(x) > 2*PI: Wigner rotation matrix is only defined on [0..2*PI]."))));
    }
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10_double> > D =
      
    x10aux::ref<x10::array::Array<x10_double> >((new (memset(x10aux::alloc<x10::array::Array<x10_double> >(), 0, sizeof(x10::array::Array<x10_double>))) x10::array::Array<x10_double>()))
    ;
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> reg57120 =
          x10aux::nullCheck(x10::lang::IntRange::_make(((x10_int) -(l)), l))->x10::lang::IntRange::__times(
            x10::lang::IntRange::_make(((x10_int) -(l)), l));
        {
            
            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
            (D)->::x10::lang::Object::_constructor();
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            D->FMGL(region) = (reg57120);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            D->FMGL(rank) = x10aux::nullCheck(reg57120)->
                              FMGL(rank);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            D->FMGL(rect) = x10aux::nullCheck(reg57120)->
                              FMGL(rect);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            D->FMGL(zeroBased) = x10aux::nullCheck(reg57120)->
                                   FMGL(zeroBased);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            D->FMGL(rail) = x10aux::nullCheck(reg57120)->
                              FMGL(rail);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            D->FMGL(size) = x10aux::nullCheck(reg57120)->size();
            
            //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            D->FMGL(layout) = (__extension__ ({
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout alloc1995457121 =
                  
                x10::array::RectLayout::_alloc();
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc1995457121)->::x10::array::RectLayout::_constructor(
                  reg57120);
                alloc1995457121;
            }))
            ;
            
            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int n57122 = (__extension__ ({
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this57124 =
                  D->
                    FMGL(layout);
                this57124->FMGL(size);
            }))
            ;
            
            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            D->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10_double >(n57122, 8, false, true);
        }
        
    }))
    ;
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(theta, 0.0)))
    {
        
        //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
        x10_int i44102min4410357456 =
          ((x10_int) -(l));
        
        //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
        x10_int i44102max4410457457 =
          l;
        
        //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
        {
            x10_int i4410257458;
            for (
                 //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                 i4410257458 =
                   i44102min4410357456;
                 ((i4410257458) <= (i44102max4410457457));
                 
                 //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                 i4410257458 =
                   ((x10_int) ((i4410257458) + (((x10_int)1)))))
            {
                
                //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                x10_int k57459 =
                  i4410257458;
                
                //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i05712557448 =
                      k57459;
                    
                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i15712657449 =
                      k57459;
                    
                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret5712857450;
                    {
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                        (D->
                           FMGL(raw))->__set((__extension__ ({
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this5713357451 =
                              D->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i05713057452 =
                              i05712557448;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i15713157453 =
                              i15712657449;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret5713457454;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset5713257455 =
                                  ((x10_int) ((i05713057452) - (this5713357451->
                                                                  FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset5713257455 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset5713257455) * (this5713357451->
                                                                                                FMGL(delta1))))) + (i15713157453)))) - (this5713357451->
                                                                                                                                          FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret5713457454 =
                                  offset5713257455;
                            }
                            ret5713457454;
                        }))
                        , 1.0);
                        
                        //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret5712857450 =
                          1.0;
                    }
                    ret5712857450;
                }))
                ;
            }
        }
        
        //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
        return D;
        
    }
    else
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(theta,
                               3.141592653589793)))
    {
        
        //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
        x10_int i44118min4411957469 =
          ((x10_int) -(l));
        
        //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
        x10_int i44118max4412057470 =
          l;
        
        //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
        {
            x10_int i4411857471;
            for (
                 //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                 i4411857471 =
                   i44118min4411957469;
                 ((i4411857471) <= (i44118max4412057470));
                 
                 //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                 i4411857471 =
                   ((x10_int) ((i4411857471) + (((x10_int)1)))))
            {
                
                //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                x10_int k57472 =
                  i4411857471;
                
                //#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i05713657460 =
                      k57472;
                    
                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i15713757461 =
                      ((x10_int) -(k57472));
                    
                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double v5713857462 =
                      x10aux::math_utils::pow(((x10_double) (((x10_int)-1))),((x10_double) (((x10_int) ((l) + (k57472))))));
                    
                    //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret5713957463;
                    {
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                        (D->
                           FMGL(raw))->__set((__extension__ ({
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this5714457464 =
                              D->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i05714157465 =
                              i05713657460;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i15714257466 =
                              i15713757461;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret5714557467;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset5714357468 =
                                  ((x10_int) ((i05714157465) - (this5714457464->
                                                                  FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset5714357468 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset5714357468) * (this5714457464->
                                                                                                FMGL(delta1))))) + (i15714257466)))) - (this5714457464->
                                                                                                                                          FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret5714557467 =
                                  offset5714357468;
                            }
                            ret5714557467;
                        }))
                        , v5713857462);
                        
                        //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret5713957463 =
                          v5713857462;
                    }
                    ret5713957463;
                }))
                ;
            }
        }
        
        //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
        return D;
        
    }
    
    //#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_double thetaPrime = theta;
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
    if (((theta) >= (((3.141592653589793) / (2.0)))))
    {
        
        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
        if (((theta) < (3.141592653589793)))
        {
            
            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
            thetaPrime =
              ((3.141592653589793) - (theta));
        }
        else
        
        //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
        if (((theta) > (3.141592653589793)) &&
            ((theta) < (((9.42477796076938) / (2.0)))))
        {
            
            //#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
            thetaPrime =
              ((theta) - (3.141592653589793));
        }
        
    }
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_double cosTheta = x10aux::math_utils::cos(thetaPrime);
    
    //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_double sinTheta = x10aux::math_utils::sin(thetaPrime);
    
    //#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_double gk0 = 1.0;
    
    //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_int i44134max4413657590 = l;
    
    //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
    {
        x10_int i4413457591;
        for (
             //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
             i4413457591 = ((x10_int)1); ((i4413457591) <= (i44134max4413657590));
             
             //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
             i4413457591 =
               ((x10_int) ((i4413457591) + (((x10_int)1)))))
        {
            
            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int k57592 =
              i4413457591;
            
            //#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
            gk0 =
              ((x10aux::math_utils::sqrt(((((((2.0) * (((x10_double) (k57592))))) - (((x10_double) (((x10_int)1)))))) / (((2.0) * (((x10_double) (k57592)))))))) * (gk0));
        }
    }
    
    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_double gl0 = gk0;
    
    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_int i157148 = l;
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_double v57149 = ((((x10aux::math_utils::pow(-1.0,((x10_double) (l)))) * (gl0))) * (x10aux::math_utils::pow(sinTheta,((x10_double) (l)))));
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_double ret57150;
        {
            
            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
            (D->FMGL(raw))->__set((__extension__ ({
                
                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this57155 =
                  D->
                    FMGL(layout);
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                x10_int i157153 = i157148;
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                x10_int ret57156;
                {
                    
                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int offset57154 =
                      ((x10_int) ((((x10_int)0)) - (this57155->
                                                      FMGL(min0))));
                    
                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    offset57154 = ((x10_int) ((((x10_int) ((((x10_int) ((offset57154) * (this57155->
                                                                                           FMGL(delta1))))) + (i157153)))) - (this57155->
                                                                                                                                FMGL(min1))));
                    
                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    ret57156 = offset57154;
                }
                ret57156;
            }))
            , v57149);
            
            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret57150 = v57149;
        }
        ret57150;
    }))
    ;
    
    //#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_double glm = gl0;
    
    //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_double sign = x10aux::math_utils::pow(((x10_double) (((x10_int)-1))),((x10_double) (l)));
    
    //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_int i44150max4415257594 = l;
    
    //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
    {
        x10_int i4415057595;
        for (
             //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
             i4415057595 = ((x10_int)1); ((i4415057595) <= (i44150max4415257594));
             
             //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
             i4415057595 =
               ((x10_int) ((i4415057595) + (((x10_int)1)))))
        {
            
            //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int m57596 =
              i4415057595;
            
            //#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
            glm =
              ((x10aux::math_utils::sqrt(((((x10_double) (((x10_int) ((((x10_int) ((l) - (m57596)))) + (((x10_int)1))))))) / (((x10_double) (((x10_int) ((l) + (m57596))))))))) * (glm));
            
            //#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
            sign =
              ((sign) * (-1.0));
            
            //#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05715857473 =
                  m57596;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i15715957474 =
                  l;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double v5716057475 =
                  ((((((sign) * (glm))) * (x10aux::math_utils::pow(((1.0) + (cosTheta)),((x10_double) (m57596)))))) * (x10aux::math_utils::pow(sinTheta,((x10_double) (((x10_int) ((l) - (m57596))))))));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double ret5716157476;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (D->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this5716657477 =
                          D->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i05716357478 =
                          i05715857473;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i15716457479 =
                          i15715957474;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret5716757480;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset5716557481 =
                              ((x10_int) ((i05716357478) - (this5716657477->
                                                              FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset5716557481 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5716557481) * (this5716657477->
                                                                                            FMGL(delta1))))) + (i15716457479)))) - (this5716657477->
                                                                                                                                      FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret5716757480 =
                              offset5716557481;
                        }
                        ret5716757480;
                    }))
                    , v5716057475);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5716157476 =
                      v5716057475;
                }
                ret5716157476;
            }))
            ;
        }
    }
    
    //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
    {
        x10_int k;
        for (
             //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
             k = l; ((k) > (((x10_int) -(l))));
             
             //#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
             k =
               ((x10_int) ((k) - (((x10_int)1)))))
        {
            
            //#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i057179 =
                  l;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i157180 =
                  ((x10_int) ((k) - (((x10_int)1))));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double v57181 =
                  ((((((((((x10_double) (((x10_int) ((l) + (k)))))) / (x10aux::math_utils::sqrt(((((((x10_double) (l))) * (((((x10_double) (l))) + (1.0))))) - (((((x10_double) (k))) * (((((x10_double) (k))) - (1.0)))))))))) * (sinTheta))) / (((1.0) + (cosTheta))))) * ((__extension__ ({
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i057169 =
                      l;
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i157170 =
                      k;
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret57171;
                    {
                        
                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret57171 =
                          (D->
                             FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this57176 =
                              D->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i057173 =
                              i057169;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i157174 =
                              i157170;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret57177;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset57175 =
                                  ((x10_int) ((i057173) - (this57176->
                                                             FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset57175 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset57175) * (this57176->
                                                                                           FMGL(delta1))))) + (i157174)))) - (this57176->
                                                                                                                                FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret57177 =
                                  offset57175;
                            }
                            ret57177;
                        }))
                        );
                    }
                    ret57171;
                }))
                ));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double ret57182;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (D->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this57187 =
                          D->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i057184 =
                          i057179;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i157185 =
                          i157180;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret57188;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset57186 =
                              ((x10_int) ((i057184) - (this57187->
                                                         FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset57186 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset57186) * (this57187->
                                                                                       FMGL(delta1))))) + (i157185)))) - (this57187->
                                                                                                                            FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret57188 =
                              offset57186;
                        }
                        ret57188;
                    }))
                    , v57181);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret57182 =
                      v57181;
                }
                ret57182;
            }))
            ;
        }
    }
    
    //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
    {
        x10_int m;
        for (
             //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
             m = ((x10_int) ((l) - (((x10_int)1))));
             ((m) >= (((x10_int)0)));
             
             //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
             m =
               ((x10_int) ((m) - (((x10_int)1)))))
        {
            
            //#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
            {
                x10_int k;
                for (
                     //#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                     k =
                       l;
                     ((k) > (((x10_int) -(l))));
                     
                     //#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                     k =
                       ((x10_int) ((k) - (((x10_int)1)))))
                {
                    
                    //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i057210 =
                          m;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i157211 =
                          ((x10_int) ((k) - (((x10_int)1))));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double v57212 =
                          ((((x10aux::math_utils::sqrt(((((x10_double) (((x10_int) ((((x10_int) ((l) * (((x10_int) ((l) + (((x10_int)1)))))))) - (((x10_int) ((m) * (((x10_int) ((m) + (((x10_int)1))))))))))))) / (((x10_double) (((x10_int) ((((x10_int) ((l) * (((x10_int) ((l) + (((x10_int)1)))))))) - (((x10_int) ((k) * (((x10_int) ((k) - (((x10_int)1)))))))))))))))) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i057190 =
                              ((x10_int) ((m) + (((x10_int)1))));
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i157191 =
                              k;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret57192;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret57192 =
                                  (D->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this57197 =
                                      D->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i057194 =
                                      i057190;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i157195 =
                                      i157191;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret57198;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset57196 =
                                          ((x10_int) ((i057194) - (this57197->
                                                                     FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset57196 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset57196) * (this57197->
                                                                                                   FMGL(delta1))))) + (i157195)))) - (this57197->
                                                                                                                                        FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret57198 =
                                          offset57196;
                                    }
                                    ret57198;
                                }))
                                );
                            }
                            ret57192;
                        }))
                        ))) + (((((((((((x10_double) (((x10_int) ((m) + (k)))))) / (x10aux::math_utils::sqrt(((x10_double) (((x10_int) ((((x10_int) ((l) * (((x10_int) ((l) + (((x10_int)1)))))))) - (((x10_int) ((k) * (((x10_int) ((k) - (((x10_int)1)))))))))))))))) * (sinTheta))) / (((1.0) + (cosTheta))))) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i057200 =
                              m;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i157201 =
                              k;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret57202;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret57202 =
                                  (D->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this57207 =
                                      D->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i057204 =
                                      i057200;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i157205 =
                                      i157201;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret57208;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset57206 =
                                          ((x10_int) ((i057204) - (this57207->
                                                                     FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset57206 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset57206) * (this57207->
                                                                                                   FMGL(delta1))))) + (i157205)))) - (this57207->
                                                                                                                                        FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret57208 =
                                          offset57206;
                                    }
                                    ret57208;
                                }))
                                );
                            }
                            ret57202;
                        }))
                        ))));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double ret57213;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (D->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this57218 =
                                  D->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i057215 =
                                  i057210;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i157216 =
                                  i157211;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret57219;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset57217 =
                                      ((x10_int) ((i057215) - (this57218->
                                                                 FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset57217 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset57217) * (this57218->
                                                                                               FMGL(delta1))))) + (i157216)))) - (this57218->
                                                                                                                                    FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret57219 =
                                      offset57217;
                                }
                                ret57219;
                            }))
                            , v57212);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret57213 =
                              v57212;
                        }
                        ret57213;
                    }))
                    ;
                }
            }
            
        }
    }
    
    //#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_int i44182min4418357597 = ((x10_int) -(l));
    
    //#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
    {
        x10_int i4418257599;
        for (
             //#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
             i4418257599 = i44182min4418357597;
             ((i4418257599) <= (((x10_int)-1)));
             
             //#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
             i4418257599 =
               ((x10_int) ((i4418257599) + (((x10_int)1)))))
        {
            
            //#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int m57600 =
              i4418257599;
            
            //#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
            sign =
              x10aux::math_utils::pow(((x10_double) (((x10_int)-1))),((x10_double) (((x10_int) ((m57600) - (l))))));
            
            //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int i44166min4416757499 =
              ((x10_int) -(l));
            
            //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int i44166max4416857500 =
              l;
            
            //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
            {
                x10_int i4416657501;
                for (
                     //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                     i4416657501 =
                       i44166min4416757499;
                     ((i4416657501) <= (i44166max4416857500));
                     
                     //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                     i4416657501 =
                       ((x10_int) ((i4416657501) + (((x10_int)1)))))
                {
                    
                    //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10_int k57502 =
                      i4416657501;
                    
                    //#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05723157482 =
                          m57600;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15723257483 =
                          k57502;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double v5723357484 =
                          ((sign) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05722157485 =
                              ((x10_int) -(m57600));
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15722257486 =
                              ((x10_int) -(k57502));
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret5722357487;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5722357487 =
                                  (D->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5722857488 =
                                      D->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05722557489 =
                                      i05722157485;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15722657490 =
                                      i15722257486;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5722957491;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5722757492 =
                                          ((x10_int) ((i05722557489) - (this5722857488->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5722757492 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5722757492) * (this5722857488->
                                                                                                        FMGL(delta1))))) + (i15722657490)))) - (this5722857488->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5722957491 =
                                          offset5722757492;
                                    }
                                    ret5722957491;
                                }))
                                );
                            }
                            ret5722357487;
                        }))
                        ));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double ret5723457493;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (D->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5723957494 =
                                  D->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05723657495 =
                                  i05723157482;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15723757496 =
                                  i15723257483;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5724057497;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5723857498 =
                                      ((x10_int) ((i05723657495) - (this5723957494->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5723857498 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5723857498) * (this5723957494->
                                                                                                    FMGL(delta1))))) + (i15723757496)))) - (this5723957494->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5724057497 =
                                      offset5723857498;
                                }
                                ret5724057497;
                            }))
                            , v5723357484);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5723457493 =
                              v5723357484;
                        }
                        ret5723457493;
                    }))
                    ;
                    
                    //#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                    sign =
                      ((sign) * (((x10_double) (((x10_int)-1)))));
                }
            }
            
        }
    }
    
    //#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
    if (((theta) >= (((3.141592653589793) / (2.0)))))
    {
        
        //#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
        if (((theta) < (3.141592653589793)))
        {
            
            //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
            sign =
              -1.0;
            
            //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int i44214min4421557542 =
              ((x10_int) -(l));
            
            //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int i44214max4421657543 =
              l;
            
            //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
            {
                x10_int i4421457544;
                for (
                     //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                     i4421457544 =
                       i44214min4421557542;
                     ((i4421457544) <= (i44214max4421657543));
                     
                     //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                     i4421457544 =
                       ((x10_int) ((i4421457544) + (((x10_int)1)))))
                {
                    
                    //#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10_int m57545 =
                      i4421457544;
                    
                    //#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                    sign =
                      ((sign) * (-1.0));
                    
                    //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10_int i44198min4419957538 =
                      ((x10_int) -(l));
                    
                    //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
                    {
                        x10_int i4419857540;
                        for (
                             //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                             i4419857540 =
                               i44198min4419957538;
                             ((i4419857540) <= (((x10_int)0)));
                             
                             //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                             i4419857540 =
                               ((x10_int) ((i4419857540) + (((x10_int)1)))))
                        {
                            
                            //#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                            x10_int k57541 =
                              i4419857540;
                            
                            //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                            x10_double tmp57503 =
                              (__extension__ ({
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05724257504 =
                                  m57545;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15724357505 =
                                  k57541;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double ret5724457506;
                                {
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5724457506 =
                                      (D->
                                         FMGL(raw))->__apply((__extension__ ({
                                        
                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5724957507 =
                                          D->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05724657508 =
                                          i05724257504;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15724757509 =
                                          i15724357505;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5725057510;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5724857511 =
                                              ((x10_int) ((i05724657508) - (this5724957507->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5724857511 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5724857511) * (this5724957507->
                                                                                                            FMGL(delta1))))) + (i15724757509)))) - (this5724957507->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5725057510 =
                                              offset5724857511;
                                        }
                                        ret5725057510;
                                    }))
                                    );
                                }
                                ret5724457506;
                            }))
                            ;
                            
                            //#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                            (__extension__ ({
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05726257512 =
                                  m57545;
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15726357513 =
                                  k57541;
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double v5726457514 =
                                  ((sign) * ((__extension__ ({
                                    
                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05725257515 =
                                      m57545;
                                    
                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15725357516 =
                                      ((x10_int) -(k57541));
                                    
                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_double ret5725457517;
                                    {
                                        
                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret5725457517 =
                                          (D->
                                             FMGL(raw))->__apply((__extension__ ({
                                            
                                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10::array::RectLayout this5725957518 =
                                              D->
                                                FMGL(layout);
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05725657519 =
                                              i05725257515;
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i15725757520 =
                                              i15725357516;
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int ret5726057521;
                                            {
                                                
                                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int offset5725857522 =
                                                  ((x10_int) ((i05725657519) - (this5725957518->
                                                                                  FMGL(min0))));
                                                
                                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                offset5725857522 =
                                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset5725857522) * (this5725957518->
                                                                                                                FMGL(delta1))))) + (i15725757520)))) - (this5725957518->
                                                                                                                                                          FMGL(min1))));
                                                
                                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                ret5726057521 =
                                                  offset5725857522;
                                            }
                                            ret5726057521;
                                        }))
                                        );
                                    }
                                    ret5725457517;
                                }))
                                ));
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double ret5726557523;
                                {
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                    (D->
                                       FMGL(raw))->__set((__extension__ ({
                                        
                                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5727057524 =
                                          D->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05726757525 =
                                          i05726257512;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15726857526 =
                                          i15726357513;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5727157527;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5726957528 =
                                              ((x10_int) ((i05726757525) - (this5727057524->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5726957528 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5726957528) * (this5727057524->
                                                                                                            FMGL(delta1))))) + (i15726857526)))) - (this5727057524->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5727157527 =
                                              offset5726957528;
                                        }
                                        ret5727157527;
                                    }))
                                    , v5726457514);
                                    
                                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5726557523 =
                                      v5726457514;
                                }
                                ret5726557523;
                            }))
                            ;
                            
                            //#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                            (__extension__ ({
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05727357529 =
                                  m57545;
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15727457530 =
                                  ((x10_int) -(k57541));
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double v5727557531 =
                                  ((sign) * (tmp57503));
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double ret5727657532;
                                {
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                    (D->
                                       FMGL(raw))->__set((__extension__ ({
                                        
                                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5728157533 =
                                          D->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05727857534 =
                                          i05727357529;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15727957535 =
                                          i15727457530;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5728257536;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5728057537 =
                                              ((x10_int) ((i05727857534) - (this5728157533->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5728057537 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5728057537) * (this5728157533->
                                                                                                            FMGL(delta1))))) + (i15727957535)))) - (this5728157533->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5728257536 =
                                              offset5728057537;
                                        }
                                        ret5728257536;
                                    }))
                                    , v5727557531);
                                    
                                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5727657532 =
                                      v5727557531;
                                }
                                ret5727657532;
                            }))
                            ;
                        }
                    }
                    
                }
            }
            
        }
        else
        
        //#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
        if (((theta) > (3.141592653589793)) &&
            ((theta) < (((9.42477796076938) / (2.0)))))
        {
            
            //#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
            sign =
              -1.0;
            
            //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int i44246min4424757585 =
              ((x10_int) -(l));
            
            //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
            {
                x10_int i4424657587;
                for (
                     //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                     i4424657587 =
                       i44246min4424757585;
                     ((i4424657587) <= (((x10_int)0)));
                     
                     //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                     i4424657587 =
                       ((x10_int) ((i4424657587) + (((x10_int)1)))))
                {
                    
                    //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10_int m57588 =
                      i4424657587;
                    
                    //#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                    sign =
                      ((sign) * (-1.0));
                    
                    //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10_int i44230min4423157581 =
                      ((x10_int) -(l));
                    
                    //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10_int i44230max4423257582 =
                      l;
                    
                    //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
                    {
                        x10_int i4423057583;
                        for (
                             //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                             i4423057583 =
                               i44230min4423157581;
                             ((i4423057583) <= (i44230max4423257582));
                             
                             //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                             i4423057583 =
                               ((x10_int) ((i4423057583) + (((x10_int)1)))))
                        {
                            
                            //#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                            x10_int k57584 =
                              i4423057583;
                            
                            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                            x10_double tmp57546 =
                              (__extension__ ({
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05728457547 =
                                  m57588;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15728557548 =
                                  k57584;
                                
                                //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double ret5728657549;
                                {
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5728657549 =
                                      (D->
                                         FMGL(raw))->__apply((__extension__ ({
                                        
                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5729157550 =
                                          D->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05728857551 =
                                          i05728457547;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15728957552 =
                                          i15728557548;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5729257553;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5729057554 =
                                              ((x10_int) ((i05728857551) - (this5729157550->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5729057554 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5729057554) * (this5729157550->
                                                                                                            FMGL(delta1))))) + (i15728957552)))) - (this5729157550->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5729257553 =
                                              offset5729057554;
                                        }
                                        ret5729257553;
                                    }))
                                    );
                                }
                                ret5728657549;
                            }))
                            ;
                            
                            //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                            (__extension__ ({
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05730457555 =
                                  m57588;
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15730557556 =
                                  k57584;
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double v5730657557 =
                                  ((sign) * ((__extension__ ({
                                    
                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05729457558 =
                                      ((x10_int) -(m57588));
                                    
                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15729557559 =
                                      k57584;
                                    
                                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_double ret5729657560;
                                    {
                                        
                                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret5729657560 =
                                          (D->
                                             FMGL(raw))->__apply((__extension__ ({
                                            
                                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10::array::RectLayout this5730157561 =
                                              D->
                                                FMGL(layout);
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05729857562 =
                                              i05729457558;
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int i15729957563 =
                                              i15729557559;
                                            
                                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int ret5730257564;
                                            {
                                                
                                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int offset5730057565 =
                                                  ((x10_int) ((i05729857562) - (this5730157561->
                                                                                  FMGL(min0))));
                                                
                                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                offset5730057565 =
                                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset5730057565) * (this5730157561->
                                                                                                                FMGL(delta1))))) + (i15729957563)))) - (this5730157561->
                                                                                                                                                          FMGL(min1))));
                                                
                                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                ret5730257564 =
                                                  offset5730057565;
                                            }
                                            ret5730257564;
                                        }))
                                        );
                                    }
                                    ret5729657560;
                                }))
                                ));
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double ret5730757566;
                                {
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                    (D->
                                       FMGL(raw))->__set((__extension__ ({
                                        
                                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5731257567 =
                                          D->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05730957568 =
                                          i05730457555;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15731057569 =
                                          i15730557556;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5731357570;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5731157571 =
                                              ((x10_int) ((i05730957568) - (this5731257567->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5731157571 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5731157571) * (this5731257567->
                                                                                                            FMGL(delta1))))) + (i15731057569)))) - (this5731257567->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5731357570 =
                                              offset5731157571;
                                        }
                                        ret5731357570;
                                    }))
                                    , v5730657557);
                                    
                                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5730757566 =
                                      v5730657557;
                                }
                                ret5730757566;
                            }))
                            ;
                            
                            //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                            (__extension__ ({
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i05731557572 =
                                  ((x10_int) -(m57588));
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_int i15731657573 =
                                  k57584;
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double v5731757574 =
                                  ((sign) * (tmp57546));
                                
                                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10_double ret5731857575;
                                {
                                    
                                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                    (D->
                                       FMGL(raw))->__set((__extension__ ({
                                        
                                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10::array::RectLayout this5732357576 =
                                          D->
                                            FMGL(layout);
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05732057577 =
                                          i05731557572;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int i15732157578 =
                                          i15731657573;
                                        
                                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5732457579;
                                        {
                                            
                                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int offset5732257580 =
                                              ((x10_int) ((i05732057577) - (this5732357576->
                                                                              FMGL(min0))));
                                            
                                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            offset5732257580 =
                                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5732257580) * (this5732357576->
                                                                                                            FMGL(delta1))))) + (i15732157578)))) - (this5732357576->
                                                                                                                                                      FMGL(min1))));
                                            
                                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                            ret5732457579 =
                                              offset5732257580;
                                        }
                                        ret5732457579;
                                    }))
                                    , v5731757574);
                                    
                                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                    ret5731857575 =
                                      v5731757574;
                                }
                                ret5731857575;
                            }))
                            ;
                        }
                    }
                    
                }
            }
            
        }
        
    }
    
    //#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
    return D;
    
}

//#line 147 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
  au::edu::anu::mm::WignerRotationMatrix::getCollection(
  x10_double theta,
  x10_int numTerms) {
    
    //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > collection =
      
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >(), 0, sizeof(x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > >))) x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > >()))
    ;
    
    //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        {
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
            (collection)->::x10::lang::Object::_constructor();
            
            //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> myReg57327 =
              x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::RectRegion1D> alloc1996057328 =
                  
                x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                ;
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc1996057328)->::x10::array::RectRegion1D::_constructor(
                  ((x10_int)0),
                  ((x10_int) ((((x10_int)2)) - (((x10_int)1)))));
                alloc1996057328;
            }))
            );
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            collection->FMGL(region) = myReg57327;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            collection->FMGL(rank) = ((x10_int)1);
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            collection->FMGL(rect) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            collection->FMGL(zeroBased) =
              true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            collection->FMGL(rail) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            collection->FMGL(size) = ((x10_int)2);
            
            //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            collection->FMGL(layout) = (__extension__ ({
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout alloc1996157329 =
                  
                x10::array::RectLayout::_alloc();
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int _max057333 = ((x10_int) ((((x10_int)2)) - (((x10_int)1))));
                    {
                        
                        //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(rank) = ((x10_int)1);
                        
                        //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(min0) = ((x10_int)0);
                        
                        //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(delta0) = ((x10_int) ((((x10_int) ((_max057333) - (((x10_int)0))))) + (((x10_int)1))));
                        
                        //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(size) = ((alloc1996157329->
                                           FMGL(delta0)) > (((x10_int)0)))
                          ? (alloc1996157329->
                               FMGL(delta0))
                          : (((x10_int)0));
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(min1) = ((x10_int)0);
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(delta1) = ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(min2) = ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(delta2) = ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(min3) = ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(delta3) = ((x10_int)0);
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(min) = X10_NULL;
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996157329->
                          FMGL(delta) = X10_NULL;
                    }
                    
                }))
                ;
                alloc1996157329;
            }))
            ;
            
            //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int n57330 = (__extension__ ({
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this57335 =
                  collection->
                    FMGL(layout);
                this57335->FMGL(size);
            }))
            ;
            
            //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            collection->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > >(n57330, 8, false, true);
        }
        
    }))
    ;
    
    //#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
    {
        x10_int i4427857621;
        for (
             //#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
             i4427857621 = ((x10_int)0); ((i4427857621) <= (((x10_int)1)));
             
             //#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
             i4427857621 =
               ((x10_int) ((i4427857621) + (((x10_int)1)))))
        {
            
            //#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int r57622 =
              i4427857621;
            
            //#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > R57608 =
              
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > >(), 0, sizeof(x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > >))) x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > >()))
            ;
            
            //#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int size5733657609 =
                  ((x10_int) ((numTerms) + (((x10_int)1))));
                {
                    
                    //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (R57608)->::x10::lang::Object::_constructor();
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Region> myReg5733757610 =
                      x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                        
                        //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::RectRegion1D> alloc199605733857611 =
                          
                        x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                        ;
                        
                        //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                        (alloc199605733857611)->::x10::array::RectRegion1D::_constructor(
                          ((x10_int)0),
                          ((x10_int) ((size5733657609) - (((x10_int)1)))));
                        alloc199605733857611;
                    }))
                    );
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    R57608->
                      FMGL(region) =
                      myReg5733757610;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    R57608->
                      FMGL(rank) =
                      ((x10_int)1);
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    R57608->
                      FMGL(rect) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    R57608->
                      FMGL(zeroBased) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    R57608->
                      FMGL(rail) =
                      true;
                    
                    //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    R57608->
                      FMGL(size) =
                      size5733657609;
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    R57608->
                      FMGL(layout) =
                      (__extension__ ({
                        
                        //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout alloc199615733957612 =
                          
                        x10::array::RectLayout::_alloc();
                        
                        //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                        (__extension__ ({
                            
                            //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int _max05734357613 =
                              ((x10_int) ((size5733657609) - (((x10_int)1))));
                            {
                                
                                //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(rank) =
                                  ((x10_int)1);
                                
                                //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(min0) =
                                  ((x10_int)0);
                                
                                //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(delta0) =
                                  ((x10_int) ((((x10_int) ((_max05734357613) - (((x10_int)0))))) + (((x10_int)1))));
                                
                                //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(size) =
                                  ((alloc199615733957612->
                                      FMGL(delta0)) > (((x10_int)0)))
                                  ? (alloc199615733957612->
                                       FMGL(delta0))
                                  : (((x10_int)0));
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(min1) =
                                  ((x10_int)0);
                                
                                //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(delta1) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(min2) =
                                  ((x10_int)0);
                                
                                //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(delta2) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(min3) =
                                  ((x10_int)0);
                                
                                //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(delta3) =
                                  ((x10_int)0);
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(min) =
                                  X10_NULL;
                                
                                //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                alloc199615733957612->
                                  FMGL(delta) =
                                  X10_NULL;
                            }
                            
                        }))
                        ;
                        alloc199615733957612;
                    }))
                    ;
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int n5734057614 =
                      (__extension__ ({
                        
                        //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this5734557615 =
                          R57608->
                            FMGL(layout);
                        this5734557615->
                          FMGL(size);
                    }))
                    ;
                    
                    //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                    R57608->
                      FMGL(raw) =
                      x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<x10::array::Array<x10_double> > >(n5734057614, 8, false, true);
                }
                
            }))
            ;
            
            //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int i44262max4426457605 =
              numTerms;
            
            //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
            {
                x10_int i4426257606;
                for (
                     //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                     i4426257606 =
                       ((x10_int)0);
                     ((i4426257606) <= (i44262max4426457605));
                     
                     //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                     i4426257606 =
                       ((x10_int) ((i4426257606) + (((x10_int)1)))))
                {
                    
                    //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10_int l57607 =
                      i4426257606;
                    
                    //#line 152 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05734657601 =
                          l57607;
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10_double> > v5734757602 =
                          au::edu::anu::mm::WignerRotationMatrix::getDmk(
                            (x10aux::struct_equals(r57622,
                                                   ((x10_int)0)))
                              ? (theta)
                              : (((((((x10_double) (((x10_int)2)))) * (3.141592653589793))) - (theta))),
                            l57607);
                        
                        //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10_double> > ret5734857603;
                        {
                            
                            //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (R57608->
                               FMGL(raw))->__set(i05734657601, v5734757602);
                            
                            //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5734857603 =
                              v5734757602;
                        }
                        ret5734857603;
                    }))
                    ;
                }
            }
            
            //#line 154 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05735557616 =
                  r57622;
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > v5735657617 =
                  R57608;
                
                //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret5735757618;
                {
                    
                    //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (collection->
                       FMGL(raw))->__set(i05735557616, v5735657617);
                    
                    //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5735757618 =
                      v5735657617;
                }
                ret5735757618;
            }))
            ;
        }
    }
    
    //#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
    return collection;
    
}

//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
  au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
  x10_double theta,
  x10_int numTerms,
  x10_int op) {
    
    //#line 165 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > collection =
      au::edu::anu::mm::WignerRotationMatrix::getCollection(
        theta,
        numTerms);
    
    //#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_double F_mk;
    
    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Region> p4433057677 =
      x10::lang::IntRange::_make(((x10_int)0), ((x10_int)1))->x10::lang::IntRange::__times(
        x10::lang::IntRange::_make(((x10_int)0), numTerms));
    
    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_int l44331min4433257678 = p4433057677->min(
                                    ((x10_int)1));
    
    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_int l44331max4433357679 = p4433057677->max(
                                    ((x10_int)1));
    
    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_int rev44348min4434957680 = p4433057677->min(
                                      ((x10_int)0));
    
    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
    x10_int rev44348max4435057681 = p4433057677->max(
                                      ((x10_int)0));
    
    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
    {
        x10_int rev4434857682;
        for (
             //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
             rev4434857682 = rev44348min4434957680;
             ((rev4434857682) <= (rev44348max4435057681));
             
             //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
             rev4434857682 =
               ((x10_int) ((rev4434857682) + (((x10_int)1)))))
        {
            
            //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
            x10_int rev57683 =
              rev4434857682;
            
            //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
            {
                x10_int l4433157684;
                for (
                     //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                     l4433157684 =
                       l44331min4433257678;
                     ((l4433157684) <= (l44331max4433357679));
                     
                     //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                     l4433157684 =
                       ((x10_int) ((l4433157684) + (((x10_int)1)))))
                {
                    
                    //#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10_int l57685 =
                      l4433157684;
                    
                    //#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Array<x10_double> > R57669 =
                      (__extension__ ({
                        
                        //#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > this5737357670 =
                          (__extension__ ({
                            
                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05736457671 =
                              rev57683;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > ret5736557672;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                            goto __ret5736657673; __ret5736657673: {
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5736557672 =
                                  (x10aux::nullCheck(collection)->
                                     FMGL(raw))->__apply(i05736457671);
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret5736657673_end_;
                            }goto __ret5736657673_end_; __ret5736657673_end_: ;
                            }
                            ret5736557672;
                            }))
                            ;
                            
                        
                        //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05737257674 =
                          l57685;
                        
                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Array<x10_double> > ret5737457675;
                        
                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                        goto __ret5737557676; __ret5737557676: {
                        {
                            
                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5737457675 =
                              (x10aux::nullCheck(this5737357670)->
                                 FMGL(raw))->__apply(i05737257674);
                            
                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                            goto __ret5737557676_end_;
                        }goto __ret5737557676_end_; __ret5737557676_end_: ;
                        }
                        ret5737457675;
                        }))
                        ;
                        
                        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Region> p4429457660 =
                          x10aux::nullCheck(R57669)->
                            FMGL(region);
                        
                        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                        x10_int k44295min4429657661 =
                          p4429457660->min(
                            ((x10_int)1));
                        
                        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                        x10_int k44295max4429757662 =
                          p4429457660->max(
                            ((x10_int)1));
                        
                        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                        x10_int m44312min4431357663 =
                          p4429457660->min(
                            ((x10_int)0));
                        
                        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                        x10_int m44312max4431457664 =
                          p4429457660->max(
                            ((x10_int)0));
                        
                        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
                        {
                            x10_int m4431257665;
                            for (
                                 //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                                 m4431257665 =
                                   m44312min4431357663;
                                 ((m4431257665) <= (m44312max4431457664));
                                 
                                 //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                                 m4431257665 =
                                   ((x10_int) ((m4431257665) + (((x10_int)1)))))
                            {
                                
                                //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                                x10_int m57666 =
                                  m4431257665;
                                
                                //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": polyglot.ast.For_c
                                {
                                    x10_int k4429557667;
                                    for (
                                         //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                                         k4429557667 =
                                           k44295min4429657661;
                                         ((k4429557667) <= (k44295max4429757662));
                                         
                                         //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                                         k4429557667 =
                                           ((x10_int) ((k4429557667) + (((x10_int)1)))))
                                    {
                                        
                                        //#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                                        x10_int k57668 =
                                          k4429557667;
                                        
                                        //#line 171 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                                        F_mk =
                                          x10aux::math_utils::sqrt((((((__extension__ ({
                                            
                                            //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                            x10_int i5738157623 =
                                              ((x10_int) ((l57685) - (k57668)));
                                            (__extension__ ({
                                                
                                                //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                                x10aux::ref<x10::array::Array<x10_double> > this5738357624 =
                                                  au::edu::anu::mm::Factorial::
                                                    FMGL(factorial__get)();
                                                
                                                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int i05738257625 =
                                                  i5738157623;
                                                
                                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_double ret5738457626;
                                                
                                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                goto __ret5738557627; __ret5738557627: {
                                                {
                                                    
                                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                    ret5738457626 =
                                                      (x10aux::nullCheck(this5738357624)->
                                                         FMGL(raw))->__apply(i05738257625);
                                                    
                                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                    goto __ret5738557627_end_;
                                                }goto __ret5738557627_end_; __ret5738557627_end_: ;
                                                }
                                                ret5738457626;
                                                }))
                                                ;
                                            }))
                                            ) * ((__extension__ ({
                                                
                                                //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                                x10_int i5739157628 =
                                                  ((x10_int) ((l57685) + (k57668)));
                                                (__extension__ ({
                                                    
                                                    //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                                    x10aux::ref<x10::array::Array<x10_double> > this5739357629 =
                                                      au::edu::anu::mm::Factorial::
                                                        FMGL(factorial__get)();
                                                    
                                                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10_int i05739257630 =
                                                      i5739157628;
                                                    
                                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10_double ret5739457631;
                                                    
                                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                    goto __ret5739557632; __ret5739557632: {
                                                    {
                                                        
                                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                        ret5739457631 =
                                                          (x10aux::nullCheck(this5739357629)->
                                                             FMGL(raw))->__apply(i05739257630);
                                                        
                                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                        goto __ret5739557632_end_;
                                                    }goto __ret5739557632_end_; __ret5739557632_end_: ;
                                                    }
                                                    ret5739457631;
                                                    }))
                                                    ;
                                                }))
                                                ))) / ((((__extension__ ({
                                                    
                                                    //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                                    x10_int i5740157633 =
                                                      ((x10_int) ((l57685) - (m57666)));
                                                    (__extension__ ({
                                                        
                                                        //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                                        x10aux::ref<x10::array::Array<x10_double> > this5740357634 =
                                                          au::edu::anu::mm::Factorial::
                                                            FMGL(factorial__get)();
                                                        
                                                        //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i05740257635 =
                                                          i5740157633;
                                                        
                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_double ret5740457636;
                                                        
                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                        goto __ret5740557637; __ret5740557637: {
                                                        {
                                                            
                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                            ret5740457636 =
                                                              (x10aux::nullCheck(this5740357634)->
                                                                 FMGL(raw))->__apply(i05740257635);
                                                            
                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                            goto __ret5740557637_end_;
                                                        }goto __ret5740557637_end_; __ret5740557637_end_: ;
                                                        }
                                                        ret5740457636;
                                                        }))
                                                        ;
                                                    }))
                                                    ) * ((__extension__ ({
                                                        
                                                        //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i5741157638 =
                                                          ((x10_int) ((l57685) + (m57666)));
                                                        (__extension__ ({
                                                            
                                                            //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
                                                            x10aux::ref<x10::array::Array<x10_double> > this5741357639 =
                                                              au::edu::anu::mm::Factorial::
                                                                FMGL(factorial__get)();
                                                            
                                                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i05741257640 =
                                                              i5741157638;
                                                            
                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_double ret5741457641;
                                                            
                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                            goto __ret5741557642; __ret5741557642: {
                                                            {
                                                                
                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                ret5741457641 =
                                                                  (x10aux::nullCheck(this5741357639)->
                                                                     FMGL(raw))->__apply(i05741257640);
                                                                
                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                goto __ret5741557642_end_;
                                                            }goto __ret5741557642_end_; __ret5741557642_end_: ;
                                                            }
                                                            ret5741457641;
                                                            }))
                                                            ;
                                                        }))
                                                        )))));
                                                    
                                                    //#line 173 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10If_c
                                                    if ((x10aux::struct_equals(op,
                                                                               ((x10_int)2))) ||
                                                        (x10aux::struct_equals(rev57683,
                                                                               ((x10_int)1))) &&
                                                        (x10aux::struct_equals(op,
                                                                               ((x10_int)-1))))
                                                    {
                                                        
                                                        //#line 173 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalAssign_c
                                                        F_mk =
                                                          ((((x10_double) (((x10_int)1)))) / (F_mk));
                                                    }
                                                    
                                                    //#line 175 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                                                    (__extension__ ({
                                                        
                                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i05743157643 =
                                                          m57666;
                                                        
                                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_int i15743257644 =
                                                          k57668;
                                                        
                                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_double v5743357645 =
                                                          (((__extension__ ({
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i05742157646 =
                                                              m57666;
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_int i15742257647 =
                                                              k57668;
                                                            
                                                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                            x10_double ret5742357648;
                                                            {
                                                                
                                                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                ret5742357648 =
                                                                  (x10aux::nullCheck(R57669)->
                                                                     FMGL(raw))->__apply((__extension__ ({
                                                                    
                                                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                    x10::array::RectLayout this5742857649 =
                                                                      x10aux::nullCheck(R57669)->
                                                                        FMGL(layout);
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int i05742557650 =
                                                                      i05742157646;
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int i15742657651 =
                                                                      i15742257647;
                                                                    
                                                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int ret5742957652;
                                                                    {
                                                                        
                                                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                        x10_int offset5742757653 =
                                                                          ((x10_int) ((i05742557650) - (this5742857649->
                                                                                                          FMGL(min0))));
                                                                        
                                                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                        offset5742757653 =
                                                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5742757653) * (this5742857649->
                                                                                                                                        FMGL(delta1))))) + (i15742657651)))) - (this5742857649->
                                                                                                                                                                                  FMGL(min1))));
                                                                        
                                                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                        ret5742957652 =
                                                                          offset5742757653;
                                                                    }
                                                                    ret5742957652;
                                                                }))
                                                                );
                                                            }
                                                            ret5742357648;
                                                        }))
                                                        ) * (F_mk));
                                                        
                                                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_double ret5743457654;
                                                        {
                                                            
                                                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                                            (x10aux::nullCheck(R57669)->
                                                               FMGL(raw))->__set((__extension__ ({
                                                                
                                                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                x10::array::RectLayout this5743957655 =
                                                                  x10aux::nullCheck(R57669)->
                                                                    FMGL(layout);
                                                                
                                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                x10_int i05743657656 =
                                                                  i05743157643;
                                                                
                                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                x10_int i15743757657 =
                                                                  i15743257644;
                                                                
                                                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                x10_int ret5744057658;
                                                                {
                                                                    
                                                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                    x10_int offset5743857659 =
                                                                      ((x10_int) ((i05743657656) - (this5743957655->
                                                                                                      FMGL(min0))));
                                                                    
                                                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                    offset5743857659 =
                                                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5743857659) * (this5743957655->
                                                                                                                                    FMGL(delta1))))) + (i15743757657)))) - (this5743957655->
                                                                                                                                                                              FMGL(min1))));
                                                                    
                                                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                                    ret5744057658 =
                                                                      offset5743857659;
                                                                }
                                                                ret5744057658;
                                                            }))
                                                            , v5743357645);
                                                            
                                                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                            ret5743457654 =
                                                              v5743357645;
                                                        }
                                                        ret5743457654;
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
                    
                    //#line 178 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
                    return collection;
                    
                }
                
            
            //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
              au::edu::anu::mm::WignerRotationMatrix::getACollection(
              x10_double theta,
              x10_int numTerms) {
                
                //#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
                return au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
                         theta,
                         numTerms,
                         ((x10_int)0));
                
            }
            
            //#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
              au::edu::anu::mm::WignerRotationMatrix::getBCollection(
              x10_double theta,
              x10_int numTerms) {
                
                //#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
                return au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
                         theta,
                         numTerms,
                         ((x10_int)-1));
                
            }
            
            //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
              au::edu::anu::mm::WignerRotationMatrix::getCCollection(
              x10_double theta,
              x10_int numTerms) {
                
                //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
                return au::edu::anu::mm::WignerRotationMatrix::getExpandedCollection(
                         theta,
                         numTerms,
                         ((x10_int)2));
                
            }
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
            x10aux::ref<au::edu::anu::mm::WignerRotationMatrix>
              au::edu::anu::mm::WignerRotationMatrix::au__edu__anu__mm__WignerRotationMatrix____au__edu__anu__mm__WignerRotationMatrix__this(
              ) {
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10Return_c
                return ((x10aux::ref<au::edu::anu::mm::WignerRotationMatrix>)this);
                
            }
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10ConstructorDecl_c
            void au::edu::anu::mm::WignerRotationMatrix::_constructor(
              )
            {
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10ConstructorCall_c
                (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.AssignPropertyCall_c
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> this5744257686 =
                      ((x10aux::ref<au::edu::anu::mm::WignerRotationMatrix>)this);
                    {
                        
                        //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(this5744257686)->
                          FMGL(X10__object_lock_id0) =
                          ((x10_int)-1);
                    }
                    
                }))
                ;
                
            }
            x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> au::edu::anu::mm::WignerRotationMatrix::_make(
              )
            {
                x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::WignerRotationMatrix>(), 0, sizeof(au::edu::anu::mm::WignerRotationMatrix))) au::edu::anu::mm::WignerRotationMatrix();
                this_->_constructor();
                return this_;
            }
            
            
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10ConstructorDecl_c
            void au::edu::anu::mm::WignerRotationMatrix::_constructor(
              x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
            {
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10ConstructorCall_c
                (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.AssignPropertyCall_c
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> this5744557687 =
                      ((x10aux::ref<au::edu::anu::mm::WignerRotationMatrix>)this);
                    {
                        
                        //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldAssign_c
                        x10aux::nullCheck(this5744557687)->
                          FMGL(X10__object_lock_id0) =
                          ((x10_int)-1);
                    }
                    
                }))
                ;
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldAssign_c
                ((x10aux::ref<au::edu::anu::mm::WignerRotationMatrix>)this)->
                  FMGL(X10__object_lock_id0) =
                  x10aux::nullCheck(paramLock)->getIndex();
                
            }
            x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> au::edu::anu::mm::WignerRotationMatrix::_make(
              x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
            {
                x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::WignerRotationMatrix>(), 0, sizeof(au::edu::anu::mm::WignerRotationMatrix))) au::edu::anu::mm::WignerRotationMatrix();
                this_->_constructor(paramLock);
                return this_;
            }
            
            
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10MethodDecl_c
            void
              au::edu::anu::mm::WignerRotationMatrix::__fieldInitializers43267(
              ) {
                
                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10": x10.ast.X10FieldAssign_c
                ((x10aux::ref<au::edu::anu::mm::WignerRotationMatrix>)this)->
                  FMGL(X10__object_lock_id0) =
                  ((x10_int)-1);
            }
            const x10aux::serialization_id_t au::edu::anu::mm::WignerRotationMatrix::_serialization_id = 
                x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::WignerRotationMatrix::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);
            
            void au::edu::anu::mm::WignerRotationMatrix::_serialize_body(x10aux::serialization_buffer& buf) {
                x10::lang::Object::_serialize_body(buf);
                
            }
            
            x10aux::ref<x10::lang::Reference> au::edu::anu::mm::WignerRotationMatrix::_deserializer(x10aux::deserialization_buffer& buf) {
                x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::WignerRotationMatrix>(), 0, sizeof(au::edu::anu::mm::WignerRotationMatrix))) au::edu::anu::mm::WignerRotationMatrix();
                buf.record_reference(this_);
                this_->_deserialize_body(buf);
                return this_;
            }
            
            void au::edu::anu::mm::WignerRotationMatrix::_deserialize_body(x10aux::deserialization_buffer& buf) {
                x10::lang::Object::_deserialize_body(buf);
                
            }
            
            x10aux::RuntimeType au::edu::anu::mm::WignerRotationMatrix::rtt;
            void au::edu::anu::mm::WignerRotationMatrix::_initRTT() {
                if (rtt.initStageOne(&rtt)) return;
                const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
                rtt.initStageTwo("au.edu.anu.mm.WignerRotationMatrix",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
            }
            /* END of WignerRotationMatrix */
/*************************************************/
