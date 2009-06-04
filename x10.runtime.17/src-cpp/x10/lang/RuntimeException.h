#ifndef X10_LANG_RUNTIMEEXCEPTION_H
#define X10_LANG_RUNTIMEEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Exception.h>

namespace x10 {

    namespace lang {

        class RuntimeException : public Exception {
        public:
            RTT_H_DECLS;

            static x10aux::ref<RuntimeException> _make();
            static x10aux::ref<RuntimeException> _make(x10aux::ref<String> message);
            static x10aux::ref<RuntimeException> _make(x10aux::ref<Throwable> cause);
            static x10aux::ref<RuntimeException> _make(x10aux::ref<String> message,
                                                       x10aux::ref<Throwable> cause);

            static const x10aux::serialization_id_t _serialization_id;

            virtual void _serialize_id(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(_serialization_id,m);
            }

            template<class T> static x10aux::ref<T> _deserializer(x10aux::serialization_buffer &buf);
        };

        template<class T> x10aux::ref<T> RuntimeException::_deserializer(x10aux::serialization_buffer &buf){
            x10aux::ref<RuntimeException> this_ =
                new (x10aux::alloc<RuntimeException>()) RuntimeException();
            this_->_deserialize_body(buf);
            return this_;
        }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
