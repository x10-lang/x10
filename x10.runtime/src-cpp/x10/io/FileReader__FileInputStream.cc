#include <x10/io/FileReader__FileInputStream.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

x10aux::ref<FileReader__FileInputStream> FileReader__FileInputStream::STANDARD_IN
    = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream(stdin);

x10aux::ref<FileReader__FileInputStream>
FileReader__FileInputStream::_make(x10aux::ref<x10::lang::String> name) {
    return new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream (x10aux::io::FILEPtrStream::open_file(name, "r"));
}

const x10aux::serialization_id_t FileReader__FileInputStream::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(FileReader__FileInputStream::_deserializer<x10::lang::Object>);

void FileReader__FileInputStream::_serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    InputStreamReader__InputStream::_serialize_body(buf, m);
    // TODO: attempting to serialize _inputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    //       
    assert(false);
    // buf.write(this->_inputStream,m);
}

template<class __T> x10aux::ref<__T> FileReader__FileInputStream::_deserializer(x10aux::deserialization_buffer& buf) {
    // TODO: attempting to serialize _outputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    //       
    assert(false);
    //    x10aux::ref<FileReader__FileInputStream> this_ = new (x10aux::alloc_remote<FileReader__FileInputStream>()) FileReader__FileInputStream();
    // this_->_deserialize_body(buf);
    // return this_;
    return NULL;
}

void FileReader__FileInputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    InputStreamReader__InputStream::_deserialize_body(buf);

    // TODO: attempting to serialize _inputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    //       
    assert(false);
    // _inputStream = buf.read<x10aux::io::FILEPtrInputStream>();
}

RTT_CC_DECLS1(FileReader__FileInputStream, "x10.io.FileReader.FileReader__FileInputStream", InputStreamReader__InputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
