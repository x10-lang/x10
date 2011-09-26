/*************************************************/
/* START of MortonDist */
#include <au/edu/anu/mm/MortonDist.h>

#include <x10/array/Dist.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/array/PlaceGroup.h>
#include <x10/array/Region.h>
#include <x10/lang/Math.h>
#include <x10/lang/Double.h>
#include <x10/lang/Place.h>
#include <x10/lang/Runtime.h>
#include <au/edu/anu/mm/MortonDist__MortonSubregion.h>
#include <x10/lang/Sequence.h>
#include <x10/array/Array.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/array/RectRegion1D.h>
#include <x10/array/RectLayout.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/UnsupportedOperationException.h>
#include <x10/array/ConstantDist.h>
#include <x10/lang/Any.h>
#include <x10/lang/Boolean.h>
#include <x10/array/Point.h>
#include <x10/lang/String.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/Iterable.h>
#ifndef AU_EDU_ANU_MM_MORTONDIST__CLOSURE__1_CLOSURE
#define AU_EDU_ANU_MM_MORTONDIST__CLOSURE__1_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_mm_MortonDist__closure__1 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> >::itable<au_edu_anu_mm_MortonDist__closure__1> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10aux::ref<x10::array::Region> __apply(x10_int i) {
        
        //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return saved_this->au::edu::anu::mm::MortonDist::mortonRegionForPlace(
                 x10aux::nullCheck(saved_this->
                                     FMGL(pg))->__apply(
                   i));
        
    }
    
    // captured environment
    x10aux::ref<au::edu::anu::mm::MortonDist> saved_this;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->saved_this);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_MortonDist__closure__1* storage = x10aux::alloc<au_edu_anu_mm_MortonDist__closure__1>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_MortonDist__closure__1>(storage));
        x10aux::ref<au::edu::anu::mm::MortonDist> that_saved_this = buf.read<x10aux::ref<au::edu::anu::mm::MortonDist> >();
        x10aux::ref<au_edu_anu_mm_MortonDist__closure__1> this_ = new (storage) au_edu_anu_mm_MortonDist__closure__1(that_saved_this);
        return this_;
    }
    
    au_edu_anu_mm_MortonDist__closure__1(x10aux::ref<au::edu::anu::mm::MortonDist> saved_this) : saved_this(saved_this) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> > >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> > >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10:158";
    }

};

#endif // AU_EDU_ANU_MM_MORTONDIST__CLOSURE__1_CLOSURE
x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10::lang::Place>::itable<au::edu::anu::mm::MortonDist >  au::edu::anu::mm::MortonDist::_itable_0(&au::edu::anu::mm::MortonDist::equals, &au::edu::anu::mm::MortonDist::hashCode, &au::edu::anu::mm::MortonDist::__apply, &au::edu::anu::mm::MortonDist::toString, &au::edu::anu::mm::MortonDist::typeName);
x10::lang::Any::itable<au::edu::anu::mm::MortonDist >  au::edu::anu::mm::MortonDist::_itable_1(&au::edu::anu::mm::MortonDist::equals, &au::edu::anu::mm::MortonDist::hashCode, &au::edu::anu::mm::MortonDist::toString, &au::edu::anu::mm::MortonDist::typeName);
x10::lang::Iterable<x10aux::ref<x10::array::Point> >::itable<au::edu::anu::mm::MortonDist >  au::edu::anu::mm::MortonDist::_itable_2(&au::edu::anu::mm::MortonDist::equals, &au::edu::anu::mm::MortonDist::hashCode, &au::edu::anu::mm::MortonDist::iterator, &au::edu::anu::mm::MortonDist::toString, &au::edu::anu::mm::MortonDist::typeName);
x10aux::itable_entry au::edu::anu::mm::MortonDist::_itables[4] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10::lang::Place> >, &_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &_itable_1), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterable<x10aux::ref<x10::array::Point> > >, &_itable_2), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::mm::MortonDist>())};

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::MortonDist::getOrderedLock(
  ) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::MortonDist.X10$class_lock_id1");
    x10_int __var504__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var504__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var505__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var505__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::MortonDist.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::MortonDist.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__506 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__deserialize));


//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::MortonDist::getStaticOrderedLock(
  ) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId57688 = au::edu::anu::mm::MortonDist::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId57688);
    }))
    ;
    
}

//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c
/**
     * The place group for this distribution
     */
                                                        //#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c
                                                        /**
     * The number of binary digits per dimension in the Z-index. 
     */
                                                                                                                                     //#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c
                                                                                                                                     /**
     * Cached restricted region for the current place.
     */

//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MortonDist>
  au::edu::anu::mm::MortonDist::makeMorton(
  x10aux::ref<x10::array::Region> r) {
    
    //#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist> alloc45281 =
          
        x10aux::ref<au::edu::anu::mm::MortonDist>((new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist>(), 0, sizeof(au::edu::anu::mm::MortonDist))) au::edu::anu::mm::MortonDist()))
        ;
        
        //#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorCall_c
        (alloc45281)->::au::edu::anu::mm::MortonDist::_constructor(
          r,
          x10aux::class_cast_unchecked<x10aux::ref<x10::array::PlaceGroup> >(x10::array::PlaceGroup::
                                                                               FMGL(WORLD__get)()),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc45281;
    }))
    ;
    
}

//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MortonDist>
  au::edu::anu::mm::MortonDist::makeMorton(
  x10aux::ref<x10::array::Region> r,
  x10aux::ref<x10::array::PlaceGroup> pg) {
    
    //#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist> alloc45282 =
          
        x10aux::ref<au::edu::anu::mm::MortonDist>((new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist>(), 0, sizeof(au::edu::anu::mm::MortonDist))) au::edu::anu::mm::MortonDist()))
        ;
        
        //#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorCall_c
        (alloc45282)->::au::edu::anu::mm::MortonDist::_constructor(
          r,
          pg,
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc45282;
    }))
    ;
    
}

//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MortonDist::_constructor(
  x10aux::ref<x10::array::Region> r,
  x10aux::ref<x10::array::PlaceGroup> pg)
{
    
    //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Dist> this57978 =
          ((x10aux::ref<x10::array::Dist>)this);
        
        //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> region57977 =
          r;
        {
            
            //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10ConstructorCall_c
            (this57978)->::x10::lang::Object::_constructor();
            
            //#line 669 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57978)->
              FMGL(region) =
              region57977;
        }
        
    }))
    ;
    
    //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.AssignPropertyCall_c
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist> this5798058165 =
          ((x10aux::ref<au::edu::anu::mm::MortonDist>)this);
        {
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5798058165)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5798058165)->
              FMGL(regionForHere) =
              X10_NULL;
        }
        
    }))
    ;
    
    //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
      FMGL(dimDigits) =
      x10::lang::Math::log2(
        x10aux::double_utils::toInt(x10aux::math_utils::cbrt(((x10_double) (x10aux::nullCheck(r)->size())))));
    
    //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
      FMGL(pg) =
      pg;
    
}
x10aux::ref<au::edu::anu::mm::MortonDist> au::edu::anu::mm::MortonDist::_make(
  x10aux::ref<x10::array::Region> r,
  x10aux::ref<x10::array::PlaceGroup> pg)
{
    x10aux::ref<au::edu::anu::mm::MortonDist> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist>(), 0, sizeof(au::edu::anu::mm::MortonDist))) au::edu::anu::mm::MortonDist();
    this_->_constructor(r,
    pg);
    return this_;
}



//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MortonDist::_constructor(
  x10aux::ref<x10::array::Region> r,
  x10aux::ref<x10::array::PlaceGroup> pg,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Dist> this57984 =
          ((x10aux::ref<x10::array::Dist>)this);
        
        //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> region57983 =
          r;
        {
            
            //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10ConstructorCall_c
            (this57984)->::x10::lang::Object::_constructor();
            
            //#line 669 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57984)->
              FMGL(region) =
              region57983;
        }
        
    }))
    ;
    
    //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.AssignPropertyCall_c
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist> this5798658166 =
          ((x10aux::ref<au::edu::anu::mm::MortonDist>)this);
        {
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5798658166)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5798658166)->
              FMGL(regionForHere) =
              X10_NULL;
        }
        
    }))
    ;
    
    //#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
      FMGL(dimDigits) =
      x10::lang::Math::log2(
        x10aux::double_utils::toInt(x10aux::math_utils::cbrt(((x10_double) (x10aux::nullCheck(r)->size())))));
    
    //#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
      FMGL(pg) =
      pg;
    
    //#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::MortonDist> au::edu::anu::mm::MortonDist::_make(
  x10aux::ref<x10::array::Region> r,
  x10aux::ref<x10::array::PlaceGroup> pg,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::MortonDist> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist>(), 0, sizeof(au::edu::anu::mm::MortonDist))) au::edu::anu::mm::MortonDist();
    this_->_constructor(r,
    pg,
    paramLock);
    return this_;
}



