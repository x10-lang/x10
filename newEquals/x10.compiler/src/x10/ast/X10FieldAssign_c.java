/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
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
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.X10Context;
import x10.types.X10Flags;
import x10.types.X10Type;
import x10.types.X10TypeMixin;

public class X10FieldAssign_c extends FieldAssign_c {
    
    public X10FieldAssign_c(Position pos, Receiver target, Id name, Operator op, Expr right) {
        super(pos, target, name, op, right);
    }
    
    @Override
    public Assign typeCheckLeft(ContextVisitor tc) throws SemanticException {
        tc = tc.context(((X10Context) tc.context()).pushAssignment());
        return super.typeCheckLeft(tc);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
    
    	TypeSystem ts = tc.typeSystem();
        X10FieldAssign_c n = (X10FieldAssign_c) typeCheckLeft(tc);
        X10Type t = (X10Type) n.leftType();
        X10Type targetType = (X10Type) n.target().type();

        if (t == null)
            t = (X10Type) ts.unknownType(n.position());

        Expr right = n.right();
        Assign.Operator op = n.operator();

        X10Type s = (X10Type) right.type();
        
    	// Check the proto condition.
    	
    	if (s.isProto()) {
    		if (! targetType.isProto()) {
    			throw new SemanticException("The receiver " + this.target() + 
    					" does not have a proto type; hence " + 
    					this.right() + 
    					" cannot be assigned to it's field " + this.name(), 
    					this.position());
    		}
    		if (op != ASSIGN) {
    			throw new SemanticException("Only the assignment operator = can be used to "
    					+ " assign the proto value " + this.right() +
    					" to a field.", position());
    		}
    		s = s.clearFlags(X10Flags.PROTO);
    		if (! (ts.isSubtype(s, t, tc.context()))) {
    			throw new SemanticException("Cannot assign " + s + " to " + t + ".",
        				n.position());
    			
    		}
    		n.checkFieldPlaceType(tc);
    		
    		n= (X10FieldAssign_c) n.type(t);
    	}
        
        return X10LocalAssign_c.typeCheckAssign(n, tc);
    }
    
    public void checkFieldPlaceType(ContextVisitor tc) throws SemanticException {
    	((X10Field_c) left(tc.nodeFactory())).checkFieldPlaceType(tc);
    }
}