/*
 * Created by vj on Jan 23, 2005
 */
package x10.parser;

import java.util.List;
import java.util.Iterator;

import polyglot.ast.AmbExpr;
import polyglot.ext.jl.parse.Name;
import polyglot.parse.VarDeclarator;
import polyglot.types.Flags;
import polyglot.util.Position;

/**
 * @author vj Jan 23, 2005
 * @author igor Jan 13, 2006
 */
public class X10VarDeclarator extends VarDeclarator {
	private final AmbExpr[] vars;
	public Flags flags;

	public X10VarDeclarator(Position pos, String name) {
		this(pos, name, null);
	}

	public X10VarDeclarator(Position pos, String name, List/*<Name>*/ paramList) {
		super(pos, name);
		if (paramList != null) {
			this.vars = new AmbExpr[paramList.size()];
			for (int i = 0; i < this.vars.length; i++)
				this.vars[i] = (AmbExpr) ((Name) paramList.get(i)).toExpr();
		} else
			vars = null;
	}

	public void setFlag(Flags flags) {
		boolean allCapitals = name.equals(name.toUpperCase());
		// vj: disable until we have more support for declarative programming in X10.
		this.flags = (false && (allCapitals || hasExplodedVars())) ? flags.set(Flags.FINAL) : flags;
	}

	public boolean hasExplodedVars() {
		return vars != null;
	}

	public AmbExpr[] names() {
		return vars;
	}
}

