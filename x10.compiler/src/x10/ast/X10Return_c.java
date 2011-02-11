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

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return_c;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FunctionDef;
import polyglot.types.InitializerDef;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerms;
import x10.errors.Errors;
import x10.types.ClosureDef;
import x10.types.X10ClassType;
import polyglot.types.Context;
import x10.types.X10MethodDef;
import x10.types.X10ProcedureDef;
import polyglot.types.TypeSystem;

import x10.types.X10Context_c;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;

public class X10Return_c extends Return_c {

	protected boolean implicit;

	public X10Return_c(Position pos, Expr expr, boolean implicit) {
		super(pos, expr);
		this.implicit = implicit;
	}
	
	public static Type removeLocals(Context ctx, Type t, CodeDef thisCode) {
	    Type b = Types.baseType(t);
	    if (b != t)
	        b = removeLocals(ctx, b, thisCode);
	    CConstraint c = Types.xclause(t);
	    if (c == null)
	        return b;
	    c = removeLocals(ctx, c, thisCode);
	    return Types.xclause(b, c);
	}
	
	public static CConstraint removeLocals(Context ctx, CConstraint c, CodeDef thisCode) {
	    if (ctx.currentCode() != thisCode) {
	        return c;
	    }
	    TypeSystem ts = (TypeSystem) ctx.typeSystem();
	    LI:
	        for (LocalDef li : ctx.locals()) {
	            try {
	                if (thisCode instanceof X10ProcedureDef) {
	                    for (LocalDef fi : ((X10ProcedureDef) thisCode).formalNames())
	                        if (li == fi)
	                            continue LI;
	                }
	                XLocal l = ts.xtypeTranslator().translate(li.asInstance());
	                XEQV x = XTerms.makeEQV();
	                c = c.substitute(x, l);
	            }
	            catch (XFailure e) {
	            }
	        }
	    return removeLocals((Context) ctx.pop(), c, thisCode);
	}

