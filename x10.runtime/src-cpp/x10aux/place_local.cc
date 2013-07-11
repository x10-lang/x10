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

using namespace x10::lang;
using namespace x10aux;

#define MAX_FAST_ID 255

x10_int place_local::_nextId;
simple_hashmap<int, void*>* place_local::_map;
void **place_local::_fastData;
x10aux::reentrant_lock* place_local::_lock;

// used by simple_hashmap
x10_uint x10aux::simple_hash_code(int id) {
    return (x10_uint)id;
}

void place_local::initialize() {
    _nextId = 0;
    _lock = new (alloc<reentrant_lock>())reentrant_lock();

    _map = new (x10aux::alloc< x10aux::simple_hashmap<int, void*> >()) x10aux::simple_hashmap<int, void*>();
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
    void* data;
    _lock->lock();
    if (id < MAX_FAST_ID) {
        data = _fastData[id];
    } else {
        data = _map->get(id);
    }
    _lock->unlock();
    return data;
}

void place_local::put(x10_int id, void *data) {
    _lock->lock();
    if (id < MAX_FAST_ID) {
        _fastData[id] = data;
    } else {
        if (NULL == data) {
            // optimize storing NULL as absence of the key (match Java Map semantics)
            remove(id);
        } else {
            _map->put(id, data);
        }
    }
    _lock->unlock();
}

void place_local::remove(x10_int id) {
    _lock->lock();
    if (id < MAX_FAST_ID) {
        assert(NULL != _fastData[id]);
        _fastData[id] = NULL;
    } else {
        _map->remove(id);
    }
    _lock->unlock();
}
