#ifndef X10_IO_NATIVEFILE_H
#define X10_IO_NATIVEFILE_H

#include <x10aux/config.h>

#include <stdio.h>

#include <x10/lang/Ref.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
    }

    namespace io {

        class File__NativeFile : public x10::lang::Ref {
            public:
            RTT_H_DECLS_CLASS;

        private:

            x10aux::ref<x10::lang::String> name;

        public:

            static x10aux::ref<File__NativeFile> _make(x10aux::ref<x10::lang::String> s);
            x10aux::ref<File__NativeFile> _constructor(x10aux::ref<x10::lang::String> s) {
                name = s;
                return this;
            }

            virtual x10aux::ref<x10::lang::String> getName() { return name; }

            virtual x10aux::ref<x10::lang::String> getParent() { abort(); return name; }

            virtual x10aux::ref<x10::lang::String> getPath() { abort(); return name; }

            virtual x10_boolean isAbsolute() { abort(); return (x10_boolean)false; }

            virtual x10aux::ref<x10::lang::String> getAbsolutePath() { abort(); return name; }

            virtual x10aux::ref<x10::lang::String> getCanonicalPath() { abort(); return name; }

            virtual x10_boolean canRead() { abort(); return (x10_boolean)false; }

            virtual x10_boolean canWrite() { abort(); return (x10_boolean)false; }

            virtual x10_boolean exists() { abort(); return (x10_boolean)false; }

            virtual x10_boolean isDirectory() { abort(); return (x10_boolean)false; }

            virtual x10_boolean isFile() { abort(); return (x10_boolean)false; }

            virtual x10_boolean isHidden() { abort(); return (x10_boolean)false; }

            virtual x10_long lastModified() { abort(); return (x10_long)0; }

            virtual x10_long length() { abort(); return (x10_long)0; }

            virtual x10_boolean setLastModified(x10_long t) { (void)t; abort(); return (x10_boolean)false; }

        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
