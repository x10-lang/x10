/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#include <x10aux/reference_logger.h>
#include <x10aux/lock.h>
#include <x10aux/alloc.h>

using namespace x10aux;

#if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)

#define NUM_BUCKETS 4096
#define ADDR_SHIFT 7

ReferenceLogger *x10aux::ReferenceLogger::it;

ReferenceLogger::ReferenceLogger() {
    _lock = new (alloc<reentrant_lock>())reentrant_lock();
    _buckets = x10aux::alloc_z<Bucket*>(NUM_BUCKETS*sizeof(Bucket*));
}

ReferenceLogger* ReferenceLogger::initMe() {
    return new (x10aux::alloc<ReferenceLogger>()) ReferenceLogger();
}

#define REPORT_POP_COUNT 0

#if REPORT_POP_COUNT
static int count = 0;
#endif

void ReferenceLogger::log_(void *x) {

    // Critical section guarded by lock:
    // Lookup x in hashmap.
    //   If found; done.
    //   If not found, allocate a new bucket to contain x and add it.
    _lock->lock();

#if REPORT_POP_COUNT
    count += 1;
    if (count % 1000 == 0) {
        printf("Referenced logger count is %d\n", count);
    }
#endif
    
    // Hash function: throw out low bits and mod by NUM_BUCKETS.
    int bucket = (((size_t)(x)) >> ADDR_SHIFT) % NUM_BUCKETS;
    Bucket *cur = _buckets[bucket];
    while (cur != NULL) {
        if (cur->_reference == x) {
            _S_("RefLogger: "<<x<<" was already recorded as a globally escaped reference");
            _lock->unlock();
            return;
        }
        cur = cur->_next;
    }
    Bucket *newBucket = x10aux::alloc<Bucket>();
    newBucket->_reference = x;
    newBucket->_next = _buckets[bucket];
    _buckets[bucket] = newBucket;
    
    _S_("RefLogger: recording "<<x<<" as a new globally escaped reference");
    _lock->unlock();
}

void ReferenceLogger::forget_(void *x) {

    // Critical section guarded by lock:
    // Lookup x in hashmap.
    //   If found; remove it.
    _lock->lock();

    // Hash function: throw out low bits and mod by NUM_BUCKETS.
    int bucket = (((size_t)(x)) >> ADDR_SHIFT) % NUM_BUCKETS;
    Bucket *cur = _buckets[bucket];
    Bucket *prev = NULL;
    while (cur != NULL) {
        if (cur->_reference == x) {
            _S_("RefLogger: "<<x<<" was logged; now forgetting");
            if (NULL == prev) {
                _buckets[bucket] = cur->_next;
            } else {
                prev->_next = cur->_next;
            }
#if REPORT_POP_COUNT
            count -= 1;
            if (count % 1000 == 0) {
                printf("Referenced logger count is %d\n", count);
            }
#endif
            _lock->unlock();
            return;
        }
        prev = cur;
        cur = cur->_next;
    }
    
    _S_("RefLogger: "<<x<<" was not logged");
    _lock->unlock();
}
#endif /* X10_USE_BDWGC */
