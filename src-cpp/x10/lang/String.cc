#include <x10aux/alloc.h>

#include <x10/lang/String.h>

using namespace x10::lang;
using namespace x10aux;

x10_int String::hashCode() const {
    //FIXME:
    //presumably this needs a general hashcode implementation
    //that is centralised and used everywhere
    return 0;
}

ref<String> String::toString() const {
    return this;
}

x10_boolean String::equals(const ref<Object>& other) const {
    if (!RTT::it->concreteInstanceOf(other)) return false;
    // now we can downcast the Object to String
    String &other_str = static_cast<String&>(*other);
    // defer to std::string::compare to check string contents
    return !compare(other_str);
}
    

const String::RTT * const String::RTT::it =
    new String::RTT();

