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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Assign.Operator;
import polyglot.frontend.Globals;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.ErrorRef_c;
import polyglot.types.Flags;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;

import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.TypeSystem_c.MethodMatcher;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.types.X10ClassDef;
import x10.types.MethodInstance;
import polyglot.types.TypeSystem;

import x10.types.checker.Checker;
import x10.types.checker.Converter;
import x10.visit.X10TypeChecker;

/** An immutable representation of an X10 array access update: a[point] op= expr;
 * TODO
 * Typechecking rules:
 * (1) point must be of a type (region) that can be cast to the array index type.
 * (2) expr must be of a type that can be implicitly cast to the base type of the array.
 * (3) The operator, if any, must be permitted on the underlying type.
 * (4) No assignment is allowed on a value array.
 * @author vj Dec 9, 2004
 * 
 */
public class SettableAssign_c extends Assign_c implements SettableAssign {
   	protected Expr array;
   	protected List<Expr> index;
   
	/**
	 * @param pos
	 * @param left
	 * @param op
	 * @param right
	 */
    public SettableAssign_c(NodeFactory nf, Position pos, Expr array, List<Expr> index, Operator op, Expr right) {
		super(nf, pos, op, right);
		this.array = array;
		this.index = index;
	}
	
	public Type leftType() {
	    if (mi == null) return null;
	    return mi.formalTypes().get(mi.formalTypes().size()-1);
	}

	public Expr left() {
		return left(nf, null);
	}
	//@Override
	public Expr left(NodeFactory nf, ContextVisitor cv) {
	    Call c = nf.Call(position(), array, nf.Id(position(), ClosureCall.APPLY), index);
	    if (ami != null) {
	        c = c.methodInstance(ami);
	    }
	    if (type != null && ami != null) c = (Call) c.type(ami.returnType());
	    return c;
	}
	
   	/** Get the precedence of the expression. */
   	public Precedence precedence() {
   		return Precedence.LITERAL;
   	}
   	
   	/** Get the array of the expression. */
   	public Expr array() {
   		return this.array;
   	}
   	
   	/** Set the array of the expression. */
   	public SettableAssign array(Expr array) {
   	 SettableAssign_c n = (SettableAssign_c) copy();
   		n.array = array;
   		return n;
   	}
   	
   	/** Get the index of the expression. */
   	public List<Expr> index() {
   		return TypedList.copy(this.index, Expr.class, false);
   	}
   	
   	/** Set the index of the expression. */
   	public SettableAssign index(List<Expr> index) {
   	    SettableAssign_c n = (SettableAssign_c) copy();
   	    n.index = TypedList.copyAndCheck(index, Expr.class, true);
   	    return n;
   	}
   	
   	/** Reconstruct the expression. */
   	protected SettableAssign_c reconstruct( Expr array, List<Expr> index ) {
   		if (array != this.array || index != this.index) {
   		 SettableAssign_c n = (SettableAssign_c) copy();
   			n.array = array;
   			n.index = TypedList.copyAndCheck(index, Expr.class, true);
   			return n;
   		}
   		return this;
   	}
   	/** Return the access flags of the variable. */
   	public Flags flags() {
   		return Flags.NONE;
   	}
   	
   	
   	/** Visit the children of the expression. */
   	public Assign visitLeft(NodeVisitor v) {
   		Expr array = (Expr) visitChild(this.array, v);
   		List<Expr> index =  visitList(this.index, v);
   		return reconstruct(array, index);
   	}
   	
	MethodInstance mi;
	MethodInstance ami; // the apply method is searched even for SettableAssign if the operator is not "=", e.g., a(1) += 2; If it is just assignment, then ami will be null, e.g., a(1)=2;
	
	public MethodInstance methodInstance() {
	    return mi;
	}
	public SettableAssign methodInstance(MethodInstance mi) {
	    SettableAssign_c n = (SettableAssign_c) copy();
	    n.mi = mi;
	    return n;
	}
	public MethodInstance applyMethodInstance() {
	    return ami;
	}
	public SettableAssign applyMethodInstance(MethodInstance ami) {
	    SettableAssign_c n = (SettableAssign_c) copy();
	    n.ami = ami;
	    return n;
	}
	
