#include <x10aux/config.h>

#include <x10/io/IOException.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

const IOException::RTT * const IOException::RTT::it =
    new IOException::RTT();
