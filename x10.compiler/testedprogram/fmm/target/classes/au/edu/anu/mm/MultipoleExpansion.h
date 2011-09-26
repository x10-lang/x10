#ifndef __AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H
#define __AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H

#include <x10rt.h>


#define AU_EDU_ANU_MM_EXPANSION_H_NODEPS
#include <au/edu/anu/mm/Expansion.h>
#undef AU_EDU_ANU_MM_EXPANSION_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
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
class Double;
} } 
namespace x10x { namespace vector { 
class Tuple3d;
} } 
namespace x10x { namespace polar { 
class Polar3d;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class AssociatedLegendrePolynomial;
} } } } 
namespace x10 { namespace lang { 
class Complex;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace x10 { namespace array { 
class RectLayout;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace lang { 
class Throwable;
} } 
namespace x10 { namespace lang { 
class Runtime;
} } 
namespace x10 { namespace compiler { 
class Finalization;
} } 
namespace x10 { namespace compiler { 
class Abort;
} } 
namespace x10 { namespace compiler { 
class CompilerFlags;
} } 
namespace x10x { namespace vector { 
class Vector3d;
} } 
namespace x10 { namespace array { 
class Region;
} } 
namespace x10 { namespace lang { 
class IntRange;
} } 
namespace x10 { namespace array { 
class RectRegion1D;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class Factorial;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class WignerRotationMatrix;
} } } } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 

class MultipoleExpansion : public au::edu::anu::mm::Expansion   {
    public:
    RTT_H_DECLS_CLASS
    
    using au::edu::anu::mm::Expansion::rotate;
    
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
    void _constructor(x10_int p);
    
    static x10aux::ref<au::edu::anu::mm::MultipoleExpansion> _make(x10_int p);
    
    void _constructor(x10_int p, x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::MultipoleExpansion> _make(x10_int p,
                                                                   x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void _constructor(x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source);
    
    static x10aux::ref<au::edu::anu::mm::MultipoleExpansion> _make(x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source);
    
    void _constructor(x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::MultipoleExpansion> _make(x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source,
                                                                   x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::MultipoleExpansion> getOlm(
      x10_double q,
      x10aux::ref<x10x::vector::Tuple3d> v,
      x10_int p);
    static x10aux::ref<au::edu::anu::mm::MultipoleExpansion> getOlm(
      x10aux::ref<x10x::vector::Tuple3d> v,
      x10_int p);
    virtual void translateAndAddMultipole(x10aux::ref<au::edu::anu::mm::MultipoleExpansion> shift,
                                          x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source);
    virtual void translateAndAddMultipole(x10x::vector::Vector3d v,
                                          x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > complexK,
                                          x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source,
                                          x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > wigner);
    virtual void translateAndAddMultipole(
      x10x::vector::Vector3d v,
      x10aux::ref<au::edu::anu::mm::MultipoleExpansion> source);
    virtual x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
      rotate(
      x10_double theta,
      x10_double phi);
    virtual x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
      getMacroscopicParent(
      );
    virtual x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
      au__edu__anu__mm__MultipoleExpansion____au__edu__anu__mm__MultipoleExpansion__this(
      );
    void __fieldInitializers37365();
    
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
#endif // AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H

namespace au { namespace edu { namespace anu { namespace mm { 
class MultipoleExpansion;
} } } } 

#ifndef AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H_NODEPS
#define AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H_NODEPS
#ifndef AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H_GENERICS
#define AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H_GENERICS
inline x10_int au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::MultipoleExpansion::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H_GENERICS
#endif // __AU_EDU_ANU_MM_MULTIPOLEEXPANSION_H_NODEPS
