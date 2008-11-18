#include <x10aux/float_utils.h>
#include <x10aux/string_utils.h>

#include <x10/lang/String.h>					\

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::float_utils::toHexString(x10_float value) {
    (void) value;
	assert(false); /* FIXME: STUBBED NATIVE */
	return NULL;
}

const ref<String> x10aux::float_utils::toString(x10_float value) {
    (void) value; 
	assert(false); /* FIXME: STUBBED NATIVE */
	return NULL;
}

x10_float x10aux::float_utils::parseFloat(const ref<String>& s) {
    (void) s;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_boolean x10aux::float_utils::isNaN(x10_float x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_boolean x10aux::float_utils::isInfinite(x10_float x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_int x10aux::float_utils::toIntBits(x10_float x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_int x10aux::float_utils::toRawIntBits(x10_float x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_float x10aux::float_utils::fromIntBits(x10_int x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}
