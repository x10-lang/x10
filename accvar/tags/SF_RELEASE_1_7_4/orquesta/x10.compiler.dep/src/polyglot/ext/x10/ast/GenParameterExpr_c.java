/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Expr_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/** An immutable representation of the type parameter list of a parameteric type.
 * The corresponding syntax is <T1,...,TN>
 * This node is created for (type) parameteric X10 types.
 * @author Christian Grothoff
 */
public class GenParameterExpr_c extends Expr_c implements GenParameterExpr {
	/**
         * The argument list of a parametric type. Maybe null.
	 */
	protected List<TypeNode> args;
	
	public GenParameterExpr_c(Position pos, List<TypeNode> l) {
		super(pos);
		this.args =  TypedList.copyAndCheck(l, TypeNode.class, false);
	}
	
	public List<TypeNode> args() {
		return this.args;
	}
	public GenParameterExpr args( List<TypeNode> args ) {
		GenParameterExpr_c n = (GenParameterExpr_c) copy();
		n.args = args;
		return n;
	}
	protected GenParameterExpr reconstruct( List<TypeNode> args) {
		if (args == this.args) return this;
		GenParameterExpr_c n = (GenParameterExpr_c) copy();
		n.args = args;
		return n;
	}
	public Node visitChildren(NodeVisitor v) {
	    List<TypeNode> arguments = visitList(this.args, v);
	    return reconstruct(arguments);
	  }
	
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#entry()
	 */
	public Term firstChild() {
		return (args.isEmpty())
		? null : listChild( args, null);
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
		if (args != null) {
   		    v.visitCFGList( args, this, EXIT);
		}
		return succs;
	}
	public String toString() {
		String s = "<";
		int count = 0;

	    for (Iterator<TypeNode> i = args.iterator(); i.hasNext(); ) {
	        if (count++ > 2) {
	            s += "...";
	            break;
	        }
	        TypeNode n = (TypeNode) i.next();
	        s += n.toString();

	        if (i.hasNext()) {
	            s += ", ";
	        }
	    }
	    return s + ">";
	}
	/** Write the statement to an output file. */
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		if ((args == null) || args.isEmpty()) return;
		w.write("<");
		w.begin(0);
		
		for (Iterator<TypeNode> it = args.iterator(); it.hasNext();) {
			Expr e = (Expr) it.next();
			print(e, w, tr);
			
			if (it.hasNext()) {
				w.write(",");
				w.allowBreak(0, " ");
			}	
		}
		w.end();
		w.write(">");
	}
	
}
