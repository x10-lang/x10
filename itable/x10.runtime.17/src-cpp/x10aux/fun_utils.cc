#include <x10aux/fun_utils.h>
#include <x10aux/serialization.h>
#include <x10/lang/Object.h>

void
x10aux::AnyFun::_serialize(ref<AnyFun> this_,
                           x10aux::serialization_buffer &buf,
                           x10aux::addr_map &m) {
    x10::lang::Object::_serialize(this_, buf, m);
}
