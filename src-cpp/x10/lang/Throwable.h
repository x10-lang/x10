#ifndef X10_LANG_THROWABLE_H
#define X10_LANG_THROWABLE_H

#include <x10aux/config.h>
#ifdef __GLIBC__
#define MAX_TRACE_SIZE 1024
#else
#define MAX_TRACE_SIZE 1
#endif

#include <x10/lang/Value.h>
#include <x10/lang/Box.h>

namespace x10 {

/*
    namespace io {
        class Printer;
    }
*/

    namespace lang {

        class String;
        template<class T> class ValRail;

        class Throwable : public virtual Value {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<Value>()); }
                virtual const char *name() const { return "x10.lang.Throwable"; }
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Throwable>();
            }

            x10aux::ref<Box<x10aux::ref<Throwable> > > FMGL(cause);
            x10aux::ref<String> FMGL(message);

            //any longer than this and stacktrace will be truncated
            void *FMGL(trace)[MAX_TRACE_SIZE]; 
            int FMGL(trace_size);


            static x10aux::ref<Throwable> _make()
            { return (new (x10aux::alloc<Throwable>()) Throwable())->_constructor(); }

            static x10aux::ref<Throwable> _make(x10aux::ref<String> message)
            { return (new (x10aux::alloc<Throwable>()) Throwable())->_constructor(message); }

            static x10aux::ref<Throwable> _make(x10aux::ref<Throwable> cause)
            { return (new (x10aux::alloc<Throwable>()) Throwable())->_constructor(cause); }

            static x10aux::ref<Throwable> _make(x10aux::ref<String> message,
                                                x10aux::ref<Throwable> cause)
            { return (new (x10aux::alloc<Throwable>()) Throwable())->_constructor(message, cause); }

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
            virtual x10aux::ref<Box<x10aux::ref<Throwable> > > getCause() { return FMGL(cause); }
            virtual x10aux::ref<String> toString();
            virtual x10aux::ref<Throwable> fillInStackTrace();
            typedef x10aux::ref<x10::lang::ValRail<x10aux::ref<x10::lang::String> > > StringRail;
            virtual StringRail getStackTrace();
            virtual void printStackTrace();

            static const x10aux::serialization_id_t _serialization_id;

            virtual void _serialize_id(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(_serialization_id,m);
            }

            virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(FMGL(cause),m);
                buf.write(FMGL(message),m);
            }

            template<class T>
            static x10aux::ref<T> _deserializer(x10aux::serialization_buffer &buf){
                x10aux::ref<Throwable> this_ = new (x10aux::alloc<Throwable>()) Throwable();
                this_->_deserialize_body(buf);
                return this_;
            }

            void _deserialize_body(x10aux::serialization_buffer &buf) {
                FMGL(cause) = buf.read<x10aux::ref<Box<x10aux::ref<Throwable> > > >();
                FMGL(message) = buf.read<x10aux::ref<String> >();
            }

        };


    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
