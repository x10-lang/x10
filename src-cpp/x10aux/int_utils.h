#ifndef X10AUX_INT_UTILS_H
#define X10AUX_INT_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    /* java.lang.Integer.parseInt */
	x10_int int_parseInt(const ref<x10::lang::String>& s);
}

#endif
