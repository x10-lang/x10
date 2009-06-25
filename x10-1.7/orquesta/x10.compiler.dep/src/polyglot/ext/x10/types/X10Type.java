/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import java.util.List;
import java.util.Map;

import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.types.FieldInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;


/**
 * @author vj
 *
 */
public interface X10Type extends Type, X10TypeObject {

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

    boolean isNullable();
    boolean isFuture();
    NullableType toNullable();
    FutureType toFuture();

    /**
     * A string representation of this type, appropriate for displaying to the user.
     * Note: toString() returns a string that is capable of being read by the Java compiler.
     * @return
     */
    String toStringForDisplay();
    
    
    // BEGIN DEPENDENT TYPE MIXIN

    // Getters and *imperative* setters.
    // Other functionality is in the DependentTypeMixin.
    // These methods should not be called directly by client code.
    public Ref<? extends Constraint> getDepClause();
    public void setDepClause(Ref<? extends Constraint> c);
    public List<Ref<? extends Type>> getTypeParams();
    public void setTypeParams(List<Ref<? extends Type>> l);
    
    public Constraint depClause();
    public List<Type> typeParameters();
    public Constraint realClause();

    // Get the clause of the root type.  (:) for anything other than classes.
    public Constraint getRootClause();
    public void checkRealClause() throws SemanticException;  
    public X10Type rootType();
    public boolean equalsWithoutClauseImpl(Type o);
    // END DEPENDENT TYPE MIXIN
    
    public Constraint getRealClause();
    public void setRealClause(Constraint c, SemanticException error);
    

}
