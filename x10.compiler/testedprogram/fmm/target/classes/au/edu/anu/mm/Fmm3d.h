#ifndef __AU_EDU_ANU_MM_FMM3D_H
#define __AU_EDU_ANU_MM_FMM3D_H

#include <x10rt.h>


#define X10_LANG_OBJECT_H_NODEPS
#include <x10/lang/Object.h>
#undef X10_LANG_OBJECT_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10_LANG_INT_H_NODEPS
#include <x10/lang/Int.h>
#undef X10_LANG_INT_H_NODEPS
#define X10X_VECTOR_VECTOR3D_H_NODEPS
#include <x10x/vector/Vector3d.h>
#undef X10X_VECTOR_VECTOR3D_H_NODEPS
#define X10_LANG_DOUBLE_H_NODEPS
#include <x10/lang/Double.h>
#undef X10_LANG_DOUBLE_H_NODEPS
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
namespace x10x { namespace vector { 
class Vector3d;
} } 
namespace x10 { namespace lang { 
class Double;
} } 
namespace au { namespace edu { namespace anu { namespace util { 
class Timer;
} } } } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class DistArray;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class FmmBox;
} } } } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class MMAtom;
} } } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class LocalExpansion;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class MultipoleExpansion;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class LocallyEssentialTree;
} } } } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace lang { 
class Complex;
} } 
namespace x10x { namespace vector { 
class Point3d;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
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
namespace x10 { namespace array { 
class Dist;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterator;
} } 
namespace x10 { namespace lang { 
class Place;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Iterable;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace x10 { namespace array { 
class Region;
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
namespace x10x { namespace vector { 
class Tuple3d;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class Expansion;
} } } } 
namespace x10 { namespace array { 
class RectRegion1D;
} } 
namespace x10 { namespace array { 
class RectLayout;
} } 
namespace x10 { namespace lang { 
class IntRange;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class GlobalRef;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(Z1), class TPMGL(U)> class Fun_0_1;
} } 
namespace x10 { namespace compiler { 
class Inline;
} } 
namespace x10 { namespace util { 
template<class TPMGL(K), class TPMGL(V)> class HashMap;
} } 
namespace x10 { namespace util { 
template<class TPMGL(Key), class TPMGL(Val)> class Map__Entry;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(U)> class Fun_0_0;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class Reducible;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class Fmm3d__SumReducer;
} } } } 
namespace x10 { namespace array { 
class PlaceGroup;
} } 
namespace x10 { namespace array { 
class BlockDist;
} } 
namespace x10x { namespace polar { 
class Polar3d;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class WignerRotationMatrix;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class MortonDist;
} } } } 
namespace x10 { namespace array { 
class PeriodicDist;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class HashSet;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class MapSet;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 

class Fmm3d : public x10::lang::Object   {
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
    x10_int FMGL(numLevels);
    
    x10_int FMGL(lowestLevelDim);
    
    x10x::vector::Vector3d FMGL(offset);
    
    x10_double FMGL(size);
    
    x10_int FMGL(numTerms);
    
    x10_int FMGL(ws);
    
    x10_int FMGL(topLevel);
    
    static x10_int FMGL(TIMER_INDEX_TOTAL);
    
    static x10_int FMGL(TIMER_INDEX_TOTAL__get)();
    static x10_int FMGL(TIMER_INDEX_PREFETCH);
    
    static x10_int FMGL(TIMER_INDEX_PREFETCH__get)();
    static x10_int FMGL(TIMER_INDEX_DIRECT);
    
    static x10_int FMGL(TIMER_INDEX_DIRECT__get)();
    static x10_int FMGL(TIMER_INDEX_MULTIPOLE);
    
    static x10_int FMGL(TIMER_INDEX_MULTIPOLE__get)();
    static x10_int FMGL(TIMER_INDEX_COMBINE);
    
    static x10_int FMGL(TIMER_INDEX_COMBINE__get)();
    static x10_int FMGL(TIMER_INDEX_TRANSFORM);
    
    static x10_int FMGL(TIMER_INDEX_TRANSFORM__get)();
    static x10_int FMGL(TIMER_INDEX_FARFIELD);
    
    static x10_int FMGL(TIMER_INDEX_FARFIELD__get)();
    static x10_int FMGL(TIMER_INDEX_TREE);
    
    static x10_int FMGL(TIMER_INDEX_TREE__get)();
    static x10_int FMGL(TIMER_INDEX_PLACEHOLDER);
    
    static x10_int FMGL(TIMER_INDEX_PLACEHOLDER__get)();
    x10aux::ref<au::edu::anu::util::Timer> FMGL(timer);
    
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > >
      FMGL(boxes);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > >
      FMGL(lowestLevelBoxes);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > >
      FMGL(atoms);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocalExpansion> > >
      FMGL(multipoleTransforms);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >
      FMGL(multipoleTranslations);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > >
      FMGL(locallyEssentialTrees);
    
