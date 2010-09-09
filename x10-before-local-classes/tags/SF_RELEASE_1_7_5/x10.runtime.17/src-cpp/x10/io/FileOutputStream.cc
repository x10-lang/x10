#include <x10/io/FileOutputStream.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

x10aux::ref<FileOutputStream> FileOutputStream::STANDARD_OUT
    = new (x10aux::alloc<FileOutputStream>()) FileOutputStream(stdout);

x10aux::ref<FileOutputStream> FileOutputStream::STANDARD_ERR
    = new (x10aux::alloc<FileOutputStream>()) FileOutputStream(stderr);

x10aux::ref<FileOutputStream>
FileOutputStream::_make(x10aux::ref<x10::lang::String> name) {
    return new (x10aux::alloc<FileOutputStream>()) FileOutputStream (FILEPtrStream::open_file(name, "w"));
}

RTT_CC_DECLS1(FileOutputStream, "x10.io.FileWriter.FileOutputStream", NativeOutputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
