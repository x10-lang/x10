#include <x10aux/reference_logger.h>

#include <x10/runtime/Lock.h>

using namespace x10::lang;
using namespace x10::runtime;
using namespace x10aux;

#if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)
ReferenceLogger *x10aux::ReferenceLogger::it;

#define SPINE_SIZE 1024
#define BUCKET_LOG_SIZE 4
#define BUCKET_SIZE (1<<BUCKET_LOG_SIZE)
#define BUCKET_MASK ((1<<BUCKET_LOG_SIZE)-1)

ReferenceLogger::ReferenceLogger() {
    escapedReferences = x10aux::alloc<void**>(SPINE_SIZE*sizeof(void**));
    nextSlot = 0;
    lock = Lock::_make();
}

void ReferenceLogger::log_(void *x) {

    // Critical section guarded by lock:
    //   (1) Get a slot assigned to log this reference
    //   (2) If that reference would be the first one in a bucket, allocate the backing bucket.
    lock->lock();
    int mySlot = nextSlot++;
    int spineIndex = mySlot >> BUCKET_LOG_SIZE;
    int bucketIndex = mySlot & BUCKET_MASK;
    if (0 == bucketIndex) {
        assert(spineIndex<SPINE_SIZE); /* TODO: We've logged so many references that we're out of space to log them */
        escapedReferences[spineIndex] = x10aux::alloc<void*>(BUCKET_SIZE*sizeof(void*));
    }
    lock->unlock();

    // Actually log the reference in its assigned slot.  Don't have to hold the lock to do this work.
    escapedReferences[spineIndex][bucketIndex] = x;
}
#endif /* X10_USE_BDWGC */