//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::PlaceGroup> au::edu::anu::mm::MortonDist::places(
  ) {
    
    //#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
             FMGL(pg);
    
}

//#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist::numPlaces(
  ) {
    
    //#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                               FMGL(pg))->numPlaces();
    
}

//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist::get(
  x10::lang::Place p) {
    
    //#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(p, x10::lang::Place::_make(x10aux::here))))
    {
        
        //#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                     FMGL(regionForHere),
                                   X10_NULL)))
        {
            
            //#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
              FMGL(regionForHere) =
              ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::mortonRegionForPlace(
                x10::lang::Place::_make(x10aux::here));
        }
        
        //#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                 FMGL(regionForHere);
        
    }
    else
    {
        
        //#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::mortonRegionForPlace(
                 p);
        
    }
    
}

//#line 152 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist::mortonRegionForPlace(
  x10::lang::Place p) {
    
    //#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> alloc45283 =
          
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>((new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion))) au::edu::anu::mm::MortonDist__MortonSubregion()))
        ;
        
        //#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorCall_c
        (alloc45283)->::au::edu::anu::mm::MortonDist__MortonSubregion::_constructor(
          ((x10aux::ref<au::edu::anu::mm::MortonDist>)this),
          ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::getPlaceStart(
            p->
              FMGL(id)),
          ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::getPlaceEnd(
            p->
              FMGL(id)),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc45283;
    }))
    ;
    
}

//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Iterable<x10aux::ref<x10::array::Region> > >
  au::edu::anu::mm::MortonDist::regions(
  ) {
    
    //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Iterable<x10aux::ref<x10::array::Region> > > >(x10aux::nullCheck((__extension__ ({
                                                                                                                  
                                                                                                                  //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
                                                                                                                  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Region> > > alloc45284 =
                                                                                                                    
                                                                                                                  x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Region> > >((new (memset(x10aux::alloc<x10::array::Array<x10aux::ref<x10::array::Region> > >(), 0, sizeof(x10::array::Array<x10aux::ref<x10::array::Region> >))) x10::array::Array<x10aux::ref<x10::array::Region> >()))
                                                                                                                  ;
                                                                                                                  
                                                                                                                  //#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
                                                                                                                  (__extension__ ({
                                                                                                                      
                                                                                                                      //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                      x10_int size58103 =
                                                                                                                        x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                                                                                                            FMGL(pg))->numPlaces();
                                                                                                                      
                                                                                                                      //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                      x10aux::ref<x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> > > init58104 =
                                                                                                                        x10aux::class_cast_unchecked<x10aux::ref<x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> > > >(x10aux::ref<x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> > >(x10aux::ref<au_edu_anu_mm_MortonDist__closure__1>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> > >(sizeof(au_edu_anu_mm_MortonDist__closure__1)))au_edu_anu_mm_MortonDist__closure__1(this))));
                                                                                                                      {
                                                                                                                          
                                                                                                                          //#line 271 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                                                                                                                          (alloc45284)->::x10::lang::Object::_constructor();
                                                                                                                          
                                                                                                                          //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                          x10aux::ref<x10::array::Region> myReg58105 =
                                                                                                                            x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                                                                                                                              
                                                                                                                              //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                              x10aux::ref<x10::array::RectRegion1D> alloc1996258106 =
                                                                                                                                
                                                                                                                              x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                                                                                                                              ;
                                                                                                                              
                                                                                                                              //#line 273 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                                                                                                                              (alloc1996258106)->::x10::array::RectRegion1D::_constructor(
                                                                                                                                ((x10_int)0),
                                                                                                                                ((x10_int) ((size58103) - (((x10_int)1)))));
                                                                                                                              alloc1996258106;
                                                                                                                          }))
                                                                                                                          );
                                                                                                                          
                                                                                                                          //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                                                                                          x10aux::nullCheck(alloc45284)->
                                                                                                                            FMGL(region) =
                                                                                                                            myReg58105;
                                                                                                                          
                                                                                                                          //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                                                                                          x10aux::nullCheck(alloc45284)->
                                                                                                                            FMGL(rank) =
                                                                                                                            ((x10_int)1);
                                                                                                                          
                                                                                                                          //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                                                                                          x10aux::nullCheck(alloc45284)->
                                                                                                                            FMGL(rect) =
                                                                                                                            true;
                                                                                                                          
                                                                                                                          //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                                                                                          x10aux::nullCheck(alloc45284)->
                                                                                                                            FMGL(zeroBased) =
                                                                                                                            true;
                                                                                                                          
                                                                                                                          //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                                                                                          x10aux::nullCheck(alloc45284)->
                                                                                                                            FMGL(rail) =
                                                                                                                            true;
                                                                                                                          
                                                                                                                          //#line 274 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                                                                                          x10aux::nullCheck(alloc45284)->
                                                                                                                            FMGL(size) =
                                                                                                                            size58103;
                                                                                                                          
                                                                                                                          //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                                                                                          x10aux::nullCheck(alloc45284)->
                                                                                                                            FMGL(layout) =
                                                                                                                            (__extension__ ({
                                                                                                                              
                                                                                                                              //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                              x10::array::RectLayout alloc1996358107 =
                                                                                                                                
                                                                                                                              x10::array::RectLayout::_alloc();
                                                                                                                              
                                                                                                                              //#line 276 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                                                                                                                              (__extension__ ({
                                                                                                                                  
                                                                                                                                  //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                                                                                                                                  x10_int _max058116 =
                                                                                                                                    ((x10_int) ((size58103) - (((x10_int)1))));
                                                                                                                                  {
                                                                                                                                      
                                                                                                                                      //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(rank) =
                                                                                                                                        ((x10_int)1);
                                                                                                                                      
                                                                                                                                      //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(min0) =
                                                                                                                                        ((x10_int)0);
                                                                                                                                      
                                                                                                                                      //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(delta0) =
                                                                                                                                        ((x10_int) ((((x10_int) ((_max058116) - (((x10_int)0))))) + (((x10_int)1))));
                                                                                                                                      
                                                                                                                                      //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(size) =
                                                                                                                                        ((alloc1996358107->
                                                                                                                                            FMGL(delta0)) > (((x10_int)0)))
                                                                                                                                        ? (alloc1996358107->
                                                                                                                                             FMGL(delta0))
                                                                                                                                        : (((x10_int)0));
                                                                                                                                      
                                                                                                                                      //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(min1) =
                                                                                                                                        ((x10_int)0);
                                                                                                                                      
                                                                                                                                      //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(delta1) =
                                                                                                                                        ((x10_int)0);
                                                                                                                                      
                                                                                                                                      //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(min2) =
                                                                                                                                        ((x10_int)0);
                                                                                                                                      
                                                                                                                                      //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(delta2) =
                                                                                                                                        ((x10_int)0);
                                                                                                                                      
                                                                                                                                      //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(min3) =
                                                                                                                                        ((x10_int)0);
                                                                                                                                      
                                                                                                                                      //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(delta3) =
                                                                                                                                        ((x10_int)0);
                                                                                                                                      
                                                                                                                                      //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(min) =
                                                                                                                                        X10_NULL;
                                                                                                                                      
                                                                                                                                      //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                                                                                                                                      alloc1996358107->
                                                                                                                                        FMGL(delta) =
                                                                                                                                        X10_NULL;
                                                                                                                                  }
                                                                                                                                  
                                                                                                                              }))
                                                                                                                              ;
                                                                                                                              alloc1996358107;
                                                                                                                          }))
                                                                                                                          ;
                                                                                                                          
                                                                                                                          //#line 277 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                          x10_int n58108 =
                                                                                                                            (__extension__ ({
                                                                                                                              
                                                                                                                              //#line 277 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                              x10::array::RectLayout this58118 =
                                                                                                                                x10aux::nullCheck(alloc45284)->
                                                                                                                                  FMGL(layout);
                                                                                                                              this58118->
                                                                                                                                FMGL(size);
                                                                                                                          }))
                                                                                                                          ;
                                                                                                                          
                                                                                                                          //#line 278 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                          x10::util::IndexedMemoryChunk<x10aux::ref<x10::array::Region> > r58109 =
                                                                                                                            x10::util::IndexedMemoryChunk<void>::allocate<x10aux::ref<x10::array::Region> >(n58108, 8, false, false);
                                                                                                                          
                                                                                                                          //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                          x10_int i19980max199825811158167 =
                                                                                                                            ((x10_int) ((size58103) - (((x10_int)1))));
                                                                                                                          
                                                                                                                          //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.For_c
                                                                                                                          {
                                                                                                                              x10_int i199805811258168;
                                                                                                                              for (
                                                                                                                                   //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                                   i199805811258168 =
                                                                                                                                     ((x10_int)0);
                                                                                                                                   ((i199805811258168) <= (i19980max199825811158167));
                                                                                                                                   
                                                                                                                                   //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                                                                                                                   i199805811258168 =
                                                                                                                                     ((x10_int) ((i199805811258168) + (((x10_int)1)))))
                                                                                                                              {
                                                                                                                                  
                                                                                                                                  //#line 279 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                                                                                                                                  x10_int i5811358169 =
                                                                                                                                    i199805811258168;
                                                                                                                                  
                                                                                                                                  //#line 280 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                                                                                                                                  (r58109)->__set(i5811358169, x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> >::__apply(x10aux::nullCheck(init58104), 
                                                                                                                                    i5811358169));
                                                                                                                              }
                                                                                                                          }
                                                                                                                          
                                                                                                                          //#line 282 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
                                                                                                                          x10aux::nullCheck(alloc45284)->
                                                                                                                            FMGL(raw) =
                                                                                                                            r58109;
                                                                                                                      }
                                                                                                                      
                                                                                                                  }))
                                                                                                                  ;
                                                                                                                  alloc45284;
                                                                                                              }))
                                                                                                              )->x10::array::Array<x10aux::ref<x10::array::Region> >::sequence());
    
}

