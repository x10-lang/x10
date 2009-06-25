/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Context;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

public class X10LocalDecl_c extends LocalDecl_c implements X10VarDecl {
	
	public X10LocalDecl_c(Position pos, Flags flags, TypeNode type,
			Id name, Expr init) {
		super(pos, flags, type, name, init);
	}
	  
	public String shortToString() {
		return "<X10LocalDecl_c #" + hashCode() 
		// + " flags= |" + flags + "|"
		+(type() == null ? "" : " <TypeNode #" + type().hashCode()+"type=" + type().type() + ">")
		+ " name=|" + id().id() + "|"
		+ (init() == null ? "" : " <Expr #" + init().hashCode() +">")
		+ (localDef() == null ? "" : " <LocalInstance #" + localDef().hashCode() +">")
		+ ">";
	}

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
	
}
