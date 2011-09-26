#ifndef __X10_ARRAY_TRIANGULARREGION_H
#define __X10_ARRAY_TRIANGULARREGION_H

#include <x10rt.h>


#define X10_ARRAY_REGION_H_NODEPS
#include <x10/array/Region.h>
#undef X10_ARRAY_REGION_H_NODEPS
#define X10_LANG_INT_H_NODEPS
#include <x10/lang/Int.h>
#undef X10_LANG_INT_H_NODEPS
#define X10_LANG_BOOLEAN_H_NODEPS
#include <x10/lang/Boolean.h>
#undef X10_LANG_BOOLEAN_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(Z1), class TPMGL(U)> class Fun_0_1;
} } 
namespace x10 { namespace lang { 
class ArrayIndexOutOfBoundsException;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10 { namespace lang { 
class UnsupportedOperationException;
} } 
namespace x10 { namespace lang { 
class IntRange;
} } 
namespace x10 { namespace array { 
class RectRegion1D;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace x10 { namespace array { 
class TriangularRegion__TriangularRegionIterator;
} } 
namespace x10 { namespace array { 

class TriangularRegion : public x10::array::Region   {
    public:
    RTT_H_DECLS_CLASS
    
    static x10aux::itable_entry _itables[3];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Iterable<x10aux::ref<x10::array::Point> >::itable<x10::array::TriangularRegion > _itable_0;
    
    static x10::lang::Any::itable<x10::array::TriangularRegion > _itable_1;
    
    using x10::array::Region::indexOf;
    using x10::array::Region::min;
    using x10::array::Region::max;
    using x10::array::Region::contains;
    
    x10_int FMGL(dim);
    
    x10_int FMGL(rowMin);
    
    x10_int FMGL(colMin);
    
    x10_boolean FMGL(lower);
    
    void _constructor(x10_int rowMin, x10_int colMin, x10_int size, x10_boolean lower);
    
    static x10aux::ref<x10::array::TriangularRegion> _make(x10_int rowMin,
                                                           x10_int colMin,
                                                           x10_int size,
                                                           x10_boolean lower);
    
    virtual x10_boolean isConvex();
    virtual x10_boolean isEmpty();
    virtual x10_int indexOf(x10aux::ref<x10::array::Point> pt);
    virtual x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> > min();
    virtual x10aux::ref<x10::lang::Fun_0_1<x10_int, x10_int> > max();
    virtual x10_int size();
    virtual x10_boolean contains(x10aux::ref<x10::array::Point> p);
    virtual x10_boolean contains(x10aux::ref<x10::array::Region> r);
    virtual x10aux::ref<x10::array::Region> complement();
    virtual x10aux::ref<x10::array::Region> intersection(x10aux::ref<x10::array::Region> t);
    virtual x10aux::ref<x10::array::Region> product(x10aux::ref<x10::array::Region> r);
    virtual x10aux::ref<x10::array::Region> translate(x10aux::ref<x10::array::Point> v);
    virtual x10aux::ref<x10::array::Region> projection(x10_int axis);
    virtual x10aux::ref<x10::array::Region> eliminate(x10_int axis);
    virtual x10aux::ref<x10::array::Region> boundingBox();
    virtual x10aux::ref<x10::array::Region> computeBoundingBox();
    virtual x10aux::ref<x10::lang::Iterator<x10aux::ref<x10::array::Point> > >
      iterator(
      );
    virtual x10aux::ref<x10::lang::String> toString();
    virtual x10aux::ref<x10::array::TriangularRegion> x10__array__TriangularRegion____x10__array__TriangularRegion__this(
      );
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: virtual x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(x10aux::serialization_buffer& buf);
    
    public: static x10aux::ref<x10::lang::Reference> _deserializer(x10aux::deserialization_buffer& buf);
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } 
#endif // X10_ARRAY_TRIANGULARREGION_H

namespace x10 { namespace array { 
class TriangularRegion;
} } 

#ifndef X10_ARRAY_TRIANGULARREGION_H_NODEPS
#define X10_ARRAY_TRIANGULARREGION_H_NODEPS
#ifndef X10_ARRAY_TRIANGULARREGION_H_GENERICS
#define X10_ARRAY_TRIANGULARREGION_H_GENERICS
#endif // X10_ARRAY_TRIANGULARREGION_H_GENERICS
#endif // __X10_ARRAY_TRIANGULARREGION_H_NODEPS
