/*************************************************/
/* START of TestPeriodicFmm3d */
#include <au/edu/anu/mm/TestPeriodicFmm3d.h>

#include <au/edu/anu/chem/mm/TestElectrostatic.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Double.h>
#include <x10/array/Array.h>
#include <x10/lang/String.h>
#include <x10/lang/Boolean.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/array/RectLayout.h>
#include <x10/lang/Any.h>
#include <x10/io/Printer.h>
#include <x10/io/Console.h>
#include <x10/lang/Math.h>
#include <x10/array/DistArray.h>
#include <au/edu/anu/chem/mm/MMAtom.h>
#include <au/edu/anu/mm/PeriodicFmm3d.h>
#include <x10x/vector/Point3d.h>
#include <au/edu/anu/mm/Fmm3d.h>

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::TestPeriodicFmm3d::getOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::TestPeriodicFmm3d.X10$class_lock_id1");
    x10_int __var1__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var1__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var2__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var2__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::TestPeriodicFmm3d.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::TestPeriodicFmm3d.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__3 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__deserialize));


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::TestPeriodicFmm3d::getStaticOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId17382 = au::edu::anu::mm::TestPeriodicFmm3d::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId17382);
    }))
    ;
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
x10_double au::edu::anu::mm::TestPeriodicFmm3d::sizeOfCentralCluster(
  ) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Return_c
    return 80.0;
    
}

