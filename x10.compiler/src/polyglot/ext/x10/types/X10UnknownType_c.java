/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.jl.types.UnknownType_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
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
    protected X10Type baseType = this;
    public X10Type baseType() { return baseType;}
    public boolean isParametric() { return typeParameters != null && ! typeParameters.isEmpty();}
    public List typeParameters() { return typeParameters;}
    public Constraint depClause() { return depClause; }
    public Constraint realClause() { return depClause; }
    public C_Var selfVar() { return depClause()==null ? null : depClause().selfVar();}
    public boolean isConstrained() { return depClause !=null && ! depClause.valid();}
    public boolean typeEqualsImpl(Type o) {
        return equalsImpl(o);
    }
    public int hashCode() {
        return 
          (baseType == this ? super.hashCode() : baseType.hashCode() ) 
          + (isConstrained() ? depClause.hashCode() : 0)
  		+ (isParametric() ? typeParameters.hashCode() :0);
        
    }
    public void setDepGen(Constraint d, List/*<GenParameterExpr>*/ l) {
		depClause = d;
		typeParameters = l;
	}
    public void addBinding(C_Term t1, C_Term t2) {
		if (depClause == null)
			depClause = new Constraint_c();
		depClause = depClause.addBinding(t1, t2);
	}
    public boolean consistent() {
    	return depClause== null || depClause.consistent();
    }
    public X10Type makeVariant(Constraint d, List l) { 
        if (d == null && (l == null || l.isEmpty()))
                return this;
        X10UnknownType_c n = (X10UnknownType_c) copy();
        // n.baseType = baseType; // this may not be needed.
        n.typeParameters = (l==null || l.isEmpty())? typeParameters : l;
		n.depClause = (d==null) ? depClause : d;
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
       
       return baseType == other.baseType;
    }
   
    public List properties() { return Collections.EMPTY_LIST;}
    
    public NullableType toNullable() { return X10Type_c.toNullable(this);}
    public FutureType toFuture() { return X10Type_c.toFuture(this);}
    /** 
     * The unknwon type has unknown safety status -- defaults to false.
     */
    public boolean safe() { return false;}
}
