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

#ifndef X10RT_JNI_HELPERS_H
#define X10RT_JNI_HELPERS_H

#include <x10rt_front.h>

#define __int64 __int64_t
#include <jni.h>

#include <assert.h>

/*
 * Data structure used to cache the information needed to do a
 * JNI invocation up into the JVM
 */
typedef struct methodDesciption {
    jclass targetClass;
    jmethodID targetMethod;
} methodDescription;


/*
 * Use theJVM pointer we cached during initialization to acquire the JNIEnv* for the current thread
 */
extern JNIEnv* jniHelper_getEnv();

extern void initCachedJVM(JNIEnv*);

extern void jniHelper_abort(const char* format, ...);

extern void jniHelper_oom(JNIEnv*, const char* msg);

extern const char* X10_PAUSE_GC_ON_SEND;
    


// TODO: MessageReader and MessageWriter should probably be
//       provided by X10RT itself to enable sharing of one
//       optimized implementation of reading/writing values
//       from/to buffers in network order.

class MessageReader {
public:
    const x10rt_msg_params *msg;
    void *cursor;
#ifndef NDEBUG
    unsigned long bytesRead;
#endif    
    
    MessageReader(const x10rt_msg_params *msg_) : msg(msg_) {
        cursor = msg->msg;
#ifndef NDEBUG
        bytesRead = 0;
#endif
    }

    jboolean readJBoolean() {
#ifndef NDEBUG
        bytesRead += sizeof(jboolean);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jboolean ans = *((jboolean*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jboolean));
        return ans;
    }

    jint readJInt() {
#ifndef NDEBUG
        bytesRead += sizeof(jint);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jint ans = *((jint*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jint));
        return ans;
    }

    jfloat readJFloat() {
#ifndef NDEBUG
        bytesRead += sizeof(jfloat);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jfloat ans = *((jfloat*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jfloat));
        return ans;
    }

    jlong readJLong() {
#ifndef NDEBUG
        bytesRead += sizeof(jlong);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jlong ans = *((jlong*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jlong));
        return ans;
    }

    jdouble readJDouble() {
#ifndef NDEBUG
        bytesRead += sizeof(jdouble);
        assert(bytesRead <= msg->len);
#endif
        // TODO: Proper endian encoding/decoding
        jdouble ans = *((jdouble*)cursor);
        cursor = (void*) (((char*)cursor)+sizeof(jdouble));
        return ans;
    }

    void endianSwapIfNeeded(int numElems, int elemSizeInBytes) {
        // TODO: Proper endian encoding/decoding.
    }
    
};


class MessageWriter {
public:
    void *buffer;
    void *cursor;
    unsigned long size;
    unsigned long bytesWritten;
    
    MessageWriter(unsigned long initialSizeInBytes) : size(initialSizeInBytes) {
        buffer = malloc(size);
        cursor = buffer;
        bytesWritten = 0;
    }

    void writeJBoolean(jboolean val) {
        bytesWritten += sizeof(jboolean);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jboolean*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jboolean));
    }

    void writeJInt(jint val) {
        bytesWritten += sizeof(jint);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jint*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jint));
    }

    void writeJFloat(jfloat val) {
        bytesWritten += sizeof(jfloat);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jfloat*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jfloat));
    }

    void writeJLong(jlong val) {
        bytesWritten += sizeof(jlong);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jlong*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jlong));
    }

    void writeJDouble(jdouble val) {
        bytesWritten += sizeof(jdouble);
        assert(bytesWritten <= size);

        // TODO: Proper endian encoding/decoding
        *((jdouble*)cursor) = val;
        cursor = (void*) (((char*)cursor)+sizeof(jdouble));
    }

    void endianSwapIfNeeded(int numElems, int elemSizeInBytes) {
        // TODO: Proper endian encoding/decoding.
    }
};
    
#endif /* X10RT_JNI_HELPERS_H */

