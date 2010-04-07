/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#include <x10aux/config.h>
#include <x10aux/basic_functions.h>
#include <x10aux/class_cast.h>

#include <x10/lang/Throwable.h>
#include <x10/lang/String.h>
#include <x10/lang/Rail.h>
#include <x10/io/Printer.h>

#ifdef __GLIBC__
#   include <execinfo.h> // for backtrace()
#   ifdef USE_BFD
#       include <bfd.h> // for filename / line number info
#   endif
#   include <cxxabi.h> // for demangling of symbol
#else
#   if defined(_AIX)
#      include <unistd.h>
#      include <stdlib.h>
#      include <stdio.h>
#      include <string.h>
#      ifndef __GNUC__
#         include <demangle.h> // for demangling of symbol
#      else
#        include <cxxabi.h> // for demangling of symbol
#      endif
#   endif
#endif

#include <stdio.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t Throwable::_serialization_id =
    DeserializationDispatcher::addDeserializer(Throwable::_deserializer<Object>);

void
Throwable::_serialize_body(x10aux::serialization_buffer &buf) {
    fillInStackTrace();
    this->Object::_serialize_body(buf);
    buf.write(FMGL(cause));
    buf.write(FMGL(message));
    buf.write(FMGL(trace_size));
    if (FMGL(trace_size) > 0) {
        for (int i=0; i<FMGL(trace_size); i++) {
            buf.write((size_t)FMGL(trace)[i]);
        }
    }
}

void
Throwable::_deserialize_body(x10aux::deserialization_buffer &buf) {
    this->Object::_deserialize_body(buf);
    FMGL(cause) = buf.read<x10aux::ref<Throwable> >();
    FMGL(message) = buf.read<x10aux::ref<String> >();
    FMGL(trace_size) = buf.read<x10_int>();
    if (FMGL(trace_size) > 0) {
        for (int i=0; i<FMGL(trace_size); i++) {
            FMGL(trace)[i] = (void*)(buf.read<size_t>());
        }
    }
}

x10aux::ref<Throwable>
Throwable::_make() {
    return (new (x10aux::alloc<Throwable>()) Throwable())->_constructor();
}

x10aux::ref<Throwable>
Throwable::_make(x10aux::ref<String> message) {
    return (new (x10aux::alloc<Throwable>()) Throwable())->_constructor(message);
}

x10aux::ref<Throwable>
Throwable::_make(x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<Throwable>()) Throwable())->_constructor(cause);
}
    
x10aux::ref<Throwable>
Throwable::_make(x10aux::ref<String> message, x10aux::ref<Throwable> cause) {
    return (new (x10aux::alloc<Throwable>()) Throwable())->_constructor(message, cause);
}

x10aux::ref<Throwable> Throwable::_constructor(x10aux::ref<String> message,
                                               x10aux::ref<Throwable> cause)
{
    this->Object::_constructor();
    this->FMGL(message) = message;
    this->FMGL(cause) = cause;
    this->FMGL(trace_size) = -1;
    return this;
}


ref<String> Throwable::toString() {
    ref<String> message = getMessage();
    if (message.isNull())
        return String::Lit(_type()->name());
    return String::Steal(alloc_printf("%s: %s",_type()->name(),message->c_str()));
}


#if !defined(__GLIBC__) && defined(_AIX)
#define BACKTRACE_SYM "backtrace__FPPvUl"
extern "C" int mt__trce(int, int, void*, int);
int backtrace(void** trace, size_t max_size) {
    int pid = ::getpid();
    int p[2];
    pipe(p);
    mt__trce(p[1], 0, NULL, 0);
    close(p[1]);
    FILE* pf = fdopen(p[0], "r");
    char m_buf[1001];
    size_t len = sizeof(m_buf) - 1;
    bool in_thread = false;
    bool in_trace = false;
    bool first_frame = false;
    char* s;
    size_t sz = 0;
    while ((s = fgets(m_buf, len, pf)) != NULL) {
        if (!in_thread) {
            if (!strncmp(s, "+++ID ", 6)) { // thread start
                char* p = strstr(s, " Process ");
                char* t = strstr(s, " Thread ");
                if (p == NULL || t == NULL)
                    continue;
                *strchr(t, '\n') = '\0';
                *t = '\0';
                int i = strtol(p+9, NULL, 10);
                if (i != pid) {
                    *t = ' ';
                    continue;
                }
                in_thread = true;
            }
            continue;
        }
        if (!strncmp(s, "---ID ", 6)) { // thread end
            in_thread = false;
            continue;
        }
        if (!in_trace) {
            if (!strcmp(s, "+++STACK\n")) { // stack trace start
               in_trace = true;
               first_frame = true;
            }
            continue;
        }
        if (!strcmp(s, "---STACK\n")) { // stack trace end
            in_trace = false;
            break; // assume we have the right thread -- we're done
        }
        if (first_frame) {
            // The first symbol has to be this function.  Skip it.
            // FIXME: theoretically, it's possible that another thread is here too
            if (strncmp(s, BACKTRACE_SYM, strlen(BACKTRACE_SYM))) {
                in_trace = false;
            }
            first_frame = false;
            continue;
        }
        if (sz >= max_size)
            break;
        trace[sz++] = strdup(s);
    }
    fclose(pf);
    close(p[0]);
    return (int)sz;
}
#endif


