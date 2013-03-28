/**
 * 
 */
package x10.types.constraints.xnative;

import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.Types;
import x10.constraint.XDef;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.xnative.XNativeVar;
import x10.types.X10LocalDef;
import x10.types.constraints.CLocal;
import x10.types.constraints.ConstraintManager;


/**
 * A representation of a local variable, with its associated type information.
 * <p>In essence, a CLocal is a serializable representation of an X10LocalInstance.
 * @author vj
 */

public class CNativeLocal extends XNativeVar<Type> implements CLocal,Typed {
    private static final long serialVersionUID = 127892741748021961L;
    X10LocalDef def;
    
    public CNativeLocal(X10LocalDef ld) {
        this(ld, ld.name().toString());
    }
    public CNativeLocal(X10LocalDef ld, String s) {
        super(Types.get(ld.type()), s);
        def = ld;
    }
    private CNativeLocal(CNativeLocal other) {
        super(other);
        def = other.def();
    }

    @Override
    public X10LocalDef def() {return def;}

    
	@Override
	public CNativeLocal copy() {
		return new CNativeLocal(this);
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o instanceof CNativeLocal) {
			CNativeLocal other = (CNativeLocal) o;
			return name.equals(other.name()) && def == other.def;
		}
		return false;
	}
    
}