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

package x10.ast;

import java.util.Collections;

import polyglot.ast.Expr;
import polyglot.ast.FieldAssign;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.types.Context;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.ConstrainedType;
import x10.types.X10LocalInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.checker.Checker;

public class X10LocalAssign_c extends LocalAssign_c {

    public X10LocalAssign_c(NodeFactory nf, Position pos, Local left, Operator op, Expr right) {
        super(nf, pos, left, op, right);
    }

    @Override
    public Type leftType() {
        LocalInstance li = local().localInstance();
        if (li == null)
            return null;
        if (li instanceof X10LocalInstance) {
            return ((X10LocalInstance) li).type();
        }
        return li.type();
    }
    
    //change this also need to change FieldAssign_c correspondingly
    @Override
    public Node checkAtomicity(ContextVisitor tc) {
    	Type leftType = this.leftType();
    	Type rightType = this.right().type();
    	
    	if(leftType instanceof X10ParsedClassType_c) {
    		X10ParsedClassType_c clazzLeft = (X10ParsedClassType_c)leftType;
    	    
    	    X10ParsedClassType_c clazzRight = null;
    	    clazzRight = Types.fetchX10ClassType(rightType);
    	    
    	    //assert clazzRight != null : "The right type is: " + rightType.getClass() + " : " + rightType;
    	      if(clazzRight != null) {
    	          if(clazzLeft.hasAtomicContext() && !clazzRight.hasAtomicContext()) {
    	    	      Errors.issue(tc.job(), new Errors.AssignMustHaveAtomicplus(clazzRight, position()));
    	          }
    	          if(!clazzLeft.hasAtomicContext() && clazzRight.hasAtomicContext()) {
    	    	      Errors.issue(tc.job(), new Errors.AssignNeedCastOffAtomicplus(clazzRight, position()));
    	          }
    	          if(clazzLeft.hasAtomicContext() && clazzRight.hasAtomicContext()) {
    	    	      if(!tc.typeSystem().typeEquals(clazzLeft.getAtomicContext(),
    	    			  clazzRight.getAtomicContext(), tc.context())) {
    	    	    	  SemanticException e = new SemanticException("Local assignment's atomic context does not match."
    	    	    			  + "\n\t left: " + this.left() + ", with type: " + clazzLeft
    	    	    			  + ", and atomic context: " + clazzLeft.getAtomicContext()
    	    	    			  + "\n\t right: " + this.right() + ", with type: " + clazzRight
    	    	    			  + ", and atomic context: " + clazzRight.getAtomicContext(), this.position);
    	    		      Errors.issue(tc.job(), e);
    	    	      }
    	         }
    	    }
    	}
    	
    	return super.checkAtomicity(tc);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
        // XTENLANG-2660
        Context context =  tc.context();
        Name name = local().name().id();
        if (context.localHasAt(name)) {
            Errors.issue(tc.job(), new Errors.LocalVariableAccessedAtDifferentPlace(name, local().position()));
        }
        //if (local().flags().isFinal()) { // final locals are checked for local access only on assignment (reading a final local can be done from any place)
        //    final X10Local_c local = (X10Local_c) local();
        //    local.checkLocalAccess(local.localInstance(), tc);
        //}
        return Checker.typeCheckAssign(this, tc);
    }

}