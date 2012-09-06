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


#include <cstdlib>
#include <cstdio>

#ifdef __bgp__
#ifndef DISABLE_CLOCK_GETTIME
#define DISABLE_CLOCK_GETTIME // seems to be broken on bgp
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
#else
#  include <sys/time.h>  // for gettimeofday (POSIX)
#endif

#include <x10/lang/RuntimeNatives.h>

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
#ifndef NO_EXCEPTIONS
    // Cannot do ::exit here, as we'll need to clean up.
    // We need not worry about user code catching the int
    // because such a catch block can only be generated
    // by us.
    throw (int)code;
#else
    // No choice here: die without cleanup.
    ::exit((int)code);
#endif
}

x10_long RuntimeNatives::currentTimeMillis() {
#ifdef DISABLE_CLOCK_GETTIME
    struct ::timeval tv;
    gettimeofday(&tv, NULL);
    return (x10_long)(tv.tv_sec * 1000LL + tv.tv_usec / 1000);
#else
    struct ::timespec ts;
    ::clock_gettime(CLOCK_X10, &ts);
    return (x10_long)(ts.tv_sec * 1000LL + ts.tv_nsec / 1000000);
#endif
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

void RuntimeNatives::println(const char *msg) {
    fprintf(stderr, "%s\n", msg);
}

Reader* RuntimeNatives::execForRead(const char *command) {
    FILE* inFd = popen(command, "r");
#ifndef NO_EXCEPTIONS
    if (inFd == NULL) {
        String* s = String::__plus(String::Lit("execForRead: "), String::Lit(command));
        x10aux::throwException(IOException::_make(s));
    }
#endif
    InputStreamReader__InputStream* in_ = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream(inFd);
    Reader* inReader_ = InputStreamReader::_make(in_);
    return inReader_;
}

Writer* RuntimeNatives::execForWrite(const char *command) {
    FILE* outFd = popen(command, "w");
#ifndef NO_EXCEPTIONS
    if (outFd == NULL) {
        String* s = String::__plus(String::Lit("execForWrite: "), String::Lit(command));
        x10aux::throwException(IOException::_make(s));
    }
#endif
    OutputStreamWriter__OutputStream* out_ = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(outFd);
    Writer* outWriter_ = OutputStreamWriter::_make(out_);
    return outWriter_;
}

// vim:tabstop=4:shiftwidth=4:expandtab
