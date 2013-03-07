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

#include <x10aux/config.h>

#include <x10aux/alloc.h>

#include <x10/io/OutputStreamWriter__OutputStream.h>
#include <x10/io/FileWriter__FileOutputStream.h>
#include <x10/lang/Rail.h>

#include <x10/util/IndexedMemoryChunk.h> // TMP while work on Rail NativeRep

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

static OutputStreamWriter__OutputStream* _STANDARD_OUT_cache = NULL;
OutputStreamWriter__OutputStream* OutputStreamWriter__OutputStream::STANDARD_OUT()
{
	if (NULL == _STANDARD_OUT_cache)
		_STANDARD_OUT_cache = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(stdout);
	return _STANDARD_OUT_cache;
}

static OutputStreamWriter__OutputStream* _STANDARD_ERR_cache = NULL;
OutputStreamWriter__OutputStream* OutputStreamWriter__OutputStream::STANDARD_ERR()
{
	if (NULL == _STANDARD_ERR_cache)
		_STANDARD_ERR_cache = new (x10aux::alloc<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(stderr);
	return _STANDARD_ERR_cache;
}

void OutputStreamWriter__OutputStream::write(Rail<x10_byte>* b) {
    this->write(b, 0, b->FMGL(size));
}

void OutputStreamWriter__OutputStream::write(Rail<x10_byte>* b, x10_int off, x10_int len) {
    x10::util::IndexedMemoryChunk<x10_byte> raw = b->FMGL(raw);
    for (x10_int i = 0; i < len; i++)
        this->write((x10_int) raw->operator[](off + i));
}

void OutputStreamWriter__OutputStream::_serialize_body(x10aux::serialization_buffer& buf) {
}

void OutputStreamWriter__OutputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
}

RTT_CC_DECLS0(OutputStreamWriter__OutputStream, "x10.io.OutputStreamWriter.OutputStream", RuntimeType::class_kind)

// vim:tabstop=4:shiftwidth=4:expandtab
