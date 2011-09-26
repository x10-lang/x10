/*************************************************/
/* START of FmmLeafBox */
#include <au/edu/anu/mm/FmmLeafBox.h>

#include <au/edu/anu/mm/FmmBox.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/util/ArrayList.h>
#include <au/edu/anu/chem/PointCharge.h>
#include <x10/array/Array.h>
#include <x10/array/Point.h>
#include <x10/lang/GlobalRef.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/Runtime.h>
#include <x10/compiler/Finalization.h>
#include <x10/compiler/Abort.h>
#include <x10/compiler/CompilerFlags.h>
#include <x10/lang/Boolean.h>

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::FmmLeafBox::getOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::FmmLeafBox.X10$class_lock_id1");
    x10_int __var354__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var354__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var355__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var355__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::FmmLeafBox.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::FmmLeafBox.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__356 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__deserialize));


//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::FmmLeafBox::getStaticOrderedLock(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId48934 = au::edu::anu::mm::FmmLeafBox::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId48934);
    }))
    ;
    
}

//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldDecl_c

//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldDecl_c
/** The U-list consists of all leaf boxes not well-separated to this box. */

//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::FmmLeafBox::_constructor(
  x10_int level,
  x10_int x,
  x10_int y,
  x10_int z,
  x10_int numTerms,
  x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent)
{
    
    //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::FmmBox>)this))->::au::edu::anu::mm::FmmBox::_constructor(
      level,
      x,
      y,
      z,
      numTerms,
      parent);
    
    //#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::FmmLeafBox> this4893652235 =
          ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4893652235)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4893652235)->
              FMGL(atoms) =
              (__extension__ ({
                
                //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::util::ArrayList<au::edu::anu::chem::PointCharge> > alloc395764893552236 =
                  
                x10aux::ref<x10::util::ArrayList<au::edu::anu::chem::PointCharge> >((new (memset(x10aux::alloc<x10::util::ArrayList<au::edu::anu::chem::PointCharge> >(), 0, sizeof(x10::util::ArrayList<au::edu::anu::chem::PointCharge>))) x10::util::ArrayList<au::edu::anu::chem::PointCharge>()))
                ;
                
                //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorCall_c
                (alloc395764893552236)->::x10::util::ArrayList<au::edu::anu::chem::PointCharge>::_constructor();
                alloc395764893552236;
            }))
            ;
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4893652235)->
              FMGL(uList) =
              X10_NULL;
        }
        
    }))
    ;
    
}
x10aux::ref<au::edu::anu::mm::FmmLeafBox> au::edu::anu::mm::FmmLeafBox::_make(
  x10_int level,
  x10_int x,
  x10_int y,
  x10_int z,
  x10_int numTerms,
  x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent)
{
    x10aux::ref<au::edu::anu::mm::FmmLeafBox> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::FmmLeafBox>(), 0, sizeof(au::edu::anu::mm::FmmLeafBox))) au::edu::anu::mm::FmmLeafBox();
    this_->_constructor(level,
    x,
    y,
    z,
    numTerms,
    parent);
    return this_;
}



//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::FmmLeafBox::_constructor(
  x10_int level,
  x10_int x,
  x10_int y,
  x10_int z,
  x10_int numTerms,
  x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<au::edu::anu::mm::FmmBox>)this))->::au::edu::anu::mm::FmmBox::_constructor(
      level,
      x,
      y,
      z,
      numTerms,
      parent);
    
    //#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.AssignPropertyCall_c
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::FmmLeafBox> this4894052237 =
          ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this);
        {
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4894052237)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4894052237)->
              FMGL(atoms) =
              (__extension__ ({
                
                //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::util::ArrayList<au::edu::anu::chem::PointCharge> > alloc395764893952238 =
                  
                x10aux::ref<x10::util::ArrayList<au::edu::anu::chem::PointCharge> >((new (memset(x10aux::alloc<x10::util::ArrayList<au::edu::anu::chem::PointCharge> >(), 0, sizeof(x10::util::ArrayList<au::edu::anu::chem::PointCharge>))) x10::util::ArrayList<au::edu::anu::chem::PointCharge>()))
                ;
                
                //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorCall_c
                (alloc395764893952238)->::x10::util::ArrayList<au::edu::anu::chem::PointCharge>::_constructor();
                alloc395764893952238;
            }))
            ;
            
            //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this4894052237)->
              FMGL(uList) =
              X10_NULL;
        }
        
    }))
    ;
    
    //#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::FmmLeafBox> au::edu::anu::mm::FmmLeafBox::_make(
  x10_int level,
  x10_int x,
  x10_int y,
  x10_int z,
  x10_int numTerms,
  x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::FmmLeafBox> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::FmmLeafBox>(), 0, sizeof(au::edu::anu::mm::FmmLeafBox))) au::edu::anu::mm::FmmLeafBox();
    this_->_constructor(level,
    x,
    y,
    z,
    numTerms,
    parent,
    paramLock);
    return this_;
}



