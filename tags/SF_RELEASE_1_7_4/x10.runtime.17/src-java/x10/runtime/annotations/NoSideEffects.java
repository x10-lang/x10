/*
 *
 * (C) Copyright IBM Corporation 2006-2008
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
 * Empty annotation indicating that a given method has no observable side
 * effects.  Useful for annotating getter methods, some constructors, etc.
 * This is distinct from annotating a method as a pure function, since
 * pure function methods must always return the same result given the
 * same arguments.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NoSideEffects { }

