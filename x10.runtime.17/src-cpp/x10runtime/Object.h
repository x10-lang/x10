/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/** XRX Lang Object Interface **/

#ifndef __XRX_OBJECT_H
#define __XRX_OBJECT_H

#include <Types.h>

namespace xrx_runtime {

class Object {
public:
	// constructor
	Object();

	// destructor
	~Object();

	// Indicates whether some object is equal to this one.
	boolean equals(const Object& obj);

	// Returns a hash code value for this object.
	int hashCode();

	// Returns a string representation of this object.
	String& toString();

	// Returns the runtime class name of this object.
	String& className();

private:
	// object hash code id
	int __object_id;
	// object name
	String *__object_name;
	// object class name
	String *__object_class_name;
};

} /* closing brace for namespace xrx_runtime */

#endif /* __XRX_OBJECT_H */
