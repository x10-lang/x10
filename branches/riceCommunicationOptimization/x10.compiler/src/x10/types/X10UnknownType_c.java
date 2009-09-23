/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import polyglot.types.Flags;
import polyglot.types.TypeSystem;
import polyglot.types.UnknownType_c;
import polyglot.util.InternalCompilerError;

public class X10UnknownType_c extends UnknownType_c implements X10UnknownType {
	protected X10UnknownType_c() {}

	public X10UnknownType_c(TypeSystem ts) {
		super(ts);
	}

	public boolean safe() {
		return false;
	}
	
	  public boolean isRooted() { return false; }
	    public boolean isX10Struct() { return false; }

	public Flags flags() {
		return Flags.NONE;
	}
	public X10Type setFlags(Flags f) {
		return this;
	}
	public boolean isProto() { return false;}
	public X10Type clearFlags(Flags f) {
		return this;
	}
	public String toString() {
		return  super.toString();
	}
	 public boolean equalsNoFlag(X10Type t2) {
			return this == t2;
		}
}
