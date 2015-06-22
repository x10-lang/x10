/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10_LANG_CHECKED_THROWABLE_H
#define X10_LANG_CHECKED_THROWABLE_H

#include <x10aux/config.h>

#include <x10/lang/X10Class.h>

namespace x10 { namespace io { class Printer; } }

namespace x10 {
    namespace lang {

        class String;
        class Exception;
        
        template <class T> class Rail;

        class CheckedThrowable : public X10Class {
        public:
            RTT_H_DECLS_CLASS;

            CheckedThrowable* FMGL(cause);
            String* FMGL(message);

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
            ::x10::lang::Rail< ::x10::lang::String*>* FMGL(cachedStackTrace);
            
            static CheckedThrowable* _make();
            static CheckedThrowable* _make(String* message);
            static CheckedThrowable* _make(CheckedThrowable* cause);
            static CheckedThrowable* _make(String* message, CheckedThrowable* cause);

            CheckedThrowable* _constructor() {
                return _constructor(NULL, NULL);
            }

            CheckedThrowable* _constructor(String* message) {
                return _constructor(message, NULL);
            }

            CheckedThrowable* _constructor(CheckedThrowable* cause) {
                return _constructor(NULL, cause);
            }

            CheckedThrowable* _constructor(String* message, CheckedThrowable* cause);

            virtual String* getMessage() { return FMGL(message); }
            virtual CheckedThrowable* getCheckedCause() { return FMGL(cause); }
            virtual Exception* getCause();
            virtual String* toString();
            virtual CheckedThrowable* fillInStackTrace();
            virtual ::x10::lang::Rail<String*>* getStackTrace();
            virtual void printStackTrace();
            virtual void printStackTrace(::x10::io::Printer*);
            
            static const ::x10aux::serialization_id_t _serialization_id;

            virtual ::x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(::x10aux::serialization_buffer &buf);

            static Reference* _deserializer(::x10aux::deserialization_buffer &buf);

            void _deserialize_body(::x10aux::deserialization_buffer &buf);
        };
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
