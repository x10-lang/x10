#ifndef X10AUX_RUNTIME_H
#define X10AUX_RUNTIME_H

#include <x10aux/place.h>

namespace x10aux {

    void init();
    void shutdown();

    class __init__ {
        static int count;
    public:
        __init__() {
            if (count++ == 0) {
                init();
            }
            __here__ = x10aux::here();
        }

        ~__init__() {
            if (--count == 0) {
                shutdown();
            }
        }

        static void init() {
            x10aux::init();
            //x10::lang::System::__init__in_out_err();
            x10aux::place::__init__MAX_PLACES();
        }

        static void shutdown() {
            x10aux::shutdown();
        }
    };

    static __init__ __init__counter;

}

#endif
