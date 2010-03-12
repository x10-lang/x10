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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.Context;
import polyglot.types.ErrorRef_c;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.Transformation;
import polyglot.util.TransformingList;
import polyglot.util.TypedList;
import polyglot.visit.AscriptionVisitor;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import x10.types.ClosureDef;
import x10.types.ClosureInstance;
import x10.types.FunctionType;
import x10.types.X10Context;
import x10.types.X10MethodInstance;
import x10.types.X10MethodInstance_c;
import x10.types.X10TypeSystem;
import x10.types.checker.Converter;
import x10.types.matcher.DumbMethodMatcher;

public class ClosureCall_c extends Expr_c implements ClosureCall {
    protected Expr target;
 //   protected List<TypeNode> typeArgs;
    protected List<Expr> arguments;

    protected X10MethodInstance ci;

    public ClosureCall_c(Position pos, Expr target, /*List<TypeNode> typeArgs,*/ List<Expr> arguments) {
	super(pos);
	assert target != null;
	//assert typeArgs != null;
        assert arguments != null;
	this.target= target;
//	this.typeArgs = TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
	this.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
    }
    
    @Override
    public boolean isConstant() {
        Expr t = target;
        if (t.isConstant()) {
            X10TypeSystem ts = (X10TypeSystem) t.type().typeSystem();
            if (ts.isValRail(t.type()) && arguments.size() == 1) {
                Expr e = arguments.get(0);
                return e.isConstant();
            }
        }
        return super.isConstant();
    }

    @Override
    public Object constantValue() {
        Expr t = target;
        if (t.isConstant()) {
            X10TypeSystem ts = (X10TypeSystem) t.type().typeSystem();
            if (ts.isValRail(t.type()) && arguments.size() == 1) {
                Expr e = arguments.get(0);
                Object a = t.constantValue();
                Object i = e.constantValue();
                if (a instanceof Object[] && i instanceof Integer) {
                    return ((Object[]) a)[(Integer) i];
                }
            }
        }
        return super.constantValue();
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
    public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
	List<Term> args = new ArrayList<Term>();
//	args.addAll(typeArgs);
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
    public X10MethodInstance closureInstance() {
	return this.ci;
    }

    /** Set the method instance of the call. */
    public ClosureCall closureInstance(X10MethodInstance ci) {
	if (ci == this.ci)
	    return this;
	ClosureCall_c n = (ClosureCall_c) copy();
	n.ci= ci;
	return n;
    }

    public ProcedureInstance<?> procedureInstance() {
	return this.ci;
    }

    /** Get the actual arguments of the call. */
    public List<Expr> arguments() {
	return this.arguments;
    }

    /** Set the actual arguments of the call. */
    public ProcedureCall arguments(List<Expr> arguments) {
        assert arguments != null;
	ClosureCall_c n= (ClosureCall_c) copy();
	n.arguments= TypedList.copyAndCheck(arguments, Expr.class, true);
	return n;
    }
    
    /** Get the actual arguments of the call. */
  /*  public List<TypeNode> typeArgs() {
	    return this.typeArgs;
    }
   */ 
    /** Set the actual arguments of the call. */
  /*  public ClosureCall typeArgs(List<TypeNode> typeArgs) {
        assert typeArgs != null;
	    ClosureCall_c n= (ClosureCall_c) copy();
	    n.typeArgs= TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
	    return n;
    }*/
    
    public List<TypeNode> typeArguments() {
        return Collections.EMPTY_LIST; // typeArgs();
    }

    /** Reconstruct the call. */
    protected ClosureCall_c reconstruct(Expr target, /*List<TypeNode> typeArgs,*/ List<Expr> arguments) {
	if (target != this.target 
		//	|| !CollectionUtil.allEqual(typeArgs, this.typeArgs) 
			|| !CollectionUtil.allEqual(arguments, this.arguments)) {
	    ClosureCall_c n= (ClosureCall_c) copy();
	    n.target= target;
	//    n.typeArgs= TypedList.copyAndCheck(typeArgs, TypeNode.class, true);
	    n.arguments= TypedList.copyAndCheck(arguments, Expr.class, true);
	    return n;
	}
	return this;
    }

    /** Visit the children of the call. */
    public Node visitChildren(NodeVisitor v) {
	Expr target= (Expr) visitChild(this.target, v);
//	List typeArgs= visitList(this.typeArgs, v);
	List arguments= visitList(this.arguments, v);
	return reconstruct(target, /*typeArgs,*/ arguments);
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
	ClosureCall_c n= (ClosureCall_c) super.buildTypes(tb);

	X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();

	X10MethodInstance mi = (X10MethodInstance) ts.createMethodInstance(position(), new ErrorRef_c<MethodDef>(ts, position(), "Cannot get MethodDef before type-checking closure call."));
	return n.closureInstance(mi);
    }
    
    static Pair<MethodInstance,List<Expr>> tryImplicitConversions(final ClosureCall_c n, ContextVisitor tc, Type targetType, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
        final X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        final Context context = tc.context();
        ClassDef currentClassDef = context.currentClassDef();

        List<MethodInstance> methods = ts.findAcceptableMethods(targetType, new DumbMethodMatcher(targetType, Name.make("apply"), typeArgs, argTypes, context));

        Pair<MethodInstance,List<Expr>> p = Converter.<MethodDef,MethodInstance>tryImplicitConversions(n, tc, targetType, methods, new X10New_c.MatcherMaker<MethodInstance>() {
            public Matcher<MethodInstance> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes) {
                return ts.MethodMatcher(ct, Name.make("apply"), typeArgs, argTypes, context);
            }
        });
        
        return p;
    }

