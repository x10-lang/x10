/*
 * Created by vj on Jan 23, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.TypeNode;
import polyglot.ast.Stmt;
import polyglot.ext.jl.ast.Formal_c;
import polyglot.types.Flags;
import polyglot.util.Position;
import  x10.parser.X10VarDeclarator;


/** An immutable representation of an X10Formal, which is of the form
 *  Flag Type VarDeclaratorId
 * Recall that a VarDeclaratorId may have an exploded syntax.
 * @author vj Jan 23, 2005
 * 
 */
public class X10Formal_c extends Formal_c implements X10Formal {
     protected X10VarDeclarator v;

	public X10Formal_c( Position pos, TypeNode type, X10VarDeclarator v) {
		super(pos, v.flags, type, v.name);
		this.v = v;
	}
	/* (non-Javadoc)
	 * @see polyglot.ext.jl.ast.X10Formal#hasExplodedVars()
	 */
	public boolean hasExplodedVars() {
		return v!=null && v.hasExplodedVars();
	}

	/* (non-Javadoc)
	 * @see polyglot.ext.jl.ast.X10Formal#explode()
	 */
	public List explode() {
		// TODO Auto-generated method stub
		return v == null ? null : v.explode();
	}
	public List explode( Stmt s ) {
		// TODO Auto-generated method stub
		return v == null ? null : v.explode(s );
	}

}
