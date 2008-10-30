package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.ArrayType_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.util.Position;

public class X10ArrayType_c extends ArrayType_c implements X10ArrayType {
    public X10ArrayType_c(TypeSystem ts, Position pos, Ref<? extends Type> base) {
        super(ts, pos, base);
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
    
    public Constraint depClause() { return X10TypeMixin.depClause(this); }
    public List<Type> typeParameters() { return X10TypeMixin.typeParameters(this); }
    public Constraint realClause() { return X10TypeMixin.realClause(this); }

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
    
    public boolean typeEquals(Type other) {
        if (X10TypeMixin.eitherIsDependent(this, (X10Type) other))
            return X10TypeMixin.typeEquals(this, (X10Type) other);
        return super.typeEquals(other);
    }
    
    public boolean isImplicitCastValid(Type toType) {
        if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
            return X10TypeMixin.isImplicitCastValid(this, (X10Type) toType);
        return super.isImplicitCastValid(toType);
    }
    
    public boolean isCastValid(Type toType) {
        if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
            return X10TypeMixin.isCastValid(this, (X10Type) toType);
        return super.isCastValid(toType);
    }
    
    public boolean isSubtype(Type ancestor) {
        if (X10TypeMixin.eitherIsDependent(this, (X10Type) ancestor))
            return X10TypeMixin.isSubtype(this, (X10Type) ancestor);
        return super.isSubtype(ancestor);
    }
    
    public boolean descendsFrom(Type ancestor) {
        if (X10TypeMixin.eitherIsDependent(this, (X10Type) ancestor))
            return X10TypeMixin.descendsFrom(this, (X10Type) ancestor);
        return super.descendsFrom(ancestor);
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
        sb.append(super.toString());
        sb.append(X10TypeMixin.clauseToString(this));
        return sb.toString();
    }
}
