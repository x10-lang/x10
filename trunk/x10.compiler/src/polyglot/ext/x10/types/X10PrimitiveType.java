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
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import polyglot.types.ClassType;
import polyglot.types.PrimitiveType;

/**
 * @author vj
 *
 */
public interface X10PrimitiveType extends PrimitiveType, /* ClassType,*/ X10NamedType {
   String typeName();
}
