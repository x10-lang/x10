#include <x10aux/int_utils.h>

#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;
using namespace std;

x10_int x10aux::int_parseInt(const ref<String>& s) {
	// FIXME: what about null?
	// FIXME: NumberFormatException?
	return atoi(((const string&)(*s)).c_str());
}
