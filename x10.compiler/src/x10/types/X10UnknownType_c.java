/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType_c;
import polyglot.util.InternalCompilerError;

/**
 * Represents an unknown X10Type.
 * 
 * @author vj
 *
 */
public class X10UnknownType_c extends UnknownType_c  {
	private static final long serialVersionUID = 3589624600892810259L;

	protected X10UnknownType_c() {}

	public X10UnknownType_c(TypeSystem ts) { super(ts); }

	// Definition of methods required by X10Type
	public boolean isSafe() { return false; }
	public boolean isRooted() { return false; }
	public boolean isX10Struct() { return false; }
	public Flags flags() { return Flags.NONE; }
	public Type setFlags(Flags f) { return this; }
	public Type clearFlags(Flags f) { return this; }
	public String toString() { return  super.toString(); }
	public boolean equalsNoFlag(Type t2) { return this == t2;}
	public boolean permitsNull() { return true; }

	public boolean isClass() { return true; }
	public ClassType toClass() {
	    return ((X10TypeSystem_c)ts).createFakeClass(QName.make("<unknown>"), new SemanticException("Unknown class"));
	}
}
