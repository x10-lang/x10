#ifndef X10_LANG_OBJECT_H
#define X10_LANG_OBJECT_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>

namespace x10 {
    namespace lang {

        class String;

        class Object {
            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT* const it;
            
                virtual void init() { initParents(0); }

                virtual std::string name() const {
                    return "x10.lang.Object";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Object>();
            }

            virtual x10_boolean equals(x10aux::ref<Object> id0) = 0;
            virtual x10_int hashCode() = 0;
            virtual x10aux::ref<String> toString() = 0;
            virtual ~Object() { }
        };
    }
}

#endif