//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Dist> au::edu::anu::mm::MortonDist::restriction(
  x10aux::ref<x10::array::Region> r) {
    
    //#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("restriction(r:Region(rank))"))));
}

//#line 165 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Dist> au::edu::anu::mm::MortonDist::restriction(
  x10::lang::Place p) {
    
    //#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 64 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> r58119 =
          ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::get(
            p);
        x10aux::class_cast_unchecked<x10aux::ref<x10::array::Dist> >((__extension__ ({
            
            //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::ConstantDist> alloc2839358120 =
              
            x10aux::ref<x10::array::ConstantDist>((new (memset(x10aux::alloc<x10::array::ConstantDist>(), 0, sizeof(x10::array::ConstantDist))) x10::array::ConstantDist()))
            ;
            
            //#line 65 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.StmtExpr_c
            (__extension__ ({
                
                //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::Region> r58121 =
                  r58119;
                
                //#line 27 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10LocalDecl_c
                x10::lang::Place p58122 =
                  x10::lang::Place::_make(x10aux::here);
                {
                    
                    //#line 28 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10LocalDecl_c
                        x10aux::ref<x10::array::Region> region58124 =
                          r58121;
                        {
                            
                            //#line 668 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10ConstructorCall_c
                            (alloc2839358120)->::x10::lang::Object::_constructor();
                            
                            //#line 669 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10": x10.ast.X10FieldAssign_c
                            x10aux::nullCheck(alloc2839358120)->
                              FMGL(region) =
                              region58124;
                        }
                        
                    }))
                    ;
                    
                    //#line 29 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10": x10.ast.X10FieldAssign_c
                    x10aux::nullCheck(alloc2839358120)->
                      FMGL(onePlace) = p58122;
                }
                
            }))
            ;
            alloc2839358120;
        }))
        );
    }))
    ;
    
}

//#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::MortonDist::equals(
  x10aux::ref<x10::lang::Any> thatObj) {
    
    //#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if (!(x10aux::instanceof<x10aux::ref<au::edu::anu::mm::MortonDist> >(thatObj)))
    {
        
        //#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 171 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<au::edu::anu::mm::MortonDist> that =
      x10aux::class_cast<x10aux::ref<au::edu::anu::mm::MortonDist> >(thatObj);
    
    //#line 172 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return (x10aux::struct_equals(x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                      FMGL(region))->size(),
                                  x10aux::nullCheck(x10aux::nullCheck(that)->
                                                      FMGL(region))->size()));
    
}

//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist::getMortonIndex(
  x10aux::ref<x10::array::Point> p) {
    
    //#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(x10aux::nullCheck(p)->
                                  FMGL(rank),
                                ((x10_int)3))))
    {
        
        //#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
        x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("getMortonIndex(p{self.rank!=3})"))));
    }
    
    //#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int index = ((x10_int)0);
    
    //#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int digitMask = (__extension__ ({
        
        //#line 376 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int i58126 = ((x10_int) ((((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                        FMGL(dimDigits)) - (((x10_int)1))));
        ((x10_int) ((((x10_int)1)) << (0x1f & (i58126))));
    }))
    ;
    
    //#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.For_c
    {
        x10_int digit;
        for (
             //#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
             digit = ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                       FMGL(dimDigits); ((digit) > (((x10_int)0)));
             
             //#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
             digit =
               ((x10_int) ((digit) - (((x10_int)1)))))
        {
            
            //#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.For_c
            {
                x10_int dim;
                for (
                     //#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
                     dim =
                       ((x10_int)0);
                     ((dim) < (((x10_int)3)));
                     
                     //#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
                     dim =
                       ((x10_int) ((dim) + (((x10_int)1)))))
                {
                    
                    //#line 191 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
                    x10_int thisDim =
                      ((x10_int) ((digitMask) & (x10aux::nullCheck(p)->x10::array::Point::__apply(
                                                   dim))));
                    
                    //#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
                    index =
                      ((x10_int) ((index) | (((x10_int) ((thisDim) << (0x1f & (((x10_int) ((((x10_int) ((digit) * (((x10_int)2))))) - (dim))))))))));
                }
            }
            
            //#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
            digitMask =
              ((x10_int) ((digitMask) >> (0x1f & (((x10_int)1)))));
        }
    }
    
    //#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return index;
    
}

//#line 209 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist::getMortonIndex(
  x10_int i0,
  x10_int i1,
  x10_int i2) {
    
    //#line 210 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int index = ((x10_int)0);
    
    //#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int digitMask = (__extension__ ({
        
        //#line 376 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
        x10_int i58127 = ((x10_int) ((((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                        FMGL(dimDigits)) - (((x10_int)1))));
        ((x10_int) ((((x10_int)1)) << (0x1f & (i58127))));
    }))
    ;
    
    //#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.For_c
    {
        x10_int digit;
        for (
             //#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
             digit = ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                       FMGL(dimDigits); ((digit) > (((x10_int)0)));
             
             //#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
             digit =
               ((x10_int) ((digit) - (((x10_int)1)))))
        {
            
            //#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
            x10_int dim0 =
              ((x10_int) ((digitMask) & (i0)));
            
            //#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
            index =
              ((x10_int) ((index) | (((x10_int) ((dim0) << (0x1f & (((x10_int) ((digit) * (((x10_int)2)))))))))));
            
            //#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
            x10_int dim1 =
              ((x10_int) ((digitMask) & (i1)));
            
            //#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
            index =
              ((x10_int) ((index) | (((x10_int) ((dim1) << (0x1f & (((x10_int) ((((x10_int) ((digit) * (((x10_int)2))))) - (((x10_int)1)))))))))));
            
            //#line 217 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
            x10_int dim2 =
              ((x10_int) ((digitMask) & (i2)));
            
            //#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
            index =
              ((x10_int) ((index) | (((x10_int) ((dim2) << (0x1f & (((x10_int) ((((x10_int) ((digit) * (((x10_int)2))))) - (((x10_int)2)))))))))));
            
            //#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
            digitMask =
              ((x10_int) ((digitMask) >> (0x1f & (((x10_int)1)))));
        }
    }
    
    //#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return index;
    
}

