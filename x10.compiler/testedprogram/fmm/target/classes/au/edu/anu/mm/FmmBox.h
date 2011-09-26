#ifndef __AU_EDU_ANU_MM_FMMBOX_H
#define __AU_EDU_ANU_MM_FMMBOX_H

#include <x10rt.h>


#define X10_LANG_OBJECT_H_NODEPS
#include <x10/lang/Object.h>
#undef X10_LANG_OBJECT_H_NODEPS
#define X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#include <x10/util/concurrent/Atomic.h>
#undef X10_UTIL_CONCURRENT_ATOMIC_H_NODEPS
#define X10_LANG_GLOBALREF_H_NODEPS
#include <x10/lang/GlobalRef.h>
#undef X10_LANG_GLOBALREF_H_NODEPS
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
namespace x10 { namespace lang { 
template<class TPMGL(T)> class GlobalRef;
} } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 
class MultipoleExpansion;
} } } } 
namespace au { namespace edu { namespace anu { namespace mm { 
class LocalExpansion;
} } } } 
namespace x10 { namespace lang { 
class Double;
} } 
namespace x10x { namespace vector { 
class Point3d;
} } 
namespace x10 { namespace lang { 
class Boolean;
} } 
namespace x10 { namespace lang { 
class Math;
} } 
namespace x10 { namespace compiler { 
class Inline;
} } 
namespace x10 { namespace util { 
template<class TPMGL(T)> class ArrayList;
} } 
namespace x10 { namespace lang { 
class String;
} } 
namespace au { namespace edu { namespace anu { namespace mm { 

class FmmBox : public x10::lang::Object   {
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
    x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > FMGL(parent);
    
    x10_int FMGL(level);
    
    x10_int FMGL(x);
    
    x10_int FMGL(y);
    
    x10_int FMGL(z);
    
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > FMGL(vList);
    
    x10aux::ref<au::edu::anu::mm::MultipoleExpansion> FMGL(multipoleExp);
    
    x10aux::ref<au::edu::anu::mm::LocalExpansion> FMGL(localExp);
    
    void _constructor(x10_int level, x10_int x, x10_int y, x10_int z, x10_int numTerms,
                      x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent);
    
    static x10aux::ref<au::edu::anu::mm::FmmBox> _make(x10_int level,
                                                       x10_int x,
                                                       x10_int y,
                                                       x10_int z,
                                                       x10_int numTerms,
                                                       x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent);
    
    void _constructor(x10_int level, x10_int x, x10_int y, x10_int z,
                      x10_int numTerms,
                      x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::FmmBox> _make(x10_int level,
                                                       x10_int x,
                                                       x10_int y,
                                                       x10_int z,
                                                       x10_int numTerms,
                                                       x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent,
                                                       x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual x10x::vector::Point3d getCentre(x10_double size);
    virtual x10_boolean wellSeparated(x10_int ws,
                                      x10_int x2,
                                      x10_int y2,
                                      x10_int z2);
    virtual x10_boolean wellSeparated(x10_int ws,
                                      x10aux::ref<au::edu::anu::mm::FmmBox> box2);
    virtual x10aux::ref<x10::array::Point> getTranslationIndex(
      x10aux::ref<x10::array::Point> boxIndex2);
    virtual x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > >
      getVList(
      );
    virtual void setVList(x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > vList);
    virtual void createVList(x10_int ws);
    virtual void createVListPeriodic(x10_int ws);
    virtual x10aux::ref<x10::lang::String> toString(
      );
    virtual x10aux::ref<au::edu::anu::mm::FmmBox>
      au__edu__anu__mm__FmmBox____au__edu__anu__mm__FmmBox__this(
      );
    void __fieldInitializers39675();
    
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
#endif // AU_EDU_ANU_MM_FMMBOX_H

namespace au { namespace edu { namespace anu { namespace mm { 
class FmmBox;
} } } } 

#ifndef AU_EDU_ANU_MM_FMMBOX_H_NODEPS
#define AU_EDU_ANU_MM_FMMBOX_H_NODEPS
#ifndef AU_EDU_ANU_MM_FMMBOX_H_GENERICS
#define AU_EDU_ANU_MM_FMMBOX_H_GENERICS
inline x10_int au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::FmmBox::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_FMMBOX_H_GENERICS
#endif // __AU_EDU_ANU_MM_FMMBOX_H_NODEPS
