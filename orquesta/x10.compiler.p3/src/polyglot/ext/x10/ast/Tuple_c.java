/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 19, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.ArrayInit_c;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.Translator;
import polyglot.visit.TypeChecker;

/** An immutable representation of the X10 construct [e1,..., ek ]. 
 * If the type of ei is region, then this represents region.factory.region(new region[] {e1,...,ek}).
 * If the type of ei is int, then this represents point.factory.point(new int[] {e1,...,ek}).
 * Implementation strategy: Pretend that the data is being carried for two calls, with a common
 * argument list. During typechecking, determine which of these calls to resolve into. Need to make sure that 
 * the data for these calls exists, with the same effect as would be obtained by running all previous phases,
 * when the new call is established.
 * @author vj Jan 19, 2005
 * 
 */
public class Tuple_c extends ArrayInit_c implements Tuple {
	public Tuple_c(Position pos, Receiver pointReceiver, Receiver regionReceiver, List<Expr> args) {
		super(pos, args);
	}
	
	public List<Expr> arguments() { return super.elements(); }

	/** Type check the initializer. */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	    Type type = null;

	    for (Expr e : elements) {
		if (type == null) {
		    type = e.type();
		}
		else {
		    type = ts.leastCommonAncestor(type, e.type());
		}
	    }

	    if (type == null) {
		return type(ts.Null());
	    }
	    else {
		Type t = ((X10TypeSystem_c) ts).newAndImprovedValueArray(Types.ref(type));
		return type(t);
	    }
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("[");
	    for (Iterator<Expr> i = elements.iterator(); i.hasNext(); ) {
		Expr e = i.next();
		sb.append(e.toString());
		if (i.hasNext()) {
		    sb.append(", ");
		}
	    }
	    sb.append("]");
	    return sb.toString();
	}
	
	
	
	@Override
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	    w.write("[");

	    for (Iterator<Expr> i = elements.iterator(); i.hasNext(); ) {
		Expr e = i.next();

		print(e, w, tr);

		if (i.hasNext()) {
		    w.write(",");
		    w.allowBreak(0, " ");
		}
	    }

	    w.write("]");
	}
	
	@Override
	public void translate(CodeWriter w, Translator tr) {
	    super.prettyPrint(w, tr);
	}
}
