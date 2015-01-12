/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#include <x10aux/config.h>
#include <x10aux/basic_functions.h>
#include <x10aux/class_cast.h>

#include <x10/lang/CheckedThrowable.h>
#include <x10/lang/Rail.h>
#include <x10/lang/String.h>
#include <x10/io/Printer.h>
#include <x10/lang/Exception.h>

#if defined(__GLIBC__) || defined(__APPLE__)
#   include <execinfo.h> // for backtrace()
#   include <cxxabi.h> // for demangling of symbol
#elif defined(_AIX)
#   include <unistd.h>
#   include <stdlib.h>
#   include <stdio.h>
#   include <string.h>
#   ifndef __GNUC__
#      include <demangle.h> // for demangling of symbol
#   else
#     include <cxxabi.h> // for demangling of symbol
#   endif
#endif

#include <stdio.h>

using namespace x10::lang;
using namespace x10aux;

const serialization_id_t CheckedThrowable::_serialization_id =
    DeserializationDispatcher::addDeserializer(CheckedThrowable::_deserializer);

void
CheckedThrowable::_serialize_body(x10aux::serialization_buffer &buf) {
    buf.write(FMGL(cause));
    buf.write(FMGL(message));
    getStackTrace(); // ensure cachedStackTrace has been computed before serializing it
    buf.write(FMGL(cachedStackTrace));
}

void
CheckedThrowable::_deserialize_body(x10aux::deserialization_buffer &buf) {
    FMGL(cause) = buf.read<CheckedThrowable*>();
    FMGL(message) = buf.read<String*>();
    FMGL(cachedStackTrace) = buf.read<Rail<String*>*>();
    FMGL(trace_size) = 0;
    FMGL(trace) = NULL;
}

Reference* CheckedThrowable::_deserializer(x10aux::deserialization_buffer &buf){
    CheckedThrowable* this_ = new (x10aux::alloc<CheckedThrowable>()) CheckedThrowable();
    this_->_deserialize_body(buf);
    return this_;
}

CheckedThrowable* CheckedThrowable::_make() {
    return (new (x10aux::alloc<CheckedThrowable>()) CheckedThrowable())->_constructor();
}

CheckedThrowable* CheckedThrowable::_make(String* message) {
    return (new (x10aux::alloc<CheckedThrowable>()) CheckedThrowable())->_constructor(message);
}

CheckedThrowable* CheckedThrowable::_make(CheckedThrowable* cause) {
    return (new (x10aux::alloc<CheckedThrowable>()) CheckedThrowable())->_constructor(cause);
}
    
CheckedThrowable* CheckedThrowable::_make(String* message, CheckedThrowable* cause) {
    return (new (x10aux::alloc<CheckedThrowable>()) CheckedThrowable())->_constructor(message, cause);
}

CheckedThrowable* CheckedThrowable::_constructor(String* message, CheckedThrowable* cause) {
    this->FMGL(message) = message;
    this->FMGL(cause) = cause;
    this->FMGL(trace_size) = -1;
    this->FMGL(trace) = NULL;
    this->FMGL(cachedStackTrace) = NULL;
    return this;
}

Exception* CheckedThrowable::getCause() {
    return x10::lang::Exception::ensureException(this);
}

String* CheckedThrowable::toString() {
    String* message = getMessage();
    if (NULL == message) {
        return String::Lit(_type()->name());
    } else {
        return String::Steal(alloc_printf("%s: %s",_type()->name(),message->c_str()));
    }
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
        trace[sz++] = ::strdup(s);
    }
    fclose(pf);
    close(p[0]);
    return (int)sz;
}
#endif

#if defined(__GLIBC__) || defined(__APPLE__)
#define MAX_TRACE_SIZE 1024
#elif defined(_AIX)

#define INITIAL_TRACE_SIZE 32
#define TRACE_SIZE_INCREMENT 64
#define MAX_TRACE_SIZE 512

#endif

