/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Lit_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.ext.x10.types.constr.Promise;
import polyglot.frontend.JLScheduler.SupertypeDef;
import polyglot.types.PrimitiveType;
import polyglot.types.PrimitiveType_c;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;

/** X10 has no primitive types. Types such as int etc are all value class types. 
 * However, this particular X10 implementation uses Java primitive types to implement some of
 * X10's value class types, namely, char, boolean, byte, int etc etc. It implements other
 * value class types as Java classes.
 * 
 * Thus this class represents one of specially implemented X10 value class types.
 * @author praun
 * @author vj
 */
public class X10PrimitiveType_c extends PrimitiveType_c implements X10PrimitiveType {
	protected X10PrimitiveType_c() { }

	public X10PrimitiveType_c(TypeSystem ts, PrimitiveType.Kind kind) {
		super(ts, kind);
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
	    
	    public boolean equalsWithoutClauseImpl(X10Type o) {
	        return X10TypeMixin.equalsIgnoreClause(this, (X10Type) o);
	    }
	    // END DEPENDENT TYPE MIXIN
	  
	/**
	 * Every X10 value type descends from X10.lang.Object, the base class,
	 * and implements the ValueType interface.
	 */
	public boolean descendsFrom(Type ancestor) {
	    if (X10TypeMixin.eitherIsDependent(this, (X10Type) ancestor))
	        return X10TypeMixin.descendsFrom(this, (X10Type) ancestor);

	    X10TypeSystem xts = (X10TypeSystem) ts;
	    return ts.typeEquals(ancestor, xts.X10Object()) || ts.typeEquals(ancestor, xts.value());
	}

	/** Return true if this type can be assigned to <code>toType</code>. */
	public boolean isImplicitCastValid(Type toType) {
            if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
                return X10TypeMixin.isImplicitCastValid(this, (X10Type) toType);
            
            X10Type targetType = (X10Type) toType;

            if (toType.isArray())
                return false;

            if (ts.isSubtype(this, targetType))
                return true;

            NullableType realTarget = targetType.toNullable();
            if (realTarget != null && ts.isSubtype(this, realTarget.base()))
                return true;

            return super.isImplicitCastValid(toType); 
	}
	
	public boolean typeEquals(Type toType) {
	    if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
	        return X10TypeMixin.typeEquals(this, (X10Type) toType);
	    return super.typeEquals(toType);
	}
	
	public boolean isSubtype(Type toType) {
            if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
                return X10TypeMixin.isSubtype(this, (X10Type) toType);
            
            if (toType instanceof NullableType) {
                NullableType nt = (NullableType) toType;
                return isSubtype(nt.base());
            }

            // This will check if <: x10.lang.Object.
            return super.isSubtype(toType);
        }
	
	public boolean numericConversionValid(Object value) {
	    X10TypeSystem xts = (X10TypeSystem) ts;

	    if (! super.numericConversionValid(value)) {
	        return false;
	    }

	    C_Special self = new C_Special_c(X10Special.SELF, this);
	    C_Lit val = new C_Lit_c(value, this);

	    try {
	        Constraint c = new Constraint_c(xts).addBinding(self, val);
	        return xts.entailsClause(c, X10TypeMixin.realClause(this));
	    }
	    catch (Failure f) {
	        // Adding binding makes real clause inconsistent.
	        return false;
	    }
	}

	/**
	 * Returns true iff a cast from this to <code>origType</code> is valid. 
	 * Note that a cast from int to x10.compilergenerated.BoxedInteger is valid.
	 * */
	public boolean isCastValid(Type toType) {
            if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
                return X10TypeMixin.isCastValid(this, (X10Type) toType);
            
	    X10TypeSystem xts = (X10TypeSystem) ts;

	    if (xts.typeEquals(toType, xts.Object())) {
	        return true;
	    }
	    
	    if (xts.typeEquals(toType, xts.X10Object())) {
	        return true;
	    }
	    
	    if (xts.isBoxedType(toType)) {
	        return isCastValid(xts.boxedTypeToPrimitiveType(toType));
	    }
	    
	    if (toType instanceof NullableType) {
	        NullableType nt = (NullableType) toType;
	        return isCastValid(nt.base());
	    }
	    
	    if (super.isCastValid(toType)) {
	        return true;
	    }
	
	    return false;
	}
	
	public void print(CodeWriter w) {
		// [IP] FIXME: is this the right thing to do here?
		w.write(super.toString());
	}

	public String toString() { 
		return toStringForDisplay();
	}

	private static String getStackTrace() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] trace = new Throwable().getStackTrace();
		for (int i=2; i < trace.length; i++)
			sb.append("\t").append(trace[i]).append("\n");
		return sb.toString();
	}

	public String toStringForDisplay() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(super.toString());
            sb.append(X10TypeMixin.clauseToString(this));
	    return sb.toString();
	}
	public String typeName() { 
	    return super.toString();
	}
	
	public boolean isValueType() { return ((X10TypeSystem) typeSystem()).isValueType(this); }
	
	/** All primitive types are safe. */
	
	public boolean safe() { return true; }

	public boolean equalsWithoutClauseImpl(Type other) {
            return X10TypeMixin.equalsIgnoreClause(this, (X10Type) other);
	}

	public boolean isFuture() {
	    return false;
	}

	public boolean isNullable() {
	    return false;
	}

	public FutureType toFuture() {
	    return null;
	}

	public NullableType toNullable() {
	    return null;
	}
}
