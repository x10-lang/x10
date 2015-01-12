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

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorCall_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.ErrorRef_c;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10ClassDef;
import x10.types.X10ConstructorDef_c;

import x10.types.constraints.CConstraint;

/**
 * A call to this(...) or super(...) in the body of a constructor.
 * (The call new C(...) is represented by an X10New_c.)
 * @author vj
 *
 */
public class X10ConstructorCall_c extends ConstructorCall_c implements X10ConstructorCall {

	/**
	 * @param pos
	 * @param kind
	 * @param qualifier
	 * @param arguments
	 */
	public X10ConstructorCall_c(Position pos, Kind kind, Expr qualifier,
		List<TypeNode> typeArguments, List<Expr> arguments) {
		super(pos, kind, qualifier, arguments);
		this.typeArguments = typeArguments;
	}
	
	// Override to remove reference to ts.Object(), which will cause resolver loop.
	@Override
	public Node buildTypes(TypeBuilder tb) {
	    TypeSystem ts = tb.typeSystem();

	    if (kind == THIS) {
	        X10ConstructorDef cd = AssignPropertyCall_c.getConstructorDef(tb);
	        if (cd != null) {
	            cd.derivedReturnType(true);
	        }
	    }

	    ConstructorCall_c n = this;

	    ConstructorInstance ci = ts.createConstructorInstance(position(), position(), new ErrorRef_c<ConstructorDef>(ts, position(), "Cannot get ConstructorDef before type-checking constructor call."));
	    return n.constructorInstance(ci);
	}
	
	@Override
	public Node visitChildren(NodeVisitor v) {
		Expr qualifier = (Expr) visitChild(this.qualifier, v);
		Expr target = (Expr) visitChild(this.target, v);
		List<TypeNode> typeArguments = visitList(this.typeArguments, v);
		List<Expr> arguments = visitList(this.arguments, v);
		X10ConstructorCall_c n = (X10ConstructorCall_c) typeArguments(typeArguments);
		return n.reconstruct(qualifier, target, arguments);
	}
	
	List<TypeNode> typeArguments;
	public List<TypeNode> typeArguments() { return typeArguments; }
	public X10ConstructorCall typeArguments(List<TypeNode> args) {
	    X10ConstructorCall_c n = (X10ConstructorCall_c) copy();
	    n.typeArguments = new ArrayList<TypeNode>(args);
	    return n;
	}

	@Override
	public X10ConstructorCall qualifier(Expr qualifier) {
	    return (X10ConstructorCall) super.qualifier(qualifier);
	}
	@Override
	public X10ConstructorCall kind(Kind kind) {
	    return (X10ConstructorCall) super.kind(kind);
	}
	@Override
	public X10ConstructorCall arguments(List<Expr> arguments) {
	    return (X10ConstructorCall) super.arguments(arguments);
	}
	@Override
	public X10ConstructorInstance constructorInstance() {
	    return (X10ConstructorInstance) super.constructorInstance();
	}
	@Override
	public X10ConstructorCall constructorInstance(ConstructorInstance ci) {
	    return (X10ConstructorCall) super.constructorInstance(ci);
	}

