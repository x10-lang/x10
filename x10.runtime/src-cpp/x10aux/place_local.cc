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

#include <assert.h>

#include <x10aux/place_local.h>
#include <x10aux/lock.h>

using namespace x10::lang;
using namespace x10aux;

x10_int place_local::_nextId;
simple_hashmap<x10_long, void*>* place_local::_map;
x10aux::reentrant_lock* place_local::_lock;

// used by simple_hashmap
x10_uint x10aux::simple_hash_code(x10_long id) {
    return (x10_uint)x10aux::hash_code(id);
}

void place_local::initialize() {
    _nextId = 0;
    _lock = new (alloc<reentrant_lock>())reentrant_lock();

    _map = new (x10aux::alloc< x10aux::simple_hashmap<x10_long, void*> >()) x10aux::simple_hashmap<x10_long, void*>();
}

x10_long place_local::nextId() {
    _lock->lock();
    x10_int id = _nextId++;
    _lock->unlock();
    x10_long plhId = (((x10_long)here) << 32) | id;
    return plhId;
}

void* place_local::get(x10_long id) {
    _lock->lock();
    void *data = _map->get(id);
    _lock->unlock();
    return data;
}

void place_local::put(x10_long id, void *data) {
    _lock->lock();
    if (NULL == data) {
        // optimize storing NULL as absence of the key (match Java Map semantics)
        remove(id);
    } else {
        _map->put(id, data);
    }
    _lock->unlock();
}

void place_local::remove(x10_long id) {
    _lock->lock();
    _map->remove(id);
    _lock->unlock();
}
