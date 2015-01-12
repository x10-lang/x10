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

#ifndef X10AUX_SERIALIZATION_H
#define X10AUX_SERIALIZATION_H

/* --------------------- 
 * Serialization support
 * ---------------------
 *
 * There are three separate mechanisms for (de)serialization --
 *
 * 1) Built-in primitives
 *
 * 2) Structs
 *
 * 3) Instances of T* (Classes, boxed structs, closures)
 *
 *
 * The mechanism (1) copies raw implementation-dependent bytes to/from the stream and we will
 * discuss it no further.
 *
 *
 * The mechanism (2) invokes a static function S::_serialize to write the bytes to the stream,
 * and a static function S::_deserialize to read bytes from the stream and produce a S.
 * These functions must be generated (or hand-written) for every struct S.
 *
 *
 * The mechanism (3) has two subcases:
 *   the value is null
 *   the value is non-null
 * Null values are indicated by the serialization id 0.
 * Non-null values are indicated by writing a non-zero serialization id,
 * followed by a serialization of the fields (if any) of the value generated
 * by recursively following this algorithm. The serialization id is used to
 * dispatch to a function that will create an object of the appropriate concrete type
 * and then deserialize its fields.
 *
 * There is an additional complication of breaking cyclic object graphs by indicating
 * that we are about to read an object that has already been read.  This is indicated
 * by the serialization id 0xFFFF followed by an offset (in objects) to the
 * object which has been repeatedly serialized.
 *
 * Serialization ids are generated at runtime in a place-independent fashion.  Classes obtain their id by
 * registering a deserialization function with DeserializationDispatcher at initialization time, and
 * storing this id in a static field.  Every non-abstract class and every closure has its own id.
 * The virtual _get_serialization_id function returns this id for writing into the buffer.
 *
 * To write data (of any kind) we use the method serialization_buffer::write(data) which does the
 * right thing, no matter which of the 3 methods applies.
 * An internal cursor is incremented ready for the next write().  Note that a class's
 * _serialize_body function should also serialize its super class's representation, e.g. by
 * deferring to the super class's _serialize_body function.
 *
 * Deserialization is more complicated as an object has to be constructed.  In case (3) 
 * the DeserializationDispatcher is invoked to read an id from the stream and decide what to do.
 * Note that in such cases, the value has always been serialized from a matching variable on the sending side,
 * so an id will always be present.  During initialization time, classes register deserialization functions with the
 * DeserializationDispatcher, which hands out the unique ids.  Thus the DeserializationDispatcher
 * can look up the id in its internal table and dispatch to the appropriate static function which
 * will construct the right kind of object and initialize it by deserializing the object's
 * representation from the stream (provided as a serialization_buffer).
 *
 * Classes define a _deserialize_body function that extracts the object's representation from the
 * stream.  The DeserializationDispatcher callback should usually just
 * create an object of the right type and then call _deserialize_body to handle the rest.  Arbitrary
 * data can be extracted from a serialization_buffer via its read<T>() function.  An internal cursor
 * is incremented so the buffer is ready for the next read().  This function will do the right thing
 * no matter what T is supplied.  Note that classes need to deserialize their parent class's
 * representation too, e.g. by calling their parent's _deserialize_body function.  The two functions
 * _serialize_body and _deserialize_body are dual, and obviously they should be written to match
 * each other.
 *
 * Classes, closures, and boxed structs must call buf.record_reference(R) on the deserialization buffer
 * buf right after allocating the object in the DeserializationDispatcher callback
 * (where R is the newly allocated object).
 */

#include <x10aux/serialization_buffer.h>
#include <x10aux/deserialization_buffer.h>
#include <x10/lang/RuntimeNatives.h>

namespace x10 { namespace lang { class Runtime__Profile; } }

namespace x10aux {

    void raiseSerializationProtocolError();
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
