#include <x10aux/config.h>

#include <x10aux/rail_utils.h>

void x10aux::_check_bounds(x10_int index, x10_int length) {
    (void)index;
    (void)length;
    #ifndef NO_BOUNDS_CHECKS
    #ifndef NO_EXCEPTIONS
    if (index<0 || index>length) {
        std::stringstream msg;
        msg<<index<<" not in [0,"<<length<<")";
        x10::lang::String msg_str(msg.str());
        typedef x10::lang::ArrayIndexOutOfBoundsException Err;
        throwException(new (alloc<Err>()) Err(msg_str));
    }
    #endif
    #endif
}

// vim:tabstop=4:shiftwidth=4:expandtab
