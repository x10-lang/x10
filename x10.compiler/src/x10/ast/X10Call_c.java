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
import polyglot.ast.Precedence;
import polyglot.ast.Call;
import polyglot.ast.Term;
import polyglot.ast.ProcedureCall;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Def;
import polyglot.types.LazyRef;

import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.util.ErrorInfo;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeBuilder;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ExceptionChecker;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Warnings;
import x10.errors.Errors.IllegalConstraint;
import x10.optimizations.inlining.Inliner;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10ConstructorInstance;
import polyglot.types.Context;
import x10.types.X10FieldInstance;

import x10.types.X10LocalInstance;
import x10.types.MethodInstance;
import x10.types.X10MethodDef;
import x10.types.X10ParsedClassType;
import x10.types.X10Use;
import polyglot.types.TypeSystem;
import polyglot.types.ProcedureDef;
import polyglot.types.ProcedureInstance;
import polyglot.types.MethodDef;
import polyglot.types.ErrorRef_c;
import polyglot.types.ContainerType;
import x10.types.checker.Checker;
import x10.types.checker.PlaceChecker;
import x10.types.constants.ConstantValue;
import x10.visit.X10TypeChecker;
import x10.X10CompilerOptions;
import x10.ExtensionInfo;

/**
 * Representation of an X10 method call.
 * @author Igor
 * @author vj
 */
public class X10Call_c extends Call_c implements X10Call {
    protected Receiver target;
    protected Id name;
    protected List<Expr> arguments;
    protected MethodInstance mi;
    protected boolean targetImplicit;

	// TODO: implement Settable and decompose x.apply(i)
	public X10Call_c(Position pos, Receiver target, Id name,
			List<TypeNode> typeArguments, List<Expr> arguments) {
		super(pos);
        assert(name != null && arguments != null); // target may be null
        this.target = target;
        this.name = name;
        this.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
        this.targetImplicit = (target == null);
		this.typeArguments = new ArrayList<TypeNode>(typeArguments);
	}

  /** Get the precedence of the call. */
  public Precedence precedence() {
    return Precedence.LITERAL;
  }

  /** Get the target object or type of the call. */
  public Receiver target() {
    return this.target;
  }

  /** Get the name of the call. */
  public Id name() {
      return this.name;
  }

  public MethodInstance procedureInstance() {
      return methodInstance();
  }

  public Call procedureInstance(ProcedureInstance<? extends ProcedureDef> pi) {
      return methodInstance((MethodInstance) pi);
  }

  public boolean isTargetImplicit() {
      return this.targetImplicit;
  }

  /** Get the actual arguments of the call. */
  public List<Expr> arguments() {
    return this.arguments;
  }

  /** Reconstruct the call. */
  protected X10Call_c reconstruct(Receiver target, Id name, List<Expr> arguments) {
    if (target != this.target || name != this.name || ! CollectionUtil.allEqual(arguments,
                                                         this.arguments)) {
      X10Call_c n = (X10Call_c) copy();

      // If the target changes, assume we want it to be an explicit target.
      n.targetImplicit = n.targetImplicit && target == n.target;

      n.target = target;
      n.name = name;
      n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
      return n;
    }

    return this;
  }

  public Node buildTypes(TypeBuilder tb) {
    Call_c n = (Call_c) super.buildTypes(tb);

    TypeSystem ts = tb.typeSystem();

    MethodInstance mi = ts.createMethodInstance(position(),position(), new ErrorRef_c<MethodDef>(ts, position(), "Cannot get MethodDef before type-checking method invocation."));
    return n.methodInstance(mi);
  }

    /**
     * Used to find the missing static target of a static method call.
     * Should return the container of the method instance.
     *
     */
    protected Type findContainer(TypeSystem ts, MethodInstance mi) {
        return mi.container();
    }

  /** Dumps the AST. */
  public void dump(CodeWriter w) {
    super.dump(w);

    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(targetImplicit " + targetImplicit + ")");
    w.end();

    if ( mi != null ) {
      w.allowBreak(4, " ");
      w.begin(0);
      w.write("(instance " + mi + ")");
      w.end();
    }

    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(name " + name + ")");
    w.end();

    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(arguments " + arguments + ")");
    w.end();
  }

  public Term firstChild() {
      if (target instanceof Term) {
          return (Term) target;
      }
      return listChild(arguments, null);
  }

