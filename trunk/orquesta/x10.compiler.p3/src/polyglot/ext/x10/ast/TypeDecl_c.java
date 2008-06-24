/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.FlagsNode;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.Term;
import polyglot.ast.Term_c;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.TypeDef;
import polyglot.ext.x10.types.TypeDef_c;
import polyglot.ext.x10.types.TypeProperty_c;
import polyglot.ext.x10.types.X10FieldDef;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassDef;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.MemberDef;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XSelf;

public class TypeDecl_c extends Term_c implements TypeDecl {
	private TypeNode type;
	private DepParameterExpr whereClause;
	private List<Formal> formals;
	private List<TypeParamNode> typeParams;
	private Id id;
	private FlagsNode flags;
	
	private TypeDef typeDef;

	public TypeDecl_c(Position pos, FlagsNode flags, Id name, List<TypeParamNode> typeParameters, List<Formal> formals, DepParameterExpr where, TypeNode type) {
		super(pos);
		this.flags = flags;
		this.id = name;
		this.typeParams = TypedList.copyAndCheck(typeParameters, TypeParamNode.class, true);
		this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
		this.whereClause = where;
		this.type = type;
	}

	public Id id() {
		return this.id;
	}

	public TypeDecl id(Id id) {
		TypeDecl_c n = (TypeDecl_c) copy();
		n.id = id;
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

	public DepParameterExpr whereClause() {
		return whereClause;
	}

	public TypeDecl whereClause(DepParameterExpr where) {
		TypeDecl_c n = (TypeDecl_c) copy();
		this.whereClause = where;
		return n;
	}

	/** Visit the children of the method. */
	public Node visitChildren(NodeVisitor v) {
		Id id = (Id) this.visitChild(this.id, v);
		FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
		List<TypeParamNode> typeParams = this.visitList(this.typeParams, v);
		List<Formal> formals = this.visitList(this.formals, v);
		DepParameterExpr where = (DepParameterExpr) this.visitChild(this.whereClause, v);
		TypeNode type = (TypeNode) this.visitChild(this.type, v);
		return reconstruct(flags, id, typeParams, formals, where, type);
	}
	/** Visit the children of the method. */
	public Node visitSignature(NodeVisitor v) {
		Id id = (Id) this.visitChild(this.id, v);
		FlagsNode flags = (FlagsNode) this.visitChild(this.flags, v);
		List<TypeParamNode> typeParams = this.visitList(this.typeParams, v);
		List<Formal> formals = this.visitList(this.formals, v);
		DepParameterExpr where = (DepParameterExpr) this.visitChild(this.whereClause, v);
		return reconstruct(flags, id, typeParams, formals, where, this.type);
	}

	protected Node reconstruct(FlagsNode flags, Id name, List<TypeParamNode> typeParams, List<Formal> formals, DepParameterExpr where, TypeNode type) {
		TypeDecl_c n = this;
		n = (TypeDecl_c) n.flags(flags);
		n = (TypeDecl_c) n.id(name);
		n = (TypeDecl_c) n.typeParameters(typeParams);
		n = (TypeDecl_c) n.formals(formals);
		n = (TypeDecl_c) n.whereClause(where);
		n = (TypeDecl_c) n.type(type);
		return n;
	}

	@Override
	public Node buildTypesOverride(TypeBuilder tb) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tb.typeSystem();

		ClassDef ct = tb.currentClass();

		TypeDef typeDef = new TypeDef_c(ts, position(), flags.flags(), id.id(), ct != null ? Types.ref(ct.asType()) : null,
		                                                                           Collections.EMPTY_LIST,
		                                                                           Collections.EMPTY_LIST,
		                                                                           Collections.EMPTY_LIST, null, null);

	        TypeDecl_c n = (TypeDecl_c) copy();
	        n = (TypeDecl_c) n.visitSignature(tb);

	        List<Ref<? extends Type>> typeParameters = new ArrayList<Ref<? extends Type>>();
	        for (TypeParamNode tpn : n.typeParameters()) {
	        	typeParameters.add(tpn.typeRef());
	        }
	        typeDef.setTypeParameters(typeParameters);
	        
	        List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>();
	        List<String> formalNames = new ArrayList<String>();
	        for (Formal f : n.formals()) {
	        	XConstraint c = new XConstraint_c();
	        	try {
				c = c.addBinding(XSelf.Self, ts.xtypeTranslator().trans(f.localDef().asInstance()));
			}
			catch (XFailure e) {
			}
	        	Type t = X10TypeMixin.xclause(f.type().type(), c);
	        	formalTypes.add(f.type().typeRef());
	        	formalNames.add(f.id().id());
	        }
	        typeDef.setFormalTypes(formalTypes);
	        typeDef.setFormalNames(formalNames);
	        
	        if (n.whereClause != null)
	        	typeDef.setWhereClause(n.whereClause.xconstraint());

	        if (n.type != null) {
	        	TypeNode tn = (TypeNode) n.visitChild(n.type, tb);
	        	n = (TypeDecl_c) n.type(tn);
	        	typeDef.setType(tn.typeRef());
	        }

	        n = (TypeDecl_c) n.typeDef(typeDef);
	        
	        return n;
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

	public String name() {
		return id.id();
	}

	public MemberDef memberDef() {
		return typeDef;
	}

}
