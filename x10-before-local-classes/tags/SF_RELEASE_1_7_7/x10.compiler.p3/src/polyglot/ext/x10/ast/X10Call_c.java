/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Call_c;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.ast.Variable;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.ParametrizedType;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10MemberDef;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.types.XTypeTranslator;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.Context;
import polyglot.types.ErrorRef_c;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.ErrorInfo;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.constraint.XLocal;
import x10.constraint.XRoot;

/**
 * A method call wrapper to rewrite getLocation() calls on primitives
 * and array operator calls. And perform other dep type processing on some selected method calls.
 * @author Igor
 */
public class X10Call_c extends Call_c implements X10Call, X10ProcedureCall {
	public X10Call_c(Position pos, Receiver target, Id name,
			List<TypeNode> typeArguments, List<Expr> arguments) {
		super(pos, target, name, arguments);
		this.typeArguments = new ArrayList<TypeNode>(typeArguments);
	}

	List<TypeNode> typeArguments;
	public List<TypeNode> typeArguments() { return typeArguments; }
	public X10Call typeArguments(List<TypeNode> args) {
		X10Call_c n = (X10Call_c) copy();
		n.typeArguments = new ArrayList<TypeNode>(args);
		return n;
	}

	@Override
	public Node visitChildren(NodeVisitor v) {
		Receiver target = (Receiver) visitChild(this.target, v);
		Id name = (Id) visitChild(this.name, v);
		List<TypeNode> typeArguments = visitList(this.typeArguments, v);
		List<Expr> arguments = visitList(this.arguments, v);
		X10Call_c n = (X10Call_c) typeArguments(typeArguments);
		return n.reconstruct(target, name, arguments);
	}

	@Override
	public Node disambiguate(ContextVisitor tc) throws SemanticException {
		return this;
	}
	

        /**
         * Looks up a method with given name and argument types.
         */
        protected Pair<MethodInstance,List<Expr>> superFindMethod(ContextVisitor tc, X10Context xc, TypeSystem_c.MethodMatcher matcher, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
            // Check for any method with the appropriate name.
            // If found, stop the search since it shadows any enclosing
            // classes method of the same name.
            TypeSystem ts = tc.typeSystem();
            ClassType currentClass = xc.currentClass();
            if (currentClass != null &&
                ts.hasMethodNamed(currentClass, matcher.name())) {
                
                // Override to change the type from C to C{self==this}.
                Type t = currentClass;
                X10TypeSystem xts = (X10TypeSystem) ts;
                XRoot thisVar = null;
                if (XTypeTranslator.THIS_VAR) {
                    CodeDef cd = xc.currentCode();
                    if (cd instanceof X10MemberDef) {
                        thisVar = ((X10MemberDef) cd).thisVar();
                    }
                }
                else {
//                  thisVar = xts.xtypeTranslator().transThis(currentClass);
                    thisVar = xts.xtypeTranslator().transThisWithoutTypeConstraint();
                }
                
                if (thisVar != null)
                    t = X10TypeMixin.setSelfVar(t, thisVar);
                
                // Found a class that has a method of the right name.
                // Now need to check if the method is of the correct type.
                
                X10MethodInstance mi = null;

                // First try to find the method without implicit conversions.
                try {
                    mi = xts.findMethod(t, matcher.container(t));
                    return new Pair<MethodInstance, List<Expr>>(mi, this.arguments);
                }
                catch (SemanticException e) {
                    // Now, try to find the method with implicit conversions, making them explicit.
                    try {
                        Pair<MethodInstance,List<Expr>> p = tryImplicitConversions(this, tc, t, typeArgs, argTypes);
                        return p;
                    }
                    catch (SemanticException e2) {
                        throw e;
                    }
                }
            }

            if (xc.pop() != null) {
                return superFindMethod(tc, (X10Context) xc.pop(), matcher, typeArgs, argTypes);
            }

            throw new SemanticException("Method " + matcher.signature() + " not found.");
        }
        
