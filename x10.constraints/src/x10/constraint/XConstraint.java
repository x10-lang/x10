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

package x10.constraint;

import java.util.List;
import java.util.Set;

import x10.constraint.visitors.XGraphVisitor;

public interface XConstraint {
	public boolean consistent(); 
    public boolean valid(); 
    
    public void addBinding(XTerm left, XTerm right);
    public void addDisBinding(XTerm left, XTerm right);
    public void addAtom(XTerm t) throws XFailure;
    public void addTerm(XTerm t) throws XFailure; 

    public boolean entails(XConstraint other);
    public boolean entails(XTerm term);
    public boolean disEntails(XTerm left, XTerm right);
    public boolean entails(XTerm left, XTerm right);
    
    public XConstraint leastUpperBound(XConstraint other);
    public XConstraint residue(XConstraint other);
    /**
     * Returns a list of the conjuncts in the constraint
     * @return
     */

    // TODO: make sure returning these arrays is not a terrible idea
    
    public List<? extends XTerm> constraints();	
    public List<? extends XFormula<?>> atoms();
    public void visit(XGraphVisitor xg);
    public String toString();
	public XConstraint copy();
	
	// CCConstraint interface:
	public XVar bindingForVar(XVar v);
	public Set<? extends XTerm> getTerms();
	public void setInconsistent();
	public Set<? extends XVar> vars(); 
	public List<? extends XTerm> extConstraintsHideFake();
	public List<? extends XTerm> extConstraints();
}
