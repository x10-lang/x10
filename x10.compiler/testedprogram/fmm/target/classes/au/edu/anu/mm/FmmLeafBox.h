#ifndef __AU_EDU_ANU_MM_FMMLEAFBOX_H
#define __AU_EDU_ANU_MM_FMMLEAFBOX_H

#include <x10rt.h>


#define AU_EDU_ANU_MM_FMMBOX_H_NODEPS
#include <au/edu/anu/mm/FmmBox.h>
#undef AU_EDU_ANU_MM_FMMBOX_H_NODEPS
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
namespace x10 { namespace util { 
template<class TPMGL(T)> class ArrayList;
} } 
namespace au { namespace edu { namespace anu { namespace chem { 
class PointCharge;
} } } } 
namespace x10 { namespace array { 
template<class TPMGL(T)> class Array;
} } 
namespace x10 { namespace array { 
class Point;
} } 
namespace x10 { namespace lang { 
template<class TPMGL(T)> class GlobalRef;
} } 
namespace x10 { namespace lang { 
class Throwable;
} } 
namespace x10 { namespace lang { 
class Runtime;
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

class FmmLeafBox : public au::edu::anu::mm::FmmBox   {
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
    x10aux::ref<x10::util::ArrayList<au::edu::anu::chem::PointCharge> > FMGL(atoms);
    
    x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > FMGL(uList);
    
    void _constructor(x10_int level, x10_int x, x10_int y, x10_int z, x10_int numTerms,
                      x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent);
    
    static x10aux::ref<au::edu::anu::mm::FmmLeafBox> _make(x10_int level,
                                                           x10_int x,
                                                           x10_int y,
                                                           x10_int z,
                                                           x10_int numTerms,
                                                           x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent);
    
    void _constructor(x10_int level, x10_int x, x10_int y, x10_int z,
                      x10_int numTerms,
                      x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent,
                      x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::mm::FmmLeafBox> _make(
             x10_int level,
             x10_int x,
             x10_int y,
             x10_int z,
             x10_int numTerms,
             x10::lang::GlobalRef<x10aux::ref<au::edu::anu::mm::FmmBox> > parent,
             x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual void addAtom(au::edu::anu::chem::PointCharge atom);
    virtual x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > >
      getUList(
      );
    virtual void setUList(x10aux::ref<x10::array::Array<x10aux::ref<x10::array::Point> > > uList);
    virtual void createUList(x10_int ws);
    virtual void createUListPeriodic(x10_int ws);
    virtual x10aux::ref<au::edu::anu::mm::FmmLeafBox>
      au__edu__anu__mm__FmmLeafBox____au__edu__anu__mm__FmmLeafBox__this(
      );
    void __fieldInitializers39455();
    
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
#endif // AU_EDU_ANU_MM_FMMLEAFBOX_H

namespace au { namespace edu { namespace anu { namespace mm { 
class FmmLeafBox;
} } } } 

#ifndef AU_EDU_ANU_MM_FMMLEAFBOX_H_NODEPS
#define AU_EDU_ANU_MM_FMMLEAFBOX_H_NODEPS
#ifndef AU_EDU_ANU_MM_FMMLEAFBOX_H_GENERICS
#define AU_EDU_ANU_MM_FMMLEAFBOX_H_GENERICS
inline x10_int au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::mm::FmmLeafBox::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_MM_FMMLEAFBOX_H_GENERICS
#endif // __AU_EDU_ANU_MM_FMMLEAFBOX_H_NODEPS
