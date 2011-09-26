#ifndef __AU_EDU_ANU_MM_PERIODICFMM3D_H
#define __AU_EDU_ANU_MM_PERIODICFMM3D_H

#include <x10rt.h>


#define AU_EDU_ANU_MM_FMM3D_H_NODEPS
#include <au/edu/anu/mm/Fmm3d.h>
#undef AU_EDU_ANU_MM_FMM3D_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10_LANG_INT_H_NODEPS
#include <x10/lang/Int.h>
#undef X10_LANG_INT_H_NODEPS
namespace x10 { namespace lang { 
class Int;
} } 
namespace x10 { namespace util { namespace concurrent { 
class OrderedLock;
} } } 
namespace x10 { namespace util { 
template<class TPMGL(K), class TPMGL(V)> class Map;
} } 
namespace x10 { namespace array { 
class Region;
} } 
namespace x10 { namespace lang { 
class IntRange;
} } 
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10x { namespace vector { 
class Point3d;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class DistArray;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class MMAtom;
} } } } } 
namespace au { namespace edu { namespace anu { namespace util { 
class Timer;
} } } } 
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
class VoidFun_0_0;
} } 
namespace x10 { namespace compiler { 
class AsyncClosure;
} } 
namespace x10 { namespace lang { 
class RuntimeException;
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
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class FmmBox;
} } } } 
namespace x10 { namespace array { 
class Dist;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class MultipoleExpansion;
} } } } 
namespace x10 { namespace array { 
class RectRegion1D;
} } 
namespace x10 { namespace array { 
class RectLayout;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class LocalExpansion;
} } } } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace x10x { namespace vector { 
class Vector3d;
} } 
namespace x10x { namespace vector { 
class Tuple3d;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class Expansion;
} } } } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Reducible;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class PeriodicFmm3d__VectorSumReducer;
} } } } 
namespace x10 { namespace lang { 
class Place;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterable;
} } 
namespace au { namespace edu { namespace anu { namespace chem { 
class Atom;
} } } } 
namespace au { namespace edu { namespace anu { namespace chem { 
class PointCharge;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class FmmLeafBox;
} } } } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class ArrayList;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class LocallyEssentialTree;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class Fmm3d__SumReducer;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 

class PeriodicFmm3d : public au::edu::anu::mm::Fmm3d   {
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
    x10_int FMGL(numShells);
    
    static x10_int FMGL(TIMER_INDEX_MACROSCOPIC);
    
    static x10_int FMGL(TIMER_INDEX_MACROSCOPIC__get)();
    static x10aux::ref<x10::array::Region> FMGL(threeCube);
    
