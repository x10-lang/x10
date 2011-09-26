#ifndef __AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H
#define __AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H

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
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace lang { 
class IllegalArgumentException;
} } 
namespace x10 { namespace array { 
class Region;
} } 
namespace x10 { namespace lang { 
class IntRange;
} } 
namespace x10 { namespace array { 
class RectLayout;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace x10 { namespace array { 
class RectRegion1D;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class Factorial;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 

class WignerRotationMatrix : public x10::lang::Object   {
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
    static x10_int FMGL(OPERATOR_A);
    
    static x10_int FMGL(OPERATOR_A__get)();
    static x10_int FMGL(OPERATOR_B);
    
    static x10_int FMGL(OPERATOR_B__get)();
    static x10_int FMGL(OPERATOR_C);
    
    static x10_int FMGL(OPERATOR_C__get)();
    static x10aux::ref<x10::array::Array<x10_double> > getDmk(x10_double theta,
                                                              x10_int l);
    static x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
      getCollection(
      x10_double theta,
      x10_int numTerms);
    static x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
      getExpandedCollection(
      x10_double theta,
      x10_int numTerms,
      x10_int op);
    static x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
      getACollection(
      x10_double theta,
      x10_int numTerms);
    static x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
      getBCollection(
      x10_double theta,
      x10_int numTerms);
    static x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Array<x10_double> > > > > >
      getCCollection(
      x10_double theta,
      x10_int numTerms);
    virtual x10aux::ref<au::edu::anu::mm::WignerRotationMatrix>
      au__edu__anu__mm__WignerRotationMatrix____au__edu__anu__mm__WignerRotationMatrix__this(
      );
    void _constructor();
    
    static x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> _make(
             );
    
    void _constructor(x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::WignerRotationMatrix> _make(
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    void __fieldInitializers43267();
    
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
#endif // AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H

namespace au { namespace edu { namespace anu { namespace mm { 
class WignerRotationMatrix;
} } } } 

#ifndef AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H_NODEPS
#define AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H_NODEPS
#ifndef AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H_GENERICS
#define AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H_GENERICS
inline x10_int au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::WignerRotationMatrix::FMGL(X10__class_lock_id1);
}

inline x10_int au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_A__get)() {
    return au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_A);
}

inline x10_int au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_B__get)() {
    return au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_B);
}

inline x10_int au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_C__get)() {
    return au::edu::anu::mm::WignerRotationMatrix::FMGL(OPERATOR_C);
}

#endif // AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H_GENERICS
#endif // __AU_EDU_ANU_MM_WIGNERROTATIONMATRIX_H_NODEPS
