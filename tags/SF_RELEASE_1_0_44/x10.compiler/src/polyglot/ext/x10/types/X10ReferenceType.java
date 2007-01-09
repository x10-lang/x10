/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 *
 */
package polyglot.ext.x10.types;

import polyglot.types.ReferenceType;

/** An X10ReferenceType is an X10Type, and a ReferenceType. 
 * At some point the distinction between X10PrimitiveType and X10ReferenceType should
 * be eliminated, since X10 (unlike Java) does not make a distinction between the two.
 * But for now its convenient to consider those X10 value classes "primitive" which are
 * implemented via Java's primitive types, i.e. char, boolean, int, byte etc.
 * 
 * @author vj
 *
 * 
 */
public interface X10ReferenceType extends X10Type, ReferenceType {

}
