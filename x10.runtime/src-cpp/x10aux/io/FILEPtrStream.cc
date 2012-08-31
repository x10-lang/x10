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

#include <x10aux/throw.h>

#include <x10aux/io/FILEPtrStream.h>
#include <x10/lang/String.h>
#include <x10/io/FileNotFoundException.h>

using namespace x10aux;
using namespace x10aux::io;
using namespace x10::lang;
using namespace x10::io;


FILE* FILEPtrStream::open_file(String* name, const char* mode) {
    const char *filename = name->c_str();
    FILE* res = fopen(filename, mode);
#ifndef NO_EXCEPTIONS
    if (res == NULL)
        throwException(FileNotFoundException::_make(name));
#endif
    return res;
}

void FILEPtrStream::close() {
    ::fclose(_stream);
}


FILE* FILEPtrStream::check_stream(FILE* stream) {
#ifndef NO_EXCEPTIONS
/* TODO
    if (stream == NULL) throwException(new (alloc<IOException>()) IOException(String("Null file pointer")));
*/
#endif
    return stream;
}


// vim:tabstop=4:shiftwidth=4:expandtab
