#include <x10aux/config.h>

#include <x10/io/File__NativeFile.h>

#include <x10aux/basic_functions.h>

#include <unistd.h>
#include <stdio.h>
#include <sys/stat.h>
#include <sys/time.h>

#include <limits.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

x10aux::ref<File__NativeFile>
File__NativeFile::_make(x10aux::ref<String> s) {
    return (new (x10aux::alloc<File__NativeFile>()) File__NativeFile())->_constructor(s);
}

void File__NativeFile::_serialize_body(serialization_buffer &buf, addr_map &m) {
    this->Ref::_serialize_body(buf, m);
}

void File__NativeFile::_deserialize_body(deserialization_buffer& buf) {
    this->Ref::_deserialize_body(buf);
}

const x10aux::serialization_id_t File__NativeFile::_serialization_id =
    x10aux::DeserializationDispatcher::addDeserializer(File__NativeFile::_deserializer<Object>);

RTT_CC_DECLS1(File__NativeFile, "x10.io.File.NativeFile", Ref)

x10aux::ref<String>
File__NativeFile::getAbsolutePath() {
    if (*path->c_str()=='/') // absolute
        return path;
    char* cwd = getcwd(NULL, _POSIX_PATH_MAX);
    if (cwd == NULL)
        return x10aux::null;
    x10aux::ref<String> absPath = String::Lit(cwd) + String::Lit("/") + path;
    free(cwd);
    return absPath;
}

x10aux::ref<String>
File__NativeFile::getCanonicalPath() {
    assert(false); /* FIXME: STUBBED NATIVE */
    return getAbsolutePath();
}

x10_boolean
File__NativeFile::canRead() {
    int status = ::access(path->c_str(), R_OK);
    return (x10_boolean)(status == 0);
}

x10_boolean
File__NativeFile::canWrite() {
    int status = ::access(path->c_str(), W_OK);
    return (x10_boolean)(status == 0);
}

x10_boolean
File__NativeFile::exists() {
    int status = ::access(path->c_str(), F_OK);
    return (x10_boolean)(status == 0);
}

x10_boolean
File__NativeFile::isDirectory() {
    struct stat info;
    int status = ::stat(path->c_str(), &info);
    return (x10_boolean)(status == 0 && S_ISDIR(info.st_mode));
}

x10_boolean
File__NativeFile::isFile() {
    struct stat info;
    int status = ::stat(path->c_str(), &info);
    return (x10_boolean)(status == 0 && S_ISREG(info.st_mode));
}

x10_boolean
File__NativeFile::isHidden() {
    return *path->c_str()=='.';
}

#if defined (__APPLE__)
#   define STAT_TIME_SEC(type) st_##type##timespec.tv_sec
#   define STAT_TIME_NSEC(type) st_##type##timespec.tv_nsec
#elif defined (_AIX)
#   define STAT_TIME_SEC(type) st_##type##time
#   define STAT_TIME_NSEC(type) st_##type##time_n
#else
#   define STAT_TIME_SEC(type) st_##type##tim.tv_sec
#   define STAT_TIME_NSEC(type) st_##type##tim.tv_nsec
#endif

x10_long
File__NativeFile::lastModified() {
    struct stat info;
    int status = ::stat(path->c_str(), &info);
    return (x10_long)(status == 0 ? (info.STAT_TIME_SEC(m) * (x10_long)1000 + info.STAT_TIME_NSEC(m) / 1000000) : 0);
}

x10_boolean
File__NativeFile::setLastModified(x10_long t) {
    struct stat info;
    int status = ::stat(path->c_str(), &info);
    if (status != 0)
        return (x10_boolean) false;
    struct timeval times[2];
    times[0].tv_sec = info.STAT_TIME_SEC(a);
    times[0].tv_usec = info.STAT_TIME_NSEC(a) / 1000;
    times[1].tv_sec = t / 1000;
    times[1].tv_usec = (t % 1000) * 1000;
    status = utimes(path->c_str(), times);
    return (x10_boolean)(status == 0);
}

x10_long
File__NativeFile::length() {
    struct stat info;
    int status = ::stat(path->c_str(), &info);
    return (x10_long)(status == 0 ? info.st_size : 0);
}

// vim:tabstop=4:shiftwidth=4:expandtab
