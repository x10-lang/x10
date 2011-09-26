/*************************************************/
/* START of ExpansionRegion */
#include <au/edu/anu/mm/ExpansionRegion.h>

#include <x10/array/Region.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/lang/ArrayIndexOutOfBoundsException.h>
#include <x10/lang/String.h>
#include <x10/array/Point.h>
#include <x10/lang/Math.h>
#include <x10/lang/UnsupportedOperationException.h>
#include <x10/lang/IntRange.h>
#include <x10/array/RectRegion1D.h>
#include <x10/lang/Iterator.h>
#include <au/edu/anu/mm/ExpansionRegion__ExpansionRegionIterator.h>
#ifndef AU_EDU_ANU_MM_EXPANSIONREGION__CLOSURE__1_CLOSURE
#define AU_EDU_ANU_MM_EXPANSIONREGION__CLOSURE__1_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_mm_ExpansionRegion__closure__1 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, x10_int>::itable<au_edu_anu_mm_ExpansionRegion__closure__1> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10_int __apply(x10_int i) {
        
        //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(i, ((x10_int)0)))) {
            
            //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
            return ((x10_int)0);
            
        } else 
        //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(i, ((x10_int)1)))) {
            
            //#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
            return ((x10_int) -(saved_this->FMGL(p)));
            
        } else {
            
            //#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
            x10aux::throwException(x10aux::nullCheck(x10::lang::ArrayIndexOutOfBoundsException::_make(((((((x10aux::string_utils::lit("min: ")) + (i))) + (x10aux::string_utils::lit(" is not a valid rank for ")))) + (saved_this)))));
        }
        
    }
    
    // captured environment
    x10aux::ref<au::edu::anu::mm::ExpansionRegion> saved_this;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->saved_this);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_ExpansionRegion__closure__1* storage = x10aux::alloc<au_edu_anu_mm_ExpansionRegion__closure__1>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_ExpansionRegion__closure__1>(storage));
        x10aux::ref<au::edu::anu::mm::ExpansionRegion> that_saved_this = buf.read<x10aux::ref<au::edu::anu::mm::ExpansionRegion> >();
        x10aux::ref<au_edu_anu_mm_ExpansionRegion__closure__1> this_ = new (storage) au_edu_anu_mm_ExpansionRegion__closure__1(that_saved_this);
        return this_;
    }
    
    au_edu_anu_mm_ExpansionRegion__closure__1(x10aux::ref<au::edu::anu::mm::ExpansionRegion> saved_this) : saved_this(saved_this) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10:36-40";
    }

};

#endif // AU_EDU_ANU_MM_EXPANSIONREGION__CLOSURE__1_CLOSURE
#ifndef AU_EDU_ANU_MM_EXPANSIONREGION__CLOSURE__2_CLOSURE
#define AU_EDU_ANU_MM_EXPANSIONREGION__CLOSURE__2_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_mm_ExpansionRegion__closure__2 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, x10_int>::itable<au_edu_anu_mm_ExpansionRegion__closure__2> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10_int __apply(x10_int i) {
        
        //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(i, ((x10_int)0)))) {
            
            //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
            return saved_this->FMGL(p);
            
        } else 
        //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(i, ((x10_int)1)))) {
            
            //#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
            return saved_this->FMGL(p);
            
        } else {
            
            //#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
            x10aux::throwException(x10aux::nullCheck(x10::lang::ArrayIndexOutOfBoundsException::_make(((((((x10aux::string_utils::lit("max: ")) + (i))) + (x10aux::string_utils::lit(" is not a valid rank for ")))) + (saved_this)))));
        }
        
    }
    
    // captured environment
    x10aux::ref<au::edu::anu::mm::ExpansionRegion> saved_this;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->saved_this);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_ExpansionRegion__closure__2* storage = x10aux::alloc<au_edu_anu_mm_ExpansionRegion__closure__2>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_ExpansionRegion__closure__2>(storage));
        x10aux::ref<au::edu::anu::mm::ExpansionRegion> that_saved_this = buf.read<x10aux::ref<au::edu::anu::mm::ExpansionRegion> >();
        x10aux::ref<au_edu_anu_mm_ExpansionRegion__closure__2> this_ = new (storage) au_edu_anu_mm_ExpansionRegion__closure__2(that_saved_this);
        return this_;
    }
    
    au_edu_anu_mm_ExpansionRegion__closure__2(x10aux::ref<au::edu::anu::mm::ExpansionRegion> saved_this) : saved_this(saved_this) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10:42-46";
    }

};