//#line 228 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Point> au::edu::anu::mm::MortonDist::getPoint(
  x10_int index) {
    
    //#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Array<x10_int> > p =
      
    x10aux::ref<x10::array::Array<x10_int> >((new (memset(x10aux::alloc<x10::array::Array<x10_int> >(), 0, sizeof(x10::array::Array<x10_int>))) x10::array::Array<x10_int>()))
    ;
    
    //#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        {
            
            //#line 243 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
            (p)->::x10::lang::Object::_constructor();
            
            //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<x10::array::Region> myReg58129 =
              x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10aux::ref<x10::array::RectRegion1D> alloc1996058130 =
                  
                x10aux::ref<x10::array::RectRegion1D>((new (memset(x10aux::alloc<x10::array::RectRegion1D>(), 0, sizeof(x10::array::RectRegion1D))) x10::array::RectRegion1D()))
                ;
                
                //#line 245 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10ConstructorCall_c
                (alloc1996058130)->::x10::array::RectRegion1D::_constructor(
                  ((x10_int)0),
                  ((x10_int) ((((x10_int)3)) - (((x10_int)1)))));
                alloc1996058130;
            }))
            );
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            p->FMGL(region) = myReg58129;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            p->FMGL(rank) = ((x10_int)1);
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            p->FMGL(rect) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            p->FMGL(zeroBased) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            p->FMGL(rail) = true;
            
            //#line 247 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            p->FMGL(size) = ((x10_int)3);
            
            //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            p->FMGL(layout) = (__extension__ ({
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout alloc1996158131 =
                  
                x10::array::RectLayout::_alloc();
                
                //#line 249 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.StmtExpr_c
                (__extension__ ({
                    
                    //#line 97 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10LocalDecl_c
                    x10_int _max058135 = ((x10_int) ((((x10_int)3)) - (((x10_int)1))));
                    {
                        
                        //#line 98 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(rank) = ((x10_int)1);
                        
                        //#line 99 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(min0) = ((x10_int)0);
                        
                        //#line 100 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(delta0) = ((x10_int) ((((x10_int) ((_max058135) - (((x10_int)0))))) + (((x10_int)1))));
                        
                        //#line 101 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(size) = ((alloc1996158131->
                                           FMGL(delta0)) > (((x10_int)0)))
                          ? (alloc1996158131->
                               FMGL(delta0))
                          : (((x10_int)0));
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(min1) = ((x10_int)0);
                        
                        //#line 103 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(delta1) = ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(min2) = ((x10_int)0);
                        
                        //#line 104 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(delta2) = ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(min3) = ((x10_int)0);
                        
                        //#line 105 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(delta3) = ((x10_int)0);
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(min) = X10_NULL;
                        
                        //#line 106 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10": x10.ast.X10FieldAssign_c
                        alloc1996158131->
                          FMGL(delta) = X10_NULL;
                    }
                    
                }))
                ;
                alloc1996158131;
            }))
            ;
            
            //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
            x10_int n58132 = (__extension__ ({
                
                //#line 250 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                x10::array::RectLayout this58137 =
                  p->
                    FMGL(layout);
                this58137->FMGL(size);
            }))
            ;
            
            //#line 251 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10FieldAssign_c
            p->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<x10_int >(n58132, 8, false, true);
        }
        
    }))
    ;
    
    //#line 231 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int digitMask = ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                         FMGL(region))->size()) / x10aux::zeroCheck(((x10_int)2))));
    
    //#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.For_c
    {
        x10_int digit;
        for (
             //#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
             digit = ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                       FMGL(dimDigits); ((digit) > (((x10_int)0)));
             
             //#line 232 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
             digit =
               ((x10_int) ((digit) - (((x10_int)1)))))
        {
            
            //#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.For_c
            {
                x10_int dim;
                for (
                     //#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
                     dim =
                       ((x10_int)0);
                     ((dim) < (((x10_int)3)));
                     
                     //#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
                     dim =
                       ((x10_int) ((dim) + (((x10_int)1)))))
                {
                    
                    //#line 234 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
                    x10_int thisDim =
                      ((x10_int) ((digitMask) & (index)));
                    
                    //#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
                    (__extension__ ({
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int i058146 =
                          dim;
                        
                        //#line 509 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int v58147 =
                          ((x10_int) (((__extension__ ({
                            
                            //#line 410 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int i058138 =
                              dim;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                            x10_int ret58139;
                            
                            //#line 409 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Labeled_c
                            goto __ret58140; __ret58140: {
                            {
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                                ret58139 =
                                  (p->
                                     FMGL(raw))->__apply(i058138);
                                
                                //#line 413 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": polyglot.ast.Branch_c
                                goto __ret58140_end_;
                            }goto __ret58140_end_; __ret58140_end_: ;
                            }
                            ret58139;
                            }))
                            ) | (((x10_int) ((thisDim) >> (0x1f & (((x10_int) ((((x10_int) ((digit) * (((x10_int)2))))) - (dim))))))))));
                            
                        
                        //#line 508 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalDecl_c
                        x10_int ret58148;
                        {
                            
                            //#line 512 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10Call_c
                            (p->
                               FMGL(raw))->__set(i058146, v58147);
                            
                            //#line 519 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10": x10.ast.X10LocalAssign_c
                            ret58148 =
                              v58147;
                        }
                        ret58148;
                        }))
                        ;
                    
                    //#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
                    digitMask =
                      ((x10_int) ((digitMask) >> (0x1f & (((x10_int)1)))));
                    }
                }
                
            }
        }
        
    
    //#line 240 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10::array::Point::make<x10_int >(
             p);
    }
    

//#line 243 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist::getPlaceStart(
  x10_int placeId) {
    
    //#line 244 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int blockSize = ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                         FMGL(region))->size()) / x10aux::zeroCheck(x10aux::num_hosts)));
    
    //#line 245 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int numLargerBlocks = ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                               FMGL(region))->size()) % x10aux::zeroCheck(x10aux::num_hosts)));
    
    //#line 246 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if (((placeId) < (numLargerBlocks))) {
        
        //#line 247 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return ((x10_int) ((placeId) * (((x10_int) ((blockSize) + (((x10_int)1)))))));
        
    } else {
        
        //#line 249 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10_int firstPortion = ((x10_int) ((numLargerBlocks) * (((x10_int) ((blockSize) + (((x10_int)1)))))));
        
        //#line 250 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return ((x10_int) ((firstPortion) + (((x10_int) ((((x10_int) ((placeId) - (numLargerBlocks)))) * (blockSize))))));
        
    }
    
}

//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist::getPlaceEnd(
  x10_int placeId) {
    
    //#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int blockSize = ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                         FMGL(region))->size()) / x10aux::zeroCheck(x10aux::num_hosts)));
    
    //#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int numLargerBlocks = ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                               FMGL(region))->size()) % x10aux::zeroCheck(x10aux::num_hosts)));
    
    //#line 257 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if (((placeId) < (numLargerBlocks))) {
        
        //#line 258 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return ((x10_int) ((((x10_int) ((((x10_int) ((placeId) + (((x10_int)1))))) * (((x10_int) ((blockSize) + (((x10_int)1)))))))) - (((x10_int)1))));
        
    } else {
        
        //#line 260 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10_int firstPortion = ((x10_int) ((numLargerBlocks) * (((x10_int) ((blockSize) + (((x10_int)1)))))));
        
        //#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return ((x10_int) ((((x10_int) ((firstPortion) + (((x10_int) ((((x10_int) ((((x10_int) ((placeId) - (numLargerBlocks)))) + (((x10_int)1))))) * (blockSize))))))) - (((x10_int)1))));
        
    }
    
}

//#line 265 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10::lang::Place au::edu::anu::mm::MortonDist::getPlaceForIndex(
  x10_int index) {
    
    //#line 266 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int blockSize = ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                         FMGL(region))->size()) / x10aux::zeroCheck(x10aux::num_hosts)));
    
    //#line 267 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int numLargerBlocks = ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
                                                               FMGL(region))->size()) % x10aux::zeroCheck(x10aux::num_hosts)));
    
    //#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int firstPart = ((x10_int) ((numLargerBlocks) * (((x10_int) ((blockSize) + (((x10_int)1)))))));
    
    //#line 269 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if (((index) > (firstPart))) {
        
        //#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10LocalDecl_c
            x10_int id58155 = ((x10_int) ((numLargerBlocks) + (((x10_int) ((((x10_int) ((index) - (firstPart)))) / x10aux::zeroCheck(blockSize))))));
            (__extension__ ({
                
                //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10LocalDecl_c
                x10::lang::Place alloc3180658156 =
                  
                x10::lang::Place::_alloc();
                
                //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10ConstructorCall_c
                (alloc3180658156)->::x10::lang::Place::_constructor(
                  id58155);
                alloc3180658156;
            }))
            ;
        }))
        ;
        
    } else {
        
        //#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10LocalDecl_c
            x10_int id58157 = ((x10_int) ((index) / x10aux::zeroCheck(((x10_int) ((blockSize) + (((x10_int)1)))))));
            (__extension__ ({
                
                //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10LocalDecl_c
                x10::lang::Place alloc3180658158 =
                  
                x10::lang::Place::_alloc();
                
                //#line 127 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10": x10.ast.X10ConstructorCall_c
                (alloc3180658158)->::x10::lang::Place::_constructor(
                  id58157);
                alloc3180658158;
            }))
            ;
        }))
        ;
        
    }
    
}

