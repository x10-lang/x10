/*
 * Created by vj on Jan 21, 2005
 */
package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ext.jl.ast.Binary_c;
import polyglot.ext.x10.types.X10ReferenceType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.main.Report;
import polyglot.types.NoMemberException;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;
import x10.lang.dist;
import x10.lang.place;
import x10.lang.region;

/**
 * An immutable representation of a binary operation Expr op Expr.
 * Overridden from Java to allow distributions, regions, points and places to
 * participate in binary operations.
 *
 * @author vj Jan 21, 2005
 */
public class X10Binary_c extends Binary_c {

	private final X10TypeSystem xts = X10TypeSystem_c.getTypeSystem();
	private final X10NodeFactory_c xnf = X10NodeFactory_c.getNodeFactory();

	/**
	 * @param pos
	 * @param left
	 * @param op
	 * @param right
	 */
	public X10Binary_c(Position pos, Expr left, Operator op, Expr right) {
		super(pos, left, op, right);

	}

	/** Get the precedence of the expression. */
	public Precedence precedence() {
		/* [IP] TODO: This should be the real precedence */
		X10Type l = (X10Type) left.type();
		if (xts.isPoint(l) || xts.isPlace(l) || xts.isDistribution(l)
				|| xts.isRegion(l) ||
			xts.isPrimitiveTypeArray(l) || xts.isDistributedArray(l))
		{
			return Precedence.LITERAL;
		}
		return super.precedence();
	}

	public boolean isConstant() {
		if (super.isConstant())
			return true;
		// [IP] An optimization: an object of a non-nullable type and "null"
		// can never be equal.
		X10Type lt = (X10Type) left.type();
		X10Type rt = (X10Type) right.type();
		if (lt == null || rt == null)
			return false;
		return (lt.isNull() && ! xts.isNullable(rt)) ||
			   (!xts.isNullable(lt) && rt.isNull());
	}
	public boolean isDisambiguated() {
		boolean val = super.isDisambiguated() && 
		(left == null || left.isDisambiguated()) &&
		(right == null || right.isDisambiguated());
		return val;
	}

	// TODO: take care of the base cases for regions, distributions, points and places.
	public Object constantValue() {

		Object result = super.constantValue();
		if (result != null)
			return result;
		if (!isConstant())
			return null;

		Object lv = left.constantValue();
		Object rv = right.constantValue();
		X10Type lt = (X10Type) left.type();
		X10Type rt = (X10Type) right.type();

		// [IP] An optimization: an object of a non-nullable type and "null"
		// can never be equal.
		assert (!(op == EQ || op == NE) || (lt.isNull() && !xts.isNullable(rt)) ||
				(!xts.isNullable(lt) && rt.isNull()));
		if (op == EQ) return Boolean.FALSE;
		if (op == NE) return Boolean.TRUE;

		try {
			if (xts.isDistribution(lt)) {
				dist l = (dist) lv;
				if (xts.isDistribution(rt)) {
					if (op == COND_OR) return l.union((dist) rv);
				}
				if (xts.isPlace(rt)) {
					if (op == BIT_OR) return l.restriction((place) rv);
				}
				if (xts.isRegion(rt)) {
					if (op == BIT_OR) return l.restriction((region) rv);
					if (op == SUB) return l.difference((region) rv);
				}
			}
			if (xts.isRegion(lt)) {
				region l = (region) lv;
				if (xts.isRegion(rt)) {
					region r = (region) rv;
					if (op == SUB) return l.difference(r);
					if (op == COND_OR) return l.union(r);
					if (op == COND_AND) return l.intersection(r);
				}
			}
		} catch (ArithmeticException e) {
			// ignore div by 0
		}
		return null;
	}

	/**
	 * Type check a binary expression. Must take care of various cases because
	 * of operators on regions, distributions, points, places and arrays.
	 * An alternative implementation strategy is to resolve each into a method
	 * call.
	 */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10Type l = (X10Type) left.type();
		X10Type r = (X10Type) right.type();

		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		// TODO: define these operations for arrays as well, with the same distribution.
		if ((op == GT || op == LT || op == GE || op == LE) && (xts.isPoint(l)
				|| xts.isPlace(l))) {
			if (xts.isPlace(l)) {
				if (!xts.isPlace(r))
					throw new SemanticException("The " + op +
							" operator instance must have a place operand.", right.position());
			}
			if (xts.isPoint(l)) {
				if (!xts.isPoint(r))
					throw new SemanticException("The " + op +
							" operator instance must have a point operand.", right.position());
			}
			return type(ts.Boolean());
		}
		// TODO: Check that the underlying regions are disjoint.

