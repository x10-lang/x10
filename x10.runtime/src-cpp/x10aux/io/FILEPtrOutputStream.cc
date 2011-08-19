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
#include <x10aux/ref.h>
#include <x10aux/alloc.h>

#include <x10aux/io/FILEPtrOutputStream.h>

#include <x10/util/IndexedMemoryChunk.h>
#include <x10/io/IOException.h>


using namespace x10aux;
using namespace x10aux::io;
using namespace x10::lang;
using namespace x10::io;

void FILEPtrOutputStream::_vprintf(const char* format, va_list parms) {
    ::vfprintf(_stream, format, parms);
}

void FILEPtrOutputStream::close() {
    ::fclose(_stream);
}

void FILEPtrOutputStream::flush() {
    ::fflush(_stream);
}

void FILEPtrOutputStream::write(x10::util::IndexedMemoryChunk<x10_byte> b,
                                x10_int off, x10_int len) {
    ::fwrite(((x10_byte*)b->raw())+off*sizeof(x10_byte), sizeof(x10_byte), len*sizeof(x10_byte), _stream);
}

void FILEPtrOutputStream::write(x10_int b) {
    ::fputc((char)b, _stream);
}

void FILEPtrOutputStream::write(const char* s) {
    ::fprintf(_stream, "%s", s);
}

// vim:tabstop=4:shiftwidth=4:expandtab
