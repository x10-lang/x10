/*************************************************/
/* START of LocallyEssentialTree */
#include <au/edu/anu/mm/LocallyEssentialTree.h>

#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/array/Array.h>
#include <x10/array/Point.h>
#include <x10/array/DistArray.h>
#include <au/edu/anu/mm/MultipoleExpansion.h>
#include <au/edu/anu/chem/PointCharge.h>
#include <x10/array/Region.h>
#include <x10/array/RectRegion1D.h>
#include <x10/array/RectLayout.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/IntRange.h>
#include <x10/array/Dist.h>
#include <x10/array/PeriodicDist.h>
#include <x10/array/ConstantDist.h>
#include <x10/lang/Place.h>
#include <x10/lang/Runtime.h>

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::LocallyEssentialTree::getOrderedLock(
  ) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::LocallyEssentialTree.X10$class_lock_id1");
    x10_int __var467__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var467__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var468__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var468__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::LocallyEssentialTree.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::LocallyEssentialTree.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__469 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__deserialize));


//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::LocallyEssentialTree::getStaticOrderedLock(
  ) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId56462 = au::edu::anu::mm::LocallyEssentialTree::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId56462);
    }))
    ;
    
}

//#line 22 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c

//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c

//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c
/**
     * A cache of multipole copies for the combined V-list of all
     * boxes at this place.  Used to overlap fetching of the multipole
     * expansions with other computation.
     * The Array has one element for each level; each element
     * holds the portion of the combined V-list for that level.
     */
                                                                                                                                                                                                                                                                                                                            //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldDecl_c
                                                                                                                                                                                                                                                                                                                            /**
     * A cache of PointCharge for the combined U-list of all
     * boxes at this place.  Used to store fetched atoms from 
     * non-well-separated boxes for use in direct evaluations 
     * with all atoms at a given place.
     */

