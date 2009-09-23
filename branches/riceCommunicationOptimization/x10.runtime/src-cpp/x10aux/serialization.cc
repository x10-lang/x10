#include <x10aux/config.h>

#include <x10aux/serialization.h>
#include <x10aux/network.h>

using namespace x10aux;
using namespace x10::lang;

#if 0
NOT USED AT PRESENT
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

bool addr_map::_find(const void* ptr) {
    for (int i = 0; i < _top; i++) {
        if (_ptrs[i] == ptr) {
            return true;
        }
    }
    return false;
}

bool addr_map::ensure_unique(const void* p) {
    if (_find(p)) {
        return false;
    }
    _add(p);
    return true;
}
#endif

void serialization_buffer::grow (void) {
    size_t new_length = length(); // no change in used portion of buffer
    size_t old_capacity = capacity();
    size_t new_capacity = (size_t) (old_capacity * 2.0); // increase capacity by a factor
    if (new_capacity<16) new_capacity = 16; // biggest primitive we might serialise -- a SIMD variable
    
    // do not use GC
    buffer = (char*)x10aux::msg_realloc(buffer, old_capacity, new_capacity);

    // update pointers to use (potentially) new buffer
    limit = buffer + new_capacity;
    cursor = buffer + new_length;
}