#endif // AU_EDU_ANU_MM_EXPANSIONREGION__CLOSURE__2_CLOSURE
x10::lang::Iterable<x10aux::ref<x10::array::Point> >::itable<au::edu::anu::mm::ExpansionRegion >  au::edu::anu::mm::ExpansionRegion::_itable_0(&au::edu::anu::mm::ExpansionRegion::equals, &au::edu::anu::mm::ExpansionRegion::hashCode, &au::edu::anu::mm::ExpansionRegion::iterator, &au::edu::anu::mm::ExpansionRegion::toString, &au::edu::anu::mm::ExpansionRegion::typeName);
x10::lang::Any::itable<au::edu::anu::mm::ExpansionRegion >  au::edu::anu::mm::ExpansionRegion::_itable_1(&au::edu::anu::mm::ExpansionRegion::equals, &au::edu::anu::mm::ExpansionRegion::hashCode, &au::edu::anu::mm::ExpansionRegion::toString, &au::edu::anu::mm::ExpansionRegion::typeName);
x10aux::itable_entry au::edu::anu::mm::ExpansionRegion::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterable<x10aux::ref<x10::array::Point> > >, &_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::mm::ExpansionRegion>())};

//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c

//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::ExpansionRegion::getOrderedLock(
  ) {
    
    //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::ExpansionRegion.X10$class_lock_id1");
    x10_int __var580__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var580__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var581__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var581__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::ExpansionRegion.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::ExpansionRegion.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__582 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::ExpansionRegion::FMGL(X10__class_lock_id1__deserialize));


//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::ExpansionRegion::getStaticOrderedLock(
  ) {
    
    //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId59202 = au::edu::anu::mm::ExpansionRegion::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId59202);
    }))
    ;
    
}
 /* static type ExpansionRegion(rank: x10.lang.Int) = au.edu.anu.mm.ExpansionRegion{self.rank==rank}; */ 

//#line 16 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c

//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::ExpansionRegion::_constructor(
  x10_int p) {
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> this59207 =
          ((x10aux::ref<x10::array::Region>)this);
        {
            
            //#line 469 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
            (this59207)->::x10::lang::Object::_constructor();
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59207)->
              FMGL(rank) = ((x10_int)2);
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59207)->
              FMGL(rect) = false;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59207)->
              FMGL(zeroBased) = true;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59207)->
              FMGL(rail) = false;
        }
        
    }))
    ;
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::ExpansionRegion> this5920959243 =
          ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this);
        {
            
            //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5920959243)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
      FMGL(p) = p;
    
}
x10aux::ref<au::edu::anu::mm::ExpansionRegion> au::edu::anu::mm::ExpansionRegion::_make(
  x10_int p) {
    x10aux::ref<au::edu::anu::mm::ExpansionRegion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion))) au::edu::anu::mm::ExpansionRegion();
    this_->_constructor(p);
    return this_;
}



//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::ExpansionRegion::_constructor(
  x10_int p,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> this59216 =
          ((x10aux::ref<x10::array::Region>)this);
        {
            
            //#line 469 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
            (this59216)->::x10::lang::Object::_constructor();
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59216)->
              FMGL(rank) =
              ((x10_int)2);
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59216)->
              FMGL(rect) =
              false;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59216)->
              FMGL(zeroBased) =
              true;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this59216)->
              FMGL(rail) =
              false;
        }
        
    }))
    ;
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::ExpansionRegion> this5921859244 =
          ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this);
        {
            
            //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5921859244)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
      FMGL(p) =
      p;
    
    //#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::ExpansionRegion> au::edu::anu::mm::ExpansionRegion::_make(
  x10_int p,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::ExpansionRegion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion))) au::edu::anu::mm::ExpansionRegion();
    this_->_constructor(p,
    paramLock);
    return this_;
}



