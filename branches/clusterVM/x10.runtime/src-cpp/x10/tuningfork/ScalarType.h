#ifndef X10_TUNINGFORK_SCALARTYPE_H
#define X10_TUNINGFORK_SCALARTYPE_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

#include <x10/lang/Value.h>

namespace x10 { namespace lang { class String; } }

namespace x10 {
    namespace tuningfork {

        // TODO: This is just enough of a skeleton to allow code to compile.
        //       At some point, this needs to be fleshed out into a true wrapper
        //       around the TuningFork C/C++ trace generation library.
        class ScalarType : public x10::lang::Value {
        public:
            RTT_H_DECLS_CLASS;

            static x10aux::ref<ScalarType> FMGL(INT);
            static x10aux::ref<ScalarType> FMGL(LONG);
            static x10aux::ref<ScalarType> FMGL(DOUBLE);
            static x10aux::ref<ScalarType> FMGL(STRING);

            virtual x10aux::ref<x10::lang::String> getName();

            virtual x10aux::ref<x10::lang::String> getDescription();
        };
    }
}

        






#endif

