#ifndef __AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H
#define __AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H

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
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10_LANG_INT_H_NODEPS
#include <x10/lang/Int.h>
#undef X10_LANG_INT_H_NODEPS
namespace au { namespace edu { namespace anu { namespace mm { 
class ExpansionRegion;
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
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 

class ExpansionRegion__ExpansionRegionIterator : public x10::lang::Object
  {
    public:
    RTT_H_DECLS_CLASS
    
    static x10aux::itable_entry _itables[3];
    
    virtual x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Iterator<x10aux::ref<x10::array::Point> >::itable<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator > _itable_0;
    
    static x10::lang::Any::itable<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator > _itable_1;
    
    x10aux::ref<au::edu::anu::mm::ExpansionRegion> FMGL(out__);
    
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
    x10_int FMGL(p);
    
    x10_int FMGL(l);
    
    x10_int FMGL(m);
    
    void _constructor(x10aux::ref<au::edu::anu::mm::ExpansionRegion> out__,
                      x10aux::ref<au::edu::anu::mm::ExpansionRegion> r);
    
    static x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> _make(
             x10aux::ref<au::edu::anu::mm::ExpansionRegion> out__,
             x10aux::ref<au::edu::anu::mm::ExpansionRegion> r);
    
    void _constructor(x10aux::ref<au::edu::anu::mm::ExpansionRegion> out__,
                      x10aux::ref<au::edu::anu::mm::ExpansionRegion> r,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator> _make(
             x10aux::ref<au::edu::anu::mm::ExpansionRegion> out__,
             x10aux::ref<au::edu::anu::mm::ExpansionRegion> r,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10_boolean hasNext();
    virtual x10aux::ref<x10::array::Point> next();
    virtual x10aux::ref<au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator>
      au__edu__anu__mm__ExpansionRegion__ExpansionRegionIterator____au__edu__anu__mm__ExpansionRegion__ExpansionRegionIterator__this(
      );
    virtual x10aux::ref<au::edu::anu::mm::ExpansionRegion>
      au__edu__anu__mm__ExpansionRegion__ExpansionRegionIterator____au__edu__anu__mm__ExpansionRegion__this(
      );
    void __fieldInitializers54335();
    
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
#endif // AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H

namespace au { namespace edu { namespace anu { namespace mm { 
class ExpansionRegion__ExpansionRegionIterator;
} } } } 

#ifndef AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H_NODEPS
#define AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H_NODEPS
#ifndef AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H_GENERICS
#define AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H_GENERICS
inline x10_int au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::ExpansionRegion__ExpansionRegionIterator::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H_GENERICS
#endif // __AU_EDU_ANU_MM_EXPANSIONREGION__EXPANSIONREGIONITERATOR_H_NODEPS
