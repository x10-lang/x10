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

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

x10aux::ref<FileWriter__FileOutputStream> FileWriter__FileOutputStream::STANDARD_OUT
    = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(stdout);

x10aux::ref<FileWriter__FileOutputStream> FileWriter__FileOutputStream::STANDARD_ERR
    = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(stderr);

x10aux::ref<FileWriter__FileOutputStream>
FileWriter__FileOutputStream::_make(x10aux::ref<x10::lang::String> name) {
    ref<FileWriter__FileOutputStream> this_ = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream (x10aux::io::FILEPtrStream::open_file(name, "w"));
    this_->OutputStreamWriter__OutputStream::_constructor();
    return this_;
}

const x10aux::serialization_id_t FileWriter__FileOutputStream::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(FileWriter__FileOutputStream::_deserializer<x10::lang::Object>);

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

RTT_CC_DECLS1(FileWriter__FileOutputStream, "x10.io.FileWriter.FileOutputStream", OutputStreamWriter__OutputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
