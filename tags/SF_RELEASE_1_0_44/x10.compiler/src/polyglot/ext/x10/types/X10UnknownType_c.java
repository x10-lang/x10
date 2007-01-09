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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.types.UnknownType_c;
import polyglot.ext.x10.ast.GenParameterExpr;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.FieldInstance;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;

/**
 * @author vj
 *
 */
public class X10UnknownType_c extends UnknownType_c implements X10UnknownType {

	 /** Used for deserializing types. */
    protected X10UnknownType_c() { }
    
    /** Creates a new type in the given a TypeSystem. */
    public X10UnknownType_c( TypeSystem ts ) {
        super(ts);
    }

    protected Constraint depClause;
    protected List/*<GenParameterExpr>*/ typeParameters;
    protected X10Type rootType = this;
    public X10Type rootType() { return rootType;}
    public boolean isRootType() { return rootType == this;}
    public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
    public List typeParameters() { return typeParameters;}
    public Constraint depClause() { return depClause; }
    public Constraint realClause() { return depClause; }
    public C_Var selfVar() { return depClause()==null ? null : depClause().selfVar();}
    public void setSelfVar(C_Var v) {
		Constraint c = depClause();
		if (c==null) {
			depClause=new Constraint_c();
		}
		depClause.setSelfVar(v);
	}
    public boolean isConstrained() { return depClause !=null && ! depClause.valid();}
    public boolean propertiesElaborated() { return true; }
    public boolean typeEqualsImpl(Type o) {
        return equalsImpl(o);
    }
    public int hashCode() {
        return 
          (rootType == this ? super.hashCode() : rootType.hashCode() ) 
          + (isConstrained() ? depClause.hashCode() : 0)
  		+ (isParametric() ? typeParameters.hashCode() :0);
        
    }
    public void setDepGen(Constraint d, List/*<GenParameterExpr>*/ l) {
		depClause = d;
		typeParameters = l;
	}
    public void addBinding(C_Var t1, C_Var t2) {
		if (depClause == null)
			depClause = new Constraint_c();
		depClause = depClause.addBinding(t1, t2);
	}
    public boolean consistent() {
    	return depClause== null || depClause.consistent();
    }
    public X10Type makeDepVariant(Constraint d, List<Type> l) { 
       return makeVariant(d, l);
    }
    public X10Type makeVariant(Constraint d, List<Type> l) { 
    	// Need to pick up the typeparameters from this
    	// made, and the realClause from the root type.
    	if (d == null && (l == null || l.isEmpty())) return this;
    	X10UnknownType_c n = (X10UnknownType_c) copy();
    	n.typeParameters = (l==null || l.isEmpty())? typeParameters : l;
    	n.depClause = d;
    	return n;
    }
    public X10Type makeNoClauseVariant() {
    	X10UnknownType_c n = (X10UnknownType_c) copy();
    	n.depClause = new Constraint_c();
    	n.typeParameters = typeParameters;
    	return n;
    }
    public C_Term propVal(String name) {
		return (depClause==null) ? null : depClause.find(name);
	}
   
    public boolean equalsImpl(TypeObject o) {
        //    Report.report(3,"X10ParsedClassType_c: equals |" + this + "| and |" + o+"|");
        if (o == this) return true;
        if (! (o instanceof X10UnknownType_c)) return false;
        X10UnknownType_c other = (X10UnknownType_c) o;
        return ((X10TypeSystem) typeSystem()).equivClause(this, other);
    }
    public boolean equalsWithoutClauseImpl(X10Type o) {
        // Report.report(3,"X10ReferenceType_c: equals |" + this + "| and |" + o+"|");
      
        if (o == this) return true;
        if (! (o instanceof X10UnknownType_c)) return false;
        X10UnknownType_c other = (X10UnknownType_c) o;
       
       return rootType == other.rootType;
    }
   
    public List<FieldInstance> properties() { return Collections.EMPTY_LIST;}
	public List<FieldInstance> definedProperties() { return Collections.EMPTY_LIST;}
    
    public NullableType toNullable() { return X10Type_c.toNullable(this);}
    public FutureType toFuture() { return X10Type_c.toFuture(this);}
    /** 
     * The unknwon type has unknown safety status -- defaults to false.
     */
    public boolean safe() { return false;}
    public String toStringForDisplay() { return "UnknownType!";}
}
