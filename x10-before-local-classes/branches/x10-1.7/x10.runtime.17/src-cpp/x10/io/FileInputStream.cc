#include <x10/io/FileInputStream.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

x10aux::ref<FileInputStream> FileInputStream::STANDARD_IN
    = new (x10aux::alloc<FileInputStream>()) FileInputStream(stdin);

x10aux::ref<FileInputStream>
FileInputStream::_make(x10aux::ref<x10::lang::String> name) {
    return new (x10aux::alloc<FileInputStream>()) FileInputStream (FILEPtrStream::open_file(name, "r"));
}

RTT_CC_DECLS1(FileInputStream, "x10.io.FileReader.FileInputStream", NativeInputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
