#include <x10aux/config.h>
#include <x10aux/boolean_utils.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/String.h>                    \

#include <strings.h>
#ifdef __CYGWIN__
extern "C" int strcasecmp(const char *, const char *);
#endif

using namespace x10::lang;
using namespace std;
using namespace x10aux;

const ref<String> x10aux::boolean_utils::toString(x10_boolean value) {
    return x10aux::to_string(value);
}

x10_boolean x10aux::boolean_utils::parseBoolean(const ref<String>& s) {
    return s != null && !::strcasecmp(s->c_str(), "true");
}

// vim:tabstop=4:shiftwidth=4:expandtab
