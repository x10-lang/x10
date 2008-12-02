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

                    virtual std::string name() const {
                        return "x10.io.File.NativeFile";
                    }

            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<NativeFile>();
            }

        private:

            x10aux::ref<x10::lang::String> name;

        public:

            explicit NativeFile(x10aux::ref<x10::lang::String> s) : Ref(), name(s) { }

            virtual x10aux::ref<x10::lang::String> getName() { return name; }

            virtual x10aux::ref<x10::lang::String> getParent() { assert(false); return name; }

            virtual x10aux::ref<x10::lang::String> getPath() { assert(false); return name; }

            virtual x10_boolean isAbsolute() { assert(false); return (x10_boolean)false; }

            virtual x10aux::ref<x10::lang::String> getAbsolutePath() { assert(false); return name; }

            virtual x10aux::ref<x10::lang::String> getCanonicalPath() { assert(false); return name; }

            virtual x10_boolean canRead() { assert(false); return (x10_boolean)false; }

            virtual x10_boolean canWrite() { assert(false); return (x10_boolean)false; }

            virtual x10_boolean exists() { assert(false); return (x10_boolean)false; }

            virtual x10_boolean isDirectory() { assert(false); return (x10_boolean)false; }

            virtual x10_boolean isFile() { assert(false); return (x10_boolean)false; }

            virtual x10_boolean isHidden() { assert(false); return (x10_boolean)false; }

            virtual x10_long lastModified() { assert(false); return (x10_long)0; }

            virtual x10_long length() { assert(false); return (x10_long)0; }

            virtual x10_boolean setLastModified(x10_long t) { (void)t; assert(false); return (x10_boolean)false; }

        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
