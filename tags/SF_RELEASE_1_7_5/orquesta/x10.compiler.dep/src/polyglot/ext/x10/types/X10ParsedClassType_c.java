/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_BinaryTerm_c;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.FieldAsTypeTransform;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.ParsedClassType_c;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.FilteringList;
import polyglot.util.Position;
import polyglot.util.Predicate;
import polyglot.util.TransformingList;

/** 6/2006 Modified so that every type is now potentially generic and dependent.
 * @author vj
 */
public class X10ParsedClassType_c extends ParsedClassType_c
implements X10ParsedClassType
{    
    protected Ref<? extends Constraint> classInvariant;
    
    public X10ParsedClassType_c(ClassDef def) {
        super(def);
    }

    public X10ParsedClassType_c(TypeSystem ts, Position pos, Ref<? extends ClassDef> def) {
        super(ts, pos, def);
    }
    
    /** Property initializers, used in annotations. */
    List<Expr> propertyInitializers;
    public List<Expr> propertyInitializers() {
        if (propertyInitializers == null)
            return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(propertyInitializers);
    }
    public Expr propertyInitializer(int i) {
        return propertyInitializers().get(i);
    }
    public X10ClassType propertyInitializers(List<Expr> inits) {
        X10ParsedClassType_c  n = (X10ParsedClassType_c) copy();
        n.propertyInitializers = inits;
        return n;
    }

    public List<X10ClassType> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }

    public List<X10ClassType> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
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

    public X10ClassType superClassRoot() {
        return (X10ClassType) (isJavaType() ? ts.Object() : ((X10TypeSystem) ts).X10Object());
    }
	
    public X10ClassDef x10Def() {
        return (X10ClassDef) def();
    }
	
    public Constraint getRootClause() {
        return x10Def().getRootClause();
    }

	public boolean isJavaType() {
	    return def().fromJavaClassFile();
	}

	/**
	 * A parsed class is safe iff it explicitly has a flag saying so.
	 */
	public boolean safe() {
		return X10Flags.toX10Flags(flags()).isSafe();
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

	public boolean isCastValid(Type toType) {
	    if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
	        return X10TypeMixin.isCastValid(this, (X10Type) toType);

	    if (toType instanceof NullableType) {
	        NullableType nt = (NullableType) toType;
	        return isCastValid(nt.base());
	    }
	    
	    if (toType instanceof FutureType) {
	        return toType.isCastValid(this);
	    }

	    return super.isCastValid(toType);
	}

	public boolean isImplicitCastValid(Type toType) {
	    if (X10TypeMixin.eitherIsDependent(this, (X10Type) toType))
	        return X10TypeMixin.isImplicitCastValid(this, (X10Type) toType);

	    if (toType instanceof NullableType) {
	        NullableType nt = (NullableType) toType;
	        return isImplicitCastValid(nt.base());
	    }
	    return super.isImplicitCastValid(toType);
	}
	
	public List<FieldInstance> definedProperties() {
	    return new TransformingList<FieldDef, FieldInstance>(x10Def().properties(), new FieldAsTypeTransform());
	}

	public List<FieldInstance> properties() {
	    Type superType = superType();
	    if (superType instanceof X10ClassType) {
	        List<FieldInstance> l = ((X10ClassType) superType).properties();
	        List<FieldInstance> l2 = new ArrayList<FieldInstance>();
	        l2.addAll(l);
	        l2.addAll(definedProperties());
	        return l2;
	    }
	    return definedProperties();
	}

	public Constraint classInvariant() {
	    return x10Def().classInvariant().get();
	}

	public boolean isFuture() { return false; }
	public boolean isNullable() { return false; }
	public NullableType toNullable() { return null; }
	public FutureType toFuture() { return null; }
	
	public String toString() {
	    return toStringForDisplay();
	}
	
	public String toStringForDisplay() {
            StringBuffer sb = new StringBuffer();
            sb.append(super.toString());
            sb.append(X10TypeMixin.clauseToString(this));
            return sb.toString();
        }
	
	boolean isX10Array;
	boolean isX10ArraySet;
	public boolean isX10Array() {
		if (isX10ArraySet) return isX10Array;
		isX10ArraySet = true;
		
		return isX10Array=((X10TypeSystem) typeSystem()).isX10Array(this);
	}
	
	    protected X10ParsedClassType setProperty(String propName) {
	        return setProperty(propName, ((X10TypeSystem) typeSystem()).TRUE());
	    }

	    protected X10Type addBinding(C_Var v1, C_Var v2) {
	        return X10TypeMixin.addBinding(this, v1, v2);
	    }
	    
	    protected X10ParsedClassType setProperty(String propName, C_Term val) {
	        X10FieldInstance fi = getProperty(propName);
	        if (fi != null) {
	            C_Var var = new C_Field_c(fi, new C_Special_c(X10Special.SELF, this));
	            if (val instanceof C_Var) {
	            return (X10ParsedClassType) addBinding(var, (C_Var) val);
	            } else {
	            	try {
	            		X10TypeSystem xts = (X10TypeSystem) ts;
	            		Constraint c = new Constraint_c(xts);
	            		Constraint c2 =  X10TypeMixin.depClause(this);
	            		c.addIn(c2);
	            		c.addTerm(new C_BinaryTerm_c("==", var, val, ts.Boolean()));
	            		return (X10ParsedClassType) X10TypeMixin.depClauseDeref(this, c);
	            	}
	            	catch (Failure f) {
	            		// Fail silently.
	            		// FIXME: should be reported to caller, which can then choose whether to suppress the error
	            		// but we're only called from code that would suppress the error anyway
	            	}
	            }
	        }
	        else {
	            assert false : "Could not find property " + propName + " in " + this;
	        }
	        return this;
	    }
	    
	    protected X10FieldInstance getProperty(String propName) {
	        try {
	            X10FieldInstance fi = (X10FieldInstance) ts.findField(this, propName);
	            if (fi != null && fi.isProperty()) {
	                return fi;
	            }
	        }
	        catch (SemanticException e) {
	            // ignore
	        }
	        return null;
	    }
	    
	    protected boolean amIProperty(String propName) {
	        X10FieldInstance fi = getProperty(propName);
	        if (fi != null) {
	            C_Var term = new C_Field_c(fi, new C_Special_c(X10Special.SELF, this)); 
	            try {
	                Constraint c = new Constraint_c((X10TypeSystem) ts);
	                c.addTerm(term);
	                return X10TypeMixin.realClause(this).entails(c);
	            }
	            catch (Failure f) {
	                return false;
	            }
	        }
	        return false;
	    }
	    
	    public boolean isRect() {
	        return amIProperty("rect");
	    }

	    public X10ParsedClassType setRect() {
	        X10ParsedClassType t = setProperty("rect");
	        if (t.isRankOne() && t.isZeroBased() && !t.isRail())
	            return t.setRail();
	        return t;
	    }

	    public C_Var onePlace() {
	        return find("onePlace");
	    }

	    public C_Var find(String propName) {
	        Constraint c = X10TypeMixin.realClause(this);
	        if (c == null) return null;
	        return c.find(propName);
	    }
	    
	    public X10ParsedClassType setOnePlace(C_Var onePlace) {
	        return setProperty("onePlace", onePlace);
	    }

	    public boolean hasLocalProperty() {
	            C_Term onePlace = onePlace();
	            return onePlace instanceof C_Here_c;
	    }
	    
	    public boolean isZeroBased() {
	            if (isRail()) return true;
	            Constraint c = X10TypeMixin.realClause(this);
	            return amIProperty("zeroBased");
	    }

	    public X10ParsedClassType setZeroBased() {
	        X10ParsedClassType t = setProperty("zeroBased");
	            if (t.isRect() && t.isRankOne() && ! t.isRail())
	                return t.setRail();
	            return t;
	    }
	    
	    public boolean isRail() {
	        return amIProperty("rail");
	    }

	    public X10ParsedClassType setRail() {
	        X10ParsedClassType t = setProperty("rail");
	            t = t.setRank(((X10TypeSystem) typeSystem()).ONE());
	            if (! t.isZeroBased()) t = t.setZeroBased();
	            if (! t.isRect()) t = t.setRect();
	            return t;
	    }
	    
	    public C_Var rank() {
	        if (isRail())
	           return ((X10TypeSystem) typeSystem()).ONE();
	        return findOrSythesize("rank");
	    }

	    private C_Var findOrSythesize(String propName) {
	        C_Var rank = find(propName);
	        if (rank == null) {
	            Constraint c = X10TypeMixin.realClause(this);
	            if (c != null) {
	                // build the synthetic term.
	                C_Var var = c.selfVar();
	                if (var !=null) {
	                    X10FieldInstance fi = getProperty(propName);
	                    if (fi != null)
	                        rank = new C_Field_c(fi, var);
	                }
	            }
	        }
	        return rank;
	    }
	    
	    public X10ParsedClassType setRank(C_Var rank) {
	            assert(rank !=null);
	            X10ParsedClassType t = setProperty("rank", rank);
	            if (t.isRankOne() && t.isZeroBased() && t.isRect() && ! t.isRail())
	                    return t.setRail();
	            return t;
	    }
	    
	    public boolean isRankOne() {
	            return isRail() || ((X10TypeSystem) typeSystem()).ONE().equals(rank());
	    }
	    public boolean isRankTwo() {
	            return ((X10TypeSystem) typeSystem()).TWO().equals(rank());
	    }
	    public boolean isRankThree() {
	            return ((X10TypeSystem) typeSystem()).THREE().equals(rank());
	    }

	    public C_Var region() {
	        return findOrSythesize("region");
	    }
	    public X10ParsedClassType setRegion(C_Term region) {
	           return setProperty("region", region);
	    }
	    
	    public C_Var distribution() {
	        return findOrSythesize("distribution");
	    }
	    public X10ParsedClassType setDistribution(C_Term dist) {
	           return setProperty("distribution", dist);
	    }

	    public C_Var self() {
	        C_Var self = find("self");
	        if (self == null) {
	            // build the synthetic term.
	            self = X10TypeMixin.realClause(this).selfVar();
	        }
	        return self;
	    }
	    
	    public boolean isConstantDist() {
	                return fullName().equals("x10.lang.dist") && amIProperty("constant");
	    }
	    
	    public X10ParsedClassType setConstantDist() {
	            return setProperty("constant");
	    }
	    
	    public boolean isUniqueDist() {
	                return fullName().equals("x10.lang.dist") && amIProperty("unique");
	    }

	    public X10ParsedClassType setUniqueDist() {
	           return setProperty("unique");
	    }
	    
	    /**
	     * The arg must be a region type. Set the properties of this type (rank, isZeroBased, isRect)
	     * from arg.
	     * @param arg
	     */
	    public X10ParsedClassType transferRegionProperties(X10ParsedClassType arg) {
	            C_Var rank = arg.rank();
	            C_Var region = arg.region();
	            if (region == null && ((X10TypeSystem) ts).isRegion(arg))
	                    region = arg.self();
	            return acceptRegionProperties(region, rank, arg.isZeroBased(), arg.isRect(), arg.isRail());
	    }
	    
	    public X10ParsedClassType acceptRegionProperties(C_Var region, C_Var rank, boolean isZeroBased, boolean isRect, boolean isRail) {
	        X10ParsedClassType t = this;
	        if (region() != null) t = t.setRegion(region());
	        if (rank() != null) t = t.setRank(rank());
	        if (isZeroBased()) t = t.setZeroBased();
	        if (isRect()) t = t.setRect();
	        if (isRail()) t = t.setRail();
	        return t;
	    }
	    public X10ParsedClassType setZeroBasedRectRankOne() {
	        return acceptRegionProperties(region(), ((X10TypeSystem) typeSystem()).ONE(), true, true, true);
	    }

	    
}

