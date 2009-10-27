#include <x10/io/FileReader__FileInputStream.h>

using namespace x10aux;
using namespace x10::lang;
using namespace x10::io;

x10aux::ref<FileReader__FileInputStream> FileReader__FileInputStream::STANDARD_IN
    = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream(stdin);

x10aux::ref<FileReader__FileInputStream>
FileReader__FileInputStream::_make(x10aux::ref<x10::lang::String> name) {
    ref<FileReader__FileInputStream> this_ = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream (x10aux::io::FILEPtrStream::open_file(name, "r"));
    this_->InputStreamReader__InputStream::_constructor();
    return this_;
}

const x10aux::serialization_id_t FileReader__FileInputStream::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(FileReader__FileInputStream::_deserializer<x10::lang::Ref>);

void FileReader__FileInputStream::_serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    InputStreamReader__InputStream::_serialize_body(buf, m);
    // This class simply has no global state.
    // TODO: attempting to serialize _inputStream is nonsensical.
    //       The old 1.7 definition of this class simply didn't work either,
    //       it just silently didn't serialize the FILEPtrInputSteam field.
    // assert(false);
    // buf.write(this->_inputStream,m);
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

RTT_CC_DECLS1(FileReader__FileInputStream, "x10.io.FileReader.FileReader__FileInputStream", InputStreamReader__InputStream)

// vim:tabstop=4:shiftwidth=4:expandtab
