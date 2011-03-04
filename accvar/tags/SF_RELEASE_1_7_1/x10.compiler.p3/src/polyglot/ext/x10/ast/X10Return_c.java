/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.ast;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return_c;
import polyglot.ext.x10.types.ClosureDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FunctionDef;
import polyglot.types.InitializerDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XFormula;
import x10.constraint.XLocal;
import x10.constraint.XTerm;

public class X10Return_c extends Return_c {

	protected boolean implicit;

	public X10Return_c(Position pos, Expr expr, boolean implicit) {
		super(pos, expr);
		this.implicit = implicit;
	}

	@Override
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();
		Context c = tc.context();
	
		CodeDef ci = c.currentCode();
		
		// If the return type is not yet known, set it to the type of the value being returned.
		if (ci instanceof FunctionDef) {
		    FunctionDef fi = (FunctionDef) ci;

		    Type exprType = expr != null ? expr.type() : null;

		    if (exprType instanceof X10ClassType) {
		        X10ClassType ct = (X10ClassType) exprType;
		        if (ct.isAnonymous()) {
		            if (ct.interfaces().size() > 0)
		                exprType = ct.interfaces().get(0);
		            else
		                exprType = ct.superClass();
		        }
		    }
		    
		    // TODO: exprType should only mention variables in scope at the function signature
		    // For closures, this includes local variables in scope at the closure.
		    // For methods and closures, this includes formal parameters (incl. this).

		    boolean merge = false;
		    if (fi instanceof X10MethodDef) {
			merge = ((X10MethodDef) fi).inferReturnType();
		    }
		    if (fi instanceof ClosureDef) {
			merge = ((ClosureDef) fi).inferReturnType();
		    }
		    
		    Ref<Type> typeRef = (Ref<Type>) fi.returnType();
		    
		    if (merge && ! typeRef.known()) {
			if (expr == null) {
			    typeRef.update(ts.Void());
			}
			else {
			    typeRef.update(exprType);
			}
		    }

		    if (typeRef.get().isVoid() && expr != null && implicit) {
			NodeFactory nf = tc.nodeFactory();
			if (expr instanceof Call || expr instanceof New || expr instanceof Assign)
			    return nf.Block(position(), nf.Eval(expr.position(), expr), nf.Return(position()));
		    }

		    if (expr == null && ! typeRef.getCached().isVoid()) {
			throw new SemanticException("Must return value from non-void method.", position());
		    }
		    if (expr != null && typeRef.getCached().isVoid()) {
			throw new SemanticException("Cannot return value from void method.", position());
		    }

		    if (expr != null && merge) {
			// Merge the types
			Type t = ts.leastCommonAncestor(typeRef.getCached(), exprType);
			typeRef.update(t);
		    }
		}
		
		return super.typeCheck(tc);
	}
}
