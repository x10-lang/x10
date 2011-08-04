/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Term_c;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.extension.X10Del_c;
import polyglot.ext.x10.types.AnnotatedType;
import polyglot.ext.x10.types.ConstrainedType;
import polyglot.ext.x10.types.MacroType;
import polyglot.ext.x10.types.TypeDef;
import polyglot.ext.x10.types.TypeDef_c;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LazyRef;
import polyglot.types.LocalDef;
import polyglot.types.MemberDef;
import polyglot.types.Named;
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
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XRoot;

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
		FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
		List<TypeParamNode> typeParams = this.visitList(this.typeParams, v);
		List<Formal> formals = this.visitList(this.formals, v);
		DepParameterExpr guard = (DepParameterExpr) this.visitChild(this.guard, v);
		TypeNode type = (TypeNode) this.visitChild(this.type, v);
		return reconstruct(flags, id, typeParams, formals, guard, type);
	}
	/** Visit the children of the method. */
	public Node visitSignature(NodeVisitor v) {
		Id id = (Id) this.visitChild(this.name, v);
		FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
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
	    return c.pushCode(typeDef);
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
	        }

	        return super.enterChildScope(child, c);
	}

	@Override
	public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
		final X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();
		X10NodeFactory nf = (X10NodeFactory) tb.nodeFactory();
		
		X10ClassDef ct = (X10ClassDef) tb.currentClass();
		Package package_ = tb.currentPackage();
		
		boolean local = tb.inCode();
		boolean topLevel = ct == null;
		
		final boolean ALLOW_TOP_LEVEL_TYPEDEFS = false;
		
		if (! local && ct == null && ! ALLOW_TOP_LEVEL_TYPEDEFS) {
		    throw new SemanticException("Type definitions must be static class or interface members.  This is a limitation of the current implementation.", position());
		}

		if (ALLOW_TOP_LEVEL_TYPEDEFS) {
		    // If this is a top-level typedef, add it to a dummy class for the package.
		    // When looking up types, we'll look for the package class then walk through the members.
		    QName dummyClass = QName.make(package_ != null ? package_.fullName() : null, X10TypeSystem.DUMMY_PACKAGE_CLASS_NAME);

		    if (ct == null) {
		    	Named n = ts.systemResolver().check(dummyClass);
		    	if (n instanceof X10ParsedClassType) {
		    		ct = ((X10ParsedClassType) n).x10Def();
		    	}
		    }

		    if (ct == null) {
			ct = (X10ClassDef) ts.createClassDef();
			ct.kind(ClassDef.TOP_LEVEL);
			ct.setPackage(package_ != null ? Types.ref(package_) : null);
			ct.name(X10TypeSystem.DUMMY_PACKAGE_CLASS_NAME);
			ct.superType(Types.ref(ts.Object()));
			ct.flags(Flags.PUBLIC.Abstract());
			ts.systemResolver().install(dummyClass, ct.asType());
		    }
		}
		
		TypeDef typeDef;

		XRoot thisVar = ct != null ? ct.thisVar() : null;
		
		if (local) {
		    typeDef = new TypeDef_c(ts, position(), Flags.NONE, name.id(), null,
		                            Collections.EMPTY_LIST,
		                            thisVar,
		                            Collections.EMPTY_LIST, Collections.EMPTY_LIST, null, null, null);
		}
		else {		
		    if (ct == null)
		        throw new SemanticException("Could not find enclosing class or package for type definition \"" + name.id() + "\".", position());

		    typeDef = new TypeDef_c(ts, position(), topLevel ? flags.flags().Static() : flags.flags(), name.id(), Types.ref(ct.asType()),
		                                                     Collections.EMPTY_LIST,
		                                                     thisVar,
		                                                     Collections.EMPTY_LIST, Collections.EMPTY_LIST, null, null, null);
		    ct.addMemberType(typeDef);
		}
		
		typeDef.setPackage(package_ != null ? Types.ref(package_) : null);

	        TypeDecl_c n = (TypeDecl_c) copy();
	        TypeBuilder tb2 = tb.pushDef(typeDef);
		n = (TypeDecl_c) n.visitSignature(tb2);
		
	        n = (TypeDecl_c) X10Del_c.visitAnnotations(n, tb2);

	        List<Ref<? extends Type>> typeParameters = new ArrayList<Ref<? extends Type>>();
	        for (TypeParamNode tpn : n.typeParameters()) {
	        	typeParameters.add(Types.ref(tpn.type()));
	        }
	        typeDef.setTypeParameters(typeParameters);
	        
	        List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>();
	        List<LocalDef> formalNames = new ArrayList<LocalDef>();
	        for (Formal f : n.formals()) {
	            final Formal f2 = f;
	            final LazyRef<XConstraint> cref = Types.<XConstraint>lazyRef(new XConstraint_c());
	            Type t = X10TypeMixin.xclause(f.type().typeRef(), cref);
	            cref.setResolver(new Runnable() {
	        	public void run() {
	        	    XConstraint c = new XConstraint_c();
	        	    try {
	        		c.addSelfBinding(ts.xtypeTranslator().trans(f2.localDef().asInstance()));
	        	    }
	        	    catch (XFailure e) {
	        	    }
	        	    catch (SemanticException e) {
	        	    }
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
	        
	        // Add to the system resolver if the type def takes no arguments.
	        // Otherwise, we'll search through the container.
	        if (! local && ct.asType().isGloballyAccessible() && formalTypes.size() == 0 && typeParameters.size() == 0) {
	        	if (ALLOW_TOP_LEVEL_TYPEDEFS) {
	        		if (ct.name().equals(X10TypeSystem.DUMMY_PACKAGE_CLASS_NAME) && ct.package_() != null)
	        			ts.systemResolver().install(QName.make(ct.package_().get().fullName(), name.id()), typeDef.asType());
	        		else if (ct.name().equals(X10TypeSystem.DUMMY_PACKAGE_CLASS_NAME) && ct.package_() == null)
	        			ts.systemResolver().install(QName.make(null, name.id()), typeDef.asType());
	        	}
	            
		    ts.systemResolver().install(QName.make(ct.fullName(), name.id()), typeDef.asType());
		}
		
	        return n;
	}
	
	@Override
	public Node typeCheck(ContextVisitor tc) throws SemanticException {
	    checkCycles(type.type());
	    X10MethodDecl_c.dupFormalCheck(typeParams, formals);
	    return this;
	}

	private void checkCycles(Type type) throws SemanticException {
	    if (type instanceof MacroType) {
		MacroType mt = (MacroType) type;
		if (mt.def() == typeDef) {
		    throw new SemanticException("Recursive type definition; type definition depends on itself.", position());
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
	    if (type instanceof AnnotatedType) {
		AnnotatedType ct = (AnnotatedType) type;
		checkCycles(ct.baseType());
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

	public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
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