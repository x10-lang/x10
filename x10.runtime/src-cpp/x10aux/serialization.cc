/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Australian National University 2013.
 */

#include <x10aux/config.h>

#include <x10aux/serialization.h>
#include <x10aux/network.h>

#include <x10/lang/Reference.h>
#include <x10/lang/Any.h>
#include <x10/lang/Rail.h>
#include <x10/xrx/Runtime__Profile.h>
#include <x10/io/Serializer.h>
#include <x10/io/SerializationException.h>

using namespace x10aux;
using namespace x10::lang;

const serialization_id_t x10aux::deserialization_buffer::CUSTOM_SERIALIZATION_END;

// used by simple_hashmap
x10_uint x10aux::simple_hash_code(const void* id) {
    // the >> 4 is to ensure aligned pointer hashes are both odd and even
    x10_ulong ptr = reinterpret_cast<uint64_t>(id) >> 4;
    x10_uint hash = ptr ^ (ptr >> 32);
    return hash;
}

void addr_map::_grow() {
    int newSize = _size << 1;
    _ptrs = x10aux::realloc(_ptrs, newSize*(sizeof(const void*)));
    _size = newSize;
}

void addr_map::_add(const void* ptr) {
    if (_top == _size) {
        _grow();
    }
    _ptrPos->put(ptr, _top);
    _ptrs[_top++] = ptr;
}

int addr_map::_find(const void* ptr) {
    int pos = _ptrPos->get(ptr); // 0 or NULL means ptr is not in the table
    return pos;
}

const void* addr_map::_get(int pos) {
    if (pos > _top || pos == 0)
        return NULL;
    return _ptrs[pos];
}

const void* addr_map::_set(int pos, const void* ptr) {
    if (pos > _top || pos == 0)
        return NULL;
    const void* old = _ptrs[pos];
    _ptrs[pos] = ptr;
    _ptrPos->put(ptr, pos);
    return old;
}

int addr_map::_position(const void* p) {
    int pos = _find(p);
    if (pos != 0) {
        return pos;
    }
    _add(p);
    return 0;
}

void serialization_buffer::_constructor(x10::io::OutputStreamWriter *os) {
	x10aux::throwUnsupportedOperationException("Haven't implemented Serializer(OutputStreamWriter) constructor for Native x10");
}

void serialization_buffer::grow (void) {
    size_t old_capacity = capacity();
    size_t new_capacity = (size_t) (old_capacity * 2.0); // increase capacity by a factor
    if (new_capacity<16) new_capacity = 16; // biggest primitive we might serialise -- a SIMD variable

    grow(new_capacity);
}

void serialization_buffer::grow (size_t new_capacity) {
    size_t new_length = length(); // no change in used portion of buffer
    
    buffer = (char*)x10aux::realloc(buffer, new_capacity);

    // update pointers to use (potentially) new buffer
    limit = buffer + new_capacity;
    cursor = buffer + new_length;
}

void serialization_buffer::serialize_reference(serialization_buffer &buf,
                                               x10::lang::Reference* this_) {
    if (NULL == this_) {
        _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET<<" to buf: "<<&buf);
        buf.write((x10aux::serialization_id_t)0);
    } else {
        x10aux::serialization_id_t id = this_->_get_serialization_id();
        _S_("Serializing id "<<id<<" of type "<< ANSI_SER<<ANSI_BOLD<<this_->_type()->name()<<" and address "<<(void*)(this_));
        buf.write(id);
        this_->_serialize_body(buf);
        _S_("Completed serialization of "<<(void*)(this_));
    }
}

Rail<x10_byte>* serialization_buffer::toRail() {
    Rail<x10_byte>* ans = Rail<x10_byte>::_makeUnsafe(length(), false);
    rail_copyRaw(buffer, ans->raw, length(), false);
    return ans;
}

void serialization_buffer::newObjectGraph() {
	x10aux::throwUnsupportedOperationException("Haven't implemented newObjectGraph for Native x10");
}

void serialization_buffer::addDeserializeCount(long extraCount) {
	// [GlobalGC] Nothing to do for Native X10 because GlobalGC is not implemented yet
}

void serialization_buffer::writeAny(x10::lang::Any *val) {
    write(val);
}

void deserialization_buffer::_constructor(x10::io::Serializer* ser) {
    cursor = buffer = ser->FMGL(__NATIVE_FIELD__)->borrow();
    len = ser->FMGL(__NATIVE_FIELD__)->length();
}

void deserialization_buffer::_constructor(x10::lang::Rail<x10_byte>*rail) {
    cursor = buffer = (char*)(rail->raw);
    len = rail->FMGL(size);
}

void deserialization_buffer::_constructor(x10::io::InputStreamReader *is) {
	x10aux::throwUnsupportedOperationException("Haven't implemented Deserializer(InputStreamReader) constructor for Native x10");
}

Reference* deserialization_buffer::deserialize_reference(deserialization_buffer &buf) {
    x10aux::serialization_id_t id = buf.read<x10aux::serialization_id_t>();
    if (id == 0) {
        _S_("Deserialized a "<<ANSI_SER<<ANSI_BOLD<<"null reference"<<ANSI_RESET);
        return NULL;
    } else {
        _S_("Deserializing non-null value with id "<<ANSI_SER<<ANSI_BOLD<<id<<ANSI_RESET<<" from buf: "<<&buf);
        return x10aux::DeserializationDispatcher::create(buf, id);
    }
}

x10::lang::Any* deserialization_buffer::readAny() {
    x10::lang::Any *val = read<x10::lang::Any*>();
    return val;
}    

void x10aux::raiseSerializationProtocolError() {
    const char* msg = "Error detected in custom serialization protocol";
    throwException(x10::io::SerializationException::_make(String::Lit(msg)));
}

// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100

