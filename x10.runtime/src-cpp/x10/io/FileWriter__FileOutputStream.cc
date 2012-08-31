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

#include <x10/io/FileWriter__FileOutputStream.h>
#include <x10/util/IndexedMemoryChunk.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

FileWriter__FileOutputStream*
FileWriter__FileOutputStream::_make(x10::lang::String* name, bool append) {
    FileWriter__FileOutputStream* this_ = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream ();
    this_->_constructor(name, append);
    return this_;
}

void FileWriter__FileOutputStream::_constructor(x10::lang::String* file, bool append) {
    this->OutputStreamWriter__OutputStream::_constructor();
    x10aux::io::FILEPtrOutputStream fpos(x10aux::io::FILEPtrStream::open_file(file, (append ? "a" : "w")));
    _outputStream = fpos;
}

void FileWriter__FileOutputStream::_constructor(FILE* file) {
    this->OutputStreamWriter__OutputStream::_constructor();
    x10aux::io::FILEPtrOutputStream fpos(file);
    _outputStream = fpos;
}

void FileWriter__FileOutputStream::_constructor() {
    this->OutputStreamWriter__OutputStream::_constructor();
    x10aux::io::FILEPtrOutputStream fpos(NULL);
    _outputStream = fpos;
}

void FileWriter__FileOutputStream::write(x10::util::IndexedMemoryChunk<x10_byte> b, x10_int off, x10_int len) {
    _outputStream.write(b, off, len);
}

const x10aux::serialization_id_t FileWriter__FileOutputStream::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(FileWriter__FileOutputStream::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

void FileWriter__FileOutputStream::_serialize_body(x10aux::serialization_buffer& buf) {
    OutputStreamWriter__OutputStream::_serialize_body(buf);
    // This class simply has no global state.
    // TODO: attempting to serialize _outputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    // assert(false);
    // buf.write(this->_outputStream);
}

void FileWriter__FileOutputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    OutputStreamWriter__OutputStream::_deserialize_body(buf);
    // This class simply has no global state.
    // TODO: attempting to serialize _outputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    // assert(false);
    // _outputStream = buf.read<x10aux::io::FILEPtrOutputStream>();
}

Reference* FileWriter__FileOutputStream::_deserializer(x10aux::deserialization_buffer& buf) {
    // TODO: attempting to serialize _outputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    // assert(false);
    FileWriter__FileOutputStream* this_ = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

RTT_CC_DECLS1(FileWriter__FileOutputStream, "x10.io.FileWriter.FileOutputStream", RuntimeType::class_kind, OutputStreamWriter__OutputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
