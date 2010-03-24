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
#include <x10/lang/ValRail.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10::io;
using namespace x10aux;


void OutputStreamWriter__OutputStream::write(ref<Rail<x10_byte> > b) {
    placeCheck(nullCheck(b));
    this->write(b, 0, b->x10__length);
}

void OutputStreamWriter__OutputStream::write(ref<Rail<x10_byte> > b,
                                             x10_int off, x10_int len) {
    if (len > 0) { placeCheck(nullCheck(b)); }
    for (x10_int i = 0; i < len; i++)
        this->write((x10_int) b->operator[](off + i));
}

void OutputStreamWriter__OutputStream::write(ref<ValRail<x10_byte> > b) {
    nullCheck(b);
    this->write(b, 0, b->x10__length);
}

void OutputStreamWriter__OutputStream::write(ref<ValRail<x10_byte> > b,
                                             x10_int off, x10_int len) {
    if (len > 0) nullCheck(b);
    for (x10_int i = 0; i < len; i++)
        this->write((x10_int) b->operator[](off + i));
}

void OutputStreamWriter__OutputStream::_serialize_body(x10aux::serialization_buffer& buf) {
    x10::lang::Object::_serialize_body(buf);
}

void OutputStreamWriter__OutputStream::_deserialize_body(x10aux::deserialization_buffer& buf) {
    x10::lang::Object::_deserialize_body(buf);
}

RTT_CC_DECLS1(OutputStreamWriter__OutputStream, "x10.io.OutputStreamWriter.OutputStream", Object)

// vim:tabstop=4:shiftwidth=4:expandtab
