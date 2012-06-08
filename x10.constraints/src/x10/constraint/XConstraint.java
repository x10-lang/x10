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

import x10.constraint.visitors.XGraphVisitor;

public interface XConstraint {
	public boolean consistent(); 
    public boolean valid(); 
    
    // TODO: clean up interface
    public void addBinding(XTerm left, XTerm right);
    public void addDisBinding(XTerm left, XTerm right);
    public void addAtom(XTerm t) throws XFailure;
    public boolean entails(XConstraint other);
    
    // TODO: clean up interface
    public boolean disEntails(XTerm left, XTerm right);
    public boolean entails(XTerm left, XTerm right);
    
    public XConstraint leastUpperBound(XConstraint other);
    public XConstraint residue(XConstraint other);
    public List<XTerm> constraints();	
    public List<XFormula<?>> atoms();
    public void visit(XGraphVisitor xg);
    public String toString();
	public XConstraint copy();
}
