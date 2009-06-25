/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.DerefTransform;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Type_c;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TransformingList;

public class ClosureType_c extends Type_c implements ClosureType {
    private static final long serialVersionUID= 2768150875334536668L;

    protected Ref<? extends Type> returnType;
    protected List<Ref<? extends Type>> argumentTypes;
    protected List<Ref<? extends Type>> throwTypes;

    public ClosureType_c() {
	super();
    }

    public ClosureType_c(TypeSystem ts) {
	super(ts);
    }

    public ClosureType_c(TypeSystem ts, Position pos) {
	super(ts, pos);
    }

    public ClosureType_c(TypeSystem ts, Position pos, Ref<? extends Type> returnType, List<Ref<? extends Type>> argTypes) {
	this(ts, pos, returnType, argTypes, new ArrayList<Ref<? extends Type>>());
    }

    public ClosureType_c(TypeSystem ts, Position pos, Ref<? extends Type> returnType, List<Ref<? extends Type>> argTypes, List<Ref<? extends Type>> throwTypes) {
	this(ts, pos);
	this.returnType = returnType;
        this.argumentTypes = argTypes;
        this.throwTypes = throwTypes;
    }
    
    public Ref<? extends Type> returnType() {
	return returnType;
    }

    public void returnType(Ref<? extends Type> returnType) {
	this.returnType= returnType;
    }

    public List<Ref<? extends Type>> argumentTypes() {
	return Collections.unmodifiableList(argumentTypes);
    }

    public void argumentTypes(List<Ref<? extends Type>> argTypes) {
	this.argumentTypes= argTypes;
    }

    public List<Ref<? extends Type>> throwTypes() {
	return Collections.unmodifiableList(throwTypes);
    }

    public void throwTypes(List<Ref<? extends Type>> argTypes) {
	this.throwTypes= argTypes;
    }

    @Override
    public String translate(Resolver c) {
	// Just combine the result of calling translate() on each of the component types?
        throw new InternalCompilerError("Fix Me: cannot translate() a closure type yet.");
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
    
    public boolean descendsFrom(Type ancestor) {
        if (X10TypeMixin.eitherIsDependent(this, (X10Type) ancestor))
            return X10TypeMixin.descendsFrom(this, (X10Type) ancestor);
        Type t = ancestor;
        if (t.typeEquals(ts.Object())) {
            return true;
        }
        if (t.typeEquals(this)) {
            return true;
        }
        return false;
    }
    
    public boolean isSubtype(Type ancestor) {
        if (X10TypeMixin.eitherIsDependent(this, (X10Type) ancestor))
            return X10TypeMixin.isSubtype(this, (X10Type) ancestor);
	// Permit covariance in the return type, so that a closure that returns a more
	// specific type can be assigned to a closure variable with a less specific
	// return type. Don't permit covariance in the throw types or argument types.
        Type t = ancestor;
	if (!(t instanceof ClosureType))
	    return false;
	ClosureType other = (ClosureType) t;
	if (!typeListEquals(argumentTypes, other.argumentTypes()))
	    return false;
	if (!typeListEquals(throwTypes, other.throwTypes()))
	    return false;
	return ts.isSubtype(returnType.get(), other.returnType().get());
    }

    @Override
    public boolean typeEquals(Type t) {
        if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
            return X10TypeMixin.typeEquals(this, (X10Type) t);
	if (!(t instanceof ClosureType))
	    return false;
	ClosureType other = (ClosureType) t;
	if (!ts.typeEquals(returnType.get(), other.returnType().get()))
	    return false;
	if (!typeListEquals(argumentTypes, other.argumentTypes()))
	    return false;
	if (!typeListEquals(throwTypes, other.throwTypes()))
	    return false;
        return true;
    }

    protected boolean typeListEquals(List<Ref<? extends Type>> l1, List<Ref<? extends Type>> l2) {
	return CollectionUtil.<Type>equals(new TransformingList<Ref<? extends Type>, Type>(l1, new DerefTransform<Type>()),
	                                   new TransformingList<Ref<? extends Type>, Type>(l2, new DerefTransform<Type>()),
	                                   new TypeSystem_c.TypeEquals());
    }
    
    public boolean isNullable() {
        return false;
    }
    public boolean isFuture() {
        return false;
    }
    public NullableType toNullable() {
        return null;
    }
    public FutureType toFuture() {
        return null;
    }

    public boolean safe() {
        return true;
    }

    public String toStringForDisplay() {
        return toString();
    }
    
    @Override
    public String toString() {
	StringBuffer buff= new StringBuffer();
	buff.append(returnType.toString())
	    .append('(');
	for(Iterator<Ref<? extends Type>> iter= argumentTypes.iterator(); iter.hasNext(); ) {
	    Ref<? extends Type> type= iter.next();
	    buff.append(type.toString());
	    if (iter.hasNext()) buff.append(',');
	}
	buff.append(')');
	if (throwTypes.size() > 0) {
	    buff.append(" throws ");
	    for(Iterator<Ref<? extends Type>> iter= throwTypes.iterator(); iter.hasNext(); ) {
	        Ref<? extends Type> type= iter.next();
		buff.append(type.toString());
		if (iter.hasNext()) buff.append(',');
	    }
	}
	return buff.toString();
    }
}