//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmLeafBox::addAtom(
  au::edu::anu::chem::PointCharge atom) {
    {
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::lang::Throwable> throwable52278 =
          X10_NULL;
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.Try_c
        try {
            {
                
                //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Call_c
                x10::util::concurrent::OrderedLock::acquireTwoLocks(
                  ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->getOrderedLock(),
                  x10aux::nullCheck(atom)->getOrderedLock());
                
                //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Call_c
                x10::lang::Runtime::pushAtomic();
                
                //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Call_c
                x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                    FMGL(atoms))->add(
                  atom);
            }
            
            //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Call_c
            x10::compiler::Finalization::plausibleThrow();
        }
        catch (x10aux::__ref& __ref__359) {
            x10aux::ref<x10::lang::Throwable>& __exc__ref__359 = (x10aux::ref<x10::lang::Throwable>&)__ref__359;
            if (true) {
                x10aux::ref<x10::lang::Throwable> formal52279 =
                  static_cast<x10aux::ref<x10::lang::Throwable> >(__exc__ref__359);
                {
                    
                    //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalAssign_c
                    throwable52278 =
                      formal52279;
                }
            } else
            throw;
        }
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10If_c
        if ((!x10aux::struct_equals(X10_NULL,
                                    throwable52278)))
        {
            
            //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10If_c
            if (x10aux::instanceof<x10aux::ref<x10::compiler::Abort> >(throwable52278))
            {
                
                //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.Throw_c
                x10aux::throwException(x10aux::nullCheck(throwable52278));
            }
            
        }
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10If_c
        if (true) {
            
            //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Call_c
            x10::lang::Runtime::popAtomic();
            
            //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Call_c
            x10::util::concurrent::OrderedLock::releaseTwoLocks(
              ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->getOrderedLock(),
              x10aux::nullCheck(atom)->getOrderedLock());
        }
        
        //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10If_c
        if ((!x10aux::struct_equals(X10_NULL,
                                    throwable52278)))
        {
            
            //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10If_c
            if (!(x10aux::instanceof<x10aux::ref<x10::compiler::Finalization> >(throwable52278)))
            {
                
                //#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.Throw_c
                x10aux::throwException(x10aux::nullCheck(throwable52278));
            }
            
        }
        
    }
}

//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > >
  au::edu::anu::mm::FmmLeafBox::getUList(
  ) {
    
    //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
             FMGL(uList);
    
}

//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmLeafBox::setUList(
  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > uList) {
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
      FMGL(uList) = uList;
}

