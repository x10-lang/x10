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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.AmbExpr_c;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.MethodDecl;
import polyglot.ast.MethodDecl_c;
import polyglot.ast.NamedVariable;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeCheckFragmentGoal;
import polyglot.ast.TypeNode;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.SetResolverGoal;
import polyglot.main.Reporter;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Def;
import polyglot.types.ErrorRef_c;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.MemberInstance;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Ref_c;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import x10.util.AnnotationUtils;
import x10.util.CollectionFactory;
import polyglot.util.ErrorInfo;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeCheckPreparer;
import polyglot.visit.TypeChecker;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.constraint.XTerm;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;
import x10.extension.X10Del;
import x10.extension.X10Del_c;
import x10.extension.X10Ext;
import x10.types.ConstrainedType;

import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import polyglot.types.Context;

import x10.types.X10MemberDef;
import x10.types.X10MethodDef;
import x10.types.MethodInstance;
import x10.types.X10ParsedClassType_c;
import x10.types.X10ProcedureDef;
import x10.types.X10TypeEnv_c;

import polyglot.types.TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.X10ParsedClassType;
import x10.types.X10MethodDef_c;
import x10.types.checker.Checker;
import x10.types.checker.PlaceChecker;
import x10.types.checker.VarChecker;
import x10.types.checker.Converter;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CNativeRequirement;
import x10.types.constraints.TypeConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.visit.X10TypeChecker;

/** A representation of a method declaration.
 * Includes an extra field to represent the guard
 * in the method definition.
 * 
 * @author vj
 *
 */
public class X10MethodDecl_c extends MethodDecl_c implements X10MethodDecl {
	// The representation of the  guard on the method definition
	DepParameterExpr guard;
	List<TypeParamNode> typeParameters;
	List<TypeNode> throwsTypes;

	TypeNode offerType;
	TypeNode hasType;
	public X10MethodDecl_c(NodeFactory nf, Position pos, FlagsNode flags, 
			TypeNode returnType, Id name,
			List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr guard,  TypeNode offerType, List<TypeNode> throwsTypes, Block body) {
		super(pos, flags, returnType instanceof HasTypeNode_c ? nf.UnknownTypeNode(returnType.position()) : returnType, 
				name, formals,  body);
		this.guard = guard;
		this.typeParameters = TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
		if (returnType instanceof HasTypeNode_c) 
			hasType = ((HasTypeNode_c) returnType).typeNode();
		this.offerType = offerType;
		this.throwsTypes = throwsTypes;
	}

	public TypeNode offerType() {
		return offerType;
	}
	protected X10MethodDecl_c hasType(TypeNode hasType) {
		if (this.hasType != hasType)  {
			X10MethodDecl_c n = (X10MethodDecl_c) copy();
			n.hasType = hasType;
			return n;
		}
		return this;
	}
	public X10MethodDecl_c offerType(TypeNode offerType) {
		if (this.offerType != offerType)  {
			X10MethodDecl_c n = (X10MethodDecl_c) copy();
			n.offerType = offerType;
			return n;
		}
		return this;
	}
	public List<TypeNode> throwsTypes() {
		return throwsTypes;
	}

	public X10MethodDecl_c throwsTypes(List<TypeNode> throwsTypes) {
		if (this.throwsTypes != throwsTypes)  {
			X10MethodDecl_c n = (X10MethodDecl_c) copy();
			n.throwsTypes = throwsTypes;
			return n;
		}
		return this;
	}

	protected X10MethodDef createMethodDef(TypeSystem ts, X10ClassDef ct, Flags flags) {
		X10MethodDef mi = (X10MethodDef) ts.methodDef(position(), name().position(), Types.ref(ct.asType()), flags, returnType.typeRef(), name.id(),
				Collections.<Ref<? extends Type>>emptyList(), Collections.<Ref<? extends Type>>emptyList(), 
				offerType == null ? null : offerType.typeRef());

		mi.setThisDef(ct.thisDef());
		mi.setPlaceTerm(PlaceChecker.methodPlaceTerm(mi));
		return mi;
	}

