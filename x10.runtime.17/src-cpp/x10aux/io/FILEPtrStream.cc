#include <x10aux/config.h>

#include <x10aux/io/FILEPtrStream.h>
#include <x10/lang/Rail.h>
#include <x10/lang/String.h>
#include <x10/io/FileNotFoundException.h>

using namespace x10aux;
using namespace x10aux::io;
using namespace x10::lang;
using namespace x10::io;


FILE* FILEPtrStream::open_file(const ref<String>& name, const char* mode) {
    const char *filename = ((const std::string&)(*name)).c_str();
    FILE* res = fopen(filename, mode);
#ifndef NO_EXCEPTIONS
    if (res == NULL)
        throw ref<FileNotFoundException>(
            new (x10aux::alloc<FileNotFoundException>())
                FileNotFoundException()); //TODO: name));
#endif
    return res;
}

void FILEPtrStream::close() {
    ::fclose(_stream);
}


FILE* FILEPtrStream::check_stream(FILE* stream) {
#ifndef NO_EXCEPTIONS
/* TODO
    if (stream == NULL) throw ref<IOException>(new (alloc<IOException>()) IOException(String("Null file pointer")));
*/
#endif
    return stream;
}