    static void FMGL(threeCube__do_init)();
    static void FMGL(threeCube__init)();
    static volatile x10aux::status FMGL(threeCube__status);
    static x10aux::ref<x10::array::Region> FMGL(threeCube__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(threeCube__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(threeCube__id);
    
    static x10aux::ref<x10::array::Region> FMGL(nineCube);
    
    static void FMGL(nineCube__do_init)();
    static void FMGL(nineCube__init)();
    static volatile x10aux::status FMGL(nineCube__status);
    static x10aux::ref<x10::array::Region> FMGL(nineCube__get)();
    static x10aux::ref<x10::lang::Reference> FMGL(nineCube__deserialize)(x10aux::deserialization_buffer &buf);
    static const x10aux::serialization_id_t FMGL(nineCube__id);
    
    void _constructor(x10_double density, x10_int numTerms, x10x::vector::Point3d topLeftFront,
                      x10_double size,
                      x10_int numAtoms,
                      x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
                      x10_int numShells);
    
    static x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> _make(
             x10_double density,
             x10_int numTerms,
             x10x::vector::Point3d topLeftFront,
             x10_double size,
             x10_int numAtoms,
             x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
             x10_int numShells);
    
    void _constructor(x10_double density, x10_int numTerms,
                      x10x::vector::Point3d topLeftFront,
                      x10_double size,
                      x10_int numAtoms,
                      x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
                      x10_int numShells,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::PeriodicFmm3d> _make(
             x10_double density,
             x10_int numTerms,
             x10x::vector::Point3d topLeftFront,
             x10_double size,
             x10_int numAtoms,
             x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
             x10_int numShells,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10_double calculateEnergy();
    virtual void combineMacroscopicExpansions(
      );
    virtual void assignAtomsToBoxes();
    virtual void addAtomToLowestLevelBoxAsync(
      x10aux::ref<x10::array::Point> boxIndex,
      x10x::vector::Point3d offsetCentre,
      x10_double charge);
    virtual x10x::vector::Vector3d cancelDipole(
      x10x::vector::Vector3d dipole);
    virtual x10_double getDirectEnergy();
    static x10x::vector::Vector3d getTranslation(
      x10_int lowestLevelDim,
      x10_double size,
      x10_int x,
      x10_int y,
      x10_int z);
    virtual x10aux::ref<au::edu::anu::mm::PeriodicFmm3d>
      au__edu__anu__mm__PeriodicFmm3d____au__edu__anu__mm__PeriodicFmm3d__this(
      );
    void __fieldInitializers24245();
    
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
#endif // AU_EDU_ANU_MM_PERIODICFMM3D_H

namespace au { namespace edu { namespace anu { namespace mm { 
class PeriodicFmm3d;
} } } } 

#ifndef AU_EDU_ANU_MM_PERIODICFMM3D_H_NODEPS
#define AU_EDU_ANU_MM_PERIODICFMM3D_H_NODEPS
#include <au/edu/anu/mm/Fmm3d.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10/array/Region.h>
#include <x10/lang/IntRange.h>
#include <x10/lang/Double.h>
#include <x10x/vector/Point3d.h>
#include <x10/array/DistArray.h>
#include <x10/array/Array.h>
#include <au/edu/anu/chem/mm/MMAtom.h>
#include <au/edu/anu/util/Timer.h>
#include <x10/lang/Runtime.h>
#include <x10/lang/FinishState.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/compiler/AsyncClosure.h>
#include <x10/lang/RuntimeException.h>
#include <x10/compiler/Finalization.h>
#include <x10/compiler/Abort.h>
#include <x10/compiler/CompilerFlags.h>
#include <x10/lang/Boolean.h>
#include <au/edu/anu/mm/FmmBox.h>
#include <x10/array/Dist.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <au/edu/anu/mm/MultipoleExpansion.h>
#include <x10/array/RectRegion1D.h>
#include <x10/array/RectLayout.h>
#include <au/edu/anu/mm/LocalExpansion.h>
#include <x10/lang/Iterator.h>
#include <x10/array/Point.h>
#include <x10x/vector/Vector3d.h>
#include <x10x/vector/Tuple3d.h>
#include <au/edu/anu/mm/Expansion.h>
#include <x10/lang/Math.h>
#include <x10/lang/Reducible.h>
#include <au/edu/anu/mm/PeriodicFmm3d__VectorSumReducer.h>
#include <x10/lang/Place.h>
#include <x10/lang/Iterable.h>
#include <au/edu/anu/chem/Atom.h>
#include <au/edu/anu/chem/PointCharge.h>
#include <au/edu/anu/mm/FmmLeafBox.h>
#include <x10/util/ArrayList.h>
#include <au/edu/anu/mm/LocallyEssentialTree.h>
#include <au/edu/anu/mm/Fmm3d__SumReducer.h>
#ifndef AU_EDU_ANU_MM_PERIODICFMM3D_H_GENERICS
#define AU_EDU_ANU_MM_PERIODICFMM3D_H_GENERICS
inline x10_int au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::PeriodicFmm3d::FMGL(X10__class_lock_id1);
}

inline x10_int au::edu::anu::mm::PeriodicFmm3d::FMGL(TIMER_INDEX_MACROSCOPIC__get)() {
    return au::edu::anu::mm::PeriodicFmm3d::FMGL(TIMER_INDEX_MACROSCOPIC);
}

inline x10aux::ref<x10::array::Region> au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube__get)() {
    if (FMGL(threeCube__status) != x10aux::INITIALIZED) {
        FMGL(threeCube__init)();
    }
    return au::edu::anu::mm::PeriodicFmm3d::FMGL(threeCube);
}

inline x10aux::ref<x10::array::Region> au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube__get)() {
    if (FMGL(nineCube__status) != x10aux::INITIALIZED) {
        FMGL(nineCube__init)();
    }
    return au::edu::anu::mm::PeriodicFmm3d::FMGL(nineCube);
}

#endif // AU_EDU_ANU_MM_PERIODICFMM3D_H_GENERICS
#endif // __AU_EDU_ANU_MM_PERIODICFMM3D_H_NODEPS
