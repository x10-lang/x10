/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

import x10.lang.annotations.ExpressionAnnotation;
import x10.lang.annotations.StatementAnnotation;

/** An annotation that requests the compiler to stack allocate an object
 * must be used *exactly* as the following for now:
 *   @StackAllocate val v = @StackAllocate new T(...);
 * EXPERIMENTAL
 * @author Olivier Tardieu
 */
public interface StackAllocate extends ExpressionAnnotation,StatementAnnotation { }