CheckedThrowable* CheckedThrowable::fillInStackTrace() {
    if (FMGL(trace_size)>=0) return this;

#if defined(__GLIBC__) || defined(__APPLE__)
    void *buffer[MAX_TRACE_SIZE];

    int numFrames = (::backtrace(buffer, MAX_TRACE_SIZE)) - 1; // 1 frame smaller to cut out this "fillInStackTrace" method from the disalayed stack
    FMGL(trace) = x10aux::alloc<void*>(numFrames*sizeof(void*), false); // does not contain pointers to GC heap
    memcpy(FMGL(trace), &buffer[1], numFrames*sizeof(void*));
    FMGL(trace_size) = numFrames;
#elif defined(_AIX)
    int numFrames = 0;
	FMGL(trace) = x10aux::alloc<void*>(INITIAL_TRACE_SIZE*sizeof(void*), false); // does not contain pointers to GC heap
	int bufferSize = INITIAL_TRACE_SIZE;
	
    // walk the stack, saving the offsets for each stack frame into "buffer".
    unsigned long stackAddr;
	#if defined(_LP64)
		__asm__ __volatile__ ("std 1, %0 \n\t" : "=m" (stackAddr));
	#else
		__asm__ __volatile__ ("stw 1, %0 \n\t" : "=m" (stackAddr));
	#endif
	// Need to leave 1 frame of slop for recapture of backtrace (See AIX code below in getStackTrace)
	while (numFrames < MAX_TRACE_SIZE-1) {
		if (numFrames == bufferSize-1) {
			FMGL(trace) = x10aux::realloc<void*>(FMGL(trace), (bufferSize+TRACE_SIZE_INCREMENT)*sizeof(void*));
		}
		FMGL(trace)[numFrames] = (void*)*(((unsigned long *)stackAddr)+2); // link register is saved here in the stack
		stackAddr = *((long *)stackAddr);
        numFrames++;
		if (stackAddr == 0) {
            // the end of the stack (main)
			break;
        }
	}
    FMGL(trace_size) = numFrames;
#endif
    return this;
}