	@Override
	public Node buildTypesOverride(TypeBuilder tb) {
		// Have to inline super.buildTypesOverride(tb) to make sure the body
		// is visited after the appropriate information is set up
		TypeSystem ts = tb.typeSystem();

		X10ClassDef ct = tb.currentClass();
		assert ct != null;

		Flags flags = this.flags.flags();

		if (ct.flags().isInterface()) {
		    flags = flags.Public().Abstract();
		}

		X10MethodDecl_c n = this;

		X10MethodDef md = createMethodDef(ts, ct, flags);
		ct.addMethod(md);

		TypeBuilder tbChk = tb.pushCode(md);

		final TypeBuilder tbx = tb;
		final MethodDef mdx = md;
		n = (X10MethodDecl_c) n.visitSignature(new NodeVisitor() {
		    @Override
		    public Node override(Node n) {
		        return X10MethodDecl_c.this.visitChild(n, tbx.pushCode(mdx));
		    }
		});

		List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>(n.formals().size());
		for (Formal f1 : n.formals()) {
		    formalTypes.add(f1.type().typeRef());
		}

		md.setReturnType(n.returnType().typeRef());
		md.setFormalTypes(formalTypes);
		
        List<Ref<? extends Type>> throw_types = new ArrayList<Ref<? extends Type>>();
        for (TypeNode t : n.throwsTypes()) {
            throw_types.add(t.typeRef());
        }
        md.setThrowTypes(throw_types);
		

		n = (X10MethodDecl_c) X10Del_c.visitAnnotations(n, tb);
				
		List<AnnotationNode> as = ((X10Del) n.del()).annotations();
		if (as != null) {
			List<Ref<? extends Type>> ats = new ArrayList<Ref<? extends Type>>(as.size());
			for (AnnotationNode an : as) {
				ats.add(an.annotationType().typeRef());
			}
			md.setDefAnnotations(ats);
		}

		// Enable return type inference for this method declaration.
		if (n.returnType() instanceof UnknownTypeNode) {
			md.inferReturnType(true);
		}
		// XXXX inferred
		if (n.guard() != null) {
			md.setSourceGuard(n.guard().valueConstraint());
			md.setGuard(Types.<CConstraint>lazyRef(ConstraintManager.getConstraintSystem().makeCConstraint()));
			md.setTypeGuard(n.guard().typeConstraint());
		} else {
			md.setGuard(Types.<CConstraint>lazyRef(ConstraintManager.getConstraintSystem().makeCConstraint()));
		}

		List<ParameterType> typeParameters = new ArrayList<ParameterType>(n.typeParameters().size());
		for (TypeParamNode tpn : n.typeParameters()) {
			typeParameters.add(tpn.type());
		}
		md.setTypeParameters(typeParameters);

		List<LocalDef> formalNames = new ArrayList<LocalDef>(n.formals().size());
		for (Formal f : n.formals()) {
			formalNames.add(f.localDef());
		}
		md.setFormalNames(formalNames);

		Flags xf = md.flags();
		if (xf.isProperty()) {
			final LazyRef<XTerm> bodyRef = Types.lazyRef(null);
			bodyRef.setResolver(new SetResolverGoal(tb.job()).intern(tb.job().extensionInfo().scheduler()));
			md.body(bodyRef);
		}

		// property implies public, final
		if (xf.isProperty()) {
			if (xf.isAbstract())
				xf = xf.Public();
			else
				xf = xf.Public().Final();

			md.setFlags(xf);
			n = (X10MethodDecl_c) n.flags(n.flags().flags(xf));
		}

		Block body = (Block) n.visitChild(n.body, tbChk);

        n = (X10MethodDecl_c) n.body(body);

		return n.methodDef(md);
	}

