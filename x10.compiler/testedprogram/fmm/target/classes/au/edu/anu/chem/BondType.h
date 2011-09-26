#ifndef __AU_EDU_ANU_CHEM_BONDTYPE_H
#define __AU_EDU_ANU_CHEM_BONDTYPE_H

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
class String;
} } 
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
class Boolean;
} } 
namespace x10 { namespace compiler { 
class Native;
} } 
namespace x10 { namespace compiler { 
class NonEscaping;
} } 
namespace au { namespace edu { namespace anu { namespace chem { 

class BondType   {
    public:
    RTT_H_DECLS_STRUCT
    
    au::edu::anu::chem::BondType* operator->() { return this; }
    
    x10aux::ref<x10::lang::String> FMGL(description);
    
    x10_double FMGL(bondOrder);
    
    static x10aux::itable_entry _itables[2];
    
    x10aux::itable_entry* _getITables() { return _itables; }
    
    static x10::lang::Any::itable<au::edu::anu::chem::BondType > _itable_0;
    
    static x10aux::itable_entry _iboxitables[2];
    
    x10aux::itable_entry* _getIBoxITables() { return _iboxitables; }
    
    static au::edu::anu::chem::BondType _alloc(){au::edu::anu::chem::BondType t; return t; }
    
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
    void _constructor(x10aux::ref<x10::lang::String> description, x10_double bondOrder);
    
    static au::edu::anu::chem::BondType _make(x10aux::ref<x10::lang::String> description,
                                              x10_double bondOrder);
    
