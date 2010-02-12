#include <x10aux/config.h>

#include <x10aux/rail_utils.h>
#include <x10aux/throw.h>

#include <x10/lang/String.h>

#include <x10/lang/ArrayIndexOutOfBoundsException.h>

using namespace x10::lang;
using namespace x10aux;

ref<String> x10aux::emptyRailToString = String::Lit("[]");

#ifndef NO_IOSTREAM
std::stringstream* x10aux::allocStringStream() {
    return new (x10aux::alloc<std::stringstream>()) std::stringstream();
}

void x10aux::freeStringStream (std::stringstream *ss) {
    x10aux::dealloc<std::stringstream>(ss);
}

void x10aux::railToStringProcessElement(std::stringstream *ss, int i, ref<x10::lang::String> elemStr) {
    if (i == 0) {
        (*ss) << "[";
    } else {
        (*ss) << ",";
    }
    (*ss) << elemStr;
}

ref<x10::lang::String> x10aux::railToStringFinalize(std::stringstream *ss) {
    (*ss) << "]";
    ref<x10::lang::String> result = x10::lang::String::Lit(ss->str().c_str());
    freeStringStream(ss);
    return result;
}
#endif /* NO_IOSTREAM */

void x10aux::throwArrayIndexOutOfBoundsException(x10_int index, x10_int length) {
#ifndef NO_EXCEPTIONS
    char *msg = alloc_printf("%ld not in [0,%ld)", (long)index, (long)length);
    throwException(x10::lang::ArrayIndexOutOfBoundsException::_make(String::Lit(msg)));
#endif
}
// vim:tabstop=4:shiftwidth=4:expandtab
