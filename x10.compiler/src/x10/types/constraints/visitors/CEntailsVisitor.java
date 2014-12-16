/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types.constraints.visitors;

import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.visitors.XGraphVisitor;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintMaker;

public class CEntailsVisitor extends XGraphVisitor{
    CConstraint c1;
    ConstraintMaker c2m;
    XVar otherSelf;
    boolean result=true;
    public CEntailsVisitor(boolean hideEQV, boolean hideFake, CConstraint c1, ConstraintMaker c2m,
    		XVar otherSelf) {
    	super(hideEQV, hideFake);
        this.c1=c1;
        this.c2m = c2m;
        this.otherSelf=otherSelf;
    }
    public boolean visitAtomicFormula(XTerm t) {
        try {
            t = t.subst(c1.self(), otherSelf);
            boolean myResult = c1.entails(t);
            if (! myResult && c2m!=null) {
                c1 = c1.copy();
                c1.addIn(c2m.make());
                c2m=null;
                if (! c1.consistent())
                    return false;
                
                myResult = c1.entails(t);
            }
            result &=myResult;   
                
        } catch (XFailure z) {
            return false;
        }
        return result;
    }
    public boolean visitEquals(XTerm t1, XTerm t2) {
        t1 = t1.subst(c1.self(), otherSelf);
        t2 = t2.subst(c1.self(), otherSelf);
        boolean myResult = c1.entails(t1, t2);
        if (! myResult && c2m!=null) {
            try {
                c1 = c1.copy();
                c1.addIn(c2m.make());
                c2m=null;
                if (! c1.consistent())
                    return false;
                myResult = c1.entails(t1, t2);
            } catch (XFailure z) {
                myResult=false;
            }
        }
        result &=myResult;   
        return result;
    }
    public boolean visitDisEquals(XTerm t1, XTerm t2) {
        t1 = t1.subst(c1.self(), otherSelf);
        t2 = t2.subst(c1.self(), otherSelf);
        boolean myResult = c1.disEntails(t1, t2);
        if (! myResult && c2m!=null) {
            try {
                c1 = c1.copy();
                c1.addIn(c2m.make());
                c2m=null;
                if (! c1.consistent())
                    return false;
                myResult = c1.disEntails(t1, t2);
            } catch (XFailure z) {
                myResult=false;
            }
        }
        result &=myResult;   
        return result;
    }
    public boolean result() {
        return result;
    }
}