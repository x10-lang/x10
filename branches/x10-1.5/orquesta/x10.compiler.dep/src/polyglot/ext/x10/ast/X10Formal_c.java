/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 23, 2005
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Formal_c;
import polyglot.ast.Id;
import polyglot.ast.Id_c;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10LocalDef;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.visit.X10PrettyPrinterVisitor;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/**
 * An immutable representation of an X10Formal, which is of the form
 *   Flag Type VarDeclaratorId
 * Recall that a VarDeclaratorId may have additional variable bindings.
 * @author vj Jan 23, 2005
 * @author igor Jan 13, 2006
 */
public class X10Formal_c extends Formal_c implements X10Formal {
	/* Invariant: vars != null */
	protected List<Formal> vars;
	boolean unnamed;

	public X10Formal_c(Position pos, Flags flags, TypeNode type,
	                   Id name, List<Formal> vars)
	{
		super(pos, flags, type,
				name == null ? new Id_c(pos, X10PrettyPrinterVisitor.getId()) : name);
		if (vars == null) vars = Collections.EMPTY_LIST;
		this.vars = TypedList.copyAndCheck(vars, Formal.class, true);
		this.unnamed = name == null;
		assert vars != null;
	}
	
	public Node visitChildren(NodeVisitor v) {
		X10Formal_c n = (X10Formal_c) super.visitChildren(v);
		List l = visitList(vars, v);
		if (! CollectionUtil.equals(l, this.vars)) {
			if (n == this) n = (X10Formal_c) copy();
			n.vars = TypedList.copyAndCheck(l, Formal.class, true);
		}
		return n;
	}
	
	public List<Formal> vars() {
		return vars;
	}
	
	public X10Formal vars(List<Formal> vars) {
	    X10Formal_c n = (X10Formal_c) super.copy();
	    n.vars = vars;
	    return n;
	}

	public boolean isUnnamed() {
		return unnamed;
	}

	int positionInArgList = -1;
	public void setPositionInArgList(int i) {
		positionInArgList = i;
	}
	public int positionInArgList() {
		return positionInArgList;
	}
	/** Get the local instances of the bound variables. */
	public LocalDef[] localInstances() {
	    LocalDef[] lis = new LocalDef[vars.size()];
		for (int i = 0; i < vars.size(); i++) {
			lis[i] = vars.get(i).localDef();
		}
		return lis;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.jl.ast.Formal#addDecls()
	 */
	public void addDecls(Context c) {
		super.addDecls(c);
		for (Iterator<Formal> j = this.vars().iterator(); j.hasNext(); ) {
			Formal fj = (Formal) j.next();
			fj.addDecls(c);
		}
	}

	// TODO: vj -- implement exploded variables appearing in depclause of return
	// type of method.
	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10Formal_c n = (X10Formal_c) super.buildTypes(tb);
		
		// Set the position of this formal in its LocalInstance.
		// This can be retrieved by X10Call_c and used to replace the
		// type of the formal by the type of the actual when computing
		// the actual return type of the method.
		((X10LocalDef)n.localDef()).setPositionInArgList(positionInArgList());
		
		return n;
	}

//	 public Node typeCheck(TypeChecker tc) throws SemanticException {
//			X10Formal_c result= (X10Formal_c) super.typeCheck(tc);
//			// Ensure that the LocalInstance is updated with the 
//			// possibly new type (w/ depclause)
//			X10LocalInstance li = (X10LocalInstance) result.li;
//			final X10Type declType= (X10Type) declType();
//			li.setType(declType);
//			boolean changed = li.setSelfClauseIfFinal();
//			if (changed) {
//				result = (X10Formal_c) result.type(result.type().type(li.type()));
//			}
//			return result;
//		
//	 }

	 private String translateVars() {
		StringBuffer sb = new StringBuffer();
		if (! vars.isEmpty()) {
			sb.append("[");
			for (int i = 0; i < vars.size(); i++)
				sb.append(i > 0 ? "," : "").append(vars.get(i).id().id());
			sb.append("]");
		}
		return sb.toString();
	}

    public String toString() {
        return super.toString() + translateVars();
    }

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Formal#hasExplodedVars()
	 */
	public boolean hasExplodedVars() {
		return ! vars.isEmpty();
	}