	@Override
	public void addDecls(Context c) {


	}
	public void setResolver(Node parent, final TypeCheckPreparer v) {
		X10MethodDef mi = (X10MethodDef) this.mi;
		if (mi.body() instanceof LazyRef<?>) {
            LazyRef<XTerm> r = (LazyRef<XTerm>) mi.body();
			TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo());
			tc = (TypeChecker) tc.context(v.context().freeze());
			r.setResolver(new TypeCheckFragmentGoal<XTerm>(parent, this, tc, r, false));
		}
//		if (mi.guard() instanceof LazyRef<?>) {
//			final LazyRef<CConstraint> g = (LazyRef<CConstraint>) mi.guard();
//			g.setResolver(new Runnable(){
//				public void run() {
//			       	if (mi.sourceGuard() != null) {
//		        		g.update(mi.sourceGuard().get());
//		                System.err.println("Propagating source guard unmodified " + g.get());
//		        	} else {
//		        		g.update(ConstraintManager.getConstraintSystem().makeCConstraint());
//		        	}
//				}
//			});
//		}
	}

	/** Visit the children of the method. */
	public Node visitSignature(NodeVisitor v) {
		FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
		Id name = (Id) this.visitChild(this.name, v);
		List<TypeParamNode> typeParams = visitList(this.typeParameters, v);
		List<Formal> formals = this.visitList(this.formals, v);
		DepParameterExpr guard = (DepParameterExpr) visitChild(this.guard, v);
		TypeNode ht = (TypeNode) visitChild(this.hasType, v);
		TypeNode ot = (TypeNode) visitChild(this.offerType, v);
		TypeNode returnType = (TypeNode) visitChild(this.returnType, v);
		List<TypeNode> throwsTypes = visitList(this.throwsTypes, v);
		return reconstruct(flags, name, typeParams, formals, guard, ht, returnType, ot, throwsTypes, this.body);
	}

	/** Reconstruct the method. 
	 * @param throwsTypes2 */
	protected X10MethodDecl_c reconstruct(FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<Formal> formals, DepParameterExpr guard, TypeNode hasType, TypeNode returnType, TypeNode offerType, List<TypeNode> throwsTypes, Block body) {
		X10MethodDecl_c n = (X10MethodDecl_c) super.reconstruct(flags, returnType, name, formals, body);
		if (! CollectionUtil.allEqual(typeParameters, n.typeParameters) || guard != n.guard || hasType != n.hasType || offerType != n.offerType || ! CollectionUtil.allEqual(throwsTypes, n.throwsTypes) ) {
			if (n == this) {
				n = (X10MethodDecl_c) n.copy();
			}
			n.typeParameters = TypedList.copyAndCheck(typeParameters, TypeParamNode.class, true);
			n.guard = guard;
			n.hasType = hasType;
			n.offerType = offerType;
			n.throwsTypes = throwsTypes;
			return n;
		}
		return n;
	}

	public List<TypeParamNode> typeParameters() {
		return typeParameters;
	}

	public X10MethodDecl_c typeParameters(List<TypeParamNode> typeParams) {
		X10MethodDecl_c n = (X10MethodDecl_c) copy();
		n.typeParameters=TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
		return n;
	}

	public DepParameterExpr guard() { return guard; }
	public X10MethodDecl_c guard(DepParameterExpr e) {
		X10MethodDecl_c n = (X10MethodDecl_c) copy();
		n.guard = e;
		return n;
	}

	@Override
	public X10MethodDecl_c flags(FlagsNode flags) {
	    return (X10MethodDecl_c) super.flags(flags);
	}
	@Override
	public X10MethodDecl_c returnType(TypeNode returnType) {
	    return (X10MethodDecl_c) super.returnType(returnType);
	}
	@Override
	public X10MethodDecl_c name(Id name) {
	    return (X10MethodDecl_c) super.name(name);
	}
	@Override
	public X10MethodDecl_c formals(List<Formal> formals) {
	    return (X10MethodDecl_c) super.formals(formals);
	}
	@Override
	public X10MethodDecl_c methodDef(MethodDef mi) {
	    return (X10MethodDecl_c) super.methodDef(mi);
	}
	@Override
	public X10MethodDef methodDef() {
	    return (X10MethodDef) super.methodDef();
	}

	@Override
	public Context enterScope(Context c) {
	    c = super.enterScope(c);
	    if (!c.inStaticContext() && methodDef().thisDef() != null)
	        c.addVariable(methodDef().thisDef().asInstance());
	    return c;
	}

	@Override
	public Context enterChildScope(Node child, Context c) {
		// We should have entered the method scope already.
		assert c.currentCode() == this.methodDef();
		Context oldC = c;
		if (child != body()) {
			// Push formals so they're in scope in the types of the other formals.
			c = c.pushBlock();
			for (TypeParamNode f : typeParameters) {
				f.addDecls(c);
			}
			for (int i=0; i < formals.size(); i++) {
				Formal f = formals.get(i);
				f.addDecls(c);
				if (f == child)
					break; // do not add downstream formals
			}
			
		}

		// Ensure that the place constraint is set appropriately when
		// entering the appropriate children
		if (child == body || child == returnType || child == hasType || child == offerType || child == guard
				|| (formals != null && formals.contains(child))|| (throwsTypes != null && throwsTypes.contains(child))) {
		    X10MethodDef md = methodDef();
		    XConstrainedTerm placeTerm = md == null ? null : md.placeTerm();
		    if (placeTerm == null) {
		        placeTerm = PlaceChecker.methodPlaceTerm(md);
		    }
			if (c == oldC)
				c = c.pushBlock();
			c.setPlace(placeTerm);
		}

		if (child == body && offerType != null && offerType.typeRef().known()) {
			if (oldC == c)
				c = c.pushBlock();
		    c.setCollectingFinishScope(offerType.type());
		}

		// Add the method guard into the environment.
		if (guard != null) {
		    if (child == body || child == offerType ||  child == hasType || child == returnType
		    		|| (formals != null && formals.contains(child))|| (throwsTypes != null && throwsTypes.contains(child))) {
		        Ref<CConstraint> vc = guard.valueConstraint();
		        Ref<TypeConstraint> tc = guard.typeConstraint();

		        if (vc != null || tc != null) {
		            if (oldC==c) {
		                c = c.pushBlock();
		            }
		            c.setName(" MethodGuard for |" + mi.name() + "| ");
		            if (vc != null)
		                c.addConstraint(vc);
		            if (tc != null) {
		                c.setTypeConstraintWithContextTerms(tc);
		            }
		        }            
		    }
		}
		addInClassInvariantIfNeeded(c, false);
		return super.enterChildScope(child, c);
	}
	public void addInClassInvariantIfNeeded(Context c, boolean force) {
	    if (!mi.flags().isStatic()) {
	        // this call occurs in the body of an instance method for T.
	        // Pick up the real clause for T -- that information is known 
	        // statically about "this"
	        Ref<? extends ContainerType> container = mi.container();
	        if (container.known()) { 
	            X10ClassType type = (X10ClassType) Types.get(container);
	            
	            Ref<CConstraint> rc = type.x10Def().realClauseWithThis();
	            c.addConstraint(rc);
	            Ref<TypeConstraint> tc = type.x10Def().typeBounds();
	            if (tc != null) {
	                c.setTypeConstraintWithContextTerms(tc);
	            }
	        }
	    }
	}
	public void translate(CodeWriter w, Translator tr) {
		Context c = tr.context();
		Flags flags = flags().flags();

		if (c.currentClass().flags().isInterface()) {
			flags = flags.clearPublic();
			flags = flags.clearAbstract();
		}

		FlagsNode oldFlags = this.flags;
		try {
			this.flags = this.flags.flags(flags.retainJava()); // ensure that X10Flags are not printed out .. javac will not know what to do with them.
			super.translate(w, tr);
		}
		finally {
			this.flags = oldFlags;
		}
	}

	@Override
	public Node setResolverOverride(Node parent, TypeCheckPreparer v) {
		final X10MethodDef mi = (X10MethodDef) this.mi;
		if (mi.inferGuard() && body() != null) {
			NodeVisitor childv = v.enter(parent, this);
			childv = childv.enter(this, v.nodeFactory().Empty(Position.COMPILER_GENERATED));

			if (mi.guard() instanceof LazyRef<?>) {
				TypeCheckPreparer tcp = (TypeCheckPreparer) childv;
				final LazyRef<CConstraint> r = (LazyRef<CConstraint>) mi.guard();
				TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo(), true);
				tc = (TypeChecker) tc.context(tcp.context().freeze());
				r.setResolver(new TypeCheckInferredGuardGoal(this, new Node[] { }, body(), tc, r, mi.sourceGuard()));

				/*
				            g.setResolver(new Runnable(){
				                public void run() {
				                    g.update(mi.sourceGuard().get());
				                    System.err.println("Propagating guard " + g.get() + " for method " + mi.name());
				                    }
				                });
				 */
			}
		}
		else {
			if (mi.guard() instanceof LazyRef<?>) {
				final LazyRef<CConstraint> r = (LazyRef<CConstraint>) mi.guard();
				r.setResolver(new Runnable(){
					public void run() {
						if (mi.sourceGuard() != null) {
							r.update(mi.sourceGuard().get());
//							System.err.println("Propagating source guard unmodified " + r.get());
						} else {
							r.update(ConstraintManager.getConstraintSystem().makeCConstraint());
						}
					}
				});
			}
		}

		if (returnType() instanceof UnknownTypeNode && body() != null) {
			UnknownTypeNode tn = (UnknownTypeNode) returnType();

			NodeVisitor childv = v.enter(parent, this);
			childv = childv.enter(this, returnType());

			if (childv instanceof TypeCheckPreparer) {
				TypeCheckPreparer tcp = (TypeCheckPreparer) childv;
				final LazyRef<Type> r = (LazyRef<Type>) tn.typeRef();
				TypeChecker tc = new X10TypeChecker(v.job(), v.typeSystem(), v.nodeFactory(), v.getMemo(), true);
				tc = (TypeChecker) tc.context(tcp.context().freeze());
                // todo: if the return type is void, let's skip this whole resolver stuff.
				r.setResolver(new TypeCheckReturnTypeGoal(this, new Node[] { guard(), offerType() }, body(), tc, r));
			}
		}
		return super.setResolverOverride(parent, v);
	}

	@Override
	protected void checkFlags(ContextVisitor tc, Flags xf) {
		// Set the native flag if incomplete or extern so super.checkFlags doesn't complain.
		super.checkFlags(tc, xf);

		if (xf.isProperty() && ! xf.isAbstract() && ! xf.isFinal()) {
			Errors.issue(tc.job(),
			        new Errors.NonAbstractPropertyMethodMustBeFinal(position()));
		}
		//if (xf.isProperty() && xf.isStatic()) {
		//	Errors.issue(tc.job(),
		//	        new Errors.PropertyMethodCannotBeStatic(position()));
		//}
	}
	
	private Type getType(ContextVisitor tc, String name) throws SemanticException {
		return tc.typeSystem().systemResolver().findOne(QName.make(name));
	}
	private boolean nodeHasOneAnnotation(ContextVisitor tc, Node n, String ann_name) {
		X10Ext ext = (X10Ext) n.ext();
		try {
			List<X10ClassType> anns = ext.annotationMatching(getType(tc, ann_name));
			if (anns.size() == 0) return false;
			if (anns.size() > 1) {
				Errors.issue(tc.job(), new SemanticException("Cannot have more than one @Opaque annotation", n.position()));
			}
			return true;
		} catch (SemanticException e) {
			assert false : e;
			return false; // in case asserts are off
		}
	}

	@Override
	public Node typeCheck(ContextVisitor tc) {
		X10MethodDecl_c n = this;
		NodeFactory nf = tc.nodeFactory();
		TypeSystem ts = (TypeSystem) tc.typeSystem();
		if (((TypeSystem) tc.typeSystem()).isStructType(mi.container().get())) {
			Flags xf = mi.flags().Final();
			mi.setFlags(xf);
			n = (X10MethodDecl_c) n.flags(n.flags().flags(xf));
		}

		Flags xf = mi.flags();

		//if (xf.isProperty() && body == null) {
		//    Errors.issue(tc.job(),
		//            new SemanticException("A property method must have a body.", position()));
		//}

		if (xf.isProperty()) {
			boolean ok = false;
			if (xf.isAbstract() || xf.isNative()) {
				ok = true;
			}
			if (nodeHasOneAnnotation(tc,n,"x10.compiler.Opaque")) {
				ok = true;
			} else if (n.body != null && n.body.statements().size() == 1) {
				Stmt s = n.body.statements().get(0);
				if (s instanceof Return) {
					Return r = (Return) s;
					if (r.expr() != null) {
                        final X10MethodDef_c mdef = (X10MethodDef_c) mi;
                        // detecting cycles in property methods
                        // recurse into the expr to see what other methods we call
                        if (mdef.isCircularPropertyMethod(r.expr())) {
                            Errors.issue(tc.job(), new SemanticException("Circular property method definition. Expanding the property method may result in an infinite loop.\n\tProperty:"+mi, position));
                        } else {
                            // while expanding this expression we might recursively type check the same method
                            XTerm v = null;
                            try {
                                Context cxt = tc.context();
                                // Translate the body with the SpecialAsQualifiedVar 
                                // bit turned on. 
                                cxt = cxt.pushSpecialAsQualifiedVar();
                                v = ts.xtypeTranslator().translate((CConstraint) null, r.expr(), cxt, true);
                                ok = true;
                                X10MethodDef mi = (X10MethodDef) this.mi;
                                if (mi.body() instanceof LazyRef<?>) {
                                    LazyRef<XTerm> bodyRef = (LazyRef<XTerm>) mi.body();
                                    bodyRef.update(v);
                                }
                            } catch (IllegalConstraint z) {
                            	Errors.issue(tc.job(),z);
                            	ok = true;
                            }
                           
                        }
					}
				}
			}
			if (! ok)
				Errors.issue(tc.job(),
				        new Errors.MethodBodyMustBeConstraintExpressiong(position()));
		}

		n = (X10MethodDecl_c) n.superTypeCheck(tc);

		try {
		    dupFormalCheck(typeParameters, formals);
		} catch (SemanticException e) {
		    Errors.issue(tc.job(), e, n);
		}

		try {
		    Types.checkMissingParameters(n.returnType());
		} catch (SemanticException e) {
		    Errors.issue(tc.job(), e, n.returnType());
		}


        if (!position.isCompilerGenerated()) { // for struct X[+T], we generate equals(T), but it is type-safe because it is readonly and struct are final.
            // formals are always in contravariant positions, while return type in covariant position (ctors are ignored)
            for (Formal f : n.formals) {
                final TypeNode fType = f.type();
                Types.checkVariance(fType, ParameterType.Variance.CONTRAVARIANT,tc.job());
            }
            Types.checkVariance(n.returnType, ParameterType.Variance.COVARIANT,tc.job());
        }

        // check subtype restriction on implicit and explicit operator as:
        // the return type must be a subtype of the container type
        final Name nameId = n.name.id();
        if (nameId==Converter.implicit_operator_as || nameId==Converter.operator_as) {
            final X10MethodDef methodDef = n.methodDef();
            final ContainerType container = methodDef.container().get();
            final Type returnT = Types.baseType(methodDef.returnType().get());
            final List<Ref<? extends Type>> formals = methodDef.formalTypes();
            assert formals.size()==1 : "Currently it is a parsing error if the number of formals for an implicit or explicit 'as' operator is different than 1! formals="+formals;
            final Type argumentT = Types.baseType(formals.get(0).get());
            // I compare ClassDef due to this example:
            //class B[U] {
            //    public static operator[T](x:T):B[T] = null;
            //}
            // We only search in the target type (not the source type), so
            // public static operator (x:Bar) as Any = null; // ERR
            assert container instanceof X10ParsedClassType : container;
            boolean isReturnWrong = !(returnT   instanceof X10ParsedClassType) || ((X10ParsedClassType)returnT  ).def()!=((X10ParsedClassType)container).def();
            boolean isFormalWrong = SEARCH_CASTS_ONLY_IN_TARGET ||  !(argumentT instanceof X10ParsedClassType) || ((X10ParsedClassType)argumentT).def()!=((X10ParsedClassType)container).def();
            if (isReturnWrong && isFormalWrong) {
                Errors.issue(tc.job(),
				        new Errors.MustHaveSameClassAsContainer(n.position()));
            }
        }
		return n;
	}
    private static boolean SEARCH_CASTS_ONLY_IN_TARGET = true; // see XTENLANG_2667



	public static void dupFormalCheck(List<TypeParamNode> typeParams, List<Formal> formals) throws SemanticException {
		Set<Name> pnames = CollectionFactory.newHashSet();
		for (TypeParamNode p : typeParams) {
			Name name = p.name().id();
			if (pnames.contains(name))
				throw new Errors.TypeParameterMultiplyDefined(name, p.position());
			pnames.add(name);
		}

		// Check for duplicate formals. This isn't caught in Formal_c
		// because we add all the formals into the scope before visiting a
		// formal, so the lookup of a duplicate formal returns itself rather
		// than the previous formal.
		Set<Name> names = CollectionFactory.newHashSet();
		LinkedList<Formal> q = new LinkedList<Formal>();
		q.addAll(formals);
		while (! q.isEmpty()) {
			Formal f = q.removeFirst();
			Name name = f.name().id();
			if (! name.equals(Name.make(""))) {
				if (names.contains(name))
					throw new Errors.LocalVariableMultiplyDefined(name, f.position());
				names.add(name);
			}
			if (f instanceof X10Formal) {
				X10Formal ff = (X10Formal) f;
				q.addAll(ff.vars());
			}
		}
	}

	protected X10MethodDecl_c superTypeCheck(ContextVisitor tc) {
		return (X10MethodDecl_c) super.typeCheck(tc);
	}

	@Override
	public Node conformanceCheck(ContextVisitor tc) {
		//checkVariance(tc);

		MethodDef mi = this.methodDef();
		TypeSystem xts = (TypeSystem) tc.typeSystem();

		if (mi.flags().isProperty()) {
			MethodInstance xmi = (MethodInstance) mi.asInstance();
			if (xmi.guard() != null && ! xmi.guard().valid())
				Errors.issue(tc.job(),
				        new Errors.PropertyMethodCannotHaveGuard(guard, position()));
		}

		checkVisibility(tc, this);

		// Need to ensure that method overriding is checked in the right context
		// The classInvariant needs to be added.
		// Note that the guard should not be added, since we need to check that the
		// guard is entailed by any method that is overridden by this method.
		Context childtc = tc.context().pushBlock(); 
		addInClassInvariantIfNeeded(childtc, true);
		ContextVisitor childVisitor = tc.context(childtc);
		return super.conformanceCheck(childVisitor);
	}

	final static boolean CHECK_VISIBILITY = false;

	protected static void checkVisibility(ContextVisitor tc, final ClassMember mem) {
		// This doesn't work since we've already translated away expressions into constraints.
		if (! CHECK_VISIBILITY)
			return;

		final SemanticException[] ex = new SemanticException[1];

		// Check if all fields, methods, etc accessed from the signature of mem are in scope wherever mem can be accessed.
		// Assumes the fields, methods, etc are accessible from mem, at least.

		mem.visitChildren(new NodeVisitor() {
			boolean on = false;

			@Override
			public Node override(Node parent, Node n) {
				if (! on) {
					if (n instanceof TypeNode) {
						try {
							on = true;
							return this.visitEdgeNoOverride(parent, n);
						}
						finally {
							on = false;
						}
					}
					else {
						return this.visitEdgeNoOverride(parent, n);

					}
				}

				if (n instanceof Stmt) {
					return n;
				}

				if (parent instanceof FieldDecl && n == ((FieldDecl) parent).init()) {
					return n;
				}

				if (n instanceof Field) {
					FieldInstance fi = (((Field) n).fieldInstance());
					if (! hasSameScope(fi, mem.memberDef())) {
						ex[0] = new SemanticException("Field " + fi.name() + " cannot be used in this signature; not accessible from all contexts in which the member is accessible.", n.position());
					}
				}
				if (n instanceof Call) {
					MethodInstance mi = (((Call) n).methodInstance());
					if (! hasSameScope(mi, mem.memberDef())) {
						ex[0] = new SemanticException("Method " + mi.signature() + " cannot be used in this signature; not accessible from all contexts in which the member is accessible.", n.position());
					}
				}
				if (n instanceof ClosureCall) {
					MethodInstance mi = (((ClosureCall) n).closureInstance());
					if (! hasSameScope(mi, mem.memberDef())) {
						ex[0] = new SemanticException("Method " + mi.signature() + " cannot be used in this signature; not accessible from all contexts in which the member is accessible.", n.position());
					}
				}
				if (n instanceof TypeNode) {
					TypeNode tn = (TypeNode) n;
					Type t = tn.type();
					t = Types.baseType(t);
					if (t instanceof X10ClassType) {
						X10ClassType ct = (X10ClassType) t;
						if (! hasSameScope(ct, mem.memberDef())) {
							ex[0] = new SemanticException("Class " + ct.fullName() + " cannot be used in this signature; not accessible from all contexts in which the member is accessible.", n.position());
						}
					}
				}
				if (n instanceof New) {
					ConstructorInstance ci = (((New) n).constructorInstance());
					if (! hasSameScope(ci, mem.memberDef())) {
						ex[0] = new SemanticException("Constructor " + ci.signature() + " cannot be used in this signature; not accessible from all contexts in which the member is accessible.", n.position());
					}
				}

				return null;
			}

			Flags getSignatureFlags(MemberDef def) {
				Flags sigFlags = def.flags();
				if (def instanceof ClassDef) {
					ClassDef cd = (ClassDef) def;
					if (cd.isTopLevel()) {
						return sigFlags;
					}
					if (cd.isMember()) {
						ClassDef outer = Types.get(cd.outer());
						Flags outerFlags = getSignatureFlags(outer);
						return combineFlagsWithContainerFlags(sigFlags, outerFlags);
					}
					return Flags.PRIVATE;
				}
				else {
					Type t = Types.get(def.container());
					t = Types.baseType(t);
					if (t instanceof ClassType) {
						ClassType ct = (ClassType) t;
						Flags outerFlags = getSignatureFlags(ct.def());
						return combineFlagsWithContainerFlags(sigFlags, outerFlags);
					}
				}
				return sigFlags;
			}

			private Flags combineFlagsWithContainerFlags(Flags sigFlags, Flags outerFlags) {
				if (outerFlags.isPrivate() || sigFlags.isPrivate())
					return Flags.PRIVATE;
				if (outerFlags.isPackage() || sigFlags.isPackage())
					return Flags.NONE;
				if (outerFlags.isProtected())
					return Flags.NONE;
				if (sigFlags.isProtected())
					return Flags.PROTECTED;
				return Flags.PUBLIC;
			}

			private <T extends Def> boolean hasSameScope(MemberInstance<T> mi, MemberDef signature) {
				Flags sigFlags = getSignatureFlags(signature);
				if (sigFlags.isPrivate()) {
					return true;
				}
				if (sigFlags.isPackage()) {
					if (mi.flags().isPublic())
						return true;
					if (mi.flags().isProtected() || mi.flags().isPackage()) {
						return hasSamePackage(mi.def(), signature);
					}
					return false;
				}
				if (sigFlags.isProtected()) {
					if (mi.flags().isPublic())
						return true;
					if (mi.flags().isProtected()) {
						return hasSameClass(mi.def(), signature);
					}
					return false;
				}
				if (sigFlags.isPublic()) {
					if (mi.flags().isPublic())
						return true;
					return false;
				}
				return false;
			}

			private ClassDef getClass(Def def) {
				if (def instanceof ClassDef) {
					return (ClassDef) def;
				}
				if (def instanceof MemberDef) {
					MemberDef md = (MemberDef) def;
					Type container = Types.get(md.container());
					if (container != null) {
						container = Types.baseType(container);
						if (container instanceof ClassType) {
							return ((ClassType) container).def();
						}
					}
				}
				return null;
			}

			private boolean hasSameClass(Def def, MemberDef accessor) {
				ClassDef c1 = getClass(def);
				ClassDef c2 = getClass(accessor);
				if (c1 == null || c2 == null)
					return false;
				return c1.equals(c2);
			}

			private boolean hasSamePackage(Def def, MemberDef accessor) {
				ClassDef c1 = getClass(def);
				ClassDef c2 = getClass(accessor);
				if (c1 == null || c2 == null)
					return false;
				Package p1 = Types.get(c1.package_());
				Package p2 = Types.get(c2.package_());
				if (p1 == null && p2 == null)
					return true;
				if (p1 == null || p2 == null)
					return false;
				return p1.equals(p2);
			}
		});

		if (ex[0] != null)
			Errors.issue(tc.job(), ex[0], mem);
	}


	/*protected void checkVariance(ContextVisitor tc) {
		if (methodDef().flags().isStatic())
			return;

		X10ClassDef cd = (X10ClassDef) tc.context().currentClassDef();
		final Map<Name,ParameterType.Variance> vars = CollectionFactory.newHashMap();
		for (int i = 0; i < cd.typeParameters().size(); i++) {
			ParameterType pt = cd.typeParameters().get(i);
			ParameterType.Variance v = cd.variances().get(i);
			vars.put(pt.name(), v);
		}

		try {
		    Checker.checkVariancesOfType(returnType.position(), returnType.type(), ParameterType.Variance.COVARIANT, "as a method return type", vars, tc);
		} catch (SemanticException e) {
		    Errors.issue(tc.job(), e, this);
		}
		for (Formal f : formals) {
			try {
			    Checker.checkVariancesOfType(f.type().position(), f.declType(), ParameterType.Variance.CONTRAVARIANT, "as a method parameter type", vars, tc);
			} catch (SemanticException e) {
			    Errors.issue(tc.job(), e, this);
			}
		}
	}*/

	public Node typeCheckOverride(Node parent, ContextVisitor tc) {
		X10MethodDecl nn = this;
		X10MethodDecl old = nn;

		TypeSystem xts = (TypeSystem) tc.typeSystem();

        // Step 0.  Process annotations.
        TypeChecker childtc = (TypeChecker) tc.enter(parent, nn);
        nn = (X10MethodDecl) X10Del_c.visitAnnotations(nn, childtc);

        // Do not infer types of native methods
        if (nn.returnType() instanceof UnknownTypeNode && ! X10FieldDecl_c.shouldInferType(nn, xts))
            Errors.issue(tc.job(), new Errors.CannotInferNativeMethodReturnType(position()));
        
		// Step I.a.  Check the formals.

		// First, record the final status of each of the type params and formals.
		List<TypeParamNode> processedTypeParams = nn.visitList(nn.typeParameters(), childtc);
		nn = (X10MethodDecl) nn.typeParameters(processedTypeParams);
		List<Formal> processedFormals = nn.visitList(nn.formals(), childtc);
		nn = (X10MethodDecl) nn.formals(processedFormals);

		// [NN]: Don't do this here, do it on lookup of the formal.  We don't want spurious self constraints in the signature.
		//            for (Formal n : processedFormals) {
		//        		Ref<Type> ref = (Ref<Type>) n.type().typeRef();
		//        		Type newType = ref.get();
		//        		
		//        		if (n.localDef().flags().isFinal()) {
		//            			XConstraint c = X10TypeMixin.xclause(newType);
		//            			if (c == null)
		//					c = new XConstraint_c();
		//				else
		//					c = c.copy();
		//            			try {
		//        				c.addSelfBinding(xts.xtypeTranslator().trans(n.localDef().asInstance()));
		//        			}
		//        			catch (XFailure e) {
		//        				throw new SemanticException(e.getMessage(), position());
		//        			}
		//            			newType = X10TypeMixin.xclause(newType, c);
		//        		}
		//        		
		//        		ref.update(newType);
		//            }

		// Step I.b.  Check the guard.
		if (nn.guard() != null) {
			ContextVisitor guardtc = childtc.context(childtc.context().pushDepType(Types.<Type>ref(xts.unknownType(nn.guard().position()))));
			DepParameterExpr processedWhere = (DepParameterExpr) nn.visitChild(nn.guard(), guardtc);
			nn = (X10MethodDecl) nn.guard(processedWhere);

			VarChecker ac = new VarChecker(childtc.job());
			ac = (VarChecker) ac.context(childtc.context());
			processedWhere.visit(ac);

			if (ac.error != null) {
				Errors.issue(ac.job(), ac.error, this);
			}

			// Now build the new formal arg list.
			// TODO: Add a marker to the TypeChecker which records
			// whether in fact it changed the type of any formal.
			List<Formal> formals = processedFormals;

			//List newFormals = new ArrayList(formals.size());
			X10ProcedureDef pi = (X10ProcedureDef) nn.memberDef();
			CConstraint c = pi.guard().get();
			try {
				if (c != null) {
					c = c.copy();

					for (Formal n : formals) {
						Ref<Type> ref = (Ref<Type>) n.type().typeRef();
						Type newType =  ref.get();

						// Fold the formal's constraint into the guard.
						XVar var = xts.xtypeTranslator().translate(n.localDef().asInstance());
						CConstraint dep = Types.xclause(newType);
						if (dep != null) {
							dep = dep.copy();
							dep = dep.substitute(var, c.self());
							/*
                            XPromise p = dep.intern(var);
                            dep = dep.substitute(p.term(), c.self());
							 */
							c.addIn(dep);
						}

						ref.update(newType);
					}
				}

				// reporter.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());

				// Fold this's constraint (the class invariant) into the guard.
				{
					Type t =  tc.context().currentClass();
					CConstraint dep = Types.xclause(t);
					if (c != null && dep != null) {
						XVar thisVar = methodDef().thisVar();
						if (thisVar != null)
							dep = dep.substitute(thisVar, c.self());
						//                                  dep = dep.copy();
						//                                  XPromise p = dep.intern(xts.xtypeTranslator().transThis(t));
						//                                  dep = dep.substitute(p.term(), c.self());
						c.addIn(dep);
					}
				}
			}
			catch (XFailure e) {
				tc.errorQueue().enqueue(ErrorInfo.SEMANTIC_ERROR, e.getMessage(), position());
				c = null;
			}

			// Check if the guard is consistent.
			if (c != null && ! c.consistent()) {
				Errors.issue(tc.job(),
						new Errors.DependentClauseIsInconsistent("method", guard),
						this);
				// FIXME: [IP] mark constraint clause as invalid
			}
		}

		
		List<TypeNode> processedThrowsTypes = nn.visitList(nn.throwsTypes(), childtc);
		nn = (X10MethodDecl) nn.throwsTypes(processedThrowsTypes);

	
		// Step II. Check the return type. 
		// Now visit the returntype to ensure that its depclause, if any is processed.
		// Visit the formals so that they get added to the scope .. the return type
		// may reference them.
		//TypeChecker tc1 = (TypeChecker) tc.copy();
		// childtc will have a "wrong" mi pushed in, but that doesnt matter.
		// we simply need to push in a non-null mi here.
		TypeChecker childtc1 = (TypeChecker) tc.enter(parent, nn);
		if (childtc1.context() == tc.context())
			childtc1 = (TypeChecker) childtc1.context((Context) tc.context().shallowCopy());
		// Add the type params and formals to the context.
		nn.visitList(nn.typeParameters(),childtc1);
		nn.visitList(nn.formals(),childtc1);
		
		Context childCxt = childtc1.context();
		addInClassInvariantIfNeeded(childCxt, true);
		childCxt.setVarWhoseTypeIsBeingElaborated(null);
		{ 
			final TypeNode r = (TypeNode) nn.visitChild(nn.returnType(), childtc1);
			nn = (X10MethodDecl) nn.returnType(r);
			Type type = PlaceChecker.ReplaceHereByPlaceTerm(r.type(), ( Context ) childtc1.context());
			((Ref<Type>) nn.methodDef().returnType()).update(r.type()); 

			if (hasType != null) {
				final TypeNode h = (TypeNode) nn.visitChild(((X10MethodDecl_c) nn).hasType, childtc1);
				Type hasType = PlaceChecker.ReplaceHereByPlaceTerm(h.type(), ( Context ) childtc1.context());
				nn = (X10MethodDecl) ((X10MethodDecl_c) nn).hasType(h);
				boolean checkSubType = true;
				try {
				    Types.checkMissingParameters(h);
				} catch (SemanticException e) {
				    Errors.issue(tc.job(), e, h);
				    checkSubType = false;
				}
				if (checkSubType && ! xts.isSubtype(type, hasType, tc.context())) {
					Errors.issue(tc.job(),
							new Errors.TypeIsNotASubtypeOfTypeBound(type, hasType, position()));
				}
			}
			// Check the offer type
			if (offerType != null) {
				final TypeNode o = (TypeNode) nn.visitChild(((X10MethodDecl_c) nn).offerType, childtc1);
				nn = (X10MethodDecl) ((X10MethodDecl_c) nn).offerType(o);		
				((X10MethodDef) nn.methodDef()).setOfferType(Types.ref(o.type()));
			} 
		}


		// reporter.report(1, "X10MethodDecl_c: typeoverride mi= " + nn.methodInstance());
		// Step III. Check the body. 
		// We must do it with the correct mi -- the return type will be
		// checked by return e; statements in the body, and the offerType by offer e; statements in the body.

		TypeChecker childtc2 = (TypeChecker) tc.enter(parent, nn); // this will push in the right mi.
		// Add the type params and formals to the context.
		nn.visitList(nn.typeParameters(),childtc2);
		nn.visitList(nn.formals(),childtc2);
		
		addInClassInvariantIfNeeded(childtc2.context(), true);
		//reporter.report(1, "X10MethodDecl_c: after visiting formals " + childtc2.context());
		// Now process the body.
		nn = (X10MethodDecl) nn.body((Block) nn.visitChild(nn.body(), childtc2));
		nn = (X10MethodDecl) childtc2.leave(parent, old, nn, childtc2);

		if (nn.returnType() instanceof UnknownTypeNode && X10FieldDecl_c.shouldInferType(nn, xts)) {
			NodeFactory nf = tc.nodeFactory();
			if (nn.body() == null) {
			    Errors.issue(tc.job(), new Errors.CannotInferMethodReturnType(nn.position()));
			    Position rtpos = nn.returnType().position();
			    nn = (X10MethodDecl_c) nn.returnType(nf.CanonicalTypeNode(rtpos, xts.unknownType(rtpos)));
			}

			// Body had no return statement.  Set to void.
			Type t;
			if (!xts.isUnknown(nn.returnType().typeRef().getCached())) {
				t = nn.returnType().typeRef().getCached();
			}
			else {
				t = xts.Void();
			}
			((Ref<Type>) nn.returnType().typeRef()).update(t);
			nn = (X10MethodDecl) nn.returnType(nf.CanonicalTypeNode(nn.returnType().position(), t));
		}		

		return nn;
	}

	private static final Collection<String> TOPICS = 
		CollectionUtil.list(Reporter.types, Reporter.context);

    /** Write the method to an output file. */
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        w.begin(0);
        for (Iterator<AnnotationNode> i = (((X10Ext) this.ext()).annotations()).iterator(); i.hasNext(); ) {
            AnnotationNode an = i.next();
            an.prettyPrint(w, tr);
            w.allowBreak(0, " ");
        }
        print(flags, w, tr);
        w.write("def " + name);
        if (!typeParameters.isEmpty()) {
            w.write("[");
            w.begin(0);
            for (Iterator<TypeParamNode> pi = typeParameters.iterator(); pi.hasNext(); ) {
                TypeParamNode pn = pi.next();
                print(pn, w, tr);
                if (pi.hasNext()) {
                    w.write(",");
                    w.allowBreak(0, " ");
                }
            }
            w.end();
            w.write("]");
        }
        w.write("(");
    
        w.allowBreak(2, 2, "", 0);
        w.begin(0);
    
        for (Iterator<Formal> i = formals.iterator(); i.hasNext(); ) {
            Formal f = i.next();
            
            print(f, w, tr);
    
            if (i.hasNext()) {
            w.write(",");
            w.allowBreak(0, " ");
            }
        }
    
        w.end();
        w.write("):");
        w.allowBreak(2, 2, "", 1);
        print(returnType, w, tr);
    
        w.end();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(flags.flags().translate());
        sb.append(returnType).append(" ");
        sb.append(name);
        if (typeParameters != null && !typeParameters.isEmpty())
            sb.append(typeParameters);
        sb.append("(...)");
        return sb.toString();
    }
}
