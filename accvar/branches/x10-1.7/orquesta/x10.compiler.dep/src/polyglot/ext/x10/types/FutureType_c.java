/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 *
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.main.Report;
import polyglot.types.ArrayType;
import polyglot.types.FieldInstance;
import polyglot.types.MethodAsTypeTransform;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Ref;
import polyglot.types.ReferenceType_c;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TransformingList;


/**
 * Implementation of Future type constructor.
 *
 * <p>This code does not yet implement Termination Equivalence for Future Types, i.e.
 * future future X = future X
 * future nullable future X = future nullable X
 * are not yet implemented.
 *
 * @author vj
 */
public class FutureType_c extends ReferenceType_c implements FutureType {
	protected Ref<? extends X10NamedType> base;
	protected List<MethodDef> methods;

	/** We type base to Type instead of X10Type because we wish to reuse code from
	 * jl which traffics in Type's. We rely on the global invariant, maintained manually
	 * in this version of the code, that at runtime the only Type's created are X10Type's.

	 * @param ts
	 * @param pos
	 * @param base
	 */
	public FutureType_c(TypeSystem ts, Position pos, Ref<? extends X10NamedType> base) {
		super(ts, pos);
		this.base = base;
		this.methods = null;
	}
	
	protected void init() {
		this.methods = new ArrayList<MethodDef>(2);
		// [IP] FIXME: is the cast below necessary?
		this.methods.add(((X10TypeSystem) ts).methodDef(position(),
				Types.ref(this),
				ts.Public(),
				base,
				"force",
				Collections.<Ref<? extends Type>>emptyList(),
				Collections.<Ref<? extends Type>>emptyList()));
		this.methods.add(((X10TypeSystem) ts).methodDef(position(),
		                                                Types.ref(this),
				ts.Public(),
				Types.ref(ts.Boolean()),
				"forced",
				Collections.<Ref<? extends Type>>emptyList(),
				Collections.<Ref<? extends Type>>emptyList()));
	}

	public boolean isFuture() {
		return true;
	}

	public FutureType toFuture() {
		return this;
	}
	
	public String name() { return base().name();}
	public String fullName() { return base().fullName();}

	/* The only methods are: public <base> force() and public boolean forced().
	 * @see polyglot.types.ReferenceType#methods()
	 */
	public List<MethodInstance> methods() {
	    init();
	    return new TransformingList<MethodDef,MethodInstance>(methods, new MethodAsTypeTransform());
	}

	/* This type has no fields.
	 * @see polyglot.types.ReferenceType#fields()
	 */
	public List<FieldInstance> fields() {
		return Collections.EMPTY_LIST;
	}

	/* The supertype is the base type of the type system.
	 * @see polyglot.types.ReferenceType#superType()
	 */
	public Type superType() {
		return ((X10TypeSystem) ts).X10Object();
	}

	/* This type implements no interfaces.
	 * @see polyglot.types.ReferenceType#interfaces()
	 */
	public List<Type> interfaces() {
		return Collections.<Type>emptyList();
	}

	/*
	 * @see polyglot.types.Type#translate(polyglot.types.Resolver)
	 */
	public String translate(Resolver c) {
		// return "future " + base().translate( c );
		// return "x10.lang.Future /*" + base().translate( c ) + "*/";
		return "x10.lang.Future";
	}

	public void print(CodeWriter w) {
		// [IP] FIXME: is this the right thing to do here?
		w.write("x10.lang.Future");
	}

        public String toString() {
            return toStringForDisplay();
        }

        public String toStringForDisplay() {
            StringBuffer sb = new StringBuffer();
            sb.append("nullable<");
            sb.append(base.toString());
            sb.append(">");
            sb.append(X10TypeMixin.clauseToString(this));
            return sb.toString();
        }

	/* Return the base type.
	 * @see polyglot.ext.x10.types.FutureType#base()
	 */
	public X10NamedType base() {
		return this.base.get();
	}
	
	public Ref<? extends X10NamedType> theBaseType() {
            return base;
        }
	
	/** Set the base type. */
        public FutureType base(Ref<? extends X10NamedType> base) {
                if (base == this.base)
                        return this;
                FutureType_c n = (FutureType_c) copy();
                n.base = base;
                return n;
        }



	/* This type has no fields.
	 * @see polyglot.types.ReferenceType#fieldNamed(java.lang.String)
	 */
	public FieldInstance fieldNamed(String name) {
		return null;
	}

	public boolean isImplicitCastValid(Type origType) {
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] |" + this + "|.isImplicitCastValidImpl(|" + origType +"|):");
		}
		
		if (X10TypeMixin.eitherIsDependent(this, (X10Type) origType))
		    return X10TypeMixin.isImplicitCastValid(this, (X10Type) origType);

		X10Type toType = (X10Type) origType;
		NullableType targetType = toType.toNullable();
		if (targetType != null) {
			toType = targetType.base();
		}

		FutureType target = toType.toFuture();
		if (target != null) {
			boolean result = base().isImplicitCastValid( target.base() );
			if (Report.should_report("debug", 5)) {
				Report.report(5, "[FutureType_c] ..." + result);
			}
			return result;
		}

		// Otherwise do the default check
		boolean result = super.isImplicitCastValid(toType);
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] ... "+result);
		}
		return result;
	}

	public boolean isCastValid(Type origType) {
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] |" + this + "|.isCastValidImpl(|" + origType +"|):");
		}

                if (X10TypeMixin.eitherIsDependent(this, (X10Type) origType))
                    return X10TypeMixin.isCastValid(this, (X10Type) origType);

		X10Type toType = (X10Type) origType;
		NullableType nullType = toType.toNullable();
		if (nullType != null)
			toType = nullType.base();

		FutureType target = toType.toFuture();
		if (target != null) {
			boolean result = base().isCastValid( target.base() );
			if (Report.should_report("debug", 5)) {
				Report.report(5, "[FutureType_c] ... target=|" + target + "|.");
				Report.report(5, "[FutureType_c] ... returns |" + result + "|.");
			}
			return result;
		}

		// Otherwise do the default check
		boolean result = super.isCastValid(toType);
		if (Report.should_report("debug", 5)) {
			Report.report(5, "[FutureType_c] ... "+result);
		}
		return result;
	}


	    public boolean equalsImpl(TypeObject t) {
	        if (t instanceof FutureType_c) {
	            FutureType_c a = (FutureType_c) t;
	            return base.equals(a.base);
	        }
	        return false;
	    }

	    public boolean typeEquals(Type t) {
	        if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
                    return X10TypeMixin.typeEquals(this, (X10Type) t);
            
	        if (t instanceof FutureType) {
	            FutureType a = (FutureType) t;
	            return ts.typeEquals(base(), a.base());
	        }
	        return false;
	    }
	    
	        public boolean descendsFrom(Type t) {
                    if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
                        return X10TypeMixin.descendsFrom(this, (X10Type) t);

                    return super.descendsFrom(t);
	        }
	        
	        public boolean isSubtype(Type t) {
	            if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
	                return X10TypeMixin.isSubtype(this, (X10Type) t);

	            return super.isSubtype(t);
	        }

	public int hashCode() {
	    return base.hashCode();
	}

	public boolean isValueType() {
		return true;
	}
	public boolean isNullable() {
	    return false;
	}
	public NullableType toNullable() {
	    return null;
	}

	public boolean safe() {
		return false;
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
}

