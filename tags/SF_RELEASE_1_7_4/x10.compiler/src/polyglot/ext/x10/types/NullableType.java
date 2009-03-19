/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

/**
 * @author vj
 */
public interface NullableType extends X10ReferenceType, X10NamedType {
	X10NamedType base();
	NullableType base(X10NamedType base);
}

