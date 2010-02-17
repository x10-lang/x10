/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types;

import java.util.ArrayList;
import java.util.List;

import x10.constraint.XRoot;
import x10.constraint.XTerm;
import polyglot.types.Type;

/**
 * Todo: This needs to be fixed. The constraints in this have to be used to figure
 * out whether c is entailed. This needs a proper constraint representation, e.g.
 * X <: Y, Y <: Z |- X <: Z
 * 
 * @author njnystrom
 * @author vj
 *
 */
public class TypeConstraint_c implements TypeConstraint {

    List<SubtypeConstraint> terms;
    boolean consistent;

    public TypeConstraint_c() {
        terms = new ArrayList<SubtypeConstraint>();
        consistent = true;
    }
  
    public TypeConstraint unify(Type t1, Type t2, X10TypeSystem xts) {
    	TypeConstraint result = this;
       final X10Context emptyContext = new X10Context_c(xts);
    	t1 = X10TypeMixin.stripConstraints(t1);
    	t2 = X10TypeMixin.stripConstraints(t2);   	
    	if (xts.typeEquals(t1, t2, emptyContext /*dummy*/))
    			return this;
    	if ((t1 instanceof ParameterType) || (t2 instanceof ParameterType)) {
    		result.addTerm(new SubtypeConstraint_c(t1, t2, SubtypeConstraint.EQUAL_KIND));
    		if (! (result.consistent(emptyContext)))
    			return result;
    	}
    	if ((t1 instanceof X10ClassType) && (t2 instanceof X10ClassType)) {
    		X10ClassType xt1 = (X10ClassType) t1;
    		X10ClassType xt2 = (X10ClassType) t2;
    		Type bt1 = xt1.x10Def().asType();
    		Type bt2 = xt2.x10Def().asType();
    		if (!xts.typeEquals(bt1,bt2, emptyContext)) {
    			result.setInconsistent();
    			return result;
    		}
    		List<Type> args1 = xt1.typeArguments();
    		List<Type> args2 = xt2.typeArguments();
    		if (args1.size() != args2.size()) {
    			result.setInconsistent();
    			return result;
    		}
    
    		for (int i=0; i < args1.size(); ++i) {
    			Type p1 = args1.get(i);
    			Type p2 = args2.get(i);
    			result = unify(p1,p2,xts);
    			if (! result.consistent(emptyContext)) {
    				return result;
    			}
    		}
    	}
    	return result;   			
    }
    public boolean entails(TypeConstraint c, X10Context xc) {
        X10TypeSystem xts = (X10TypeSystem) xc.typeSystem();
        for (SubtypeConstraint t : c.terms()) {
            if (t.isEqualityConstraint()) {
                if (!xts.typeEquals(t.subtype(), t.supertype(), xc)) {
                    return false;
                }
            }
            else if (t.isSubtypeConstraint()) {
                if (!xts.isSubtype(t.subtype(), t.supertype(), xc)) {
                    return false;
                }
            }
            
        }
        return true;
    }

    public List<SubtypeConstraint> terms() {
        return terms;
    }

    public TypeConstraint_c copy() {
        try {
            return (TypeConstraint_c) super.clone();
        }
        catch (CloneNotSupportedException e) {
            assert false;
            return this;
        }
    }
    
    public TypeConstraint addIn(TypeConstraint c) {
        terms.addAll(c.terms());
        return this;
    }
    
    public void addTerm(SubtypeConstraint c) {
        terms.add(c);
    }

    public void addTerms(List<SubtypeConstraint> terms) {
        this.terms.addAll(terms);
    }
    
    public boolean consistent(X10Context context) {
        if (consistent) {
            X10Context xc = (X10Context) context;
            X10TypeSystem ts = (X10TypeSystem) context.typeSystem();
            for (SubtypeConstraint t : terms()) {
                if (t.isEqualityConstraint()) {
                    if (! ts.typeEquals(t.subtype(), t.supertype(), xc)) {
                        consistent = false;
                        return false;
                    }
                }
                else if (t.isSubtypeConstraint()) {
                    if (! ts.isSubtype(t.subtype(), t.supertype(), xc)) {
                        consistent = false;
                        return false;
                    }
                }
            }
        }
        return consistent;
    }

    public void setInconsistent() {
        this.consistent = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see x10.types.TypeConstraint#subst(x10.constraint.XTerm,
     * x10.constraint.XRoot, boolean)
     */
    public TypeConstraint subst(XTerm y, XRoot x) {
        TypeConstraint_c c = new TypeConstraint_c();
        List<SubtypeConstraint> l = c.terms;
        for (SubtypeConstraint s : terms) {
            l.add(s.subst(y, x));
        }
        return c;
    }

    @Override
    public String toString() {
        return terms.toString();
    }

    
}
