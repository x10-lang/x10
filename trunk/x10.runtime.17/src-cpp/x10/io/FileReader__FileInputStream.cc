#include <x10/io/FileReader__FileInputStream.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

x10aux::ref<FileReader__FileInputStream> FileReader__FileInputStream::STANDARD_IN
    = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream(stdin);

x10aux::ref<FileReader__FileInputStream>
FileReader__FileInputStream::_make(x10aux::ref<x10::lang::String> name) {
    return new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream (x10aux::io::FILEPtrStream::open_file(name, "r"));
}

RTT_CC_DECLS1(FileReader__FileInputStream, "x10.io.FileReader.FileReader__FileInputStream", InputStreamReader__InputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