    /**
     * Looks up a method with name "name" and arguments compatible with
     * "argTypes".
     */
        protected Pair<MethodInstance,List<Expr>> findMethod(ContextVisitor tc, TypeSystem_c.MethodMatcher matcher, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
            X10Context xc = (X10Context) tc.context();
            Pair<MethodInstance,List<Expr>> result = xc.currentDepType() == null ? superFindMethod(tc, xc, matcher, typeArgs, argTypes) : superFindMethod(tc, (X10Context) xc.pop(), matcher, typeArgs, argTypes);
            return result;
    }

	protected Node typeCheckNullTarget(ContextVisitor tc, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		NodeFactory nf = tc.nodeFactory();
		X10Context c = (X10Context) tc.context();

		// the target is null, and thus implicit
		// let's find the target, using the context, and
		// set the target appropriately, and then type check
		// the result
		Pair<MethodInstance, List<Expr>> p = findMethod(tc, ts.MethodMatcher(null, name.id(), typeArgs, argTypes, c), typeArgs, argTypes);
		X10MethodInstance mi = (X10MethodInstance) p.fst();
		List<Expr> args = p.snd();

		
                X10TypeSystem xts = (X10TypeSystem) ts;

		Receiver r;
		if (mi.flags().isStatic()) {
			Type container = findContainer(ts, mi);       
			XRoot this_ = getThis(container);
			if (this_ != null)
			    container = X10TypeMixin.setSelfVar(container, this_);
			r = nf.CanonicalTypeNode(position().startOf(), container).typeRef(Types.ref(container));
		} else {
			// The method is non-static, so we must prepend with "this", but we
			// need to determine if the "this" should be qualified.  Get the
			// enclosing class which brought the method into scope.  This is
			// different from mi.container().  mi.container() returns a super type
			// of the class we want.
			Type scope = c.findMethodScope(name.id());
			XRoot this_ = getThis(scope);
			if (this_ != null)
			    scope = X10TypeMixin.setSelfVar(scope, this_);

			if (! ts.typeEquals(scope, c.currentClass(), c)) {
				r = (Special) nf.This(position().startOf(),
						nf.CanonicalTypeNode(position().startOf(), scope)).del().typeCheck(tc);
			}
			else {
				r = (Special) nf.This(position().startOf()).del().typeCheck(tc);
			}
		}

		Call_c call = (Call_c) this.targetImplicit(true).target(r).arguments(args);   
		Type rt = X10Field_c.rightType(mi.rightType(), mi.x10Def(), r, c);
		call = (Call_c)call.methodInstance(mi).type(rt);
		return call;
	}
	
	XRoot getThis(Type t) {
	    t = X10TypeMixin.baseType(t);
	    if (t instanceof X10ClassType) {
	        return ((X10ClassType) t).x10Def().thisVar();
	    }
	    return null;
	}

	/**
	 * Rewrite getLocation() to Here for value types and operator calls for
	 * array types, otherwise leave alone.
	 */
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
		X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		X10Context c = (X10Context) tc.context();

		Expr cc = null;
		
		{
		    // Check if target.name is a field or local of function type; if so, convert to a closure call.
			X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
			X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

			Expr f;
			if (target() != null)
				f = nf.Field(new Position(target().position(), name().position()), target(), name());
			else
				f = nf.AmbExpr(name().position(), name());

			Expr e = null;

			try {
				Node n = f;
				n = n.del().disambiguate(tc);
				n = n.del().typeCheck(tc);
				n = n.del().checkConstants(tc);
				if (n instanceof Expr && n instanceof Variable) {
					e = (Expr) n;
//					if (! ts.isFunction(e.type())) {
//						e = null;
//					}
				}
			}
			catch (SemanticException ex) {
			}

			if (e != null) {
				ClosureCall ccx = nf.ClosureCall(position(), e, typeArguments(), arguments());
				X10MethodInstance ci = (X10MethodInstance) ts.createMethodInstance(position(), new ErrorRef_c<MethodDef>(ts, position(), "Cannot get MethodDef before type-checking closure call."));
				ccx = ccx.closureInstance(ci);
				Node n = ccx;
				try {
				    n = n.del().disambiguate(tc);
				    n = n.del().typeCheck(tc);
				    cc = (Expr) n;
				}
				catch (SemanticException ex) {
				    // Check for this case:
				    // val x = x();
				    if (e instanceof Local && e.type() instanceof UnknownType) {
				        throw new SemanticException("Possible closure call on uninitialized variable " + ((Local) e).name() + ".", position());
				    }
				    else {
				        // fall through to method call case.
				    }
				}
			}
		}

