#ifndef __AU_EDU_ANU_UTIL_TIMER_H
#define __AU_EDU_ANU_UTIL_TIMER_H

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
namespace x10 { namespace lang { 
class Long;
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
namespace x10 { namespace util { 
template<class TPMGL(T)> class IndexedMemoryChunk;
} } 
namespace x10 { namespace util { 
class Timer;
} } 
namespace au { namespace edu { namespace anu { namespace util { 

class Timer : public x10::lang::Object   {
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
    x10aux::ref<x10::array::Array<x10_long> > FMGL(total);
    
    x10aux::ref<x10::array::Array<x10_long> > FMGL(count);
    
    void _constructor(x10_int n);
    
    static x10aux::ref<au::edu::anu::util::Timer> _make(x10_int n);
    
    void _constructor(x10_int n, x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    static x10aux::ref<au::edu::anu::util::Timer> _make(x10_int n, x10aux::ref<x10::util::concurrent::OrderedLock> paramLock);
    
    virtual void start(x10_int id);
    virtual void clear(x10_int id);
    virtual void stop(x10_int id);
    virtual x10_long mean(x10_int id);
    virtual x10aux::ref<au::edu::anu::util::Timer> au__edu__anu__util__Timer____au__edu__anu__util__Timer__this(
      );
    void __fieldInitializers31521();
    
    // Serialization
    public: static const x10aux::serialization_id_t _serialization_id;
    
    public: x10aux::serialization_id_t _get_serialization_id() {
         return _serialization_id;
    }
    
    public: virtual void _serialize_body(x10aux::serialization_buffer& buf);
    
    public: static x10aux::ref<x10::lang::Reference> _deserializer(x10aux::deserialization_buffer& buf);
    
    public: void _deserialize_body(x10aux::deserialization_buffer& buf);
    
};

} } } } 
#endif // AU_EDU_ANU_UTIL_TIMER_H

namespace au { namespace edu { namespace anu { namespace util { 
class Timer;
} } } } 

#ifndef AU_EDU_ANU_UTIL_TIMER_H_NODEPS
#define AU_EDU_ANU_UTIL_TIMER_H_NODEPS
#ifndef AU_EDU_ANU_UTIL_TIMER_H_GENERICS
#define AU_EDU_ANU_UTIL_TIMER_H_GENERICS
inline x10_int au::edu::anu::util::Timer::FMGL(X10__class_lock_id1__get)() {
    if (FMGL(X10__class_lock_id1__status) != x10aux::INITIALIZED) {
        FMGL(X10__class_lock_id1__init)();
    }
    return au::edu::anu::util::Timer::FMGL(X10__class_lock_id1);
}

#endif // AU_EDU_ANU_UTIL_TIMER_H_GENERICS
#endif // __AU_EDU_ANU_UTIL_TIMER_H_NODEPS
