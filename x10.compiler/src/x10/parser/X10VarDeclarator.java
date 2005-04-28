/*
 * Created by vj on Jan 23, 2005
 *
 * 
 */
package x10.parser;

import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.IntLit;
import polyglot.ast.LocalDecl;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.parse.Name;
import polyglot.parse.VarDeclarator;
import polyglot.types.Flags;
import polyglot.util.Position;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10NodeFactory_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;


/**
 * @author vj Jan 23, 2005
 * 
 */
public class X10VarDeclarator extends VarDeclarator {
	public final List/*<Name>*/ paramList;
	public Flags flags;
	X10NodeFactory nf = X10NodeFactory_c.getFactory();
	X10TypeSystem ts = X10TypeSystem_c.getFactory();
	
	public X10VarDeclarator(Position pos, String name ) {
		this(pos, name, null);
	}
	public X10VarDeclarator(Position pos, String name, List/*<Name>*/ paramList) {
		super(pos, name);
		this.paramList = paramList;
	}
	public void setFlag(Flags flags) {
		
		boolean allCapitals = name.equals(name.toUpperCase());
		// vj: disable until we have more support for declarative programming in X10.
		this.flags = (false || allCapitals || hasExplodedVars()) ? flags.set(Flags.FINAL) : flags;
	}
	public boolean hasExplodedVars() {
	 return paramList != null;
	}
	/** Create a local variable declaration for an exploded var,
	 *  at the given type, name and with the given initializer.
	 * @param flags
	 * @param type
	 * @param name
	 * @param init
	 * @return
	 */
	protected LocalDecl makeLocalDecl( TypeNode type, String name, Expr init ) {
	
		boolean allCapitals = name.equals(name.toUpperCase());
		// vj: disable until we have more support for declarative programming in X10.
		Flags f = (false || allCapitals ? flags.set(Flags.FINAL) : flags);
		return nf.LocalDecl(pos, f, nf.array(type, pos, dims), name, init);
	}
	/** Given the flags for this variable declaration, return the initialization 
	 * statements for the exploding variables, if any.
	 * 
	 * @param flags
	 * @param type
	 * @return
	 */
	public List/*<Stmt>*/ explode( Stmt s ) {
	    if (paramList == null) return null;
		List stmt = new LinkedList();
		Expr arrayBase = new Name(nf, ts, pos, name).toExpr();
		TypeNode intType = nf.CanonicalTypeNode(pos, ts.Int());
		for (int i=0; i < paramList.size(); i++) {
			// int arglist(i) = name[i];
			Name varName = (Name) paramList.get(i);
			Expr index = nf.IntLit(varName.pos, IntLit.INT, i);
			Expr init = nf.X10ArrayAccess1(varName.pos, arrayBase, index);
			Stmt d = makeLocalDecl( intType, varName.name, init);
			stmt.add(d);
		}
		if (s != null )
			stmt.add( s );
		return stmt;
	}
	public List explode () {
		return explode( null );
	}
}