package x10.types.constraints;

import java.io.Serializable;
import java.util.List;

import polyglot.util.Copy;
import x10.constraint.XFailure;
import x10.constraint.XRoot;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.X10Context;
import x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;

public interface TypeConstraint extends Copy, Serializable {
    
    List<SubtypeConstraint> terms();
    TypeConstraint subst(XTerm y, XRoot x);
    void setInconsistent();
    boolean consistent(X10Context context);
    
    TypeConstraint addIn(TypeConstraint c);
    void addTerm(SubtypeConstraint c);
    void addTerms(List<SubtypeConstraint> terms);
    
    boolean entails(TypeConstraint c, X10Context xc);
    
    /**
     * Returns the type constraint obtained by unifying the two types.
     * The returned constraint will be inconsistent if the two types
     * are not unifiable.
     * @param t1
     * @param t2
     * @return
     */
    TypeConstraint unify(Type t1, Type t2, X10TypeSystem ts);
    
    /**
     * Add the binding xtype = ytype to this.
     * @param xtype
     * @param ytype
     * @throws XFailure
     */
    void addTypeParameterBindings(Type xtype, Type ytype) throws XFailure;
    
    void checkTypeQuery( TypeConstraint query, XVar ythis, XRoot xthis, XVar[] y, XRoot[] x, 
			 X10Context context) throws SemanticException;
}