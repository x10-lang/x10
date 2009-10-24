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

void FileWriter__FileOutputStream::_serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    OutputStreamWriter__OutputStream::_serialize_body(buf, m);
    // TODO: attempting to serialize _outputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    //       
    assert(false);
    //buf.write(this->_outputStream,m);
}

template<class __T> x10aux::ref<__T> FileWriter__FileOutputStream::_deserializer(x10aux::deserialization_buffer& buf) {
    // TODO: attempting to serialize _outputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    //       
    assert(false);
    // x10aux::ref<FileWriter__FileOutputStream> this_ = new (x10aux::alloc_remote<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream();
    // this_->_deserialize_body(buf);
    // return this_;
    return NULL;
}

void FileWriter__FileOutputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    OutputStreamWriter__OutputStream::_deserialize_body(buf);
    // TODO: attempting to serialize _outputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    //       
    assert(false);
    // _outputStream = buf.read<x10aux::io::FILEPtrOutputStream>();
}



RTT_CC_DECLS1(FileWriter__FileOutputStream, "x10.io.FileWriter.FileOutputStream", OutputStreamWriter__OutputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
