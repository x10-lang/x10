#include <sstream>

#include <x10aux/config.h>
#include <x10aux/string_utils.h>
#include <x10aux/class_cast.h>

#include <x10/lang/Throwable.h>
#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

#ifdef __GLIBC__
#   include <execinfo.h> // for backtrace()
#   ifdef USE_BFD
#       include <bfd.h> // for filename / line number info
#   endif
#   include <cxxabi.h> // for demangling of symbol
#endif


using namespace x10::lang;
using namespace x10aux;

typedef Throwable::Cause Cause;

Throwable::Throwable() : Value(), FMGL(trace_size)(-1) {
    this->FMGL(cause) = null;
    this->FMGL(message) = to_string("");
}


Throwable::Throwable(ref<String> message) : Value(), FMGL(trace_size)(-1) {
    this->FMGL(cause) = null;
    this->FMGL(message) = message;
}


Throwable::Throwable(x10aux::ref<Throwable> cause) : Value(), FMGL(trace_size)(-1) {
    this->FMGL(cause) = x10aux::box(cause);
    this->FMGL(message) = to_string("");
}


Throwable::Throwable(ref<String> message, x10aux::ref<Throwable> cause)
  : Value(), FMGL(trace_size)(-1) {
    this->FMGL(cause) = x10aux::box(cause);
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
// This one gets the function name as a demangled string,
// the filename of the native executable/library that contains the function,
// and the value of the program counter (addr).
void extract_frame (const char *start, char * &filename, char * &symbol, size_t &addr) {
    // arbitrary_text + "(" + symbol + "+0x" + hex_offset + ") [0x" + address +"]"
    char *lparen = strrchr(start,'(');
    char *plus = strrchr(start,'+');
    char *x = strrchr(start,'x');

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

    if (bfd_get_file_flags (abfd) & HAS_SYMS == 0)  {
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

static void *__init = __init_bfd();

#endif // USE_BFD

#endif // __GLIBC_


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
        char *filename; char *symbol; size_t addr;
        extract_frame(messages[i],filename,symbol,addr);
        char *msg = symbol;
        #ifdef USE_BFD
        if (addr!=0) {
            char *srcfile; size_t srcline;
            extract_src_file_line_num(filename, addr, srcfile, srcline);
            // I hate writing C.
            size_t msgsz = 1+snprintf(NULL,0,"%s (%s:%d)",symbol,srcfile,srcline);
            msg = (char*)malloc(msgsz);
            snprintf(msg,msgsz,"%s (%s:%d)",symbol,srcfile,srcline);
            ::free(symbol);
            ::free(srcfile);
        }
        #endif
        (*rail)[i] = String(msg);
        ::free(msg);
        ::free(filename);
    }
    ::free(messages); // malloced by backtrace_symbols
    return rail;
#else
    const char *msg = "No stacktrace available for your compiler.  So cry your heart out.";
    return alloc_rail<ref<String>,ValRail<ref<String> > >(1,String::Lit(msg));
#endif
}


void Throwable::_serialize_fields(serialization_buffer& buf, addr_map& m) {
    (void)buf; (void)m; abort();
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
