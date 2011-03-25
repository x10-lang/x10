#include <x10/util/RemoteIndexedMemoryChunk.h>
#include <x10/lang/Place.h>

x10aux::RuntimeType x10::util::RemoteIndexedMemoryChunk<void>::rtt;

x10::lang::Place x10::util::RIMC_here_hack() {
    return x10::lang::Place::_make(x10aux::here);
}