    void _constructor(x10aux::ref<x10::lang::String> description,
                      x10_double bondOrder,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static au::edu::anu::chem::BondType _make(x10aux::ref<x10::lang::String> description,
                                              x10_double bondOrder,
                                              x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static au::edu::anu::chem::BondType FMGL(WEAK_BOND);
    
    static void FMGL(WEAK_BOND__do_init)();
    static void FMGL(WEAK_BOND__init)();
    static volatile x10aux::status FMGL(WEAK_BOND__status);
    static au::edu::anu::chem::BondType FMGL(WEAK_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(WEAK_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(WEAK_BOND__id);
    
    static au::edu::anu::chem::BondType FMGL(NO_BOND);
    
    static void FMGL(NO_BOND__do_init)();
    static void FMGL(NO_BOND__init)();
    static volatile x10aux::status FMGL(NO_BOND__status);
    static au::edu::anu::chem::BondType FMGL(NO_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(NO_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(NO_BOND__id);
    
    static au::edu::anu::chem::BondType FMGL(SINGLE_BOND);
    
    static void FMGL(SINGLE_BOND__do_init)();
    static void FMGL(SINGLE_BOND__init)();
    static volatile x10aux::status FMGL(SINGLE_BOND__status);
    static au::edu::anu::chem::BondType FMGL(SINGLE_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(SINGLE_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(SINGLE_BOND__id);
    
    static au::edu::anu::chem::BondType FMGL(DOUBLE_BOND);
    
    static void FMGL(DOUBLE_BOND__do_init)();
    static void FMGL(DOUBLE_BOND__init)();
    static volatile x10aux::status FMGL(DOUBLE_BOND__status);
    static au::edu::anu::chem::BondType FMGL(DOUBLE_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(DOUBLE_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(DOUBLE_BOND__id);
    
    static au::edu::anu::chem::BondType FMGL(TRIPLE_BOND);
    
    static void FMGL(TRIPLE_BOND__do_init)();
    static void FMGL(TRIPLE_BOND__init)();
    static volatile x10aux::status FMGL(TRIPLE_BOND__status);
    static au::edu::anu::chem::BondType FMGL(TRIPLE_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(TRIPLE_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(TRIPLE_BOND__id);
    
    static au::edu::anu::chem::BondType FMGL(QUADRUPLE_BOND);
    
    static void FMGL(QUADRUPLE_BOND__do_init)();
    static void FMGL(QUADRUPLE_BOND__init)();
    static volatile x10aux::status FMGL(QUADRUPLE_BOND__status);
    static au::edu::anu::chem::BondType FMGL(QUADRUPLE_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(QUADRUPLE_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(QUADRUPLE_BOND__id);
    
    static au::edu::anu::chem::BondType FMGL(AROMATIC_BOND);
    
    static void FMGL(AROMATIC_BOND__do_init)();
    static void FMGL(AROMATIC_BOND__init)();
    static volatile x10aux::status FMGL(AROMATIC_BOND__status);
    static au::edu::anu::chem::BondType FMGL(AROMATIC_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(AROMATIC_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(AROMATIC_BOND__id);
    
    static au::edu::anu::chem::BondType FMGL(AMIDE_BOND);
    
    static void FMGL(AMIDE_BOND__do_init)();
    static void FMGL(AMIDE_BOND__init)();
    static volatile x10aux::status FMGL(AMIDE_BOND__status);
    static au::edu::anu::chem::BondType FMGL(AMIDE_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(AMIDE_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(AMIDE_BOND__id);
    
    static au::edu::anu::chem::BondType FMGL(IONIC_BOND);
    
    static void FMGL(IONIC_BOND__do_init)();
    static void FMGL(IONIC_BOND__init)();
    static volatile x10aux::status FMGL(IONIC_BOND__status);
    static au::edu::anu::chem::BondType FMGL(IONIC_BOND__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(IONIC_BOND__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(IONIC_BOND__id);
    
    x10_boolean isStrongBond() {
        
        //#line 35 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
        return (!x10aux::struct_equals((*this), au::edu::anu::chem::BondType::
                                                  FMGL(NO_BOND__get)())) &&
        (!x10aux::struct_equals((*this),
                                au::edu::anu::chem::BondType::
                                  FMGL(WEAK_BOND__get)()));
        
    }
    x10aux::ref<x10::lang::String> toString() {
        
        //#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
        return (*this)->FMGL(description);
        
    }
    x10aux::ref<x10::lang::String> typeName();
    x10_int hashCode();
    x10_boolean equals(x10aux::ref<x10::lang::Any> other);
    x10_boolean equals(au::edu::anu::chem::BondType other) {
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
        return (x10aux::struct_equals((*this)->FMGL(description),
                                      other->
                                        FMGL(description))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(bondOrder),
                               other->
                                 FMGL(bondOrder)));
        
    }
    x10_boolean _struct_equals(x10aux::ref<x10::lang::Any> other);
    x10_boolean _struct_equals(au::edu::anu::chem::BondType other) {
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
        return (x10aux::struct_equals((*this)->FMGL(description),
                                      other->
                                        FMGL(description))) &&
        (x10aux::struct_equals((*this)->
                                 FMGL(bondOrder),
                               other->
                                 FMGL(bondOrder)));
        
    }
    au::edu::anu::chem::BondType au__edu__anu__chem__BondType____au__edu__anu__chem__BondType__this(
      ) {
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10Return_c
        return (*this);
        
    }
    void __fieldInitializers52283() {
        
        //#line 16 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/BondType.x10": x10.ast.X10FieldAssign_c
        (*this)->FMGL(X10__object_lock_id0) = ((x10_int)-1);
    }
    
    static void _serialize(au::edu::anu::chem::BondType this_, x10aux::serialization_buffer& buf);
    
    static au::edu::anu::chem::BondType _deserialize(x10aux::deserialization_buffer& buf) {
        au::edu::anu::chem::BondType this_;
        this_->_deserialize_body(buf);
        return this_;
    }
    
    void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } } } 
#endif // AU_EDU_ANU_CHEM_BONDTYPE_H

namespace au { namespace edu { namespace anu { namespace chem { 
class BondType;
} } } } 

#ifndef AU_EDU_ANU_CHEM_BONDTYPE_H_NODEPS
#define AU_EDU_ANU_CHEM_BONDTYPE_H_NODEPS
#include <x10/lang/Any.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/String.h>
#include <x10/lang/Double.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Boolean.h>
#include <x10/compiler/Native.h>
#include <x10/compiler/NonEscaping.h>
#ifndef AU_EDU_ANU_CHEM_BONDTYPE_H_GENERICS
#define AU_EDU_ANU_CHEM_BONDTYPE_H_GENERICS
inline x10_int au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(X10__class_lock_id1);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(WEAK_BOND__get)() {
    if (FMGL(WEAK_BOND__status) != x10aux::INITIALIZED) {
        FMGL(WEAK_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(WEAK_BOND);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(NO_BOND__get)() {
    if (FMGL(NO_BOND__status) != x10aux::INITIALIZED) {
        FMGL(NO_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(NO_BOND);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(SINGLE_BOND__get)() {
    if (FMGL(SINGLE_BOND__status) != x10aux::INITIALIZED) {
        FMGL(SINGLE_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(SINGLE_BOND);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND__get)() {
    if (FMGL(DOUBLE_BOND__status) != x10aux::INITIALIZED) {
        FMGL(DOUBLE_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(DOUBLE_BOND);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND__get)() {
    if (FMGL(TRIPLE_BOND__status) != x10aux::INITIALIZED) {
        FMGL(TRIPLE_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(TRIPLE_BOND);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND__get)() {
    if (FMGL(QUADRUPLE_BOND__status) != x10aux::INITIALIZED) {
        FMGL(QUADRUPLE_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(QUADRUPLE_BOND);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND__get)() {
    if (FMGL(AROMATIC_BOND__status) != x10aux::INITIALIZED) {
        FMGL(AROMATIC_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(AROMATIC_BOND);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(AMIDE_BOND__get)() {
    if (FMGL(AMIDE_BOND__status) != x10aux::INITIALIZED) {
        FMGL(AMIDE_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(AMIDE_BOND);
}

inline au::edu::anu::chem::BondType au::edu::anu::chem::BondType::FMGL(IONIC_BOND__get)() {
    if (FMGL(IONIC_BOND__status) != x10aux::INITIALIZED) {
        FMGL(IONIC_BOND__init)();
    }
    return au::edu::anu::chem::BondType::FMGL(IONIC_BOND);
}

#endif // AU_EDU_ANU_CHEM_BONDTYPE_H_GENERICS
#endif // __AU_EDU_ANU_CHEM_BONDTYPE_H_NODEPS
