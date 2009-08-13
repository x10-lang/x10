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

x10_boolean OutputStreamWriter__OutputStream::_struct_equals(ref<Object> p0) {
    if (p0.operator->() == this) return true; // short-circuit trivial equality
    if (!this->Value::_struct_equals(p0))
        return false;
//    ref<OutputStreamWriter__OutputStream> that = (ref<OutputStreamWriter__OutputStream>) p0;
//    if (!struct_equals(this->FMGL(stream), that->FMGL(stream)))
//        return false;
    return true;
}

/*
void OutputStreamWriter__OutputStream::_printf(const char* format, ...) {
    va_list parms;
    va_start(parms, format);
    _vprintf(format, parms);
    va_end(parms);
}

void OutputStreamWriter__OutputStream::printf(const ref<String>& format) {
    this->_printf("%s", ((const string&)(*format)).c_str());
    this->flush(); // FIXME [IP] temp
}
*/

RTT_CC_DECLS1(OutputStreamWriter__OutputStream, "x10.io.OutputStreamWriter.OutputStream", Value)

// vim:tabstop=4:shiftwidth=4:expandtab
