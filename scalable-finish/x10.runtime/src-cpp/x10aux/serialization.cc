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

#include <x10aux/config.h>

#include <x10aux/serialization.h>
#include <x10aux/network.h>

using namespace x10aux;
using namespace x10::lang;

void addr_map::_grow() {
    _ptrs = (const void**) ::memcpy(new (x10aux::alloc<const void*>((_size<<1)*sizeof(const void*))) const void*[_size<<1], _ptrs, _size*sizeof(const void*));
    _size <<= 1;
}

void addr_map::_add(const void* ptr) {
    if (_top == _size) {
        _grow();
    }
    _ptrs[_top++] = ptr;
}

int addr_map::_find(const void* ptr) {
    for (int i = -1; i >= -_top; i--) {
        if (_ptrs[_top+i] == ptr) {
            return i;
        }
    }
    return 0;
}

const void* addr_map::_get(int pos) {
    if (pos < -_top || pos >= 0)
        return NULL;
    return _ptrs[_top+pos];
}

const void* addr_map::_set(int pos, const void* ptr) {
    if (pos < -_top || pos >= 0)
        return NULL;
    const void* old = _ptrs[_top+pos];
    _ptrs[_top+pos] = ptr;
    return old;
}

int addr_map::_position(const void* p) {
    int pos = _find(p);
    if (pos != 0) {
        return pos;
    }
    _add(p);
    return 0;
}

serialization_buffer::serialization_buffer (void)
    // do not use GC
    : realloc_func(x10aux::msg_realloc), buffer(NULL), limit(NULL), cursor(NULL), map()
{ }

void serialization_buffer::grow (void) {
    size_t new_length = length(); // no change in used portion of buffer
    size_t old_capacity = capacity();
    size_t new_capacity = (size_t) (old_capacity * 2.0); // increase capacity by a factor
    if (new_capacity<16) new_capacity = 16; // biggest primitive we might serialise -- a SIMD variable
    
    // do not use GC
    buffer = (char*)realloc_func(buffer, old_capacity, new_capacity);

    // update pointers to use (potentially) new buffer
    limit = buffer + new_capacity;
    cursor = buffer + new_length;
}
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100

