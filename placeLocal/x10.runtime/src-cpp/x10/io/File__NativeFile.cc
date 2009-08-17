#include <x10aux/config.h>

#include <x10/io/File__NativeFile.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

x10aux::ref<File__NativeFile>
File__NativeFile::_make(x10aux::ref<x10::lang::String> s) {
    return (new (x10aux::alloc<File__NativeFile>()) File__NativeFile())->_constructor(s);
}

RTT_CC_DECLS1(File__NativeFile, "x10.io.File.NativeFile", Ref)

// vim:tabstop=4:shiftwidth=4:expandtab
