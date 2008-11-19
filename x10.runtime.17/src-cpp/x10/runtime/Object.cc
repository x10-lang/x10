/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/** XRX Lang Object Implementation **/

#include <Object.h>

namespace xrx_runtime {

// constructor
Object::Object()
{
	// to do
}

// destructor
Object::~Object()
{
	// to do
}

// Indicates whether some object is equal to this one.
boolean
Object::equals(const Object& obj)
{
	if (__object_id == obj.__object_id) {
		return true;
	}
	return false;
}

// Returns a hash code value for this object.
int
Object::hashCode()
{
	return (__object_id);
}

// Returns a string representation of this object.
String&
Object::toString()
{
	return (*__object_name);
}

// Returns the runtime class name of this object.
String&
Object::className()
{
	return (*__object_class_name);
}

} /* closing brace for namespace xrx_runtime */
