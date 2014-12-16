/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.ErrorRef_c;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;

import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.types.FunctionType;
import x10.types.MethodInstance;
import polyglot.types.TypeSystem;
import x10.types.checker.Checker;
import x10.types.checker.Converter;
import x10.visit.X10TypeChecker;

public class ClosureCall_c extends Expr_c implements ClosureCall {
	protected Expr target;
	protected List<Expr> arguments;
	protected List<TypeNode> typeArguments;

	protected MethodInstance ci;

	public ClosureCall_c(Position pos, Expr target, List<TypeNode> typeArguments, List<Expr> arguments) {
		super(pos);
		assert target != null;
		assert typeArguments != null;
		assert arguments != null;
		this.target = target;
		this.typeArguments = TypedList.copyAndCheck(typeArguments, TypeNode.class, true);
		this.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
	}

	@Override
	public Precedence precedence() {
		return Precedence.LITERAL;
	}

	public Term firstChild() {
		if (!(target instanceof Closure)) {
			return ((Term) target);
		}
		return  listChild(arguments, null);
	}

	@Override
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		List<Term> args = new ArrayList<Term>();
		args.addAll(typeArguments);
		args.addAll(arguments);

		if (!(target instanceof Closure)) { // Don't visit a literal closure here
			Term t = (Term) target;
		if (args.isEmpty()) {
			v.visitCFG(t, this, EXIT);
		}
		else {
			v.visitCFG(t, listChild(args, null), ENTRY);
		}
		}
		v.visitCFGList(args, this, EXIT);
		return succs;
	}

	public Expr target() {
		return target;
	}

	public ClosureCall target(Expr target) {
		assert target != null;
		ClosureCall_c n= (ClosureCall_c) copy();
		n.target= target;
		return n;
	}

	/** Get the method instance of the call. */
	public MethodInstance closureInstance() {
		return this.ci;
	}

	/** Set the method instance of the call. */
	public ClosureCall closureInstance(MethodInstance ci) {
		if (ci == this.ci)
			return this;
		ClosureCall_c n = (ClosureCall_c) copy();
		n.ci= ci;
		return n;
	}

	public ProcedureInstance<?> procedureInstance() {
		return this.ci;
	}

	public ClosureCall procedureInstance(ProcedureInstance<? extends ProcedureDef> pi) {
		return closureInstance((MethodInstance) pi);
	}

	/** Get the actual arguments of the call. */
	public List<Expr> arguments() {
		return this.arguments;
	}

	/** Set the actual arguments of the call. */
	public ClosureCall arguments(List<Expr> arguments) {
		assert arguments != null;
		ClosureCall_c n= (ClosureCall_c) copy();
		n.arguments= TypedList.copyAndCheck(arguments, Expr.class, true);
		return n;
	}

	/** Get the type arguments of the call. */
	public List<TypeNode> typeArguments() {
		return this.typeArguments;
	}

	/** Set the type arguments of the call. */
	public ClosureCall typeArguments(List<TypeNode> typeArguments) {
	    assert typeArguments != null;
	    ClosureCall_c n= (ClosureCall_c) copy();
	    n.typeArguments= TypedList.copyAndCheck(typeArguments, TypeNode.class, true);
	    return n;
	}

	/** Reconstruct the call. */
	protected ClosureCall_c reconstruct(Expr target, List<TypeNode> typeArguments, List<Expr> arguments) {
		if (target != this.target 
				|| !CollectionUtil.allEqual(typeArguments, this.typeArguments) 
				|| !CollectionUtil.allEqual(arguments, this.arguments)) {
			ClosureCall_c n= (ClosureCall_c) copy();
			n.target= target;
			n.typeArguments= TypedList.copyAndCheck(typeArguments, TypeNode.class, true);
			n.arguments= TypedList.copyAndCheck(arguments, Expr.class, true);
			return n;
		}
		return this;
	}

	/** Visit the children of the call. */
	public Node visitChildren(NodeVisitor v) {
		Expr target = (Expr) visitChild(this.target, v);
		List typeArguments = visitList(this.typeArguments, v);
		List<Expr> arguments = visitList(this.arguments, v);
		return reconstruct(target, typeArguments, arguments);
	}

	public Node buildTypes(TypeBuilder tb) {
		ClosureCall_c n= (ClosureCall_c) super.buildTypes(tb);

		TypeSystem ts = (TypeSystem) tb.typeSystem();

		MethodInstance mi = (MethodInstance) ts.createMethodInstance(position(), position(),
				new ErrorRef_c<MethodDef>(ts, position(), 
						"Cannot get MethodDef before type-checking closure call."));
		return n.closureInstance(mi);
	}

	@Override
	public Node typeCheck(ContextVisitor tc) {
		Type targetType = target.type();

		List<Type> typeArgs = new ArrayList<Type>(this.typeArguments.size());
		for (TypeNode ti : typeArguments) {
		    typeArgs.add(ti.type());
		}
		List<Type> actualTypes = new ArrayList<Type>(this.arguments.size());
		for (Expr ei : arguments) {
			actualTypes.add(ei.type());
		}

		// First try to find the method without implicit conversions.
		MethodInstance mi = Checker.findAppropriateMethod(tc, targetType, APPLY, typeArgs, actualTypes);
		List<Expr> args = this.arguments;
		if (mi.error() != null) {
			// Now, try to find the method with implicit conversions, making them explicit.
			try {
				Pair<MethodInstance,List<Expr>> p = Checker.tryImplicitConversions(this, tc, targetType, APPLY, typeArgs, actualTypes);
				mi = p.fst();
				args = p.snd();
			}
			catch (SemanticException e) {
			    // mi.error() will be reported in checkErrorAndGuard
			}
		}

		Warnings.checkErrorAndGuard(tc,mi,this);

		// Find the most-specific closure type.
//		ClosureCall n = this;
//		n = n.arguments(args);
//		return n.closureInstance(mi).type(mi.returnType());
		if (mi.container() instanceof FunctionType) {
			ClosureCall_c n = this;
			n = (ClosureCall_c) n.arguments(args);
			return n.closureInstance(mi).type(mi.returnType());
		}
		else {
			// FIXME: deleting this code block breaks the Java code generator.  Investigate.
			// TODO: add ts.Function and uncomment this.
			// Check that the target actually is of a function type of the appropriate type and doesn't just coincidentally implement an
			// apply method.
			//	    ClosureType ct = ts.Function(mi.typeParameters(), mi.formalTypes(), mi.returnType());
			//	    if (! targetType.isSubtype(ct))
			//		throw new SemanticException("Invalid closure call; target does not implement " + ct + ".", position());
			NodeFactory nf = (NodeFactory) tc.nodeFactory();
			X10Call_c n = (X10Call_c) nf.X10Call(position(), target(), 
					nf.Id(Position.compilerGenerated(position()), mi.name().toString()), typeArguments, args);
			n = (X10Call_c) n.methodInstance(mi).type(mi.returnType());
			return n;
		}
	}

	public String toString() {
		StringBuffer buff= new StringBuffer();
		buff.append(target)
		.append("(");
		for(Iterator<Expr> iter= arguments.iterator(); iter.hasNext(); ) {
			Expr arg= (Expr) iter.next();
			buff.append(arg);
			if (iter.hasNext()) buff.append(", ");
		}
		buff.append(")");
		return buff.toString();
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.begin(0);
		printSubExpr((Expr) target, w, tr);
		w.write("(");
		if (arguments.size() > 0) {
			w.allowBreak(2, 2, "", 0); // miser mode
			w.begin(0);
			for(Iterator<Expr> i = arguments.iterator(); i.hasNext();) {
				Expr e = (Expr) i.next();
				print(e, w, tr);
				if (i.hasNext()) {
					w.write(",");
					w.allowBreak(0, " ");
				}
			}
			w.end();
		}
		w.write(")");
		w.end();
	}
}
