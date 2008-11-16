#include <x10aux/config.h>

#include <x10/io/FileNotFoundException.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

const FileNotFoundException::RTT * const FileNotFoundException::RTT::it =
    new FileNotFoundException::RTT();
