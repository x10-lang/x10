/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import polyglot.types.Flags;
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
	protected X10UnknownType_c() {}

	public X10UnknownType_c(TypeSystem ts) { super(ts); }

	// Definition of methods required by X10Type
	public boolean isSafe() { return false; }
	public boolean isRooted() { return false; }
	public boolean isX10Struct() { return false; }
	public Flags flags() { return Flags.NONE; }
	public Type setFlags(Flags f) { return this; }
	public boolean isProto() { return false;}
	public Type clearFlags(Flags f) { return this; }
	public String toString() { return  super.toString(); }
	public boolean equalsNoFlag(Type t2) { return this == t2;}
	public boolean permitsNull() { return false; }
}
