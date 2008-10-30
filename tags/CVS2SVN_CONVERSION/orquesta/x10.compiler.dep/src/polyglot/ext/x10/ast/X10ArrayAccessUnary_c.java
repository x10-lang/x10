/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by igor on Dec 19, 2005
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.Unary;
import polyglot.ast.Variable;
import polyglot.ast.Unary_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * An immutable representation of an X10 array access pre/post-
 * increment/decrement: a[point]++, etc.
 * Typechecking rules:
 * (1) point must be of a type (region) that can be cast to the array index type.
 * (2) The base type of the array must be numeric.
 * (3) No modification is allowed on a value array.
 *
 * @author igor Dec 19, 2005
 * @author vj July 7, 2006 -- removed early expansion.
 */
public class X10ArrayAccessUnary_c extends Unary_c 
	implements X10ArrayAccessUnary
{

	/**
	 * @param pos
	 * @param op
	 * @param expr
	 */
	public X10ArrayAccessUnary_c(Position pos, Operator op, X10ArrayAccess expr) {
		super(pos, op, expr);
	}

    /** Get the precedence of the expression. */
    public Precedence precedence() {
		return Precedence.LITERAL;
    }

	public Unary expr(Expr expr) {
		X10ArrayAccessUnary_c n = (X10ArrayAccessUnary_c)super.expr(expr);
		n.assertExprType();
		return n;
	}
	
	private void assertExprType() {
		if (!(expr() instanceof X10ArrayAccess)) {
			throw new InternalCompilerError("expression of an X10ArrayAccessUnary must be an X10 array access");
		}
	}
	
	public String opString(Operator op) {
		if (op == PRE_INC) return "preInc";
		if (op == POST_INC) return "postInc";
		if (op == PRE_DEC) return "preDec";
		if (op == POST_DEC) return "postDec";
		throw new InternalCompilerError("Unknown unary operator");
	}

	/** Type check the expression. */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		if (!expr.type().isNumeric()) {
			throw new SemanticException("Operand of " + op +
					" operator must be numeric.", expr.position());
		}
		if (! (expr instanceof Variable)) {
			throw new SemanticException("Operand of " + op +
					" operator must be a variable.", expr.position());
		}
		if (((Variable) expr).flags().isFinal()) {
			throw new SemanticException("Operand of " + op +
					" operator must be a non-final variable.",
					expr.position());
		}
        return type(expr.type());
	}

	public List throwTypes(TypeSystem ts) {
		List l = new ArrayList(super.throwTypes(ts));
		l.add(ts.NullPointerException());
		return l;
	}
    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		assert false;
      X10ArrayAccess a = (X10ArrayAccess) expr;
        printSubExpr(a.array(), w, tr);
        w.write ("." + opString(op)+"(");
        
        for(Iterator i = a.index().iterator(); i.hasNext();) {
            Expr e = (Expr) i.next();
            print(e, w, tr);
            
            if (i.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }
        w.write(")");
            
    }

}
