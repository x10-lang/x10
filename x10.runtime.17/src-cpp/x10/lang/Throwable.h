#ifndef X10_LANG_THROWABLE_H
#define X10_LANG_THROWABLE_H

#include <x10aux/config.h>
#ifdef __GLIBC__
#define MAX_TRACE_SIZE 1024
#else
#define MAX_TRACE_SIZE 1
#endif
#include <x10aux/RTT.h>

#include <x10/lang/Value.h>

namespace x10 {

/*
    namespace io {
        class Printer;
    }
*/

    namespace lang {

        class String;
        template<class T> class ValRail;
        template<class T> class Box;

        class Throwable : public virtual Value {

            public:

            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<Value>()); }
                virtual std::string name() const { return "x10.lang.Throwable"; }
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Throwable>();
            }

            typedef x10aux::ref<Box<x10aux::ref<Throwable> > > Cause;

            Cause FMGL(cause);
            x10aux::ref<String> FMGL(message);

            //any longer than this and stacktrace will be truncated
            void *FMGL(trace)[MAX_TRACE_SIZE]; 
            int FMGL(trace_size);

            Throwable();
            Throwable(x10aux::ref<String> message);
            Throwable(x10aux::ref<Throwable> cause);
            Throwable(x10aux::ref<String> message, x10aux::ref<Throwable> cause);

            virtual x10aux::ref<String> getMessage();
            virtual Cause getCause();
            virtual x10aux::ref<String> toString();
            virtual x10aux::ref<Throwable> fillInStackTrace();
            typedef x10aux::ref<x10::lang::ValRail<x10aux::ref<x10::lang::String> > > StringRail;
            virtual StringRail getStackTrace();

            explicit Throwable(x10aux::SERIALIZATION_MARKER m) : Value(m) { }


            virtual void _serialize_fields(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(FMGL(cause),m);
                buf.write(FMGL(message),m);
            }

            virtual void _deserialize_fields(x10aux::serialization_buffer&) { }

        };


    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
