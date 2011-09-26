/*************************************************/
/* START of Timer */
#include <au/edu/anu/util/Timer.h>

#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/array/Array.h>
#include <x10/lang/Long.h>
#include <x10/array/Region.h>
#include <x10/array/RectRegion1D.h>
#include <x10/array/RectLayout.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/util/Timer.h>

//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldDecl_c

//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::util::Timer::getOrderedLock(
  ) {
    
    //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::util::Timer>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::util::Timer::FMGL(X10__class_lock_id1);
void au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::util::Timer.X10$class_lock_id1");
    x10_int __var107__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var107__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var108__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var108__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::util::Timer.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::util::Timer.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__109 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__deserialize));


//#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::util::Timer::getStaticOrderedLock(
  ) {
    
    //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId34385 = au::edu::anu::util::Timer::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId34385);
    }))
    ;
    
}

//#line 6 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldDecl_c

//#line 7 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldDecl_c

//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::util::Timer::_constructor(
  x10_int n) {
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.AssignPropertyCall_c
    
    //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::util::Timer> this3438634566 =
          ((x10aux::ref<au::edu::anu::util::Timer>)this);
        {
            
            //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this3438634566)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::util::Timer>)this)->
      FMGL(total) = (__extension__ ({
        
        //#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_long> > alloc31548 =
          
        x10aux::ref<x10::array::Array<x10_long> >((new (memset(x10aux::alloc<x10::array::Array<x10_long> >(), 0, sizeof(x10::array::Array<x10_long>))) x10::array::Array<x10_long>()))
        ;
        
        //#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int size34389 = n;
            {
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc31548)->::x10::lang::Object::_constructor();
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Region> myReg34390 =
                  x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc1996034391 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1996034391)->::x10::array::RectRegion1D::_constructor(
                      ((x10_int)0),
                      ((x10_int) ((size34389) - (((x10_int)1)))));
                    alloc1996034391;
                }))
                );
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31548)->
                  FMGL(region) = myReg34390;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31548)->
                  FMGL(rank) = ((x10_int)1);
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31548)->
                  FMGL(rect) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31548)->
                  FMGL(zeroBased) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31548)->
                  FMGL(rail) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31548)->
                  FMGL(size) = size34389;
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31548)->
                  FMGL(layout) = (__extension__ ({
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1996134392 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int _max034396 =
                          ((x10_int) ((size34389) - (((x10_int)1))));
                        {
                            
                            //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(rank) =
                              ((x10_int)1);
                            
                            //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(min0) =
                              ((x10_int)0);
                            
                            //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(delta0) =
                              ((x10_int) ((((x10_int) ((_max034396) - (((x10_int)0))))) + (((x10_int)1))));
                            
                            //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(size) =
                              ((alloc1996134392->
                                  FMGL(delta0)) > (((x10_int)0)))
                              ? (alloc1996134392->
                                   FMGL(delta0))
                              : (((x10_int)0));
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(min1) =
                              ((x10_int)0);
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(delta1) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(min2) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(delta2) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(min3) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(delta3) =
                              ((x10_int)0);
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(min) =
                              X10_NULL;
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134392->
                              FMGL(delta) =
                              X10_NULL;
                        }
                        
                    }))
                    ;
                    alloc1996134392;
                }))
                ;
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n34393 = (__extension__ ({
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this34398 =
                      x10aux::nullCheck(alloc31548)->
                        FMGL(layout);
                    this34398->FMGL(size);
                }))
                ;
                
                //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31548)->
                  FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10_long >(n34393, 8, false, true);
            }
            
        }))
        ;
        alloc31548;
    }))
    ;
    
    //#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::util::Timer>)this)->
      FMGL(count) = (__extension__ ({
        
        //#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_long> > alloc31549 =
          
        x10aux::ref<x10::array::Array<x10_long> >((new (memset(x10aux::alloc<x10::array::Array<x10_long> >(), 0, sizeof(x10::array::Array<x10_long>))) x10::array::Array<x10_long>()))
        ;
        
        //#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int size34399 = n;
            {
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc31549)->::x10::lang::Object::_constructor();
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Region> myReg34400 =
                  x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc1996034401 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1996034401)->::x10::array::RectRegion1D::_constructor(
                      ((x10_int)0),
                      ((x10_int) ((size34399) - (((x10_int)1)))));
                    alloc1996034401;
                }))
                );
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31549)->
                  FMGL(region) = myReg34400;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31549)->
                  FMGL(rank) = ((x10_int)1);
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31549)->
                  FMGL(rect) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31549)->
                  FMGL(zeroBased) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31549)->
                  FMGL(rail) = true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31549)->
                  FMGL(size) = size34399;
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31549)->
                  FMGL(layout) = (__extension__ ({
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1996134402 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int _max034406 =
                          ((x10_int) ((size34399) - (((x10_int)1))));
                        {
                            
                            //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(rank) =
                              ((x10_int)1);
                            
                            //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(min0) =
                              ((x10_int)0);
                            
                            //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(delta0) =
                              ((x10_int) ((((x10_int) ((_max034406) - (((x10_int)0))))) + (((x10_int)1))));
                            
                            //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(size) =
                              ((alloc1996134402->
                                  FMGL(delta0)) > (((x10_int)0)))
                              ? (alloc1996134402->
                                   FMGL(delta0))
                              : (((x10_int)0));
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(min1) =
                              ((x10_int)0);
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(delta1) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(min2) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(delta2) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(min3) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(delta3) =
                              ((x10_int)0);
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(min) =
                              X10_NULL;
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134402->
                              FMGL(delta) =
                              X10_NULL;
                        }
                        
                    }))
                    ;
                    alloc1996134402;
                }))
                ;
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n34403 = (__extension__ ({
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this34408 =
                      x10aux::nullCheck(alloc31549)->
                        FMGL(layout);
                    this34408->FMGL(size);
                }))
                ;
                
                //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31549)->
                  FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10_long >(n34403, 8, false, true);
            }
            
        }))
        ;
        alloc31549;
    }))
    ;
    
}
x10aux::ref<au::edu::anu::util::Timer> au::edu::anu::util::Timer::_make(
  x10_int n) {
    x10aux::ref<au::edu::anu::util::Timer> this_ = new (memset(x10aux::alloc<au::edu::anu::util::Timer>(), 0, sizeof(au::edu::anu::util::Timer))) au::edu::anu::util::Timer();
    this_->_constructor(n);
    return this_;
}



