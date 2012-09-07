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

#include <x10aux/io/FILEPtrInputStream.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/io/FileNotFoundException.h>

using namespace x10aux;
using namespace x10aux::io;
using namespace x10::lang;
using namespace x10::io;

x10_int x10aux::io::FILEPtrInputStream::read() {
    int c = ::fgetc(_stream);
    return (x10_int)c;
}

char* x10aux::io::FILEPtrInputStream::gets(char* s, int num) {
    return ::fgets(s, num, _stream);
}

x10_int x10aux::io::FILEPtrInputStream::read(x10::util::IndexedMemoryChunk<x10_byte> b,
                                             x10_int off, x10_int len) {

    int res = ::fread(((x10_byte*)b->raw())+off*sizeof(x10_byte),
                      sizeof(x10_byte),
                      len*sizeof(x10_byte),
                      _stream);
    return (x10_int)res;
}

void x10aux::io::FILEPtrInputStream::skip(x10_int bytes) {
    ::fseek(_stream, bytes, SEEK_CUR);
}

void x10aux::io::FILEPtrInputStream::close() {
    ::fclose(_stream);
}

FILE* FILEPtrInputStream::open_file(String* name, const char* mode) {
    const char *filename = name->c_str();
    FILE* res = fopen(filename, mode);
#ifndef NO_EXCEPTIONS
    if (res == NULL)
        throwException(FileNotFoundException::_make(name));
#endif
    return res;
}

// vim:tabstop=4:shiftwidth=4:expandtab
