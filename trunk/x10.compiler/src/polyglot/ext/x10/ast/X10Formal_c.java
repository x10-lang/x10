/*
 * Created by vj on Jan 23, 2005
 */
package polyglot.ext.x10.ast;

import java.util.List;
import java.util.ArrayList;

import polyglot.ast.AmbExpr;
import polyglot.ast.Expr;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ast.Stmt;
import polyglot.ext.jl.ast.Formal_c;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;

import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.X10TypeSystem;


/**
 * An immutable representation of an X10Formal, which is of the form
 *   Flag Type VarDeclaratorId
 * Recall that a VarDeclaratorId may have additional variable bindings.
 * @author vj Jan 23, 2005
 * @author igor Jan 13, 2006
 */
public class X10Formal_c extends Formal_c implements X10Formal {
	public static final AmbExpr[] NO_VARS = new AmbExpr[0];
	public static final LocalInstance[] NO_LOCALS = new LocalInstance[0];

	/* Invariant: vars != null */
	protected AmbExpr[] vars;
	/* Invariant after disambiguation:
	     lis != null && lis.length == vars.length */
    protected LocalInstance[] lis;

	public X10Formal_c(Position pos, Flags flags, TypeNode type,
	                   String name, AmbExpr[] vars)
	{
		super(pos, flags, type, name);
		this.vars = (vars == null) ? NO_VARS : vars;
	}

	public boolean isDisambiguated() {
		return lis != null && super.isDisambiguated();
	}

	/** Get the local instances of the bound variables. */
	public LocalInstance[] localInstances() {
		return lis;
	}

	/** Set the local instances of the bound variables. */
	public X10Formal localInstances(LocalInstance[] lis) {
		if (this.lis == lis) return this;
		X10Formal_c n = (X10Formal_c) copy();
		n.lis = lis;
		return n;
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.jl.ast.Formal#addDecls()
	 */
	public void addDecls(Context c) {
		super.addDecls(c);
		//for (int i = 0; i < lis.length; i++)
		//	c.addVariable(lis[i]);
	}

	public Node buildTypes(TypeBuilder tb) throws SemanticException {
		X10Formal_c n = (X10Formal_c) super.buildTypes(tb);
		TypeSystem ts = tb.typeSystem();
		if (vars == NO_VARS)
			return n.localInstances(NO_LOCALS);
		LocalInstance[] lis = new LocalInstance[vars.length];
		for (int i = 0; i < lis.length; i++) {
			AmbExpr v = vars[i];
			lis[i] = ts.localInstance(v.position(), flags(), ts.Int(), v.name());
		}
		return n.localInstances(lis);
	}

	public void dump(CodeWriter w) {
		super.dump(w);
		if (vars != NO_VARS) {
			w.write("(vars [");
			for (int i = 0; i < vars.length; i++) {
				if (lis != null) {
					w.allowBreak(4, " ");
					w.begin(0);
					w.write("(instance " + lis[i] + ")");
					w.end();
				}
				w.allowBreak(4, " ");
				w.begin(0);
				w.write("(name " + vars[i] + ")");
				w.end();
			}
			w.write("])");
		}
	}

	private String translateVars() {
		StringBuffer sb = new StringBuffer();
		if (vars != NO_VARS) {
			sb.append("[");
			for (int i = 0; i < vars.length; i++)
				sb.append(vars[i].name()).append(i > 0 ? "," : "");
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
		return vars != NO_VARS;
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
											 String name, LocalInstance li,
											 Expr init)
	{
		/* boolean allCapitals = name.equals(name.toUpperCase());
		// vj: disable until we have more support for declarative programming in X10.
		Flags f = (false || allCapitals ? flags.set(Flags.FINAL) : flags);
		 */
		return nf.LocalDecl(pos, flags.set(Flags.FINAL), type, name, init)
					.localInstance(li);
	}

	/**
	 * Return the initialization statements for the exploding variables.
	 *
	 * @param nf
	 * @param ts
	 * @return
	 */
	public List/*<Stmt>*/ explode(NodeFactory nf, TypeSystem ts) {
		return explode(nf, ts, name(), position(), flags(), vars, lis);
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.x10.ast.X10Formal#explode(polyglot.ast.NodeFactory, polyglot.types.TypeSystem)
	 */
	public List/*<Stmt>*/ explode(NodeFactory nf, TypeSystem ts, Stmt s) {
		return explode(nf, ts, name(), position(), flags(), vars, lis, s);
	}

	public List/*<Stmt>*/ explode(NodeFactory nf, TypeSystem ts, List/*<Stmt>*/ s) {
		return explode(nf, ts, name(), position(), flags(), vars, lis, s);
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
	public static List/*<Stmt>*/ explode(NodeFactory nf, TypeSystem ts,
										 String name, Position pos,
										 Flags flags, AmbExpr[] vars,
										 LocalInstance[] lis)
	{
		if (vars == null || vars == NO_VARS) return null;
		X10NodeFactory x10nf = (X10NodeFactory) nf;
		List stmts = new ArrayList(vars.length);
		Expr arrayBase = nf.AmbExpr(pos, name);
		TypeNode intType = x10nf.CanonicalTypeNode(pos, ts.Int());
		for (int i = 0; i < vars.length; i++) {
			// int arglist(i) = name[i];
			AmbExpr var = vars[i];
			Expr index = x10nf.IntLit(var.position(), IntLit.INT, i);
			Expr init = x10nf.X10ArrayAccess1(var.position(), arrayBase, index);
			LocalInstance li = lis != null
				? lis[i]
				: ts.localInstance(var.position(), flags, ts.Int(), var.name());
			Stmt d = makeLocalDecl(nf, var.position(), flags, intType, var.name(), li, init);
			stmts.add(d);
		}
		return stmts;
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
										 String name, Position pos,
										 Flags flags, AmbExpr[] vars)
	{
		return explode(nf, ts, name, pos, flags, vars, null);
	}

	/**
	 * Return the initialization statements for the exploding variables
	 * plus the given statement.
	 *
	 * @param nf
	 * @param ts
	 * @param name
	 * @param pos
	 * @param flags
	 * @param vars
	 * @param lis
	 * @param s
	 * @return
	 */
	public static List/*<Stmt>*/ explode(NodeFactory nf, TypeSystem ts,
										 String name, Position pos,
										 Flags flags, AmbExpr[] vars,
										 LocalInstance[] lis, Stmt s)
	{
		List/*<Stmt>*/ init = explode(nf, ts, name, pos, flags, vars, lis);
		if (s != null)
			init.add(s);
		return init;
	}

	/**
	 * Return the initialization statements for the exploding variables
	 * plus the given statements.
	 *
	 * @param nf
	 * @param ts
	 * @param name
	 * @param pos
	 * @param flags
	 * @param vars
	 * @param lis
	 * @param s
	 * @return
	 */
	public static List/*<Stmt>*/ explode(NodeFactory nf, TypeSystem ts,
										 String name, Position pos,
										 Flags flags, AmbExpr[] vars,
										 LocalInstance[] lis, List/*<Stmt>*/ s)
	{
		List/*<Stmt>*/ init = explode(nf, ts, name, pos, flags, vars, lis);
		if (s != null)
			init.addAll(s);
		return init;
	}
}

