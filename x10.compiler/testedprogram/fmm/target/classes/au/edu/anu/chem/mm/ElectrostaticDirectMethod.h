#ifndef __AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H
#define __AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H

#include <x10rt.h>


#define X10_LANG_OBJECT_H_NODEPS
#include <x10/lang/Object.h>
#undef X10_LANG_OBJECT_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10_LANG_BOOLEAN_H_NODEPS
#include <x10/lang/Boolean.h>
#undef X10_LANG_BOOLEAN_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
namespace x10 { namespace util { namespace concurrent { 
class OrderedLock;
} } } 
namespace x10 { namespace util { 
template<class TPMGL(K), class TPMGL(V)> class Map;
} } 
namespace au { namespace edu { namespace anu { namespace util { 
class Timer;
} } } } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class DistArray;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace au { namespace edu { namespace anu { namespace chem { 
class PointCharge;
} } } } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class MMAtom;
} } } } } 
namespace x10 { namespace array { 
class Dist;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(Z1), class TPMGL(U)> class Fun_0_1;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace au { namespace edu { namespace anu { namespace chem { 
class Atom;
} } } } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace x10 { namespace array { 
class Region;
} } 
namespace x10 { namespace array { 
class RectRegion1D;
} } 
namespace x10 { namespace array { 
class RectLayout;
} } 
namespace x10 { namespace lang { 
class Place;
} } 
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10 { namespace lang { 
class FinishState;
} } 
namespace x10 { namespace lang { 
class Runtime;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Reducible;
} } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class ElectrostaticDirectMethod__SumReducer;
} } } } } 
namespace x10 { namespace lang { 
class Throwable;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterable;
} } 
namespace x10 { namespace lang { 
class VoidFun_0_0;
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
class Point3d;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace compiler { 
class AsyncClosure;
} } 
namespace x10 { namespace lang { 
class RuntimeException;
} } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 

class ElectrostaticDirectMethod : public x10::lang::Object   {
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
    static x10_int FMGL(TIMER_INDEX_TOTAL);
    
    static x10_int FMGL(TIMER_INDEX_TOTAL__get)();
    x10aux::ref<au::edu::anu::util::Timer> FMGL(timer);
    
    x10_boolean FMGL(asyncComms);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >
      FMGL(atoms);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > > > >
      FMGL(otherAtoms);
    
    void _constructor(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms);
    
    static x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod> _make(
             x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms);
    
    void _constructor(x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod> _make(
             x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10_double getEnergy();
    virtual x10aux::ref<au::edu::anu::chem::mm::ElectrostaticDirectMethod>
      au__edu__anu__chem__mm__ElectrostaticDirectMethod____au__edu__anu__chem__mm__ElectrostaticDirectMethod__this(
      );
    void __fieldInitializers25763();
    
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
#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H

namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class ElectrostaticDirectMethod;
} } } } } 

#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H_NODEPS
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H_NODEPS
#ifndef AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H_GENERICS
#define AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H_GENERICS
inline x10_int au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(X10__class_lock_id1);
}

inline x10_int au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(TIMER_INDEX_TOTAL__get)() {
    return au::edu::anu::chem::mm::ElectrostaticDirectMethod::FMGL(TIMER_INDEX_TOTAL);
}

#endif // AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H_GENERICS
#endif // __AU_EDU_ANU_CHEM_MM_ELECTROSTATICDIRECTMETHOD_H_NODEPS
