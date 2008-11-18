#include <x10aux/boolean_utils.h>

#include <x10/lang/String.h>					\

using namespace x10::lang;
using namespace std;
using namespace x10aux;

ref<String> x10aux::boolean_utils::_trueString = new String("true");
ref<String> x10aux::boolean_utils::_falseString = new String("false");

const ref<String> x10aux::boolean_utils::toString(x10_boolean value) {
	return value ? _trueString : _falseString;
}

x10_boolean x10aux::boolean_utils::parseBoolean(const ref<String>& s) {
    (void) s;
	assert(false); /* FIXME: STUBBED NATIVE */
	return 0; 
}

