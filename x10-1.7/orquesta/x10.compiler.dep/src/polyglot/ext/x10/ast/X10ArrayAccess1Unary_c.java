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
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.Unary;
import polyglot.ast.Variable;
import polyglot.ast.Unary_c;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * An immutable representation of an X10 array access pre/post-
 * increment/decrement: a[index]++, etc.
 * a[index] is represented by an X10ArrayAccess1.
 *
 * @author igor Dec 19, 2005
 * @author vj July 7, 2006 -- Fixed the early expansion.
 */
public class X10ArrayAccess1Unary_c extends Unary_c
	implements X10ArrayAccess1Unary
{

	/**
	 * @param pos
	 * @param op
	 * @param expr
	 */
	public X10ArrayAccess1Unary_c(Position pos, Operator op, Expr expr) {
		super(pos, op, expr);
	}

    /** Get the precedence of the expression. */
    public Precedence precedence() {
		return Precedence.LITERAL;
    }

	public Unary expr(Expr expr) {
       
		X10ArrayAccess1Unary_c n = (X10ArrayAccess1Unary_c)super.expr(expr);
		n.assertExprType();
		return n;
	}
	
	private void assertExprType() {
		if (!(expr() instanceof X10ArrayAccess1)) {
			throw new InternalCompilerError("expression of an X10ArrayAccess1Unary must be an X10 array access");
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
		if (true) {
			super.prettyPrint(w, tr);
			return;
		}
		assert false;
        
        if (! (expr instanceof X10ArrayAccess1 || expr instanceof ArrayAccess))
            throw new InternalCompilerError("X10ArrayAccess1Unary has an illegal argument " + expr);
        if (expr instanceof X10ArrayAccess1 ) {
            X10ArrayAccess1 x = (X10ArrayAccess1) expr;
            printSubExpr(x.array(), w, tr);
            w.write ("." + opString(op)+"(");
            printSubExpr(x.index(), w, tr);
            w.write(")");   
            return;
        }
        if (expr instanceof ArrayAccess ) {
            ArrayAccess x = (ArrayAccess) expr;
            String opString = (op == PRE_DEC || op == POST_DEC) ? "--" : "++";
            if (op == PRE_INC || op ==PRE_DEC)  w.write(opString);
            
            printSubExpr(x.array(), w, tr);
            w.write ("[");
            printSubExpr(x.index(), w, tr);
            w.write("]"); 
            if (op == POST_INC || op ==POST_DEC)  w.write(opString);
        }
         
        
                 
    }
   
}