//#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::util::Timer::_constructor(
  x10_int n,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.AssignPropertyCall_c
    
    //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::util::Timer> this3440934567 =
          ((x10aux::ref<au::edu::anu::util::Timer>)this);
        {
            
            //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this3440934567)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::util::Timer>)this)->
      FMGL(total) =
      (__extension__ ({
        
        //#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_long> > alloc31550 =
          
        x10aux::ref<x10::array::Array<x10_long> >((new (memset(x10aux::alloc<x10::array::Array<x10_long> >(), 0, sizeof(x10::array::Array<x10_long>))) x10::array::Array<x10_long>()))
        ;
        
        //#line 10 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int size34412 =
              n;
            {
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc31550)->::x10::lang::Object::_constructor();
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Region> myReg34413 =
                  x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc1996034414 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1996034414)->::x10::array::RectRegion1D::_constructor(
                      ((x10_int)0),
                      ((x10_int) ((size34412) - (((x10_int)1)))));
                    alloc1996034414;
                }))
                );
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31550)->
                  FMGL(region) =
                  myReg34413;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31550)->
                  FMGL(rank) =
                  ((x10_int)1);
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31550)->
                  FMGL(rect) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31550)->
                  FMGL(zeroBased) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31550)->
                  FMGL(rail) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31550)->
                  FMGL(size) =
                  size34412;
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31550)->
                  FMGL(layout) =
                  (__extension__ ({
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1996134415 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int _max034419 =
                          ((x10_int) ((size34412) - (((x10_int)1))));
                        {
                            
                            //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(rank) =
                              ((x10_int)1);
                            
                            //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(min0) =
                              ((x10_int)0);
                            
                            //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(delta0) =
                              ((x10_int) ((((x10_int) ((_max034419) - (((x10_int)0))))) + (((x10_int)1))));
                            
                            //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(size) =
                              ((alloc1996134415->
                                  FMGL(delta0)) > (((x10_int)0)))
                              ? (alloc1996134415->
                                   FMGL(delta0))
                              : (((x10_int)0));
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(min1) =
                              ((x10_int)0);
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(delta1) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(min2) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(delta2) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(min3) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(delta3) =
                              ((x10_int)0);
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(min) =
                              X10_NULL;
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134415->
                              FMGL(delta) =
                              X10_NULL;
                        }
                        
                    }))
                    ;
                    alloc1996134415;
                }))
                ;
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n34416 =
                  (__extension__ ({
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this34421 =
                      x10aux::nullCheck(alloc31550)->
                        FMGL(layout);
                    this34421->
                      FMGL(size);
                }))
                ;
                
                //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31550)->
                  FMGL(raw) =
                  x10::util::IndexedMemoryChunk<void>::allocate<x10_long >(n34416, 8, false, true);
            }
            
        }))
        ;
        alloc31550;
    }))
    ;
    
    //#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::util::Timer>)this)->
      FMGL(count) =
      (__extension__ ({
        
        //#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_long> > alloc31551 =
          
        x10aux::ref<x10::array::Array<x10_long> >((new (memset(x10aux::alloc<x10::array::Array<x10_long> >(), 0, sizeof(x10::array::Array<x10_long>))) x10::array::Array<x10_long>()))
        ;
        
        //#line 11 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int size34422 =
              n;
            {
                
                //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc31551)->::x10::lang::Object::_constructor();
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Region> myReg34423 =
                  x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc1996034424 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                    (alloc1996034424)->::x10::array::RectRegion1D::_constructor(
                      ((x10_int)0),
                      ((x10_int) ((size34422) - (((x10_int)1)))));
                    alloc1996034424;
                }))
                );
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31551)->
                  FMGL(region) =
                  myReg34423;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31551)->
                  FMGL(rank) =
                  ((x10_int)1);
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31551)->
                  FMGL(rect) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31551)->
                  FMGL(zeroBased) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31551)->
                  FMGL(rail) =
                  true;
                
                //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31551)->
                  FMGL(size) =
                  size34422;
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31551)->
                  FMGL(layout) =
                  (__extension__ ({
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout alloc1996134425 =
                      
                    x10::array::RectLayout::_alloc();
                    
                    //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int _max034429 =
                          ((x10_int) ((size34422) - (((x10_int)1))));
                        {
                            
                            //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(rank) =
                              ((x10_int)1);
                            
                            //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(min0) =
                              ((x10_int)0);
                            
                            //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(delta0) =
                              ((x10_int) ((((x10_int) ((_max034429) - (((x10_int)0))))) + (((x10_int)1))));
                            
                            //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(size) =
                              ((alloc1996134425->
                                  FMGL(delta0)) > (((x10_int)0)))
                              ? (alloc1996134425->
                                   FMGL(delta0))
                              : (((x10_int)0));
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(min1) =
                              ((x10_int)0);
                            
                            //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(delta1) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(min2) =
                              ((x10_int)0);
                            
                            //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(delta2) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(min3) =
                              ((x10_int)0);
                            
                            //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(delta3) =
                              ((x10_int)0);
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(min) =
                              X10_NULL;
                            
                            //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                            alloc1996134425->
                              FMGL(delta) =
                              X10_NULL;
                        }
                        
                    }))
                    ;
                    alloc1996134425;
                }))
                ;
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int n34426 =
                  (__extension__ ({
                    
                    //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10::array::RectLayout this34431 =
                      x10aux::nullCheck(alloc31551)->
                        FMGL(layout);
                    this34431->
                      FMGL(size);
                }))
                ;
                
                //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                x10aux::nullCheck(alloc31551)->
                  FMGL(raw) =
                  x10::util::IndexedMemoryChunk<void>::allocate<x10_long >(n34426, 8, false, true);
            }
            
        }))
        ;
        alloc31551;
    }))
    ;
    
    //#line 9 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::util::Timer>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::util::Timer> au::edu::anu::util::Timer::_make(
  x10_int n,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::util::Timer> this_ = new (memset(x10aux::alloc<au::edu::anu::util::Timer>(), 0, sizeof(au::edu::anu::util::Timer))) au::edu::anu::util::Timer();
    this_->_constructor(n,
    paramLock);
    return this_;
}



