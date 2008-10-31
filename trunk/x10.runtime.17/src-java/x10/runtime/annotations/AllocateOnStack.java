/*
 *
 * (C) Copyright IBM Corporation 2007-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Empty annotation indicating that any instatiation of an annotated class 
 * should be allocated locally, on the stack, if at all possible.
 * The user essentially guarantees that no instantiation of this
 * class will ever escape.  To be used in very controlled and limited
 * instances.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AllocateOnStack { }

