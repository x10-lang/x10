/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

import x10.lang.annotations.StatementAnnotation;

/**
 * <tt>@Unroll</tt> may be used to annotate a loop.
 * The body of the loop will be unrolled the specified number of times.
 */
public interface Unroll(factor: Int) extends StatementAnnotation { }
