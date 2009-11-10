#include <assert.h>

#include <x10aux/place_local.h>
#include <x10aux/basic_functions.h>
#include <x10/runtime/Lock.h>

using namespace x10::lang;
using namespace x10::runtime;
using namespace x10aux;

#define MAX_FAST_ID 255
#define NUM_BUCKETS 100

x10_int place_local::_nextId;
place_local::Bucket **place_local::_buckets;
void **place_local::_fastData;
x10aux::ref<x10::runtime::Lock> place_local::_lock;

void place_local::initialize() {
    _lock = Lock::_make();
    _nextId = 0;

    _buckets = alloc<Bucket*>(NUM_BUCKETS*sizeof(Bucket*));
    memset(_buckets, 0, NUM_BUCKETS*sizeof(Bucket*));
    _fastData = alloc<void*>((MAX_FAST_ID+1)*sizeof(void*));
    memset(_fastData, 0, (MAX_FAST_ID+1)*sizeof(void*));
}

x10_int place_local::nextId() {
    // hack to allow XRX to use PLS without distribution of statics
    assert(_nextId==0 || here==0);
    _lock->lock();
    x10_int id = _nextId++;
    _lock->unlock();
    return id;
}

void* place_local::lookupData(x10_int id) {
    if (id < MAX_FAST_ID) {
        return _fastData[id];
    } else {
        _lock->lock();
        int bucket = hash_code(id) % NUM_BUCKETS;
        Bucket *cur = _buckets[bucket];
        while (cur != NULL) {
            if (cur->_id == id) {
                _lock->unlock();
                return cur->_data;
            }
            cur = cur->_next;
        }
        _lock->unlock();
        return NULL;
    }
}
            
void place_local::registerData(x10_int id, void *data) {
    assert(NULL == lookupData(id));
    if (id < MAX_FAST_ID) {
        _fastData[id] = data;
    } else {
        _lock->lock();
        int bucket = hash_code(id) % NUM_BUCKETS;
        Bucket *newBucket = alloc<Bucket>();
        newBucket->_id = id;
        newBucket->_data = data;
        newBucket->_next = _buckets[bucket];
        _buckets[bucket] = newBucket;
        _lock->unlock();
    }
}

void place_local::unregisterData(x10_int id) {
    assert(NULL == lookupData(id));
    if (id < MAX_FAST_ID) {
        _fastData[id] = NULL;
    } else {
        _lock->lock();
        int bucket = hash_code(id) % NUM_BUCKETS;
        Bucket **trailer = &(_buckets[bucket]);
        Bucket *cur = _buckets[bucket];
        while (cur != NULL) {
            if (cur->_id == id) {
                // cut cur out of bucket chain by setting trailer to cur->next;
                *trailer = cur->_next;
                _lock->unlock();
                return;
            }
            trailer = &(cur->_next);
            cur = cur->_next;
        }
        // hmm, wasn't registered in the first place
        // probably an error...
        _lock->unlock();
        assert(false);
    }
}
