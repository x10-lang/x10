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

#if defined(__GLIBC__) || defined(__APPLE__)
#define MAX_TRACE_SIZE 1024
#endif

CheckedThrowable* CheckedThrowable::fillInStackTrace() {
    if (FMGL(trace_size)>=0) return this;

#if defined(__GLIBC__) || defined(__APPLE__)
    void *buffer[MAX_TRACE_SIZE];

    int numFrames = (::backtrace(buffer, MAX_TRACE_SIZE)) - 1; // 1 frame smaller to cut out this "fillInStackTrace" method from the disalayed stack
    FMGL(trace) = x10aux::alloc<void*>(numFrames*sizeof(void*), false); // does not contain pointers to GC heap
    memcpy(FMGL(trace), &buffer[1], numFrames*sizeof(void*));
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
