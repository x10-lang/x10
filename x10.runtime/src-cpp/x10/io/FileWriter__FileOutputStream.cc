/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#include <x10/io/FileWriter__FileOutputStream.h>

#include <x10/lang/Rail.h>

#include <x10/io/FileNotFoundException.h>
#include <x10/io/NotSerializableException.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

FileWriter__FileOutputStream*
FileWriter__FileOutputStream::_make(x10::lang::String* name, bool append) {
    FileWriter__FileOutputStream* this_ = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream ();
    this_->_constructor(name, append);
    return this_;
}

void FileWriter__FileOutputStream::_constructor(x10::lang::String* name, bool append) {
    this->OutputStreamWriter__OutputStream::_constructor();

    const char *filename = name->c_str();
    FMGL(file) = fopen(filename, (append ? "a" : "w"));
    if (NULL == FMGL(file))
        throwException(FileNotFoundException::_make(name));
}

void FileWriter__FileOutputStream::_constructor(FILE* file) {
    this->OutputStreamWriter__OutputStream::_constructor();
    FMGL(file) = file;
}

void FileWriter__FileOutputStream::write(const char *str) {
    ::fprintf(FMGL(file), "%s", str);
}

void FileWriter__FileOutputStream::write(x10_int i) {
    ::fputc((char)i, FMGL(file));
}

void FileWriter__FileOutputStream::write(x10::lang::String* s) {
    x10aux::nullCheck(s);
    ::fwrite(s->c_str(), sizeof(char), s->length(), FMGL(file));
}

void FileWriter__FileOutputStream::write(x10::lang::Rail<x10_byte>* b, x10_long off, x10_long len) {
    ::fwrite(&b->raw[off], sizeof(x10_byte), len, FMGL(file));
}

void FileWriter__FileOutputStream::flush() {
    ::fflush(FMGL(file));
}

void FileWriter__FileOutputStream::close() {
    ::fclose(FMGL(file));
}

const x10aux::serialization_id_t FileWriter__FileOutputStream::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(FileWriter__FileOutputStream::_deserializer);

void FileWriter__FileOutputStream::_serialize_body(x10aux::serialization_buffer& buf) {
    x10aux::throwException(NotSerializableException::_make(String::Lit("FileWriter.FileOutputStream")));
}

void FileWriter__FileOutputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    // Should be unreachable, since serialize_body throws an exception.
    assert(false);
}

Reference* FileWriter__FileOutputStream::_deserializer(x10aux::deserialization_buffer& buf) {
    // Should be unreachable, since serialize_body throws an exception.
    assert(false);
}

RTT_CC_DECLS1(FileWriter__FileOutputStream, "x10.io.FileWriter.FileOutputStream", RuntimeType::class_kind, OutputStreamWriter__OutputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
