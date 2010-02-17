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

import java.util.ArrayList;
import java.util.List;

public class XSubst_c extends XRef_c<XConstraint> {

	XTerm y;
	XRoot x;
	XRef_c<XConstraint> ref;

	@Override
	public String toString() {
		return ref + "[" + y + "/" + x + "]";
	}

	/** term[y/x] */
	XSubst_c(XRef_c<XConstraint> ref, XTerm y, XRoot x) {
//		if (ref instanceof XSubst_c) {
//			XSubst_c s = (XSubst_c) ref;
//			if (s.result == null) {
//				if (s.y.equals(x)) {
//					this.ref = s.ref;
//					this.y = y;
//					this.x = s.x;
//					return;
//				}
//				if (s.x.equals(x)) {
//					this.ref = s.ref;
//					this.y = s.y;
//					this.x = s.x;
//					return;
//				}
//			}
//		}
		this.ref = ref;
		this.y = y;
		this.x = x;
//		
//		int i = 0;
//		
//		while (ref instanceof XSubst_c) {
//			XSubst_c s = (XSubst_c) ref;
//			ref = s.ref;
//			i++;
//		}
//		
//		if (i > 3)
//			System.out.print("");
	}
	
	private static int count = 0;

	@Override
	public XConstraint compute() {
		XRef_c<XConstraint> ref = this.ref;
		XTerm y = this.y;
		XRoot x = this.x;
		
		assert ref != null;
		assert y != null;
		assert x != null;
		
		// avoid recursion by using a stack
		ArrayList<XSubst_c> stack = new ArrayList<XSubst_c>();
		stack.add(this);
		
		while (ref instanceof XSubst_c) {
			XSubst_c s = (XSubst_c) ref;
			
			if (s.result != null)
				break;
			
			stack.add(s);
			ref = s.ref;
			
//			if (stack.size() > 100)
//				System.out.println("here" + (count++));
		}
		
		if (ref == null)
			return null;
		
		XConstraint c = ref.get();
		
		if (c == null) {
			return null;
		}
		
		boolean inconsistent = false;
		
		for (int i = stack.size()-1; i >= 0; i--) {
			XSubst_c s = stack.get(i);
			
			try {
				if (! inconsistent) {
					c = c.substitute(s.y, s.x);
				}
				s.result = c;
				s.ref = null;
				s.y = null;
				s.x = null;
			}
			catch (XFailure e) {
				inconsistent = true;
				XConstraint c2 = new XConstraint_c();
				c2.setInconsistent();
				c = c2;
			}
		}
		
		return c;
	}
}
