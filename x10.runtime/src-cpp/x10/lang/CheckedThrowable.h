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

#ifndef X10_LANG_CHECKED_THROWABLE_H
#define X10_LANG_CHECKED_THROWABLE_H

#include <x10aux/config.h>

#include <x10/lang/Reference.h>

namespace x10 { namespace io { class Printer; } }
namespace x10 { namespace array { template <class T> class Array; } }

namespace x10 {
    namespace lang {

        class String;

        class CheckedThrowable : public Reference {
        public:
            RTT_H_DECLS_CLASS;

            x10aux::ref<CheckedThrowable> FMGL(cause);
            x10aux::ref<String> FMGL(message);

            // This stores the the native backtrace information
            // captured when the exception was thrown.
            // If the exception was raised on a different place,
            // then this will not have valid information, but there may
            // still be a human-readable backtrace available in the
            // cachedStackTrace field.
            void **FMGL(trace);
            int FMGL(trace_size);

            // Computing the human-readable form of the backtrace is expensive.
            // Once we do it, keep it around for future use.
            x10aux::ref<x10::array::Array<x10aux::ref<x10::lang::String> > > FMGL(cachedStackTrace);
            
            static x10aux::ref<CheckedThrowable> _make();
            static x10aux::ref<CheckedThrowable> _make(x10aux::ref<String> message);
            static x10aux::ref<CheckedThrowable> _make(x10aux::ref<CheckedThrowable> cause);
            static x10aux::ref<CheckedThrowable> _make(x10aux::ref<String> message,
                                                x10aux::ref<CheckedThrowable> cause);

            x10aux::ref<CheckedThrowable> _constructor() {
                return _constructor(X10_NULL, X10_NULL);
            }

            x10aux::ref<CheckedThrowable> _constructor(x10aux::ref<String> message) {
                return _constructor(message, X10_NULL);
            }

            x10aux::ref<CheckedThrowable> _constructor(x10aux::ref<CheckedThrowable> cause) {
                return _constructor(X10_NULL, cause);
            }

            x10aux::ref<CheckedThrowable> _constructor(x10aux::ref<String> message,
                                                x10aux::ref<CheckedThrowable> cause);

            virtual x10aux::ref<String> getMessage() { return FMGL(message); }
            virtual x10aux::ref<CheckedThrowable> getCause() { return FMGL(cause); }
            virtual x10aux::ref<String> toString();
            virtual x10aux::ref<CheckedThrowable> fillInStackTrace();
            virtual x10aux::ref<x10::array::Array<x10aux::ref<String> > > getStackTrace();
            virtual void printStackTrace();
            virtual void printStackTrace(x10aux::ref<x10::io::Printer>);
            
            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf);

            static x10aux::ref<Reference> _deserializer(x10aux::deserialization_buffer &buf);

            void _deserialize_body(x10aux::deserialization_buffer &buf);
        };
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
