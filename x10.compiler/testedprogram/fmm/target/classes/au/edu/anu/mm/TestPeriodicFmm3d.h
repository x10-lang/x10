#ifndef __AU_EDU_ANU_MM_TESTPERIODICFMM3D_H
#define __AU_EDU_ANU_MM_TESTPERIODICFMM3D_H

#include <x10rt.h>


#define AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H_NODEPS
#include <au/edu/anu/chem/mm/TestElectrostatic.h>
#undef AU_EDU_ANU_CHEM_MM_TESTELECTROSTATIC_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
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
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace x10 { namespace array { 
class RectLayout;
} } 
namespace x10 { namespace lang { 
class Any;
} } 
namespace x10 { namespace io { 
class Printer;
} } 
namespace x10 { namespace io { 
class Console;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class DistArray;
} } 
namespace au { namespace edu { namespace anu { namespace chem { namespace mm { 
class MMAtom;
} } } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class PeriodicFmm3d;
} } } } 
namespace x10x { namespace vector { 
class Point3d;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class Fmm3d;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 

class TestPeriodicFmm3d : public au::edu::anu::chem::mm::TestElectrostatic
  {
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
    virtual x10_double sizeOfCentralCluster();
    static void main(x10aux::ref<x10::array::Array<x10aux::ref<x10::lang::String> > > args);
    virtual void test(x10_int numAtoms, x10_double density, x10_int numTerms,
                      x10_int numShells,
                      x10_boolean verbose);
    virtual x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> au__edu__anu__mm__TestPeriodicFmm3d____au__edu__anu__mm__TestPeriodicFmm3d__this(
      );
    void _constructor();
    
    static x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> _make(
             );
    
    void _constructor(x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::TestPeriodicFmm3d> _make(
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void __fieldInitializers17081();
    
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
#endif // AU_EDU_ANU_MM_TESTPERIODICFMM3D_H

namespace au { namespace edu { namespace anu { namespace mm { 
class TestPeriodicFmm3d;
} } } } 

#ifndef AU_EDU_ANU_MM_TESTPERIODICFMM3D_H_NODEPS
#define AU_EDU_ANU_MM_TESTPERIODICFMM3D_H_NODEPS
#ifndef AU_EDU_ANU_MM_TESTPERIODICFMM3D_H_GENERICS
#define AU_EDU_ANU_MM_TESTPERIODICFMM3D_H_GENERICS
inline x10_int au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::TestPeriodicFmm3d::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_TESTPERIODICFMM3D_H_GENERICS
#endif // __AU_EDU_ANU_MM_TESTPERIODICFMM3D_H_NODEPS
