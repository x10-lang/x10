/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.Variable;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.main.Report;
import polyglot.types.Type;

public class C_BinaryTerm_c extends C_Term_c implements C_BinaryTerm {

	public final C_Term left;
	public final C_Term right;
	public final String op;
	public C_BinaryTerm_c(String op, C_Term left, C_Term right, Type type) {
		super(type);
		this.left = left;
		this.right = right;
		this.op = op;
		assert left.type() != null;
		assert right.type() != null;
		assert type != null;
	}
	
	public C_Term copy() {
	    return new C_BinaryTerm_c(op, left.copy(), right.copy(), type);
	}
	
	public C_Term substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
	    C_BinaryTerm_c result = new C_BinaryTerm_c(op, left.substitute(y, x, propagate, visited), right.substitute(y, x, propagate, visited), substituteType(y, x, propagate, visited));
	    return result;
	}
	
        public void collectVars(List<C_Var> accum) {
            left.collectVars(accum);
            right.collectVars(accum);
        }
        
        /** Return true if po is higher prec than op. */
        private boolean higherPrec(String po) {
            if (po.equals("||")) return false;
            if (op.equals("==") && po.equals("&&")) return false;
            if (op.equals("!=") && po.equals("&&")) return false;
            if (op.equals("<=") && po.equals("&&")) return false;
            if (op.equals("<=") && po.equals("==")) return false;
            if (op.equals("<=") && po.equals("!=")) return false;
            if (op.equals("<") && po.equals("&&")) return false;
            if (op.equals("<") && po.equals("==")) return false;
            if (op.equals("<") && po.equals("!=")) return false;
            if (op.equals(">=") && po.equals("&&")) return false;
            if (op.equals(">=") && po.equals("==")) return false;
            if (op.equals(">=") && po.equals("!=")) return false;
            if (op.equals(">") && po.equals("&&")) return false;
            if (op.equals(">") && po.equals("==")) return false;
            if (op.equals(">") && po.equals("!=")) return false;

            if (op.equals("&&") && po.equals("&&")) return true;

            if (po.equals("||")) return true;
            if (po.equals("==") && op.equals("&&")) return true;
            if (po.equals("!=") && op.equals("&&")) return true;
            if (po.equals("<=") && op.equals("&&")) return true;
            if (po.equals("<=") && op.equals("==")) return true;
            if (po.equals("<=") && op.equals("!=")) return true;
            if (po.equals("<") && op.equals("&&")) return true;
            if (po.equals("<") && op.equals("==")) return true;
            if (po.equals("<") && op.equals("!=")) return true;
            if (po.equals(">=") && op.equals("&&")) return true;
            if (po.equals(">=") && op.equals("==")) return true;
            if (po.equals(">=") && op.equals("!=")) return true;
            if (po.equals(">") && op.equals("&&")) return true;
            if (po.equals(">") && op.equals("==")) return true;
            if (po.equals(">") && op.equals("!=")) return true;
            
            return false;
        }
	
        /** Return true if t is higher prec than this. */
	private boolean higherPrec(C_Term t) {
	    return t instanceof C_Var || t instanceof C_UnaryTerm ||
	    t instanceof C_BinaryTerm && higherPrec(((C_BinaryTerm) t).op());
	}

	public String op() { return op;}
	public C_Term left() { return left; }
	public C_Term right() { return right;}
	public String toString() {
	    StringBuffer sb = new StringBuffer();
	    if (! higherPrec(left))
	        sb.append("(");
	    sb.append(left.toString());
	    if (! higherPrec(left))
	        sb.append(")");
	    sb.append(" ");
	    sb.append(op);
	    sb.append(" ");
	    if (! higherPrec(right))
	        sb.append("(");
	    sb.append(right.toString());
	    if (! higherPrec(right))
	        sb.append(")");
	    return sb.toString();
	}
	
	public int hashCode() {
		return ((left == null) ? 0 : left.hashCode()) 
		+ (right==null ?  0 : right.hashCode())
		+ (op==null ? 0 : op.hashCode());
	}
	public boolean equals(Object o) {
		if (! (o instanceof C_BinaryTerm_c)) return false;
		if (o == null) return false;
		C_BinaryTerm_c other = (C_BinaryTerm_c) o;
		return left.equals(other.left) && right.equals(other.right) && op.equals(other.op);
	}
	public Promise toPromise() {
		throw new Error("Not implemented yet.");
	}
	public boolean rootVarIsSelf() { return false;}
	public boolean rootVarIsThis() { return false;}
	public boolean isEQV() { return false;}
	public boolean prefixes(C_Term term) { return false;}
}