	@Override
	public Node buildTypes(TypeBuilder tb) {
	    SettableAssign_c n = (SettableAssign_c) super.buildTypes(tb);

	    TypeSystem ts = (TypeSystem) tb.typeSystem();

	    MethodInstance mi = ts.createMethodInstance(position(), position(),new ErrorRef_c<MethodDef>(ts, position(), "Cannot get MethodDef before type-checking settable assign."));
	    MethodInstance ami = ts.createMethodInstance(position(), position(), new ErrorRef_c<MethodDef>(ts, position(), "Cannot get MethodDef before type-checking settable assign."));
	    return n.methodInstance(mi).applyMethodInstance(ami);
	}

	static Pair<MethodInstance,List<Expr>> tryImplicitConversions(X10Call_c n, ContextVisitor tc,
	        Type targetType, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
	    final TypeSystem ts = (TypeSystem) tc.typeSystem();
	    final Context context = tc.context();

	    List<MethodInstance> methods = ts.findAcceptableMethods(targetType,
	            ts.MethodMatcher(targetType, SettableAssign.SET, typeArgs, argTypes, context, true));

	    Pair<MethodInstance,List<Expr>> p = Converter.<MethodDef,MethodInstance>tryImplicitConversions(n, tc,
	            targetType, methods, new X10New_c.MatcherMaker<MethodInstance>() {
	        public Matcher<MethodInstance> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes) {
	            return ts.MethodMatcher(ct, SettableAssign.SET, typeArgs, argTypes, context);
	        }
	    });

