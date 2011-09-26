#ifndef __AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H
#define __AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H

#include <x10rt.h>


#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
#define X10_LANG_REDUCIBLE_H_NODEPS
#include <x10/lang/Reducible.h>
#undef X10_LANG_REDUCIBLE_H_NODEPS
#define X10_LANG_ANY_H_NODEPS
#include <x10/lang/Any.h>
#undef X10_LANG_ANY_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
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
namespace x10 { namespace compiler { 
class Native;
} } 
namespace x10 { namespace compiler { 
class NonEscaping;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 

class Fmm3d__SumReducer   {
    public:
    RTT_H_DECLS_STRUCT
    
    au::edu::anu::mm::Fmm3d__SumReducer* operator->() { return this; }
    
    static x10aux::itable_entry _itables[3];
    
    x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Any::itable<au::edu::anu::mm::Fmm3d__SumReducer > _itable_0;
    
    static x10::lang::Reducible<x10_double>::itable<au::edu::anu::mm::Fmm3d__SumReducer > _itable_1;
    
    static x10aux::itable_entry _iboxitables[3];
    
    x10aux::itable_entry* _getIBoxITables() { return _iboxitables; }
    
    static au::edu::anu::mm::Fmm3d__SumReducer _alloc(){au::edu::anu::mm::Fmm3d__SumReducer t; return t; }
    
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
    x10_double zero() {
        
        //#line 677 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10Return_c
        return 0.0;
        
    }
    x10_double __apply(x10_double a, x10_double b) {
        
        //#line 678 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10Return_c
        return ((a) + (b));
        
    }
    x10aux::ref<x10::lang::String> typeName();
    x10aux::ref<x10::lang::String> toString();
    x10_int hashCode() {
        
        //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10LocalDecl_c
        x10_int result = ((x10_int)1);
        
        //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10Return_c
        return result;
        
    }
    x10_boolean equals(x10aux::ref<x10::lang::Any> other);
    x10_boolean equals(au::edu::anu::mm::Fmm3d__SumReducer other) {
        
        //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10Return_c
        return true;
        
    }
    x10_boolean _struct_equals(x10aux::ref<x10::lang::Any> other);
    x10_boolean _struct_equals(au::edu::anu::mm::Fmm3d__SumReducer other) {
        
        //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10Return_c
        return true;
        
    }
    au::edu::anu::mm::Fmm3d__SumReducer au__edu__anu__mm__Fmm3d__SumReducer____au__edu__anu__mm__Fmm3d__SumReducer__this(
      ) {
        
        //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10Return_c
        return (*this);
        
    }
    void _constructor() {
        
        //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.AssignPropertyCall_c
        
        //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.StmtExpr_c
        (__extension__ ({
            
            //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10LocalDecl_c
            au::edu::anu::mm::Fmm3d__SumReducer this4215147790 = (*this);
            {
                
                //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10FieldAssign_c
                this4215147790->FMGL(X10__object_lock_id0) = ((x10_int)-1);
            }
            
        }))
        ;
        
    }
    static au::edu::anu::mm::Fmm3d__SumReducer _make() {
        au::edu::anu::mm::Fmm3d__SumReducer this_; 
        this_->_constructor();
        return this_;
    }
    
    void _constructor(x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static au::edu::anu::mm::Fmm3d__SumReducer _make(x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void __fieldInitializers34573() {
        
        //#line 676 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Fmm3d.x10": x10.ast.X10FieldAssign_c
        (*this)->FMGL(X10__object_lock_id0) = ((x10_int)-1);
    }
    
    static void _serialize(au::edu::anu::mm::Fmm3d__SumReducer this_, x10aux::serialization_buffer& buf);
    
    static au::edu::anu::mm::Fmm3d__SumReducer _deserialize(x10aux::deserialization_buffer& buf) {
        au::edu::anu::mm::Fmm3d__SumReducer this_;
        this_->_deserialize_body(buf);
        return this_;
    }
    
    void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } } } 
#endif // AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H

namespace au { namespace edu { namespace anu { namespace mm { 
class Fmm3d__SumReducer;
} } } } 

#ifndef AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H_NODEPS
#define AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H_NODEPS
#ifndef AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H_GENERICS
#define AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H_GENERICS
inline x10_int au::edu::anu::mm::Fmm3d__SumReducer::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::Fmm3d__SumReducer::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H_GENERICS
#endif // __AU_EDU_ANU_MM_FMM3D__SUMREDUCER_H_NODEPS