	/**
	 * Create a local variable declaration for an exploded var,
	 * at the given type, name and with the given initializer.
	 * The exploded variable is implicitly final.
	 *
	 * @param nf
	 * @param pos
	 * @param type
	 * @param name
	 * @param li
	 * @param init
	 * @return
	 */
	protected static LocalDecl makeLocalDecl(NodeFactory nf, Position pos,
											 Flags flags, TypeNode type,
											 Id name, LocalDef li,
											 Expr init)
	{
		/* boolean allCapitals = name.equals(name.id().toUpperCase());
		// vj: disable until we have more support for declarative programming in X10.
		Flags f = (false || allCapitals ? flags.set(Flags.FINAL) : flags);
		 */
		return nf.LocalDecl(pos, flags.set(Flags.FINAL), type, name, init)
					.localDef(li);
	}

	/**
	 * Return the initialization statements for the exploding variables.
	 *
	 * @param nf
	 * @param ts
	 * @return
	 */
	public List<Stmt> explode(NodeFactory nf, TypeSystem ts) {
		return explode(nf, ts, id(), position(), flags(), vars, localDef());
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Formal#explode(polyglot.ast.NodeFactory, polyglot.types.TypeSystem)
	 */
	public List<Stmt> explode(NodeFactory nf, TypeSystem ts, Stmt s) {
		List<Stmt> init = this.explode(nf, ts);
		if (s != null)
			init.add(s);
		return init;
	}

	
	public List<Stmt> explode(NodeFactory nf, TypeSystem ts, List<Stmt> s, boolean prepend) {
		List<Stmt> init = this.explode(nf, ts);
		if (s != null) {
			if (prepend) init.addAll(s);
			else init.addAll(0, s);
		}
		return init;
	}

	/**
	 * Return the initialization statements for the exploding variables.
	 *
	 * @param nf
	 * @param ts
	 * @param name
	 * @param pos
	 * @param flags
	 * @param vars
	 * @param lis
	 * @return
	 */
	private static List<Stmt> explode(NodeFactory nf, TypeSystem ts,
										  Id name, Position pos,
										  Flags flags, List<Formal> vars,
										  LocalDef bli)
	{
		if (vars == null || vars.isEmpty()) return null;
		X10NodeFactory x10nf = (X10NodeFactory) nf;
		List<Stmt> stmts = new TypedList(new ArrayList(vars.size()), Stmt.class, false);
		Expr arrayBase =
			(bli == null) ? nf.AmbExpr(pos, name)
						  : (Expr) nf.Local(pos, name).localInstance(bli.asInstance()).type(bli.asInstance().type());
		TypeNode intType = x10nf.CanonicalTypeNode(pos, ts.Int());
		for (int i = 0; i < vars.size(); i++) {
			// int arglist(i) = name[i];
			Formal var = vars.get(i);
			Expr index = x10nf.IntLit(var.position(), IntLit.INT, i).type(ts.Int());
			Expr init = x10nf.X10ArrayAccess1(var.position(), arrayBase, index).type(ts.Int());
			LocalDef li = var.localDef();
			Stmt d = makeLocalDecl(nf, var.position(), flags, intType, var.id(), li, init);
			stmts.add(d);
		}
		return stmts;
	}
//	public X10Formal_c pickUpTypeFromTypeNode(TypeChecker tc) {
//		X10LocalInstance xli = (X10LocalInstance) li;
//		X10Type newType = (X10Type) type.type();
//		xli.setType(newType);
//		xli.setSelfClauseIfFinal();
//		return  (X10Formal_c) type(type().type(xli.type()));
//	}
	
	public Context enterChildScope(Node child, Context c) {
		X10Context cxt = (X10Context) c;
		if (child == this.type) {
			TypeSystem ts = c.typeSystem();
			LocalDef li = localDef();
			cxt.addVariable(li.asInstance());
			cxt.setVarWhoseTypeIsBeingElaborated(li);
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}
	/**
	 * Return the initialization statements for the exploding variables
	 * early.
	 *
	 * @param nf
	 * @param ts
	 * @param name
	 * @param pos
	 * @param flags
	 * @param vars
	 * @return
	 */
	public static List/*<Stmt>*/ explode(NodeFactory nf, TypeSystem ts,
										 Id name, Position pos,
										 Flags flags, List<Formal> vars)
	{
		return explode(nf, ts, name, pos, flags, vars, null);
	}
}

