/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.types;

/**
 * Represents a named X10Type. Such types are not anonymous.
 * Examples of such types are X10PrimitiveTypes, X10ParsedClassTypes, X10ArrayTypes.
 */
import polyglot.types.Named;
import polyglot.types.Type;

public interface X10NamedType extends Named, Type {
	
	
}