//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmLeafBox::createUList(
  x10_int ws) {
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
    x10_int levelDim = (__extension__ ({
        
        //#line 376 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int i52215 = ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                           FMGL(level);
        ((x10_int) ((((x10_int)1)) << (0x1f & (i52215))));
    }))
    ;
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::util::ArrayList<x10aux::ref<x10::array::Point> > > uList =
      
    x10aux::ref<x10::util::ArrayList<x10aux::ref<x10::array::Point> > >((new (memset(x10aux::alloc<x10::util::ArrayList<x10aux::ref<x10::array::Point> > >(), 0, sizeof(x10::util::ArrayList<x10aux::ref<x10::array::Point> >))) x10::util::ArrayList<x10aux::ref<x10::array::Point> >()))
    ;
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorCall_c
    (uList)->::x10::util::ArrayList<x10aux::ref<x10::array::Point> >::_constructor();
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
    x10_int i39610min3961152257 = (__extension__ ({
        
        //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int b5221752258 = ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                             FMGL(x)) - (ws)));
        ((((x10_int)0)) < (b5221752258)) ? (b5221752258)
          : (((x10_int)0));
    }))
    ;
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
    x10_int i39610max3961252259 = ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                    FMGL(x);
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.For_c
    {
        x10_int i3961052260;
        for (
             //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
             i3961052260 = i39610min3961152257;
             ((i3961052260) <= (i39610max3961252259));
             
             //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalAssign_c
             i3961052260 =
               ((x10_int) ((i3961052260) + (((x10_int)1)))))
        {
            
            //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
            x10_int x52261 =
              i3961052260;
            
            //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
            x10_int i39594min3959552250 =
              (__extension__ ({
                
                //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                x10_int b5221952251 =
                  ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                 FMGL(y)) - (ws)));
                ((((x10_int)0)) < (b5221952251))
                  ? (b5221952251)
                  : (((x10_int)0));
            }))
            ;
            
            //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
            x10_int i39594max3959652252 =
              (__extension__ ({
                
                //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                x10_int a5222052253 =
                  ((x10_int) ((levelDim) - (((x10_int)1))));
                
                //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                x10_int b5222152254 =
                  ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                 FMGL(y)) + (ws)));
                ((a5222052253) < (b5222152254))
                  ? (a5222052253)
                  : (b5222152254);
            }))
            ;
            
            //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.For_c
            {
                x10_int i3959452255;
                for (
                     //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                     i3959452255 =
                       i39594min3959552250;
                     ((i3959452255) <= (i39594max3959652252));
                     
                     //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalAssign_c
                     i3959452255 =
                       ((x10_int) ((i3959452255) + (((x10_int)1)))))
                {
                    
                    //#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                    x10_int y52256 =
                      i3959452255;
                    
                    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                    x10_int i39578min3957952243 =
                      (__extension__ ({
                        
                        //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                        x10_int b5222352244 =
                          ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                         FMGL(z)) - (ws)));
                        ((((x10_int)0)) < (b5222352244))
                          ? (b5222352244)
                          : (((x10_int)0));
                    }))
                    ;
                    
                    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                    x10_int i39578max3958052245 =
                      (__extension__ ({
                        
                        //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                        x10_int a5222452246 =
                          ((x10_int) ((levelDim) - (((x10_int)1))));
                        
                        //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                        x10_int b5222552247 =
                          ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                         FMGL(z)) + (ws)));
                        ((a5222452246) < (b5222552247))
                          ? (a5222452246)
                          : (b5222552247);
                    }))
                    ;
                    
                    //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.For_c
                    {
                        x10_int i3957852248;
                        for (
                             //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                             i3957852248 =
                               i39578min3957952243;
                             ((i3957852248) <= (i39578max3958052245));
                             
                             //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalAssign_c
                             i3957852248 =
                               ((x10_int) ((i3957852248) + (((x10_int)1)))))
                        {
                            
                            //#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                            x10_int z52249 =
                              i3957852248;
                            
                            //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10If_c
                            if (((x52261) < (((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                               FMGL(x))) ||
                                (x10aux::struct_equals(x52261,
                                                       ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                                         FMGL(x))) &&
                                ((y52256) < (((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                               FMGL(y))) ||
                                (x10aux::struct_equals(x52261,
                                                       ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                                         FMGL(x))) &&
                                (x10aux::struct_equals(y52256,
                                                       ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                                         FMGL(y))) &&
                                ((z52249) < (((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                               FMGL(z))))
                            {
                                
                                //#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Call_c
                                uList->add(
                                  (__extension__ ({
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i05222652239 =
                                        x52261;
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i15222752240 =
                                        y52256;
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i25222852241 =
                                        z52249;
                                      (__extension__ ({
                                          
                                          //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                          x10aux::ref<x10::array::Point> alloc299415222952242 =
                                            
                                          x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
                                          ;
                                          
                                          //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
                                          (alloc299415222952242)->::x10::array::Point::_constructor(
                                            i05222652239,
                                            i15222752240,
                                            i25222852241);
                                          alloc299415222952242;
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
    
    //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
      FMGL(uList) = uList->toArray();
}

//#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmLeafBox::createUListPeriodic(
  x10_int ws) {
    
    //#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
    x10_int levelDim = (__extension__ ({
        
        //#line 376 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int i52230 = ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                           FMGL(level);
        ((x10_int) ((((x10_int)1)) << (0x1f & (i52230))));
    }))
    ;
    
    //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::util::ArrayList<x10aux::ref<x10::array::Point> > > uList =
      
    x10aux::ref<x10::util::ArrayList<x10aux::ref<x10::array::Point> > >((new (memset(x10aux::alloc<x10::util::ArrayList<x10aux::ref<x10::array::Point> > >(), 0, sizeof(x10::util::ArrayList<x10aux::ref<x10::array::Point> >))) x10::util::ArrayList<x10aux::ref<x10::array::Point> >()))
    ;
    
    //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorCall_c
    (uList)->::x10::util::ArrayList<x10aux::ref<x10::array::Point> >::_constructor();
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
    x10_int i39658min3965952274 = ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                                 FMGL(x)) - (ws)));
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
    x10_int i39658max3966052275 = ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                    FMGL(x);
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.For_c
    {
        x10_int i3965852276;
        for (
             //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
             i3965852276 = i39658min3965952274;
             ((i3965852276) <= (i39658max3966052275));
             
             //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalAssign_c
             i3965852276 =
               ((x10_int) ((i3965852276) + (((x10_int)1)))))
        {
            
            //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
            x10_int x52277 =
              i3965852276;
            
            //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
            x10_int i39642min3964352270 =
              ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                             FMGL(y)) - (ws)));
            
            //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
            x10_int i39642max3964452271 =
              ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                             FMGL(y)) + (ws)));
            
            //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.For_c
            {
                x10_int i3964252272;
                for (
                     //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                     i3964252272 =
                       i39642min3964352270;
                     ((i3964252272) <= (i39642max3964452271));
                     
                     //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalAssign_c
                     i3964252272 =
                       ((x10_int) ((i3964252272) + (((x10_int)1)))))
                {
                    
                    //#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                    x10_int y52273 =
                      i3964252272;
                    
                    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                    x10_int i39626min3962752266 =
                      ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                     FMGL(z)) - (ws)));
                    
                    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                    x10_int i39626max3962852267 =
                      ((x10_int) ((((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                     FMGL(z)) + (ws)));
                    
                    //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": polyglot.ast.For_c
                    {
                        x10_int i3962652268;
                        for (
                             //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                             i3962652268 =
                               i39626min3962752266;
                             ((i3962652268) <= (i39626max3962852267));
                             
                             //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalAssign_c
                             i3962652268 =
                               ((x10_int) ((i3962652268) + (((x10_int)1)))))
                        {
                            
                            //#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
                            x10_int z52269 =
                              i3962652268;
                            
                            //#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10If_c
                            if (((x52277) < (((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                               FMGL(x))) ||
                                (x10aux::struct_equals(x52277,
                                                       ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                                         FMGL(x))) &&
                                ((y52273) < (((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                               FMGL(y))) ||
                                (x10aux::struct_equals(x52277,
                                                       ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                                         FMGL(x))) &&
                                (x10aux::struct_equals(y52273,
                                                       ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                                         FMGL(y))) &&
                                ((z52269) < (((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
                                               FMGL(z))))
                            {
                                
                                //#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Call_c
                                uList->add(
                                  (__extension__ ({
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i05223152262 =
                                        x52277;
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i15223252263 =
                                        y52273;
                                      
                                      //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                      x10_int i25223352264 =
                                        z52269;
                                      (__extension__ ({
                                          
                                          //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
                                          x10aux::ref<x10::array::Point> alloc299415223452265 =
                                            
                                          x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
                                          ;
                                          
                                          //#line 126 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
                                          (alloc299415223452265)->::x10::array::Point::_constructor(
                                            i05223152262,
                                            i15223252263,
                                            i25223352264);
                                          alloc299415223452265;
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
    
    //#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
      FMGL(uList) = uList->toArray();
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::FmmLeafBox>
  au::edu::anu::mm::FmmLeafBox::au__edu__anu__mm__FmmLeafBox____au__edu__anu__mm__FmmLeafBox__this(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this);
    
}

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::FmmLeafBox::__fieldInitializers39455(
  ) {
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
      FMGL(atoms) = (__extension__ ({
        
        //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::util::ArrayList<au::edu::anu::chem::PointCharge> > alloc39576 =
          
        x10aux::ref<x10::util::ArrayList<au::edu::anu::chem::PointCharge> >((new (memset(x10aux::alloc<x10::util::ArrayList<au::edu::anu::chem::PointCharge> >(), 0, sizeof(x10::util::ArrayList<au::edu::anu::chem::PointCharge>))) x10::util::ArrayList<au::edu::anu::chem::PointCharge>()))
        ;
        
        //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10ConstructorCall_c
        (alloc39576)->::x10::util::ArrayList<au::edu::anu::chem::PointCharge>::_constructor();
        alloc39576;
    }))
    ;
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::FmmLeafBox>)this)->
      FMGL(uList) = X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::FmmLeafBox::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::FmmLeafBox::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::FmmLeafBox::_serialize_body(x10aux::serialization_buffer& buf) {
    au::edu::anu::mm::FmmBox::_serialize_body(buf);
    buf.write(this->FMGL(atoms));
    buf.write(this->FMGL(uList));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::FmmLeafBox::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::FmmLeafBox> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::FmmLeafBox>(), 0, sizeof(au::edu::anu::mm::FmmLeafBox))) au::edu::anu::mm::FmmLeafBox();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::FmmLeafBox::_deserialize_body(x10aux::deserialization_buffer& buf) {
    au::edu::anu::mm::FmmBox::_deserialize_body(buf);
    FMGL(atoms) = buf.read<x10aux::ref<x10::util::ArrayList<au::edu::anu::chem::PointCharge> > >();
    FMGL(uList) = buf.read<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > >();
}

x10aux::RuntimeType au::edu::anu::mm::FmmLeafBox::rtt;
void au::edu::anu::mm::FmmLeafBox::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<au::edu::anu::mm::FmmBox>()};
    rtt.initStageTwo("au.edu.anu.mm.FmmLeafBox",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
/* END of FmmLeafBox */
/*************************************************/
