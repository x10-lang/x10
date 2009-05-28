#include <x10aux/config.h>

#include <x10aux/alloc.h>

#include <x10/io/NativeInputStream.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

x10_int NativeInputStream::read(ref<Rail<x10_byte> > b) {
    return this->read(b, 0, b->x10__length);
}

x10_int NativeInputStream::read(ref<Rail<x10_byte> > b,
                          x10_int off, x10_int len) {
    x10_int val;
    x10_int i;
    for (i = 0; i < len && (val = this->read()) != -1; i++)
        b->operator[](off + i) = (x10_byte) (val & 0xFF);
    return i;
}

x10_boolean NativeInputStream::_struct_equals(ref<Object> p0) {
    if (p0.get() == this) return true; // short-circuit trivial equality
    if (!this->Value::_struct_equals(p0))
        return false;
//    ref<NativeInputStream> that = (ref<NativeInputStream>) p0;
//    if (!struct_equals(this->FMGL(stream), that->FMGL(stream)))
//        return false;
    return true;
}


RTT_CC_DECLS1(NativeInputStream, "x10.io.InputStreamReader.InputStream", Value)

// vim:tabstop=4:shiftwidth=4:expandtab
