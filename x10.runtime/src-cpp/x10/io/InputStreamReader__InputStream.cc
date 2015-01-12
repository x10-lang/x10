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

#include <x10aux/alloc.h>

#include <x10/io/InputStreamReader__InputStream.h>
#include <x10/io/FileReader__FileInputStream.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;

static InputStreamReader__InputStream* _STANDARD_IN_cache = NULL;

InputStreamReader__InputStream* InputStreamReader__InputStream::STANDARD_IN()
{
	if (NULL == _STANDARD_IN_cache)
		_STANDARD_IN_cache = new (x10aux::alloc<FileReader__FileInputStream>()) FileReader__FileInputStream(stdin);
    return _STANDARD_IN_cache;
}

x10_int InputStreamReader__InputStream::read(x10::lang::Rail<x10_byte>* b) {
    return this->read(b, 0, b->FMGL(size));
}

x10_int InputStreamReader__InputStream::read(x10::lang::Rail<x10_byte>* b,
                                             x10_int off, x10_int len) {
    x10_int val;
    x10_int i;
    for (i = 0; i < len && (val = this->read()) != -1; i++)
        b->raw[off + i] = (x10_byte) (val & 0xFF);
    return i;
}

void InputStreamReader__InputStream::_serialize_body(x10aux::serialization_buffer& buf) {
}

void InputStreamReader__InputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
}

RTT_CC_DECLS0(InputStreamReader__InputStream, "x10.io.InputStreamReader.InputStream", RuntimeType::class_kind)

// vim:tabstop=4:shiftwidth=4:expandtab