  public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
      if (target instanceof Term) {
          Term t = (Term) target;

          if (!arguments.isEmpty()) {
              v.visitCFG(t, listChild(arguments, null), ENTRY);
              v.visitCFGList(arguments, this, EXIT);
          } else {
              v.visitCFG(t, this, EXIT);
          }
      }

      return succs;
  }

  /** Check exceptions thrown by the call. */
  public Node exceptionCheck(ExceptionChecker ec) {
    if (mi == null) {
      throw new InternalCompilerError(position(),
                                      "Null method instance after type "
                                      + "check.");
    }

    return super.exceptionCheck(ec);
  }


  // check that the implicit target setting is correct.
  protected void checkConsistency(Context c) throws SemanticException {
      if (targetImplicit) {
          // the target is implicit. Check that the
          // method found in the target type is the
          // same as the method found in the context.

          // as exception will be thrown if no appropriate method
          // exists.
          MethodInstance ctxtMI = c.findMethod(c.typeSystem().MethodMatcher(null, name.id(), mi.formalTypes(), c));

          // cannot perform this check due to the context's findMethod returning a
          // different method instance than the typeSystem in some situations
//          if (!c.typeSystem().equals(ctxtMI, mi)) {
//              throw new InternalCompilerError("Method call " + this + " has an " +
//                   "implicit target, but the name " + name + " resolves to " +
//                   ctxtMI + " in " + ctxtMI.container() + " instead of " + mi+ " in " + mi.container(), position());
//          }
      }
  }

	List<TypeNode> typeArguments;
	public List<TypeNode> typeArguments() { return typeArguments; }
	public X10Call typeArguments(List<TypeNode> args) {
		if (args == this.typeArguments) return this;
		X10Call_c n = (X10Call_c) copy();
		n.typeArguments = new ArrayList<TypeNode>(args);
		return n;
	}

	public X10Call target(Receiver target) {
        X10Call_c n = (X10Call_c) copy();
        n.target = target;
        return n;
	}
	public X10Call name(Id name)  {
        X10Call_c n = (X10Call_c) copy();
        n.name = name;
        return n;
	}
	public X10Call targetImplicit(boolean targetImplicit) {
        if (targetImplicit == this.targetImplicit) {
            return this;
        }
        X10Call_c n = (X10Call_c) copy();
        n.targetImplicit = targetImplicit;
        return n;
	}
	public X10Call arguments(List<Expr> arguments) {
		if (arguments == this.arguments) return this;
        X10Call_c n = (X10Call_c) copy();
        n.arguments = TypedList.copyAndCheck(arguments, Expr.class, true);
        return n;
	}
	public MethodInstance methodInstance() {
	    return (MethodInstance) this.mi;
	}
	
	public X10Call methodInstance(MethodInstance mi) {
        if (mi == this.mi) return this;
        X10Call_c n = (X10Call_c) copy();
        n.mi = mi;
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
	public Node disambiguate(ContextVisitor tc) {
		return this;
	}


	private Type resolveType(ContextVisitor tc, Position pos, Receiver r, Name name) {
        NodeFactory nf = (NodeFactory) tc.nodeFactory();
        TypeSystem ts = (TypeSystem) tc.typeSystem();

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
        
        Context c = (Context) tc.context();
        TypeSystem ts = (TypeSystem) tc.typeSystem();
        
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
        TypeSystem xts = (TypeSystem) tc.typeSystem();
        
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
     */
    protected X10New findStructConstructor(ContextVisitor tc, Receiver r, List<Type> typeArgs, List<Type> argTypes, List<Expr> args) {
        NodeFactory nf = (NodeFactory) tc.nodeFactory();
        Type t = resolveType(tc, position(), r, name().id());
        if (t == null)
            return null;
        if (Types.isX10Struct(t)) {
            X10New_c neu = (X10New_c) nf.X10New(position(), nf.CanonicalTypeNode(position(), t), Collections.<TypeNode>emptyList(), args);
            neu = (X10New_c) neu.newOmitted(true);
            Pair<ConstructorInstance, List<Expr>> p = X10New_c.findConstructor(tc, neu, t, argTypes);
            X10ConstructorInstance ci = (X10ConstructorInstance) p.fst();
            if (ci.error() != null)
                return null;

            neu = (X10New_c) neu.typeCheck(tc);
            ci = neu.constructorInstance();
            if (ci.error() == null)
                return neu;
        }
        return null;
    }

    private Receiver computeReceiver(ContextVisitor tc, MethodInstance mi) {
        TypeSystem xts = (TypeSystem) tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        Context c = (Context) tc.context();
        Position prefixPos = position().startOf().markCompilerGenerated();
        try {
            if (mi.flags().isStatic() || c.inStaticContext()) {
                Type container = findContainer(xts, mi);
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
                        scope = Types.setSelfVar(scope, thisVar);
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

	XVar getThis(Type t) {
	    t = Types.baseType(t);
	    if (t instanceof X10ClassType) {
	        return ((X10ClassType) t).x10Def().thisVar();
	    }
	    return null;
	}

	public Node typeCheck(ContextVisitor tc) {
		NodeFactory xnf = (NodeFactory) tc.nodeFactory();
		TypeSystem xts = (TypeSystem) tc.typeSystem();
		Context c = (Context) tc.context();

		if (mi != null && ((MethodInstance)mi).isValid()) // already typechecked
		    return this;

		Name name = this.name().id();

		Expr cc = null;

		{
			// Check if target.name is a field or local of function type;
			// if so, convert to a closure call.
			NodeFactory nf = (NodeFactory) tc.nodeFactory();

			Expr e = null;

			if (target() == null) {
			    final X10LocalInstance li = X10Local_c.findAppropriateLocal(tc, name);
			    if (li.error() == null) {
			        //e = xnf.Local(name().position(), name()).localInstance(li).type(li.type());
			        e = xnf.Local(name().position(), name()).localInstance(li);
			        e = (Expr) e.del().typeCheck(tc);
			        e = (Expr) e.del().checkConstants(tc);
			    }
			}
			if (e == null) {
			    boolean isStatic = target() instanceof TypeNode || (target() == null && c.inStaticContext());
			    X10FieldInstance fi = null;
			    if (target() == null) {
			        fi = (X10FieldInstance) c.findVariableSilent(name); // we didn't find a local, so it must be a field
		            if (fi != null && isStatic && !fi.flags().isStatic())
		                fi = null;
                    // it might be a field of an outer class, so we must check all inner classes in between are non-static
                    if (fi!=null && !fi.flags().isStatic()) {
                        X10ClassType containerType = (X10ClassType) fi.container();
                        X10ClassType currentClass = c.currentClass();
                        while (containerType.def()!=currentClass.def()) {
                            if (currentClass.flags().isStatic()) {
                                fi = null;
                                break;
                            }
                            currentClass = (X10ClassType) currentClass.outer();
                            if (currentClass==null) break;
                        }
                    }
			    }
			    if (fi == null) {
			        Type targetType = target() == null ? c.currentClass() : target().type();
			        fi = X10Field_c.findAppropriateField(tc, targetType, name,
			                isStatic, Types.contextKnowsType(target()), this.name().position());
			    }
			    if (fi.error() == null) {
			        Receiver target = this.target() == null ?
			                X10Disamb_c.makeMissingFieldTarget(fi, name().position(), tc) :
			                    this.target();
			        //e = xnf.Field(new Position(target.position(), name().position()), target,
			        //        name()).fieldInstance(fi).targetImplicit(target()==null).type(fi.type());
			        e = xnf.Field(new Position(target.position(), name().position()), target,
			                name()).fieldInstance(fi).targetImplicit(target()==null);
			        e = (Expr) e.del().typeCheck(tc);
			        e = (Expr) e.del().checkConstants(tc);
			    }
			}

			if (e != null) {
			    List<Type> typeArgs = new ArrayList<Type>(typeArguments().size());
			    for (TypeNode ti : typeArguments()) {
			        typeArgs.add(ti.type());
			    }
			    List<Type> actualTypes = new ArrayList<Type>(arguments().size());
			    for (Expr ei : arguments()) {
			        actualTypes.add(ei.type());
			    }
			    // First try to find the method without implicit conversions.
			    MethodInstance ci = Checker.findAppropriateMethod(tc, e.type(), ClosureCall.APPLY, typeArgs, actualTypes);
                if (ci.error() != null) {
			        // Now, try to find the method with implicit conversions, making them explicit.
			        try {
			            Pair<MethodInstance,List<Expr>> p = Checker.tryImplicitConversions(this, tc, e.type(), ClosureCall.APPLY, typeArgs, actualTypes);
			            ci =  p.fst();
			        }
			        catch (SemanticException se) { }
			    }
			    if (ci.error() != null) {
			        // Check for this case:
			        // val x = x();
			        if (e instanceof Local && xts.isUnknown(e.type())) {
			            Errors.issue(tc.job(),
			                    new SemanticException("Possible closure call on uninitialized variable " + ((Local) e).name() + ".",
			                            position()));
			            Node n = nf.ClosureCall(position(), e, typeArguments(), arguments()).closureInstance(ci);
			            //n = n.del().disambiguate(tc);
			            n = n.del().typeCheck(tc);
			            cc = (Expr) n;			            
			        }
			    } else {
			        Node n = nf.ClosureCall(position(), e, typeArguments(), arguments()).closureInstance(ci);
			        //n = n.del().disambiguate(tc);
			        n = n.del().typeCheck(tc);
			        cc = (Expr) n;
			    }
			}
		}


		if (target instanceof TypeNode) {
		    Type t = ((TypeNode) target).type();
		    t = Types.baseType(t);
		    if (t instanceof ParameterType) {
		        Errors.issue(tc.job(),
		                new SemanticException("Cannot invoke a static method of a type parameter.",
		                        position()));
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

		X10New structCall = null;

        // Now trying a method call
		Type targetType = this.target() == null ? null : this.target().type();
		MethodInstance mi = null;
		List<Expr> args = null;
		// First try to find the method without implicit conversions.
		Pair<MethodInstance, List<Expr>> p = Checker.findMethod(tc, this, targetType, name, typeArgs, argTypes);
		mi =  p.fst();
        // [DC] surely p.snd() is the same as this.arguments() ???
		args = p.snd();
	    SemanticException error=mi.error();
		if (error != null && !(error instanceof Errors.MultipleMethodDefsMatch)) {
		    // Now, try to find the method with implicit conversions, making them explicit.
		    try {
		        p = Checker.tryImplicitConversions(this, tc, targetType, name, typeArgs, argTypes);
		        mi = p.fst();
		        args = p.snd();
		    }
		    catch (SemanticException e2) {
		    	// [DC] seems to be a bug that e2 is not reported to the user?
		        // Nothing worked. If you have a cc, thats the one. Exit with cc.
		        if (cc != null) {
		            // [IP] TODO: move closure call lookup here 
		            Node result = cc.typeCheck(tc);
		            if (result instanceof Expr) {
		                try {
		                    Types.checkMissingParameters((Expr) result);
		                } catch (SemanticException e) {
		                    e.setPosition(this.position());
		                    Errors.issue(tc.job(), e, this);
		                }
		            }
		            //Checker.checkOfferType(position(), ((ClosureCall) cc).closureInstance(), tc);
		            Warnings.checkErrorAndGuard(tc, (X10Use<?>) ((ProcedureCall) cc).procedureInstance(), result);
		            return result;
		        }
		        // Otherwise, try a struct constructor.  If we have both a struct constructor and a
		        // closure call, then, according to spec section 8.2, the user can disambiguate
		        // using: "new StructCtor(...); therefore we give precedence to the closure-call.
		        // TODO: Need to try struct call with implicit conversions as well.
		        structCall = findStructConstructor(tc, target, typeArgs, argTypes, arguments);
		    }
		}

		if (mi.error() != null) {
		    // Must be a struct call
		    if (structCall != null) {
		        assert (this.target() == null || this.target() instanceof TypeNode);
		        try {
		            Checker.checkOfferType(position(), structCall.constructorInstance(), tc);
		        } catch (SemanticException e) {
		            e.setPosition(this.position());
		            Errors.issue(tc.job(), e, this);
		        }
		        Warnings.checkErrorAndGuard(tc, structCall.constructorInstance(), structCall);
		        return structCall;
		    }
		    // Nope, it wasn't -- so report the error and proceed
		    mi.error().setPosition(this.position());
		    Errors.issue(tc.job(), mi.error(), this);
		}

		// OK so now we have mi and args that correspond to a success.
        // We have both a method and a struct constructor, so we give precedence to the method (the user can chose the struct ctor with "new")

		// if the target is null, and thus implicit, find the target using the context
		Receiver target = this.target() == null ? computeReceiver(tc, mi) : this.target();

		if (target != null) {
		    /* This call is in a static context if and only if
		     * the target (possibly implicit) is a type node.
		     */
		    boolean staticContext = (target instanceof TypeNode);

		    if (staticContext && !mi.flags().isStatic()) {
		        Errors.issue(tc.job(),
		                new Errors.CannotAccessNonStaticFromStaticContext(mi, position()));
		    }

		    // If the target is super, but the method is abstract, then complain.
		    if (target instanceof Special &&
		            ((Special) target).kind() == Special.SUPER &&
		            mi.flags().isAbstract()) {
		        Errors.issue(tc.job(),
		                new SemanticException("Cannot call an abstract method of the super class",
		                        this.position()));
		    }
		}

		Type rt = Checker.rightType(mi.rightType(), mi.x10Def(), target, c);
		X10Call_c methodCall = (X10Call_c) this.methodInstance(mi);
		methodCall = (X10Call_c) methodCall.arguments(args);
		{
			// check if this is a property call, and if so adjust the return type
			if (mi.flags().isProperty()) {
				if (c.inDepType()) {
				    // vj TODO: Why is this being repeated? 
					// rt = Checker.rightType(mi.rightType(), mi.x10Def(), target, c);
				} else {
					try {
						rt =  Checker.expandCall(mi.rightType(), methodCall, c);
					} catch (IllegalConstraint z) {
						// ignore, we will go with mi.rightType.
					}
				}
			}
		}
		methodCall = (X10Call_c) methodCall.type(rt);
		if (this.target() == null)
		    methodCall = (X10Call_c) methodCall.targetImplicit(true).target(target);
		try {
		    Types.checkMissingParameters(methodCall);
		} catch (SemanticException e) {
		    e.setPosition(this.position());
		    Errors.issue(tc.job(), e, this);
		}
		try {
		    Checker.checkOfferType(position(), (MethodInstance) methodCall.methodInstance(), tc);
		} catch (SemanticException e) {
		    e.setPosition(this.position());
		    Errors.issue(tc.job(), e, this);
		}
		Warnings.checkErrorAndGuard(tc, mi, methodCall);
		List<Type> ctcAnnotations = ((X10MethodDef) mi.def()).annotationsMatching(xts.CompileTimeConstant());
		if (!ctcAnnotations.isEmpty()) {
		    X10CompilerOptions opts = (X10CompilerOptions) tc.job().extensionInfo().getOptions();
		    ConstantValue cv = Inliner.extractCompileTimeConstant(rt, ctcAnnotations, opts, c);
		    if (cv != null) {
		        methodCall = methodCall.constantValue(cv);
		    }
		}
		return methodCall;
	}


	ConstantValue constantValue;
	public ConstantValue constantValue() { return constantValue; }
	public boolean isConstant() { return constantValue != null; }

	public X10Call_c constantValue(ConstantValue value) {
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
       if (name.toString().startsWith("operator") && arguments.size() >= 2) {
           w.begin(0);
           print(arguments.get(0), w, tr);
           w.write(name.toString().substring(8));
           print(arguments.get(1), w, tr);
           w.allowBreak(0, " ");
           w.end();
           return;
       }

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
	    if (nonVirtual()) {
	        w.write("/"+"*"+"non-virtual"+"*"+"/");
	    }
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

   private boolean nonVirtual = false;
   
    /* (non-Javadoc)
     * @see polyglot.ast.Call#isNonVirtual()
     */
    public boolean nonVirtual() {
        return nonVirtual;
    }

    /* (non-Javadoc)
     * @see polyglot.ast.Call#markNonVirtual()
     */
    public X10Call nonVirtual(boolean nv) {
        if (nonVirtual == nv) return this;
        X10Call_c c = (X10Call_c) copy();
        c.nonVirtual = nv;
        return c;
    }

    public List<Type> throwTypes(TypeSystem ts) {
        List<Type> l = new ArrayList<Type>();

        assert mi != null : "null mi for " + this;

        l.addAll(mi.throwTypes());
        l.addAll(ts.uncheckedExceptions());

        if (target instanceof Expr && ! (target instanceof Special)) {
            l.add(ts.NullPointerException());
        }

        return l;
    }
}
