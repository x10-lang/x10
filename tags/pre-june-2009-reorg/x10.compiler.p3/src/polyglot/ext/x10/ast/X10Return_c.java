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
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10ProcedureDef;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FunctionDef;
import polyglot.types.InitializerDef;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XEQV;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerms;

public class X10Return_c extends Return_c {

	protected boolean implicit;

	public X10Return_c(Position pos, Expr expr, boolean implicit) {
		super(pos, expr);
		this.implicit = implicit;
	}
	
	public Type removeLocals(X10Context ctx, Type t, CodeDef thisCode) {
	    Type b = X10TypeMixin.baseType(t);
	    if (b != t)
	        b = removeLocals(ctx, b, thisCode);
	    XConstraint c = X10TypeMixin.xclause(t);
	    if (c == null)
	        return b;
	    c = removeLocals(ctx, c, thisCode);
	    return X10TypeMixin.xclause(b, c);
	}
	
	public XConstraint removeLocals(X10Context ctx, XConstraint c, CodeDef thisCode) {
	    if (ctx.currentCode() != thisCode) {
	        return c;
	    }
	    X10TypeSystem ts = (X10TypeSystem) ctx.typeSystem();
	    LI:
	        for (LocalDef li : ctx.locals()) {
	            try {
	                if (thisCode instanceof X10ProcedureDef) {
	                    for (LocalDef fi : ((X10ProcedureDef) thisCode).formalNames())
	                        if (li == fi)
	                            continue LI;
	                }
	                XLocal l = ts.xtypeTranslator().trans(li.asInstance());
	                XEQV x = c.genEQV(true);
	                c = c.substitute(x, l);
	            }
	            catch (SemanticException e) {
	            }
	            catch (XFailure e) {
	            }
	        }
	    return removeLocals((X10Context) ctx.pop(), c, thisCode);
	}

	@Override
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		X10Context c = (X10Context) tc.context();
	
		CodeDef ci = c.currentCode();
		
		if (ci == ts.asyncCodeInstance(true) || ci == ts.asyncCodeInstance(false)) {
		    throw new SemanticException("Cannot return from an async.");
		}
		
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
		    
		    if (merge) {
		        if (expr == null) {
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
		                exprType = removeLocals((X10Context) tc.context(), exprType, tc.context().currentCode());
		                Type t = ts.leastCommonAncestor(typeRef.getCached(), exprType, c);
		                typeRef.update(t);
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
		    if (expr != null && implicit && expr.type().isVoid()) {
		    	NodeFactory nf = tc.nodeFactory();
		    	return nf.Block(position(), nf.Eval(expr.position(), expr), nf.Return(position()));
		    }

		    if (expr == null && ! typeRef.getCached().isVoid()) {
			throw new SemanticException("Must return value from non-void method.", position());
		    }
		    if (expr != null && typeRef.getCached().isVoid()) {
			throw new SemanticException("Cannot return value from void method or closure.", position());
		    }
		}
		
		X10Return_c n = this;
		
		if (expr != null) {
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
		        Expr e = X10New_c.attemptCoercion(tc, expr, returnType);
		        n = (X10Return_c) n.expr(e);
		    }
		}
		
		return n.superTypeCheck(tc);
	}

	private Node superTypeCheck(ContextVisitor tc) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        Context c = tc.context();
        
        CodeDef ci = c.currentCode();
        
        if (ci instanceof InitializerDef) {
            throw new SemanticException(
        	"Cannot return from an initializer block.", position());
        }
        
        if (ci instanceof ConstructorDef) {
            if (expr != null) {
        	throw new SemanticException(
        	    "Cannot return a value from " + ci + ".",
        	    position());
            }
        
            return this;
        }
        
        if (ci instanceof FunctionDef) {
            FunctionDef fi = (FunctionDef) ci;
            Type returnType = Types.get(fi.returnType());

            if (returnType == null) {
                throw new InternalCompilerError("Null return type for " + fi);
            }
            
            if (returnType instanceof UnknownType) {
                throw new SemanticException();
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
                    throw new SemanticException("Cannot return a value from " +
                        fi + ".", position());
                }
                else {
                    return this;
                }
            }
            else if (expr == null) {
                throw new SemanticException("Must return a value from " +
                    fi + ".", position());
            }
        
            if (ts.isImplicitCastValid(expr.type(), returnType, c)) {
                return this;
            }
        
            throw new SemanticException("Cannot return expression of type " +
        	expr.type() + " from " + fi + ".", expr.position());
        }
        
        throw new SemanticException("Cannot return from this context.", position());
    }
}
