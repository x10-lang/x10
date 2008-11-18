#include <x10/io/FileOutputStream.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

x10aux::ref<FileOutputStream> FileOutputStream::STANDARD_OUT
    = new FileOutputStream(stdout);

x10aux::ref<FileOutputStream> FileOutputStream::STANDARD_ERR
    = new FileOutputStream(stderr);


DEFINE_RTT(FileOutputStream);
