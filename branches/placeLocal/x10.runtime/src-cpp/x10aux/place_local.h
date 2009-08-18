#ifndef X10AUX_PLACELOCAL_H
#define X10AUX_PLACELOCAL_H

#include <x10aux/config.h>

namespace x10aux {

    class place_local {
    private:
        static volatile x10_int _nextId;
        static int _tableSize;
        static void** _handles;

    public:
        static x10_int nextId();
        static void* lookupHandle(x10_int id);
        static void registerHandle(x10_int id, void *data);
    };
}

#endif