//#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::util::Timer::start(x10_int id) {
    
    //#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_long> > x34465 =
          ((x10aux::ref<au::edu::anu::util::Timer>)this)->
            FMGL(total);
        
        //#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10_int y034466 = id;
        
        //#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10_long z34467 = (__extension__ ({
            x10aux::system_utils::nanoTime();
        }))
        ;
        
        //#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10_long ret34476;
        {
            
            //#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
            x10_long r34468 = ((x10_long) (((__extension__ ({
                
                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i03443234469 = y034466;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long ret3443334470;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                goto __ret3443434471; __ret3443434471: {
                {
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret3443334470 = (x10aux::nullCheck(x34465)->
                                       FMGL(raw))->__apply(i03443234469);
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                    goto __ret3443434471_end_;
                }goto __ret3443434471_end_; __ret3443434471_end_: ;
                }
                ret3443334470;
                }))
                ) - (z34467)));
                
            
            //#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i03444934472 = y034466;
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long v3445034473 = r34468;
                
                //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long ret3445134474;
                {
                    
                    //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (x10aux::nullCheck(x34465)->
                       FMGL(raw))->__set(i03444934472, v3445034473);
                    
                    //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret3445134474 = v3445034473;
                }
                ret3445134474;
            }))
            ;
            
            //#line 14 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalAssign_c
            ret34476 = r34468;
            }
        ret34476;
        }))
        ;
    }
    

