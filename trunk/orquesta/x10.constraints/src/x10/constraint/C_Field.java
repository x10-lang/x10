/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;


/**
 * A representation of a Field.
 * @author vj
 *
 */
public interface C_Field extends C_Var {
	C_Var receiver();
	C_Name field();
}
