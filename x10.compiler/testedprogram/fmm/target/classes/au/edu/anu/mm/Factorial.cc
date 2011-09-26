/*************************************************/
/* START of Factorial */
#include <au/edu/anu/mm/Factorial.h>


//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10FieldDecl_c

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::Factorial::getOrderedLock(
  ) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::Factorial>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::Factorial.X10$class_lock_id1");
    x10_int __var438__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var438__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var439__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var439__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::Factorial.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::Factorial.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__440 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::Factorial::FMGL(X10__class_lock_id1__deserialize));


//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::Factorial::getStaticOrderedLock(
  ) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId55555 = au::edu::anu::mm::Factorial::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId55555);
    }))
    ;
    
}

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10FieldDecl_c
x10aux::ref<x10::array::Array<x10_double> >
  au::edu::anu::mm::Factorial::FMGL(factorial);
void au::edu::anu::mm::Factorial::FMGL(factorial__do_init)() {
    FMGL(factorial__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::Factorial.factorial");
    x10aux::ref<x10::array::Array<x10_double> >
      __var442__ =
      au::edu::anu::mm::Factorial::calcFact();
    FMGL(factorial) = __var442__;
    FMGL(factorial__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::Factorial::FMGL(factorial__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var443__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(factorial__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var443__ != x10aux::UNINITIALIZED) goto WAIT;
        FMGL(factorial__do_init)();
        x10aux::StaticInitBroadcastDispatcher::broadcastStaticField(FMGL(factorial),
                                                                    FMGL(factorial__id));
        // Notify all waiting threads
        x10aux::StaticInitBroadcastDispatcher::lock();
        x10aux::StaticInitBroadcastDispatcher::notify();
    }
    WAIT:
    if (FMGL(factorial__status) != x10aux::INITIALIZED) {
                                                             x10aux::StaticInitBroadcastDispatcher::lock();
                                                             _SI_("WAITING for field: au::edu::anu::mm::Factorial.factorial to be initialized");
                                                             while (FMGL(factorial__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                             _SI_("CONTINUING because field: au::edu::anu::mm::Factorial.factorial has been initialized");
                                                             x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__444 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::Factorial::FMGL(factorial__init));

volatile x10aux::status au::edu::anu::mm::Factorial::FMGL(factorial__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::Factorial::FMGL(factorial__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(factorial) = buf.read<x10aux::ref<x10::array::Array<x10_double> > >();
    au::edu::anu::mm::Factorial::FMGL(factorial__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::Factorial::FMGL(factorial__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::Factorial::FMGL(factorial__deserialize));


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10_double> >
  au::edu::anu::mm::Factorial::calcFact(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10_double> > fact =
      
    x10aux::ref<x10::array::Array<x10_double> >((new (memset(x10aux::alloc<x10::array::Array<x10_double> >(), 0, sizeof(x10::array::Array<x10_double>))) x10::array::Array<x10_double>()))
    ;
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        {
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
            (fact)->::x10::lang::Object::_constructor();
            
            //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> myReg55557 =
              x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::RectRegion1D> alloc1996055558 =
                  
                x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                ;
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc1996055558)->::x10::array::RectRegion1D::_constructor(
                  ((x10_int)0),
                  ((x10_int) ((((x10_int)100)) - (((x10_int)1)))));
                alloc1996055558;
            }))
            );
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            fact->FMGL(region) = myReg55557;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            fact->FMGL(rank) = ((x10_int)1);
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            fact->FMGL(rect) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            fact->FMGL(zeroBased) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            fact->FMGL(rail) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            fact->FMGL(size) = ((x10_int)100);
            
            //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            fact->FMGL(layout) = (__extension__ ({
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout alloc1996155559 =
                  
                x10::array::RectLayout::_alloc();
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int _max055563 = ((x10_int) ((((x10_int)100)) - (((x10_int)1))));
                    {
                        
                        //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(rank) = ((x10_int)1);
                        
                        //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(min0) = ((x10_int)0);
                        
                        //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(delta0) = ((x10_int) ((((x10_int) ((_max055563) - (((x10_int)0))))) + (((x10_int)1))));
                        
                        //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(size) = ((alloc1996155559->
                                           FMGL(delta0)) > (((x10_int)0)))
                          ? (alloc1996155559->
                               FMGL(delta0))
                          : (((x10_int)0));
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(min1) = ((x10_int)0);
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(delta1) = ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(min2) = ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(delta2) = ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(min3) = ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(delta3) = ((x10_int)0);
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(min) = X10_NULL;
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996155559->
                          FMGL(delta) = X10_NULL;
                    }
                    
                }))
                ;
                alloc1996155559;
            }))
            ;
            
            //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int n55560 = (__extension__ ({
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this55565 =
                  fact->
                    FMGL(layout);
                this55565->FMGL(size);
            }))
            ;
            
            //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            fact->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10_double >(n55560, 8, false, true);
        }
        
    }))
    ;
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_double ret55568;
        {
            
            //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
            (fact->FMGL(raw))->__set(((x10_int)0), 1.0);
            
            //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret55568 = 1.0;
        }
        ret55568;
    }))
    ;
    
    //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": polyglot.ast.For_c
    {
        x10_int i5394555615;
        for (
             //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
             i5394555615 = ((x10_int)1); ((i5394555615) <= (((x10_int)99)));
             
             //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalAssign_c
             i5394555615 =
               ((x10_int) ((i5394555615) + (((x10_int)1)))))
        {
            
            //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
            x10_int i55616 =
              i5394555615;
            
            //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_int i05558355607 =
                  i55616;
                
                //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double v5558455608 =
                  ((((x10_double) (i55616))) * ((__extension__ ({
                    
                    //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_int i05557555609 =
                      ((x10_int) ((i55616) - (((x10_int)1))));
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10_double ret5557655610;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                    goto __ret5557755611; __ret5557755611: {
                    {
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret5557655610 =
                          (fact->
                             FMGL(raw))->__apply(i05557555609);
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                        goto __ret5557755611_end_;
                    }goto __ret5557755611_end_; __ret5557755611_end_: ;
                    }
                    ret5557655610;
                    }))
                    ));
                    
                
                //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10_double ret5558555612;
                {
                    
                    //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                    (fact->
                       FMGL(raw))->__set(i05558355607, v5558455608);
                    
                    //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret5558555612 =
                      v5558455608;
                }
                ret5558555612;
                }))
                ;
            }
        }
        
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10Return_c
    return fact;
    }
    

