/*************************************************/
/* START of AssociatedLegendrePolynomial */
#include <au/edu/anu/mm/AssociatedLegendrePolynomial.h>

#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Double.h>
#include <x10/array/Array.h>
#include <x10/lang/Math.h>
#include <x10/array/TriangularRegion.h>
#include <x10/array/Region.h>
#include <x10/array/RectLayout.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/IllegalArgumentException.h>

//#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10FieldDecl_c

//#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::AssociatedLegendrePolynomial::getOrderedLock(
  ) {
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::AssociatedLegendrePolynomial.X10$class_lock_id1");
    x10_int __var571__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var571__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var572__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var572__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::AssociatedLegendrePolynomial.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::AssociatedLegendrePolynomial.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__573 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::AssociatedLegendrePolynomial::FMGL(X10__class_lock_id1__deserialize));


//#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::AssociatedLegendrePolynomial::getStaticOrderedLock(
  ) {
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId58170 = au::edu::anu::mm::AssociatedLegendrePolynomial::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId58170);
    }))
    ;
    
}

//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10_double> >
  au::edu::anu::mm::AssociatedLegendrePolynomial::getPlk(
  x10_double theta,
  x10_int p) {
    
    //#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10_double cosTheta = x10aux::math_utils::cos(theta);
    
    //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10_double sinTheta = x10aux::math_utils::sin(theta);
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::TriangularRegion> triRegion =
      
    x10aux::ref<x10::array::TriangularRegion>((new (memset(x10aux::alloc<x10::array::TriangularRegion>(), 0, sizeof(x10::array::TriangularRegion))) x10::array::TriangularRegion()))
    ;
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
        x10_int size58927 = ((x10_int) ((p) + (((x10_int)1))));
        {
            
            //#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                {
                    
                    //#line 469 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (triRegion)->::x10::lang::Object::_constructor();
                    
                    //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                    triRegion->FMGL(rank) =
                      ((x10_int)2);
                    
                    //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                    triRegion->FMGL(rect) =
                      false;
                    
                    //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                    triRegion->FMGL(zeroBased) =
                      true;
                    
                    //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                    triRegion->FMGL(rail) =
                      false;
                }
                
            }))
            ;
            
            //#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            triRegion->FMGL(dim) = size58927;
            
            //#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            triRegion->FMGL(rowMin) = ((x10_int)0);
            
            //#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            triRegion->FMGL(colMin) = ((x10_int)0);
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            triRegion->FMGL(lower) = true;
        }
        
    }))
    ;
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10_double> > P =
      
    x10aux::ref<x10::array::Array<x10_double> >((new (memset(x10aux::alloc<x10::array::Array<x10_double> >(), 0, sizeof(x10::array::Array<x10_double>))) x10::array::Array<x10_double>()))
    ;
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> reg58935 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::array::Region> >(triRegion);
        {
            
            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
            (P)->::x10::lang::Object::_constructor();
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            P->FMGL(region) = (reg58935);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            P->FMGL(rank) = x10aux::nullCheck(reg58935)->
                              FMGL(rank);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            P->FMGL(rect) = x10aux::nullCheck(reg58935)->
                              FMGL(rect);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            P->FMGL(zeroBased) = x10aux::nullCheck(reg58935)->
                                   FMGL(zeroBased);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            P->FMGL(rail) = x10aux::nullCheck(reg58935)->
                              FMGL(rail);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            P->FMGL(size) = x10aux::nullCheck(reg58935)->size();
            
            //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            P->FMGL(layout) = (__extension__ ({
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout alloc1995458936 =
                  
                x10::array::RectLayout::_alloc();
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc1995458936)->::x10::array::RectLayout::_constructor(
                  reg58935);
                alloc1995458936;
            }))
            ;
            
            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int n58937 = (__extension__ ({
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this58939 =
                  P->
                    FMGL(layout);
                this58939->FMGL(size);
            }))
            ;
            
            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            P->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10_double >(n58937, 8, false, true);
        }
        
    }))
    ;
    
    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_double ret58943;
        {
            
            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
            (P->FMGL(raw))->__set((__extension__ ({
                
                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this58948 =
                  P->
                    FMGL(layout);
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                x10_int ret58949;
                {
                    
                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int offset58947 =
                      ((x10_int) ((((x10_int)0)) - (this58948->
                                                      FMGL(min0))));
                    
                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    offset58947 = ((x10_int) ((((x10_int) ((((x10_int) ((offset58947) * (this58948->
                                                                                           FMGL(delta1))))) + (((x10_int)0))))) - (this58948->
                                                                                                                                     FMGL(min1))));
                    
                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    ret58949 = offset58947;
                }
                ret58949;
            }))
            , 1.0);
            
            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret58943 = 1.0;
        }
        ret58943;
    }))
    ;
    
    //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10_double fact = 1.0;
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10_int i52700max5270259193 = p;
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": polyglot.ast.For_c
    {
        x10_int i5270059194;
        for (
             //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
             i5270059194 = ((x10_int)1); ((i5270059194) <= (i52700max5270259193));
             
             //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
             i5270059194 =
               ((x10_int) ((i5270059194) + (((x10_int)1)))))
        {
            
            //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
            x10_int l59195 =
              i5270059194;
            
            //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05896159129 =
                  l59195;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i15896259130 =
                  l59195;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double v5896359131 =
                  ((((fact) * (sinTheta))) * ((-((__extension__ ({
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i05895159132 =
                      ((x10_int) ((l59195) - (((x10_int)1))));
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i15895259133 =
                      ((x10_int) ((l59195) - (((x10_int)1))));
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret5895359134;
                    {
                        
                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret5895359134 =
                          (P->
                             FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this5895859135 =
                              P->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i05895559136 =
                              i05895159132;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i15895659137 =
                              i15895259133;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret5895959138;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset5895759139 =
                                  ((x10_int) ((i05895559136) - (this5895859135->
                                                                  FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset5895759139 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset5895759139) * (this5895859135->
                                                                                                FMGL(delta1))))) + (i15895659137)))) - (this5895859135->
                                                                                                                                          FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret5895959138 =
                                  offset5895759139;
                            }
                            ret5895959138;
                        }))
                        );
                    }
                    ret5895359134;
                }))
                ))));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double ret5896459140;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (P->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this5896959141 =
                          P->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i05896659142 =
                          i05896159129;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i15896759143 =
                          i15896259130;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret5897059144;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset5896859145 =
                              ((x10_int) ((i05896659142) - (this5896959141->
                                                              FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset5896859145 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5896859145) * (this5896959141->
                                                                                            FMGL(delta1))))) + (i15896759143)))) - (this5896959141->
                                                                                                                                      FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret5897059144 =
                              offset5896859145;
                        }
                        ret5897059144;
                    }))
                    , v5896359131);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5896459140 =
                      v5896359131;
                }
                ret5896459140;
            }))
            ;
            
            //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05898259146 =
                  l59195;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i15898359147 =
                  ((x10_int) ((l59195) - (((x10_int)1))));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double v5898459148 =
                  ((((fact) * (cosTheta))) * ((__extension__ ({
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i05897259149 =
                      ((x10_int) ((l59195) - (((x10_int)1))));
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i15897359150 =
                      ((x10_int) ((l59195) - (((x10_int)1))));
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret5897459151;
                    {
                        
                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret5897459151 =
                          (P->
                             FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this5897959152 =
                              P->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i05897659153 =
                              i05897259149;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i15897759154 =
                              i15897359150;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret5898059155;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset5897859156 =
                                  ((x10_int) ((i05897659153) - (this5897959152->
                                                                  FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset5897859156 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset5897859156) * (this5897959152->
                                                                                                FMGL(delta1))))) + (i15897759154)))) - (this5897959152->
                                                                                                                                          FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret5898059155 =
                                  offset5897859156;
                            }
                            ret5898059155;
                        }))
                        );
                    }
                    ret5897459151;
                }))
                ));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double ret5898559157;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (P->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this5899059158 =
                          P->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i05898759159 =
                          i05898259146;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i15898859160 =
                          i15898359147;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret5899159161;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset5898959162 =
                              ((x10_int) ((i05898759159) - (this5899059158->
                                                              FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset5898959162 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset5898959162) * (this5899059158->
                                                                                            FMGL(delta1))))) + (i15898859160)))) - (this5899059158->
                                                                                                                                      FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret5899159161 =
                              offset5898959162;
                        }
                        ret5899159161;
                    }))
                    , v5898459148);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5898559157 =
                      v5898459148;
                }
                ret5898559157;
            }))
            ;
            
            //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
            fact =
              ((fact) + (2.0));
        }
    }
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
    fact = 1.0;
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10_int i52732max5273459197 = p;
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": polyglot.ast.For_c
    {
        x10_int i5273259198;
        for (
             //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
             i5273259198 = ((x10_int)2); ((i5273259198) <= (i52732max5273459197));
             
             //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
             i5273259198 =
               ((x10_int) ((i5273259198) + (((x10_int)1)))))
        {
            
            //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
            x10_int l59199 =
              i5273259198;
            
            //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
            fact =
              ((fact) + (2.0));
            
            //#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
            x10_int i52716max5271859189 =
              ((x10_int) ((l59199) - (((x10_int)2))));
            
            //#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": polyglot.ast.For_c
            {
                x10_int i5271659190;
                for (
                     //#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
                     i5271659190 =
                       ((x10_int)0);
                     ((i5271659190) <= (i52716max5271859189));
                     
                     //#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
                     i5271659190 =
                       ((x10_int) ((i5271659190) + (((x10_int)1)))))
                {
                    
                    //#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
                    x10_int k59191 =
                      i5271659190;
                    
                    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i05901359163 =
                          l59199;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i15901459164 =
                          k59191;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double v5901559165 =
                          ((((((((fact) * (cosTheta))) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05899359166 =
                              ((x10_int) ((l59199) - (((x10_int)1))));
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15899459167 =
                              k59191;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret5899559168;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5899559168 =
                                  (P->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5900059169 =
                                      P->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05899759170 =
                                      i05899359166;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15899859171 =
                                      i15899459167;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5900159172;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5899959173 =
                                          ((x10_int) ((i05899759170) - (this5900059169->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5899959173 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5899959173) * (this5900059169->
                                                                                                        FMGL(delta1))))) + (i15899859171)))) - (this5900059169->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5900159172 =
                                          offset5899959173;
                                    }
                                    ret5900159172;
                                }))
                                );
                            }
                            ret5899559168;
                        }))
                        ))) - (((((x10_double) (((x10_int) ((((x10_int) ((l59199) + (k59191)))) - (((x10_int)1))))))) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i05900359174 =
                              ((x10_int) ((l59199) - (((x10_int)2))));
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i15900459175 =
                              k59191;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret5900559176;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret5900559176 =
                                  (P->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this5901059177 =
                                      P->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i05900759178 =
                                      i05900359174;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i15900859179 =
                                      i15900459175;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5901159180;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset5900959181 =
                                          ((x10_int) ((i05900759178) - (this5901059177->
                                                                          FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset5900959181 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset5900959181) * (this5901059177->
                                                                                                        FMGL(delta1))))) + (i15900859179)))) - (this5901059177->
                                                                                                                                                  FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret5901159180 =
                                          offset5900959181;
                                    }
                                    ret5901159180;
                                }))
                                );
                            }
                            ret5900559176;
                        }))
                        ))))) / (((x10_double) (((x10_int) ((l59199) - (k59191)))))));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double ret5901659182;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (P->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this5902159183 =
                                  P->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i05901859184 =
                                  i05901359163;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i15901959185 =
                                  i15901459164;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret5902259186;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset5902059187 =
                                      ((x10_int) ((i05901859184) - (this5902159183->
                                                                      FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset5902059187 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset5902059187) * (this5902159183->
                                                                                                    FMGL(delta1))))) + (i15901959185)))) - (this5902159183->
                                                                                                                                              FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret5902259186 =
                                      offset5902059187;
                                }
                                ret5902259186;
                            }))
                            , v5901559165);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret5901659182 =
                              v5901559165;
                        }
                        ret5901659182;
                    }))
                    ;
                }
            }
            
        }
    }
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10Return_c
    return P;
    
}

