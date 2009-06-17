#ifndef X10AUX_BOOLEAN_UTILS_H
#define X10AUX_BOOLEAN_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}


namespace x10aux {
    class boolean_utils {
    public:
        static const ref<x10::lang::String> toString(x10_boolean value);
        static x10_boolean parseBoolean(const ref<x10::lang::String>& s);

    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
