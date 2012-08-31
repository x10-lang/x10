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

#include <x10/io/FileReader__FileInputStream.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

FileReader__FileInputStream*
FileReader__FileInputStream::_make(x10::lang::String* name) {
    FileReader__FileInputStream* this_ = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream ();
    this_->_constructor(name);
    return this_;
}

void FileReader__FileInputStream::_constructor(x10::lang::String* file) {
    this->InputStreamReader__InputStream::_constructor();
    x10aux::io::FILEPtrInputStream fpis(x10aux::io::FILEPtrStream::open_file(file, "r"));
    _inputStream = fpis;
}

void FileReader__FileInputStream::_constructor(FILE* file) {
    this->InputStreamReader__InputStream::_constructor();
    x10aux::io::FILEPtrInputStream fpis(file);
    _inputStream = fpis;
}

void FileReader__FileInputStream::_constructor() {
    this->InputStreamReader__InputStream::_constructor();
    x10aux::io::FILEPtrInputStream fpis(NULL);
    _inputStream = fpis;
}

x10_int FileReader__FileInputStream::read(x10::util::IndexedMemoryChunk<x10_byte> b,
                                          x10_int off,
                                          x10_int len) {
    return _inputStream.read(b, off, len);
}

const x10aux::serialization_id_t FileReader__FileInputStream::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(FileReader__FileInputStream::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void FileReader__FileInputStream::_serialize_body(x10aux::serialization_buffer& buf) {
    InputStreamReader__InputStream::_serialize_body(buf);
    // This class simply has no global state.
    // TODO: attempting to serialize _inputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    // assert(false);
    // buf.write(this->_inputStream);
}

void FileReader__FileInputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    InputStreamReader__InputStream::_deserialize_body(buf);
    // This class simply has no global state.
    // TODO: attempting to serialize _inputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    // assert(false);
    // _inputStream = buf.read<x10aux::io::FILEPtrInputStream>();
}

x10::lang::Reference* FileReader__FileInputStream::_deserializer(x10aux::deserialization_buffer& buf) {
    // TODO: attempting to serialize _outputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    // assert(false);
    FileReader__FileInputStream* this_ = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream(NULL);
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}


RTT_CC_DECLS1(FileReader__FileInputStream, "x10.io.FileReader.FileReader__FileInputStream", RuntimeType::class_kind, InputStreamReader__InputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
