#ifndef X10AUX_ASYNC_POLL_H
#define X10AUX_ASYNC_POLL_H

#include <x10aux/config.h>

#include <x10/x10.h> // pgas

namespace x10aux {
    inline void async_poll() {
        x10_probe();
    }
}

#endif

// vim:tabstop=4:shiftwidth=4:expandtab
