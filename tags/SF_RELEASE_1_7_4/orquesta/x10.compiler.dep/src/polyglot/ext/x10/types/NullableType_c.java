/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 26, 2004
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.ArrayType;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.ReferenceType_c;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;


/**
 * Implementation of the nullable type constructor.
 *
 * @author vj
 */
public class NullableType_c extends ReferenceType_c implements NullableType {

	protected Ref<? extends X10NamedType> base;
    
	// RMF 11/2/2005 --
	// Attempting to initialize these (unused) fields at construction time causes a
	// chicken-and-egg problem for any recursive type with a nullable field of the
	// owner's type (e.g. class Node { nullable Node link; }): can't initialize the
	// type of the nullable field without knowing all the fields/methods first!
//	protected List fields;
//	protected List methods;
	// IP 2/9/2006 -- This is unused
//	protected List interfaces;
	
	// TODO: vj add dep clauses.

	/** Used for deserializing types. */
	protected NullableType_c() { }

	public String name() {
		return base().name();
	}
	public String fullName() {
		return base().fullName();
	}
	/**
	 * Factory method for producing nullable types. Implements nullable nullable X = nullable X.
	 *
	 * @param ts
	 * @param pos
	 * @param base -- the base type for the nullable type.
	 * @return
	 */
	public static NullableType makeNullable(TypeSystem ts, Position pos, Ref<? extends X10NamedType> base) {
		return new NullableType_c(ts, pos, base);
	}

	/**
	 * Constructor is made private. Called only in the factory method above.
	 *
	 * @param ts
	 * @param pos
	 * @param base
	 */
	private NullableType_c(TypeSystem ts, Position pos, Ref<? extends X10NamedType> base) {
		super(ts, pos);
		this.base = base;
		assert base != null;
	}

	public X10NamedType ultimateBase() {
	    Type t = base();
	    if (t instanceof NullableType) {
	        return ((NullableType) t).ultimateBase();
	    }
	    return (X10NamedType) t;
	}
	
	public X10NamedType base() {
	    return Types.get(base);
	}

	public Ref<? extends X10NamedType> theBaseType() {
	    return base;
	}
	
	/** Set the base type. */
	public NullableType base(Ref<? extends X10NamedType> base) {
		if (base == this.base)
			return this;
		NullableType_c n = (NullableType_c) copy();
		n.base = base;
		return n;
	}

	public void print(CodeWriter w) {
		// [IP] FIXME: is this the right thing to do here?
		w.write("/"+"*"+"nullable<"+"*"+"/");
		base().print(w);
		w.write("/"+"*"+">"+"*"+"/");
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

	private static String getStackTrace() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] trace = new Throwable().getStackTrace();
		for (int i=2; i < trace.length; i++)
			sb.append("\t").append(trace[i]).append("\n");
		return sb.toString();
	}

	/**
	 * This needs to be changed so that the right boxed class is produced for
	 * primitive types.
	 * [IP] TODO: Fix this.
	 * @author vj
	 */
	public String translate(Resolver c) {
		return base().translate(c);
	}

	public List<MethodInstance> methods() {
	    if (base() instanceof ReferenceType) {
	        ReferenceType rt = (ReferenceType) base();
	        return rt.methods();
	    }
	    return Collections.EMPTY_LIST;
	}

	public List<FieldInstance> fields() {
            if (base() instanceof ReferenceType) {
                ReferenceType rt = (ReferenceType) base();
                return rt.fields();
            }
            return Collections.EMPTY_LIST;
	}

	public FieldInstance fieldNamed(String name) {
	    if (base() instanceof ReferenceType) {
	        ReferenceType rt = (ReferenceType) base();
	        return rt.fieldNamed(name);
	    }
	    return null;
	}

	public Type superType() {
	    if (base() instanceof ReferenceType) {
                ReferenceType rt = (ReferenceType) base();
                return rt.superType();
	    }
	    X10TypeSystem ts = (X10TypeSystem) typeSystem();
	    return ts.X10Object();
	}

	public List<Type> interfaces() {
            if (base() instanceof ReferenceType) {
                ReferenceType rt = (ReferenceType) base();
                return rt.interfaces();
            }
            return Collections.EMPTY_LIST;
	}


        public boolean equalsImpl(TypeObject t) {
            if (t instanceof NullableType_c) {
                NullableType_c a = (NullableType_c) t;
                return base.equals(a.base);
            }
            return false;
        }
        
        public int hashCode() {
            return base.hashCode();
        }
        
        public boolean descendsFrom(Type t) {
            if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
                return X10TypeMixin.descendsFrom(this, (X10Type) t);
            return super.descendsFrom(t);
        }
        
        public boolean isSubtype(Type t) {
            if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
                return X10TypeMixin.isSubtype(this, (X10Type) t);

            if (t instanceof NullableType) {
                NullableType nt = (NullableType) t;
                return base().isSubtype(nt.base());
            }
            
            return super.isSubtype(t);
        }

        public boolean typeEquals(Type t) {
            if (X10TypeMixin.eitherIsDependent(this, (X10Type) t))
                return X10TypeMixin.typeEquals(this, (X10Type) t);
            
            if (t instanceof NullableType) {
                NullableType nt = (NullableType) t;
                return ts.typeEquals(base(), nt.base());
            }
         
            return false;
        }

        public boolean isImplicitCastValid(Type toType) {
            if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
                return X10TypeMixin.isImplicitCastValid(this, (X10Type) toType);

            if (toType instanceof NullableType) {
                NullableType nt = (NullableType) toType;
                return base().isImplicitCastValid(nt.base());
            }
            
            return super.isImplicitCastValid(toType);
	}

	/**
	 * This method only test if java types are compatibles for the cast.
	 * Hence even a potentially invalid cast as ((T1) nullable T2) is accepted.
	 * However it is the responsability of X10Cast class to generates code
	 * that checks expression value is not null.
	 */
	public boolean isCastValid(Type toType) {
            if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
                return X10TypeMixin.isCastValid(this, (X10Type) toType);
            
            if (toType instanceof NullableType) {
                NullableType nt = (NullableType) toType;
                return base().isCastValid(nt.base());
            }
            
            return base().isCastValid(toType);
	}
	
	public boolean isArray() {
	    return base().isArray();   
	}
	public ArrayType toArray() { return base().toArray();}
    
    public List<FieldInstance> properties() {
        //return base.properties();
    	 return Collections.EMPTY_LIST;
    }
    public List<FieldInstance> definedProperties() {
        return Collections.EMPTY_LIST;
    }
    
    public boolean isNullable() { return true;}
    public boolean isFuture() { return false;}
    public NullableType toNullable() { return this;}
    public FutureType toFuture() { return null;}
    public boolean safe() { return base().safe();}
    
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