#ifdef __GLIBC__
// This one gets the function name as a demangled string,
// the filename of the native executable/library that contains the function,
// and the value of the program counter (addr).
void extract_frame_ct (const char *start, char * &filename, char * &symbol, size_t &addr) {
    // arbitrary_text + "(" + symbol + "+0x" + hex_offset + ") [0x" + address +"]"
    const char *lparen = strrchr(start,'(');
    const char *plus = NULL == lparen ? NULL : strrchr(lparen,'+');
    const char *x = NULL == lparen ? NULL : strrchr(lparen,'x');

    if (lparen==NULL || plus==NULL || x==NULL) {
        filename = NULL;
        symbol = ::strdup(start);
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
#elif defined(__APPLE__)
// This one gets the function name as a demangled string,
// the filename of the native executable/library that contains the function,
// and the value of the program counter (addr).
void extract_frame_ct (const char *start, char * &filename, char * &symbol, size_t &addr) {
    // arbitrary_text + " 0x" + address + " " + symbol + " + " offset
    // arbitrary_text + "(" + symbol + "+0x" + hex_offset + ") [0x" + address +"]"
    const char *x = strstr(start," 0x");
    const char *space = strchr(x+1,' ');
    const char *plus = strchr(space,'+');

    if (space==NULL || plus==NULL || x==NULL) {
        filename = NULL;
        symbol = ::strdup(start);
        addr = 0;
        return;
    }

    filename = (char*)malloc(x-start+1);
    strncpy(filename,start,x-start);
    filename[x-start] = '\0';

    char *mangled = (char*)malloc(plus-space-1);
    strncpy(mangled,space+1,plus-space-2);
    mangled[plus-space-2] = '\0';

    size_t offset = strtol(plus+2, NULL, 10);
    addr = strtol(x+3, NULL, 16);
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
#endif


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

Rail<String*>* CheckedThrowable::getStackTrace() {
    if (NULL == FMGL(cachedStackTrace)) {
        #if defined(__GLIBC__) || defined(__APPLE__)
        if (FMGL(trace_size) <= 0) {
            const char *msg = "No stacktrace recorded.";
            FMGL(cachedStackTrace) = Rail<String*>::_make(1);
            FMGL(cachedStackTrace)->__set(0, String::Lit(msg));
        } else {
            Rail<String*>* rail = Rail<String*>::_make(FMGL(trace_size));
            char **messages = ::backtrace_symbols(FMGL(trace), FMGL(trace_size));
            for (int i=0 ; i<FMGL(trace_size) ; ++i) {
                char *filename; char *symbol; size_t addr;
                extract_frame_ct(messages[i],filename,symbol,addr);
                char *msg = symbol;
                rail->__set(i, String::Lit(msg));
                ::free(msg);
                ::free(filename);
            }
            ::free(messages); // malloced by backtrace_symbols
            FMGL(cachedStackTrace) = rail;
        }
        #elif defined(_AIX)
        if (FMGL(trace_size) <= 0) {
            const char *msg = "No stacktrace recorded.";
            FMGL(cachedStackTrace) = Rail<String*>::_make(1);
            FMGL(cachedStackTrace)->__set(0, String::Lit(msg));
        } else {
			// build up a fake stack from our saved addresses
			// the fake stack doesn't need anything more than back-pointers and enough offset to hold the frame references
			unsigned long* fakeStack = (unsigned long *)malloc((FMGL(trace_size)+1) * 3 * sizeof(unsigned long)); // pointer, junk, link register, junk, junk, junk
			long i;
			for (i=0; i<FMGL(trace_size); i++)
			{
				fakeStack[i*3] = (unsigned long)&(fakeStack[(i+1)*3]);
				fakeStack[i*3+1] = 0xdeadbeef;
				fakeStack[i*3+2] = (unsigned long)FMGL(trace)[i];
			}
			fakeStack[i*3] = 0;

			// manipulate the existing stack to point to our fake stack
			unsigned long stackPointer;
			#if defined(_LP64)
				__asm__ __volatile__ ("std 1, %0 \n\t" : "=m" (stackPointer));
			#else
				__asm__ __volatile__ ("stw 1, %0 \n\t" : "=m" (stackPointer));
			#endif

			unsigned long originalStackPointer = stackPointer;
			*((unsigned long*)stackPointer) = (unsigned long)fakeStack; // this line overwrites the back chain pointer in the stack to the fake one.

			// call the original slow backtrace method to convert the offsets into text
			// this overwrites the contents of "trace" and value of "trace_size", which are no longer needed.
			// We add one to the limit becasue we left 1 extra frame in the code above, are
			// our fake stack has one extra frame in it (relative to the original).
			FMGL(trace_size) = ::backtrace(FMGL(trace), (FMGL(trace_size)+1)*sizeof(void*));
			
			// replace the stack frame pointer to point to the real stack again
			*((unsigned long*)stackPointer) = originalStackPointer;

			// delete the fake stack, which is no longer needed
			free(fakeStack);

			// from here on down, proceed as before
            Rail<String*>* rail = Rail<String*>::_make(FMGL(trace_size));
			char *msg;
			for (int i=0 ; i<FMGL(trace_size) ; ++i) {
				char* s = (char*)FMGL(trace)[i];
				char* c = strstr(s, " : ");
				if (c == NULL) {
					rail->__set(i, String::Lit("???????"));
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
				rail->__set(i, String::Lit(msg));
				::free(msg);

			}
			FMGL(cachedStackTrace) = rail;
        }
    #else
        const char *msg = "Detailed stacktraces not supported on this platform.";
        FMGL(cachedStackTrace) = Rail<String*>::_make(1);
        FMGL(cachedStackTrace)->__set(0, String::Lit(msg));
    #endif
    }

    return FMGL(cachedStackTrace);
}

void CheckedThrowable::printStackTrace() {
    fprintf(stderr, "%s\n", this->toString()->c_str());
    Rail<String*>* trace = this->getStackTrace();
    for (int i = 0; i < trace->FMGL(size); ++i)
        fprintf(stderr, "\tat %s\n", trace->__apply(i)->c_str());
    CheckedThrowable* cause = FMGL(cause);
    if (NULL != cause) {
        fprintf(stderr, "Caused by: ");
        cause->printStackTrace();
    }
}

void CheckedThrowable::printStackTrace(x10::io::Printer* printer) {
    printer->println(class_cast<Any*,String*>(toString()));
    Rail<String*>* trace = this->getStackTrace();
    String* atStr = String::Lit("\tat ");
    for (int i=0 ; i<trace->FMGL(size) ; ++i) { 
        printer->print(atStr);
        printer->println(class_cast<Any*,String*>(trace->__apply(i)));
    }
    CheckedThrowable* cause = FMGL(cause);
    if (NULL != cause) {
        printer->print(x10::lang::String::Lit("Caused by: "));
        cause->printStackTrace(printer);
    }
}

RTT_CC_DECLS0(CheckedThrowable, "x10.lang.CheckedThrowable", RuntimeType::class_kind)

// vim:tabstop=4:shiftwidth=4:expandtab