ref<Throwable> Throwable::fillInStackTrace() {
#if defined(__GLIBC__) || defined(_AIX)
    if (FMGL(trace_size)>=0) return this;
    FMGL(trace_size) = ::backtrace(FMGL(trace), sizeof(FMGL(trace))/sizeof(*FMGL(trace)));
#endif
    return this;
}


#ifdef __GLIBC__
// This one gets the function name as a demangled string,
// the filename of the native executable/library that contains the function,
// and the value of the program counter (addr).
void extract_frame (const char *start, char * &filename, char * &symbol, size_t &addr) {
    // arbitrary_text + "(" + symbol + "+0x" + hex_offset + ") [0x" + address +"]"
    const char *lparen = strrchr(start,'(');
    const char *plus = strrchr(start,'+');
    const char *x = strrchr(start,'x');

    if (lparen==NULL || plus==NULL || x==NULL) {
        filename = NULL;
        symbol = strdup(start);
        addr = 0;
        return;
    }

    filename = (char*)malloc(lparen-start+1);
    strncpy(filename,start,lparen-start);
    filename[lparen-start] = '\0';

    char *mangled = (char*)malloc(plus-lparen);
    strncpy(mangled,lparen+1,plus-lparen-1);
    mangled[plus-lparen-1] = '\0';

    size_t offset = strtol(plus+3, NULL, 16);
    addr = strtol(x+1, NULL, 16);
    (void)offset;
    //addr += offset;

    // don't free symbol, it's persistant
    symbol = NULL;
    symbol = abi::__cxa_demangle(mangled, NULL, NULL, NULL);
    if (symbol==NULL) {
        symbol = mangled;
    } else {
        free(mangled);
    }
}

#ifdef USE_BFD
// This one opens up the executable file (filename) and looks for addr,
// returning a string containing the source file and line number corresponding
// to that address.
void extract_src_file_line_num (const char *filename, size_t addr,
                                char *&srcfile, size_t &linenum) {
    linenum=-1;
    //std::cerr<<"Filename: \""<<filename<<"\"  addr: 0x"<<std::hex<<addr<<std::dec<<std::endl;
    bfd *abfd = bfd_openr(filename,NULL);
    if (abfd==NULL) {
        srcfile = strdup("?");
        // file not readable, apparently
        return;
    }

    if (bfd_check_format (abfd, bfd_archive)) {
        bfd_close(abfd);
        srcfile = strdup("??");
        // file readable but of the wrong format?
        return;
    }

    char ** matching;
    if (! bfd_check_format_matches (abfd, bfd_object, &matching)) {
        // no idea what this is for
        srcfile = strdup("???");
        return;
    }

    if ((bfd_get_file_flags (abfd) & HAS_SYMS) == 0)  {
        bfd_close(abfd);
        // file has no symbols
        srcfile = strdup("????");
        return;
    }

    asymbol **syms = NULL;

    unsigned int sz;
    //FIXME: the (void**) cast below is a hack - what the hell is wrong with this API?!
    long symcount = bfd_read_minisymbols (abfd, FALSE, (void**)&syms, &sz);
    if (symcount == 0) {
        symcount = bfd_read_minisymbols (abfd, TRUE, (void**)&syms, &sz);
    }
    if (symcount < 0) {
        bfd_close(abfd);
        // file has no symbols (again?)
        srcfile = strdup("?????");
        return;
    }
    for (asection *sec = abfd->sections; sec!=NULL; sec=sec->next) {

        if ((bfd_get_section_flags(abfd, sec) & SEC_ALLOC) == 0)
            continue;

        bfd_vma vma = bfd_get_section_vma (abfd, sec);

        if (addr < vma) continue;

        bfd_size_type size = bfd_get_section_size (sec);
        if (addr >= vma + size) continue;

        const char *srcpath=NULL; // we want this
        unsigned int line=0; // we want this
        const char *funcname=NULL; // already have this info, so don't bother returning it
        bfd_boolean found = bfd_find_nearest_line (abfd, sec, syms, addr - vma,
                                                   &srcpath, &funcname, &line);

        if (found) {
            linenum = line; //return value;

            if (srcpath==NULL) {
                // I have no idea why this happens but it does...
                srcfile = strdup("??????");
                ::free(syms);
                bfd_close(abfd);
                return;
            }

            const char *srcfile_;

            // get the filename
            srcfile_ = strrchr(srcpath,'/');
            if (srcfile_==NULL) {
                // no '/' in srcpath
                srcfile_ = srcpath;
            } else {
                // skip over the '/' to leave just the file name
                srcfile_++;
            }

            srcfile = strdup(srcfile_); // return value;

            ::free(syms);
            bfd_close(abfd);
            return;
        }


    }

    ::free(syms);
    bfd_close(abfd);

    srcfile = strdup("???????");
}

