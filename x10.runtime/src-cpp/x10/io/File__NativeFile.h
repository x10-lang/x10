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

#ifndef X10_IO_NATIVEFILE_H
#define X10_IO_NATIVEFILE_H

#include <x10aux/config.h>
#include <x10aux/serialization.h>

#include <x10/lang/X10Class.h>

#include <x10/lang/String.h>

#include <x10/lang/Rail.h>

namespace x10 {

    namespace io {

        class File__NativeFile : public ::x10::lang::X10Class {
            public:
            RTT_H_DECLS_CLASS;

        private:

            ::x10::lang::String* path;

            virtual x10_boolean mkdirs(const char *s);

        public:

            static File__NativeFile* _make(::x10::lang::String* s);
            File__NativeFile* _constructor(::x10::lang::String* s) {
                path = s;
                return this;
            }

            static const ::x10aux::serialization_id_t _serialization_id;

            virtual ::x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(::x10aux::serialization_buffer &buf);

            static ::x10::lang::Reference* _deserializer(::x10aux::deserialization_buffer &buf);

            virtual void _deserialize_body(::x10aux::deserialization_buffer& buf);

            virtual ::x10::lang::String* getPath() { return path; }

            virtual ::x10::lang::String* getAbsolutePath();

            virtual ::x10::lang::String* getCanonicalPath();

            virtual x10_boolean canRead();

            virtual x10_boolean canWrite();

            virtual x10_boolean exists();

            virtual x10_boolean isDirectory();

            virtual x10_boolean isFile();

            virtual x10_boolean isHidden();

            virtual x10_long lastModified();

            virtual x10_boolean setLastModified(x10_long t);

            virtual x10_long length();

            virtual x10_boolean del();

            virtual ::x10::lang::Rail< ::x10::lang::String*>* list();

            virtual x10_boolean mkdir();

            virtual x10_boolean mkdirs();

            virtual x10_boolean renameTo(File__NativeFile* dest);

        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