    x10_boolean FMGL(periodic);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > > > > >
      FMGL(wignerA);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > > > > >
      FMGL(wignerB);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > > > > >
      FMGL(wignerC);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > > > > >
      FMGL(complexK);
    
    static x10_boolean FMGL(useOldOperators);
    
    static x10_boolean FMGL(useOldOperators__get)();
    void _constructor(x10_double density,
                      x10_int numTerms,
                      x10_int ws,
                      x10x::vector::Point3d topLeftFront,
                      x10_double size,
                      x10_int numAtoms,
                      x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms);
    
    static x10aux::ref<au::edu::anu::mm::Fmm3d> _make(
             x10_double density,
             x10_int numTerms,
             x10_int ws,
             x10x::vector::Point3d topLeftFront,
             x10_double size,
             x10_int numAtoms,
             x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms);
    
    void _constructor(x10_double density,
                      x10_int numTerms,
                      x10_int ws,
                      x10x::vector::Point3d topLeftFront,
                      x10_double size,
                      x10_int numAtoms,
                      x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::Fmm3d> _make(
             x10_double density,
             x10_int numTerms,
             x10_int ws,
             x10x::vector::Point3d topLeftFront,
             x10_double size,
             x10_int numAtoms,
             x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void _constructor(x10_double density,
                      x10_int numTerms,
                      x10_int ws,
                      x10x::vector::Point3d topLeftFront,
                      x10_double size,
                      x10_int numAtoms,
                      x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
                      x10_int topLevel,
                      x10_boolean periodic);
    
    static x10aux::ref<au::edu::anu::mm::Fmm3d> _make(
             x10_double density,
             x10_int numTerms,
             x10_int ws,
             x10x::vector::Point3d topLeftFront,
             x10_double size,
             x10_int numAtoms,
             x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
             x10_int topLevel,
             x10_boolean periodic);
    
    void _constructor(x10_double density,
                      x10_int numTerms,
                      x10_int ws,
                      x10x::vector::Point3d topLeftFront,
                      x10_double size,
                      x10_int numAtoms,
                      x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
                      x10_int topLevel,
                      x10_boolean periodic,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::Fmm3d> _make(
             x10_double density,
             x10_int numTerms,
             x10_int ws,
             x10x::vector::Point3d topLeftFront,
             x10_double size,
             x10_int numAtoms,
             x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::chem::mm::MMAtom> > > > > atoms,
             x10_int topLevel,
             x10_boolean periodic,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10_double calculateEnergy();
    virtual void assignAtomsToBoxes();
    virtual void multipoleLowestLevel();
    virtual void combineMultipoles();
    virtual void transformToLocal();
    static x10x::vector::Vector3d getChildBoxVector(
      x10aux::ref<x10::array::Point> shift,
      x10_double sideLength);
    static x10aux::ref<x10::array::Point>
      getChildBoxCentreIndex(
      x10aux::ref<x10::array::Point> shift);
    static void prefetchMultipoles(x10_int level,
                                   x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > thisLevelBoxes,
                                   x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > > locallyEssentialTrees);
    static x10aux::ref<x10::array::Array<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >
      getMultipolesForBoxList(
      x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > thisLevelBoxes,
      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > boxList);
    virtual void prefetchRemoteAtoms();
    static x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >
      getAtomsForBoxList(
      x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes,
      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > boxList);
    virtual x10_double getDirectEnergy();
    virtual x10_double getFarFieldEnergy(
      );
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > >
      precomputeTranslations(
      x10_int numLevels,
      x10_int topLevel,
      x10_int numTerms,
      x10_double size);
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > > > > >
      precomputeWignerA(
      x10_int numTerms);
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > > > > >
      precomputeWignerC(
      x10_int numTerms);
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > > > > > >
      precomputeWignerB(
      x10_int numTerms,
      x10_int ws);
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10::lang::Complex> > > > > > > >
      precomputeComplex(
      x10_int numTerms,
      x10_int ws);
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocalExpansion> > >
      precomputeTransforms(
      x10_int numLevels,
      x10_int topLevel,
      x10_int numTerms,
      x10_double size);
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > >
      constructTree(
      x10_int numLevels,
      x10_int topLevel,
      x10_int numTerms,
      x10_int ws,
      x10_boolean periodic);
    x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> > >
      createLocallyEssentialTrees(
      x10_int numLevels,
      x10_int topLevel,
      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > > boxes,
      x10_boolean periodic);
    static x10aux::ref<x10::array::Point>
      getLowestLevelBoxIndex(
      x10x::vector::Point3d offsetCentre,
      x10_int lowestLevelDim,
      x10_double size);
    static x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> >
      getParentForChild(
      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > > > boxes,
      x10_int level,
      x10_int topLevel,
      x10_int x,
      x10_int y,
      x10_int z);
    static x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> >
      getAtomsForBox(
      x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > lowestLevelBoxes,
      x10_int x,
      x10_int y,
      x10_int z);
    static x10aux::ref<au::edu::anu::mm::MultipoleExpansion>
      getMultipoleExpansionLocalCopy(
      x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::FmmBox> > > thisLevelBoxes,
      x10_int x,
      x10_int y,
      x10_int z);
    virtual x10aux::ref<au::edu::anu::mm::Fmm3d>
      au__edu__anu__mm__Fmm3d____au__edu__anu__mm__Fmm3d__this(
      );
    void __fieldInitializers34574();
    
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
#endif // AU_EDU_ANU_MM_FMM3D_H

namespace au { namespace edu { namespace anu { namespace mm { 
class Fmm3d;
} } } } 

#ifndef AU_EDU_ANU_MM_FMM3D_H_NODEPS
#define AU_EDU_ANU_MM_FMM3D_H_NODEPS
#include <x10/lang/Object.h>
#include <x10/util/concurrent/Atomic.h>
#include <x10/lang/Int.h>
#include <x10/util/concurrent/OrderedLock.h>
#include <x10/util/Map.h>
#include <x10x/vector/Vector3d.h>
#include <x10/lang/Double.h>
#include <au/edu/anu/util/Timer.h>
#include <x10/array/Array.h>
#include <x10/array/DistArray.h>
#include <au/edu/anu/mm/FmmBox.h>
#include <au/edu/anu/chem/mm/MMAtom.h>
#include <au/edu/anu/mm/LocalExpansion.h>
#include <au/edu/anu/mm/MultipoleExpansion.h>
#include <au/edu/anu/mm/LocallyEssentialTree.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/Complex.h>
#include <x10x/vector/Point3d.h>
#include <x10/lang/Math.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/Runtime.h>
#include <x10/lang/FinishState.h>
#include <x10/lang/Throwable.h>
#include <x10/lang/VoidFun_0_0.h>
#include <x10/compiler/AsyncClosure.h>
#include <x10/lang/RuntimeException.h>
#include <x10/compiler/Finalization.h>
#include <x10/compiler/Abort.h>
#include <x10/compiler/CompilerFlags.h>
#include <x10/array/Dist.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/Place.h>
#include <x10/lang/Iterable.h>
#include <x10/array/Point.h>
#include <x10/array/Region.h>
#include <au/edu/anu/chem/Atom.h>
#include <au/edu/anu/chem/PointCharge.h>
#include <au/edu/anu/mm/FmmLeafBox.h>
#include <x10/util/ArrayList.h>
#include <x10x/vector/Tuple3d.h>
#include <au/edu/anu/mm/Expansion.h>
#include <x10/array/RectRegion1D.h>
#include <x10/array/RectLayout.h>
#include <x10/lang/IntRange.h>
#include <x10/lang/GlobalRef.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/compiler/Inline.h>
#include <x10/util/HashMap.h>
#include <x10/util/Map__Entry.h>
#include <x10/lang/Fun_0_0.h>
#include <x10/lang/Reducible.h>
#include <au/edu/anu/mm/Fmm3d__SumReducer.h>
#include <x10/array/PlaceGroup.h>
#include <x10/array/BlockDist.h>
#include <x10x/polar/Polar3d.h>
#include <au/edu/anu/mm/WignerRotationMatrix.h>
#include <au/edu/anu/mm/MortonDist.h>
#include <x10/array/PeriodicDist.h>
#include <x10/util/HashSet.h>
#include <x10/util/MapSet.h>
#ifndef AU_EDU_ANU_MM_FMM3D_H_GENERICS
#define AU_EDU_ANU_MM_FMM3D_H_GENERICS
inline x10_int au::edu::anu::mm::Fmm3d::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::Fmm3d::FMGL(X10__class_lock_id1);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_TOTAL__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_TOTAL);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_PREFETCH__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_PREFETCH);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_DIRECT__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_DIRECT);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_MULTIPOLE__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_MULTIPOLE);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_COMBINE__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_COMBINE);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_TRANSFORM__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_TRANSFORM);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_FARFIELD__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_FARFIELD);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_TREE__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_TREE);
}

inline x10_int au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_PLACEHOLDER__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(TIMER_INDEX_PLACEHOLDER);
}

inline x10_boolean au::edu::anu::mm::Fmm3d::FMGL(useOldOperators__get)() {
    return au::edu::anu::mm::Fmm3d::FMGL(useOldOperators);
}

#endif // AU_EDU_ANU_MM_FMM3D_H_GENERICS
#endif // __AU_EDU_ANU_MM_FMM3D_H_NODEPS