//#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10::lang::Place au::edu::anu::mm::MortonDist::__apply(
  x10aux::ref<x10::array::Point> pt) {
    
    //#line 278 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int index = ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::getMortonIndex(
                      pt);
    
    //#line 279 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::getPlaceForIndex(
             index);
    
}

//#line 282 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist::offset(
  x10aux::ref<x10::array::Point> pt) {
    
    //#line 284 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int offset = x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::get(
                                         x10::lang::Place::_make(x10aux::here)))->indexOf(
                       pt);
    
    //#line 285 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(offset, ((x10_int)-1))))
    {
     
    }
    
    //#line 289 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return offset;
    
}

//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist::offset(
  x10_int i0,
  x10_int i1,
  x10_int i2) {
    
    //#line 294 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int offset = x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::get(
                                         x10::lang::Place::_make(x10aux::here)))->indexOf(
                       i0,
                       i1,
                       i2);
    
    //#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if ((x10aux::struct_equals(offset, ((x10_int)-1))))
    {
     
    }
    
    //#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return offset;
    
}

//#line 302 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist::maxOffset(
  ) {
    
    //#line 303 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::array::Region> r = ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::get(
                                          x10::lang::Place::_make(x10aux::here));
    
    //#line 304 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10_int) ((x10aux::nullCheck(r)->size()) - (((x10_int)1))));
    
}

//#line 307 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::mm::MortonDist::toString(
  ) {
    
    //#line 308 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10aux::ref<x10::lang::String> s = x10aux::string_utils::lit("MortonDist(");
    
    //#line 309 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_boolean first = true;
    
    //#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.For_c
    {
        x10aux::ref<x10::lang::Iterator<x10::lang::Place> > p45286;
        for (
             //#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
             p45286 = x10aux::nullCheck((__extension__ ({
                          
                          //#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
                          x10aux::ref<au::edu::anu::mm::MortonDist> this58160 =
                            ((x10aux::ref<au::edu::anu::mm::MortonDist>)this);
                          x10aux::nullCheck(this58160)->
                            FMGL(pg);
                      }))
                      )->iterator(); x10::lang::Iterator<x10::lang::Place>::hasNext(x10aux::nullCheck(p45286));
             ) {
            
            //#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
            x10::lang::Place p = x10::lang::Iterator<x10::lang::Place>::next(x10aux::nullCheck(p45286));
            
            //#line 311 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
            if (!(first)) {
                
                //#line 311 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
                s = ((s) + (x10aux::string_utils::lit(",")));
            }
            
            //#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
            s = ((s) + (((((((x10aux::string_utils::lit("")) + (((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->au::edu::anu::mm::MortonDist::get(
                                                                  p)))) + (x10aux::string_utils::lit("->")))) + (p->
                                                                                                                   FMGL(id)))));
            
            //#line 313 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
            first = false;
        }
    }
    
    //#line 315 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalAssign_c
    s = ((s) + (x10aux::string_utils::lit(")")));
    
    //#line 316 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return s;
    
}

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MortonDist>
  au::edu::anu::mm::MortonDist::au__edu__anu__mm__MortonDist____au__edu__anu__mm__MortonDist__this(
  ) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::MortonDist>)this);
    
}

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::MortonDist::__fieldInitializers44486(
  ) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist>)this)->
      FMGL(regionForHere) = X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::MortonDist::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::MortonDist::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::MortonDist::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::array::Dist::_serialize_body(buf);
    buf.write(this->FMGL(pg));
    buf.write(this->FMGL(dimDigits));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::MortonDist::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::MortonDist> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist>(), 0, sizeof(au::edu::anu::mm::MortonDist))) au::edu::anu::mm::MortonDist();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::MortonDist::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::array::Dist::_deserialize_body(buf);
    FMGL(pg) = buf.read<x10aux::ref<x10::array::PlaceGroup> >();
    FMGL(dimDigits) = buf.read<x10_int>();
    
}

x10aux::RuntimeType au::edu::anu::mm::MortonDist::rtt;
void au::edu::anu::mm::MortonDist::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::array::Dist>()};
    rtt.initStageTwo("au.edu.anu.mm.MortonDist",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> >::itable<au_edu_anu_mm_MortonDist__closure__1>au_edu_anu_mm_MortonDist__closure__1::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_MortonDist__closure__1::__apply, &au_edu_anu_mm_MortonDist__closure__1::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_MortonDist__closure__1::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10aux::ref<x10::array::Region> > >, &au_edu_anu_mm_MortonDist__closure__1::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_MortonDist__closure__1::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_MortonDist__closure__1::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

/* END of MortonDist */
/*************************************************/
/*************************************************/
/* START of MortonDist$MortonSubregion$MortonSubregionIterator */
#include <au/edu/anu/mm/MortonDist__MortonSubregion__MortonSubregionIterator.h>

#include <x10/lang/Object.h>
#include <x10/lang/Iterator.h>
#include <x10/array/Point.h>
#include <x10/util/concurrent/Atomic.h>
#include <au/edu/anu/mm/MortonDist__MortonSubregion.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Boolean.h>
#include <au/edu/anu/mm/MortonDist.h>
x10::lang::Iterator<x10aux::ref<x10::array::Point> >::itable<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator >  au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_itable_0(&au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::equals, &au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::hasNext, &au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::hashCode, &au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::next, &au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::toString, &au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::typeName);
x10::lang::Any::itable<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator >  au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_itable_1(&au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::equals, &au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::hashCode, &au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::toString, &au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::typeName);
x10aux::itable_entry au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >, &_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>())};

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c

//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c

//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::getOrderedLock(
  ) {
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator.X10$class_lock_id1");
    x10_int __var532__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var532__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var533__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var533__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__534 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::FMGL(X10__class_lock_id1__deserialize));


//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::getStaticOrderedLock(
  ) {
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId57968 = au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId57968);
    }))
    ;
    
}

//#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c

//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_constructor(
  x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> out__,
  x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> r)
{
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
      FMGL(out__) =
      out__;
    
    //#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.AssignPropertyCall_c
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> this5796958163 =
          ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this);
        {
            
            //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5796958163)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5796958163)->
              FMGL(index) =
              ((x10_int)0);
        }
        
    }))
    ;
    
    //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
      FMGL(index) =
      ((x10_int) ((x10aux::nullCheck(r)->
                     FMGL(start)) - (((x10_int)1))));
    
}
x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_make(
  x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> out__,
  x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> r)
{
    x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator))) au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator();
    this_->_constructor(out__,
    r);
    return this_;
}



//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_constructor(
  x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> out__,
  x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> r,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
      FMGL(out__) =
      out__;
    
    //#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorCall_c
    (((x10aux::ref<x10::lang::Object>)this))->::x10::lang::Object::_constructor();
    
    //#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.AssignPropertyCall_c
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> this5797258164 =
          ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this);
        {
            
            //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5797258164)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
            
            //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5797258164)->
              FMGL(index) =
              ((x10_int)0);
        }
        
    }))
    ;
    
    //#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
      FMGL(index) =
      ((x10_int) ((x10aux::nullCheck(r)->
                     FMGL(start)) - (((x10_int)1))));
    
    //#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_make(
  x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> out__,
  x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> r,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator))) au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator();
    this_->_constructor(out__,
    r,
    paramLock);
    return this_;
}



//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::hasNext(
  ) {
    
    //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if (((((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
            FMGL(index)) < (x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
                                                FMGL(out__))->
                              FMGL(end))))
    {
        
        //#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return true;
        
    }
    else
    {
        
        //#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return false;
        
    }
    
}

//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Point> au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::next(
  ) {
    
    //#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10aux::nullCheck(x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
                                                 FMGL(out__))->
                               FMGL(out__))->au::edu::anu::mm::MortonDist::getPoint(
             (__extension__ ({
                 
                 //#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
                 x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> x57975 =
                   ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this);
                 x10aux::nullCheck(x57975)->
                   FMGL(index) =
                   ((x10_int) ((x10aux::nullCheck(x57975)->
                                  FMGL(index)) + (((x10_int)1))));
             }))
             );
    
}