//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::ExpansionRegion::isConvex(
  ) {
    
    //#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::ExpansionRegion::isEmpty(
  ) {
    
    //#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return false;
    
}

//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >
  au::edu::anu::mm::ExpansionRegion::min(
  ) {
    
    //#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >(x10aux::ref<au_edu_anu_mm_ExpansionRegion__closure__1>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, x10_int> >(sizeof(au_edu_anu_mm_ExpansionRegion__closure__1)))au_edu_anu_mm_ExpansionRegion__closure__1(this)));
    
}

//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >
  au::edu::anu::mm::ExpansionRegion::max(
  ) {
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >(x10aux::ref<au_edu_anu_mm_ExpansionRegion__closure__2>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, x10_int> >(sizeof(au_edu_anu_mm_ExpansionRegion__closure__2)))au_edu_anu_mm_ExpansionRegion__closure__2(this)));
    
}

//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::ExpansionRegion::size(
  ) {
    
    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return ((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                       FMGL(p)) + (((x10_int)1))))) * (((x10_int) ((((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                                                      FMGL(p)) + (((x10_int)1)))))));
    
}

//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::ExpansionRegion::contains(
  x10aux::ref<x10::array::Point> p) {
    
    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(x10aux::nullCheck(p)->
                                 FMGL(rank),
                               ((x10_int)2))))
    {
        
        //#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
        return ((x10aux::nullCheck(p)->x10::array::Point::__apply(
                   ((x10_int)0))) >= (((x10_int)0))) &&
        ((x10aux::nullCheck(p)->x10::array::Point::__apply(
            ((x10_int)0))) <= (((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                 FMGL(p))) &&
        ((::abs(x10aux::nullCheck(p)->x10::array::Point::__apply(
                  ((x10_int)1)))) <= (x10aux::nullCheck(p)->x10::array::Point::__apply(
                                        ((x10_int)0))));
        
    }
    
    //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(((((x10aux::string_utils::lit("contains(")) + (p))) + (x10aux::string_utils::lit(")"))))));
}

//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::ExpansionRegion::contains(
  x10aux::ref<x10::array::Region> r) {
    
    //#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
    if (x10aux::instanceof<x10aux::ref<au::edu::anu::mm::ExpansionRegion> >(r) &&
        (x10aux::struct_equals(x10aux::nullCheck((x10aux::class_cast<x10aux::ref<au::edu::anu::mm::ExpansionRegion> >(r)))->
                                 FMGL(p),
                               ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                 FMGL(p))))
    {
        
        //#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
        return true;
        
    }
    
    //#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("contains(Region)"))));
}

//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::ExpansionRegion::complement(
  ) {
    
    //#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("complement()"))));
}

//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::ExpansionRegion::intersection(
  x10aux::ref<x10::array::Region> t) {
    
    //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("intersection()"))));
}

//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::ExpansionRegion::product(
  x10aux::ref<x10::array::Region> that) {
    
    //#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("product()"))));
}

//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::ExpansionRegion::translate(
  x10aux::ref<x10::array::Point> v) {
    
    //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("translate()"))));
}

//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::ExpansionRegion::projection(
  x10_int axis) {
    
    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Switch_c
    switch (axis) {
        
        //#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Case_c
        case ((x10_int)0): ;
        {
            
            //#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r59221 =
                  x10::lang::IntRange::_make(((x10_int)0), ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                             FMGL(p));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063159222 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063159222)->::x10::array::RectRegion1D::_constructor(
                      r59221->
                        FMGL(min),
                      r59221->
                        FMGL(max));
                    alloc2063159222;
                }))
                );
            }))
            ;
            
        }
        //#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Case_c
        case ((x10_int)1): ;
        {
            
            //#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r59223 =
                  x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                            FMGL(p))), ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                                         FMGL(p));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063159224 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063159224)->::x10::array::RectRegion1D::_constructor(
                      r59223->
                        FMGL(min),
                      r59223->
                        FMGL(max));
                    alloc2063159224;
                }))
                );
            }))
            ;
            
        }
        //#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Case_c
        default: ;
        {
            
            //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
            x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(((((x10aux::string_utils::lit("projection(")) + (axis))) + (x10aux::string_utils::lit(")"))))));
        }
    }
}