//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::TestPeriodicFmm3d::main(
  x10aux::ref<x10::array::Array<x10aux::ref<x10::lang::String> > > args) {
    
    //#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_int numAtoms;
    
    //#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_double density = 60.0;
    
    //#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_int numTerms = ((x10_int)10);
    
    //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_int numShells = ((x10_int)10);
    
    //#line 31 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
    x10_boolean verbose = false;
    
    //#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
    if (((x10aux::nullCheck(args)->FMGL(size)) > (((x10_int)0))))
    {
        
        //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
        numAtoms =
          x10aux::int_utils::parseInt((__extension__ ({
            
            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::lang::String> ret20026;
            
            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
            goto __ret20027; __ret20027: {
            {
                
                //#line 411 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10If_c
                if (x10aux::nullCheck(args)->
                      FMGL(rail))
                {
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret20026 =
                      (x10aux::nullCheck(args)->
                         FMGL(raw))->__apply(((x10_int)0));
                    
                    //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                    goto __ret20027_end_;
                }
                else
                {
                    
                    //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                    ret20026 =
                      (x10aux::nullCheck(args)->
                         FMGL(raw))->__apply((__extension__ ({
                        
                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10::array::RectLayout this22219 =
                          x10aux::nullCheck(args)->
                            FMGL(layout);
                        
                        //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                        x10_int ret22220;
                        {
                            
                            //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int offset22218 =
                              ((x10_int) ((((x10_int)0)) - (this22219->
                                                              FMGL(min0))));
                            
                            //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                            ret22220 =
                              offset22218;
                        }
                        ret22220;
                    }))
                    );
                    
                    //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                    goto __ret20027_end_;
                }
                
            }goto __ret20027_end_; __ret20027_end_: ;
            }
            ret20026;
            }))
            );
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
        if (((x10aux::nullCheck(args)->
                FMGL(size)) > (((x10_int)1))))
        {
            
            //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
            density =
              x10aux::double_utils::parseDouble((__extension__ ({
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::lang::String> ret22223;
                
                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                goto __ret22224; __ret22224: {
                {
                    
                    //#line 411 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10If_c
                    if (x10aux::nullCheck(args)->
                          FMGL(rail))
                    {
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret22223 =
                          (x10aux::nullCheck(args)->
                             FMGL(raw))->__apply(((x10_int)1));
                        
                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                        goto __ret22224_end_;
                    }
                    else
                    {
                        
                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                        ret22223 =
                          (x10aux::nullCheck(args)->
                             FMGL(raw))->__apply((__extension__ ({
                            
                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10::array::RectLayout this22227 =
                              x10aux::nullCheck(args)->
                                FMGL(layout);
                            
                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                            x10_int ret22228;
                            {
                                
                                //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int offset22226 =
                                  ((x10_int) ((((x10_int)1)) - (this22227->
                                                                  FMGL(min0))));
                                
                                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                ret22228 =
                                  offset22226;
                            }
                            ret22228;
                        }))
                        );
                        
                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                        goto __ret22224_end_;
                    }
                    
                }goto __ret22224_end_; __ret22224_end_: ;
                }
                ret22223;
                }))
                );
            
            //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
            if (((x10aux::nullCheck(args)->
                    FMGL(size)) > (((x10_int)2))))
            {
                
                //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                numTerms =
                  x10aux::int_utils::parseInt((__extension__ ({
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::lang::String> ret22246;
                    
                    //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                    goto __ret22247; __ret22247: {
                    {
                        
                        //#line 411 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10If_c
                        if (x10aux::nullCheck(args)->
                              FMGL(rail))
                        {
                            
                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret22246 =
                              (x10aux::nullCheck(args)->
                                 FMGL(raw))->__apply(((x10_int)2));
                            
                            //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                            goto __ret22247_end_;
                        }
                        else
                        {
                            
                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret22246 =
                              (x10aux::nullCheck(args)->
                                 FMGL(raw))->__apply((__extension__ ({
                                
                                //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10::array::RectLayout this22250 =
                                  x10aux::nullCheck(args)->
                                    FMGL(layout);
                                
                                //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                x10_int ret22251;
                                {
                                    
                                    //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int offset22249 =
                                      ((x10_int) ((((x10_int)2)) - (this22250->
                                                                      FMGL(min0))));
                                    
                                    //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                    ret22251 =
                                      offset22249;
                                }
                                ret22251;
                            }))
                            );
                            
                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                            goto __ret22247_end_;
                        }
                        
                    }goto __ret22247_end_; __ret22247_end_: ;
                    }
                    ret22246;
                    }))
                    );
                
                //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
                if (((x10aux::nullCheck(args)->
                        FMGL(size)) > (((x10_int)3))))
                {
                    
                    //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                    numShells =
                      x10aux::int_utils::parseInt((__extension__ ({
                        
                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::lang::String> ret22254;
                        
                        //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                        goto __ret22255; __ret22255: {
                        {
                            
                            //#line 411 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10If_c
                            if (x10aux::nullCheck(args)->
                                  FMGL(rail))
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret22254 =
                                  (x10aux::nullCheck(args)->
                                     FMGL(raw))->__apply(((x10_int)3));
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret22255_end_;
                            }
                            else
                            {
                                
                                //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret22254 =
                                  (x10aux::nullCheck(args)->
                                     FMGL(raw))->__apply((__extension__ ({
                                    
                                    //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                    x10::array::RectLayout this22258 =
                                      x10aux::nullCheck(args)->
                                        FMGL(layout);
                                    
                                    //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                    x10_int ret22259;
                                    {
                                        
                                        //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                        x10_int offset22257 =
                                          ((x10_int) ((((x10_int)3)) - (this22258->
                                                                          FMGL(min0))));
                                        
                                        //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                        ret22259 =
                                          offset22257;
                                    }
                                    ret22259;
                                }))
                                );
                                
                                //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret22255_end_;
                            }
                            
                        }goto __ret22255_end_; __ret22255_end_: ;
                        }
                        ret22254;
                        }))
                        );
                    
                    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
                    if (((numShells) < (((x10_int)1))))
                    {
                        
                        //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                        numShells =
                          ((x10_int)1);
                    }
                    
                    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
                    if (((x10aux::nullCheck(args)->
                            FMGL(size)) > (((x10_int)4))))
                    {
                        
                        //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
                        if (x10aux::equals((__extension__ ({
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                x10aux::ref<x10::lang::String> ret22262;
                                
                                //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                                goto __ret22263; __ret22263: {
                                {
                                    
                                    //#line 411 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10If_c
                                    if (x10aux::nullCheck(args)->
                                          FMGL(rail))
                                    {
                                        
                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret22262 =
                                          (x10aux::nullCheck(args)->
                                             FMGL(raw))->__apply(((x10_int)4));
                                        
                                        //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                        goto __ret22263_end_;
                                    }
                                    else
                                    {
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                        ret22262 =
                                          (x10aux::nullCheck(args)->
                                             FMGL(raw))->__apply((__extension__ ({
                                            
                                            //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                            x10::array::RectLayout this22266 =
                                              x10aux::nullCheck(args)->
                                                FMGL(layout);
                                            
                                            //#line 129 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                            x10_int ret22267;
                                            {
                                                
                                                //#line 130 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                x10_int offset22265 =
                                                  ((x10_int) ((((x10_int)4)) - (this22266->
                                                                                  FMGL(min0))));
                                                
                                                //#line 131 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalAssign_c
                                                ret22267 =
                                                  offset22265;
                                            }
                                            ret22267;
                                        }))
                                        );
                                        
                                        //#line 418 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                        goto __ret22263_end_;
                                    }
                                    
                                }goto __ret22263_end_; __ret22263_end_: ;
                                }
                                ret22262;
                                }))
                                ,x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Any> >(x10aux::string_utils::lit("-verbose"))))
                            {
                                
                                //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalAssign_c
                                verbose =
                                  true;
                            }
                            
                        }
                        
                    }
                    
                }
                }
                
            }
            else
            {
                
                //#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                x10aux::nullCheck(x10::io::Console::
                                    FMGL(ERR))->x10::io::Printer::println(
                  x10aux::string_utils::lit("usage: TestPeriodicFmm3d numAtoms [density] [numTerms] [numShells] [-verbose]"));
                
                //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Return_c
                return;
            }
            
            //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
            x10aux::nullCheck((__extension__ ({
                
                //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> alloc17341 =
                  
                x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>((new (memset(x10aux::alloc<au::edu::anu::mm::TestPeriodicFmm3d>(), 0, sizeof(au::edu::anu::mm::TestPeriodicFmm3d))) au::edu::anu::mm::TestPeriodicFmm3d()))
                ;
                
                //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                (alloc17341)->::au::edu::anu::mm::TestPeriodicFmm3d::_constructor(
                  x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                alloc17341;
            }))
            )->test(
              numAtoms,
              density,
              numTerms,
              numShells,
              verbose);
        }
        
        //#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
        void
          au::edu::anu::mm::TestPeriodicFmm3d::test(
          x10_int numAtoms,
          x10_double density,
          x10_int numTerms,
          x10_int numShells,
          x10_boolean verbose) {
            
            //#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
            if (verbose)
            {
                
                //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10_int numLevels =
                  (__extension__ ({
                    
                    //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                    x10_int b23630 =
                      x10aux::double_utils::toInt(((((x10aux::math_utils::log(((((x10_double) (numAtoms))) / (density)))) / (x10aux::math_utils::log(8.0)))) + (1.0)));
                    ((((x10_int)2)) < (b23630))
                      ? (b23630)
                      : (((x10_int)2));
                }))
                ;
                
                //#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                x10aux::nullCheck(x10::io::Console::
                                    FMGL(OUT))->x10::io::Printer::println(
                  ((((((((((((((((((x10aux::string_utils::lit("Testing Periodic FMM for ")) + (numAtoms))) + (x10aux::string_utils::lit(" atoms, target density = ")))) + (density))) + (x10aux::string_utils::lit(" numTerms = ")))) + (numTerms))) + (x10aux::string_utils::lit(" numShells = ")))) + (numShells))) + (x10aux::string_utils::lit(" numLevels = ")))) + (numLevels)));
            }
            else
            {
                
                //#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                x10aux::nullCheck(x10::io::Console::
                                    FMGL(OUT))->print(
                  ((numAtoms) + (x10aux::string_utils::lit(" atoms: "))));
            }
            
            //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms =
              ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->generateAtoms(
                numAtoms);
            
            //#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> fmm3d =
              
            x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>((new (memset(x10aux::alloc<au::edu::anu::mm::PeriodicFmm3d>(), 0, sizeof(au::edu::anu::mm::PeriodicFmm3d))) au::edu::anu::mm::PeriodicFmm3d()))
            ;
            
            //#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
            (fmm3d)->::au::edu::anu::mm::PeriodicFmm3d::_constructor(
              density,
              numTerms,
              (__extension__ ({
                  
                  //#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                  x10x::vector::Point3d alloc17342 =
                    
                  x10x::vector::Point3d::_alloc();
                  
                  //#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
                  (alloc17342)->::x10x::vector::Point3d::_constructor(
                    0.0,
                    0.0,
                    0.0,
                    x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
                  alloc17342;
              }))
              ,
              80.0,
              numAtoms,
              atoms,
              numShells,
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            
            //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
            fmm3d->assignAtomsToBoxes();
            
            //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
            x10_double energy =
              fmm3d->calculateEnergy();
            
            //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10If_c
            if (verbose)
            {
                
                //#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                x10aux::nullCheck(x10::io::Console::
                                    FMGL(OUT))->x10::io::Printer::println(
                  ((x10aux::string_utils::lit("energy = ")) + (energy)));
                
                //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
                  x10aux::string_utils::lit("Tree construction"),
                  ((x10_int)7),
                  fmm3d->
                    FMGL(timer));
                
                //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
                  x10aux::string_utils::lit("Prefetch"),
                  ((x10_int)1),
                  fmm3d->
                    FMGL(timer));
                
                //#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
                  x10aux::string_utils::lit("Direct"),
                  ((x10_int)2),
                  fmm3d->
                    FMGL(timer));
                
                //#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
                  x10aux::string_utils::lit("Multipole"),
                  ((x10_int)3),
                  fmm3d->
                    FMGL(timer));
                
                //#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
                  x10aux::string_utils::lit("Combine"),
                  ((x10_int)4),
                  fmm3d->
                    FMGL(timer));
                
                //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
                  x10aux::string_utils::lit("Macroscopic"),
                  ((x10_int)8),
                  fmm3d->
                    FMGL(timer));
                
                //#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
                  x10aux::string_utils::lit("Transform"),
                  ((x10_int)5),
                  fmm3d->
                    FMGL(timer));
                
                //#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
                ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
                  x10aux::string_utils::lit("Far field"),
                  ((x10_int)6),
                  fmm3d->
                    FMGL(timer));
            }
            
            //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Call_c
            ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->logTime(
              x10aux::string_utils::lit("Total"),
              ((x10_int)0),
              fmm3d->
                FMGL(timer));
        }
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
        x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>
          au::edu::anu::mm::TestPeriodicFmm3d::au__edu__anu__mm__TestPeriodicFmm3d____au__edu__anu__mm__TestPeriodicFmm3d__this(
          ) {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10Return_c
            return ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this);
            
        }
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10ConstructorDecl_c
        void au::edu::anu::mm::TestPeriodicFmm3d::_constructor(
          )
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
            (((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this))->::au::edu::anu::chem::mm::TestElectrostatic::_constructor();
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.AssignPropertyCall_c
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> this2483224838 =
                  ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this);
                {
                    
                    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(this2483224838)->
                      FMGL(X10__object_lock_id0) =
                      ((x10_int)-1);
                }
                
            }))
            ;
            
        }
        x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> au::edu::anu::mm::TestPeriodicFmm3d::_make(
          )
        {
            x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::TestPeriodicFmm3d>(), 0, sizeof(au::edu::anu::mm::TestPeriodicFmm3d))) au::edu::anu::mm::TestPeriodicFmm3d();
            this_->_constructor();
            return this_;
        }
        
        
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10ConstructorDecl_c
        void au::edu::anu::mm::TestPeriodicFmm3d::_constructor(
          x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10ConstructorCall_c
            (((x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>)this))->::au::edu::anu::chem::mm::TestElectrostatic::_constructor();
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.AssignPropertyCall_c
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> this2483524839 =
                  ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this);
                {
                    
                    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(this2483524839)->
                      FMGL(X10__object_lock_id0) =
                      ((x10_int)-1);
                }
                
            }))
            ;
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
            ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->
              FMGL(X10__object_lock_id0) =
              x10aux::nullCheck(paramLock)->getIndex();
            
        }
        x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> au::edu::anu::mm::TestPeriodicFmm3d::_make(
          x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
        {
            x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::TestPeriodicFmm3d>(), 0, sizeof(au::edu::anu::mm::TestPeriodicFmm3d))) au::edu::anu::mm::TestPeriodicFmm3d();
            this_->_constructor(paramLock);
            return this_;
        }
        
        
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10MethodDecl_c
        void
          au::edu::anu::mm::TestPeriodicFmm3d::__fieldInitializers17081(
          ) {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/test/au/edu/anu/mm/TestPeriodicFmm3d.x10": x10.ast.X10FieldAssign_c
            ((x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d>)this)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        const x10aux::serialization_id_t au::edu::anu::mm::TestPeriodicFmm3d::_serialization_id = 
            x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::TestPeriodicFmm3d::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);
        
        void au::edu::anu::mm::TestPeriodicFmm3d::_serialize_body(x10aux::serialization_buffer& buf) {
            au::edu::anu::chem::mm::TestElectrostatic::_serialize_body(buf);
            
        }
        
        x10aux::ref<x10::lang::Reference> au::edu::anu::mm::TestPeriodicFmm3d::_deserializer(x10aux::deserialization_buffer& buf) {
            x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::TestPeriodicFmm3d>(), 0, sizeof(au::edu::anu::mm::TestPeriodicFmm3d))) au::edu::anu::mm::TestPeriodicFmm3d();
            buf.record_reference(this_);
            this_->_deserialize_body(buf);
            return this_;
        }
        
        void au::edu::anu::mm::TestPeriodicFmm3d::_deserialize_body(x10aux::deserialization_buffer& buf) {
            au::edu::anu::chem::mm::TestElectrostatic::_deserialize_body(buf);
            
        }
        
        x10aux::RuntimeType au::edu::anu::mm::TestPeriodicFmm3d::rtt;
        void au::edu::anu::mm::TestPeriodicFmm3d::_initRTT() {
            if (rtt.initStageOne(&rtt)) return;
            const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<au::edu::anu::chem::mm::TestElectrostatic>()};
            rtt.initStageTwo("au.edu.anu.mm.TestPeriodicFmm3d",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
        }
        /* END of TestPeriodicFmm3d */
/*************************************************/