//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>
  au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::au__edu__anu__mm__MortonDist__MortonSubregion__MortonSubregionIterator____au__edu__anu__mm__MortonDist__MortonSubregion__MortonSubregionIterator__this(
  ) {
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this);
    
}

//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>
  au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::au__edu__anu__mm__MortonDist__MortonSubregion__MortonSubregionIterator____au__edu__anu__mm__MortonDist__MortonSubregion__this(
  ) {
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
             FMGL(out__);
    
}

//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MortonDist>
  au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::au__edu__anu__mm__MortonDist__MortonSubregion__MortonSubregionIterator____au__edu__anu__mm__MortonDist__this(
  ) {
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
                               FMGL(out__))->
             FMGL(out__);
    
}

//#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::__fieldInitializers44484(
  ) {
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
    
    //#line 101 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>)this)->
      FMGL(index) = ((x10_int)0);
}
const x10aux::serialization_id_t au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
    buf.write(this->FMGL(index));
    buf.write(this->FMGL(out__));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator))) au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
    FMGL(index) = buf.read<x10_int>();
    FMGL(out__) = buf.read<x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> >();
}

x10aux::RuntimeType au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::rtt;
void au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Object>(), x10aux::getRTT<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >()};
    rtt.initStageTwo("au.edu.anu.mm.MortonDist.MortonSubregion.MortonSubregionIterator",x10aux::RuntimeType::class_kind, 2, parents, 0, NULL, NULL);
}
/* END of MortonDist$MortonSubregion$MortonSubregionIterator */
/*************************************************/
/*************************************************/
/* START of MortonDist$MortonSubregion */
#include <au/edu/anu/mm/MortonDist__MortonSubregion.h>

#include <x10/array/Region.h>
#include <x10/util/concurrent/Atomic.h>
#include <au/edu/anu/mm/MortonDist.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/array/Dist.h>
#include <x10/lang/Boolean.h>
#include <x10/array/Point.h>
#include <x10/lang/UnsupportedOperationException.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/array/EmptyRegion.h>
#include <x10/lang/Iterator.h>
#include <au/edu/anu/mm/MortonDist__MortonSubregion__MortonSubregionIterator.h>
#include <x10/lang/String.h>
#ifndef AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION__CLOSURE__2_CLOSURE
#define AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION__CLOSURE__2_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_mm_MortonDist__MortonSubregion__closure__2 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, x10_int>::itable<au_edu_anu_mm_MortonDist__MortonSubregion__closure__2> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10_int __apply(x10_int i) {
        
        //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return ((x10_int)0);
        
    }
    
    // captured environment
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_MortonDist__MortonSubregion__closure__2* storage = x10aux::alloc<au_edu_anu_mm_MortonDist__MortonSubregion__closure__2>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_MortonDist__MortonSubregion__closure__2>(storage));
        x10aux::ref<au_edu_anu_mm_MortonDist__MortonSubregion__closure__2> this_ = new (storage) au_edu_anu_mm_MortonDist__MortonSubregion__closure__2();
        return this_;
    }
    
    au_edu_anu_mm_MortonDist__MortonSubregion__closure__2() { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10:61";
    }

};

#endif // AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION__CLOSURE__2_CLOSURE
#ifndef AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION__CLOSURE__3_CLOSURE
#define AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION__CLOSURE__3_CLOSURE
#include <x10/lang/Closure.h>
#include <x10/lang/Fun_0_1.h>
class au_edu_anu_mm_MortonDist__MortonSubregion__closure__3 : public x10::lang::Closure {
    public:
    
    static x10::lang::Fun_0_1<x10_int, x10_int>::itable<au_edu_anu_mm_MortonDist__MortonSubregion__closure__3> _itable;
    static x10aux::itable_entry _itables[2];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    // closure body
    x10_int __apply(x10_int i) {
        
        //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return (__extension__ ({
            
            //#line 376 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
            x10_int i57708 = x10aux::nullCheck(saved_this->FMGL(out__))->
                               FMGL(dimDigits);
            ((x10_int) ((((x10_int)1)) << (0x1f & (i57708))));
        }))
        ;
        
    }
    
    // captured environment
    x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> saved_this;
    
    x10aux::serialization_id_t _get_serialization_id() {
        return _serialization_id;
    }
    
    void _serialize_body(x10aux::serialization_buffer &buf) {
        buf.write(this->saved_this);
    }
    
    template<class __T> static x10aux::ref<__T> _deserialize(x10aux::deserialization_buffer &buf) {
        au_edu_anu_mm_MortonDist__MortonSubregion__closure__3* storage = x10aux::alloc<au_edu_anu_mm_MortonDist__MortonSubregion__closure__3>();
        buf.record_reference(x10aux::ref<au_edu_anu_mm_MortonDist__MortonSubregion__closure__3>(storage));
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> that_saved_this = buf.read<x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> >();
        x10aux::ref<au_edu_anu_mm_MortonDist__MortonSubregion__closure__3> this_ = new (storage) au_edu_anu_mm_MortonDist__MortonSubregion__closure__3(that_saved_this);
        return this_;
    }
    
    au_edu_anu_mm_MortonDist__MortonSubregion__closure__3(x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> saved_this) : saved_this(saved_this) { }
    
    static const x10aux::serialization_id_t _serialization_id;
    
    static const x10aux::RuntimeType* getRTT() { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    virtual const x10aux::RuntimeType *_type() const { return x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >(); }
    
    x10aux::ref<x10::lang::String> toString() {
        return x10aux::string_utils::lit(this->toNativeString());
    }
    
    const char* toNativeString() {
        return "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10:62";
    }

};

#endif // AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION__CLOSURE__3_CLOSURE
x10::lang::Iterable<x10aux::ref<x10::array::Point> >::itable<au::edu::anu::mm::MortonDist__MortonSubregion >  au::edu::anu::mm::MortonDist__MortonSubregion::_itable_0(&au::edu::anu::mm::MortonDist__MortonSubregion::equals, &au::edu::anu::mm::MortonDist__MortonSubregion::hashCode, &au::edu::anu::mm::MortonDist__MortonSubregion::iterator, &au::edu::anu::mm::MortonDist__MortonSubregion::toString, &au::edu::anu::mm::MortonDist__MortonSubregion::typeName);
x10::lang::Any::itable<au::edu::anu::mm::MortonDist__MortonSubregion >  au::edu::anu::mm::MortonDist__MortonSubregion::_itable_1(&au::edu::anu::mm::MortonDist__MortonSubregion::equals, &au::edu::anu::mm::MortonDist__MortonSubregion::hashCode, &au::edu::anu::mm::MortonDist__MortonSubregion::toString, &au::edu::anu::mm::MortonDist__MortonSubregion::typeName);
x10aux::itable_entry au::edu::anu::mm::MortonDist__MortonSubregion::_itables[3] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterable<x10aux::ref<x10::array::Point> > >, &_itable_0), x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &_itable_1), x10aux::itable_entry(NULL, (void*)x10aux::getRTT<au::edu::anu::mm::MortonDist__MortonSubregion>())};

//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock> au::edu::anu::mm::MortonDist__MortonSubregion::getOrderedLock(
  ) {
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10::util::concurrent::OrderedLock::getObjectLock(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                                               FMGL(X10__object_lock_id0));
    
}

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c
x10_int au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1);
void au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__do_init)() {
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZING;
    _SI_("Doing static initialisation for field: au::edu::anu::mm::MortonDist__MortonSubregion.X10$class_lock_id1");
    x10_int __var543__ = x10::util::concurrent::OrderedLock::createClassLock();
    FMGL(X10__class_lock_id1) = __var543__;
    FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
}
void au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__init)() {
    if (x10aux::here == 0) {
        x10aux::status __var544__ = (x10aux::status)x10aux::atomic_ops::compareAndSet_32((volatile x10_int*)&FMGL(X10__class_lock_id1__status), (x10_int)x10aux::UNINITIALIZED, (x10_int)x10aux::INITIALIZING);
        if (__var544__ != x10aux::UNINITIALIZED) goto WAIT;
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
                                                                       _SI_("WAITING for field: au::edu::anu::mm::MortonDist__MortonSubregion.X10$class_lock_id1 to be initialized");
                                                                       while (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) x10aux::StaticInitBroadcastDispatcher::await();
                                                                       _SI_("CONTINUING because field: au::edu::anu::mm::MortonDist__MortonSubregion.X10$class_lock_id1 has been initialized");
                                                                       x10aux::StaticInitBroadcastDispatcher::unlock();
    }
}
static void* __init__545 X10_PRAGMA_UNUSED = x10aux::InitDispatcher::addInitializer(au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__init));

