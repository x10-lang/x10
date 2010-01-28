/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;

/** An annotation that instructs the C++ backend to compile the block as a CUDA
 * kernel.  This implies that the block should be capable of running on a GPU,
 * which means restrictions on what language features are allowed.
 * @author Dave Cunningham
 */
public interface CUDA { }
