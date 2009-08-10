/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import polyglot.types.ArrayType_c;
import polyglot.types.Flags;
import polyglot.types.Named;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

public class X10ArrayType_c extends ArrayType_c implements X10ArrayType {
    public X10ArrayType_c(TypeSystem ts, Position pos, Ref<? extends Type> base) {
        super(ts, pos, base);
    }
    
    public boolean isGloballyAccessible() {
	    return false;
    }
	
    public Name name() { return ((Named) base).name();}
    public QName fullName() { return ((Named) base).fullName();}

    public boolean safe() {
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        return sb.toString();
    }
    // TODO: vj check if this can have any flags in reality.
	 // begin Flagged mixin
   Flags flags;
    public Flags flags() { return flags;}
 
	public X10Type setFlags(Flags f) {
		X10Flags xf = (X10Flags) f;
		X10ArrayType_c c = (X10ArrayType_c) this.copy();
		if (c.flags == null)
			c.flags = X10Flags.toX10Flags(Flags.NONE);
		c.flags = xf.isRooted() 
				? (xf.isStruct() ? ((X10Flags) c.flags).Rooted().Struct() : ((X10Flags) c.flags).Rooted())
						: ((xf.isStruct()) ? ((X10Flags) c.flags).Struct() : c.flags);
				return c;
	}
	public X10Type clearFlags(Flags f) {
		X10ArrayType_c c = (X10ArrayType_c) this.copy();
		if (c.flags == null)
			c.flags = X10Flags.toX10Flags(Flags.NONE);
		c.flags = c.flags.clear(f);
		return c;
	}
	  public boolean isRooted() { return flags == null ? false : ((X10Flags) flags).isRooted(); }
	    public boolean isX10Struct() { return flags == null ? false : ((X10Flags) flags).isStruct(); }
}
