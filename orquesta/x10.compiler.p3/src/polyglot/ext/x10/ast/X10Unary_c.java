/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by igor on Feb 15, 2006
 */
package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Unary;
import polyglot.ast.Unary_c;
import polyglot.ast.Binary.Operator;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * An immutable representation of a unary operation op Expr.
 * Overridden from Java to allow unary negation of points.
 *
 * @author igor Feb 15, 2006
 */
public class X10Unary_c extends Unary_c {

	/**
	 * @param pos
	 * @param op
	 * @param expr
	 */
	public X10Unary_c(Position pos, Operator op, Expr expr) {
		super(pos, op, expr);
	}

	/** Get the precedence of the expression. */
	public Precedence precedence() {
		/* [IP] TODO: This should be the real precedence */
		Type l = expr.type();
        X10TypeSystem xts = (X10TypeSystem) l.typeSystem();
		if (xts.isPoint(l)) {
			return Precedence.LITERAL;
		}
		return super.precedence();
	}

	// TODO: take care of constant points.
	public Object constantValue() {
		return super.constantValue();
	}

	/**
	 * Type check a binary expression. Must take care of various cases because
	 * of operators on regions, distributions, points, places and arrays.
	 * An alternative implementation strategy is to resolve each into a method
	 * call.
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
	    X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	        Type t = expr.type();
//		if ((op == NEG || op == POS) && ts.isPoint(t)) {
//			return type(t);
//		}
		
		Map<Unary.Operator,String> methodNameMap = new HashMap<Unary.Operator, String>();
		methodNameMap.put(NEG, "neg");
		methodNameMap.put(POS, "pos");
		methodNameMap.put(NOT, "not");
		methodNameMap.put(BIT_NOT, "not");
		
		String methodName = methodNameMap.get(op);
		Type l = t;
		if (l instanceof StructType && methodName != null) {
		    // Check if there is a method with the appropriate name and type with the left operand as receiver.   
		    try {
			X10MethodInstance mi = ts.findMethod((StructType) l, methodName, Collections.EMPTY_LIST, Collections.EMPTY_LIST, tc.context().currentClassDef());
			return type(mi.returnType());
		    }
		    catch (SemanticException e) {
			// Cannot find the method.  Fall through.
		    }
		}

		X10Unary_c n = (X10Unary_c) super.typeCheck(tc);

		Type resultType = n.type();
		resultType = ts.performUnaryOperation(resultType, t, op);
		if (resultType != n.type()) {
			n = (X10Unary_c) n.type(resultType);
		}

		return n;
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		Type t = expr.type();
        X10TypeSystem ts = (X10TypeSystem) t.typeSystem();
		if ((op == NEG || op == POS) && ts.isPoint(t)) {
			printSubExpr(expr, true, w, tr);
			if (op == NEG) {
				w.write(".neg()");
			}
			return;
		}
		super.prettyPrint(w, tr);
	}
}