		/////////////////////////////////////////////////////////////////////
		// Inline the super call here and handle type arguments.
		/////////////////////////////////////////////////////////////////////

		List<Type> typeArgs = new ArrayList<Type>(this.typeArguments.size());

		for (TypeNode tn : this.typeArguments) {
		    typeArgs.add(tn.type());
		}

		List<Type> argTypes = new ArrayList<Type>(this.arguments.size());

		for (Expr e : this.arguments) {
		    Type et = e.type();
		    argTypes.add(et);
		}

		if (this.target == null) {
		    try {
		        X10Call_c n = (X10Call_c) this.typeCheckNullTarget(tc, typeArgs, argTypes);
		        if (cc != null)
		            throw new SemanticException("Ambiguous call; both " + n.methodInstance() + " and closure match.", position());
		        return n;
		    }
		    catch (SemanticException e) {
		        if (cc != null)
		            return cc;
		        throw e;
		    }
		}

    		if (target instanceof TypeNode) {
    		    Type t = ((TypeNode) target).type();
    		    t = X10TypeMixin.baseType(t);
    		    if (t instanceof ParameterType) {
    		        throw new SemanticException("Cannot invoke a static method of a type parameter.", position());
    		    }
    		}

		X10Call_c n = this;

		Type targetType = this.target.type();
		Name name = this.name.id();
		ClassDef currentClassDef = c.currentClassDef();

		X10MethodInstance mi = null;
		List<Expr> args = null;

		// First try to find the method without implicit conversions.
		try {
		    mi = xts.findMethod(targetType, xts.MethodMatcher(targetType, name, typeArgs, argTypes, c));
		    args = n.arguments;
		}
		catch (SemanticException e) {
		    try {
		        if (name == Name.make("equals") && argTypes.size() == 1 && typeArgs.size() == 0 && xts.isParameterType(targetType) && xts.isParameterType(argTypes.get(0))) {
		            // Check that both equals(Ref) and equals(Value) are present
		            mi = (X10MethodInstance) xts.findMethod(targetType, xts.MethodMatcher(targetType, name, typeArgs, Collections.singletonList(xts.Ref()), c));
                            mi = null;
		            mi = (X10MethodInstance) xts.findMethod(targetType, xts.MethodMatcher(targetType, name, typeArgs, Collections.singletonList(xts.Value()), c));
		            mi = (X10MethodInstance) mi.formalTypes(Collections.singletonList(X10TypeMixin.baseType(targetType)));
		            LocalInstance d = mi.formalNames().get(0);
		            mi = (X10MethodInstance) mi.formalNames(Collections.singletonList(d.type(X10TypeMixin.baseType(targetType))));
		            args = n.arguments;
		        }
		    }
		    catch (SemanticException e3) {
		    }
		    
		    if (mi == null) {
		        // Now, try to find the method with implicit conversions, making them explicit.
		        try {
		            Pair<MethodInstance,List<Expr>> p = tryImplicitConversions(n, tc, targetType, typeArgs, argTypes);
		            mi = (X10MethodInstance) p.fst();
		            args = p.snd();
		        }
		        catch (SemanticException e2) {
		            if (cc != null)
		                return cc;
		            throw e;
		        }
		    }
		    
		    assert mi != null && args != null;
		}
		
		if (cc != null)
		    throw new SemanticException("Ambiguous call; both " + mi + " and closure match.", position());

		/* This call is in a static context if and only if
		 * the target (possibly implicit) is a type node.
		 */
		boolean staticContext = (n.target instanceof TypeNode);

		if (staticContext && !mi.flags().isStatic()) {
		    throw new SemanticException("Cannot call non-static method " + name
		                                + " of " + n.target.type() + " in static "
		                                + "context.", this.position());
		}

		// If the target is super, but the method is abstract, then complain.
		if (n.target instanceof Special && 
		        ((Special) n.target).kind() == Special.SUPER &&
		        mi.flags().isAbstract()) {
		    throw new SemanticException("Cannot call an abstract method " +
		                                "of the super class", this.position());            
		}