//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10_double> >
  au::edu::anu::mm::AssociatedLegendrePolynomial::getPlm(
  x10_double x,
  x10_int p) {
    
    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10If_c
    if (((::fabs(x)) > (1.0))) {
        
        //#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": polyglot.ast.Throw_c
        x10aux::throwException(x10aux::nullCheck(x10::lang::IllegalArgumentException::_make(x10aux::string_utils::lit("abs(x) > 1: Associated Legendre functions are only defined on [-1, 1]."))));
    }
    
    //#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::TriangularRegion> triRegion =
      
    x10aux::ref<x10::array::TriangularRegion>((new (memset(x10aux::alloc<x10::array::TriangularRegion>(), 0, sizeof(x10::array::TriangularRegion))) x10::array::TriangularRegion()))
    ;
    
    //#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10LocalDecl_c
        x10_int size59026 = ((x10_int) ((p) + (((x10_int)1))));
        {
            
            //#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                {
                    
                    //#line 469 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (triRegion)->::x10::lang::Object::_constructor();
                    
                    //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                    triRegion->FMGL(rank) =
                      ((x10_int)2);
                    
                    //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                    triRegion->FMGL(rect) =
                      false;
                    
                    //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                    triRegion->FMGL(zeroBased) =
                      true;
                    
                    //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
                    triRegion->FMGL(rail) =
                      false;
                }
                
            }))
            ;
            
            //#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            triRegion->FMGL(dim) = size59026;
            
            //#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            triRegion->FMGL(rowMin) = ((x10_int)0);
            
            //#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            triRegion->FMGL(colMin) = ((x10_int)0);
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10": x10.ast.X10FieldAssign_c
            triRegion->FMGL(lower) = true;
        }
        
    }))
    ;
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10_double> > Plm =
      
    x10aux::ref<x10::array::Array<x10_double> >((new (memset(x10aux::alloc<x10::array::Array<x10_double> >(), 0, sizeof(x10::array::Array<x10_double>))) x10::array::Array<x10_double>()))
    ;
    
    //#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> reg59034 =
          x10aux::class_cast_unchecked<x10aux::ref<x10::array::Region> >(triRegion);
        {
            
            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
            (Plm)->::x10::lang::Object::_constructor();
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            Plm->FMGL(region) = (reg59034);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            Plm->FMGL(rank) = x10aux::nullCheck(reg59034)->
                                FMGL(rank);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            Plm->FMGL(rect) = x10aux::nullCheck(reg59034)->
                                FMGL(rect);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            Plm->FMGL(zeroBased) = x10aux::nullCheck(reg59034)->
                                     FMGL(zeroBased);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            Plm->FMGL(rail) = x10aux::nullCheck(reg59034)->
                                FMGL(rail);
            
            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            Plm->FMGL(size) = x10aux::nullCheck(reg59034)->size();
            
            //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            Plm->FMGL(layout) = (__extension__ ({
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout alloc1995459035 =
                  
                x10::array::RectLayout::_alloc();
                
                //#line 133 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc1995459035)->::x10::array::RectLayout::_constructor(
                  reg59034);
                alloc1995459035;
            }))
            ;
            
            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int n59036 = (__extension__ ({
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this59038 =
                  Plm->
                    FMGL(layout);
                this59038->FMGL(size);
            }))
            ;
            
            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            Plm->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10_double >(n59036, 8, false, true);
        }
        
    }))
    ;
    
    //#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_double ret59042;
        {
            
            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
            (Plm->FMGL(raw))->__set((__extension__ ({
                
                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this59047 =
                  Plm->
                    FMGL(layout);
                
                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                x10_int ret59048;
                {
                    
                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int offset59046 =
                      ((x10_int) ((((x10_int)0)) - (this59047->
                                                      FMGL(min0))));
                    
                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    offset59046 = ((x10_int) ((((x10_int) ((((x10_int) ((offset59046) * (this59047->
                                                                                           FMGL(delta1))))) + (((x10_int)0))))) - (this59047->
                                                                                                                                     FMGL(min1))));
                    
                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                    ret59048 = offset59046;
                }
                ret59048;
            }))
            , 1.0);
            
            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret59042 = 1.0;
        }
        ret59042;
    }))
    ;
    
    //#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10_double somx2 = x10aux::math_utils::sqrt(((((1.0) - (x))) * (((1.0) + (x)))));
    
    //#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
    x10_double fact = 1.0;
    
    //#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": polyglot.ast.For_c
    {
        x10_int i;
        for (
             //#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
             i = ((x10_int)1); ((i) <= (p));
             
             //#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
             i =
               ((x10_int) ((i) + (((x10_int)1)))))
        {
            
            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i059060 =
                  i;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i159061 =
                  i;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double v59062 =
                  (((((-((__extension__ ({
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i059050 =
                      ((x10_int) ((i) - (((x10_int)1))));
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i159051 =
                      ((x10_int) ((i) - (((x10_int)1))));
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret59052;
                    {
                        
                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret59052 =
                          (Plm->
                             FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this59057 =
                              Plm->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i059054 =
                              i059050;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i159055 =
                              i159051;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret59058;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset59056 =
                                  ((x10_int) ((i059054) - (this59057->
                                                             FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset59056 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset59056) * (this59057->
                                                                                           FMGL(delta1))))) + (i159055)))) - (this59057->
                                                                                                                                FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret59058 =
                                  offset59056;
                            }
                            ret59058;
                        }))
                        );
                    }
                    ret59052;
                }))
                ))) * (fact))) * (somx2));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double ret59063;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (Plm->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this59068 =
                          Plm->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i059065 =
                          i059060;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i159066 =
                          i159061;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret59069;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset59067 =
                              ((x10_int) ((i059065) - (this59068->
                                                         FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset59067 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset59067) * (this59068->
                                                                                       FMGL(delta1))))) + (i159066)))) - (this59068->
                                                                                                                            FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret59069 =
                              offset59067;
                        }
                        ret59069;
                    }))
                    , v59062);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret59063 =
                      v59062;
                }
                ret59063;
            }))
            ;
            
            //#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i059081 =
                  i;
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i159082 =
                  ((x10_int) ((i) - (((x10_int)1))));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double v59083 =
                  ((((x) * (fact))) * ((__extension__ ({
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i059071 =
                      ((x10_int) ((i) - (((x10_int)1))));
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i159072 =
                      ((x10_int) ((i) - (((x10_int)1))));
                    
                    //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret59073;
                    {
                        
                        //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret59073 =
                          (Plm->
                             FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this59078 =
                              Plm->
                                FMGL(layout);
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i059075 =
                              i059071;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int i159076 =
                              i159072;
                            
                            //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret59079;
                            {
                                
                                //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset59077 =
                                  ((x10_int) ((i059075) - (this59078->
                                                             FMGL(min0))));
                                
                                //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                offset59077 =
                                  ((x10_int) ((((x10_int) ((((x10_int) ((offset59077) * (this59078->
                                                                                           FMGL(delta1))))) + (i159076)))) - (this59078->
                                                                                                                                FMGL(min1))));
                                
                                //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret59079 =
                                  offset59077;
                            }
                            ret59079;
                        }))
                        );
                    }
                    ret59073;
                }))
                ));
                
                //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double ret59084;
                {
                    
                    //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (Plm->
                       FMGL(raw))->__set((__extension__ ({
                        
                        //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this59089 =
                          Plm->
                            FMGL(layout);
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i059086 =
                          i059081;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int i159087 =
                          i159082;
                        
                        //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret59090;
                        {
                            
                            //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset59088 =
                              ((x10_int) ((i059086) - (this59089->
                                                         FMGL(min0))));
                            
                            //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            offset59088 =
                              ((x10_int) ((((x10_int) ((((x10_int) ((offset59088) * (this59089->
                                                                                       FMGL(delta1))))) + (i159087)))) - (this59089->
                                                                                                                            FMGL(min1))));
                            
                            //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret59090 =
                              offset59088;
                        }
                        ret59090;
                    }))
                    , v59083);
                    
                    //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret59084 =
                      v59083;
                }
                ret59084;
            }))
            ;
            
            //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
            fact =
              ((fact) + (2.0));
        }
    }
    
    //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": polyglot.ast.For_c
    {
        x10_int m;
        for (
             //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
             m = ((x10_int)0); ((m) <= (((x10_int) ((p) - (((x10_int)2))))));
             
             //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
             m =
               ((x10_int) ((m) + (((x10_int)1)))))
        {
            
            //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": polyglot.ast.For_c
            {
                x10_int l;
                for (
                     //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
                     l =
                       ((x10_int) ((m) + (((x10_int)2))));
                     ((l) <= (p));
                     
                     //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalAssign_c
                     l =
                       ((x10_int) ((l) + (((x10_int)1)))))
                {
                    
                    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i059112 =
                          l;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i159113 =
                          m;
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double v59114 =
                          ((((((((x) * (((((2.0) * (((x10_double) (l))))) - (1.0))))) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i059092 =
                              ((x10_int) ((l) - (((x10_int)1))));
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i159093 =
                              m;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret59094;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret59094 =
                                  (Plm->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this59099 =
                                      Plm->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i059096 =
                                      i059092;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i159097 =
                                      i159093;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret59100;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset59098 =
                                          ((x10_int) ((i059096) - (this59099->
                                                                     FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset59098 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset59098) * (this59099->
                                                                                                   FMGL(delta1))))) + (i159097)))) - (this59099->
                                                                                                                                        FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret59100 =
                                          offset59098;
                                    }
                                    ret59100;
                                }))
                                );
                            }
                            ret59094;
                        }))
                        ))) - (((((((x10_double) (((x10_int) ((l) + (m)))))) - (1.0))) * ((__extension__ ({
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i059102 =
                              ((x10_int) ((l) - (((x10_int)2))));
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i159103 =
                              m;
                            
                            //#line 433 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_double ret59104;
                            {
                                
                                //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret59104 =
                                  (Plm->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 437 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this59109 =
                                      Plm->
                                        FMGL(layout);
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i059106 =
                                      i059102;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int i159107 =
                                      i159103;
                                    
                                    //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret59110;
                                    {
                                        
                                        //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset59108 =
                                          ((x10_int) ((i059106) - (this59109->
                                                                     FMGL(min0))));
                                        
                                        //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        offset59108 =
                                          ((x10_int) ((((x10_int) ((((x10_int) ((offset59108) * (this59109->
                                                                                                   FMGL(delta1))))) + (i159107)))) - (this59109->
                                                                                                                                        FMGL(min1))));
                                        
                                        //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret59110 =
                                          offset59108;
                                    }
                                    ret59110;
                                }))
                                );
                            }
                            ret59104;
                        }))
                        ))))) / (((x10_double) (((x10_int) ((l) - (m)))))));
                        
                        //#line 535 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_double ret59115;
                        {
                            
                            //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (Plm->
                               FMGL(raw))->__set((__extension__ ({
                                
                                //#line 539 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this59120 =
                                  Plm->
                                    FMGL(layout);
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i059117 =
                                  i059112;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int i159118 =
                                  i159113;
                                
                                //#line 134 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret59121;
                                {
                                    
                                    //#line 135 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset59119 =
                                      ((x10_int) ((i059117) - (this59120->
                                                                 FMGL(min0))));
                                    
                                    //#line 136 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    offset59119 =
                                      ((x10_int) ((((x10_int) ((((x10_int) ((offset59119) * (this59120->
                                                                                               FMGL(delta1))))) + (i159118)))) - (this59120->
                                                                                                                                    FMGL(min1))));
                                    
                                    //#line 137 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret59121 =
                                      offset59119;
                                }
                                ret59121;
                            }))
                            , v59114);
                            
                            //#line 540 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret59115 =
                              v59114;
                        }
                        ret59115;
                    }))
                    ;
                }
            }
            
        }
    }
    
    //#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10Return_c
    return Plm;
    
}

