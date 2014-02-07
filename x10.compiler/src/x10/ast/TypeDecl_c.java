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
import java.util.List;

import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Term;
import polyglot.ast.Term_c;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.Package;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;

import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.errors.Errors;
import x10.extension.X10Del_c;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.TypeDef;
import x10.types.TypeDef_c;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ParsedClassType;
import polyglot.types.TypeSystem;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.types.constraints.TypeConstraint;

public class TypeDecl_c extends Term_c implements TypeDecl {
	private TypeNode type;
	private DepParameterExpr guard;
	private List<Formal> formals;
	private List<TypeParamNode> typeParams;
	private Id name;
	private FlagsNode flags;
	
	private TypeDef typeDef;

	public TypeDecl_c(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<Formal> formals, DepParameterExpr guard, TypeNode type) {
		super(pos);
		this.flags = flags;
		this.name = name;
		this.typeParams = TypedList.copyAndCheck(typeParameters, TypeParamNode.class, true);
		this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
		this.guard = guard;
		this.type = type;
	}

	public Id name() {
		return this.name;
	}

	public TypeDecl name(Id id) {
		TypeDecl_c n = (TypeDecl_c) copy();
		n.name = id;
		return n;
	}

	public FlagsNode flags() {
		return this.flags;
	}

	public TypeDecl flags(FlagsNode flags) {
		TypeDecl_c n = (TypeDecl_c) copy();
		n.flags = flags;
		return n;
	}

	public TypeNode type() {
		return this.type;
	}

	public TypeDecl type(TypeNode type) {
		TypeDecl_c n = (TypeDecl_c) copy();
		n.type = type;
		return n;
	}

	public List<TypeParamNode> typeParameters() {
		return Collections.<TypeParamNode> unmodifiableList(this.typeParams);
	}

	public TypeDecl typeParameters(List<TypeParamNode> typeParams) {
		TypeDecl_c n = (TypeDecl_c) copy();
		n.typeParams = TypedList.copyAndCheck(typeParams, TypeParamNode.class, true);
		return n;
	}

	public List<Formal> formals() {
		return Collections.<Formal> unmodifiableList(this.formals);
	}

	public TypeDecl formals(List<Formal> formals) {
		TypeDecl_c n = (TypeDecl_c) copy();
		n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
		return n;
	}

	public DepParameterExpr guard() {
		return guard;
	}

	public TypeDecl guard(DepParameterExpr guard) {
		TypeDecl_c n = (TypeDecl_c) copy();
		n.guard = guard;
		return n;
	}

	/** Visit the children of the method. */
	public Node visitChildren(NodeVisitor v) {
		Id id = (Id) this.visitChild(this.name, v);
		FlagsNode flags = (FlagsNode) this.visitChild(this.flags(), v);
		List<TypeParamNode> typeParams = this.visitList(this.typeParams, v);
		List<Formal> formals = this.visitList(this.formals, v);
		DepParameterExpr guard = (DepParameterExpr) this.visitChild(this.guard, v);
		TypeNode type = (TypeNode) this.visitChild(this.type, v);
		return reconstruct(flags, id, typeParams, formals, guard, type);
	}
	/** Visit the children of the method. */
	public Node visitSignature(NodeVisitor v) {
		Id id = (Id) this.visitChild(this.name, v);
		FlagsNode flags = (FlagsNode) this.visitChild(this.flags(), v);
		List<TypeParamNode> typeParams = this.visitList(this.typeParams, v);
		List<Formal> formals = this.visitList(this.formals, v);
		DepParameterExpr guard = (DepParameterExpr) this.visitChild(this.guard, v);
		return reconstruct(flags, id, typeParams, formals, guard, this.type);
	}

	protected Node reconstruct(FlagsNode flags, Id name, List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr guard, TypeNode type) {
		TypeDecl_c n = this;
		n = (TypeDecl_c) n.flags(flags);
		n = (TypeDecl_c) n.name(name);
		n = (TypeDecl_c) n.typeParameters(typeParams);
		n = (TypeDecl_c) n.formals(formals);
		n = (TypeDecl_c) n.guard(guard);
		n = (TypeDecl_c) n.type(type);
		return n;
	}
	
