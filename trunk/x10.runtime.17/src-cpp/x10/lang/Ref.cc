#include <sstream>

#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10/lang/Ref.h>
#include <x10/lang/String.h>

#include <x10/runtime/Lock.h>

using namespace x10::lang;
using namespace x10::runtime;
using namespace x10aux;

#ifdef X10_USE_BDWGC
Ref::ReferenceLogger* Ref::refLogger = new (x10aux::alloc<Ref::ReferenceLogger>()) ReferenceLogger();

#define SPINE_SIZE 1024
#define BUCKET_LOG_SIZE 4
#define BUCKET_SIZE (1<<BUCKET_LOG_SIZE)
#define BUCKET_MASK ((1<<BUCKET_LOG_SIZE)-1)

Ref::ReferenceLogger::ReferenceLogger() {
    escapedReferences = (Ref***)(x10aux::alloc<Ref>(SPINE_SIZE));
    nextSlot = 0;
    lock = Lock::_make();
}

void Ref::ReferenceLogger::log(ref<Ref> x) {

    // Critical section guarded by lock:
    //   (1) Get a slot assigned to log this reference
    //   (2) If that reference would be the first one in a bucket, allocate the backing bucket.
    lock->lock();
    int mySlot = nextSlot++;
    int spineIndex = mySlot >> BUCKET_LOG_SIZE;
    int bucketIndex = mySlot & BUCKET_MASK;
    if (0 == bucketIndex) {
        assert(spineIndex<SPINE_SIZE); /* TODO: We've logged so many references that we're out of space to log them */
        escapedReferences[spineIndex] = (Ref**)(x10aux::alloc<Ref>(BUCKET_SIZE));
    }
    lock->unlock();

    // Actually log the reference in its assigned slot.  Don't have to hold the lock to do this work.
    escapedReferences[spineIndex][bucketIndex] = x.get();
}
#endif /* X10_USE_BDWGC */
    
x10_int x10::lang::Ref::hashCode() {
    return (x10_int) (int64_t)(void*)this;
}

x10aux::ref<x10::lang::String> x10::lang::Ref::toString() {
    std::stringstream ss;
    ss << this->_type()->name() << "@" << std::hex << (std::size_t)this;
    return String::Lit(ss.str().c_str());
}

const serialization_id_t Ref::serialization_id =
    DeserializationDispatcher::addDeserializer(Ref::_deserialize<Object>);

DEFINE_RTT(Ref);
// vim:tabstop=4:shiftwidth=4:expandtab
