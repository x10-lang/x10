package x10.types;

import java.io.Serializable;
import java.util.List;

import polyglot.util.Copy;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

public interface TypeConstraint extends Copy, Serializable {
    
    List<SubtypeConstraint> terms();
    TypeConstraint subst(XTerm y, XRoot x);
    void setInconsistent();
    boolean consistent(X10Context context);
    
    TypeConstraint addIn(TypeConstraint c);
    void addTerm(SubtypeConstraint c);
    void addTerms(List<SubtypeConstraint> terms);
    
    boolean entails(TypeConstraint c, X10Context xc);
}