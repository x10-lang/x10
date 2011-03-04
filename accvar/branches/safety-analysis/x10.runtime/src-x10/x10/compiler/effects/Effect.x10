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
 * A statement is marked @Unsafe if is not safe.
 * @author vj
 */ 
public interface Effect(effect:Effects) extends StatementAnnotation { }