	    return p;
	}

	@Override
	public Assign typeCheckLeft(ContextVisitor tc) {
		TypeSystem ts = (TypeSystem) tc.typeSystem();
		NodeFactory nf = (NodeFactory) tc.nodeFactory();
		TypeSystem xts = ts;

		MethodInstance mi = null;

		List<Type> typeArgs = Collections.<Type>emptyList();
		List<Type> actualTypes = new ArrayList<Type>(index.size()+1);
		for (Expr ei : index) {
		    actualTypes.add(ei.type());
		}

		List<Expr> args = new ArrayList<Expr>();
		args.addAll(index);

		MethodInstance ami = null;
		Expr right = this.right;
		Expr val = this.right;

		// FIXME: try to find the method with implicit conversions if this fails.
		ami = Checker.findAppropriateMethod(tc, array.type(), ClosureCall.APPLY, typeArgs, actualTypes);

		if (op != Assign.ASSIGN) {
		    X10Call left = nf.X10Call(position(), array, nf.Id(position(),
		            ClosureCall.APPLY), Collections.<TypeNode>emptyList(),
		            index);
            if (ami.error() != null) {
		        // Now, try to find the method with implicit conversions, making them explicit.
		        try {
		            Pair<MethodInstance,List<Expr>> p = Checker.tryImplicitConversions(left, tc, array.type(), ClosureCall.APPLY, typeArgs, actualTypes);
		            ami =  p.fst();
		        } catch (SemanticException se) { }
		    }
            left = (X10Call_c) left.methodInstance(ami).type(ami.returnType());
		    if (ami.error() != null) { // it's an error only if op is not =, e.g., a(1)+=1;
		        Type bt = Types.baseType(array.type());
		        boolean arrayP = xts.isX10RegionArray(bt) || xts.isX10RegionDistArray(bt);
		        Errors.issue(tc.job(), new Errors.CannotAssignToElement(leftToString(), arrayP, right, Types.arrayElementType(array.type()), position(), ami.error()));
		    }
		    X10Binary_c n = (X10Binary_c) nf.Binary(position(), left, op.binaryOperator(), right);
		    X10Call c = X10Binary_c.desugarBinaryOp(n, tc);
		    MethodInstance cmi = (MethodInstance) c.methodInstance();
		    if (cmi.error() != null) {
		        Type bt = Types.baseType(array.type());
		        // arrayP is used to tweak the error message used
		        boolean arrayP = xts.isX10RegionArray(bt) || xts.isX10RegionDistArray(bt);
		        Errors.issue(tc.job(),
		                new Errors.CannotPerformAssignmentOperation(leftToString(), arrayP, op.toString(), right, Types.arrayElementType(array.type()), position(), cmi.error()));
		    }
		    if (cmi.flags().isStatic()) {
		        right = c.arguments().get(1);
		    } else if (c.name().id().equals(X10Binary_c.invBinaryMethodName(n.operator()))) {
		        right = (Expr) c.target();
		    } else {
		        right = c.arguments().get(0);
		    }
		    val = c;
		}

		actualTypes.add(val.type());
		args.add(val);

		// First try to find the method without implicit conversions.
		mi = Checker.findAppropriateMethod(tc, array.type(), SET, typeArgs, actualTypes);
		if (mi.error() != null) {
		    // Now, try to find the method with implicit conversions, making them explicit.
		    try {
		        X10Call_c n = (X10Call_c) nf.X10Call(position(), array, nf.Id(position(), SET), Collections.<TypeNode>emptyList(), args);
		        Pair<MethodInstance,List<Expr>> p = tryImplicitConversions(n, tc, array.type(), typeArgs, actualTypes);
		        mi =  p.fst();
		        args = p.snd();
		    }
		    catch (SemanticException e) {
		        if (mi.error() instanceof Errors.CannotGenerateCast) {
		            throw new InternalCompilerError("Unexpected cast error", mi.error());
		        }
		        Type bt = Types.baseType(array.type());
		        boolean arrayP = xts.isX10RegionArray(bt) || xts.isX10RegionDistArray(bt);
		        Errors.issue(tc.job(), new Errors.CannotAssignToElement(leftToString(), arrayP, right, Types.arrayElementType(array.type()), position(), mi.error()));
		    }
		}

		if (op == Assign.ASSIGN) {
		    right = args.get(args.size()-1);
		}

		if (mi.flags().isStatic() ) {
		    Errors.issue(tc.job(), new Errors.AssignSetMethodCantBeStatic(mi, array, position()));
		}

		SettableAssign_c a = this;
		a = (SettableAssign_c) a.methodInstance(mi);
		a = (SettableAssign_c) a.applyMethodInstance(ami);
		a = (SettableAssign_c) a.right(right);
		a = (SettableAssign_c) a.index(args.subList(0, args.size()-1));
		return a;
	}
	
	@Override
	public Node typeCheck(ContextVisitor tc) {
	    SettableAssign_c a = (SettableAssign_c) x10.types.checker.Checker.typeCheckAssign(this, tc);
	    return a.type(a.mi.returnType());
	}
	
	public Term firstChild() {
	    return array;
	}
	
	protected void acceptCFGAssign(CFGBuilder v) {
		v.visitCFG(array, listChild(index, right()), ENTRY);
		v.visitCFGList(index, right(), ENTRY);
		v.visitCFG(right(), this, EXIT);
	}
	protected void acceptCFGOpAssign(CFGBuilder v) {
	    v.visitCFG(array, listChild(index, right()), ENTRY);
	    v.visitCFGList(index, right(), ENTRY);
	    v.visitCFG(right(), this, EXIT);
	}

	public List<Type> throwTypes(TypeSystem ts) {
		List<Type> l = new ArrayList<Type>(super.throwTypes(ts));
		l.add(ts.NullPointerException());
		l.add(ts.OutOfBoundsException());
		return l;
	}

	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(array.toString());
	    sb.append("(");
	    String sep = "";
	    for (Expr e : index) {
	        sb.append(sep);
	        sep = ", ";
	        sb.append(e);
	    }
	    sb.append(") ");
	    sb.append(op);
	    sb.append(" ");
	    sb.append(right.toString());
	    return sb.toString();
	}

	/** Write the expression to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	    Type at = array.type();

	    printSubExpr(array, w, tr);
	    w.write ("(");
	    w.begin(0);

	    for(Iterator<Expr> i = index.iterator(); i.hasNext();) {
	        Expr e = i.next();
	        print(e, w, tr);
	        if (i.hasNext()) {
	            w.write(",");
	            w.allowBreak(0, " ");
	        }
	    }

	    w.write (")");
	    w.write(" ");
	    w.write(op.toString());
	    w.write(" ");

	    print(right, w, tr);

	    w.end();
	}

	public String leftToString() {
	    String arg = index.toString();
	    return array.toString() + "(" + arg.substring(1, arg.length()-1) + ")";
	}

}
