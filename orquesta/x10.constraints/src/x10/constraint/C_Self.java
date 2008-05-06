/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;

public interface C_Self extends C_Root {
	public static final C_Self Self = new C_Self_c();
}