		// Copy the method instance so we can modify it.
		Type rt = X10Field_c.rightType(mi.rightType(), mi.x10Def(), n.target, c);
		X10Call_c result = (X10Call_c) n.methodInstance(mi).type(rt);
		result = (X10Call_c) result.arguments(args);
		
		/////////////////////////////////////////////////////////////////////
		// End inlined super call.
		/////////////////////////////////////////////////////////////////////

		// If we found a method, the call must type check, so no need to check
		// the arguments here.
		result.checkConsistency(c);
		//	        	result = result.adjustMI(tc);
		//	        	result.checkWhereClause(tc);
		result.checkAnnotations(tc);

		return result;
	}

	static Pair<MethodInstance,List<Expr>> tryImplicitConversions(final X10Call_c n, ContextVisitor tc, Type targetType, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
	    final X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	    final Context context = tc.context();
	    ClassDef currentClassDef = context.currentClassDef();

	    List<MethodInstance> methods = ts.findAcceptableMethods(targetType, new X10TypeSystem_c.DumbMethodMatcher(targetType, n.name().id(), typeArgs, argTypes, context));

	    Pair<MethodInstance,List<Expr>> p = X10New_c.<MethodDef,MethodInstance>tryImplicitConversions(n, tc, targetType, methods, new X10New_c.MatcherMaker<MethodInstance>() {
	        public Matcher<MethodInstance> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes) {
	            return ts.MethodMatcher(ct, n.name().id(), typeArgs, argTypes, context);
	        }
	    });
	    
	    return p;
	}

	private void checkAnnotations(ContextVisitor tc) throws SemanticException {
		X10Context c = (X10Context) tc.context();
		X10MethodInstance mi = (X10MethodInstance) methodInstance();
		try {
		    if (mi !=null) {
		        X10Flags flags = X10Flags.toX10Flags(mi.flags());
		        if (c.inNonBlockingCode() 
		                && ! (mi.isSafe() || flags.isNonBlocking() || flags.isExtern()))
		            throw new SemanticException(mi + ": Only nonblocking methods can be called from nonblocking code.", 
		                                        position());
		        if (c.inSequentialCode() 
		                && ! (mi.isSafe() || flags.isSequential() || flags.isExtern()))
		            throw new SemanticException(mi + ": Only sequential methods can be called from sequential code.", 
		                                        position());
		        if (c.inLocalCode() 
		                && ! (mi.isSafe() || flags.isLocal() || flags.isExtern()))
		            throw new SemanticException(mi + ": Only local methods can be called from local code.", 
		                                        position());
		    }
		}
		catch (SemanticException e) {
		    tc.errorQueue().enqueue(ErrorInfo.WARNING, "WARNING (should be error, but method annotations in XRX are wrong): " + e.getMessage(), position());
		}
	}

	private Node superTypeCheck(ContextVisitor tc) throws SemanticException {
		return super.typeCheck(tc);
	}

	@Override
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	    // RMF 10/24/09 - omit the ".apply" for array accesses
	    X10Type targetType= (X10Type) this.target.type();
	    X10TypeSystem ts= (X10TypeSystem) targetType.typeSystem();

        if (this.name.toString().equals("apply") && ts.isX10Array(targetType)) {
	        // omit the ".apply()"
	        w.begin(0);
	        if (!targetImplicit) {
	            if (target instanceof Expr) {
	                printSubExpr((Expr) target, w, tr);
	            }
	            else if (target != null) {
	                print(target, w, tr);
	            }
//	            w.write(".");
	            w.allowBreak(2, 3, "", 0);
	        }

//          w.write(name + "(");
	        w.write("(");
	        if (arguments.size() > 0) {
	            w.allowBreak(2, 2, "", 0); // miser mode
	            w.begin(0);

	            for (Iterator<Expr> i = arguments.iterator(); i.hasNext(); ) {
	                Expr e = i.next();
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
	    } else
	        super.prettyPrint(w, tr);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(targetImplicit ? "" : target.toString() + ".");
		sb.append(name);
		if (typeArguments != null && typeArguments.size() > 0) {
			sb.append("[");
			sb.append(CollectionUtil.listToString(typeArguments));
			sb.append("]");
		}
		sb.append("(");
		sb.append(CollectionUtil.listToString(arguments));
		sb.append(")");
		return sb.toString();
	}
}

