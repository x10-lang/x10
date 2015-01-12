/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
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
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.X10ClassType;
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
