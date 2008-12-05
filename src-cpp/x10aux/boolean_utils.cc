#include <x10aux/boolean_utils.h>

#include <x10/lang/String.h>                    \

#include <strings.h>
#ifdef __CYGWIN__
extern "C" int strcasecmp(const char *, const char *);
#endif

using namespace x10::lang;
using namespace std;
using namespace x10aux;

ref<String> x10aux::boolean_utils::_trueString = String::Lit("true");
ref<String> x10aux::boolean_utils::_falseString = String::Lit("false");

const ref<String> x10aux::boolean_utils::toString(x10_boolean value) {
    return value ? _trueString : _falseString;
}

x10_boolean x10aux::boolean_utils::parseBoolean(const ref<String>& s) {
    return s != null && !::strcasecmp(((const string&)(*s)).c_str(), "true");
}

// vim:tabstop=4:shiftwidth=4:expandtab