//#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial>
  au::edu::anu::mm::AssociatedLegendrePolynomial::au__edu__anu__mm__AssociatedLegendrePolynomial____au__edu__anu__mm__AssociatedLegendrePolynomial__this(
  ) {
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial>)this);
    
}

//#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::AssociatedLegendrePolynomial::_constructor(
  ) {
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.AssignPropertyCall_c
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial> this5912359200 =
          ((x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial>)this);
        {
            
            //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5912359200)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
}
x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial> au::edu::anu::mm::AssociatedLegendrePolynomial::_make(
  ) {
    x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::AssociatedLegendrePolynomial>(), 0, sizeof(au::edu::anu::mm::AssociatedLegendrePolynomial))) au::edu::anu::mm::AssociatedLegendrePolynomial();
    this_->_constructor();
    return this_;
}



//#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::AssociatedLegendrePolynomial::_constructor(
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.AssignPropertyCall_c
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial> this5912659201 =
          ((x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial>)this);
        {
            
            //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5912659201)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial> au::edu::anu::mm::AssociatedLegendrePolynomial::_make(
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::AssociatedLegendrePolynomial>(), 0, sizeof(au::edu::anu::mm::AssociatedLegendrePolynomial))) au::edu::anu::mm::AssociatedLegendrePolynomial();
    this_->_constructor(paramLock);
    return this_;
}



//#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::AssociatedLegendrePolynomial::__fieldInitializers52332(
  ) {
    
    //#line 19 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/AssociatedLegendrePolynomial.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
}
const x10aux::serialization_id_t au::edu::anu::mm::AssociatedLegendrePolynomial::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::AssociatedLegendrePolynomial::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::AssociatedLegendrePolynomial::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::AssociatedLegendrePolynomial::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::AssociatedLegendrePolynomial> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::AssociatedLegendrePolynomial>(), 0, sizeof(au::edu::anu::mm::AssociatedLegendrePolynomial))) au::edu::anu::mm::AssociatedLegendrePolynomial();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::AssociatedLegendrePolynomial::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    
}

x10aux::RuntimeType au::edu::anu::mm::AssociatedLegendrePolynomial::rtt;
void au::edu::anu::mm::AssociatedLegendrePolynomial::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
    rtt.initStageTwo("au.edu.anu.mm.AssociatedLegendrePolynomial",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
/* END of AssociatedLegendrePolynomial */
/*************************************************/