//#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::ExpansionRegion::eliminate(
  x10_int axis) {
    
    //#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Switch_c
    switch (axis) {
        
        //#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Case_c
        case ((x10_int)0): ;
        {
            
            //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r59225 =
                  x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                            FMGL(p))), ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                                         FMGL(p));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063159226 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063159226)->::x10::array::RectRegion1D::_constructor(
                      r59225->
                        FMGL(min),
                      r59225->
                        FMGL(max));
                    alloc2063159226;
                }))
                );
            }))
            ;
            
        }
        //#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Case_c
        case ((x10_int)1): ;
        {
            
            //#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 423 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10::lang::IntRange r59227 =
                  x10::lang::IntRange::_make(((x10_int)0), ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                             FMGL(p));
                x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::RectRegion1D> alloc2063159228 =
                      
                    x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                    ;
                    
                    //#line 424 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2063159228)->::x10::array::RectRegion1D::_constructor(
                      r59227->
                        FMGL(min),
                      r59227->
                        FMGL(max));
                    alloc2063159228;
                }))
                );
            }))
            ;
            
        }
        //#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Case_c
        default: ;
        {
            
            //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": polyglot.ast.Throw_c
            x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(((((x10aux::string_utils::lit("projection(")) + (axis))) + (x10aux::string_utils::lit(")"))))));
        }
    }
}

//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::ExpansionRegion::indexOf(
  x10aux::ref<x10::array::Point> pt) {
    
    //#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(x10aux::nullCheck(pt)->
                                  FMGL(rank),
                                ((x10_int)2))))
    {
        
        //#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
        return ((x10_int)-1);
        
    }
    
    //#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return ((x10_int) ((((x10_int) ((x10aux::nullCheck(pt)->x10::array::Point::__apply(
                                       ((x10_int)0))) * (x10aux::nullCheck(pt)->x10::array::Point::__apply(
                                                           ((x10_int)0)))))) + (x10aux::nullCheck(pt)->x10::array::Point::__apply(
                                                                                  ((x10_int)1)))));
    
}

//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::ExpansionRegion::boundingBox(
  ) {
    
    //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return x10::lang::IntRange::_make(((x10_int)0), ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                      FMGL(p))->x10::lang::IntRange::__times(
             x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                       FMGL(p))), ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                                    FMGL(p)));
    
}

//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::ExpansionRegion::computeBoundingBox(
  ) {
    
    //#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return x10::lang::IntRange::_make(((x10_int)0), ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                      FMGL(p))->x10::lang::IntRange::__times(
             x10::lang::IntRange::_make(((x10_int) -(((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                       FMGL(p))), ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                                    FMGL(p)));
    
}

//#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >
  au::edu::anu::mm::ExpansionRegion::iterator(
  ) {
    
    //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return x10aux::class_cast<x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > >((__extension__ ({
        
        //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> alloc54773 =
          
        x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>((new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator))) au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator()))
        ;
        
        //#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10ConstructorCall_c
        (alloc54773)->::au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_constructor(
          ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this),
          ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc54773;
    }))
    );
    
}

//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::mm::ExpansionRegion::toString(
  ) {
    
    //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return ((((x10aux::string_utils::lit("ExpansionRegion (p = ")) + (((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
                                                                        FMGL(p)))) + (x10aux::string_utils::lit(")")));
    
}

//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::ExpansionRegion>
  au::edu::anu::mm::ExpansionRegion::au__edu__anu__mm__ExpansionRegion____au__edu__anu__mm__ExpansionRegion__this(
  ) {
    
    //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this);
    
}

//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::ExpansionRegion::__fieldInitializers54336(
  ) {
    
    //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
}
const x10aux::serialization_id_t au::edu::anu::mm::ExpansionRegion::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::ExpansionRegion::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::ExpansionRegion::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::array::Region::_serialize_body(buf);
    buf.write(this->FMGL(p));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::ExpansionRegion::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::ExpansionRegion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion))) au::edu::anu::mm::ExpansionRegion();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::ExpansionRegion::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::array::Region::_deserialize_body(buf);
    FMGL(p) = buf.read<x10_int>();
}