//#line 15 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::util::Timer::clear(x10_int id) {
    
    //#line 15 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 15 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_long> > this34480 =
          ((x10aux::ref<au::edu::anu::util::Timer>)this)->
            FMGL(total);
        
        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_int i034478 = id;
        
        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_long v34479 = ((x10_long) (((x10_int)0)));
        
        //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_long ret34481;
        {
            
            //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
            (x10aux::nullCheck(this34480)->
               FMGL(raw))->__set(i034478, v34479);
            
            //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret34481 = v34479;
        }
        ret34481;
    }))
    ;
}

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::util::Timer::stop(x10_int id) {
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_long> > x34505 =
          ((x10aux::ref<au::edu::anu::util::Timer>)this)->
            FMGL(total);
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10_int y034506 = id;
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10_long z34507 = (__extension__ ({
            x10aux::system_utils::nanoTime();
        }))
        ;
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10_long ret34516;
        {
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
            x10_long r34508 = ((x10_long) (((__extension__ ({
                
                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i03448834509 = y034506;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long ret3448934510;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                goto __ret3449034511; __ret3449034511: {
                {
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret3448934510 = (x10aux::nullCheck(x34505)->
                                       FMGL(raw))->__apply(i03448834509);
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                    goto __ret3449034511_end_;
                }goto __ret3449034511_end_; __ret3449034511_end_: ;
                }
                ret3448934510;
                }))
                ) + (z34507)));
                
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i03449634512 = y034506;
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long v3449734513 = r34508;
                
                //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long ret3449834514;
                {
                    
                    //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (x10aux::nullCheck(x34505)->
                       FMGL(raw))->__set(i03449634512, v3449734513);
                    
                    //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret3449834514 = v3449734513;
                }
                ret3449834514;
            }))
            ;
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalAssign_c
            ret34516 = r34508;
            }
        ret34516;
        }))
        ;
    
    //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_long> > x34535 =
          ((x10aux::ref<au::edu::anu::util::Timer>)this)->
            FMGL(count);
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10_int y034536 = id;
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
        x10_long ret34546;
        {
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
            x10_long r34538 = ((x10_long) (((__extension__ ({
                
                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i03451834539 = y034536;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long ret3451934540;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                goto __ret3452034541; __ret3452034541: {
                {
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret3451934540 = (x10aux::nullCheck(x34535)->
                                       FMGL(raw))->__apply(i03451834539);
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                    goto __ret3452034541_end_;
                }goto __ret3452034541_end_; __ret3452034541_end_: ;
                }
                ret3451934540;
                }))
                ) + (((x10_long)1ll))));
                
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i03452634542 = y034536;
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long v3452734543 = r34538;
                
                //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long ret3452834544;
                {
                    
                    //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (x10aux::nullCheck(x34535)->
                       FMGL(raw))->__set(i03452634542, v3452734543);
                    
                    //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret3452834544 = v3452734543;
                }
                ret3452834544;
            }))
            ;
            
            //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalAssign_c
            ret34546 = r34538;
            }
        ret34546;
        }))
        ;
    }
    
    //#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10MethodDecl_c
    x10_long au::edu::anu::util::Timer::mean(
      x10_int id) {
        
        //#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10Return_c
        return ((x10_long) (((__extension__ ({
            
            //#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Array<x10_long> > this34549 =
              ((x10aux::ref<au::edu::anu::util::Timer>)this)->
                FMGL(total);
            
            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int i034548 = id;
            
            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_long ret34550;
            
            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
            goto __ret34551; __ret34551: {
            {
                
                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                ret34550 = (x10aux::nullCheck(this34549)->
                              FMGL(raw))->__apply(i034548);
                
                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                goto __ret34551_end_;
            }goto __ret34551_end_; __ret34551_end_: ;
            }
            ret34550;
            }))
            ) / x10aux::zeroCheck((__extension__ ({
                
                //#line 17 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Array<x10_long> > this34558 =
                  ((x10aux::ref<au::edu::anu::util::Timer>)this)->
                    FMGL(count);
                
                //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i034557 = id;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_long ret34559;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                goto __ret34560; __ret34560: {
                {
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret34559 = (x10aux::nullCheck(this34558)->
                                  FMGL(raw))->__apply(i034557);
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                    goto __ret34560_end_;
                }goto __ret34560_end_; __ret34560_end_: ;
                }
                ret34559;
                }))
                )));
                
        }
        
        //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10MethodDecl_c
        x10aux::ref<au::edu::anu::util::Timer>
          au::edu::anu::util::Timer::au__edu__anu__util__Timer____au__edu__anu__util__Timer__this(
          ) {
            
            //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10Return_c
            return ((x10aux::ref<au::edu::anu::util::Timer>)this);
            
        }
        
        //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10MethodDecl_c
        void au::edu::anu::util::Timer::__fieldInitializers31521(
          ) {
            
            //#line 5 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/util/Timer.x10": x10.ast.X10FieldAssign_c
            ((x10aux::ref<au::edu::anu::util::Timer>)this)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        const x10aux::serialization_id_t au::edu::anu::util::Timer::_serialization_id = 
            x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::util::Timer::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);
        
        void au::edu::anu::util::Timer::_serialize_body(x10aux::serialization_buffer& buf) {
            x10::lang::Object::_serialize_body(buf);
            buf.write(this->FMGL(total));
            buf.write(this->FMGL(count));
            
        }
        
        x10aux::ref<x10::lang::Reference> au::edu::anu::util::Timer::_deserializer(x10aux::deserialization_buffer& buf) {
            x10aux::ref<au::edu::anu::util::Timer> this_ = new (memset(x10aux::alloc<au::edu::anu::util::Timer>(), 0, sizeof(au::edu::anu::util::Timer))) au::edu::anu::util::Timer();
            buf.record_reference(this_);
            this_->_deserialize_body(buf);
            return this_;
        }
        
        void au::edu::anu::util::Timer::_deserialize_body(x10aux::deserialization_buffer& buf) {
            x10::lang::Object::_deserialize_body(buf);
            FMGL(total) = buf.read<x10aux::ref<x10::array::Array<x10_long> > >();
            FMGL(count) = buf.read<x10aux::ref<x10::array::Array<x10_long> > >();
        }
        
        
    x10aux::RuntimeType au::edu::anu::util::Timer::rtt;
    void au::edu::anu::util::Timer::_initRTT() {
        if (rtt.initStageOne(&rtt)) return;
        const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
        rtt.initStageTwo("au.edu.anu.util.Timer",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
    }
    /* END of Timer */
/*************************************************/
