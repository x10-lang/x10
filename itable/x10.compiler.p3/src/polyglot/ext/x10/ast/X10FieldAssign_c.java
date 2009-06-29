/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.Expr;
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

public class X10FieldAssign_c extends FieldAssign_c {
    
    public X10FieldAssign_c(Position pos, Receiver target, Id name, Operator op, Expr right) {
        super(pos, target, name, op, right);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        return X10LocalAssign_c.typeCheckAssign(this, tc);
    }
}