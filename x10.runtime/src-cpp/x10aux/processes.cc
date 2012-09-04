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

#include <x10aux/processes.h>
#include <x10/io/IOException.h>
#include <x10/io/Reader.h>
#include <x10/io/Writer.h>
#include <x10/io/FileReader__FileInputStream.h>
#include <x10/io/FileWriter__FileOutputStream.h>
#include <x10/io/InputStreamReader.h>
#include <x10/io/InputStreamReader__InputStream.h>
#include <x10/io/OutputStreamWriter.h>
#include <x10/io/OutputStreamWriter__OutputStream.h>

#include <stdlib.h>
#include <stdio.h>

x10::io::Reader* x10aux::processes::execForRead(const char *command) {
    FILE* inFd = popen(command, "r");
#ifndef NO_EXCEPTIONS
    if (inFd == NULL) {
        x10::lang::String* s = x10::lang::String::__plus(x10aux::string_utils::lit("execForRead: "), x10aux::string_utils::lit(command));
        throwException(x10::io::IOException::_make(s));
    }
#endif
    x10::io::InputStreamReader__InputStream* in_ = new (x10aux::alloc<x10::io::FileReader__FileInputStream>()) x10::io::FileReader__FileInputStream(inFd);
    x10::io::Reader* inReader_ = x10::io::InputStreamReader::_make(in_);
    return inReader_;
}

x10::io::Writer* x10aux::processes::execForWrite(const char *command) {
    FILE* outFd = popen(command, "w");
#ifndef NO_EXCEPTIONS
    if (outFd == NULL) {
        x10::lang::String* s = x10::lang::String::__plus(x10aux::string_utils::lit("execForWrite: "), x10aux::string_utils::lit(command));
        throwException(x10::io::IOException::_make(s));
    }
#endif
    x10::io::OutputStreamWriter__OutputStream* out_ = new (x10aux::alloc<x10::io::FileWriter__FileOutputStream>()) x10::io::FileWriter__FileOutputStream(outFd);
    x10::io::Writer* outWriter_ = x10::io::OutputStreamWriter::_make(out_);
    return outWriter_;
}

// vim:tabstop=4:shiftwidth=4:expandtab
