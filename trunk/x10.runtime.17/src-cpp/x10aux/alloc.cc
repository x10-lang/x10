#include <x10aux/alloc.h>

#include <cstdio>
#include <cstdarg>

using namespace x10aux;

#ifdef __CYGWIN__
extern "C" int vsnprintf(char *, size_t, const char *, va_list); 
#endif

char *x10aux::alloc_printf(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    std::size_t sz = vsnprintf(NULL,0,fmt,args);
    va_end(args);
    char *r = x10aux::alloc<char>(sz+1);
    va_start(args, fmt);
    vsprintf(r, fmt, args);
    va_end(args);
    return r;
}

char *x10aux::realloc_printf(char *buf, const char *fmt, ...) {
    std::size_t original_sz = strlen(buf);
    va_list args;
    va_start(args, fmt);
    std::size_t sz = original_sz + vsnprintf(NULL,0,fmt,args);
    va_end(args);
    char *r = x10aux::realloc(buf,sz+1);
    // append the new stuff onto the original stuff
    va_start(args, fmt);
    vsprintf(&r[original_sz], fmt, args);
    va_end(args);
    return r;
}


