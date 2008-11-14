#include <x10aux/config.h>

#include <x10aux/alloc.h>

#include <x10/io/NativeOutputStream.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;


void NativeOutputStream::write(const ref<Rail<x10_byte> >& b) {
    this->write(b, 0, b->x10__length);
}

void NativeOutputStream::write(const ref<Rail<x10_byte> >& b,
                               x10_int off, x10_int len) {
    for (x10_int i = 0; i < len; i++)
        this->write((x10_int) b->operator[](off + i));
}

/*
void NativeOutputStream::_printf(const char* format, ...) {
    va_list parms;
    va_start(parms, format);
    _vprintf(format, parms);
    va_end(parms);
}

void NativeOutputStream::printf(const ref<String>& format) {
    this->_printf("%s", ((const string&)(*format)).c_str());
    this->flush(); // FIXME [IP] temp
}
*/



const NativeOutputStream::RTT * const NativeOutputStream::RTT::it =
    new NativeOutputStream::RTT();

