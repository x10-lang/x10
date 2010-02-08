/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

import x10.lang.annotations.ExpressionAnnotation;

/** An annotation on "new" that requests the compiler to stack allocate the object
 * EXPERIMENTAL
 * @author Olivier Tardieu
 */
public interface StackAllocate extends ExpressionAnnotation { }
