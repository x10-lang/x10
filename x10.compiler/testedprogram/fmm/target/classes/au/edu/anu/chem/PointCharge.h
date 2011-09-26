#ifndef __AU_EDU_ANU_CHEM_POINTCHARGE_H
#define __AU_EDU_ANU_CHEM_POINTCHARGE_H

#include <x10rt.h>


#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10X_VECTOR_POINT3D_H_NODEPS
#include <x10x/vector/Point3d.h>
#undef X10X_VECTOR_POINT3D_H_NODEPS
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
class Point3d;
} } 
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10 { namespace compiler { 
class Native;
} } 
namespace x10 { namespace compiler { 
class NonEscaping;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace au { namespace edu { namespace anu { namespace chem { 

class PointCharge   {
    public:
    RTT_H_DECLS_STRUCT
    
    au::edu::anu::chem::PointCharge* operator->() { return this; }
    
    static x10aux::itable_entry _itables[2];
    
    x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Any::itable<au::edu::anu::chem::PointCharge > _itable_0;
    
    static x10aux::itable_entry _iboxitables[2];
    
    x10aux::itable_entry* _getIBoxITables() { return _iboxitables; }
    
    static au::edu::anu::chem::PointCharge _alloc(){au::edu::anu::chem::PointCharge t; return t; }
    
    x10_int FMGL(X10__object_lock_id0);
    
    x10aux::ref<x10::util::concurrent::OrderedLock> getOrderedLock();
    static x10_int FMGL(X10__class_lock_id1);
    
    static void FMGL(X10__class_lock_id1__do_init)();
    static void FMGL(X10__class_lock_id1__init)();
    static volatile x10aux::status FMGL(X10__class_lock_id1__status);
    static x10_int FMGL(X10__class_lock_id1__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(X10__class_lock_id1__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(X10__class_lock_id1__id);
    
    static x10aux::ref<x10::util::concurrent::OrderedLock> getStaticOrderedLock(
      );
    x10x::vector::Point3d FMGL(centre);
    
    x10_double FMGL(charge);
    
    void _constructor(x10x::vector::Point3d centre, x10_double charge);
    
    static au::edu::anu::chem::PointCharge _make(x10x::vector::Point3d centre,
                                                 x10_double charge);
    
    void _constructor(x10x::vector::Point3d centre, x10_double charge, x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static au::edu::anu::chem::PointCharge _make(x10x::vector::Point3d centre,
                                                 x10_double charge,
                                                 x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    x10aux::ref<x10::lang::String> toString();
    x10aux::ref<x10::lang::String> typeName();
    x10_int hashCode();
    x10_boolean equals(x10aux::ref<x10::lang::Any> other);
    x10_boolean equals(au::edu::anu::chem::PointCharge other) {
        
        //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
        return (x10aux::struct_equals((*this)->FMGL(centre), other->FMGL(centre))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(charge),
                               other->
                                 FMGL(charge)));
        
    }
    x10_boolean _struct_equals(x10aux::ref<x10::lang::Any> other);
    x10_boolean _struct_equals(au::edu::anu::chem::PointCharge other) {
        
        //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
        return (x10aux::struct_equals((*this)->FMGL(centre), other->
                                                               FMGL(centre))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(charge),
                               other->
                                 FMGL(charge)));
        
    }
    au::edu::anu::chem::PointCharge au__edu__anu__chem__PointCharge____au__edu__anu__chem__PointCharge__this(
      ) {
        
        //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10Return_c
        return (*this);
        
    }
    void __fieldInitializers30547() {
        
        //#line 22 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/PointCharge.x10": x10.ast.X10FieldAssign_c
        (*this)->FMGL(X10__object_lock_id0) = ((x10_int)-1);
    }
    
    static void _serialize(au::edu::anu::chem::PointCharge this_, x10aux::serialization_buffer& buf);
    
    static au::edu::anu::chem::PointCharge _deserialize(x10aux::deserialization_buffer& buf) {
        au::edu::anu::chem::PointCharge this_;
        this_->_deserialize_body(buf);
        return this_;
    }
    
    void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } } } 
#endif // AU_EDU_ANU_CHEM_POINTCHARGE_H

namespace au { namespace edu { namespace anu { namespace chem { 
class PointCharge;
} } } } 

#ifndef AU_EDU_ANU_CHEM_POINTCHARGE_H_NODEPS
#define AU_EDU_ANU_CHEM_POINTCHARGE_H_NODEPS
#ifndef AU_EDU_ANU_CHEM_POINTCHARGE_H_GENERICS
#define AU_EDU_ANU_CHEM_POINTCHARGE_H_GENERICS
inline x10_int au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::chem::PointCharge::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_CHEM_POINTCHARGE_H_GENERICS
#endif // __AU_EDU_ANU_CHEM_POINTCHARGE_H_NODEPS
