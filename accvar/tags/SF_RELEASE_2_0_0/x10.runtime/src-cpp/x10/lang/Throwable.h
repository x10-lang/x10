#ifndef X10_LANG_THROWABLE_H
#define X10_LANG_THROWABLE_H

#include <x10aux/config.h>
#if defined(__GLIBC__) || defined(_AIX)
#define MAX_TRACE_SIZE 1024
#else
#define MAX_TRACE_SIZE 1
#endif

#include <x10/lang/Ref.h>

namespace x10 {
    namespace lang {

        class String;
        template<class T> class ValRail;

        class Throwable : public Ref {
        public:
            RTT_H_DECLS_CLASS;

            x10aux::ref<Throwable> FMGL(cause);
            x10aux::ref<String> FMGL(message);

            //any longer than this and stacktrace will be truncated
            void *FMGL(trace)[MAX_TRACE_SIZE]; 
            int FMGL(trace_size);

            static x10aux::ref<Throwable> _make();
            static x10aux::ref<Throwable> _make(x10aux::ref<String> message);
            static x10aux::ref<Throwable> _make(x10aux::ref<Throwable> cause);
            static x10aux::ref<Throwable> _make(x10aux::ref<String> message,
                                                x10aux::ref<Throwable> cause);
        protected:
            x10aux::ref<Throwable> _constructor() {
                return _constructor(x10aux::null, x10aux::null);
            }

            x10aux::ref<Throwable> _constructor(x10aux::ref<String> message) {
                return _constructor(message, x10aux::null);
            }

            x10aux::ref<Throwable> _constructor(x10aux::ref<Throwable> cause) {
                return _constructor(x10aux::null, cause);
            }

            x10aux::ref<Throwable> _constructor(x10aux::ref<String> message,
                                                x10aux::ref<Throwable> cause);

        public:
            virtual x10aux::ref<String> getMessage() { return FMGL(message); }
            virtual x10aux::ref<Throwable> getCause() { return FMGL(cause); }
            virtual x10aux::ref<String> toString();
            virtual x10aux::ref<Throwable> fillInStackTrace();
            typedef x10aux::ref<x10::lang::ValRail<x10aux::ref<x10::lang::String> > > StringRail;
            virtual StringRail getStackTrace();
            virtual void printStackTrace();

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m);

            template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &buf);

            void _deserialize_body(x10aux::deserialization_buffer &buf);
        };

        template<class T> x10aux::ref<T> Throwable::_deserializer(x10aux::deserialization_buffer &buf){
            x10aux::ref<Throwable> this_ = new (x10aux::alloc<Throwable>()) Throwable();
            this_->_deserialize_body(buf);
            return this_;
        }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