	public Context enterScope(Context c) {
	    c = c.pushCode(typeDef);
	    if (!c.inStaticContext() && typeDef.thisDef() != null)
	        c.addVariable(typeDef.thisDef().asInstance());
	    return c;
	}

	public Context enterChildScope(Node child, Context c) {
	    if (child != type) {
	        // Push formals so they're in scope in the types of the other formals.
	        c = c.pushBlock();
	        for (TypeParamNode f : typeParams) {
	            f.addDecls(c);
	        }
	        for (Formal f : formals) {
	            f.addDecls(c);
	        }
	    } else {
	    	if (guard != null) {
		    	// add the guard when descending into the RHS of the typedef

		        Ref<CConstraint> vc = guard.valueConstraint();
				Ref<TypeConstraint> tc = guard.typeConstraint();
	
				if (vc != null || tc != null) {
			        c = c.pushBlock();
					c.setName(" Typedef guard for |" + name() + "| ");
					if (vc != null)
						c.addConstraint(vc);
					if (tc != null) {
						c.setTypeConstraintWithContextTerms(tc);
					}
				}
	    	}
	    }

	    return super.enterChildScope(child, c);
	}

	private static final boolean ALLOW_TOP_LEVEL_TYPEDEFS = true;
    
	@Override
	public Node buildTypesOverride(TypeBuilder tb) {
		final TypeSystem ts = (TypeSystem) tb.typeSystem();
		NodeFactory nf = (NodeFactory) tb.nodeFactory();
		
		X10ClassDef ct = (X10ClassDef) tb.currentClass();
		Package package_ = tb.currentPackage();
		
		boolean local = tb.inCode();
		boolean topLevel = !local && ct == null;
		
		if (topLevel && !ALLOW_TOP_LEVEL_TYPEDEFS) {
		    Errors.issue(tb.job(),
		                 new Errors.TypeDefinitionMustBeStaticClassOrInterfaceMembers(position()));
		}

		// FIXME: also check if the current method is static
		XVar thisVar = ct == null ? null : ct.thisVar();
		Ref<X10ClassType> container = ct == null ? null : Types.ref(ct.asType());
		Flags flags = local ? Flags.NONE : this.flags().flags();
		if (topLevel)
		    flags = flags.Static();

		TypeDef typeDef = new TypeDef_c(ts, position(), name().position(), flags, name.id(), container,
		                                Collections.<ParameterType>emptyList(),
		                                thisVar, Collections.<LocalDef>emptyList(),
		                                Collections.<Ref<? extends Type>>emptyList(), null, null, null);
		if (!local && ct != null) {
		    ct.addMemberType(typeDef);
		}
		
		typeDef.setPackage(package_ != null ? Types.ref(package_) : null);

		TypeDecl_c n = (TypeDecl_c) copy();
		TypeBuilder tb2 = tb.pushDef(typeDef);
		n = (TypeDecl_c) n.visitSignature(tb2);
		
		n = (TypeDecl_c) X10Del_c.visitAnnotations(n, tb2);

		List<ParameterType> typeParameters = new ArrayList<ParameterType>();
		for (TypeParamNode tpn : n.typeParameters()) {
			typeParameters.add(tpn.type());
		}
		typeDef.setTypeParameters(typeParameters);
	        
		List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>();
		List<LocalDef> formalNames = new ArrayList<LocalDef>();
		for (Formal f : n.formals()) {
		    final Formal f2 = f;
		    final LazyRef<CConstraint> cref = Types.<CConstraint>lazyRef(ConstraintManager.getConstraintSystem().makeCConstraint());
		    ConstrainedType t = ConstrainedType.xclause(f.type().typeRef(), cref);
		    cref.setResolver(new Runnable() {
		        public void run() {
		            CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
		            c.addSelfBinding(ts.xtypeTranslator().translate(f2.localDef().asInstance()));
		            cref.update(c);
		        }
		    });
		    formalTypes.add(f.type().typeRef());
		    formalNames.add(f.localDef());
		}
		typeDef.setFormalTypes(formalTypes);
		typeDef.setFormalNames(formalNames);

		if (n.guard != null) {
		    typeDef.setGuard(n.guard.valueConstraint());
		    typeDef.setTypeGuard(n.guard.typeConstraint());
		}

		if (n.type != null) {
		    TypeNode tn = (TypeNode) n.visitChild(n.type, tb2);
		    n = (TypeDecl_c) n.type(tn);
		    typeDef.setType(tn.typeRef());
		}

		n = (TypeDecl_c) n.typeDef(typeDef);

		// Add to the system resolver.
		if (!local && typeDef.asType().isGloballyAccessible()) {
		    if (ct == null) {
		        if (ALLOW_TOP_LEVEL_TYPEDEFS) {
		            QName pkgName = typeDef.package_() == null ? null : typeDef.package_().get().fullName();
		            ts.systemResolver().install(QName.make(pkgName, name.id()), typeDef.asType());
		        }
		    } else {
		        ts.systemResolver().install(QName.make(ct.fullName(), name.id()), typeDef.asType());
		    }
		}

		return n;
	}
	
