/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#include <assert.h>

#include <x10aux/place_local.h>
#include <x10aux/lock.h>
#include <x10aux/basic_functions.h>

using namespace x10::lang;
using namespace x10aux;

#define MAX_FAST_ID 255
#define NUM_BUCKETS 100

x10_int place_local::_nextId;
place_local::Bucket **place_local::_buckets;
void **place_local::_fastData;
x10aux::reentrant_lock* place_local::_lock;

void place_local::initialize() {
    _nextId = 0;
    _lock = new (alloc<reentrant_lock>())reentrant_lock();

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

void* place_local::get(x10_int id) {
    if (id < MAX_FAST_ID) {
        return _fastData[id];
    } else {
        _lock->lock();
        int bucket = hash_code(id) % NUM_BUCKETS;
        Bucket *cur = _buckets[bucket];
        while (cur != NULL) {
            if (cur->_id == id) {
                void* ans = cur->_data;
                _lock->unlock();
                return ans;
            }
            cur = cur->_next;
        }
        _lock->unlock();
        // didn't find it, which means we return NULL (match Java Map semantics)
        return NULL;
    }
}

void place_local::put(x10_int id, void *data) {
    if (id < MAX_FAST_ID) {
        _fastData[id] = data;
    } else {
        if (NULL == data) {
            // optimize storing NULL as absence of the key (match Java Map semantics)
            remove(id);
        } else {
            int bucket = hash_code(id) % NUM_BUCKETS;
            // First, search to see if we are replacing an exisiting key
            Bucket *cur = _buckets[bucket];
            while (cur != NULL) {
                if (cur->_id == id) {
                    // Replace
                    cur->_data = data;
                    _lock->unlock();
                    return;
                }
                cur = cur->_next;
            }
            // Didn't find it.  Insert at head of bucket chain
            Bucket *newBucket = alloc<Bucket>();
            newBucket->_id = id;
            newBucket->_data = data;
            newBucket->_next = _buckets[bucket];
            _buckets[bucket] = newBucket;
            _lock->unlock();
        }
    }
}

void place_local::remove(x10_int id) {
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
                x10aux::dealloc(cur);
                return;
            }
            trailer = &(cur->_next);
            cur = cur->_next;
        }
        // wasn't here in the first place,  since we optimize storing NULL
        // by not storing anything, this isn't an error.
        _lock->unlock();
    }
}