//#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10MethodDecl_c
x10_double au::edu::anu::mm::Factorial::getFactorial(
  x10_int i) {
    
    //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Array<x10_double> > this55593 =
          au::edu::anu::mm::Factorial::
            FMGL(factorial__get)();
        
        //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_int i055592 = i;
        
        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
        x10_double ret55594;
        
        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
        goto __ret55595; __ret55595: {
        {
            
            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
            ret55594 = (x10aux::nullCheck(this55593)->
                          FMGL(raw))->__apply(i055592);
            
            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
            goto __ret55595_end_;
        }goto __ret55595_end_; __ret55595_end_: ;
        }
        ret55594;
        }))
        ;
        
    }
    

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::Factorial> au::edu::anu::mm::Factorial::au__edu__anu__mm__Factorial____au__edu__anu__mm__Factorial__this(
  ) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::Factorial>)this);
    
}

//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::Factorial::_constructor(
  ) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.AssignPropertyCall_c
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::Factorial> this5560155617 =
          ((x10aux::ref<au::edu::anu::mm::Factorial>)this);
        {
            
            //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5560155617)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
}
x10aux::ref<au::edu::anu::mm::Factorial> au::edu::anu::mm::Factorial::_make(
  ) {
    x10aux::ref<au::edu::anu::mm::Factorial> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::Factorial>(), 0, sizeof(au::edu::anu::mm::Factorial))) au::edu::anu::mm::Factorial();
    this_->_constructor();
    return this_;
}



//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::Factorial::_constructor(
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.AssignPropertyCall_c
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::Factorial> this5560455618 =
          ((x10aux::ref<au::edu::anu::mm::Factorial>)this);
        {
            
            //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5560455618)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Factorial>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::Factorial> au::edu::anu::mm::Factorial::_make(
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::Factorial> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::Factorial>(), 0, sizeof(au::edu::anu::mm::Factorial))) au::edu::anu::mm::Factorial();
    this_->_constructor(paramLock);
    return this_;
}



//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::Factorial::__fieldInitializers53929(
  ) {
    
    //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::Factorial>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
}
const x10aux::serialization_id_t au::edu::anu::mm::Factorial::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::Factorial::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::Factorial::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::Factorial::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::Factorial> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::Factorial>(), 0, sizeof(au::edu::anu::mm::Factorial))) au::edu::anu::mm::Factorial();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::Factorial::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    
}

x10aux::RuntimeType au::edu::anu::mm::Factorial::rtt;
void au::edu::anu::mm::Factorial::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::lang::Object>()};
    rtt.initStageTwo("au.edu.anu.mm.Factorial",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
/* END of Factorial */
/*************************************************/
