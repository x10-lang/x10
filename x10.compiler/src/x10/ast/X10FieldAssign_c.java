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

import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Assign.Operator;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.ConstrainedType;
import x10.types.X10ClassType;
import x10.types.X10ParsedClassType_c;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.ConstructorDef;
import x10.types.X10FieldInstance;

import x10.types.checker.Checker;
import x10.types.checker.PlaceChecker;
import x10.visit.X10TypeChecker;
import x10.errors.Errors;

public class X10FieldAssign_c extends FieldAssign_c {
    
    public X10FieldAssign_c(NodeFactory nf, Position pos, Receiver target, Id name, Operator op, Expr right) {
        super(nf, pos, target, name, op, right);
    }
    
    @Override
    public Assign typeCheckLeft(ContextVisitor tc) {
        Context cxt = (Context) tc.context();
        if (cxt.inDepType()) {
            SemanticException e = new Errors.NoAssignmentInDepType(this, this.position());
            Errors.issue(tc.job(), e, this);
        } else {
            tc = tc.context(((Context) tc.context()).pushAssignment());
        }
        Assign res = super.typeCheckLeft(tc);
        return res;
    }
    
    @Override
    public Node checkAtomicity(ContextVisitor tc) {
    	//TODO why the following two calls are must!
    	Node leftNode = this.left().checkAtomicity(tc);
    	Node rightNode = this.right().checkAtomicity(tc);
    	
    	X10ParsedClassType_c leftFieldClassType = null;
    	if(leftNode instanceof X10Field_c) {
    		X10Field_c leftField = (X10Field_c)leftNode;
    		Type leftFieldType = leftField.type();
    		leftFieldClassType = Types.fetchX10ClassType(leftFieldType);
    	}
    	
    	X10ParsedClassType_c rightFieldClassType = null;
    	if(rightNode instanceof X10Field_c) {
    		X10Field_c rightField = (X10Field_c)rightNode;
    		Type rightFieldType = rightField.type();
    		rightFieldClassType = Types.fetchX10ClassType(rightFieldType);
    	}
    	
    	//check whether the left/right side all have atomicity context or not
    	X10ParsedClassType_c clazztype = Types.fetchX10ClassType(this.left().type());
    	if(leftFieldClassType != null) {
    		clazztype = leftFieldClassType;
    	}
    	if(clazztype != null) {
    		//check the right side
    		Type rightType = this.right().type();
    		//to see if the right side is still a class type
    		X10ParsedClassType_c rightClassType = Types.fetchX10ClassType(rightType);
    		if(rightFieldClassType != null) {
    			rightClassType = rightFieldClassType;
    		}
    		//check if the right side is not null, excluding the case like: field = null;
    		if(rightClassType != null){
    		  if(clazztype.getAtomicContext() != null) {
    			if(!rightClassType.hasAtomicContext()) {
    				SemanticException e = new Errors.TypeDoesnotHaveAtomicContext(this.right, rightClassType, position());
    				Errors.issue(tc.job(), e);
    			}
    			if(!tc.typeSystem().typeEquals(clazztype.getAtomicContext(), rightClassType.getAtomicContext(),
    					tc.context())) {
    				SemanticException e = new Errors.AtomicContextNotEqual(this.left(), clazztype,
    						(X10ParsedClassType_c)clazztype.getAtomicContext(),
    						this.right, rightClassType,
    						(X10ParsedClassType_c)rightClassType.getAtomicContext(), position());
    				Errors.issue(tc.job(), e);
    			}
    		  } else {
    			if(rightClassType.hasAtomicContext()) {
    				Errors.issue(tc.job(), new Errors.AssignNeedCastOffAtomicplus(rightClassType, position()));
    			}
    		  }
    		}
    	}
    	
    	return super.checkAtomicity(tc);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) {
    	TypeSystem ts = tc.typeSystem();
        X10FieldAssign_c n = (X10FieldAssign_c) typeCheckLeft(tc);
        Type t =  n.leftType();
        // Check that the field being assigned to is not a property.
        // Such fields can only be set in a property() statement.
        X10FieldInstance fd = (X10FieldInstance) n.fieldInstance();
        if (fd.isProperty()) {
            SemanticException e = new Errors.CannotAssignToProperty(fd, n.position());
            Errors.issue(tc.job(), e, n);
        }

        // check that a static field is never assigned
        final Flags flags = fd.flags();
        if (flags.isStatic()) {
            Errors.issue(tc.job(), new Errors.CannotAssignToStaticField(fd, n.position));
        }
        // final instance fields can only be assigned via this in a ctor
        if (!flags.isStatic() && flags.isFinal()) {
            boolean isThis = target() instanceof Special && ((Special)target()).kind()==Special.THIS;
            if (!isThis || tc.context().getCtorIgnoringAsync()==null)
				Errors.issue(tc.job(), new Errors.CannotAssignValueToFinalField(fd,n.position));
        }

        if (t == null)
            t =  ts.unknownType(n.position());

    	X10Field_c target = (X10Field_c) n.left();
    	// Not needed in the orthogonal locality proposal.
    	//try {
    	//    target = PlaceChecker.makeFieldAccessLocalIfNecessary(target, tc);
    	//} catch (SemanticException e) {
    	//    Errors.issue(tc.job(), e, this);
    	//}
    	n = (X10FieldAssign_c) n.reconstruct(target.target(), n.name());
    	t = n.leftType();
    	n = (X10FieldAssign_c) n.type(t);
    	return Checker.typeCheckAssign(n, tc);
    }

}
