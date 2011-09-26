#ifndef __X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H
#define __X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H

#include <x10rt.h>


#define X10_LANG_OBJECT_H_NODEPS
#include <x10/lang/Object.h>
#undef X10_LANG_OBJECT_H_NODEPS
#define X10_LANG_ITERATOR_H_NODEPS
#include <x10/lang/Iterator.h>
#undef X10_LANG_ITERATOR_H_NODEPS
namespace x10 { namespace array { 
class Point;
} } 
#define X10_LANG_INT_H_NODEPS
#include <x10/lang/Int.h>
#undef X10_LANG_INT_H_NODEPS
#define X10_LANG_BOOLEAN_H_NODEPS
#include <x10/lang/Boolean.h>
#undef X10_LANG_BOOLEAN_H_NODEPS
namespace x10 { namespace array { 
class TriangularRegion;
} } 
namespace x10 { namespace lang { 
class Int;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace array { 

class TriangularRegion__TriangularRegionIterator : public x10::lang::Object
  {
    public:
    RTT_H_DECLS_CLASS
    
    static x10aux::itable_entry _itables[3];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Iterator<x10aux::ref<x10::array::Point> >::itable<x10::array::TriangularRegion__TriangularRegionIterator > _itable_0;
    
    static x10::lang::Any::itable<x10::array::TriangularRegion__TriangularRegionIterator > _itable_1;
    
    x10aux::ref<x10::array::TriangularRegion> FMGL(out__);
    
    x10_int FMGL(dim);
    
    x10_boolean FMGL(lower);
    
    x10_int FMGL(i);
    
    x10_int FMGL(j);
    
    void _constructor(x10aux::ref<x10::array::TriangularRegion> out__, x10aux::ref<x10::array::TriangularRegion> r);
    
    static x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator> _make(
             x10aux::ref<x10::array::TriangularRegion> out__,
             x10aux::ref<x10::array::TriangularRegion> r);
    
    virtual x10_boolean hasNext();
    virtual x10aux::ref<x10::array::Point> next();
    virtual x10aux::ref<x10::array::TriangularRegion__TriangularRegionIterator>
      x10__array__TriangularRegion__TriangularRegionIterator____x10__array__TriangularRegion__TriangularRegionIterator__this(
      );
    virtual x10aux::ref<x10::array::TriangularRegion> x10__array__TriangularRegion__TriangularRegionIterator____x10__array__TriangularRegion__this(
      );
    void __fieldInitializers58171();
    
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
#endif // X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H

namespace x10 { namespace array { 
class TriangularRegion__TriangularRegionIterator;
} } 

#ifndef X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H_NODEPS
#define X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H_NODEPS
#ifndef X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H_GENERICS
#define X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H_GENERICS
#endif // X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H_GENERICS
#endif // __X10_ARRAY_TRIANGULARREGION__TRIANGULARREGIONITERATOR_H_NODEPS
