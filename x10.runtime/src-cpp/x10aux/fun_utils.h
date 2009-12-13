#ifndef X10AUX_FUN_UTILS_H
#define X10AUX_FUN_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10/lang/Object.h>

namespace x10aux {
    class serialization_buffer;

    class AnyFun {
    public:

        static void _serialize(ref<AnyFun> this_,
                               x10aux::serialization_buffer &buf) {
            x10::lang::Object::_serialize(this_, buf);
        }

        template<class T> static x10aux::ref<T> _deserialize(x10aux::deserialization_buffer &buf) {
            return x10::lang::Object::_deserialize<T>(buf);
        }
    };
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
