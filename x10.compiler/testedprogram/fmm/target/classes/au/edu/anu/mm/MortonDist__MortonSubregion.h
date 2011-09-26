#ifndef __AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H
#define __AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H

#include <x10rt.h>


#define X10_ARRAY_REGION_H_NODEPS
#include <x10/array/Region.h>
#undef X10_ARRAY_REGION_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10_LANG_INT_H_NODEPS
#include <x10/lang/Int.h>
#undef X10_LANG_INT_H_NODEPS
namespace au { namespace edu { namespace anu { namespace mm { 
class MortonDist;
} } } } 
namespace x10 { namespace lang { 
class Int;
} } 
namespace x10 { namespace util { namespace concurrent { 
class OrderedLock;
} } } 
namespace x10 { namespace util { 
template<class TPMGL(K), class TPMGL(V)> class Map;
} } 
namespace x10 { namespace array { 
class Dist;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace x10 { namespace lang { 
class UnsupportedOperationException;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(Z1), class TPMGL(U)> class Fun_0_1;
} } 
namespace x10 { namespace array { 
class EmptyRegion;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class MortonDist__MortonSubregion__MortonSubregionIterator;
} } } } 
namespace x10 { namespace lang { 
class String;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 

class MortonDist__MortonSubregion : public x10::array::Region   {
    public:
    RTT_H_DECLS_CLASS
    
    static x10aux::itable_entry _itables[3];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Iterable<x10aux::ref<x10::array::Point> >::itable<au::edu::anu::mm::MortonDist__MortonSubregion > _itable_0;
    
    static x10::lang::Any::itable<au::edu::anu::mm::MortonDist__MortonSubregion > _itable_1;
    
    using x10::array::Region::indexOf;
    using x10::array::Region::min;
    using x10::array::Region::max;
    using x10::array::Region::contains;
    
    x10aux::ref<au::edu::anu::mm::MortonDist> FMGL(out__);
    
    x10_int FMGL(X10__object_lock_id0);
    
    virtual x10aux::ref<x10::util::concurrent::OrderedLock> getOrderedLock(
      );
    static x10_int FMGL(X10__class_lock_id1);
    
    static void FMGL(X10__class_lock_id1__do_init)();
    static void FMGL(X10__class_lock_id1__init)();
    static volatile x10aux::status FMGL(X10__class_lock_id1__status);
    static x10_int FMGL(X10__class_lock_id1__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(X10__class_lock_id1__id);
    
    static x10aux::ref<x10::util::concurrent::OrderedLock> getStaticOrderedLock(
      );
    x10_int FMGL(start);
    
    x10_int FMGL(end);
    
    void _constructor(x10aux::ref<au::edu::anu::mm::MortonDist> out__, x10_int start,
                      x10_int end);
    
    static x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> _make(
             x10aux::ref<au::edu::anu::mm::MortonDist> out__,
             x10_int start,
             x10_int end);
    
    void _constructor(x10aux::ref<au::edu::anu::mm::MortonDist> out__,
                      x10_int start,
                      x10_int end,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion> _make(
             x10aux::ref<au::edu::anu::mm::MortonDist> out__,
             x10_int start,
             x10_int end,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10_int size();
    virtual x10_int totalLength();
    virtual x10_boolean isConvex();
    virtual x10_boolean isEmpty();
    virtual x10_int indexOf(x10aux::ref<x10::array::Point> pt);
    virtual x10_int indexOf(x10_int i0, x10_int i1, x10_int i2);
    virtual x10aux::ref<x10::array::Region> boundingBox();
    virtual x10aux::ref<x10::array::Region> computeBoundingBox();
    virtual x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> > min(
      );
    virtual x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> > max(
      );
    virtual x10_boolean contains(x10aux::ref<x10::array::Region> that);
    virtual x10aux::ref<x10::array::Region> intersection(x10aux::ref<x10::array::Region> that);
    virtual x10aux::ref<x10::array::Region> complement();
    virtual x10aux::ref<x10::array::Region> product(x10aux::ref<x10::array::Region> that);
    virtual x10aux::ref<x10::array::Region> projection(x10_int axis);
    virtual x10aux::ref<x10::array::Region> translate(x10aux::ref<x10::array::Point> v);
    virtual x10aux::ref<x10::array::Region> eliminate(x10_int axis);
    virtual x10_boolean contains(x10aux::ref<x10::array::Point> p);
    virtual x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >
      iterator(
      );
    virtual x10aux::ref<x10::lang::String> toString();
    virtual x10aux::ref<au::edu::anu::mm::MortonDist__MortonSubregion>
      au__edu__anu__mm__MortonDist__MortonSubregion____au__edu__anu__mm__MortonDist__MortonSubregion__this(
      );
    virtual x10aux::ref<au::edu::anu::mm::MortonDist> au__edu__anu__mm__MortonDist__MortonSubregion____au__edu__anu__mm__MortonDist__this(
      );
    void __fieldInitializers44485();
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: virtual x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(x10aux::serialization_buffer& buf);
    
    public: static x10aux::ref<x10::lang::Reference> _deserializer(x10aux::deserialization_buffer& buf);
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } } } 
#endif // AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H

namespace au { namespace edu { namespace anu { namespace mm { 
class MortonDist__MortonSubregion;
} } } } 

#ifndef AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H_NODEPS
#define AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H_NODEPS
#ifndef AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H_GENERICS
#define AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H_GENERICS
inline x10_int au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::MortonDist__MortonSubregion::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H_GENERICS
#endif // __AU_EDU_ANU_MM_MORTONDIST__MORTONSUBREGION_H_NODEPS