//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::LocallyEssentialTree::_constructor(
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > combinedUList,
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > combinedVList,
  x10aux::ref<x10::array::Array<x10_int> > uListMin,
  x10aux::ref<x10::array::Array<x10_int> > uListMax,
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMin,
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMax)
{
    
    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.AssignPropertyCall_c
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> this5646357047 =
          ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this);
        {
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5646357047)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
      FMGL(combinedUList) =
      combinedUList;
    
    //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
      FMGL(combinedVList) =
      combinedVList;
    
    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
      FMGL(uListMin) =
      uListMin;
    
    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
      FMGL(uListMax) =
      uListMax;
    
    //#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
      FMGL(vListMin) =
      vListMin;
    
    //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
      FMGL(vListMax) =
      vListMax;
    
    //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > > > multipoleCopies =
      
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > > >(), 0, sizeof(x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > >))) x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > >()))
    ;
    
    //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_int size56466 =
          x10aux::nullCheck(combinedVList)->
            FMGL(size);
        {
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
            (multipoleCopies)->::x10::lang::Object::_constructor();
            
            //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> myReg56467 =
              x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::RectRegion1D> alloc1996056468 =
                  
                x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                ;
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc1996056468)->::x10::array::RectRegion1D::_constructor(
                  ((x10_int)0),
                  ((x10_int) ((size56466) - (((x10_int)1)))));
                alloc1996056468;
            }))
            );
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            multipoleCopies->
              FMGL(region) =
              myReg56467;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            multipoleCopies->
              FMGL(rank) =
              ((x10_int)1);
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            multipoleCopies->
              FMGL(rect) =
              true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            multipoleCopies->
              FMGL(zeroBased) =
              true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            multipoleCopies->
              FMGL(rail) =
              true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            multipoleCopies->
              FMGL(size) =
              size56466;
            
            //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            multipoleCopies->
              FMGL(layout) =
              (__extension__ ({
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout alloc1996156469 =
                  
                x10::array::RectLayout::_alloc();
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int _max056473 =
                      ((x10_int) ((size56466) - (((x10_int)1))));
                    {
                        
                        //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(rank) =
                          ((x10_int)1);
                        
                        //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(min0) =
                          ((x10_int)0);
                        
                        //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(delta0) =
                          ((x10_int) ((((x10_int) ((_max056473) - (((x10_int)0))))) + (((x10_int)1))));
                        
                        //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(size) =
                          ((alloc1996156469->
                              FMGL(delta0)) > (((x10_int)0)))
                          ? (alloc1996156469->
                               FMGL(delta0))
                          : (((x10_int)0));
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(min1) =
                          ((x10_int)0);
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(delta1) =
                          ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(min2) =
                          ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(delta2) =
                          ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(min3) =
                          ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(delta3) =
                          ((x10_int)0);
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(min) =
                          X10_NULL;
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996156469->
                          FMGL(delta) =
                          X10_NULL;
                    }
                    
                }))
                ;
                alloc1996156469;
            }))
            ;
            
            //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int n56470 =
              (__extension__ ({
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this56475 =
                  multipoleCopies->
                    FMGL(layout);
                this56475->
                  FMGL(size);
            }))
            ;
            
            //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            multipoleCopies->
              FMGL(raw) =
              x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > >(n56470, 8, false, true);
        }
        
    }))
    ;
    
    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
    x10_int i47437max4743957049 =
      ((x10_int) ((x10aux::nullCheck(combinedVList)->
                     FMGL(size)) - (((x10_int)1))));
    
    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": polyglot.ast.For_c
    {
        x10_int i4743757050;
        for (
             //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
             i4743757050 =
               ((x10_int)0);
             ((i4743757050) <= (i47437max4743957049));
             
             //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalAssign_c
             i4743757050 =
               ((x10_int) ((i4743757050) + (((x10_int)1)))))
        {
            
            //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
            x10_int i57051 =
              i4743757050;
            
            //#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10If_c
            if ((!x10aux::struct_equals((__extension__ ({
                                            
                                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05647656994 =
                                              i57109;
                                            
                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > ret5647756995;
                                            
                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                            goto __ret5647856996; __ret5647856996: {
                                            {
                                                
                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                ret5647756995 =
                                                  (x10aux::nullCheck(combinedVList)->
                                                     FMGL(raw))->__apply(i05647656994);
                                                
                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                goto __ret5647856996_end_;
                                            }goto __ret5647856996_end_; __ret5647856996_end_: ;
                                            }
                                            ret5647756995;
                                            }))
                                            ,
                                            X10_NULL)))
                {
                    
                    //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::Region> multipoleCopiesLevelRegion56997 =
                      x10aux::nullCheck(x10::lang::IntRange::_make((__extension__ ({
                          
                          //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                          x10aux::ref<x10::array::Array<x10_int> > this5649356998 =
                            (__extension__ ({
                              
                              //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10_int i05648456999 =
                                i57109;
                              
                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10aux::ref<x10::array::Array<x10_int> > ret5648557000;
                              
                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                              goto __ret5648657001; __ret5648657001: {
                              {
                                  
                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                  ret5648557000 =
                                    (x10aux::nullCheck(vListMin)->
                                       FMGL(raw))->__apply(i05648456999);
                                  
                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                  goto __ret5648657001_end_;
                              }goto __ret5648657001_end_; __ret5648657001_end_: ;
                              }
                              ret5648557000;
                              }))
                              ;
                              
                          
                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                          x10_int ret5649457002;
                          
                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                          goto __ret5649557003; __ret5649557003: {
                          {
                              
                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                              ret5649457002 =
                                (x10aux::nullCheck(this5649356998)->
                                   FMGL(raw))->__apply(((x10_int)0));
                              
                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                              goto __ret5649557003_end_;
                          }goto __ret5649557003_end_; __ret5649557003_end_: ;
                          }
                          ret5649457002;
                          }))
                          , (__extension__ ({
                              
                              //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                              x10aux::ref<x10::array::Array<x10_int> > this5651057004 =
                                (__extension__ ({
                                  
                                  //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                  x10_int i05650157005 =
                                    i57109;
                                  
                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                  x10aux::ref<x10::array::Array<x10_int> > ret5650257006;
                                  
                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                  goto __ret5650357007; __ret5650357007: {
                                  {
                                      
                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                      ret5650257006 =
                                        (x10aux::nullCheck(vListMax)->
                                           FMGL(raw))->__apply(i05650157005);
                                      
                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                      goto __ret5650357007_end_;
                                  }goto __ret5650357007_end_; __ret5650357007_end_: ;
                                  }
                                  ret5650257006;
                                  }))
                                  ;
                                  
                              
                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                              x10_int ret5651157008;
                              
                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                              goto __ret5651257009; __ret5651257009: {
                              {
                                  
                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                  ret5651157008 =
                                    (x10aux::nullCheck(this5651057004)->
                                       FMGL(raw))->__apply(((x10_int)0));
                                  
                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                  goto __ret5651257009_end_;
                              }goto __ret5651257009_end_; __ret5651257009_end_: ;
                              }
                              ret5651157008;
                              }))
                              ))->x10::lang::IntRange::__times(
                                x10::lang::IntRange::_make((__extension__ ({
                                    
                                    //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                    x10aux::ref<x10::array::Array<x10_int> > this5652757010 =
                                      (__extension__ ({
                                        
                                        //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int i05651857011 =
                                          i57109;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<x10::array::Array<x10_int> > ret5651957012;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                        goto __ret5652057013; __ret5652057013: {
                                        {
                                            
                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5651957012 =
                                              (x10aux::nullCheck(vListMin)->
                                                 FMGL(raw))->__apply(i05651857011);
                                            
                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                            goto __ret5652057013_end_;
                                        }goto __ret5652057013_end_; __ret5652057013_end_: ;
                                        }
                                        ret5651957012;
                                        }))
                                        ;
                                        
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret5652857014;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                    goto __ret5652957015; __ret5652957015: {
                                    {
                                        
                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret5652857014 =
                                          (x10aux::nullCheck(this5652757010)->
                                             FMGL(raw))->__apply(((x10_int)1));
                                        
                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                        goto __ret5652957015_end_;
                                    }goto __ret5652957015_end_; __ret5652957015_end_: ;
                                    }
                                    ret5652857014;
                                    }))
                                    , (__extension__ ({
                                        
                                        //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<x10::array::Array<x10_int> > this5654457016 =
                                          (__extension__ ({
                                            
                                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10_int i05653557017 =
                                              i57109;
                                            
                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10aux::ref<x10::array::Array<x10_int> > ret5653657018;
                                            
                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                            goto __ret5653757019; __ret5653757019: {
                                            {
                                                
                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                ret5653657018 =
                                                  (x10aux::nullCheck(vListMax)->
                                                     FMGL(raw))->__apply(i05653557017);
                                                
                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                goto __ret5653757019_end_;
                                            }goto __ret5653757019_end_; __ret5653757019_end_: ;
                                            }
                                            ret5653657018;
                                            }))
                                            ;
                                            
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret5654557020;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                        goto __ret5654657021; __ret5654657021: {
                                        {
                                            
                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret5654557020 =
                                              (x10aux::nullCheck(this5654457016)->
                                                 FMGL(raw))->__apply(((x10_int)1));
                                            
                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                            goto __ret5654657021_end_;
                                        }goto __ret5654657021_end_; __ret5654657021_end_: ;
                                        }
                                        ret5654557020;
                                        }))
                                        ))->__times(
                                      (__extension__ ({
                                          
                                          //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                          x10::lang::IntRange r5658657022 =
                                            x10::lang::IntRange::_make((__extension__ ({
                                              
                                              //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                              x10aux::ref<x10::array::Array<x10_int> > this5656157023 =
                                                (__extension__ ({
                                                  
                                                  //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                  x10_int i05655257024 =
                                                    i57109;
                                                  
                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                  x10aux::ref<x10::array::Array<x10_int> > ret5655357025;
                                                  
                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                  goto __ret5655457026; __ret5655457026: {
                                                  {
                                                      
                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                      ret5655357025 =
                                                        (x10aux::nullCheck(vListMin)->
                                                           FMGL(raw))->__apply(i05655257024);
                                                      
                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                      goto __ret5655457026_end_;
                                                  }goto __ret5655457026_end_; __ret5655457026_end_: ;
                                                  }
                                                  ret5655357025;
                                                  }))
                                                  ;
                                                  
                                              
                                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int ret5656257027;
                                              
                                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                              goto __ret5656357028; __ret5656357028: {
                                              {
                                                  
                                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                  ret5656257027 =
                                                    (x10aux::nullCheck(this5656157023)->
                                                       FMGL(raw))->__apply(((x10_int)2));
                                                  
                                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                  goto __ret5656357028_end_;
                                              }goto __ret5656357028_end_; __ret5656357028_end_: ;
                                              }
                                              ret5656257027;
                                              }))
                                              , (__extension__ ({
                                                  
                                                  //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                  x10aux::ref<x10::array::Array<x10_int> > this5657857029 =
                                                    (__extension__ ({
                                                      
                                                      //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                      x10_int i05656957030 =
                                                        i57109;
                                                      
                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                      x10aux::ref<x10::array::Array<x10_int> > ret5657057031;
                                                      
                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                      goto __ret5657157032; __ret5657157032: {
                                                      {
                                                          
                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                          ret5657057031 =
                                                            (x10aux::nullCheck(vListMax)->
                                                               FMGL(raw))->__apply(i05656957030);
                                                          
                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                          goto __ret5657157032_end_;
                                                      }goto __ret5657157032_end_; __ret5657157032_end_: ;
                                                      }
                                                      ret5657057031;
                                                      }))
                                                      ;
                                                      
                                                  
                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                  x10_int ret5657957033;
                                                  
                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                  goto __ret5658057034; __ret5658057034: {
                                                  {
                                                      
                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                      ret5657957033 =
                                                        (x10aux::nullCheck(this5657857029)->
                                                           FMGL(raw))->__apply(((x10_int)2));
                                                      
                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                      goto __ret5658057034_end_;
                                                  }goto __ret5658057034_end_; __ret5658057034_end_: ;
                                                  }
                                                  ret5657957033;
                                                  }))
                                                  );
                                                  x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                                      
                                                      //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                                      x10aux::ref<x10::array::RectRegion1D> alloc206315658757035 =
                                                        
                                                      x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                                      ;
                                                      
                                                      //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                                                      (alloc206315658757035)->::x10::array::RectRegion1D::_constructor(
                                                        r5658657022->
                                                          FMGL(min),
                                                        r5658657022->
                                                          FMGL(max));
                                                      alloc206315658757035;
                                                  }))
                                                  );
                                              }))
                                              );
                                          
                                          //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.StmtExpr_c
                                          (__extension__ ({
                                              
                                              //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int i05672457036 =
                                                i57109;
                                              
                                              //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > v5672557037 =
                                                (__extension__ ({
                                                  
                                                  //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                                                  x10aux::ref<x10::array::Dist> dist5672257038 =
                                                    x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
                                                      
                                                      //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                      x10aux::ref<x10::array::PeriodicDist> alloc4743257039 =
                                                        
                                                      x10aux::ref<x10::array::PeriodicDist>((new (memset(x10aux::alloc<x10::array::PeriodicDist>(), 0, sizeof(x10::array::PeriodicDist))) x10::array::PeriodicDist()))
                                                      ;
                                                      
                                                      //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10ConstructorCall_c
                                                      (alloc4743257039)->::x10::array::PeriodicDist::_constructor(
                                                        (__extension__ ({
                                                            
                                                            //#line 64 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                            x10aux::ref<x10::array::Region> r5658857040 =
                                                              multipoleCopiesLevelRegion56997;
                                                            x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
                                                                
                                                                //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                x10aux::ref<x10::array::ConstantDist> alloc283935658957041 =
                                                                  
                                                                x10aux::ref<x10::array::ConstantDist>((new (memset(x10aux::alloc<x10::array::ConstantDist>(), 0, sizeof(x10::array::ConstantDist))) x10::array::ConstantDist()))
                                                                ;
                                                                
                                                                //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.StmtExpr_c
                                                                (__extension__ ({
                                                                    
                                                                    //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                                                                    x10aux::ref<x10::array::Region> r5671757042 =
                                                                      r5658857040;
                                                                    
                                                                    //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                                                                    x10::lang::Place p5671857043 =
                                                                      x10::lang::Place::_make(x10aux::here);
                                                                    {
                                                                        
                                                                        //#line 28 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.StmtExpr_c
                                                                        (__extension__ ({
                                                                            
                                                                            //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                            x10aux::ref<x10::array::Region> region5672057044 =
                                                                              r5671757042;
                                                                            {
                                                                                
                                                                                //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10ConstructorCall_c
                                                                                (alloc283935658957041)->::x10::lang::Object::_constructor();
                                                                                
                                                                                //#line 669 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10FieldAssign_c
                                                                                x10aux::nullCheck(alloc283935658957041)->
                                                                                  FMGL(region) =
                                                                                  region5672057044;
                                                                            }
                                                                            
                                                                        }))
                                                                        ;
                                                                        
                                                                        //#line 29 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10FieldAssign_c
                                                                        x10aux::nullCheck(alloc283935658957041)->
                                                                          FMGL(onePlace) =
                                                                          p5671857043;
                                                                    }
                                                                    
                                                                }))
                                                                ;
                                                                alloc283935658957041;
                                                            }))
                                                            );
                                                        }))
                                                        );
                                                      alloc4743257039;
                                                  }))
                                                  );
                                                  (__extension__ ({
                                                      
                                                      //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                                                      x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > alloc304845672357045 =
                                                        
                                                      x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >))) x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >()))
                                                      ;
                                                      
                                                      //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
                                                      (alloc304845672357045)->::x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >::_constructor(
                                                        dist5672257038);
                                                      alloc304845672357045;
                                                  }))
                                                  ;
                                              }))
                                              ;
                                              
                                              //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > ret5672657046;
                                              {
                                                  
                                                  //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                                  (multipoleCopies->
                                                     FMGL(raw))->__set(i05672457036, v5672557037);
                                                  
                                                  //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                  ret5672657046 =
                                                    v5672557037;
                                              }
                                              ret5672657046;
                                          }))
                                          ;
                                      }
                                      
                                    }
                                }
                                
                              
                              //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                              ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                FMGL(multipoleCopies) =
                                multipoleCopies;
                              
                              //#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                              x10aux::ref<x10::array::Region> cachedAtomsRegion =
                                x10aux::nullCheck(x10::lang::IntRange::_make((__extension__ ({
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret56734;
                                    
                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                    goto __ret56735; __ret56735: {
                                    {
                                        
                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret56734 =
                                          (x10aux::nullCheck(uListMin)->
                                             FMGL(raw))->__apply(((x10_int)0));
                                        
                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                        goto __ret56735_end_;
                                    }goto __ret56735_end_; __ret56735_end_: ;
                                    }
                                    ret56734;
                                    }))
                                    , (__extension__ ({
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                        x10_int ret56742;
                                        
                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                        goto __ret56743; __ret56743: {
                                        {
                                            
                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                            ret56742 =
                                              (x10aux::nullCheck(uListMax)->
                                                 FMGL(raw))->__apply(((x10_int)0));
                                            
                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                            goto __ret56743_end_;
                                        }goto __ret56743_end_; __ret56743_end_: ;
                                        }
                                        ret56742;
                                        }))
                                        ))->x10::lang::IntRange::__times(
                                          x10::lang::IntRange::_make((__extension__ ({
                                              
                                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                              x10_int ret56750;
                                              
                                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                              goto __ret56751; __ret56751: {
                                              {
                                                  
                                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                  ret56750 =
                                                    (x10aux::nullCheck(uListMin)->
                                                       FMGL(raw))->__apply(((x10_int)1));
                                                  
                                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                  goto __ret56751_end_;
                                              }goto __ret56751_end_; __ret56751_end_: ;
                                              }
                                              ret56750;
                                              }))
                                              , (__extension__ ({
                                                  
                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                  x10_int ret56758;
                                                  
                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                  goto __ret56759; __ret56759: {
                                                  {
                                                      
                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                      ret56758 =
                                                        (x10aux::nullCheck(uListMax)->
                                                           FMGL(raw))->__apply(((x10_int)1));
                                                      
                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                      goto __ret56759_end_;
                                                  }goto __ret56759_end_; __ret56759_end_: ;
                                                  }
                                                  ret56758;
                                                  }))
                                                  ))->__times(
                                            (__extension__ ({
                                                
                                                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                                x10::lang::IntRange r56781 =
                                                  x10::lang::IntRange::_make((__extension__ ({
                                                    
                                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10_int ret56766;
                                                    
                                                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                    goto __ret56767; __ret56767: {
                                                    {
                                                        
                                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                        ret56766 =
                                                          (x10aux::nullCheck(uListMin)->
                                                             FMGL(raw))->__apply(((x10_int)2));
                                                        
                                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                        goto __ret56767_end_;
                                                    }goto __ret56767_end_; __ret56767_end_: ;
                                                    }
                                                    ret56766;
                                                    }))
                                                    , (__extension__ ({
                                                        
                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                        x10_int ret56774;
                                                        
                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                        goto __ret56775; __ret56775: {
                                                        {
                                                            
                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                            ret56774 =
                                                              (x10aux::nullCheck(uListMax)->
                                                                 FMGL(raw))->__apply(((x10_int)2));
                                                            
                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                            goto __ret56775_end_;
                                                        }goto __ret56775_end_; __ret56775_end_: ;
                                                        }
                                                        ret56774;
                                                        }))
                                                        );
                                                        
                                                    x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                                        
                                                        //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                                        x10aux::ref<x10::array::RectRegion1D> alloc2063156782 =
                                                          
                                                        x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                                        ;
                                                        
                                                        //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                                                        (alloc2063156782)->::x10::array::RectRegion1D::_constructor(
                                                          r56781->
                                                            FMGL(min),
                                                          r56781->
                                                            FMGL(max));
                                                        alloc2063156782;
                                                    }))
                                                    );
                                                }))
                                                );
                                            
                                          
                                          //#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                          ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                            FMGL(cachedAtoms) =
                                            (__extension__ ({
                                              
                                              //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                                              x10aux::ref<x10::array::Dist> dist56790 =
                                                x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
                                                  
                                                  //#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                  x10aux::ref<x10::array::PeriodicDist> alloc47433 =
                                                    
                                                  x10aux::ref<x10::array::PeriodicDist>((new (memset(x10aux::alloc<x10::array::PeriodicDist>(), 0, sizeof(x10::array::PeriodicDist))) x10::array::PeriodicDist()))
                                                  ;
                                                  
                                                  //#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10ConstructorCall_c
                                                  (alloc47433)->::x10::array::PeriodicDist::_constructor(
                                                    (__extension__ ({
                                                        
                                                        //#line 64 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                        x10aux::ref<x10::array::Region> r56783 =
                                                          cachedAtomsRegion;
                                                        x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
                                                            
                                                            //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                            x10aux::ref<x10::array::ConstantDist> alloc2839356784 =
                                                              
                                                            x10aux::ref<x10::array::ConstantDist>((new (memset(x10aux::alloc<x10::array::ConstantDist>(), 0, sizeof(x10::array::ConstantDist))) x10::array::ConstantDist()))
                                                            ;
                                                            
                                                            //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.StmtExpr_c
                                                            (__extension__ ({
                                                                
                                                                //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                                                                x10aux::ref<x10::array::Region> r56785 =
                                                                  r56783;
                                                                
                                                                //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                                                                x10::lang::Place p56786 =
                                                                  x10::lang::Place::_make(x10aux::here);
                                                                {
                                                                    
                                                                    //#line 28 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.StmtExpr_c
                                                                    (__extension__ ({
                                                                        
                                                                        //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                        x10aux::ref<x10::array::Region> region56788 =
                                                                          r56785;
                                                                        {
                                                                            
                                                                            //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10ConstructorCall_c
                                                                            (alloc2839356784)->::x10::lang::Object::_constructor();
                                                                            
                                                                            //#line 669 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10FieldAssign_c
                                                                            x10aux::nullCheck(alloc2839356784)->
                                                                              FMGL(region) =
                                                                              region56788;
                                                                        }
                                                                        
                                                                    }))
                                                                    ;
                                                                    
                                                                    //#line 29 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10FieldAssign_c
                                                                    x10aux::nullCheck(alloc2839356784)->
                                                                      FMGL(onePlace) =
                                                                      p56786;
                                                                }
                                                                
                                                            }))
                                                            ;
                                                            alloc2839356784;
                                                        }))
                                                        );
                                                    }))
                                                    );
                                                  alloc47433;
                                              }))
                                              );
                                              (__extension__ ({
                                                  
                                                  //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                                                  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > alloc3048456791 =
                                                    
                                                  x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >))) x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >()))
                                                  ;
                                                  
                                                  //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
                                                  (alloc3048456791)->::x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::_constructor(
                                                    dist56790);
                                                  alloc3048456791;
                                              }))
                                              ;
                                          }))
                                          ;
                                          
                                        }
                                        x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> au::edu::anu::mm::LocallyEssentialTree::_make(
                                          x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > combinedUList,
                                          x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > combinedVList,
                                          x10aux::ref<x10::array::Array<x10_int> > uListMin,
                                          x10aux::ref<x10::array::Array<x10_int> > uListMax,
                                          x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMin,
                                          x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMax)
                                        {
                                            x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::LocallyEssentialTree>(), 0, sizeof(au::edu::anu::mm::LocallyEssentialTree))) au::edu::anu::mm::LocallyEssentialTree();
                                            this_->_constructor(combinedUList,
                                            combinedVList,
                                            uListMin,
                                            uListMax,
                                            vListMin,
                                            vListMax);
                                            return this_;
                                        }
                                        
                                        
                                        
                                    
                                    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10ConstructorDecl_c
                                    void au::edu::anu::mm::LocallyEssentialTree::_constructor(
                                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > combinedUList,
                                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > combinedVList,
                                      x10aux::ref<x10::array::Array<x10_int> > uListMin,
                                      x10aux::ref<x10::array::Array<x10_int> > uListMax,
                                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMin,
                                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMax,
                                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
                                    {
                                        
                                        //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10ConstructorCall_c
                                        (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
                                        
                                        //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.AssignPropertyCall_c
                                        
                                        //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.StmtExpr_c
                                        (__extension__ ({
                                            
                                            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                            x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> this5679257105 =
                                              ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this);
                                            {
                                                
                                                //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                                x10aux::nullCheck(this5679257105)->
                                                  FMGL(X10__object_lock_id0) =
                                                  ((x10_int)-1);
                                            }
                                            
                                        }))
                                        ;
                                        
                                        //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                        ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                          FMGL(combinedUList) =
                                          combinedUList;
                                        
                                        //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                        ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                          FMGL(combinedVList) =
                                          combinedVList;
                                        
                                        //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                        ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                          FMGL(uListMin) =
                                          uListMin;
                                        
                                        //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                        ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                          FMGL(uListMax) =
                                          uListMax;
                                        
                                        //#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                        ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                          FMGL(vListMin) =
                                          vListMin;
                                        
                                        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                        ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                          FMGL(vListMax) =
                                          vListMax;
                                        
                                        //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                        x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > > > multipoleCopies =
                                          
                                        x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > > >(), 0, sizeof(x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > >))) x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > >()))
                                        ;
                                        
                                        //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.StmtExpr_c
                                        (__extension__ ({
                                            
                                            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10_int size56795 =
                                              x10aux::nullCheck(combinedVList)->
                                                FMGL(size);
                                            {
                                                
                                                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                                                (multipoleCopies)->::x10::lang::Object::_constructor();
                                                
                                                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10aux::ref<x10::array::Region> myReg56796 =
                                                  x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                                    
                                                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10aux::ref<x10::array::RectRegion1D> alloc1996056797 =
                                                      
                                                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                                    ;
                                                    
                                                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                                                    (alloc1996056797)->::x10::array::RectRegion1D::_constructor(
                                                      ((x10_int)0),
                                                      ((x10_int) ((size56795) - (((x10_int)1)))));
                                                    alloc1996056797;
                                                }))
                                                );
                                                
                                                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                multipoleCopies->
                                                  FMGL(region) =
                                                  myReg56796;
                                                
                                                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                multipoleCopies->
                                                  FMGL(rank) =
                                                  ((x10_int)1);
                                                
                                                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                multipoleCopies->
                                                  FMGL(rect) =
                                                  true;
                                                
                                                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                multipoleCopies->
                                                  FMGL(zeroBased) =
                                                  true;
                                                
                                                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                multipoleCopies->
                                                  FMGL(rail) =
                                                  true;
                                                
                                                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                multipoleCopies->
                                                  FMGL(size) =
                                                  size56795;
                                                
                                                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                multipoleCopies->
                                                  FMGL(layout) =
                                                  (__extension__ ({
                                                    
                                                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10::array::RectLayout alloc1996156798 =
                                                      
                                                    x10::array::RectLayout::_alloc();
                                                    
                                                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                                                    (__extension__ ({
                                                        
                                                        //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                        x10_int _max056802 =
                                                          ((x10_int) ((size56795) - (((x10_int)1))));
                                                        {
                                                            
                                                            //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(rank) =
                                                              ((x10_int)1);
                                                            
                                                            //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(min0) =
                                                              ((x10_int)0);
                                                            
                                                            //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(delta0) =
                                                              ((x10_int) ((((x10_int) ((_max056802) - (((x10_int)0))))) + (((x10_int)1))));
                                                            
                                                            //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(size) =
                                                              ((alloc1996156798->
                                                                  FMGL(delta0)) > (((x10_int)0)))
                                                              ? (alloc1996156798->
                                                                   FMGL(delta0))
                                                              : (((x10_int)0));
                                                            
                                                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(min1) =
                                                              ((x10_int)0);
                                                            
                                                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(delta1) =
                                                              ((x10_int)0);
                                                            
                                                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(min2) =
                                                              ((x10_int)0);
                                                            
                                                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(delta2) =
                                                              ((x10_int)0);
                                                            
                                                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(min3) =
                                                              ((x10_int)0);
                                                            
                                                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(delta3) =
                                                              ((x10_int)0);
                                                            
                                                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(min) =
                                                              X10_NULL;
                                                            
                                                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                            alloc1996156798->
                                                              FMGL(delta) =
                                                              X10_NULL;
                                                        }
                                                        
                                                    }))
                                                    ;
                                                    alloc1996156798;
                                                }))
                                                ;
                                                
                                                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                x10_int n56799 =
                                                  (__extension__ ({
                                                    
                                                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                    x10::array::RectLayout this56804 =
                                                      multipoleCopies->
                                                        FMGL(layout);
                                                    this56804->
                                                      FMGL(size);
                                                }))
                                                ;
                                                
                                                //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                multipoleCopies->
                                                  FMGL(raw) =
                                                  x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > >(n56799, 8, false, true);
                                            }
                                            
                                        }))
                                        ;
                                        
                                        //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                        x10_int i47453max4745557107 =
                                          ((x10_int) ((x10aux::nullCheck(combinedVList)->
                                                         FMGL(size)) - (((x10_int)1))));
                                        
                                        //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": polyglot.ast.For_c
                                        {
                                            x10_int i4745357108;
                                            for (
                                                 //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                 i4745357108 =
                                                   ((x10_int)0);
                                                 ((i4745357108) <= (i47453max4745557107));
                                                 
                                                 //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalAssign_c
                                                 i4745357108 =
                                                   ((x10_int) ((i4745357108) + (((x10_int)1)))))
                                            {
                                                
                                                //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                x10_int i57109 =
                                                  i4745357108;
                                                
                                                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10If_c
                                                if ((!x10aux::struct_equals((__extension__ ({
                                                                                
                                                                                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                x10_int i05680557052 =
                                                                                  i57109;
                                                                                
                                                                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > ret5680657053;
                                                                                
                                                                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                goto __ret5680757054; __ret5680757054: {
                                                                                {
                                                                                    
                                                                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                    ret5680657053 =
                                                                                      (x10aux::nullCheck(combinedVList)->
                                                                                         FMGL(raw))->__apply(i05680557052);
                                                                                    
                                                                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                    goto __ret5680757054_end_;
                                                                                }goto __ret5680757054_end_; __ret5680757054_end_: ;
                                                                                }
                                                                                ret5680657053;
                                                                                }))
                                                                                ,
                                                                                X10_NULL)))
                                                    {
                                                        
                                                        //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                        x10aux::ref<x10::array::Region> multipoleCopiesLevelRegion57055 =
                                                          x10aux::nullCheck(x10::lang::IntRange::_make((__extension__ ({
                                                              
                                                              //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                              x10aux::ref<x10::array::Array<x10_int> > this5682257056 =
                                                                (__extension__ ({
                                                                  
                                                                  //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                  x10_int i05681357057 =
                                                                    i57109;
                                                                  
                                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                  x10aux::ref<x10::array::Array<x10_int> > ret5681457058;
                                                                  
                                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                  goto __ret5681557059; __ret5681557059: {
                                                                  {
                                                                      
                                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                      ret5681457058 =
                                                                        (x10aux::nullCheck(vListMin)->
                                                                           FMGL(raw))->__apply(i05681357057);
                                                                      
                                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                      goto __ret5681557059_end_;
                                                                  }goto __ret5681557059_end_; __ret5681557059_end_: ;
                                                                  }
                                                                  ret5681457058;
                                                                  }))
                                                                  ;
                                                                  
                                                              
                                                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                              x10_int ret5682357060;
                                                              
                                                              //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                              goto __ret5682457061; __ret5682457061: {
                                                              {
                                                                  
                                                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                  ret5682357060 =
                                                                    (x10aux::nullCheck(this5682257056)->
                                                                       FMGL(raw))->__apply(((x10_int)0));
                                                                  
                                                                  //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                  goto __ret5682457061_end_;
                                                              }goto __ret5682457061_end_; __ret5682457061_end_: ;
                                                              }
                                                              ret5682357060;
                                                              }))
                                                              , (__extension__ ({
                                                                  
                                                                  //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                                  x10aux::ref<x10::array::Array<x10_int> > this5683957062 =
                                                                    (__extension__ ({
                                                                      
                                                                      //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                      x10_int i05683057063 =
                                                                        i57109;
                                                                      
                                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                      x10aux::ref<x10::array::Array<x10_int> > ret5683157064;
                                                                      
                                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                      goto __ret5683257065; __ret5683257065: {
                                                                      {
                                                                          
                                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                          ret5683157064 =
                                                                            (x10aux::nullCheck(vListMax)->
                                                                               FMGL(raw))->__apply(i05683057063);
                                                                          
                                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                          goto __ret5683257065_end_;
                                                                      }goto __ret5683257065_end_; __ret5683257065_end_: ;
                                                                      }
                                                                      ret5683157064;
                                                                      }))
                                                                      ;
                                                                      
                                                                  
                                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                  x10_int ret5684057066;
                                                                  
                                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                  goto __ret5684157067; __ret5684157067: {
                                                                  {
                                                                      
                                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                      ret5684057066 =
                                                                        (x10aux::nullCheck(this5683957062)->
                                                                           FMGL(raw))->__apply(((x10_int)0));
                                                                      
                                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                      goto __ret5684157067_end_;
                                                                  }goto __ret5684157067_end_; __ret5684157067_end_: ;
                                                                  }
                                                                  ret5684057066;
                                                                  }))
                                                                  ))->x10::lang::IntRange::__times(
                                                                    x10::lang::IntRange::_make((__extension__ ({
                                                                        
                                                                        //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                                        x10aux::ref<x10::array::Array<x10_int> > this5685657068 =
                                                                          (__extension__ ({
                                                                            
                                                                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                            x10_int i05684757069 =
                                                                              i57109;
                                                                            
                                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                            x10aux::ref<x10::array::Array<x10_int> > ret5684857070;
                                                                            
                                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                            goto __ret5684957071; __ret5684957071: {
                                                                            {
                                                                                
                                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                ret5684857070 =
                                                                                  (x10aux::nullCheck(vListMin)->
                                                                                     FMGL(raw))->__apply(i05684757069);
                                                                                
                                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                goto __ret5684957071_end_;
                                                                            }goto __ret5684957071_end_; __ret5684957071_end_: ;
                                                                            }
                                                                            ret5684857070;
                                                                            }))
                                                                            ;
                                                                            
                                                                        
                                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                        x10_int ret5685757072;
                                                                        
                                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                        goto __ret5685857073; __ret5685857073: {
                                                                        {
                                                                            
                                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                            ret5685757072 =
                                                                              (x10aux::nullCheck(this5685657068)->
                                                                                 FMGL(raw))->__apply(((x10_int)1));
                                                                            
                                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                            goto __ret5685857073_end_;
                                                                        }goto __ret5685857073_end_; __ret5685857073_end_: ;
                                                                        }
                                                                        ret5685757072;
                                                                        }))
                                                                        , (__extension__ ({
                                                                            
                                                                            //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                                            x10aux::ref<x10::array::Array<x10_int> > this5687357074 =
                                                                              (__extension__ ({
                                                                                
                                                                                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                x10_int i05686457075 =
                                                                                  i57109;
                                                                                
                                                                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                x10aux::ref<x10::array::Array<x10_int> > ret5686557076;
                                                                                
                                                                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                goto __ret5686657077; __ret5686657077: {
                                                                                {
                                                                                    
                                                                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                    ret5686557076 =
                                                                                      (x10aux::nullCheck(vListMax)->
                                                                                         FMGL(raw))->__apply(i05686457075);
                                                                                    
                                                                                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                    goto __ret5686657077_end_;
                                                                                }goto __ret5686657077_end_; __ret5686657077_end_: ;
                                                                                }
                                                                                ret5686557076;
                                                                                }))
                                                                                ;
                                                                                
                                                                            
                                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                            x10_int ret5687457078;
                                                                            
                                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                            goto __ret5687557079; __ret5687557079: {
                                                                            {
                                                                                
                                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                ret5687457078 =
                                                                                  (x10aux::nullCheck(this5687357074)->
                                                                                     FMGL(raw))->__apply(((x10_int)1));
                                                                                
                                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                goto __ret5687557079_end_;
                                                                            }goto __ret5687557079_end_; __ret5687557079_end_: ;
                                                                            }
                                                                            ret5687457078;
                                                                            }))
                                                                            ))->__times(
                                                                          (__extension__ ({
                                                                              
                                                                              //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                                                              x10::lang::IntRange r5691557080 =
                                                                                x10::lang::IntRange::_make((__extension__ ({
                                                                                  
                                                                                  //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                                                  x10aux::ref<x10::array::Array<x10_int> > this5689057081 =
                                                                                    (__extension__ ({
                                                                                      
                                                                                      //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                      x10_int i05688157082 =
                                                                                        i57109;
                                                                                      
                                                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                      x10aux::ref<x10::array::Array<x10_int> > ret5688257083;
                                                                                      
                                                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                      goto __ret5688357084; __ret5688357084: {
                                                                                      {
                                                                                          
                                                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                          ret5688257083 =
                                                                                            (x10aux::nullCheck(vListMin)->
                                                                                               FMGL(raw))->__apply(i05688157082);
                                                                                          
                                                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                          goto __ret5688357084_end_;
                                                                                      }goto __ret5688357084_end_; __ret5688357084_end_: ;
                                                                                      }
                                                                                      ret5688257083;
                                                                                      }))
                                                                                      ;
                                                                                      
                                                                                  
                                                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                  x10_int ret5689157085;
                                                                                  
                                                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                  goto __ret5689257086; __ret5689257086: {
                                                                                  {
                                                                                      
                                                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                      ret5689157085 =
                                                                                        (x10aux::nullCheck(this5689057081)->
                                                                                           FMGL(raw))->__apply(((x10_int)2));
                                                                                      
                                                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                      goto __ret5689257086_end_;
                                                                                  }goto __ret5689257086_end_; __ret5689257086_end_: ;
                                                                                  }
                                                                                  ret5689157085;
                                                                                  }))
                                                                                  , (__extension__ ({
                                                                                      
                                                                                      //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                                                      x10aux::ref<x10::array::Array<x10_int> > this5690757087 =
                                                                                        (__extension__ ({
                                                                                          
                                                                                          //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                          x10_int i05689857088 =
                                                                                            i57109;
                                                                                          
                                                                                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                          x10aux::ref<x10::array::Array<x10_int> > ret5689957089;
                                                                                          
                                                                                          //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                          goto __ret5690057090; __ret5690057090: {
                                                                                          {
                                                                                              
                                                                                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                              ret5689957089 =
                                                                                                (x10aux::nullCheck(vListMax)->
                                                                                                   FMGL(raw))->__apply(i05689857088);
                                                                                              
                                                                                              //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                              goto __ret5690057090_end_;
                                                                                          }goto __ret5690057090_end_; __ret5690057090_end_: ;
                                                                                          }
                                                                                          ret5689957089;
                                                                                          }))
                                                                                          ;
                                                                                          
                                                                                      
                                                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                      x10_int ret5690857091;
                                                                                      
                                                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                      goto __ret5690957092; __ret5690957092: {
                                                                                      {
                                                                                          
                                                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                          ret5690857091 =
                                                                                            (x10aux::nullCheck(this5690757087)->
                                                                                               FMGL(raw))->__apply(((x10_int)2));
                                                                                          
                                                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                          goto __ret5690957092_end_;
                                                                                      }goto __ret5690957092_end_; __ret5690957092_end_: ;
                                                                                      }
                                                                                      ret5690857091;
                                                                                      }))
                                                                                      );
                                                                                      x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                                                                          
                                                                                          //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                                                                          x10aux::ref<x10::array::RectRegion1D> alloc206315691657093 =
                                                                                            
                                                                                          x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                                                                          ;
                                                                                          
                                                                                          //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                                                                                          (alloc206315691657093)->::x10::array::RectRegion1D::_constructor(
                                                                                            r5691557080->
                                                                                              FMGL(min),
                                                                                            r5691557080->
                                                                                              FMGL(max));
                                                                                          alloc206315691657093;
                                                                                      }))
                                                                                      );
                                                                                  }))
                                                                                  );
                                                                              
                                                                              //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.StmtExpr_c
                                                                              (__extension__ ({
                                                                                  
                                                                                  //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                  x10_int i05692657094 =
                                                                                    i57109;
                                                                                  
                                                                                  //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                  x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > v5692757095 =
                                                                                    (__extension__ ({
                                                                                      
                                                                                      //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                                                                                      x10aux::ref<x10::array::Dist> dist5692457096 =
                                                                                        x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
                                                                                          
                                                                                          //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                                                          x10aux::ref<x10::array::PeriodicDist> alloc4743457097 =
                                                                                            
                                                                                          x10aux::ref<x10::array::PeriodicDist>((new (memset(x10aux::alloc<x10::array::PeriodicDist>(), 0, sizeof(x10::array::PeriodicDist))) x10::array::PeriodicDist()))
                                                                                          ;
                                                                                          
                                                                                          //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10ConstructorCall_c
                                                                                          (alloc4743457097)->::x10::array::PeriodicDist::_constructor(
                                                                                            (__extension__ ({
                                                                                                
                                                                                                //#line 64 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                                                x10aux::ref<x10::array::Region> r5691757098 =
                                                                                                  multipoleCopiesLevelRegion56997;
                                                                                                x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
                                                                                                    
                                                                                                    //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                                                    x10aux::ref<x10::array::ConstantDist> alloc283935691857099 =
                                                                                                      
                                                                                                    x10aux::ref<x10::array::ConstantDist>((new (memset(x10aux::alloc<x10::array::ConstantDist>(), 0, sizeof(x10::array::ConstantDist))) x10::array::ConstantDist()))
                                                                                                    ;
                                                                                                    
                                                                                                    //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.StmtExpr_c
                                                                                                    (__extension__ ({
                                                                                                        
                                                                                                        //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                                                                                                        x10aux::ref<x10::array::Region> r5691957100 =
                                                                                                          r5691757098;
                                                                                                        
                                                                                                        //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                                                                                                        x10::lang::Place p5692057101 =
                                                                                                          x10::lang::Place::_make(x10aux::here);
                                                                                                        {
                                                                                                            
                                                                                                            //#line 28 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.StmtExpr_c
                                                                                                            (__extension__ ({
                                                                                                                
                                                                                                                //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                                                                x10aux::ref<x10::array::Region> region5692257102 =
                                                                                                                  r5691957100;
                                                                                                                {
                                                                                                                    
                                                                                                                    //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10ConstructorCall_c
                                                                                                                    (alloc283935691857099)->::x10::lang::Object::_constructor();
                                                                                                                    
                                                                                                                    //#line 669 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10FieldAssign_c
                                                                                                                    x10aux::nullCheck(alloc283935691857099)->
                                                                                                                      FMGL(region) =
                                                                                                                      region5692257102;
                                                                                                                }
                                                                                                                
                                                                                                            }))
                                                                                                            ;
                                                                                                            
                                                                                                            //#line 29 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10FieldAssign_c
                                                                                                            x10aux::nullCheck(alloc283935691857099)->
                                                                                                              FMGL(onePlace) =
                                                                                                              p5692057101;
                                                                                                        }
                                                                                                        
                                                                                                    }))
                                                                                                    ;
                                                                                                    alloc283935691857099;
                                                                                                }))
                                                                                                );
                                                                                            }))
                                                                                            );
                                                                                          alloc4743457097;
                                                                                      }))
                                                                                      );
                                                                                      (__extension__ ({
                                                                                          
                                                                                          //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                                                                                          x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > alloc304845692557103 =
                                                                                            
                                                                                          x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >))) x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >()))
                                                                                          ;
                                                                                          
                                                                                          //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
                                                                                          (alloc304845692557103)->::x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> >::_constructor(
                                                                                            dist5692457096);
                                                                                          alloc304845692557103;
                                                                                      }))
                                                                                      ;
                                                                                  }))
                                                                                  ;
                                                                                  
                                                                                  //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                  x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > ret5692857104;
                                                                                  {
                                                                                      
                                                                                      //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                                                                      (multipoleCopies->
                                                                                         FMGL(raw))->__set(i05692657094, v5692757095);
                                                                                      
                                                                                      //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                      ret5692857104 =
                                                                                        v5692757095;
                                                                                  }
                                                                                  ret5692857104;
                                                                              }))
                                                                              ;
                                                                          }
                                                                          
                                                                        }
                                                                    }
                                                                    
                                                                  
                                                                  //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                                                  ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                                                    FMGL(multipoleCopies) =
                                                                    multipoleCopies;
                                                                  
                                                                  //#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                                  x10aux::ref<x10::array::Region> cachedAtomsRegion =
                                                                    x10aux::nullCheck(x10::lang::IntRange::_make((__extension__ ({
                                                                        
                                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                        x10_int ret56936;
                                                                        
                                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                        goto __ret56937; __ret56937: {
                                                                        {
                                                                            
                                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                            ret56936 =
                                                                              (x10aux::nullCheck(uListMin)->
                                                                                 FMGL(raw))->__apply(((x10_int)0));
                                                                            
                                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                            goto __ret56937_end_;
                                                                        }goto __ret56937_end_; __ret56937_end_: ;
                                                                        }
                                                                        ret56936;
                                                                        }))
                                                                        , (__extension__ ({
                                                                            
                                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                            x10_int ret56944;
                                                                            
                                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                            goto __ret56945; __ret56945: {
                                                                            {
                                                                                
                                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                ret56944 =
                                                                                  (x10aux::nullCheck(uListMax)->
                                                                                     FMGL(raw))->__apply(((x10_int)0));
                                                                                
                                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                goto __ret56945_end_;
                                                                            }goto __ret56945_end_; __ret56945_end_: ;
                                                                            }
                                                                            ret56944;
                                                                            }))
                                                                            ))->x10::lang::IntRange::__times(
                                                                              x10::lang::IntRange::_make((__extension__ ({
                                                                                  
                                                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                  x10_int ret56952;
                                                                                  
                                                                                  //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                  goto __ret56953; __ret56953: {
                                                                                  {
                                                                                      
                                                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                      ret56952 =
                                                                                        (x10aux::nullCheck(uListMin)->
                                                                                           FMGL(raw))->__apply(((x10_int)1));
                                                                                      
                                                                                      //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                      goto __ret56953_end_;
                                                                                  }goto __ret56953_end_; __ret56953_end_: ;
                                                                                  }
                                                                                  ret56952;
                                                                                  }))
                                                                                  , (__extension__ ({
                                                                                      
                                                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                      x10_int ret56960;
                                                                                      
                                                                                      //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                      goto __ret56961; __ret56961: {
                                                                                      {
                                                                                          
                                                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                          ret56960 =
                                                                                            (x10aux::nullCheck(uListMax)->
                                                                                               FMGL(raw))->__apply(((x10_int)1));
                                                                                          
                                                                                          //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                          goto __ret56961_end_;
                                                                                      }goto __ret56961_end_; __ret56961_end_: ;
                                                                                      }
                                                                                      ret56960;
                                                                                      }))
                                                                                      ))->__times(
                                                                                (__extension__ ({
                                                                                    
                                                                                    //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                                                                    x10::lang::IntRange r56983 =
                                                                                      x10::lang::IntRange::_make((__extension__ ({
                                                                                        
                                                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                        x10_int ret56968;
                                                                                        
                                                                                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                        goto __ret56969; __ret56969: {
                                                                                        {
                                                                                            
                                                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                            ret56968 =
                                                                                              (x10aux::nullCheck(uListMin)->
                                                                                                 FMGL(raw))->__apply(((x10_int)2));
                                                                                            
                                                                                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                            goto __ret56969_end_;
                                                                                        }goto __ret56969_end_; __ret56969_end_: ;
                                                                                        }
                                                                                        ret56968;
                                                                                        }))
                                                                                        , (__extension__ ({
                                                                                            
                                                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                            x10_int ret56976;
                                                                                            
                                                                                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                                                                            goto __ret56977; __ret56977: {
                                                                                            {
                                                                                                
                                                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                                ret56976 =
                                                                                                  (x10aux::nullCheck(uListMax)->
                                                                                                     FMGL(raw))->__apply(((x10_int)2));
                                                                                                
                                                                                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                                                                                goto __ret56977_end_;
                                                                                            }goto __ret56977_end_; __ret56977_end_: ;
                                                                                            }
                                                                                            ret56976;
                                                                                            }))
                                                                                            );
                                                                                            
                                                                                        x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                                                                            
                                                                                            //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                                                                                            x10aux::ref<x10::array::RectRegion1D> alloc2063156984 =
                                                                                              
                                                                                            x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                                                                            ;
                                                                                            
                                                                                            //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                                                                                            (alloc2063156984)->::x10::array::RectRegion1D::_constructor(
                                                                                              r56983->
                                                                                                FMGL(min),
                                                                                              r56983->
                                                                                                FMGL(max));
                                                                                            alloc2063156984;
                                                                                        }))
                                                                                        );
                                                                                    }))
                                                                                    );
                                                                                
                                                                              
                                                                              //#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                                                              ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                                                                FMGL(cachedAtoms) =
                                                                                (__extension__ ({
                                                                                  
                                                                                  //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                                                                                  x10aux::ref<x10::array::Dist> dist56992 =
                                                                                    x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
                                                                                      
                                                                                      //#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10LocalDecl_c
                                                                                      x10aux::ref<x10::array::PeriodicDist> alloc47435 =
                                                                                        
                                                                                      x10aux::ref<x10::array::PeriodicDist>((new (memset(x10aux::alloc<x10::array::PeriodicDist>(), 0, sizeof(x10::array::PeriodicDist))) x10::array::PeriodicDist()))
                                                                                      ;
                                                                                      
                                                                                      //#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10ConstructorCall_c
                                                                                      (alloc47435)->::x10::array::PeriodicDist::_constructor(
                                                                                        (__extension__ ({
                                                                                            
                                                                                            //#line 64 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                                            x10aux::ref<x10::array::Region> r56985 =
                                                                                              cachedAtomsRegion;
                                                                                            x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
                                                                                                
                                                                                                //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                                                x10aux::ref<x10::array::ConstantDist> alloc2839356986 =
                                                                                                  
                                                                                                x10aux::ref<x10::array::ConstantDist>((new (memset(x10aux::alloc<x10::array::ConstantDist>(), 0, sizeof(x10::array::ConstantDist))) x10::array::ConstantDist()))
                                                                                                ;
                                                                                                
                                                                                                //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.StmtExpr_c
                                                                                                (__extension__ ({
                                                                                                    
                                                                                                    //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                                                                                                    x10aux::ref<x10::array::Region> r56987 =
                                                                                                      r56985;
                                                                                                    
                                                                                                    //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                                                                                                    x10::lang::Place p56988 =
                                                                                                      x10::lang::Place::_make(x10aux::here);
                                                                                                    {
                                                                                                        
                                                                                                        //#line 28 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.StmtExpr_c
                                                                                                        (__extension__ ({
                                                                                                            
                                                                                                            //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                                                                                                            x10aux::ref<x10::array::Region> region56990 =
                                                                                                              r56987;
                                                                                                            {
                                                                                                                
                                                                                                                //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10ConstructorCall_c
                                                                                                                (alloc2839356986)->::x10::lang::Object::_constructor();
                                                                                                                
                                                                                                                //#line 669 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10FieldAssign_c
                                                                                                                x10aux::nullCheck(alloc2839356986)->
                                                                                                                  FMGL(region) =
                                                                                                                  region56990;
                                                                                                            }
                                                                                                            
                                                                                                        }))
                                                                                                        ;
                                                                                                        
                                                                                                        //#line 29 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10FieldAssign_c
                                                                                                        x10aux::nullCheck(alloc2839356986)->
                                                                                                          FMGL(onePlace) =
                                                                                                          p56988;
                                                                                                    }
                                                                                                    
                                                                                                }))
                                                                                                ;
                                                                                                alloc2839356986;
                                                                                            }))
                                                                                            );
                                                                                        }))
                                                                                        );
                                                                                      alloc47435;
                                                                                  }))
                                                                                  );
                                                                                  (__extension__ ({
                                                                                      
                                                                                      //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10LocalDecl_c
                                                                                      x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > alloc3048456993 =
                                                                                        
                                                                                      x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >((new (memset(x10aux::alloc<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >(), 0, sizeof(x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >))) x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >()))
                                                                                      ;
                                                                                      
                                                                                      //#line 109 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10": x10.ast.X10ConstructorCall_c
                                                                                      (alloc3048456993)->::x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > >::_constructor(
                                                                                        dist56992);
                                                                                      alloc3048456993;
                                                                                  }))
                                                                                  ;
                                                                              }))
                                                                              ;
                                                                              
                                                                              //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                                                              ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                                                                FMGL(X10__object_lock_id0) =
                                                                                x10aux::nullCheck(paramLock)->getIndex();
                                                                              
                                                                            }
                                                                            x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> au::edu::anu::mm::LocallyEssentialTree::_make(
                                                                              x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > combinedUList,
                                                                              x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > combinedVList,
                                                                              x10aux::ref<x10::array::Array<x10_int> > uListMin,
                                                                              x10aux::ref<x10::array::Array<x10_int> > uListMax,
                                                                              x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMin,
                                                                              x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMax,
                                                                              x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
                                                                            {
                                                                                x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::LocallyEssentialTree>(), 0, sizeof(au::edu::anu::mm::LocallyEssentialTree))) au::edu::anu::mm::LocallyEssentialTree();
                                                                                this_->_constructor(combinedUList,
                                                                                combinedVList,
                                                                                uListMin,
                                                                                uListMax,
                                                                                vListMin,
                                                                                vListMax,
                                                                                paramLock);
                                                                                return this_;
                                                                            }
                                                                            
                                                                            
                                                                            
                                                                        
                                                                        //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10MethodDecl_c
                                                                        x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>
                                                                          au::edu::anu::mm::LocallyEssentialTree::au__edu__anu__mm__LocallyEssentialTree____au__edu__anu__mm__LocallyEssentialTree__this(
                                                                          ) {
                                                                            
                                                                            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10Return_c
                                                                            return ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this);
                                                                            
                                                                        }
                                                                        
                                                                        //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10MethodDecl_c
                                                                        void
                                                                          au::edu::anu::mm::LocallyEssentialTree::__fieldInitializers47383(
                                                                          ) {
                                                                            
                                                                            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10": x10.ast.X10FieldAssign_c
                                                                            ((x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>)this)->
                                                                              FMGL(X10__object_lock_id0) =
                                                                              ((x10_int)-1);
                                                                        }
                                                                        const x10aux::serialization_id_t au::edu::anu::mm::LocallyEssentialTree::_serialization_id = 
                                                                            x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::LocallyEssentialTree::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);
                                                                        
                                                                        void au::edu::anu::mm::LocallyEssentialTree::_serialize_body(x10aux::serialization_buffer& buf) {
                                                                            x10::lang::Object::_serialize_body(buf);
                                                                            buf.write(this->FMGL(combinedUList));
                                                                            buf.write(this->FMGL(combinedVList));
                                                                            buf.write(this->FMGL(uListMin));
                                                                            buf.write(this->FMGL(uListMax));
                                                                            buf.write(this->FMGL(vListMin));
                                                                            buf.write(this->FMGL(vListMax));
                                                                            buf.write(this->FMGL(multipoleCopies));
                                                                            buf.write(this->FMGL(cachedAtoms));
                                                                            
                                                                        }
                                                                        
                                                                        x10aux::ref<x10::lang::Reference> au::edu::anu::mm::LocallyEssentialTree::_deserializer(x10aux::deserialization_buffer& buf) {
                                                                            x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::LocallyEssentialTree>(), 0, sizeof(au::edu::anu::mm::LocallyEssentialTree))) au::edu::anu::mm::LocallyEssentialTree();
                                                                            buf.record_reference(this_);
                                                                            this_->_deserialize_body(buf);
                                                                            return this_;
                                                                        }
                                                                        
                                                                        void au::edu::anu::mm::LocallyEssentialTree::_deserialize_body(x10aux::deserialization_buffer& buf) {
                                                                            x10::lang::Object::_deserialize_body(buf);
                                                                            FMGL(combinedUList) = buf.read<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > >();
                                                                            FMGL(combinedVList) = buf.read<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > >();
                                                                            FMGL(uListMin) = buf.read<x10aux::ref<x10::array::Array<x10_int> > >();
                                                                            FMGL(uListMax) = buf.read<x10aux::ref<x10::array::Array<x10_int> > >();
                                                                            FMGL(vListMin) = buf.read<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > >();
                                                                            FMGL(vListMax) = buf.read<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > >();
                                                                            FMGL(multipoleCopies) = buf.read<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > > > >();
                                                                            FMGL(cachedAtoms) = buf.read<x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > >();
                                                                        }
                                                                        
                                                                        
                                                                    x10aux::RuntimeType au::edu::anu::mm::LocallyEssentialTree::rtt;
                                                                    void au::edu::anu::mm::LocallyEssentialTree::_initRTT() {
                                                                        if (rtt.initStageOne(&rtt)) return;
                                                                        const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
                                                                        rtt.initStageTwo("au.edu.anu.mm.LocallyEssentialTree",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
                                                                    }
                                                                    /* END of LocallyEssentialTree */
/*************************************************/