	@Override
	public Node typeCheck(ContextVisitor tc) {
	    try {
	        checkCycles(type.type());
	    } catch (SemanticException z) {
	        Errors.issue(tc.job(), z, this);
	    }
	    try {
	        X10MethodDecl_c.dupFormalCheck(typeParams, formals);
	    } catch (SemanticException z) {
	        Errors.issue(tc.job(), z, this);
	    }
	    try {
	        Types.checkMissingParameters(type);
	    } catch (SemanticException e) {
	        Errors.issue(tc.job(), e, type);
	    }
	    return this;
	}

	private void checkCycles(Type type) throws SemanticException {
	    if (type instanceof MacroType) {
		MacroType mt = (MacroType) type;
		if (mt.def() == typeDef) {
		    throw new Errors.RecursiveTypeDefinition(position());
		}
	    }
	    if (type instanceof ConstrainedType) {
		ConstrainedType ct = (ConstrainedType) type;
		checkCycles(ct.baseType().get());
	    }
	    if (type instanceof ClassType) {
		ClassType ct = (ClassType) type;
		checkCycles(ct.superClass());
		for (Type t : ct.interfaces())
		    checkCycles(t);
	    }
	}

	public TypeDef typeDef() { return typeDef; }
	public TypeDecl typeDef(TypeDef typeDef) {
		TypeDecl_c n = (TypeDecl_c) copy();
		n.typeDef = typeDef;
		return n;
	}

	public Term firstChild() {
		return listChild(typeParameters(), listChild(formals(), type));
	}

	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		List<Term> children = new ArrayList<Term>();
		children.addAll(typeParams);
		children.addAll(formals);
		
		v.visitCFGList(children, type(), ENTRY);
		v.visitCFG(type(), this, EXIT);

		return succs;
	}

	public MemberDef memberDef() {
		return typeDef;
	}
	
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(flags().flags().translate());
	    sb.append("type ");
	    sb.append(name());
	    if (typeParameters().size() > 0) {
		String sep = "";
		sb.append("[");
		for (TypeParamNode f : typeParameters()) {
		    sb.append(sep);
		    sep = ",";
		    sb.append(f);
		}
		sb.append("]");
	    }
	    if (formals().size() > 0) {
		String sep = "";
		sb.append("(");
		for (Formal f : formals()) {
		    sb.append(sep);
		    sep = ",";
		    sb.append(f);
		}
		sb.append(")");
	    }
	    if (guard() != null) {
	        sb.append(guard());
	    }
	    sb.append(" = ");
	    sb.append(type());
	    sb.append(";");
	    return sb.toString();
	}
}
