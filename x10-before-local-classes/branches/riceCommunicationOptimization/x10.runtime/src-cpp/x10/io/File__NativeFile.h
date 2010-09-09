#ifndef X10_IO_NATIVEFILE_H
#define X10_IO_NATIVEFILE_H

#include <x10aux/config.h>

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
                path = s;
                return this;
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
