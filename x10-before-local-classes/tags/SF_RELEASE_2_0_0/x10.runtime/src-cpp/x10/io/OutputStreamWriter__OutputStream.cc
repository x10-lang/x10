#include <x10aux/config.h>

#include <x10aux/alloc.h>

#include <x10/io/OutputStreamWriter__OutputStream.h>
#include <x10/lang/ValRail.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;


void OutputStreamWriter__OutputStream::write(ref<Rail<x10_byte> > b) {
    placeCheck(nullCheck(b));
    this->write(b, 0, b->x10__length);
}

void OutputStreamWriter__OutputStream::write(ref<Rail<x10_byte> > b,
                                             x10_int off, x10_int len) {
    if (len > 0) { placeCheck(nullCheck(b)); }
    for (x10_int i = 0; i < len; i++)
        this->write((x10_int) b->operator[](off + i));
}

void OutputStreamWriter__OutputStream::write(ref<ValRail<x10_byte> > b) {
    nullCheck(b);
    this->write(b, 0, b->x10__length);
}

void OutputStreamWriter__OutputStream::write(ref<ValRail<x10_byte> > b,
                                             x10_int off, x10_int len) {
    if (len > 0) nullCheck(b);
    for (x10_int i = 0; i < len; i++)
        this->write((x10_int) b->operator[](off + i));
}

void OutputStreamWriter__OutputStream::_serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    x10::lang::Ref::_serialize_body(buf, m);
}

void OutputStreamWriter__OutputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Ref::_deserialize_body(buf);
}

RTT_CC_DECLS1(OutputStreamWriter__OutputStream, "x10.io.OutputStreamWriter.OutputStream", Ref)

// vim:tabstop=4:shiftwidth=4:expandtab
