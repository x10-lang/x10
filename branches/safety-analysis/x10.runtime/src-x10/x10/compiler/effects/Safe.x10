/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler.effects;

import x10.lang.annotations.StatementAnnotation;

/**
 * Annotation to mark classes as having a particular native representation.
 * lang is the name of the language, typically "java" or "c".
 * type is the type of the native representation of T.
 * boxedType is the type the native representation of Box[T] (note: for java, Box[int] != Integer).  This should be non-null for value types.  For class types it's not examined.
 * rtt is an expression that returns the runtime type for T.
 */ 
public interface Safe extends StatementAnnotation { }
