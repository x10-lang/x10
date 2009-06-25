/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

/**
 * Represents a named X10Type. Such types are not anonymous.
 * Examples of such types are X10PrimitiveTypes, X10ParsedClassTypes, X10ArrayTypes.
 */
import polyglot.types.Named;

public interface X10NamedType extends Named, X10Type {
	
}
