/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.Type;


/**
 * @author vj
 *
 */
public interface X10Type extends Type {

	/**
	 * An X10Type is said to be safe if all its methods are safe, i.e. sequential, local and nonblocking.
	 * All primitive types (int, boolean, double, short, long, byte) are safe.
	 * All Java array types are safe.
	 * A future type is not safe. Its only operation, force, is blocking.
	 * A nullable type is safe if its base type is safe.
	 * A ParsedClass type (user-defined interface or class type) is safe if the user annotates it so.
	 * The compiler will check that all its methods are safe.
	 * @return
	 */
    boolean safe();
    
    /** Return a subtype of the basetype with specified depclause and type parameters. 
     *  If d==null, then the specified depclause is this.depClause,
     *  if g==null, then the specified type parameter is this.typeParameter.
     *  The realclause of the new type is the realclause of the basetype,
     *  with the specified depclause added in.
     * @param d
     * @param g
     * @return
     */
    X10Type makeVariant(Constraint d, List g);
    /*
     * Destructively set the depclause and parameter list to be d and g respectively.
     * 
     */
    void setDepGen(Constraint d, List g);
    X10Type  baseType();
    List typeParameters();
    boolean isParametric();
    NullableType toNullable();
    FutureType toFuture();
    /**
     * Return the depClause, if any defined for this type.
     * @return
     */
    Constraint depClause();
    /**
     * Return the realClause for this type. The realClause is the conjunction of the
     * depClause and the baseClause for the type -- it represents all the constraints
     * that are satisfied by an instance of this type. The baseClause is the invariant for
     * the base type. If the base type C has defined properties P1 p1, ..., Pk pk, 
     * and inherits from type B, then the baseClause for C is the baseClause for B
     * conjoined with r1[self.p1/self, self/this] && ... && rk[self.pk/self, self/this]
     * where ri is the realClause for Pi.
     * 
     * @return
     */
    Constraint realClause();
    /**
     * Is this is a constrained type?
     * @return true iff depClause()==null or depClause().valid();
     */
    boolean isConstrained();
    
    /**
     * If the type constrains the given property to
     * a particular value, then return that value, otherwise 
     * return null
     * @param name -- the name of the property.
     * @return null if there is no value associated with the property in the type.
     */
    C_Term propVal(String name);
    /** The list of properties of the class.
     
     * @return
     */
    List/*<PropertyInstance>*/ properties();
    
    /**
     * Add t1=t2 to the realclause and the depclause. Note that this may cause the clause
     * to become inconsistent.
     * @param t1
     * @param t2
     */
    void addBinding(C_Term t1, C_Term t2);
    
    /**
     * Is the type consistent?
     * @return
     */
    boolean consistent();
    C_Var selfVar();
    
    boolean equalsWithoutClauseImpl(X10Type other);
   

}
