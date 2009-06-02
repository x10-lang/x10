#include <x10aux/config.h>

#include <x10aux/serialization.h>

using namespace x10aux;
using namespace x10::lang;

void
addr_map::_grow() {
    _ptrs = (const void**) ::memcpy(new (x10aux::alloc<const void*>((_size<<1)*sizeof(const void*))) const void*[_size<<1], _ptrs, _size*sizeof(const void*));
    _size <<= 1;
}

char *
serialization_buffer::grow() {
    assert (limit != NULL);
    char* saved_buf = buffer;
    size_t length = cursor - buffer;
    size_t allocated = limit - buffer;
    float grow_factor = GROW_PERCENT / 100.0;
    size_t new_size = (size_t) (allocated * grow_factor);
    buffer = alloc(new_size);
    ::memcpy(buffer, saved_buf, length);
    limit = buffer + new_size;
    cursor = buffer + length;
    dealloc(saved_buf);
    return buffer;
}

