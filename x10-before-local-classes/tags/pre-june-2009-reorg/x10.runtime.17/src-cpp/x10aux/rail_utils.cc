#include <x10aux/config.h>

#include <x10aux/rail_utils.h>
#include <x10aux/throw.h>

#include <x10/lang/String.h>

#include <x10/lang/ArrayIndexOutOfBoundsException.h>

using namespace x10::lang;
using namespace x10aux;

void x10aux::throwArrayIndexOutOfBoundsException(x10_int index, x10_int length) {
#ifndef NO_EXCEPTIONS
    char *msg = alloc_printf("%d not in [0,%d)", index, length);
    throwException(x10::lang::ArrayIndexOutOfBoundsException::_make(String::Lit(msg)));
#endif
}
// vim:tabstop=4:shiftwidth=4:expandtab