		if (op == COND_OR && xts.isDistributedArray(l)) { // || -- <T>array.union(<T>array right)
			if (!(l.equals(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have distributed array operands of the same base type ", right.position());
			}
			return type(l);
		}
		if (op == COND_OR && xts.isRegion(l)) { // region.union(region r)
			if (!(xts.isRegion(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a region operand.", right.position());
			}
			return type(ts.region());
		}
		if (op == COND_OR && xts.isDistribution(l)) { // || distribution.union(distribution r)
			if (!(xts.isDistribution(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			return type(ts.distribution());
		}
		if (op == COND_AND && xts.isRegion(l)) { // && region.intersection(region r)
			if (!(xts.isRegion(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a region operand.", right.position());
			}
			return type(ts.region());
		}
		if (op == COND_AND && xts.isDistribution(l)) { // && distribution.intersection(distribution r)
			if (!(xts.isDistribution(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			return type(ts.distribution());
		}

		if (op == BIT_OR && xts.isDistributedArray(l)) { // | array.restriction(distribution or region or place)
			if (!(xts.isDistribution(r) || xts.isRegion(r) || xts.isPlace(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			return type(l);
		}
		if (op == BIT_OR && xts.isDistribution(l)) {
			// distribution.restriction(place p) or distribution.restriction(region r)
			if (!(xts.isPlace(r) || xts.isRegion(r)))
				throw new SemanticException("This " + op +
						" operator instance must have a place or region operand.", right.position());
			return type(ts.distribution());
		}

		if (op == SUB && xts.isDistribution(l)) { //distribution.difference(region r)
			if (!(xts.isRegion(r) || xts.isDistribution(r))) {
				throw new SemanticException("The " + op +
						" operator must have a region or distribution operand.", right.position());
			}
			return type(ts.distribution());
		}
		if (op == SUB && xts.isRegion(l)) { // distribution.difference(region r)
			if (!xts.isRegion(r)) {
				throw new SemanticException("The " + op +
						" operator must have a region operands.", right.position());
			}
			return type(ts.region());
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) &&
				xts.isPrimitiveTypeArray(l)) {
			// FIXME: allow strings here
			// pointwise numerical operations. TODO: Check that one type can be numerically coerced to the other.
			if (!l.equals(r)) {
				throw new SemanticException("The " + op
						+ " operator must have arrays of the same base type as operands.", right.position());
			}
			return type(l);
		}

		if ((op == SUB || op == ADD || op == MUL || op == DIV) &&
				xts.isPoint(l) && !ts.equals(r, ts.String()))
		{
			if (!xts.isPoint(r) && !r.isIntOrLess())
				throw new SemanticException("The " + op +
						" operator instance must have a point or integer operand.", right.position());
			return type(l);
		}

		if ((op == SUB || op == ADD || op == MUL || op == DIV) &&
				xts.isPoint(r) && !ts.equals(l, ts.String()))
		{
			if (!l.isIntOrLess())
				throw new SemanticException("The " + op +
						" operator instance must have a point or integer operand.", left.position());
			return type(r);
		}

		return super.typeCheck(tc);
	}

	public List acceptCFG(CFGBuilder v, List succs) {
		if ((op == COND_OR && (left instanceof dist || left instanceof region))
				|| (op == COND_AND && left instanceof region))
		{
			v.visitCFG(left, right.entry());
			v.visitCFG(right, this);
			return succs;
		}
		return super.acceptCFG(v, succs);
	}

	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		X10Type l = (X10Type) left.type();
		X10Type r = (X10Type) right.type();

		if ((op == GT || op == LT || op == GE || op == LE) & (xts.isPoint(l) || xts.isPlace(l))) {
			String name = op == GT ? "gt" : op == LT ? "lt" : op == GE ? "ge" : "le";
			generateInstanceCall(w, tr, left, name, right);
			return;
		}

		if (op == COND_OR && (xts.isDistribution(l) ||
				xts.isRegion(l) || xts.isPrimitiveTypeArray(l)))
		{
			generateStaticOrInstanceCall(w, tr, left, "union", right);
			return;
		}
		if (op == COND_AND && (xts.isRegion(l) || xts.isDistribution(l))) {
			generateStaticOrInstanceCall(w, tr, left, "intersection", right);
			return;
		}

		// New for X10.
		if (op == BIT_OR && (xts.isDistribution(l) ||
					xts.isDistributedArray(l) || xts.isPlace(l))) {
			generateStaticOrInstanceCall(w, tr, left, "restriction", right);
			return;
		}

		// Modified for X10.
		if (op == SUB && (xts.isDistribution(l) || xts.isRegion(l))) {
			generateInstanceCall(w, tr, left, "difference", right);
			return;
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPrimitiveTypeArray(l)) {
			String name = op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div";
			generateInstanceCall(w, tr, left, name, right);
			return;
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPoint(l) && !xts.equals(r, xts.String())) {
			String name = op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div";
			generateInstanceCall(w, tr, left, name, right);
			return;
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) && xts.isPoint(r) && !xts.equals(l, xts.String())) {
			String name = "inv" + (op == SUB ? "sub" : op == ADD ? "add" : op == MUL ? "mul" : "div");
			generateInstanceCall(w, tr, right, name, left);
			return;
		}
		super.prettyPrint(w, tr);
	}

	/**
	 * @param w
	 * @param tr
	 * @param left TODO
	 * @param name
	 * @param right TODO
	 */
	private void generateStaticOrInstanceCall(CodeWriter w, PrettyPrinter tr, Expr left, String name, Expr right) {
		X10Type l = (X10Type) left.type();
		X10Type r = (X10Type) right.type();
		try {
			X10ReferenceType aType = (X10ReferenceType) l;
			xts.findMethod(aType, name, Collections.singletonList(r), xts.Object());
		} catch (NoMemberException e) {
			xnf.Call(position(), xnf.CanonicalTypeNode(position(), xts.ArrayOperations()),
					name, left, right).prettyPrint(w, tr);
//			w.write("x10.lang.ArrayOperations.");
//			w.write(name);
//			w.write("(");
//			printSubExpr(left, false, w, tr);
//			w.write(", ");
//			printSubExpr(right, false, w, tr);
//			w.write(")");
			return;
		} catch (SemanticException e) {
			assert (false);
			throw new RuntimeException(e);
		}
		generateInstanceCall(w, tr, left, name, right);
		return;
	}

	/**
	 * @param w
	 * @param tr
	 * @param left TODO
	 * @param name
	 * @param right TODO
	 */
	private void generateInstanceCall(CodeWriter w, PrettyPrinter tr, Expr left, String name, Expr right) {
		xnf.Call(position(), left, name, right).prettyPrint(w, tr);
//		printSubExpr(left, true, w, tr);
//		w.write(".");
//		w.write(name);
//		w.write("(");
//		printSubExpr(right, false, w, tr);
//		w.write(")");
	}
}

