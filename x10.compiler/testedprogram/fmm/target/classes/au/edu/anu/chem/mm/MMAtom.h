#ifndef __AU_EDU_ANU_CHEM_MM_MMATOM_H
#define __AU_EDU_ANU_CHEM_MM_MMATOM_H

#include <x10rt.h>


#define AU_EDU_ANU_CHEM_ATOM_H_NODEPS
#include <au/edu/anu/chem/Atom.h>
#undef AU_EDU_ANU_CHEM_ATOM_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10X_VECTOR_VECTOR3D_H_NODEPS
#include <x10x/vector/Vector3d.h>
#undef X10X_VECTOR_VECTOR3D_H_NODEPS
#define X10_LANG_DOUBLE_H_NODEPS
#include <x10/lang/Double.h>
#undef X10_LANG_DOUBLE_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
namespace x10 { namespace util { namespace concurrent { 
class OrderedLock;
} } } 
namespace x10 { namespace util { 
template<class TPMGL(K), class TPMGL(V)> class Map;
} } 
namespace x10x { namespace vector { 
class Vector3d;
} } 
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10x { namespace vector { 
class Point3d;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class MMAtom__PackedRepresentation;
} } } } } 
namespace x10 { namespace lang { 
class Zero;
} } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 

class MMAtom : public au::edu::anu::chem::Atom   {
    public:
    RTT_H_DECLS_CLASS
    
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
    x10x::vector::Vector3d FMGL(force);
    
    x10x::vector::Vector3d FMGL(velocity);
    
    x10_double FMGL(charge);
    
    void _constructor(x10aux::ref<x10::lang::String> symbol, x10x::vector::Point3d centre,
                      x10_double charge);
    
    static x10aux::ref<au::edu::anu::chem::mm::MMAtom> _make(x10aux::ref<x10::lang::String> symbol,
                                                             x10x::vector::Point3d centre,
                                                             x10_double charge);
    
    void _constructor(x10aux::ref<x10::lang::String> symbol,
                      x10x::vector::Point3d centre,
                      x10_double charge,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::chem::mm::MMAtom> _make(
             x10aux::ref<x10::lang::String> symbol,
             x10x::vector::Point3d centre,
             x10_double charge,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void _constructor(x10x::vector::Point3d centre,
                      x10_double charge);
    
    static x10aux::ref<au::edu::anu::chem::mm::MMAtom> _make(
             x10x::vector::Point3d centre,
             x10_double charge);
    
    void _constructor(x10x::vector::Point3d centre,
                      x10_double charge,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::chem::mm::MMAtom> _make(
             x10x::vector::Point3d centre,
             x10_double charge,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void _constructor(x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom);
    
    static x10aux::ref<au::edu::anu::chem::mm::MMAtom> _make(
             x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom);
    
    void _constructor(x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::chem::mm::MMAtom> _make(
             x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void _constructor(x10x::vector::Point3d centre);
    
    static x10aux::ref<au::edu::anu::chem::mm::MMAtom> _make(
             x10x::vector::Point3d centre);
    
    void _constructor(x10x::vector::Point3d centre,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::chem::mm::MMAtom> _make(
             x10x::vector::Point3d centre,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10_double pairEnergy(x10aux::ref<au::edu::anu::chem::mm::MMAtom> atom2);
    virtual au::edu::anu::chem::mm::MMAtom__PackedRepresentation
      getPackedRepresentation(
      );
    virtual x10aux::ref<au::edu::anu::chem::mm::MMAtom>
      au__edu__anu__chem__mm__MMAtom____au__edu__anu__chem__mm__MMAtom__this(
      );
    void __fieldInitializers25366();
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: virtual x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(x10aux::serialization_buffer& buf);
    
    public: static x10aux::ref<x10::lang::Reference> _deserializer(x10aux::deserialization_buffer& buf);
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } } } } 
#endif // AU_EDU_ANU_CHEM_MM_MMATOM_H

namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class MMAtom;
} } } } } 

#ifndef AU_EDU_ANU_CHEM_MM_MMATOM_H_NODEPS
#define AU_EDU_ANU_CHEM_MM_MMATOM_H_NODEPS
#ifndef AU_EDU_ANU_CHEM_MM_MMATOM_H_GENERICS
#define AU_EDU_ANU_CHEM_MM_MMATOM_H_GENERICS
inline x10_int au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::chem::mm::MMAtom::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_CHEM_MM_MMATOM_H_GENERICS
#endif // __AU_EDU_ANU_CHEM_MM_MMATOM_H_NODEPS
