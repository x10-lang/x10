/*
 *
 * (C) Copyright IBM Corporation 2006-2008
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
public interface XField extends XVar {
	XVar receiver();
	XName field();
}
