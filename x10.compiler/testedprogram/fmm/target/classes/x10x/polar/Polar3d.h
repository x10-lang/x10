#ifndef __X10X_POLAR_POLAR3D_H
#define __X10X_POLAR_POLAR3D_H

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
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10x { namespace vector { 
class Point3d;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10x { namespace vector { 
class Tuple3d;
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
namespace x10x { namespace polar { 

class Polar3d   {
    public:
    RTT_H_DECLS_STRUCT
    
    x10x::polar::Polar3d* operator->() { return this; }
    
    static x10aux::itable_entry _itables[2];
    
    x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Any::itable<x10x::polar::Polar3d > _itable_0;
    
    static x10aux::itable_entry _iboxitables[2];
    
    x10aux::itable_entry* _getIBoxITables() { return _iboxitables; }
    
    static x10x::polar::Polar3d _alloc(){x10x::polar::Polar3d t; return t; }
    
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
    x10_double FMGL(r);
    
    x10_double FMGL(theta);
    
    x10_double FMGL(phi);
    
    void _constructor(x10_double r, x10_double theta, x10_double phi) {
        
        //#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.AssignPropertyCall_c
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10LocalDecl_c
            x10x::polar::Polar3d this5711157117 = (*this);
            {
                
                //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
                this5711157117->FMGL(X10__object_lock_id0) = ((x10_int)-1);
            }
            
        }))
        ;
        
        //#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
        (*this)->FMGL(r) = r;
        
        //#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
        (*this)->FMGL(theta) = theta;
        
        //#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
        (*this)->FMGL(phi) = phi;
        
    }
    static x10x::polar::Polar3d _make(x10_double r, x10_double theta, x10_double phi)
    {
        x10x::polar::Polar3d this_; 
        this_->_constructor(r,
        theta,
        phi);
        return this_;
    }
    
    void _constructor(
      x10_double r,
      x10_double theta,
      x10_double phi,
      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10x::polar::Polar3d _make(
             x10_double r,
             x10_double theta,
             x10_double phi,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    x10x::vector::Point3d
      toPoint3d(
      );
    static x10x::polar::Polar3d
      getPolar3d(
      x10aux::ref<x10x::vector::Tuple3d> point);
    x10x::polar::Polar3d
      rotate(
      x10_double alpha,
      x10_double beta);
    x10aux::ref<x10::lang::String>
      toString(
      );
    x10aux::ref<x10::lang::String>
      typeName(
      );
    x10_int
      hashCode(
      );
    x10_boolean
      equals(
      x10aux::ref<x10::lang::Any> other);
    x10_boolean
      equals(
      x10x::polar::Polar3d other) {
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
        return (x10aux::struct_equals((*this)->
                                        FMGL(r),
                                      other->
                                        FMGL(r))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(theta),
                               other->
                                 FMGL(theta))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(phi),
                               other->
                                 FMGL(phi)));
        
    }
    x10_boolean
      _struct_equals(
      x10aux::ref<x10::lang::Any> other);
    x10_boolean
      _struct_equals(
      x10x::polar::Polar3d other) {
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
        return (x10aux::struct_equals((*this)->
                                        FMGL(r),
                                      other->
                                        FMGL(r))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(theta),
                               other->
                                 FMGL(theta))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(phi),
                               other->
                                 FMGL(phi)));
        
    }
    x10x::polar::Polar3d
      x10x__polar__Polar3d____x10x__polar__Polar3d__this(
      ) {
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10Return_c
        return (*this);
        
    }
    void
      __fieldInitializers42844(
      ) {
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/x10x/polar/Polar3d.x10": x10.ast.X10FieldAssign_c
        (*this)->
          FMGL(X10__object_lock_id0) =
          ((x10_int)-1);
    }
    
    static void _serialize(x10x::polar::Polar3d this_, x10aux::serialization_buffer& buf);
    
    static x10x::polar::Polar3d _deserialize(x10aux::deserialization_buffer& buf) {
        x10x::polar::Polar3d this_;
        this_->_deserialize_body(buf);
        return this_;
    }
    
    void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } 
#endif // X10X_POLAR_POLAR3D_H

namespace x10x { namespace polar { 
class Polar3d;
} } 

#ifndef X10X_POLAR_POLAR3D_H_NODEPS
#define X10X_POLAR_POLAR3D_H_NODEPS
#ifndef X10X_POLAR_POLAR3D_H_GENERICS
#define X10X_POLAR_POLAR3D_H_GENERICS
inline x10_int x10x::polar::Polar3d::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return x10x::polar::Polar3d::FMGL(X10__class_lock_id1);
}

#endif // X10X_POLAR_POLAR3D_H_GENERICS
#endif // __X10X_POLAR_POLAR3D_H_NODEPS