volatile x10aux::status au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__status);
// extract value from a buffer
x10aux::ref<x10::lang::Reference> au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf) {
    FMGL(X10__class_lock_id1) = buf.read<x10_int>();
    au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__status) = x10aux::INITIALIZED;
    // Notify all waiting threads
    x10aux::StaticInitBroadcastDispatcher::lock();
    x10aux::StaticInitBroadcastDispatcher::notify();
    return X10_NULL;
}
const x10aux::serialization_id_t au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__id) =
  x10aux::StaticInitBroadcastDispatcher::addRoutine(au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__deserialize));


//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::util::concurrent::OrderedLock>
  au::edu::anu::mm::MortonDist__MortonSubregion::getStaticOrderedLock(
  ) {
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return (__extension__ ({
        
        //#line 170 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/util/concurrent/OrderedLock.x10": x10.ast.X10LocalDecl_c
        x10_int lockId57689 = au::edu::anu::mm::MortonDist__MortonSubregion::
                                FMGL(X10__class_lock_id1__get)();
        x10::util::Map<x10_int, x10aux::ref<x10::util::concurrent::OrderedLock> >::getOrThrow(x10aux::nullCheck(x10::util::concurrent::OrderedLock::
                                                                                                                  FMGL(lockMap__get)()), 
          lockId57689);
    }))
    ;
    
}

//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c

//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldDecl_c

//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MortonDist__MortonSubregion::_constructor(
  x10aux::ref<au::edu::anu::mm::MortonDist> out__,
  x10_int start,
  x10_int end) {
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
      FMGL(out__) = out__;
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> this57694 =
          ((x10aux::ref<x10::array::Region>)this);
        {
            
            //#line 469 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
            (this57694)->::x10::lang::Object::_constructor();
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57694)->
              FMGL(rank) = ((x10_int)3);
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57694)->
              FMGL(rect) = false;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57694)->
              FMGL(zeroBased) = false;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57694)->
              FMGL(rail) = false;
        }
        
    }))
    ;
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.AssignPropertyCall_c
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> this5769658161 =
          ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this);
        {
            
            //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5769658161)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
      FMGL(start) = start;
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
      FMGL(end) = end;
    
}
x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> au::edu::anu::mm::MortonDist__MortonSubregion::_make(
  x10aux::ref<au::edu::anu::mm::MortonDist> out__,
  x10_int start,
  x10_int end) {
    x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion))) au::edu::anu::mm::MortonDist__MortonSubregion();
    this_->_constructor(out__, start, end);
    return this_;
}



//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorDecl_c
void au::edu::anu::mm::MortonDist__MortonSubregion::_constructor(
  x10aux::ref<au::edu::anu::mm::MortonDist> out__,
  x10_int start,
  x10_int end,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    
    //#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
      FMGL(out__) =
      out__;
    
    //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<x10::array::Region> this57703 =
          ((x10aux::ref<x10::array::Region>)this);
        {
            
            //#line 469 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
            (this57703)->::x10::lang::Object::_constructor();
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57703)->
              FMGL(rank) =
              ((x10_int)3);
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57703)->
              FMGL(rect) =
              false;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57703)->
              FMGL(zeroBased) =
              false;
            
            //#line 472 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this57703)->
              FMGL(rail) =
              false;
        }
        
    }))
    ;
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.AssignPropertyCall_c
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.StmtExpr_c
    (__extension__ ({
        
        //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> this5770558162 =
          ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this);
        {
            
            //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
            x10aux::nullCheck(this5770558162)->
              FMGL(X10__object_lock_id0) =
              ((x10_int)-1);
        }
        
    }))
    ;
    
    //#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
      FMGL(start) =
      start;
    
    //#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
      FMGL(end) =
      end;
    
    //#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
      FMGL(X10__object_lock_id0) =
      x10aux::nullCheck(paramLock)->getIndex();
    
}
x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> au::edu::anu::mm::MortonDist__MortonSubregion::_make(
  x10aux::ref<au::edu::anu::mm::MortonDist> out__,
  x10_int start,
  x10_int end,
  x10aux::ref<x10::util::concurrent::OrderedLock> paramLock)
{
    x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion))) au::edu::anu::mm::MortonDist__MortonSubregion();
    this_->_constructor(out__,
    start,
    end,
    paramLock);
    return this_;
}



//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist__MortonSubregion::size(
  ) {
    
    //#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10_int) ((((x10_int) ((((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                       FMGL(end)) - (((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                                       FMGL(start))))) + (((x10_int)1))));
    
}

//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist__MortonSubregion::totalLength(
  ) {
    
    //#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10aux::nullCheck(x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                                 FMGL(out__))->
                               FMGL(region))->size();
    
}

//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::MortonDist__MortonSubregion::isConvex(
  ) {
    
    //#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return true;
    
}

//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::MortonDist__MortonSubregion::isEmpty(
  ) {
    
    //#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
               FMGL(end)) < (((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                               FMGL(start)));
    
}

//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist__MortonSubregion::indexOf(
  x10aux::ref<x10::array::Point> pt) {
    
    //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(x10aux::nullCheck(pt)->
                                  FMGL(rank),
                                ((x10_int)3))))
    {
        
        //#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return ((x10_int)-1);
        
    }
    
    //#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                            FMGL(out__))->au::edu::anu::mm::MortonDist::getMortonIndex(
                          pt)) - (((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                    FMGL(start))));
    
}

//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_int au::edu::anu::mm::MortonDist__MortonSubregion::indexOf(
  x10_int i0,
  x10_int i1,
  x10_int i2) {
    
    //#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10_int) ((x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                            FMGL(out__))->au::edu::anu::mm::MortonDist::getMortonIndex(
                          i0,
                          i1,
                          i2)) - (((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                    FMGL(start))));
    
}

//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist__MortonSubregion::boundingBox(
  ) {
    
    //#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("boundingBox()"))));
}

//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist__MortonSubregion::computeBoundingBox(
  ) {
    
    //#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("computeBoundingBox()"))));
}

//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >
  au::edu::anu::mm::MortonDist__MortonSubregion::min(
  ) {
    
    //#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >(x10aux::ref<au_edu_anu_mm_MortonDist__MortonSubregion__closure__2>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, x10_int> >(sizeof(au_edu_anu_mm_MortonDist__MortonSubregion__closure__2)))au_edu_anu_mm_MortonDist__MortonSubregion__closure__2()));
    
}

//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >
  au::edu::anu::mm::MortonDist__MortonSubregion::max(
  ) {
    
    //#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> >(x10aux::ref<au_edu_anu_mm_MortonDist__MortonSubregion__closure__3>(new (x10aux::alloc<x10::lang::Fun_0_1<x10_int, x10_int> >(sizeof(au_edu_anu_mm_MortonDist__MortonSubregion__closure__3)))au_edu_anu_mm_MortonDist__MortonSubregion__closure__3(this)));
    
}

