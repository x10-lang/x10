/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 28, 2004
 *
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.types.NullType_c;
import polyglot.ast.Expr;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.GenParameterExpr;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.types.NullType;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

/** Every X10 term must have a type. This is the type of the X10 term null.
 * Note that there is no X10 type called Null; only the term null.
 * @author vj
 *
 */
public class X10NullType_c extends NullType_c implements X10NullType {
    public X10NullType_c( TypeSystem ts ) {super(ts);}
    
    public String name() { return "NULL_TYPE";}
    public String fullName() { return "NULL_TYPE";}
    
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
    
    public boolean typeEquals(Type o) {
        return o instanceof NullType;
    }
    
    /**
     * This is different from the definition of jl.Nullable. X10 does not 
     * assume that every reference type contains null. The type must be nullable
     * for it to contain null.
     * TODO: Check if the result should be just: targetType.isNullable().
     */
    public boolean isImplicitCastValid(Type toType) {
    	return toType.isNull() || toType instanceof NullableType;
    }	

    /** 
     * TODO: vj -- check if this implementation is correct.
     * The definition of descendsFrom in TypeSystem is
     * Returns true iff child is not ancestor, but child descends from ancestor. 
     * In the X10 type system, the Null type should not descend from any type.
     */
    public boolean descendsFrom(Type ancestor) {
        return false;
    }

    /**
     * Same as isImplicitCastValidImpl.
     **/
    public boolean isCastValid(Type toType) {
    	X10Type targetType = (X10Type) toType;
        return toType.isNull() || ((X10TypeSystem) ts).isNullable(targetType);
    }

    public boolean safe() { return true;}

    public String toStringForDisplay() { 
    	return "null";
    }
    
    public NullableType toNullable() {
        return null;
    }
    
    public FutureType toFuture() {
        return null;
    }
    
    public boolean isNullable() {
        return false;
    }
    
    public boolean isFuture() {
        return false;
    }
	
}
