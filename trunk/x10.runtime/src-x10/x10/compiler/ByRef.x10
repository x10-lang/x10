/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

import x10.lang.annotations.ClassAnnotation;

/** An annotation on a struct that instructs the C++ backend to pass instances
 * of this struct by reference and not value.
 * @author Dave Cunningham
 */
public interface ByRef extends ClassAnnotation { }
