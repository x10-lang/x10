/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2012.
 */

#include <x10aux/config.h>

#include <cstdlib>
#include <cstdio>

#ifdef __bg__
#ifndef DISABLE_CLOCK_GETTIME
#define DISABLE_CLOCK_GETTIME // clock_gettime not supported by BlueGene CNK
#endif
#endif

#ifndef DISABLE_CLOCK_GETTIME
#  if !defined(_POSIX_TIMERS) || _POSIX_TIMERS <= 0
#    define DISABLE_CLOCK_GETTIME
#  endif
#endif
#ifndef DISABLE_CLOCK_GETTIME
#  include <time.h>  // for clock_gettime (optional POSIX)
#  if defined(_POSIX_MONOTONIC_CLOCK) && _POSIX_MONOTONIC_CLOCK >= 0
#    define CLOCK_X10 CLOCK_MONOTONIC
#  else
#    define CLOCK_X10 CLOCK_REALTIME
#  endif
#endif
#include <sys/time.h>  // for gettimeofday (POSIX)

#ifdef __GNU_LIBRARY__ 
#include <sys/sysinfo.h> // for get_nprocs
#endif

#include <strings.h>

#ifdef __MACH__
#include <crt_externs.h>
#endif

#include <x10/lang/RuntimeNatives.h>

#include <x10/util/HashMap.h>

#include <x10/io/IOException.h>
#include <x10/io/Reader.h>
#include <x10/io/Writer.h>
#include <x10/io/FileReader__FileInputStream.h>
#include <x10/io/FileWriter__FileOutputStream.h>
#include <x10/io/InputStreamReader.h>
#include <x10/io/InputStreamReader__InputStream.h>
#include <x10/io/OutputStreamWriter.h>
#include <x10/io/OutputStreamWriter__OutputStream.h>

using namespace x10::lang;
using namespace x10::io;


void RuntimeNatives::exit(x10_int code) {
	// exit now, using this exit code
	::exit(code);
}

x10_long RuntimeNatives::currentTimeMillis() {
    struct ::timeval tv;
    gettimeofday(&tv, NULL);
    return (x10_long)(tv.tv_sec * 1000LL + tv.tv_usec / 1000);
}

x10_long RuntimeNatives::nanoTime() {
#ifdef DISABLE_CLOCK_GETTIME
    struct ::timeval tv;
    gettimeofday(&tv, NULL);
    return (x10_long)(tv.tv_sec * 1000000000LL + tv.tv_usec * 1000LL);
#else
    struct ::timespec ts;
    ::clock_gettime(CLOCK_X10, &ts);
    return (x10_long)(ts.tv_sec * 1000000000LL + ts.tv_nsec);
#endif
}

x10_int RuntimeNatives::availableProcessors() {
    x10_int numProcs = -1; // -1 means we don't know.

#ifdef __GNU_LIBRARY__ 
    numProcs = get_nprocs();
#endif

#if defined(_SC_NPROCESSORS_ONLN)
    if (numProcs < 0) {
        numProcs = sysconf(_SC_NPROCESSORS_ONLN);
    }
#endif

    if (numProcs < 1) {
        // Oh well, we don't know, so maintain the X10 2.4.3 and older
        // behavior and just say it is 1.
        numProcs = 1;
    }

    return numProcs;
}


::x10::lang::String* RuntimeNatives::timeToString(long seconds) {
    time_t t = static_cast<const time_t>(seconds);
    struct tm *timeinfo = localtime(&t);
    char buffer[80];
    strftime(buffer, 80, "%a %b %d %H:%M:%S %Z %Y", timeinfo);
    return ::x10aux::makeStringLit(buffer);
}

void RuntimeNatives::println(const char *msg) {
    fprintf(stderr, "%s\n", msg);
}

Reader* RuntimeNatives::execForRead(const char *command) {
    FILE* inFd = popen(command, "r");
    if (inFd == NULL) {
        String* s = String::__plus(String::Lit("execForRead: "), String::Lit(command));
        x10aux::throwException(IOException::_make(s));
    }
    InputStreamReader__InputStream* in_ = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream(inFd);
    Reader* inReader_ = InputStreamReader::_make(in_);
    return inReader_;
}

Writer* RuntimeNatives::execForWrite(const char *command) {
    FILE* outFd = popen(command, "w");
    if (outFd == NULL) {
        String* s = String::__plus(String::Lit("execForWrite: "), String::Lit(command));
        x10aux::throwException(IOException::_make(s));
    }
    OutputStreamWriter__OutputStream* out_ = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(outFd);
    Writer* outWriter_ = OutputStreamWriter::_make(out_);
    return outWriter_;
}

#ifndef __MACH__
    extern char **environ;
#endif

x10::util::HashMap<x10::lang::String*,x10::lang::String*>* RuntimeNatives::loadenv() {
#ifdef __MACH__
    char** environ = *_NSGetEnviron();
#endif
    x10::util::HashMap<x10::lang::String*,x10::lang::String*>* map =
        x10::util::HashMap<x10::lang::String*, x10::lang::String*>::_make();
    for (unsigned i=0 ; environ[i]!=NULL ; ++i) {
        char *var = x10aux::alloc_utils::strdup(environ[i]);
        *strchr(var,'=') = '\0';
        char* val = getenv(var);
        assert(val!=NULL);
//        fprintf(stderr, "Loading environment variable %s=%s\n", var, val);
        map->put(x10::lang::String::Lit(var), x10::lang::String::Lit(val));
    }
    return map;
}

// vim:tabstop=4:shiftwidth=4:expandtab
