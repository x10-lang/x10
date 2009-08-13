#include <x10/io/FileWriter__FileOutputStream.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

x10aux::ref<FileWriter__FileOutputStream> FileWriter__FileOutputStream::STANDARD_OUT
    = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(stdout);

x10aux::ref<FileWriter__FileOutputStream> FileWriter__FileOutputStream::STANDARD_ERR
    = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(stderr);

x10aux::ref<FileWriter__FileOutputStream>
FileWriter__FileOutputStream::_make(x10aux::ref<x10::lang::String> name) {
    return new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream (x10aux::io::FILEPtrStream::open_file(name, "w"));
}

RTT_CC_DECLS1(FileWriter__FileOutputStream, "x10.io.FileWriter.FileOutputStream", OutputStreamWriter__OutputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
