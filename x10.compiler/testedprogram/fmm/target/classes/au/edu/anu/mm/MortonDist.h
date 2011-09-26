#ifndef __AU_EDU_ANU_MM_MORTONDIST_H
#define __AU_EDU_ANU_MM_MORTONDIST_H

#include <x10rt.h>


#define X10_ARRAY_DIST_H_NODEPS
#include <x10/array/Dist.h>
#undef X10_ARRAY_DIST_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10_LANG_INT_H_NODEPS
#include <x10/lang/Int.h>
#undef X10_LANG_INT_H_NODEPS
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
class PlaceGroup;
} } 
namespace x10 { namespace array { 
class Region;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10 { namespace lang { 
class Place;
} } 
namespace x10 { namespace lang { 
class Runtime;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class MortonDist__MortonSubregion;
} } } } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Sequence;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(Z1), class TPMGL(U)> class Fun_0_1;
} } 
namespace x10 { namespace array { 
class RectRegion1D;
} } 
namespace x10 { namespace array { 
class RectLayout;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace x10 { namespace lang { 
class UnsupportedOperationException;
} } 
namespace x10 { namespace array { 
class ConstantDist;
} } 
namespace x10 { namespace lang { 
class Any;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterable;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 

class MortonDist : public x10::array::Dist   {
    public:
    RTT_H_DECLS_CLASS
    
    static x10aux::itable_entry _itables[4];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Fun_0_1<x10aux::ref<x10::array::Point>, x10::lang::Place>::itable<au::edu::anu::mm::MortonDist > _itable_0;
    
    static x10::lang::Any::itable<au::edu::anu::mm::MortonDist > _itable_1;
    
    static x10::lang::Iterable<x10aux::ref<x10::array::Point> >::itable<au::edu::anu::mm::MortonDist > _itable_2;
    
    using x10::array::Dist::__apply;
    using x10::array::Dist::offset;
    
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
    x10aux::ref<x10::array::PlaceGroup> FMGL(pg);
    
    x10_int FMGL(dimDigits);
    
    x10aux::ref<x10::array::Region> FMGL(regionForHere);
    
    static x10aux::ref<au::edu::anu::mm::MortonDist> makeMorton(x10aux::ref<x10::array::Region> r);
    static x10aux::ref<au::edu::anu::mm::MortonDist> makeMorton(x10aux::ref<x10::array::Region> r,
                                                                x10aux::ref<x10::array::PlaceGroup> pg);
    void _constructor(x10aux::ref<x10::array::Region> r, x10aux::ref<x10::array::PlaceGroup> pg);
    
    static x10aux::ref<au::edu::anu::mm::MortonDist> _make(x10aux::ref<x10::array::Region> r,
                                                           x10aux::ref<x10::array::PlaceGroup> pg);
    
    void _constructor(x10aux::ref<x10::array::Region> r, x10aux::ref<x10::array::PlaceGroup> pg,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::MortonDist> _make(
             x10aux::ref<x10::array::Region> r,
             x10aux::ref<x10::array::PlaceGroup> pg,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10aux::ref<x10::array::PlaceGroup> places(
      );
    virtual x10_int numPlaces();
    virtual x10aux::ref<x10::array::Region> get(x10::lang::Place p);
    x10aux::ref<x10::array::Region> mortonRegionForPlace(
      x10::lang::Place p);
    virtual x10aux::ref<x10::lang::Iterable<x10aux::ref<x10::array::Region> > >
      regions(
      );
    virtual x10aux::ref<x10::array::Dist> restriction(
      x10aux::ref<x10::array::Region> r);
    virtual x10aux::ref<x10::array::Dist> restriction(
      x10::lang::Place p);
    virtual x10_boolean equals(x10aux::ref<x10::lang::Any> thatObj);
    virtual x10_int getMortonIndex(x10aux::ref<x10::array::Point> p);
    virtual x10_int getMortonIndex(x10_int i0, x10_int i1,
                                   x10_int i2);
    virtual x10aux::ref<x10::array::Point> getPoint(
      x10_int index);
    virtual x10_int getPlaceStart(x10_int placeId);
    virtual x10_int getPlaceEnd(x10_int placeId);
    x10::lang::Place getPlaceForIndex(x10_int index);
    virtual x10::lang::Place __apply(x10aux::ref<x10::array::Point> pt);
    virtual x10_int offset(x10aux::ref<x10::array::Point> pt);
    virtual x10_int offset(x10_int i0, x10_int i1,
                           x10_int i2);
    virtual x10_int maxOffset();
    virtual x10aux::ref<x10::lang::String> toString(
      );
    virtual x10aux::ref<au::edu::anu::mm::MortonDist>
      au__edu__anu__mm__MortonDist____au__edu__anu__mm__MortonDist__this(
      );
    void __fieldInitializers44486();
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(x10aux::serialization_buffer& buf);
    
    public: static x10aux::ref<x10::lang::Reference> _deserializer(x10aux::deserialization_buffer& buf);
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } } } 
#endif // AU_EDU_ANU_MM_MORTONDIST_H

namespace au { namespace edu { namespace anu { namespace mm { 
class MortonDist;
} } } } 

#ifndef AU_EDU_ANU_MM_MORTONDIST_H_NODEPS
#define AU_EDU_ANU_MM_MORTONDIST_H_NODEPS
#ifndef AU_EDU_ANU_MM_MORTONDIST_H_GENERICS
#define AU_EDU_ANU_MM_MORTONDIST_H_GENERICS
inline x10_int au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::MortonDist::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_MORTONDIST_H_GENERICS
#endif // __AU_EDU_ANU_MM_MORTONDIST_H_NODEPS
