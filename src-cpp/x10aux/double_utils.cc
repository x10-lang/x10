#include <x10aux/double_utils.h>
#include <x10aux/string_utils.h>

#include <x10/lang/String.h>

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::double_utils::toHexString(x10_double value) {
    (void) value;
	assert(false); /* FIXME: STUBBED NATIVE */
	return NULL;
}

const ref<String> x10aux::double_utils::toString(x10_double value) {
    (void) value; 
	assert(false); /* FIXME: STUBBED NATIVE */
	return NULL;
}

x10_double x10aux::double_utils::parseDouble(const ref<String>& s) {
    (void) s;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_boolean x10aux::double_utils::isNaN(x10_double x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_boolean x10aux::double_utils::isInfinite(x10_double x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_long x10aux::double_utils::toLongBits(x10_double x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_long x10aux::double_utils::toRawLongBits(x10_double x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

x10_double x10aux::double_utils::fromLongBits(x10_long x) {
	(void) x;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}
