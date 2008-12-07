#include <x10aux/config.h>

#include <x10aux/rail_utils.h>
#include <x10aux/throw.h>

#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

void x10aux::_check_bounds(x10_int index, x10_int length) {
    (void)index;
    (void)length;
    #ifndef NO_BOUNDS_CHECKS
    #ifndef NO_EXCEPTIONS
    if (index < 0 || index > length) {
        std::stringstream msg;
        msg << index << " not in [0," << length << ")";
        typedef x10::lang::ArrayIndexOutOfBoundsException Err;
        throwException(Err::_make(String::Lit(msg.str().c_str())));
    }
    #endif
    #endif
}

// vim:tabstop=4:shiftwidth=4:expandtab