    @Override
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
	Type targetType = target.type();

	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	/*
	if (! ts.isFunction(targetType)) {
	    throw new SemanticException("The target of a closure call must be a function type, not " + targetType + ".", target.position());
	}
	*/

	List<Type> typeArgs = Collections.EMPTY_LIST;
	//new ArrayList<Type>(this.typeArgs.size());

/*	for (TypeNode tn : this.typeArgs) {
		typeArgs.add(tn.type());
	}
	*/
	List<Type> actualTypes = new ArrayList<Type>(this.arguments.size());
	for (Expr ei : arguments) {
	    actualTypes.add(ei.type());
	}
	
	X10TypeSystem xts = ts;

	X10MethodInstance mi = null;
	List<Expr> args = null;

	// First try to find the method without implicit conversions.
	try {
	    Context context = tc.context();
	    mi = ts.findMethod(targetType, ts.MethodMatcher(targetType, Name.make("apply"), typeArgs, actualTypes, context));
	    args = this.arguments;
	}
	catch (SemanticException e) {
	    // Now, try to find the method with implicit conversions, making them explicit.
	    try {
	        Pair<MethodInstance,List<Expr>> p = tryImplicitConversions(this, tc, targetType, typeArgs, actualTypes);
	        mi = (X10MethodInstance) p.fst();
	        args = p.snd();
	    }
	    catch (SemanticException e2) {
	        throw e;
	    }
	}

	// Find the most-specific closure type.
	
	if (mi.container() instanceof FunctionType) {
	    X10MethodInstance ci = ((FunctionType) mi.container()).applyMethod();
	    ClosureCall_c n = this;
	    n = (ClosureCall_c) n.arguments(args);
	    ci = x10.types.matcher.Matcher.computeReturnType(mi, args, (X10Context) tc.context());
	    return n.closureInstance(ci).type(ci.returnType());
	}
	else {
	    // TODO: add ts.Function and uncomment this.
	    // Check that the target actually is of a function type of the appropriate type and doesn't just coincidentally implement an
	    // apply method.
//	    ClosureType ct = ts.Function(mi.typeParameters(), mi.formalTypes(), mi.returnType());
//	    if (! targetType.isSubtype(ct))
//		throw new SemanticException("Invalid closure call; target does not implement " + ct + ".", position());
	    X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
	    X10Call_c n = (X10Call_c) nf.X10Call(position(), target(), 
	    		nf.Id(position(), mi.name().toString()), Collections.EMPTY_LIST, args);
	    mi = x10.types.matcher.Matcher.computeReturnType(mi, args, (X10Context) tc.context());
	    n = (X10Call_c) n.methodInstance(mi);
	    n = (X10Call_c) n.type(mi.returnType());
	    return n;
	}
	
//	List<ClosureType> functionTypes = x10ts.getFunctionSupertypes(newTargetType);
//	
//	if (functionTypes.isEmpty()) {
//	    throw new SemanticException("The target of a closure call must be a function type, not " + newTargetType + ".", target.position());
//	}
//	
//	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
//	
//	for (ClosureType ct: functionTypes) {
//	    if (ts.typeEquals(ct.returnType(), mi.returnType()) && CollectionUtil.allElementwise(ct.argumentTypes(), mi.formalTypes(), new TypeSystem_c.TypeEquals())) {
//		try {
//		    ClosureInstance ci = ct.closureInstance();
//		    ClosureInstance ci2 = X10MethodInstance_c.instantiate(ci, targetType, typeArgs, actualTypes);
//		    return this.closureInstance(ci2).type(ci2.returnType());
//		}
//		catch (SemanticException e) {
//		    if (functionTypes.size() == 1)
//			throw e;
//		}
//	    }
//	}
//	
//	throw new SemanticException("Invalid closure call.", position());
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av)
    {
        if (child == target) {
            return ci.container();
        }

        Iterator i = this.arguments.iterator();
        Iterator j = ci.formalTypes().iterator();

        while (i.hasNext() && j.hasNext()) {
            Expr e = (Expr) i.next();
            Type t = (Type) j.next();

            if (e == child) {
                return t;
            }
        }

        return child.type();
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