x10aux::RuntimeType au::edu::anu::mm::ExpansionRegion::rtt;
void au::edu::anu::mm::ExpansionRegion::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::array::Region>()};
    rtt.initStageTwo("au.edu.anu.mm.ExpansionRegion",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
x10::lang::Fun_0_1<x10_int, x10_int>::itable<au_edu_anu_mm_ExpansionRegion__closure__1>au_edu_anu_mm_ExpansionRegion__closure__1::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_ExpansionRegion__closure__1::__apply, &au_edu_anu_mm_ExpansionRegion__closure__1::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_ExpansionRegion__closure__1::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >, &au_edu_anu_mm_ExpansionRegion__closure__1::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_ExpansionRegion__closure__1::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_ExpansionRegion__closure__1::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::Fun_0_1<x10_int, x10_int>::itable<au_edu_anu_mm_ExpansionRegion__closure__2>au_edu_anu_mm_ExpansionRegion__closure__2::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_ExpansionRegion__closure__2::__apply, &au_edu_anu_mm_ExpansionRegion__closure__2::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_ExpansionRegion__closure__2::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >, &au_edu_anu_mm_ExpansionRegion__closure__2::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_ExpansionRegion__closure__2::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_ExpansionRegion__closure__2::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

/* END of ExpansionRegion */
/*************************************************/
/*************************************************/
/* START of ExpansionRegion$ExpansionRegionIterator */
#include <au/edu/anu/mm/ExpansionRegion__ExpansionRegionIterator.h>

#include <x10/lang/Object.h>
#include <x10/lang/Iterator.h>
#include <x10/array/Point.h>
#include <x10/util/concurrent/Atomic.h>
#include <au/edu/anu/mm/ExpansionRegion.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Boolean.h>
x10::lang::Iterator<x10aux::ref<x10::array::Point> >::itable<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator >  au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_itable_0(&au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::equals, &au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::hasNext, &au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::hashCode, &au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::next, &au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::toString, &au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::typeName);
x10::lang::Any::itable<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator >  au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_itable_1(&au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::equals, &au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::hashCode, &au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::toString, &au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::typeName);
x10aux::itable_entry au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >, &_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>())};

//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::getOrderedLock(
  ) {
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator.X10$class_lock_id1");
    x10_int __var605__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var605__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var606__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var606__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__607 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__deserialize));


//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::getStaticOrderedLock(
  ) {
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId59229 = au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId59229);
    }))
    ;
    
}

//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c

//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c

//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldDecl_c

//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_constructor(
  x10aux::ref<au::edu::anu::mm::ExpansionRegion> out__,
  x10aux::ref<au::edu::anu::mm::ExpansionRegion> r)
{
    
    //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(out__) =
      out__;
    
    //#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> this5923059245 =
          ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this);
        {
            
            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5923059245)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5923059245)->
              FMGL(l) =
              ((x10_int)0);
            
            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5923059245)->
              FMGL(m) =
              ((x10_int)0);
        }
        
    }))
    ;
    
    //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(p) =
      x10aux::nullCheck(r)->
        FMGL(p);
    
    //#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(l) =
      ((x10_int)0);
    
    //#line 138 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(m) =
      ((x10_int)0);
    
}
x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_make(
  x10aux::ref<au::edu::anu::mm::ExpansionRegion> out__,
  x10aux::ref<au::edu::anu::mm::ExpansionRegion> r)
{
    x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator))) au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator();
    this_->_constructor(out__,
    r);
    return this_;
}



