/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#ifndef X10_LANG_THROWABLE_H
#define X10_LANG_THROWABLE_H

#include <x10aux/config.h>
#if defined(__GLIBC__) || defined(_AIX)
#define MAX_TRACE_SIZE 1024
#else
#define MAX_TRACE_SIZE 1
#endif

#include <x10/lang/Object.h>

namespace x10 { namespace io { class Printer; } }

namespace x10 {
    namespace lang {

        class String;
        template<class T> class ValRail;

        class Throwable : public Object {
        public:
            RTT_H_DECLS_CLASS;

            x10aux::ref<Throwable> FMGL(cause);
            x10aux::ref<String> FMGL(message);

            // This stores the the native backtrace information
            // captured when the exception was thrown.
            // If the exception was raised on a different place,
            // then this will not have valid information, but there may
            // still be a human-readable backtrace available in the
            // cachedStackTrace field.
            void *FMGL(trace)[MAX_TRACE_SIZE]; 
            int FMGL(trace_size);

            // Computing the human-readable form of the backtrace is expensive.
            // Once we do it, keep it around for future use.
            typedef x10aux::ref<x10::lang::ValRail<x10aux::ref<x10::lang::String> > > StringRail;
            StringRail FMGL(cachedStackTrace);
            
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
            virtual StringRail getStackTrace();
            virtual void printStackTrace();
            virtual void printStackTrace(x10aux::ref<x10::io::Printer>);
            
            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf);

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
