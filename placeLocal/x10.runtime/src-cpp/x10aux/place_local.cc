#include <x10aux/alloc.h>
#include <assert.h>

#include <x10aux/place_local.h>

using namespace x10aux;

volatile x10_int x10aux::place_local::_nextId = 1;
x10_int x10aux::place_local::_tableSize = 100;
void** x10aux::place_local::_handles = NULL;


x10_int x10aux::place_local::nextId() {
    assert(x10aux::here == 0);
    // TODO: put a lock around this.
    x10_int id = _nextId++;
    assert(id < _tableSize); // TODO: dynamically grow table
    return id;
}

void* x10aux::place_local::lookupHandle(x10_int id) {
    if (NULL == _handles) {
        _handles = x10aux::alloc<void*>(100);
        memset(_handles, 0, 100*sizeof(void*));
    }
    // TODO: all sorts of error handling
    assert(id > 0 && id < _tableSize);
    return _handles[id];
}
            
void x10aux::place_local::registerHandle(x10_int id, void *data) {
    if (NULL == _handles) {
        _handles = x10aux::alloc<void*>(100);
        memset(_handles, 0, 100*sizeof(void*));
    }
    // TODO: all sorts of error handling
    assert(id > 0 && id < _tableSize);
    assert(NULL == _handles[id]);
    _handles[id] = data;
}
