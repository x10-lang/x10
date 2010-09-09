#ifndef X10AUX_REFERENCE_LOGGER_H
#define X10AUX_REFERENCE_LOGGER_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace runtime {
        class Lock;
    }
}


namespace x10aux {

#if defined(X10_USE_BDWGC) || defined(X10_DEBUG_REFERENCE_LOGGER)
    /* References that have been shipped to remote Places,
     * and therefore must be treated as roots for local GCs
     */
    class ReferenceLogger {
        void*** escapedReferences;
        int nextSlot;
    public: 
        ReferenceLogger();
        void log_(void *x);
        x10aux::ref<x10::runtime::Lock> lock;
        static ReferenceLogger* it;
        static void log(void *x) {
            if (it==NULL)
                it = new (x10aux::alloc<ReferenceLogger>()) ReferenceLogger();
            it->log_(x);
        }
    };

#endif

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