	@Override
	public Node typeCheck(ContextVisitor tc) {
		TypeSystem ts = (TypeSystem) tc.typeSystem();
		Context c = (Context) tc.context();
		
		CodeDef ci = c.currentCode();
		
		if (((X10Context_c)c).inAsyncScope()) { // can return from an at but not from an async
		    Errors.issue(tc.job(), new Errors.CannotReturnFromAsync(position()), this);
		    return this;
		}

		X10Return_c n = this;
        
		Type exprType = n.expr() != null ? n.expr().type() : null;


		// In the exprType, we may have replaced here by PlaceTerm from the context
		// in support of checking references to this object across place boundaries within
		// the code for this method. 
		// Now this value is being returned from this method.
		// Replace PlaceTerm from the context with here so that exprType will 
		// correctly be of ! type in the calling environment (which does not know about PlaceTerm.
		if (exprType != null) {
			exprType = PlaceChecker.ReplacePlaceTermByHere(exprType, tc.context());

			n = (X10Return_c) n.expr(n.expr().type(exprType));
		}
		    
		// If the return type is not yet known, set it to the type of the value being returned.
		if (ci instanceof FunctionDef) {
		    FunctionDef fi = (FunctionDef) ci;
		    
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
		    
		    if (merge) {
		        if (n.expr() == null) {
		            if (! typeRef.known()) {
		                typeRef.update(ts.Void());
		            }
		        }
		        else {
		            if (! typeRef.known()) {
		                typeRef.update(exprType);
		            }
		            else {
		                // Merge the types
		                exprType = removeLocals((Context) tc.context(), exprType, tc.context().currentCode());
		                try {
		                    Type t = ts.leastCommonAncestor(typeRef.getCached(), exprType, c);
		                    typeRef.update(t);
		                } catch (SemanticException e) {
		                    Errors.issue(tc.job(), e, n);
		                }
		            }
		        }
		    }

		   /* vj: Commented out -- per X10 1.7 manual a void method or closure body
		    * cannot have a terminating expression.
		    * See test 
		    * examples/Constructs/Closures/ClosureReturn5_MustFailCompile.
		    * if (typeRef.get().isVoid() && expr != null && implicit) {
		    	NodeFactory nf = tc.nodeFactory();
		    	if (expr instanceof Call || expr instanceof New || expr instanceof Assign)
		    		return nf.Block(position(), nf.Eval(expr.position(), expr), nf.Return(position()));
		    }
		    */
		    // Note that for the code def m(args) = e;
		    // the e is translated into a return e;
		    // Now we must make sure that if e is of type void, then
		    // the return e; is replaced by {eval(e); return;}
		    if (n.expr() != null && n.implicit && n.expr().type().isVoid()) {
		    	NodeFactory nf = tc.nodeFactory();
		    	return nf.Block(n.position(), nf.Eval(n.expr().position(), n.expr()), nf.Return(n.position()));
		    }

		    if (n.expr() == null && ! typeRef.getCached().isVoid()) {
		        Errors.issue(tc.job(),
		                new Errors.MustReturnValueFromNonVoidMethod(n.position()));
		    }
		    if (n.expr() != null && typeRef.getCached().isVoid()) {
		        Errors.issue(tc.job(),
		                new Errors.CannotReturnValueFromVoidMethod(n.position()));
		    }
		}
		
		if (n.expr() != null) {
		    if (ci instanceof FunctionDef) {
		        FunctionDef fi = (FunctionDef) ci;
		        Type returnType = Types.get(fi.returnType());
		        
//		        if (fi instanceof X10MemberDef) {
//		            XRoot classThisVar = ((X10ClassDef) c.currentClassDef()).thisVar();
//		            XRoot methodThisVar = ((X10MemberDef) fi).thisVar();
//		            if (classThisVar != null && methodThisVar != null) {
//		                returnType = X10MethodInstance_c.subst(returnType, new XVar[] { classThisVar }, new XRoot[] { methodThisVar });
//		                Type t = expr.type();
//		                t = X10MethodInstance_c.subst(t, new XVar[] { classThisVar }, new XRoot[] { methodThisVar });
//		                Expr e = X10New_c.attemptCoercion(tc, expr.type(t), returnType);
//		                n = (X10Return_c) n.expr(e);
//		                return n.superTypeCheck(tc);
//		            }
//		        }
		        Expr e = Converter.attemptCoercion(tc, n.expr(), returnType);
		        if (e != null)
		            n = (X10Return_c) n.expr(e);
		    }
		}
		
		try {
		    return n.superTypeCheck(tc);
		} catch (SemanticException e) {
		    Errors.issue(tc.job(), e, n);
		    return n;
		}
	}

	private Node superTypeCheck(ContextVisitor tc) throws SemanticException {
	    TypeSystem ts = (TypeSystem) tc.typeSystem();
        Context c = tc.context();
        
        CodeDef ci = c.currentCode();
        
        if (ci instanceof InitializerDef) {
            throw new SemanticException("Cannot return from an initializer block.", position());
        }
        
        if (ci instanceof ConstructorDef) {
            if (expr != null) {
        	throw new SemanticException("Cannot return a value from " + ci + ".",position());
            }
        
            return this;
        }
        
        if (ci instanceof FunctionDef) {
            FunctionDef fi = (FunctionDef) ci;
            Type returnType = Types.get(fi.returnType());

            if (returnType == null) {
                throw new InternalCompilerError("Null return type for " + fi);
            }
            
//            if (fi instanceof X10MemberDef) {
//                XRoot classThisVar = ((X10ClassDef) c.currentClassDef()).thisVar();
//                XRoot methodThisVar = ((X10MemberDef) fi).thisVar();
//                if (classThisVar != null && methodThisVar != null) {
//                    returnType = X10MethodInstance_c.subst(returnType, new XVar[] { classThisVar }, new XRoot[] { methodThisVar });
//                }
//            }
        
            if (returnType.isVoid()) {
                if (expr != null) {
                    throw new SemanticException("Cannot return a value from " +fi + ".", position());
                }
                else {
                    return this;
                }
            }
            else if (expr == null) {
                throw new SemanticException("Must return a value from " +fi + ".", position());
            }
        
            if (ts.isImplicitCastValid(expr.type(), returnType, c)) {
                return this;
            }
            throw new Errors.CannotReturnExpr(expr.type(), returnType, expr.position());
        }
        
        throw new SemanticException("Cannot return from this context.", position());
    }
}
