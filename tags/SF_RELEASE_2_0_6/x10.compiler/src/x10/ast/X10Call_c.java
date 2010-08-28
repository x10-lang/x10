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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Ambiguous;
import polyglot.ast.Call_c;
import polyglot.ast.Disamb;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Prefix;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.Matcher;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem_c;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context;
import x10.types.X10FieldInstance;
import x10.types.X10Flags;
import x10.types.X10LocalInstance;
import x10.types.X10MemberDef;
import x10.types.X10MethodInstance;
import x10.types.X10ParsedClassType;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10TypeSystem_c;
import x10.types.XTypeTranslator;
import x10.types.checker.Checker;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.matcher.DumbMethodMatcher;
import x10.visit.X10TypeChecker;

/**
 * Representation of an X10 method call.
 * @author Igor
 * @author vj
 */
public class X10Call_c extends Call_c implements X10Call, X10ProcedureCall {
	// TODO: implement Settable and decompose x.apply(i)
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
	public X10Call arguments(List<Expr> args) {
		X10Call_c n = (X10Call_c) copy();
		n.arguments = new ArrayList<Expr>(args);
		return n;
	}
	public Call_c reconstruct(Receiver target, Id name, List<Expr> arguments) {
		return super.reconstruct(target, name, arguments);
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
	public static Pair<MethodInstance,List<Expr>> findMethod(ContextVisitor tc, X10ProcedureCall n,
	        Type targetType, Name name, List<Type> typeArgs, List<Type> actualTypes) {
	    X10MethodInstance mi;
	    X10TypeSystem_c xts = (X10TypeSystem_c) tc.typeSystem();
	    X10Context context = (X10Context) tc.context();
	    boolean haveUnknown = xts.hasUnknown(targetType);
	    for (Type t : actualTypes) {
	        if (xts.hasUnknown(t)) haveUnknown = true;
	    }
	    SemanticException error = null;
	    try {
	        return findMethod(tc, context, n, targetType, name, typeArgs, actualTypes, context.inStaticContext());
	    } catch (SemanticException e) {
	        error = e;
	    }
	    // If not returned yet, fake the method instance.
	    Collection<X10MethodInstance> mis = null;
	    try {
	        mis = findMethods(tc, targetType, name, typeArgs, actualTypes);
	    } catch (SemanticException e) {
	        if (error == null) error = e;
	    }
	    // See if all matches have the same return type, and save that to avoid losing information.
	    Type rt = null;
	    if (mis != null) {
	        for (X10MethodInstance xmi : mis) {
	            if (rt == null) {
	                rt = xmi.returnType();
	            } else if (!xts.typeEquals(rt, xmi.returnType(), context)) {
	                if (xts.typeBaseEquals(rt, xmi.returnType(), context)) {
	                    rt = X10TypeMixin.baseType(rt);
	                } else {
	                    rt = null;
	                    break;
	                }
	            }
	        }
	    }
	    if (targetType == null)
	        targetType = context.currentClass();
	    if (haveUnknown)
	        error = new SemanticException(); // null message
	    mi = xts.createFakeMethod(targetType.toClass(), Flags.PUBLIC, name, typeArgs, actualTypes, error);
	    if (rt != null) mi = mi.returnType(rt);
	    return new Pair<MethodInstance, List<Expr>>(mi, n.arguments());
	}

	private static Pair<MethodInstance,List<Expr>> findMethod(ContextVisitor tc, X10Context xc,
	        X10ProcedureCall n, Type targetType, Name name, List<Type> typeArgs,
			List<Type> argTypes, boolean requireStatic) throws SemanticException {

	    X10MethodInstance mi = null;
	    X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        if (targetType != null) {
	        mi = xts.findMethod(targetType, xts.MethodMatcher(targetType, name, typeArgs, argTypes, xc));
	        return new Pair<MethodInstance, List<Expr>>(mi, n.arguments());
	    }
	    if (xc.currentDepType() != null)
	        xc = (X10Context) xc.pop();
	    ClassType currentClass = xc.currentClass();
	    if (currentClass != null && xts.hasMethodNamed(currentClass, name)) {
	        // Override to change the type from C to C{self==this}.
	        Type t = currentClass;
	        XVar thisVar = null;
	        if (XTypeTranslator.THIS_VAR) {
	            CodeDef cd = xc.currentCode();
	            if (cd instanceof X10MemberDef) {
	                thisVar = ((X10MemberDef) cd).thisVar();
	            }
	        }
	        else {
	            //thisVar = xts.xtypeTranslator().transThis(currentClass);
	            thisVar = xts.xtypeTranslator().transThisWithoutTypeConstraint();
	        }

	        if (thisVar != null)
	            t = X10TypeMixin.setSelfVar(t, thisVar);

	        // Found a class that has a method of the right name.
	        // Now need to check if the method is of the correct type.

	        // First try to find the method without implicit conversions.
	        try {
	            mi = xts.findMethod(t, xts.MethodMatcher(t, name, typeArgs, argTypes, xc));
	            if (!requireStatic || mi.flags().isStatic())
	                return new Pair<MethodInstance, List<Expr>>(mi, n.arguments());
	        }
	        catch (SemanticException e) {
	            // Now, try to find the method with implicit conversions, making them explicit.
	            try {
	                Pair<MethodInstance,List<Expr>> p = tryImplicitConversions(n, tc, t, name, typeArgs, argTypes);
	                if (!requireStatic || p.fst().flags().isStatic())
	                    return p;
	            }
	            catch (SemanticException e2) {
	                throw e;
	            }
	        }
	    }

	    while (xc.pop() != null && xc.pop().currentClass() == currentClass)
	        xc = (X10Context) xc.pop();
	    if (xc.pop() != null) {
	        return findMethod(tc, (X10Context) xc.pop(), n, targetType, name, typeArgs, argTypes, currentClass.flags().isStatic());
	    }

	    TypeSystem_c.MethodMatcher matcher = xts.MethodMatcher(targetType, name, typeArgs, argTypes, xc);
	    throw new Errors.MethodOrStaticConstructorNotFound(matcher, n.position());
	}

	private static Collection<X10MethodInstance> findMethods(ContextVisitor tc, Type targetType, Name name, List<Type> typeArgs,
	        List<Type> actualTypes) throws SemanticException {
	    X10TypeSystem_c xts = (X10TypeSystem_c) tc.typeSystem();
	    X10Context context = (X10Context) tc.context();
	    if (targetType == null) {
	        // TODO
	        return Collections.emptyList();
	    }
	    return xts.findMethods(targetType, xts.MethodMatcher(targetType, name, typeArgs, actualTypes, context));
	}

    private Type resolveType(ContextVisitor tc, Position pos, Receiver r, Name name) {
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

        TypeNode otn;
        List<TypeNode> typeArgs = typeArguments();
        Id nameNode = nf.Id(pos, name);
        LazyRef<? extends Type> tRef = Types.lazyRef(ts.unknownType(pos));
        if (typeArgs.size() > 0) {
            otn = nf.AmbMacroTypeNode(pos, r, nameNode, typeArgs, Collections.<Expr>emptyList()).typeRef(tRef);
        } else {
            otn = nf.AmbTypeNode(pos, r, nameNode).typeRef(tRef);
        }

        TypeNode tn = disambiguateTypeNode(tc, (Ambiguous) otn, pos, r, nameNode);
        if (tn == null)
            return null;

        Type t = tn.type();
        // FIXME: why should these be different?
        if (typeArgs.size() > 0) {
            return ambMacroTypeNodeType(tc, t, typeArgs);
        } else {
            return ambTypeNodeType(tc, t);
        }
    }
    
    private static Type ambTypeNodeType(ContextVisitor tc, Type t) {
        
        X10Context c = (X10Context) tc.context();
        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
        
        t = ts.expandMacros(t);
        
        if (t instanceof ParameterType) {
            ParameterType pt = (ParameterType) t;
            Def def = Types.get(pt.def());
            boolean inConstructor = false;
            if (c.currentCode() instanceof ConstructorDef) {
                ConstructorDef td = (ConstructorDef) c.currentCode();
                Type container = Types.get(td.container());
                if (container instanceof X10ClassType) {
                    X10ClassType ct = (X10ClassType) container;
                    if (ct.def() == def) {
                        inConstructor = true;
                    }
                }
            }
            if (c.inStaticContext() && def instanceof ClassDef && ! inConstructor) {
                return null;
            }
        }
        
        try {
            X10CanonicalTypeNode_c.checkType(tc.context(), t, Position.COMPILER_GENERATED);
        } catch (SemanticException e) {
            return null;
        }
        
        if (t.isClass()) {
            ClassType ct = t.toClass();
            if (ct.isTopLevel() || ct.isMember()) {
                if (!ts.classAccessible(ct.def(), c)) {
                    return null;
                }
            }
        }
        
        return t;
    }
    
    private static Type ambMacroTypeNodeType(ContextVisitor tc, Type t, List<TypeNode> typeArgs) {
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        
        if (xts.isUnknown(t)) {
            return null;
        }
        
        if (!typeArgs.isEmpty() && t instanceof X10ParsedClassType) {
            X10ParsedClassType ct = (X10ParsedClassType) t;
            int numParams = ct.x10Def().typeParameters().size();
            if (numParams != typeArgs.size())
                return null;
            List<Type> typeArgsTypes = new ArrayList<Type>(numParams);
            for (TypeNode tni : typeArgs) {
                typeArgsTypes.add(tni.type());
            }
            return ct.typeArguments(typeArgsTypes);
        }
        
        return t;
    }
    
    private static TypeNode disambiguateTypeNode(ContextVisitor tc,
            Ambiguous otn, Position pos, Prefix prefix, Id name) {
        TypeNode tn = null;
        try {
            // Look for a simply-named type.
            Disamb disamb = tc.nodeFactory().disamb();
            Node n = disamb.disambiguate(otn, tc, pos, prefix, name);
            if (n instanceof TypeNode)
                tn = (TypeNode) n;
        } catch (SemanticException e) { }
        return tn;
    }

    /**
     * First try for a struct constructor, and then look for a static method.
     * @param tc
     * @param typeArgs
     * @param argTypes
     * @param args
     * @return
     * @throws SemanticException
     */
    protected X10New findStructConstructor(ContextVisitor tc, Receiver r, List<Type> typeArgs, List<Type> argTypes, List<Expr> args) {
        X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
        Type t = resolveType(tc, position(), r, name().id());
        if (t == null)
            return null;
        if (X10TypeMixin.isX10Struct(t)) {
            X10New_c neu = (X10New_c) nf.X10New(position(), nf.CanonicalTypeNode(position(), t), Collections.EMPTY_LIST, args);
            neu = (X10New_c) neu.newOmitted(true);
            Pair<ConstructorInstance, List<Expr>> p = X10New_c.findConstructor(tc, neu, t, argTypes);
            X10ConstructorInstance ci = (X10ConstructorInstance) p.fst();
            if (ci.error() != null)
                return null;

            try {
                neu = (X10New_c) neu.typeCheck1(tc);
                return neu;
            } catch (SemanticException cause) {
                throw new InternalCompilerError("Unexpected exception when typechecking "+neu, neu.position(), cause);
            }
        }
        return null;
    }

    private Receiver computeReceiver(ContextVisitor tc, X10MethodInstance mi) {
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        X10Context c = (X10Context) tc.context();
        Position prefixPos = position().startOf().markCompilerGenerated();
        try {
            if (mi.flags().isStatic() || c.inStaticContext()) {
                Type container = findContainer(xts, mi);
                XVar thisVar = getThis(container);
                if (thisVar != null)
                    container = X10TypeMixin.setSelfVar(container, thisVar);
                return nf.CanonicalTypeNode(prefixPos, container).typeRef(Types.ref(container));
            } else {
                // The method is non-static, so we must prepend with "this", but we
                // need to determine if the "this" should be qualified.  Get the
                // enclosing class which brought the method into scope.  This is
                // different from mi.container().  mi.container() returns a super type
                // of the class we want.
                if (mi.error() != null) {
                    // The method wasn't found -- assume current class.
                    return (Special) nf.This(prefixPos).del().typeCheck(tc);
                }
                Type scope = c.findMethodScope(name.id());
                if (!xts.typeEquals(scope, c.currentClass(), c)) {
                    XVar thisVar = getThis(scope);
                    if (thisVar != null)
                        scope = X10TypeMixin.setSelfVar(scope, thisVar);
                    return (Special) nf.This(prefixPos,
                            nf.CanonicalTypeNode(prefixPos, scope)).del().typeCheck(tc);
                }
                else {
                    return (Special) nf.This(prefixPos).del().typeCheck(tc);
                }
            }
        } catch (SemanticException e) {
            throw new InternalCompilerError("Unexpected error while computing receiver", position(), e);
        }
    }

    protected X10Call typeCheckNullTargetForMethod(ContextVisitor tc, List<Type> typeArgs, List<Type> argTypes, X10MethodInstance mi, List<Expr> args) throws SemanticException {
		Receiver r = computeReceiver(tc, mi);
		X10Call_c call = (X10Call_c) this.targetImplicit(true).target(r).arguments(args);
		Type rt = X10Field_c.rightType(mi.rightType(), mi.x10Def(), r, (X10Context) tc.context());
		call = (X10Call_c)call.methodInstance(mi).type(rt);
		call.checkProtoMethod();
		return call;
	}

	void checkProtoMethod() throws SemanticException {
		if (X10TypeMixin.isProto(target().type())
				&& ! X10Flags.toX10Flags(methodInstance().flags()).isProto()
			)
			throw new SemanticException(methodInstance()
					+ " must be declared as a proto method since it is called on a receiver " +
					target() + " with a proto type.");
	}
	XVar getThis(Type t) {
	    t = X10TypeMixin.baseType(t);
	    if (t instanceof X10ClassType) {
	        return ((X10ClassType) t).x10Def().thisVar();
	    }
	    return null;
	}

	public Node typeCheck(ContextVisitor tc) {
	    Node n;
	    try {
	        n = typeCheck1(tc);
	    } catch (SemanticException e) {
	        Errors.issue(tc.job(), e, this);
	        X10TypeSystem_c ts = (X10TypeSystem_c) tc.typeSystem();
	        List<Type> typeArgs = new ArrayList<Type>(this.typeArguments.size());
	        for (TypeNode tn : this.typeArguments) {
	            typeArgs.add(tn.type());
	        }
	        List<Type> argTypes = new ArrayList<Type>(this.arguments.size());
	        for (Expr a : this.arguments) {
	            argTypes.add(a.type());
	        }
	        X10MethodInstance mi = ts.createFakeMethod(name.id(), typeArgs, argTypes, e);
	        Type rt = mi.rightType(); // X10Field_c.rightType(mi.rightType(), mi.x10Def(), n.target, c);
	        n = (X10Call_c) methodInstance(mi).type(rt);
	        try {
	            n = ((X10Call_c)n).typeCheckNullTargetForMethod(tc, typeArgs, argTypes, mi, this.arguments);
	        } catch (SemanticException e2) { }
	    }
	    return n;
	}
	public Node typeCheck1(ContextVisitor tc) throws SemanticException {
		X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		X10Context c = (X10Context) tc.context();

		if (mi != null && ((X10MethodInstance)mi).isValid()) // already typechecked
		    return this;

		Name name = this.name().id();

		Expr cc = null;

		{
			// Check if target.name is a field or local of function type;
			// if so, convert to a closure call.
			X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();
			X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

			Expr e = null;

			if (target() == null) {
			    X10LocalInstance li = X10Local_c.findAppropriateLocal(tc, name);
			    if (li.error() == null) {
			        //e = xnf.Local(name().position(), name()).localInstance(li).type(li.type());
			        try {
			            e = xnf.Local(name().position(), name()).localInstance(li);
			            e = (Expr) e.del().typeCheck(tc);
			            e = (Expr) e.del().checkConstants(tc);
			        } catch (SemanticException cause) {
			            throw new InternalCompilerError("Unexpected exception when typechecking "+e, e.position(), cause);
			        }
			    }
			}
			if (e == null) {
			    boolean isStatic = target() instanceof TypeNode || (target() == null && c.inStaticContext());
			    X10FieldInstance fi = null;
			    if (target() == null) {
			        fi = (X10FieldInstance) c.findVariableSilent(name);
		            if (fi != null && isStatic && !fi.flags().isStatic())
		                fi = null;
			    }
			    if (fi == null) {
			        Type targetType = target() == null ? c.currentClass() : target().type();
			        fi = X10Field_c.findAppropriateField(tc, targetType, name,
			                isStatic,
			                X10TypeMixin.contextKnowsType(target()));
			    }
			    if (fi.error() == null) {
			        try {
			            Receiver target = this.target() == null ?
			                    X10Disamb_c.makeMissingFieldTarget(fi, name().position(), tc) :
			                        this.target();
			            //e = xnf.Field(new Position(target.position(), name().position()), target,
			            //        name()).fieldInstance(fi).targetImplicit(target()==null).type(fi.type());
			            e = xnf.Field(new Position(target.position(), name().position()), target,
			                    name()).fieldInstance(fi).targetImplicit(target()==null);
			            e = (Expr) e.del().typeCheck(tc);
			            e = (Expr) e.del().checkConstants(tc);
			        } catch (SemanticException cause) {
			            throw new InternalCompilerError("Unexpected exception when typechecking "+e, e.position(), cause);
			        }
			    }
			}

			if (e != null) {
			    assert typeArguments().size() == 0;
			    List<Type> typeArgs = Collections.emptyList();
			    List<Type> actualTypes = new ArrayList<Type>(arguments().size());
			    for (Expr ei : arguments()) {
			        actualTypes.add(ei.type());
			    }
			    // First try to find the method without implicit conversions.
			    X10MethodInstance ci = ClosureCall_c.findAppropriateMethod(tc, e.type(), ClosureCall.APPLY, typeArgs, actualTypes);
			    List<Expr> args = this.arguments;
			    if (ci.error() != null) {
			        // Now, try to find the method with implicit conversions, making them explicit.
			        try {
			            Pair<MethodInstance,List<Expr>> p = X10Call_c.tryImplicitConversions(this, tc, e.type(), ClosureCall.APPLY, typeArgs, actualTypes);
			            ci = (X10MethodInstance) p.fst();
			            args = p.snd();
			        }
			        catch (SemanticException se) { }
			    }
			    if (ci.error() != null) {
			        // Check for this case:
			        // val x = x();
			        if (e instanceof Local && xts.isUnknown(e.type())) {
			            throw new SemanticException("Possible closure call on uninitialized variable " + ((Local) e).name() + ".", position());
			        }
			    } else {
			        ClosureCall ccx = nf.ClosureCall(position(), e,  arguments()).closureInstance(ci);
			        Node n = ccx;
			        try {
			            //n = n.del().disambiguate(tc);
			            n = n.del().typeCheck(tc);
			            cc = (Expr) n;
			        }
			        catch (SemanticException cause) {
			            throw new InternalCompilerError("Unexpected exception when typechecking "+ccx, ccx.position(), cause);
			        }
			    }
			}
		}

		// We have a cc and a valid call with no target. The one with //todo: fill in this comment
		if (cc instanceof ClosureCall) {
			ClosureCall call = (ClosureCall) cc;
			if (call.target() instanceof Local) {
				// cc is of the form r() where r is a local variable.
				// This overrides any other possibility for this call, e.g. a static or an instance method call.
				X10TypeMixin.checkMissingParameters(cc);
				Checker.checkOfferType(position(), call.closureInstance(), tc);
				return cc;
			}
		}

		if (target instanceof TypeNode) {
		    Type t = ((TypeNode) target).type();
		    t = X10TypeMixin.baseType(t);
		    if (t instanceof ParameterType) {
		        throw new SemanticException("Cannot invoke a static method of a type parameter.", position());
		    }
		}

		List<Type> typeArgs = new ArrayList<Type>(this.typeArguments.size());
		for (TypeNode tn : this.typeArguments) {
		    typeArgs.add(tn.type());
		}

		List<Type> argTypes = new ArrayList<Type>(this.arguments.size());
		for (Expr e : this.arguments) {
		    Type et = e.type();
		    argTypes.add(et);
		}

		// TODO: Need to try struct call with implicit conversions as well.
		X10New structCall = findStructConstructor(tc, target, typeArgs, argTypes, arguments);
		// We have both a struct constructor and a closure call.  Spec section 8.2.
		if (structCall != null && cc != null) {
		    throw new Errors.AmbiguousCall(structCall.constructorInstance(), cc, position());
		}

		Type targetType = this.target() == null ? null : this.target().type();
		X10MethodInstance mi = null;
		List<Expr> args = null;
		// First try to find the method without implicit conversions.
		Pair<MethodInstance, List<Expr>> p = findMethod(tc, this, targetType, name, typeArgs, argTypes);
		mi = (X10MethodInstance) p.fst();
		args = p.snd();
		if (mi.error() != null) {
		    // Now, try to find the method with implicit conversions, making them explicit.
		    try {
		        p = tryImplicitConversions(this, tc, targetType, name, typeArgs, argTypes);
		        mi = (X10MethodInstance) p.fst();
		        args = p.snd();
		    }
		    catch (SemanticException e2) {
		        // Nothing worked. If you have a cc, thats the one. Exit with cc.
		        if (cc != null) {
		            Node result = cc.typeCheck(tc);
		            if (result instanceof Expr) {
		                X10TypeMixin.checkMissingParameters((Expr) result);
		            }
		            //Checker.checkOfferType(position(), ((ClosureCall) cc).closureInstance(), tc);
		            return result;
		        }
		        if (structCall == null) {
		            throw mi.error();
		        }
		    }
		}

		if (mi.error() != null) {
		    // Must be a struct call
		    assert (this.target() == null || this.target() instanceof TypeNode);
		    Checker.checkOfferType(position(), (X10ConstructorInstance) structCall.constructorInstance(), tc);
		    return structCall;
		}

		// OK so now we have mi and args that correspond to a success.
		if (structCall != null) {
		    // We have both a method and a struct constructor.
		    // Have to report an ambiguity, as there is no way for the user to resolve it.
		    throw new Errors.AmbiguousCall(mi, structCall.constructorInstance(), position());
		}

		if (cc != null) {
		    // We have both a method call and a closure call.  Spec section 8.2.
		    throw new Errors.AmbiguousCall(mi, cc, position());
		}

		// if the target is null, and thus implicit, find the target using the context
		Receiver target = this.target() == null ? computeReceiver(tc, mi) : this.target();

		if (target != null) {
		    /* This call is in a static context if and only if
		     * the target (possibly implicit) is a type node.
		     */
		    boolean staticContext = (target instanceof TypeNode);

		    if (staticContext && !mi.flags().isStatic()) {
		        throw new SemanticException("Cannot call non-static method " + name
		                + " of " + target.type() + " in static "
		                + "context.", this.position());
		    }

		    // If the target is super, but the method is abstract, then complain.
		    if (target instanceof Special &&
		            ((Special) target).kind() == Special.SUPER &&
		            mi.flags().isAbstract()) {
		        throw new SemanticException("Cannot call an abstract method " +
		                "of the super class", this.position());
		    }
		}

		Type rt = X10Field_c.rightType(mi.rightType(), mi.x10Def(), target, c);
		X10Call_c methodCall = (X10Call_c) this.methodInstance(mi).type(rt);
		methodCall = (X10Call_c) methodCall.arguments(args);
		if (this.target() == null)
		    methodCall = (X10Call_c) methodCall.targetImplicit(true).target(target);
		methodCall.checkProtoMethod();
		methodCall = (X10Call_c) PlaceChecker.makeReceiverLocalIfNecessary(methodCall, tc);
		//methodCall.checkConsistency(c); // [IP] Removed -- this is dead code at this point
		methodCall.checkAnnotations(tc);
		X10TypeMixin.checkMissingParameters(methodCall);
		Checker.checkOfferType(position(), (X10MethodInstance) methodCall.methodInstance(), tc);
		return methodCall;
	}



	public static Pair<MethodInstance,List<Expr>> tryImplicitConversions(X10ProcedureCall n, ContextVisitor tc,
	        Type targetType, final Name name, List<Type> typeArgs, List<Type> argTypes) throws SemanticException {
	    final X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	    final Context context = tc.context();

	    List<MethodInstance> methods = ts.findAcceptableMethods(targetType,
	            new DumbMethodMatcher(targetType, name, typeArgs, argTypes, context));

	    Pair<MethodInstance,List<Expr>> p = Converter.<MethodDef,MethodInstance>tryImplicitConversions(n, tc,
	            targetType, methods, new X10New_c.MatcherMaker<MethodInstance>() {
	        public Matcher<MethodInstance> matcher(Type ct, List<Type> typeArgs, List<Type> argTypes) {
	            return ts.MethodMatcher(ct, name, typeArgs, argTypes, context);
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
		                && ! (mi.isSafe() || flags.isPinned() || flags.isExtern()))
		            throw new SemanticException(mi + ": Only pinned methods can be called from pinned code.",
		                                        position());
		    }
		}
		catch (SemanticException e) {
		    Warnings.issue(tc.job(), "WARNING (should be error, but method annotations in XRX are wrong): " + e.getMessage(), position());
		}
	}

	private Node superTypeCheck(ContextVisitor tc) throws SemanticException {
		return super.typeCheck(tc);
	}

	Object constantValue;
	public Object constantValue() { return constantValue; }
	public boolean isConstant() { return constantValue != null; }

	public X10Call_c constantValue(Object value) {
		X10Call_c n = (X10Call_c) copy();
		n.constantValue = value;
		return n;
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


    /** Write the expression to an output file. */
   @Override
	  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	    w.begin(0);
	    if (!targetImplicit) {
	        if (target instanceof Expr) {
	          printSubExpr((Expr) target, w, tr);
	        }
	        else if (target instanceof X10CanonicalTypeNode_c) {
	            ((X10CanonicalTypeNode_c) target).prettyPrint(w, tr, false);
	        }
	        else if (target != null) {
	          print(target, w, tr);
	        }
	    w.write(".");
	    w.allowBreak(2, 3, "", 0);
	    }

        w.write(name + "");

	    if (typeArguments.size() > 0) {
	        w.write("[");
	        w.allowBreak(2, 2, "", 0); // miser mode
	        w.begin(0);
	                
	        for (Iterator<TypeNode> i = typeArguments.iterator(); i.hasNext(); ) {
	            TypeNode t = i.next();
	            t.prettyPrint(w, tr);
	            if (i.hasNext()) {
	            w.write(",");
	            w.allowBreak(0, " ");
	            }
	        }
            w.write("]");
	        w.end();
	    }
	    
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
	  }
}
