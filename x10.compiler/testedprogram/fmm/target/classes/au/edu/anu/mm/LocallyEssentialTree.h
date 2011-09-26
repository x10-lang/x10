#ifndef __AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H
#define __AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H

#include <x10rt.h>


#define X10_LANG_OBJECT_H_NODEPS
#include <x10/lang/Object.h>
#undef X10_LANG_OBJECT_H_NODEPS
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
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class DistArray;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class MultipoleExpansion;
} } } } 
namespace au { namespace edu { namespace anu { namespace chem { 
class PointCharge;
} } } } 
namespace x10 { namespace array { 
class Region;
} } 
namespace x10 { namespace array { 
class RectRegion1D;
} } 
namespace x10 { namespace array { 
class RectLayout;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace x10 { namespace lang { 
class IntRange;
} } 
namespace x10 { namespace array { 
class Dist;
} } 
namespace x10 { namespace array { 
class PeriodicDist;
} } 
namespace x10 { namespace array { 
class ConstantDist;
} } 
namespace x10 { namespace lang { 
class Place;
} } 
namespace x10 { namespace lang { 
class Runtime;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 

class LocallyEssentialTree : public x10::lang::Object   {
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
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > FMGL(combinedUList);
    
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > >
      FMGL(combinedVList);
    
    x10aux::ref<x10::array::Array<x10_int> > FMGL(uListMin);
    
    x10aux::ref<x10::array::Array<x10_int> > FMGL(uListMax);
    
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > >
      FMGL(vListMin);
    
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > >
      FMGL(vListMax);
    
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::DistArray<x10aux::ref<au::edu::anu::mm::MultipoleExpansion> > > > >
      FMGL(multipoleCopies);
    
    x10aux::ref<x10::array::DistArray<x10aux::ref<x10::array::Array<au::edu::anu::chem::PointCharge> > > >
      FMGL(cachedAtoms);
    
    void _constructor(x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > combinedUList,
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > combinedVList,
                      x10aux::ref<x10::array::Array<x10_int> > uListMin,
                      x10aux::ref<x10::array::Array<x10_int> > uListMax,
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMin,
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMax);
    
    static x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> _make(
             x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > combinedUList,
             x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > combinedVList,
             x10aux::ref<x10::array::Array<x10_int> > uListMin,
             x10aux::ref<x10::array::Array<x10_int> > uListMax,
             x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMin,
             x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMax);
    
    void _constructor(x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > combinedUList,
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > combinedVList,
                      x10aux::ref<x10::array::Array<x10_int> > uListMin,
                      x10aux::ref<x10::array::Array<x10_int> > uListMax,
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMin,
                      x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMax,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::LocallyEssentialTree> _make(
             x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > combinedUList,
             x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > > > combinedVList,
             x10aux::ref<x10::array::Array<x10_int> > uListMin,
             x10aux::ref<x10::array::Array<x10_int> > uListMax,
             x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMin,
             x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_int> > > > vListMax,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10aux::ref<au::edu::anu::mm::LocallyEssentialTree>
      au__edu__anu__mm__LocallyEssentialTree____au__edu__anu__mm__LocallyEssentialTree__this(
      );
    void __fieldInitializers47383();
    
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
#endif // AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H

namespace au { namespace edu { namespace anu { namespace mm { 
class LocallyEssentialTree;
} } } } 

#ifndef AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H_NODEPS
#define AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H_NODEPS
#ifndef AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H_GENERICS
#define AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H_GENERICS
inline x10_int au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::LocallyEssentialTree::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H_GENERICS
#endif // __AU_EDU_ANU_MM_LOCALLYESSENTIALTREE_H_NODEPS
