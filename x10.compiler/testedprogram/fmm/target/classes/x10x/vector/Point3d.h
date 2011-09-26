#ifndef __X10X_VECTOR_POINT3D_H
#define __X10X_VECTOR_POINT3D_H

#include <x10rt.h>


#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
#define X10X_VECTOR_TUPLE3D_H_NODEPS
#include <x10x/vector/Tuple3d.h>
#undef X10X_VECTOR_TUPLE3D_H_NODEPS
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
class Double;
} } 
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
class String;
} } 
namespace x10x { namespace vector { 
class Vector3d;
} } 
namespace x10 { namespace compiler { 
class Inline;
} } 
namespace x10 { namespace lang { 
class Math;
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
namespace x10x { namespace vector { 

class Point3d   {
    public:
    RTT_H_DECLS_STRUCT
    
    x10x::vector::Point3d* operator->() { return this; }
    
    x10_double FMGL(i);
    
    x10_double FMGL(j);
    
    x10_double FMGL(k);
    
    static x10aux::itable_entry _itables[3];
    
    x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Any::itable<x10x::vector::Point3d > _itable_0;
    
    static x10x::vector::Tuple3d::itable<x10x::vector::Point3d > _itable_1;
    
    static x10aux::itable_entry _iboxitables[3];
    
    x10aux::itable_entry* _getIBoxITables() { return _iboxitables; }
    
    static x10x::vector::Point3d _alloc(){x10x::vector::Point3d t; return t; }
    
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
    void _constructor(x10_double i, x10_double j, x10_double k) {
        
        //#line 11 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.AssignPropertyCall_c
        FMGL(i) = i;
        FMGL(j) = j;
        FMGL(k) = k;
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
            x10x::vector::Point3d this2484125359 = (*this);
            {
                
                //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10FieldAssign_c
                this2484125359->FMGL(X10__object_lock_id0) = ((x10_int)-1);
            }
            
        }))
        ;
        
    }
    static x10x::vector::Point3d _make(x10_double i, x10_double j, x10_double k)
    {
        x10x::vector::Point3d this_; 
        this_->_constructor(i,
        j,
        k);
        return this_;
    }
    
    void _constructor(
      x10_double i,
      x10_double j,
      x10_double k,
      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10x::vector::Point3d _make(
             x10_double i,
             x10_double j,
             x10_double k,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void _constructor(
      x10aux::ref<x10x::vector::Tuple3d> t);
    
    static x10x::vector::Point3d _make(
             x10aux::ref<x10x::vector::Tuple3d> t);
    
    void _constructor(
      x10aux::ref<x10x::vector::Tuple3d> t,
      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10x::vector::Point3d _make(
             x10aux::ref<x10x::vector::Tuple3d> t,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    x10_double
      i(
      ) {
        
        //#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return (*this)->
                 FMGL(i);
        
    }
    x10_double
      j(
      ) {
        
        //#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return (*this)->
                 FMGL(j);
        
    }
    x10_double
      k(
      ) {
        
        //#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return (*this)->
                 FMGL(k);
        
    }
    x10aux::ref<x10::lang::String>
      toString(
      );
    x10x::vector::Point3d
      add(
      x10x::vector::Vector3d b);
    x10x::vector::Point3d
      __plus(
      x10x::vector::Vector3d that);
    x10x::vector::Point3d
      __times(
      x10_double that);
    x10x::vector::Point3d
      scale(
      x10_double c);
    x10x::vector::Vector3d
      vector(
      x10x::vector::Point3d b);
    x10x::vector::Vector3d
      __minus(
      x10x::vector::Point3d that);
    x10_double
      distanceSquared(
      x10x::vector::Point3d b) {
        
        //#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10_double di =
          (((*this)->
              FMGL(i)) - (b->
                            FMGL(i)));
        
        //#line 59 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10_double dj =
          (((*this)->
              FMGL(j)) - (b->
                            FMGL(j)));
        
        //#line 60 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10LocalDecl_c
        x10_double dk =
          (((*this)->
              FMGL(k)) - (b->
                            FMGL(k)));
        
        //#line 61 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return ((((((di) * (di))) + (((dj) * (dj))))) + (((dk) * (dk))));
        
    }
    x10_double
      distance(
      x10x::vector::Point3d b);
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
      x10x::vector::Point3d other) {
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return (x10aux::struct_equals((*this)->
                                        FMGL(i),
                                      other->
                                        FMGL(i))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(j),
                               other->
                                 FMGL(j))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(k),
                               other->
                                 FMGL(k)));
        
    }
    x10_boolean
      _struct_equals(
      x10aux::ref<x10::lang::Any> other);
    x10_boolean
      _struct_equals(
      x10x::vector::Point3d other) {
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return (x10aux::struct_equals((*this)->
                                        FMGL(i),
                                      other->
                                        FMGL(i))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(j),
                               other->
                                 FMGL(j))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(k),
                               other->
                                 FMGL(k)));
        
    }
    x10x::vector::Point3d
      x10x__vector__Point3d____x10x__vector__Point3d__this(
      ) {
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10Return_c
        return (*this);
        
    }
    void
      __fieldInitializers23957(
      ) {
        
        //#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10": x10.ast.X10FieldAssign_c
        (*this)->
          FMGL(X10__object_lock_id0) =
          ((x10_int)-1);
    }
    
    static void _serialize(x10x::vector::Point3d this_, x10aux::serialization_buffer& buf);
    
    static x10x::vector::Point3d _deserialize(x10aux::deserialization_buffer& buf) {
        x10x::vector::Point3d this_;
        this_->_deserialize_body(buf);
        return this_;
    }
    
    void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } 
#endif // X10X_VECTOR_POINT3D_H

namespace x10x { namespace vector { 
class Point3d;
} } 

#ifndef X10X_VECTOR_POINT3D_H_NODEPS
#define X10X_VECTOR_POINT3D_H_NODEPS
#ifndef X10X_VECTOR_POINT3D_H_GENERICS
#define X10X_VECTOR_POINT3D_H_GENERICS
inline x10_int x10x::vector::Point3d::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return x10x::vector::Point3d::FMGL(X10__class_lock_id1);
}

#endif // X10X_VECTOR_POINT3D_H_GENERICS
#endif // __X10X_VECTOR_POINT3D_H_NODEPS
