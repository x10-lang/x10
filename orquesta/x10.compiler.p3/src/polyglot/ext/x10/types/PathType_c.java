/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.types.Types;
import polyglot.util.Position;

public class PathType_c extends Type_c implements PathType {
	C_Var base;
	TypeProperty prop;
	
	PathType_c(TypeSystem ts, Position pos, C_Var base, TypeProperty prop) {
		super(ts, pos);
		this.base = base;
		this.prop = prop;
	}
	
	public C_Var base() {
		return base;
	}
	
	public TypeProperty property() {
		return prop;
	}

	public PathType base(C_Var base) {
		PathType_c t = (PathType_c) copy();
		t.base = base;
		return t;
	}
	
	public PathType property(TypeProperty prop) {
		PathType_c t = (PathType_c) copy();
		t.prop = prop;
		return t;
	}
	
    // BEGIN DEPENDENT TYPE MIXIN
    protected Ref<? extends Constraint> depClause;
    protected List<Ref<? extends Type>> typeParams; // should be folded into depClause as constraint on type properties
    
    /** Cached real clause, computed from the depClause and the rootType's class invariant, if any. */
    protected Constraint realClause;
    protected SemanticException realClauseInvalid;

    public Constraint getRealClause() { return realClause; }
    public void setRealClause(Constraint c, SemanticException error) {
        this.realClause = c;
        this.realClauseInvalid = error;
    }

    public Ref<? extends Constraint> getDepClause() { return depClause; }
    public void setDepClause(Ref<? extends Constraint> c) {
        this.depClause = c;
        this.realClause = null; // force recomputation
        this.realClauseInvalid = null;
    }
    public List<Ref<? extends Type>> getTypeParams() { return typeParams; }
    public void setTypeParams(List<Ref<? extends Type>> l) {
        this.typeParams = l;
    }
    
    public C_Var selfVar() { return X10TypeMixin.selfVar(this); }
    public X10Type makeNoClauseVariant() { return X10TypeMixin.makeNoClauseVariant(this); }
    public X10Type makeVariant(Constraint c, List<Type> l) { return X10TypeMixin.makeVariant(this, c, l); }
    public boolean isConstrained() { return X10TypeMixin.isConstrained(this); }
    public boolean isParametric() { return X10TypeMixin.isParametric(this); }

    public Constraint depClause() { return X10TypeMixin.depClause(this); }
    public List<Type> typeParameters() { return X10TypeMixin.typeParameters(this); }
    public Constraint realClause() { return X10TypeMixin.realClause(this); }

    public X10Type depClause(Constraint c) { return X10TypeMixin.depClause(this, Types.ref(c)); }
    public X10Type depClause(Ref<? extends Constraint> c) { return X10TypeMixin.depClause(this, c); }
    public X10Type typeParams(List<Ref<? extends Type>> l) { return X10TypeMixin.typeParams(this, l); }

    public Constraint getRootClause() {
        return new Constraint_c((X10TypeSystem) ts);
    }
    
    public void checkRealClause() throws SemanticException {
        if (realClause == null) {
            Constraint c = X10TypeMixin.realClause(this);
            assert c == realClause; // make sure the result got cached
        }
        if (realClauseInvalid != null) {
            throw realClauseInvalid;
        }
    }

    public X10Type rootType() {
        return X10TypeMixin.makeNoClauseVariant(this);
    }
    
    public boolean equalsWithoutClauseImpl(Type o) {
        return X10TypeMixin.equalsIgnoreClause(this, (X10Type) o);
    }
    // END DEPENDENT TYPE MIXIN
    
	public String translate(Resolver c) {
		// should be the property bound
		return "java.lang.Object";
	}
	
	public boolean isFuture() {
		return false;
	}
	public boolean isNullable() {
		return false;
	}
	public boolean safe() {
		return true;
	}
	public FutureType toFuture() {
		return null;
	}
	public NullableType toNullable() {
		return null;
	}

	public String toString() {
	    return toStringForDisplay();
	}
	
	public String toStringForDisplay() {
		StringBuffer sb = new StringBuffer();
		sb.append(base + "." + prop.name());
		sb.append(X10TypeMixin.clauseToString(this));
		return sb.toString();
	}
	
	public boolean typeEquals(Type t) {
		if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
			return X10TypeMixin.typeEquals(this, (X10Type) t);
		if (t instanceof PathType_c) {
			PathType_c pt = (PathType_c) t;
			return base.equals(pt.base) && prop.equals(pt.prop);
		}
		return false;
	}
	
	public boolean isSubtype(Type t) {
		if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
			return X10TypeMixin.isSubtype(this, (X10Type) t);
		if (t instanceof PathType_c) {
			PathType_c pt = (PathType_c) t;
			return base.equals(pt.base) && prop.equals(pt.prop);
		}
		return false;
	}
}