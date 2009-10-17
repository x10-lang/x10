#ifndef X10_IO_NATIVEFILE_H
#define X10_IO_NATIVEFILE_H

#include <x10aux/config.h>
#include <x10aux/serialization.h>

#include <x10/lang/Ref.h>

#include <x10/lang/String.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
    }

    namespace io {

        class File__NativeFile : public x10::lang::Ref {
            public:
            RTT_H_DECLS_CLASS;

        private:

            x10aux::ref<x10::lang::String> path;

        public:

            static x10aux::ref<File__NativeFile> _make(x10aux::ref<x10::lang::String> s);
            x10aux::ref<File__NativeFile> _constructor(x10aux::ref<x10::lang::String> s) {
                this->x10::lang::Ref::_constructor();
                path = s;
                return this;
            }

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                this->x10::lang::Ref::_serialize_body(buf, m);
            }

            template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &buf) {
                x10aux::ref<File__NativeFile> this_ = new (x10aux::alloc_remote<File__NativeFile>()) File__NativeFile();
                this_->_deserialize_body(buf);
                return this_;
            }

            void _deserialize_body(x10aux::deserialization_buffer& buf) {
                this->x10::lang::Ref::_deserialize_body(buf);
            }

            virtual x10aux::ref<x10::lang::String> getPath() { return path; }

            virtual x10aux::ref<x10::lang::String> getAbsolutePath();

            virtual x10aux::ref<x10::lang::String> getCanonicalPath();

            virtual x10_boolean canRead();

            virtual x10_boolean canWrite();

            virtual x10_boolean exists();

            virtual x10_boolean isDirectory();

            virtual x10_boolean isFile();

            virtual x10_boolean isHidden();

            virtual x10_long lastModified();

            virtual x10_boolean setLastModified(x10_long t);

            virtual x10_long length();

        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
