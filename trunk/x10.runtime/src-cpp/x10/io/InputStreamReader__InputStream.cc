#include <x10aux/config.h>

#include <x10aux/alloc.h>

#include <x10/io/InputStreamReader__InputStream.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

x10_int InputStreamReader__InputStream::read(ref<Rail<x10_byte> > b) {
    nullCheck(b);
    placeCheck(b);
    return this->read(b, 0, b->x10__length);
}

x10_int InputStreamReader__InputStream::read(ref<Rail<x10_byte> > b,
                                             x10_int off, x10_int len) {
    x10_int val;
    x10_int i;
    placeCheck(nullCheck(b));  // Strictly speaking this should be inside the loop, but X10 has looser exception semantics than Java, so lift it out of loop.
    for (i = 0; i < len && (val = this->read()) != -1; i++)
        b->operator[](off + i) = (x10_byte) (val & 0xFF);
    return i;
}

void InputStreamReader__InputStream::_serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    x10::lang::Ref::_serialize_body(buf, m);
}

void InputStreamReader__InputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Ref::_deserialize_body(buf);
}

RTT_CC_DECLS1(InputStreamReader__InputStream, "x10.io.InputStreamReader.InputStream", Ref)

// vim:tabstop=4:shiftwidth=4:expandtab
