#include <x10/tuningfork/ScalarType.h>

#include <assert.h>

using namespace x10::lang;
using namespace x10::tuningfork;

// TODO: Must initialize these to real values
x10aux::ref<ScalarType> ScalarType::FMGL(INT);
x10aux::ref<ScalarType> ScalarType::FMGL(LONG);
x10aux::ref<ScalarType> ScalarType::FMGL(DOUBLE);
x10aux::ref<ScalarType> ScalarType::FMGL(STRING);

x10aux::ref<String> ScalarType::getName() {
    assert(false); // TODO
    return NULL;
}

x10aux::ref<String> ScalarType::getDescription() {
    assert(false); // TODO
    return NULL;
}

RTT_CC_DECLS1(ScalarType, "x10.tuningfork.ScalarValue", Value)
