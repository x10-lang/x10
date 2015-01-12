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

#include <x10/io/FileReader__FileInputStream.h>

#include <x10/lang/Rail.h>
#include <x10/io/EOFException.h>
#include <x10/io/FileNotFoundException.h>
#include <x10/io/NotSerializableException.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

FileReader__FileInputStream*
FileReader__FileInputStream::_make(x10::lang::String* name) {
    FileReader__FileInputStream* this_ = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream ();
    this_->_constructor(name);
    return this_;
}

void FileReader__FileInputStream::_constructor(x10::lang::String* name) {
    this->InputStreamReader__InputStream::_constructor();

    const char *filename = name->c_str();
    this->FMGL(file) = fopen(filename, "r");
    if (NULL == this->FMGL(file))
        throwException(FileNotFoundException::_make(name));
}

void FileReader__FileInputStream::_constructor(FILE* file) {
    this->InputStreamReader__InputStream::_constructor();
    FMGL(file) = file;
}

char * FileReader__FileInputStream::gets(char *buf, int sz) {
    return ::fgets(buf, sz, FMGL(file));
}

void FileReader__FileInputStream::close() {
    ::fclose(FMGL(file));
}

x10_int FileReader__FileInputStream::read() {
    int c = ::fgetc(FMGL(file));
    return (x10_int)c;
}

x10_int FileReader__FileInputStream::read(x10::lang::Rail<x10_byte>* b,
                                          x10_int off,
                                          x10_int len) {
    int res = ::fread(&b->raw[off], sizeof(x10_byte), len*sizeof(x10_byte), FMGL(file));
    return (x10_int)res;
}

x10::lang::String* FileReader__FileInputStream::readLine() {
    char *buf = NULL;
    size_t bufLen = 0;
    ssize_t rc = getline(&buf, &bufLen, FMGL(file));
    if (-1 == rc) {
        if (feof(FMGL(file))) {
            x10aux::throwException<x10::io::EOFException>();
        } else {
            x10aux::throwException<x10::io::IOException>();
        }
    }
    // Null trailing '\n'
    size_t len = strlen(buf);
    buf[len-1] = '\0';
    x10::lang::String* ans = x10::lang::String::_make(buf, false);
    x10aux::system_dealloc(buf);
    return ans;
}

void FileReader__FileInputStream::skip(x10_long bytes) {
    ::fseek(FMGL(file), bytes, SEEK_CUR);
}

long FileReader__FileInputStream::offset() {
    return ::ftell(FMGL(file));
}

const x10aux::serialization_id_t FileReader__FileInputStream::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(FileReader__FileInputStream::_deserializer);

void FileReader__FileInputStream::_serialize_body(x10aux::serialization_buffer& buf) {
    x10aux::throwException(NotSerializableException::_make(String::Lit("FileReader.FileInputStream")));
}

void FileReader__FileInputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    // Should be unreachable, since serialize_body throws an exception.
    assert(false);
}

x10::lang::Reference* FileReader__FileInputStream::_deserializer(x10aux::deserialization_buffer& buf) {
    // Should be unreachable, since serialize_body throws an exception.
    assert(false);
}


RTT_CC_DECLS1(FileReader__FileInputStream, "x10.io.FileReader.FileReader__FileInputStream", RuntimeType::class_kind, InputStreamReader__InputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
