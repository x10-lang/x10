/*
 * Created by vj on Jan 21, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.BooleanLit;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ext.jl.ast.Binary_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.FlowGraph;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import x10.lang.distribution;
import x10.lang.place;
import x10.lang.region;
import x10.lang.point;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;

/** An immutable representation of a binary operation Expr op Expr. Overridden from Java to allow distributions
 * regions, points and places to participate in binary operations.
 * @author vj Jan 21, 2005
 * 
 */
public class X10Binary_c extends Binary_c {

	/**
	 * @param pos
	 * @param left
	 * @param op
	 * @param right
	 */
	public X10Binary_c(Position pos, Expr left, Operator op, Expr right) {
		super(pos, left, op, right);
	}
	// TODO: take care of the base cases for regions, distributions, points and places.
	public Object constantValue() {
		
		Object result = super.constantValue();
		if (result != null) 
			return result;
		if (! isConstant()) 
			return null;
		
		Object lv = left.constantValue();
		Object rv = right.constantValue();
		X10Type lt = (X10Type) left.type();
		X10Type rt = (X10Type) right.type();
		
		try {
			if (lt.isDistribution()) {
				distribution l = (distribution) lv;
				if (lt.isDistribution()) {
					if (op == COND_OR) return l.union((distribution) rv);
				}
				if (rt.isPlace()) {
					if (op == BIT_OR ) return l.restriction( (place) rv);
				}
				if (rt.isRegion()) {
					if (op == BIT_OR) return l.restriction( (region) rv);
					if (op == SUB) return l.difference((region) rv);
				}
			}
			if (lt.isRegion()) {
				region l = (region) lv;
				if (rt.isRegion()) {
					region r = (region) rv;
					if (op == SUB) return l.difference( r );
					if (op == COND_OR) return l.union( r );
					if (op == COND_AND) return l.intersection( r ); 
				}
			}
		} catch (Exception e) {
			// ignore div by 0
			
		}
		return null;
	}
	
	/** Type check a binary expression. Must take care of various cases because of operators
	 * on regions, distributions, points, places and arrays.
	 * An alternative implementation strategy is to resolve each into a method call. 
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10Type l = (X10Type) left.type();
		X10Type r = (X10Type) right.type();
		
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		// TODO: define these operations for arrays as well, with the same distribution.
		if ((op == GT || op == LT || op == GE || op == LE) & (l.isPoint() || l.isPlace())) {
			if (l.isPlace()) {
				if (! r.isPlace())
					throw new SemanticException("The " + op +
							" operator instance must have a place operand.", right.position());
			}
			if (l.isPoint()) {
				if (! r.isPoint())
					throw new SemanticException("The " + op +
							" operator instance must have a point operand.", right.position());
				
			}
			return type(ts.Boolean());
		}
		// TODO: Check that the underlying regions are disjoint.

		if (op == COND_OR && l.isDistributedArray()) { // || -- <T>array.union( <T>array right)
			if (! (l.equals(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have distributed array operands of the same base type ", right.position());
			}
			return type(l);
		}
		if (op == COND_OR && l.isRegion()) { // region.union(region r)
			if (! (r.isRegion())) {
				throw new SemanticException("This " + op +
						" operator instance must have a region operand.", right.position());
			}
			return type(ts.region());
		}
		if (op == COND_AND && l.isRegion()) { // region.intersection(region r)
			if (! (r.isRegion())) {
				throw new SemanticException("This " + op +
						" operator instance must have a region operand.", right.position());
			}
			return type(ts.region());
		}
		
		
		if (op == BIT_OR && l.isDistributedArray()) { // | array.restriction(distribution or region)
			if (! (r.isDistribution() || r.isRegion())) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			return type(l);
		}
		if (op == BIT_OR && l.isDistribution()) { 
			// distribution.restriction(place p) or distribution.restriction(region r)
			if ((! r.isPlace()) && (! r.isRegion() ))
				throw new SemanticException("This " + op +
						" operator instance must have a place or region operand.", right.position());
			if (r.isPlace()) return type(ts.region());
			if (r.isRegion()) return type(ts.distribution());
		}
		
		if (op == SUB && l.isDistribution()) { //distribution.difference(region r)
			if ( ! r.isRegion()) {
				throw new SemanticException("The " + op +
						" operator must have a region operands.", right.position());
			}
			return type(ts.distribution());
		}
		if (op == SUB && l.isRegion()) { // distribution.difference( region r)
			if ( ! r.isRegion()) {
				throw new SemanticException("The " + op +
						" operator must have a region operands.", right.position());
			}
			return type(ts.region());
		}	
		if ((op == SUB || op == ADD || op == MUL || op == DIV) && 
				l.isPrimitiveTypeArray()) {
			// pointwise numerical operations. TODO: Check that one type can be numerically coerced to the other.
			if (! l.equals(r)) {
				throw new SemanticException("The " + op 
						+ " operator must have  arrays of the same base type as operands.", right.position());
			}
			return type(l);
					
		}
		return super.typeCheck(tc);
	}
	public List acceptCFG(CFGBuilder v, List succs) {
		if ((op == COND_OR && (left instanceof distribution || left instanceof region))
				|| (op == COND_AND && left instanceof region)) {
			v.visitCFG(left, right.entry());
			v.visitCFG(right, this);
			return succs;
			
		}
		return super.acceptCFG(v, succs);
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		X10Type l = (X10Type) left.type();
		X10Type r = (X10Type) right.type();
		
		if ((op == GT || op == LT || op == GE || op == LE) & (l.isPoint() || l.isPlace())) {
			printSubExpr(left, true, w, tr);
			w.write(".");
			w.write(op == GT ? "gt" : (op == LT ? "lt" : (op == GE ? "ge" : "le")));
			w.write("(");
			printSubExpr(right, false, w, tr);
			w.write(")");
			return;
		}
		
		if (op == COND_OR && (l.isDistribution() || l.isRegion() || l.isPrimitiveTypeArray())) {
			printSubExpr(left, true, w, tr);
			w.write(".union(");
			printSubExpr(right, false, w, tr);
			w.write(")");
			return;
		}
		if (op == COND_AND && l.isRegion()) {
			printSubExpr(left, true, w, tr);
			w.write(".intersection(");
			printSubExpr(right, false, w, tr);
			w.write(")");
			return;
		}
		
		// New for X10.
		if (op == BIT_OR && (l.isDistribution() || l.isDistributedArray())) {
			printSubExpr(left, true, w, tr);
			w.write(".restriction(");
			printSubExpr(right, false, w, tr);
			w.write(")");
			return;
		}
		
		// Modified for X10.
		if (op == SUB && (l.isDistribution() || l.isRegion())) {
			printSubExpr(left, true, w, tr);
			w.write(".difference(");
			printSubExpr(right, false, w, tr);
			w.write(")");
			return;
		}	
		if ((op == SUB || op == ADD || op == MUL || op == DIV) &&  l.isPrimitiveTypeArray()) {
			printSubExpr(left, true, w, tr);
			w.write(".");
			w.write(op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div");
			w.write("(");
			printSubExpr(right, false, w, tr);
			w.write(")");
			return;
		}	
		super.prettyPrint(w, tr);
		
	}
	
}