        @Override
	public Node typeCheck(ContextVisitor tc) {

	    X10ConstructorInstance ci;
	    List<Expr> args;
	    X10ConstructorCall_c n = this;
	    
	        TypeSystem ts = (TypeSystem) tc.typeSystem();

	        Context context = tc.context();
            ClassType ct = context.currentClass();
	        Type superType = ct.superClass();
	        if (kind == SUPER && superType == null) {
	        	// this can happen for structs, and for Object
	        	Type type =  context.currentClass();

	        	// the super() call inserted by the parser needs to be thrown out
        		NodeFactory nf = (NodeFactory) tc.nodeFactory();
        		return nf.Empty(Position.compilerGenerated(position()));
	        }

	        // The qualifier specifies the enclosing instance of this inner class.
	        // The type of the qualifier must be the outer class of this
	        // inner class or one of its super types.
	        //
	        // Example:
	        //
	        // class Outer {
	        //     class Inner { }
	        // }
	        //
	        // class ChildOfInner extends Outer.Inner {
	        //     ChildOfInner() { (new Outer()).super(); }
	        // }
	        if (qualifier != null) {
	            if (kind != SUPER) {
	                Errors.issue(tc.job(),
	                        new Errors.CanOnlyQualifySuperConstructorInvocation(position()));
	            }

	            if (!superType.isClass() || !superType.toClass().isInnerClass() ||
	                    superType.toClass().inStaticContext()) {
	                Errors.issue(tc.job(),
	                        new Errors.ClassNotInnerClass(superType, position()));
	            }

	            Type qt = qualifier.type();

	            if (! qt.isClass() || !qt.isSubtype(superType.toClass().container(), context)) {
	                Errors.issue(tc.job(),
	                        new Errors.QualifierDoesNotMatchEnclosingClass(qt, superType.toClass().container(), qualifier.position()),
	                        this);
	            }
	        }

	        if (kind == SUPER) {
	            if (! superType.isClass() && !ts.isUnknown(superType)) {
	                Errors.issue(tc.job(),
	                        new Errors.SuperTypeIsNotAClass(ct, position()));
	            }

	            Expr q = qualifier;

	            // If the super class is an inner class (i.e., has an enclosing
	            // instance of its container class), then either a qualifier 
	            // must be provided, or ct must have an enclosing instance of the
	            // super class's container class, or a subclass thereof.
	            if (q == null && superType.isClass() && superType.toClass().isInnerClass()) {
	                ClassType superContainer = (ClassType) superType.toClass().container();
	                // ct needs an enclosing instance of superContainer, 
	                // or a subclass of superContainer.
	                ClassType e = ct;

	                while (e != null) {
	                    if (e.isSubtype(superContainer, context) && ct.hasEnclosingInstance(e)) {
	                        NodeFactory nf = tc.nodeFactory();
	                        q = nf.This(position(), nf.CanonicalTypeNode(position(), e)).type(e);

	                        break; 
	                    }
	                    e = e.outer();
	                }

	                if (e == null) {
	                    Errors.issue(tc.job(),
	                            new Errors.ClassTypeMustHaveEnclosingInstance(ct, superContainer, position()));
	                }               
	                if (e == ct) {
	                    Errors.issue(tc.job(),
	                            new Errors.ClassTypeMustBeSpecifiedInSuperConstructor(ct, superContainer, position()));
	                }
	            }

	            if (qualifier != q)
	                n = (X10ConstructorCall_c) n.qualifier(q);
	        }

	        List<Type> argTypes = new ArrayList<Type>();

	        for ( Expr e : n.arguments()) {
	            argTypes.add(e.type());
	        }

	        if (kind == SUPER) {
	            ct = ct.superClass().toClass();
	        }
	    
	    try {
	        ci = (X10ConstructorInstance) ts.findConstructor(ct, ts.ConstructorMatcher(ct, argTypes, context));
	        args = n.arguments();
	    }
	    catch (SemanticException e) {
	        // Now, try to find the method with implicit conversions, making them explicit.
	        try {
	            Pair<ConstructorInstance,List<Expr>> p = X10New_c.tryImplicitConversions(n, tc, ct, argTypes);
	            ci = (X10ConstructorInstance) p.fst();
	            args = p.snd();
	        }
	        catch (SemanticException e2) {
	            Pair<ConstructorInstance,List<Expr>> p = X10New_c.findConstructor(tc, n, ct, argTypes);
	            ci = (X10ConstructorInstance) p.fst();
	            args = p.snd();
	        }
	    }

	    if (!ci.checkConstraintsAtRuntime()) {
	        Warnings.checkErrorAndGuard(tc, ci, n);
	    }

	    n = (X10ConstructorCall_c) n.constructorInstance(ci);
	    n = (X10ConstructorCall_c) n.arguments(args);

	    if (n.kind().equals(ConstructorCall.SUPER)) {
	        Context ctx = context;
	        if (! (ctx.inCode()) || ! (ctx.currentCode() instanceof X10ConstructorDef)) {
	            Errors.issue(tc.job(),
	                    new SemanticException("A call to super must occur only in the body of a constructor.", position()));
	        } else {
	            // The constructor *within which this super call happens*.
	            X10ConstructorDef thisConstructor = (X10ConstructorDef) ctx.currentCode();
	            Type returnType = ci.returnType();
	            CConstraint c = Types.realX(returnType);
	            thisConstructor.setSupType(returnType);
	            
	            // Also make this information available downstream within this constructor.
	            // Need to do this here because the constructor may not have a property clause.
	            CConstraint cc = ctx.currentConstraint();
	            cc.addIn(thisConstructor.thisVar(), c);
	            ctx.setCurrentConstraint(cc);
	        }
	    }

		return n;
	}

    public static void checkSuperType(ContextVisitor tc, Type returnType, Position position) {
        Context context = tc.context();
        X10ClassDef classDef = context.currentClassDef();
        Type extendsType = Types.get(classDef.superType());
        if (extendsType!=null && returnType!=null && !returnType.isSubtype(extendsType, context) ) {
            Errors.issue(tc.job(),
                    new Errors.SuperCallCannotEstablishSuperType(returnType,extendsType, position));
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(qualifier != null ? qualifier + "." : "");
        sb.append(kind);
        /*
        if (typeArguments != null && typeArguments.size() > 0) {
            sb.append("[");
            sb.append(CollectionUtil.listToString(typeArguments));
            sb.append("]");
        }
        */
        sb.append("(");
        sb.append(CollectionUtil.listToString(arguments));
        sb.append(")");
        return sb.toString();
    }

    /* (non-Javadoc)
     * @see polyglot.ast.ConstructorCall#target(polyglot.ast.Expr)
     */
    @Override
    public X10ConstructorCall target(Expr target) {
        return (X10ConstructorCall) super.target(target);
    }

    public List<Type> throwTypes(TypeSystem ts) {
        List<Type> l = new ArrayList<Type>();
        l.addAll(ci.throwTypes());
        l.addAll(ts.uncheckedExceptions());
        return l;
    }

}
