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

import java.util.Collections;
import java.util.List;




/**
 * A representation of logical variables. 
 * 
 * <p> EQVs  are existentially quantified
 * in each constraint in which they occur.

  * <p> UQVs  are free in the constraint and may occur in multiple constraints. 
 * @author vj
 * @see XUQV
 *
 */
public class XEQV extends XRoot  {
	
    public final int num;
    public XEQV(int n) {
        this.num=n;
    }
    
    @Override
    public XTermKind kind() { return XTermKind.LOCAL;}
    @Override
    public int prefersBeingBound() {
        return XTerm.TERM_PREFERS_BEING_BOUND;
    }

    public boolean hasVar(XVar v) {
        return equals(v);
    }
    @Override
    public boolean hasEQV() {
        return true;
    }
    @Override
    public boolean isEQV() {
        return true;
    }
    @Override
    public List<XEQV> eqvs() {
			return Collections.<XEQV>singletonList(this);
	}


    public boolean okAsNestedTerm() {
    	return true;
    }
    @Override
    public int hashCode() {
        return num;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof XEQV) {
            return num == ((XEQV) o).num;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "eqv#" + num;
    }
}
