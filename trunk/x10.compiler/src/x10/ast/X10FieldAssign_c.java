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
import polyglot.ast.Receiver;
import polyglot.ast.Assign.Operator;
import polyglot.types.FieldInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.X10ClassType;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;

import x10.types.X10TypeMixin;
import x10.types.checker.Checker;
import x10.types.checker.PlaceChecker;
import x10.errors.Errors;

public class X10FieldAssign_c extends FieldAssign_c {
    
    public X10FieldAssign_c(Position pos, Receiver target, Id name, Operator op, Expr right) {
        super(pos, target, name, op, right);
    }
    
    @Override
    public Assign typeCheckLeft(ContextVisitor tc) throws SemanticException {
    	X10Context cxt = (X10Context) tc.context();
    	if (cxt.inDepType()) 
    		throw new Errors.NoAssignmentInDepType(this, this.position());
    	
        tc = tc.context(((X10Context) tc.context()).pushAssignment());
        return super.typeCheckLeft(tc);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
    
    	TypeSystem ts = tc.typeSystem();
        X10FieldAssign_c n = (X10FieldAssign_c) typeCheckLeft(tc);
        Type t =  n.leftType();
     // Check that the field being assigned to is not a property. Such fields 
        X10FieldInstance fd = (X10FieldInstance) n.fieldInstance();
        if (fd.isProperty()) {
        	throw new Errors.CannotAssignToProperty(fd, n.position());
        }
        Type targetType =  n.target().type();

        if (t == null)
            t =  ts.unknownType(n.position());

        Expr right = n.right();
        Assign.Operator op = n.operator();

        Type s =  right.type();
        
    	// Check the proto condition.
    	
    	if (X10TypeMixin.isProto(s)) {
    		if (! X10TypeMixin.isProto(targetType)) 
    			throw new Errors.ProtoValuesAssignableOnlyToProtoReceivers(this.right(), this, position());
    		if (op != ASSIGN) 
    			throw new Errors.ProtoValuesAssignableOnlyUsingEquals(right(), position());
    		s = X10TypeMixin.baseOfProto(s);
    		if (! (ts.isSubtype(s, t, tc.context()))) 
    			throw new Errors.CannotAssign(n.right(), n.target().type(), n.position);

    		PlaceChecker.checkFieldPlaceType((Field) left(tc.nodeFactory()), (X10Context) tc.context());
    		
    		n= (X10FieldAssign_c) n.type(t);
    		return n;
    	}
    	
    	
        
        return Checker.typeCheckAssign(n, tc);
    }
    
}