void *__init_bfd() {
    bfd_init();
    return NULL;
}

static void *__init_bfd_ = __init_bfd();

#endif // USE_BFD

#endif // __GLIBC_


#if !defined(__GLIBC__) && defined(_AIX)
static char* demangle_symbol(char* name) {
#if defined(__GNUC__)
    char* res = abi::__cxa_demangle(name, NULL, NULL, NULL);
    if (res == NULL)
        return name;
    return res;
#else
    char* rest;
    Name* n = Demangle(name, rest);
    if (n == NULL)
        return name;
    return n->Text();
#endif
}
#endif

ref<ValRail<ref<String> > > Throwable::getStackTrace() {
#if defined(__GLIBC__)
    if (FMGL(trace_size) <= 0) {
        const char *msg = "No stacktrace recorded.";
        return alloc_rail<ref<String>,ValRail<ref<String> > >(1, String::Lit(msg));
    }
    ref<ValRail<ref<String> > > rail =
        alloc_rail<ref<String>,ValRail<ref<String> > >(FMGL(trace_size));
    char **messages = ::backtrace_symbols(FMGL(trace), FMGL(trace_size));
    for (int i=0 ; i<FMGL(trace_size) ; ++i) {
        char *filename; char *symbol; size_t addr;
        extract_frame(messages[i],filename,symbol,addr);
        char *msg = symbol;
        #ifdef USE_BFD
        if (addr != 0) {
            char *srcfile; size_t srcline;
            extract_src_file_line_num(filename, addr, srcfile, srcline);
            // I hate writing C.
            size_t msgsz = 1 + snprintf(NULL, 0, "%s (%s:%d)", symbol, srcfile, srcline);
            msg = (char*)malloc(msgsz);
            snprintf(msg, msgsz, "%s (%s:%d)", symbol, srcfile, srcline);
            ::free(symbol);
            ::free(srcfile);
        }
        #endif
        (*rail)[i] = String::Lit(msg);
        ::free(msg);
        ::free(filename);
    }
    ::free(messages); // malloced by backtrace_symbols
    return rail;
#elif defined(_AIX)
    if (FMGL(trace_size) <= 0) {
        const char *msg = "No stacktrace recorded.";
        return alloc_rail<ref<String>,ValRail<ref<String> > >(1, String::Lit(msg));
    }
    ref<ValRail<ref<String> > > rail =
        alloc_rail<ref<String>,ValRail<ref<String> > >(FMGL(trace_size));
    char *msg;
    for (int i=0 ; i<FMGL(trace_size) ; ++i) {
        char* s = (char*)FMGL(trace)[i];
        char* c = strstr(s, " : ");
        if (c == NULL) {
            (*rail)[i] = String::Lit("???????");
            continue;
        }
        c[0] = '\0';
        c += 3;
        char* n = strchr(c, '\n');
        if (n != NULL)
            *n = '\0';
        s = demangle_symbol(s);
        char* f = strstr(c, " # ");
        if (f != NULL) {
            unsigned long l = strtoul(c, NULL, 10);
            char* p = strchr(f, '<');
            if (p != NULL) {
                f = p + 1;
                char* z = strchr(f, '>');
                if (z != NULL)
                    *z = '\0';
            } else {
                f += 3;
            }
            msg = alloc_printf("%s (%s:%d)", s, f, l);
        } else {
            msg = alloc_printf("%s (offset %s)", s, c);
            f = c;
        }
        (*rail)[i] = String::Lit(msg);
        ::free(msg);
    }
    return rail;
#else
    const char *msg = "Detailed stacktraces not supported on this platform.";
    return alloc_rail<ref<String>,ValRail<ref<String> > >(1, String::Lit(msg));
#endif
}

void Throwable::printStackTrace() {
    fprintf(stderr, "%s\n", this->toString()->c_str());
    x10aux::ref<ValRail<x10aux::ref<String> > > trace = nullCheck(this->getStackTrace());
    for (int i = 0; i < trace->FMGL(length); ++i)
        fprintf(stderr, "\tat %s\n", (*trace)[i]->c_str());
}

void Throwable::printStackTrace(x10aux::ref<x10::io::Printer> printer) {
    printer->println(toString());
    x10aux::ref<x10::lang::ValRail<x10aux::ref<x10::lang::String> > > trace = getStackTrace();
    for (int i=0 ; i<trace->FMGL(length) ; ++i) { 
        printer->print(x10::lang::String::Lit("\tat "));
        printer->println((*trace)[i]);
    }
}

RTT_CC_DECLS1(Throwable, "x10.lang.Throwable", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
