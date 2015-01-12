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

#include <x10aux/config.h>

#include <x10/lang/String.h>

#include <x10/lang/Rail.h>

#include <x10/io/File.h>

#include <x10/io/File__NativeFile.h>

#include <x10aux/basic_functions.h>

#include <unistd.h>
#include <dirent.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/time.h>

#if defined(__FreeBSD__)
#include <sys/types.h>
#endif

#include <limits.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

File__NativeFile* File__NativeFile::_make(String* s) {
    return (new (x10aux::alloc<File__NativeFile>()) File__NativeFile())->_constructor(s);
}

void File__NativeFile::_serialize_body(serialization_buffer &buf) {
}

void File__NativeFile::_deserialize_body(deserialization_buffer& buf) {
}

const x10aux::serialization_id_t File__NativeFile::_serialization_id =
    x10aux::DeserializationDispatcher::addDeserializer(File__NativeFile::_deserializer);

Reference* File__NativeFile::_deserializer(x10aux::deserialization_buffer &buf) {
    File__NativeFile* this_ = new (x10aux::alloc<File__NativeFile>()) File__NativeFile();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

RTT_CC_DECLS0(File__NativeFile, "x10.io.File.NativeFile", RuntimeType::class_kind)

String* File__NativeFile::getAbsolutePath() {
    if (*path->c_str()=='/') // absolute
        return path;
    char* cwd = getcwd(NULL, _POSIX_PATH_MAX);
    if (cwd == NULL)
        return NULL;
    String* absPath = String::__plus(String::__plus(String::Lit(cwd), String::Lit("/")), path);
    free(cwd);
    return absPath;
}

String* File__NativeFile::getCanonicalPath() {
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
#elif defined (__FreeBSD__)
#   define STAT_TIME_SEC(type) st_##type##timespec.tv_sec
#   define STAT_TIME_NSEC(type) st_##type##timespec.tv_nsec
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

x10_boolean
File__NativeFile::del() {
	struct stat info;
	int status = ::stat(path->c_str(), &info);
	if (status != 0)
		return (x10_boolean) false;
	if(S_ISREG(info.st_mode))
		return (x10_boolean) (::remove(path->c_str()) == 0);
	else
		return (x10_boolean) (::rmdir(path->c_str()) == 0);
}

Rail<String*>*
File__NativeFile::list() {
	char sep = (char) (File::FMGL(SEPARATOR__get)().v);
	DIR* dir;

    if((dir = ::opendir(path->c_str())) == NULL) {
		return NULL;
    }

    x10::util::GrowableRail<String*>* gr = x10::util::GrowableRail<String*>::_make();
	struct dirent* de;
    while ((de = ::readdir(dir)) != NULL) {
        const char* fn = de->d_name;
        if ((strncmp(fn, ".", 1) != 0) && (strncmp(fn, "..", 1) != 0)){
            gr->add(String::Lit(fn));
        }
    }
    ::closedir(dir);

    return gr->toRail();
}

x10_boolean
File__NativeFile::mkdir() {
	mode_t mode = 0777;
	return (x10_boolean) (::mkdir(path->c_str(), mode) == 0);
}

x10_boolean
File__NativeFile::mkdirs(const char *s) {
	char sep = (char) (File::FMGL(SEPARATOR__get)().v);
	mode_t mode = 0777;
	if(::strlen(s) <= 0)
		return (x10_boolean) false;
	if(::strchr(s, sep) == NULL)
		return (x10_boolean) (::mkdir(s, mode) == 0);
	if(::mkdir(s, mode) == 0) {
		return (x10_boolean) true;
	} else {
		char *buf;
		buf = (char *)::malloc(sizeof(char) * (::strlen(s) + 1));
		::strcpy(buf, s);
		char *c = ::strrchr(buf, sep);
		*c = '\0';
		if(mkdirs(buf)) {
			::free(buf);
			return (x10_boolean) (::mkdir(s, mode) == 0);
		} else {
			::free(buf);
			return (x10_boolean) false;
		}
	}
}

x10_boolean
File__NativeFile::mkdirs() {
	return mkdirs(path->c_str());
}

x10_boolean
File__NativeFile::renameTo(File__NativeFile* dest) {
	return (x10_boolean) (rename(path->c_str(), dest->path->c_str()) == 0);
}

// vim:tabstop=4:shiftwidth=4:expandtab