//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::MortonDist__MortonSubregion::contains(
  x10aux::ref<x10::array::Region> that) {
    
    //#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if (x10aux::instanceof<x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> >(that))
    {
        
        //#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> thatMS =
          x10aux::class_cast<x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> >(that);
        
        //#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
        if ((x10aux::struct_equals(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->totalLength(),
                                   x10aux::nullCheck(thatMS)->totalLength())) &&
            ((((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                FMGL(start)) <= (x10aux::nullCheck(thatMS)->
                                   FMGL(start))) &&
            ((((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                FMGL(end)) >= (x10aux::nullCheck(thatMS)->
                                 FMGL(end))))
        {
            
            //#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
            return true;
            
        }
        
    }
    
    //#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return false;
    
}

//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist__MortonSubregion::intersection(
  x10aux::ref<x10::array::Region> that) {
    
    //#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if (x10aux::instanceof<x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> >(that))
    {
        
        //#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> thatMS =
          x10aux::class_cast<x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> >(that);
        
        //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
        if ((!x10aux::struct_equals(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->totalLength(),
                                    x10aux::nullCheck(thatMS)->totalLength())))
        {
            
            //#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
            return (__extension__ ({
                
                //#line 60 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                x10_int rank57709 =
                  ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                    FMGL(rank);
                x10aux::class_cast_unchecked<x10aux::ref<x10::array::Region> >((__extension__ ({
                    
                    //#line 60 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10LocalDecl_c
                    x10aux::ref<x10::array::EmptyRegion> alloc2062257710 =
                      
                    x10aux::ref<x10::array::EmptyRegion>((new (memset(x10aux::alloc<x10::array::EmptyRegion>(), 0, sizeof(x10::array::EmptyRegion))) x10::array::EmptyRegion()))
                    ;
                    
                    //#line 60 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10": x10.ast.X10ConstructorCall_c
                    (alloc2062257710)->::x10::array::EmptyRegion::_constructor(
                      rank57709);
                    alloc2062257710;
                }))
                );
            }))
            ;
            
        }
        
        //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return x10aux::class_cast<x10aux::ref<x10::array::Region> >((__extension__ ({
            
            //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
            x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> alloc45279 =
              
            x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>((new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion))) au::edu::anu::mm::MortonDist__MortonSubregion()))
            ;
            
            //#line 79 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorCall_c
            (alloc45279)->::au::edu::anu::mm::MortonDist__MortonSubregion::_constructor(
              ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                FMGL(out__),
              (__extension__ ({
                  
                  //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                  x10_int a57964 =
                    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                      FMGL(start);
                  
                  //#line 333 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                  x10_int b57965 =
                    x10aux::nullCheck(thatMS)->
                      FMGL(start);
                  ((a57964) < (b57965))
                    ? (b57965)
                    : (a57964);
              }))
              ,
              (__extension__ ({
                  
                  //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                  x10_int a57966 =
                    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                      FMGL(end);
                  
                  //#line 334 "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10": x10.ast.X10LocalDecl_c
                  x10_int b57967 =
                    x10aux::nullCheck(thatMS)->
                      FMGL(end);
                  ((a57966) < (b57967))
                    ? (a57966)
                    : (b57967);
              }))
              ,
              x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
            alloc45279;
        }))
        );
        
    }
    else
    {
        
        //#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
        x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("intersection(Region(!MortonSubregion))"))));
    }
    
}

//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist__MortonSubregion::complement(
  ) {
    
    //#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("complement()"))));
}

//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist__MortonSubregion::product(
  x10aux::ref<x10::array::Region> that) {
    
    //#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("product(Region)"))));
}

//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist__MortonSubregion::projection(
  x10_int axis) {
    
    //#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("projection(axis : Int)"))));
}

//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist__MortonSubregion::translate(
  x10aux::ref<x10::array::Point> v) {
    
    //#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("translate(Point)"))));
}

//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::array::Region> au::edu::anu::mm::MortonDist__MortonSubregion::eliminate(
  x10_int axis) {
    
    //#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": polyglot.ast.Throw_c
    x10aux::throwException(x10aux::nullCheck(x10::lang::UnsupportedOperationException::_make(x10aux::string_utils::lit("eliminate(axis : Int)"))));
}

//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10_boolean au::edu::anu::mm::MortonDist__MortonSubregion::contains(
  x10aux::ref<x10::array::Point> p) {
    
    //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10If_c
    if ((!x10aux::struct_equals(x10aux::nullCheck(p)->
                                  FMGL(rank),
                                ((x10_int)3))))
    {
        
        //#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
        return false;
        
    }
    
    //#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
    x10_int index = x10aux::nullCheck(((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                        FMGL(out__))->au::edu::anu::mm::MortonDist::getMortonIndex(
                      p);
    
    //#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((index) >= (((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                          FMGL(start))) &&
    ((index) <= (((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                   FMGL(end)));
    
}

//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >
  au::edu::anu::mm::MortonDist__MortonSubregion::iterator(
  ) {
    
    //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return x10aux::class_cast<x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > > >((__extension__ ({
        
        //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10LocalDecl_c
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator> alloc45280 =
          
        x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>((new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator))) au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator()))
        ;
        
        //#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10ConstructorCall_c
        (alloc45280)->::au::edu::anu::mm::MortonDist__MortonSubregion__MortonSubregionIterator::_constructor(
          ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this),
          ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this),
          x10::util::concurrent::OrderedLock::createAndStoreObjectLock());
        alloc45280;
    }))
    );
    
}

//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<x10::lang::String> au::edu::anu::mm::MortonDist__MortonSubregion::toString(
  ) {
    
    //#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((((((((x10aux::string_utils::lit("Z[")) + (((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                                         FMGL(start)))) + (x10aux::string_utils::lit("..")))) + (((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
                                                                                                                   FMGL(end)))) + (x10aux::string_utils::lit("]")));
    
}

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>
  au::edu::anu::mm::MortonDist__MortonSubregion::au__edu__anu__mm__MortonDist__MortonSubregion____au__edu__anu__mm__MortonDist__MortonSubregion__this(
  ) {
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this);
    
}

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
x10aux::ref<au::edu::anu::mm::MortonDist>
  au::edu::anu::mm::MortonDist__MortonSubregion::au__edu__anu__mm__MortonDist__MortonSubregion____au__edu__anu__mm__MortonDist__this(
  ) {
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10Return_c
    return ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
             FMGL(out__);
    
}

//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10MethodDecl_c
void au::edu::anu::mm::MortonDist__MortonSubregion::__fieldInitializers44485(
  ) {
    
    //#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MortonDist.x10": x10.ast.X10FieldAssign_c
    ((x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>)this)->
      FMGL(X10__object_lock_id0) = ((x10_int)-1);
}
const x10aux::serialization_id_t au::edu::anu::mm::MortonDist__MortonSubregion::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au::edu::anu::mm::MortonDist__MortonSubregion::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void au::edu::anu::mm::MortonDist__MortonSubregion::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::array::Region::_serialize_body(buf);
    buf.write(this->FMGL(start));
    buf.write(this->FMGL(end));
    buf.write(this->FMGL(out__));
    
}

x10aux::ref<x10::lang::Reference> au::edu::anu::mm::MortonDist__MortonSubregion::_deserializer(x10aux::deserialization_buffer& buf) {
    x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> this_ = new (memset(x10aux::alloc<au::edu::anu::mm::MortonDist__MortonSubregion>(), 0, sizeof(au::edu::anu::mm::MortonDist__MortonSubregion))) au::edu::anu::mm::MortonDist__MortonSubregion();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

void au::edu::anu::mm::MortonDist__MortonSubregion::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::array::Region::_deserialize_body(buf);
    FMGL(start) = buf.read<x10_int>();
    FMGL(end) = buf.read<x10_int>();
    FMGL(out__) = buf.read<x10aux::ref<au::edu::anu::mm::MortonDist> >();
}

x10aux::RuntimeType au::edu::anu::mm::MortonDist__MortonSubregion::rtt;
void au::edu::anu::mm::MortonDist__MortonSubregion::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[1] = { x10aux::getRTT<x10::array::Region>()};
    rtt.initStageTwo("au.edu.anu.mm.MortonDist.MortonSubregion",x10aux::RuntimeType::class_kind, 1, parents, 0, NULL, NULL);
}
x10::lang::Fun_0_1<x10_int, x10_int>::itable<au_edu_anu_mm_MortonDist__MortonSubregion__closure__2>au_edu_anu_mm_MortonDist__MortonSubregion__closure__2::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_MortonDist__MortonSubregion__closure__2::__apply, &au_edu_anu_mm_MortonDist__MortonSubregion__closure__2::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_MortonDist__MortonSubregion__closure__2::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >, &au_edu_anu_mm_MortonDist__MortonSubregion__closure__2::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_MortonDist__MortonSubregion__closure__2::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_MortonDist__MortonSubregion__closure__2::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

x10::lang::Fun_0_1<x10_int, x10_int>::itable<au_edu_anu_mm_MortonDist__MortonSubregion__closure__3>au_edu_anu_mm_MortonDist__MortonSubregion__closure__3::_itable(&x10::lang::Reference::equals, &x10::lang::Closure::hashCode, &au_edu_anu_mm_MortonDist__MortonSubregion__closure__3::__apply, &au_edu_anu_mm_MortonDist__MortonSubregion__closure__3::toString, &x10::lang::Closure::typeName);
x10aux::itable_entry au_edu_anu_mm_MortonDist__MortonSubregion__closure__3::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, x10_int> >, &au_edu_anu_mm_MortonDist__MortonSubregion__closure__3::_itable),x10aux::itable_entry(NULL, NULL)};

const x10aux::serialization_id_t au_edu_anu_mm_MortonDist__MortonSubregion__closure__3::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(au_edu_anu_mm_MortonDist__MortonSubregion__closure__3::_deserialize<x10::lang::Reference>,x10aux::CLOSURE_KIND_NOT_ASYNC);

/* END of MortonDist$MortonSubregion */
/*************************************************/
