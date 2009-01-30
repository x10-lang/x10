#include <x10aux/alloc.h>

#include <cstdio>
#include <cstdarg>

using namespace x10aux;

#ifdef __CYGWIN__
extern "C" int vsnprintf(char *, size_t, const char *, va_list); 
#endif
const char *x10aux::alloc_printf(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    size_t sz = vsnprintf(NULL,0,fmt,args);
    char *r = x10aux::alloc<char>(sz+1);
    r[sz] = '\0';
    vsprintf(r, fmt, args);
    va_end(args);
    return r;
}

