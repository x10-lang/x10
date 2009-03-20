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

        class NativeFile : public x10::lang::Ref {
            public:
            class RTT : public x10aux::RuntimeType {
                public:
                    static RTT* const it;

                    virtual void init() {
                        initParents(1,x10aux::getRTT<x10::lang::Ref>());
                    }

                    virtual const char *name() const {
                        return "x10.io.File.NativeFile";
                    }

            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<NativeFile>();
            }

        private:

            x10aux::ref<x10::lang::String> name;

        public:

            static x10aux::ref<NativeFile> _make(x10aux::ref<x10::lang::String> s) {
                return (new (x10aux::alloc<NativeFile>()) NativeFile())->_constructor(s);
            }
            x10aux::ref<NativeFile> _constructor(x10aux::ref<x10::lang::String> s) {
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
