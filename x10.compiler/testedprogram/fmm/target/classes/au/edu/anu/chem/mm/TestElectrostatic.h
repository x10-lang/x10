#ifndef __AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H
#define __AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H

#include <x10rt.h>


#define X10_LANG_OBJECT_H_NODEPS
#include <x10/lang/Object.h>
#undef X10_LANG_OBJECT_H_NODEPS
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
class Long;
} } 
namespace x10 { namespace util { 
class Random;
} } 
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace au { namespace edu { namespace anu { namespace util { 
class Timer;
} } } } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace io { 
class Printer;
} } 
namespace x10 { namespace io { 
class Console;
} } 
namespace x10 { namespace lang { 
class Any;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class DistArray;
} } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class MMAtom;
} } } } } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class ArrayList;
} } 
namespace x10 { namespace array { 
class Dist;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(Z1), class TPMGL(U)> class Fun_0_1;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace lang { 
class Runtime;
} } 
namespace x10 { namespace lang { 
class FinishState;
} } 
namespace x10 { namespace lang { 
class Throwable;
} } 
namespace x10 { namespace lang { 
class Place;
} } 
namespace x10 { namespace lang { 
class VoidFun_0_0;
} } 
namespace x10x { namespace vector { 
class Point3d;
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
namespace x10 { namespace compiler { 
class AsyncClosure;
} } 
namespace x10 { namespace lang { 
class RuntimeException;
} } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 

class TestElectrostatic : public x10::lang::Object   {
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
    static x10_long FMGL(RANDOM_SEED);
    
    static x10_long FMGL(RANDOM_SEED__get)();
    static x10aux::ref<x10::util::Random> FMGL(R);
    
    static void FMGL(R__do_init)();
    static void FMGL(R__init)();
    static volatile x10aux::status FMGL(R__status);
    static x10aux::ref<x10::util::Random> FMGL(R__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(R__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(R__id);
    
    static x10_double FMGL(NOISE);
    
    static x10_double FMGL(NOISE__get)();
    x10_double FMGL(SIZE);
    
    virtual x10_double sizeOfCentralCluster();
    virtual void logTime(x10aux::ref<x10::lang::String> desc, x10_int timerIndex,
                         x10aux::ref<au::edu::anu::util::Timer> timer,
                         x10_boolean printNewline);
    virtual void logTime(x10aux::ref<x10::lang::String> desc, x10_int timerIndex,
                         x10aux::ref<au::edu::anu::util::Timer> timer);
    virtual x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >
      generateAtoms(
      x10_int numAtoms);
    virtual x10_int getPlaceId(x10_double x, x10_double y,
                               x10_double z);
    static x10_double randomNoise();
    virtual x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic>
      au__edu__anu__chem__mm__TestElectrostatic____au__edu__anu__chem__mm__TestElectrostatic__this(
      );
    void _constructor();
    
    static x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> _make(
             );
    
    void _constructor(x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::chem::mm::TestElectrostatic> _make(
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void __fieldInitializers23633();
    
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
#endif // AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H

namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class TestElectrostatic;
} } } } } 

#ifndef AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H_NODEPS
#define AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H_NODEPS
#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/lang/Long.h>
#include <x10/util/Random.h>
#include <x10/lang/Double.h>
#include <x10/lang/String.h>
#include <au/edu/anu/util/Timer.h>
#include <x10/lang/Boolean.h>
#include <x10/io/Printer.h>
#include <x10/io/Console.h>
#include <x10/lang/Any.h>
#include <x10/array/Array.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/array/DistArray.h>
#include <au/edu/anu/chem/mm/MMAtom.h>
#include <x10/util/ArrayList.h>
#include <x10/array/Dist.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/array/Point.h>
#include <x10/lang/Math.h>
#include <x10/lang/Runtime.h>
#include <x10/lang/FinishState.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/Place.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10x/vector/Point3d.h>
#include <x10/compiler/Finalization.h>
#include <x10/compiler/Abort.h>
#include <x10/compiler/CompilerFlags.h>
#include <x10/compiler/AsyncClosure.h>
#include <x10/lang/RuntimeException.h>
#ifndef AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H_GENERICS
#define AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H_GENERICS
inline x10_int au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::chem::mm::TestElectrostatic::FMGL(X10__class_lock_id1);
}

inline x10_long au::edu::anu::chem::mm::TestElectrostatic::FMGL(RANDOM_SEED__get)() {
    return au::edu::anu::chem::mm::TestElectrostatic::FMGL(RANDOM_SEED);
}

inline x10aux::ref<x10::util::Random> au::edu::anu::chem::mm::TestElectrostatic::FMGL(R__get)() {
    if (FMGL(R__status) != x10aux::INITIALIZED) {
        FMGL(R__init)();
    }
    return au::edu::anu::chem::mm::TestElectrostatic::FMGL(R);
}

inline x10_double au::edu::anu::chem::mm::TestElectrostatic::FMGL(NOISE__get)() {
    return au::edu::anu::chem::mm::TestElectrostatic::FMGL(NOISE);
}

#endif // AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H_GENERICS
#endif // __AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H_NODEPS
