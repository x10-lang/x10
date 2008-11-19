#include <x10aux/boolean_utils.h>

#include <x10/lang/String.h>					\

using namespace x10::lang;
using namespace std;
using namespace x10aux;

ref<String> x10aux::boolean_utils::_trueString = new (alloc<String>())String("true");
ref<String> x10aux::boolean_utils::_falseString = new (alloc<String>())String("false");

const ref<String> x10aux::boolean_utils::toString(x10_boolean value) {
	return value ? _trueString : _falseString;
}

x10_boolean x10aux::boolean_utils::parseBoolean(const ref<String>& s) {
    return s != NULL && !strcasecmp(((const string&)(*s)).c_str(), "true");
}