//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_constructor(
  x10aux::ref<au::edu::anu::mm::ExpansionRegion> out__,
  x10aux::ref<au::edu::anu::mm::ExpansionRegion> r,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(out__) =
      out__;
    
    //#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.AssignPropertyCall_c
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> this5923359246 =
          ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this);
        {
            
            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5923359246)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5923359246)->
              FMGL(l) =
              ((x10_int)0);
            
            //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5923359246)->
              FMGL(m) =
              ((x10_int)0);
        }
        
    }))
    ;
    
    //#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(p) =
      x10aux::nullCheck(r)->
        FMGL(p);
    
    //#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(l) =
      ((x10_int)0);
    
    //#line 138 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(m) =
      ((x10_int)0);
    
    //#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_make(
  x10aux::ref<au::edu::anu::mm::ExpansionRegion> out__,
  x10aux::ref<au::edu::anu::mm::ExpansionRegion> r,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator))) au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator();
    this_->_constructor(out__,
    r,
    paramLock);
    return this_;
}



//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::hasNext(
  ) {
    
    //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
    if (((((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
            FMGL(l)) <= (((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
                           FMGL(p))) && ((((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
                                            FMGL(m)) <= (((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
                                                           FMGL(l))))
    {
        
        //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
        return true;
        
    }
    else
    {
        
        //#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
        return false;
        
    }
    
}

//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Point> au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::next(
  ) {
    
    //#line 147 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Point> nextPoint =
      (__extension__ ({
        
        //#line 125 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
        x10_int i059236 =
          ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
            FMGL(l);
        
        //#line 125 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
        x10_int i159237 =
          ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
            FMGL(m);
        (__extension__ ({
            
            //#line 125 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Point> alloc2994059238 =
              
            x10aux::ref<x10::array::Point>((new (memset(x10aux::alloc<x10::array::Point>(), 0, sizeof(x10::array::Point))) x10::array::Point()))
            ;
            
            //#line 125 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10": x10.ast.X10ConstructorCall_c
            (alloc2994059238)->::x10::array::Point::_constructor(
              i059236,
              i159237);
            alloc2994059238;
        }))
        ;
    }))
    ;
    
    //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10If_c
    if (((((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
            FMGL(m)) < (((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
                          FMGL(l)))) {
        
        //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> x59239 =
              ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this);
            x10aux::nullCheck(x59239)->FMGL(m) =
              ((x10_int) ((x10aux::nullCheck(x59239)->
                             FMGL(m)) + (((x10_int)1))));
        }))
        ;
    } else {
        
        //#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> x59241 =
              ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this);
            x10aux::nullCheck(x59241)->FMGL(l) =
              ((x10_int) ((x10aux::nullCheck(x59241)->
                             FMGL(l)) + (((x10_int)1))));
        }))
        ;
        
        //#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
        ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
          FMGL(m) = ((x10_int) -(((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
                                   FMGL(l)));
    }
    
    //#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return nextPoint;
    
}

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>
  au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::au__edu__anu__mm__ExpansionRegion__ExpansionRegionIterator____au__edu__anu__mm__ExpansionRegion__ExpansionRegionIterator__this(
  ) {
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this);
    
}

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::ExpansionRegion>
  au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::au__edu__anu__mm__ExpansionRegion__ExpansionRegionIterator____au__edu__anu__mm__ExpansionRegion__this(
  ) {
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
             FMGL(out__);
    
}

//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::__fieldInitializers54335(
  ) {
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(l) = ((x10_int)0);
    
    //#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>)this)->
      FMGL(m) = ((x10_int)0);
}
const x10aux::serialization_id_t au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    buf.write(this->FMGL(p));
    buf.write(this->FMGL(l));
    buf.write(this->FMGL(m));
    buf.write(this->FMGL(out__));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>(), 0, sizeof(au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator))) au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    FMGL(p) = buf.read<x10_int>();
    FMGL(l) = buf.read<x10_int>();
    FMGL(m) = buf.read<x10_int>();
    FMGL(out__) = buf.read<x10aux::ref<au::edu::anu::mm::ExpansionRegion> >();
}

x10aux::RuntimeType au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::rtt;
void au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Object>(), x10aux::getRTT<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >()};
    rtt.initStageTwo("au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator",x10aux::RuntimeType::class_kind, 2, parents, 0, NULL, NULL);
}
/* END of ExpansionRegion$ExpansionRegionIterator */
/*************************************************/
