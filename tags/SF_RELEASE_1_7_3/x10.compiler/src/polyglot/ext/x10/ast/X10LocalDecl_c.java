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
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.LocalDecl_c;
import polyglot.ast.TypeNode_c;
import polyglot.types.LocalInstance_c;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.constr.C_Local_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.visit.TypeElaborator;
import polyglot.frontend.MissingDependencyException;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

public class X10LocalDecl_c extends LocalDecl_c implements X10VarDecl {
	
	public X10LocalDecl_c(Position pos, Flags flags, TypeNode type,
			Id name, Expr init) {
		super(pos, flags, type, name, init);
	}
	  
    public boolean isDisambiguated() {
        return (type == null || type.isDisambiguated()) 
        && li != null && li.isCanonical() && super.isDisambiguated();
    }
	public String shortToString() {
		return "<X10LocalDecl_c #" + hashCode() 
		// + " flags= |" + flags + "|"
		+(type() == null ? "" : " <TypeNode #" + type().hashCode()+"type=" + type().type() + ">")
		+ " name=|" + name() + "|"
		+ (init() == null ? "" : " <Expr #" + init().hashCode() +">")
		+ (localInstance() == null ? "" : " <LocalInstance #" + localInstance().hashCode() +">")
		+ ">";
	}
	public Node typeCheck(TypeChecker tc) throws SemanticException {
			X10LocalDecl_c result= (X10LocalDecl_c) super.typeCheck(tc);
			((X10LocalInstance) result.li).setSelfClauseIfFinal();
			return result.type(type().type(li.type()));
		
	}
	public X10LocalDecl_c pickUpTypeFromTypeNode(TypeChecker tc) {
		X10LocalInstance xli = (X10LocalInstance) li;
		X10Type newType = (X10Type) type.type();
		xli.setType(newType);
		xli.setSelfClauseIfFinal();
		return  (X10LocalDecl_c) type(type().type(xli.type()));
	}
	
	public Context enterChildScope(Node child, Context c) {
		X10Context cxt = (X10Context) c;
		if (child == this.type) {
			TypeSystem ts = c.typeSystem();
			LocalInstance li = localInstance();
			cxt.addVariable(li);
			cxt.setVarWhoseTypeIsBeingElaborated(localInstance());
		}
		Context cc = super.enterChildScope(child, c);
		return cc;
	}

	
}
