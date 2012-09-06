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

#include <stdlib.h>
#include <stdio.h>

using namespace x10::lang;
using namespace x10::io;

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
