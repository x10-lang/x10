#include <sstream>

#include <x10aux/config.h>
#include <x10aux/string_utils.h>

#include <x10/lang/Throwable.h>
#include <x10/lang/String.h>
#include <x10/lang/Rail.h>


#ifdef __GLIBC__
#include <execinfo.h> // for getStackTrace()
#endif
#ifdef __GNUC__
#include <cxxabi.h> // for demangle()
#endif


using namespace x10::lang;
using namespace x10aux;

typedef Throwable::Cause Cause;

Throwable::Throwable() : Value(), FMGL(trace_size)(-1) {
    this->FMGL(cause) = NULL;
    this->FMGL(message) = to_string("");
}


Throwable::Throwable(ref<String> message) : Value(), FMGL(trace_size)(-1) {
    this->FMGL(cause) = NULL;
    this->FMGL(message) = message;
}


Throwable::Throwable(Cause cause) : Value(), FMGL(trace_size)(-1) {
    this->FMGL(cause) = cause;
    this->FMGL(message) = to_string("");
}


Throwable::Throwable(ref<String> message, Cause cause) : Value(), FMGL(trace_size)(-1) {
    this->FMGL(cause) = cause;
    this->FMGL(message) = message;
}


ref<String> Throwable::getMessage() {
    return FMGL(message);
}

Cause Throwable::getCause() {
    return FMGL(cause);
}

ref<String> Throwable::toString() {
    std::stringstream ss;
    ss << this->_type()->name() << ": " << *this->getMessage();
    return to_string(ss.str().c_str());
}


ref<Throwable> Throwable::fillInStackTrace() {
#ifdef __GLIBC__
    if (FMGL(trace_size)>=0) return this;
    FMGL(trace_size) = ::backtrace(FMGL(trace), sizeof(FMGL(trace))/sizeof(*FMGL(trace)));
#endif
    return this;
}


#ifdef __GLIBC__
static char *demangle (const char *line) {
    // arbitrary_text + "(" + symbol + "+0x" + hex_offset + ") [0x" + address +"]"
    char *start = strrchr(line,'(');
    char *end = strrchr(line,'+');
    assert(start!=NULL);
    assert(end!=NULL);
    start++;
    size_t len = end - start + 1; // extra 1 for null terminator
    char *mangled = (char*)malloc(len);
    strncpy(mangled,start,len-1);
    mangled[len-1] = '\0';
    char *demangled = abi::__cxa_demangle(mangled, NULL, NULL, NULL);
    if (demangled==NULL) {
        demangled = mangled;
        mangled = NULL;
    }
    free(mangled);
    return demangled;
}
#endif


ref<ValRail<ref<String> > > Throwable::getStackTrace() {
#ifdef __GLIBC__
    if (FMGL(trace_size)<=0) {
        const char *msg = "No stacktrace recorded.";
        return alloc_rail<ref<String>,ValRail<ref<String> > >(1,String(msg));
    }
    ref<ValRail<ref<String> > > rail =
        alloc_rail<ref<String>,ValRail<ref<String> > >(FMGL(trace_size));
    char **messages = ::backtrace_symbols(FMGL(trace), FMGL(trace_size));
    for (int i=0 ; i<FMGL(trace_size) ; ++i) {
        char *demangled = demangle(messages[i]);
        (*rail)[i] = String(demangled);
        ::free(demangled); // malloced by demangle
    }
    ::free(messages); // malloced by backtrace_symbols
    return rail;
#else
    const char *msg = "No stacktrace available for your compiler.";
    return alloc_rail<ref<String>,ValRail<ref<String> > >(1,String(msg));
#endif
}


void Throwable::_serialize_fields(serialization_buffer& buf, addr_map& m) {
    Value::_serialize_fields(buf, m);
/*
    buf.write(this->FMGL(cause)); 
    _S_("Written reference cause");
    if (!m.ensure_unique(this->FMGL(message))) assert (false);
    this->message->_serialize(buf, m);
    _S_("Serialized message");
*/
}
void Throwable::_deserialize_fields(serialization_buffer& buf) {
    (void)buf; abort();
/*
    this->cause = buf.read<ref<Box < ref<Throwable> > > >();
    this->message = _deserialize_value_ref<String >(buf);
*/
}


DEFINE_RTT(Throwable);
// vim:tabstop=4:shiftwidth=4:expandtab
