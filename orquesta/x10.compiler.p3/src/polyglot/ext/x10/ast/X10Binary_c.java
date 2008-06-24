/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 21, 2005
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Binary_c;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ext.x10.types.X10ArraysMixin;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10LocalDef_c;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.ExprFlattener.Flattener;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.TypeChecker;
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XSelf;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
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
public class X10Binary_c extends Binary_c implements X10Binary {

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
		Type l = left.type();
		X10TypeSystem xts = (X10TypeSystem) l.typeSystem();
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
		Type lt = left.type();
		Type rt = right.type();
		X10TypeSystem xts = (X10TypeSystem) lt.typeSystem();
		if (lt == null || rt == null)
			return false;
		if (operator() == Binary.EQ || operator() == Binary.NE) {
		if ( xts.isValueType(lt) && rt.isNull())
		    return true;
		if ( xts.isValueType(rt) && lt.isNull())
		    return true;
		}
		return false;
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
		Type lt = left.type();
		Type rt = right.type();
		X10TypeSystem xts = (X10TypeSystem) lt.typeSystem();

		// [IP] An optimization: an value and null can never be equal
		if (op == EQ) {
		    if ( xts.isValueType(lt) && rt.isNull())
			return Boolean.FALSE;
		    if ( xts.isValueType(rt) && lt.isNull())
			return Boolean.FALSE;
		}
		if (op == NE) {
		    if ( xts.isValueType(lt) && rt.isNull())
			return Boolean.TRUE;
		    if ( xts.isValueType(rt) && lt.isNull())
			return Boolean.TRUE;
		    return null;
		}

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
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

		Type l = left.type();
		Type r = right.type();
		//Report.report(1, "X10Binary_c: l=" + l + " r=" + r + " op=" + op);
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		if (op == EQ || op == NE) {
			l = X10TypeMixin.xclause(l, null);
			r = X10TypeMixin.xclause(r, null);
			if (! ts.isCastValid(l, r) && ! ts.isCastValid(r, l)) {
					throw new SemanticException("The " + op +
					    " operator must have operands of similar type.",
					    position());
				    }
			
				return type(ts.Boolean());
			}
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
			l = X10TypeMixin.xclause(l, null);
			r = X10TypeMixin.xclause(r, null);
			if (!(l.typeEquals(r))) {
				throw new SemanticException("The operands of " + op +
						" have base types " + l + " and " + r + "; these must " +
								"be similar. ", right.position());
			}
			return type(l);
		}
		if (op == COND_OR && xts.isRegion(l)) { // region.union(region r)
			if (!(xts.isRegion(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a region operand.", right.position());
			}
			Type t = checkRanks(l,r, ts.region());
			X10Binary_c b = (X10Binary_c) type(t);
			XConstraint c = X10TypeMixin.xclause(t);
			if (c != null) {
				try {
					XTerm v = xts.xtypeTranslator().trans(b);
					c = c.copy();
					try {
						c.addSelfBinding(v);
						t = X10TypeMixin.xclause(t, c);
					}
					catch (XFailure f) {
						// cannot translate the constraint; ignore
					}
				}
				catch (SemanticException e) {}
			}
			return b.type(t);
		}
		if (op == COND_OR && xts.isDistribution(l)) { // || distribution.union(distribution r)
			if (!(xts.isDistribution(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			// Cannot return l since all the properties of l, e.g. region, may not be preserved.
			return type(checkRanks(l,r, ts.distribution()));
		}
		if (op == COND_AND && xts.isRegion(l)) { // && region.intersection(region r)
			if (!(xts.isRegion(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a region operand.", right.position());
			}
			Type t = checkRanks(l,r, ts.region());
			X10Binary_c b = (X10Binary_c) type(t);
			XConstraint c = X10TypeMixin.xclause(t);
			if (c != null) {
				try {
					XTerm v = xts.xtypeTranslator().trans(b);
					c = c.copy();
					try {
						c.addSelfBinding(v);
						t = X10TypeMixin.xclause(t, c);
					}
					catch (XFailure f) {
						// cannot translate the constraint; ignore
					}
				}
				catch (SemanticException e) {}
			}
			return b.type(t);
		}
		if (op == COND_AND && xts.isDistribution(l)) { 
			// && distribution.intersection(distribution r)
			if (!(xts.isDistribution(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			
			return type(checkRanks(l,r, ts.distribution()));
		}

		if (op == BIT_OR && xts.isDistributedArray(l)) { 
			// | array.restriction(distribution or region or place)
			if (!(xts.isDistribution(r) || xts.isRegion(r) || xts.isPlace(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			Type lType = l;
			Type type = lType;
			//Report.report(1, "X10Binary_c: array | r or p or d, lType = " + lType);
			XTerm rank = X10ArraysMixin.rank(lType);
			
			if (rank != null) type = X10ArraysMixin.setRank(type, rank);
			if (xts.isPlace(r) && r instanceof Here) 
				type = X10ArraysMixin.setOnePlace(type, xts.here());
			try {
				XTerm v = xts.xtypeTranslator().trans(right);
				if (xts.isDistribution(r))
					type = X10ArraysMixin.setDistribution(type, v);
				if (xts.isRegion(r))
					type = X10ArraysMixin.setRegion(type, v);
			}
			catch (SemanticException e) {
			}
			Expr result = type(type);
			//Report.report(1, "X10Binary_c: returning " + result + " of type " + result.type());
			return result;
		}
		if (op == BIT_OR && xts.isDistribution(l)) {
			// distribution.restriction(place p) or distribution.restriction(region r)
			if (!(xts.isPlace(r) || xts.isRegion(r)))
				throw new SemanticException("This " + op +
						" operator instance must have a place or region operand.", right.position());
			
			
			Type lType = l;
			Type type = ts.distribution();
			//Report.report(1, "X10Binary_c: dist | r or p or d, lType = " + lType);
			XTerm rank = X10ArraysMixin.rank(lType);
			
			if (rank != null) type = X10ArraysMixin.setRank(type, rank);
			if (xts.isPlace(r) && r instanceof Here) 
				type = X10ArraysMixin.setOnePlace(type, xts.here());
			
			Expr result = type(type);
			//Report.report(1, "X10Binary_c: returning " + result + " of type " + result.type());
			return result;
		}

		if (op == SUB && xts.isDistribution(l)) { //distribution.difference(region r)
			if (!(xts.isRegion(r) || xts.isDistribution(r))) {
				throw new SemanticException("The " + op +
						" operator must have a region or distribution operand.", right.position());
			}
			return type(checkRanks(l,r, ts.distribution()));
		}
		if ((op == ADD || op == SUB) && xts.isRegion(l) && xts.isPoint(r)) {
			Type t = checkRanks(l,r, ts.region());
			X10Binary_c b = (X10Binary_c) type(t);
			XConstraint c = X10TypeMixin.xclause(t);
			if (c != null) {
				try {
					XTerm v = xts.xtypeTranslator().trans(b);
					c = c.copy();
					try {
						c.addSelfBinding(v);
						t = X10TypeMixin.xclause(t, c);
					}
					catch (XFailure f) {
						// cannot translate the constraint; ignore
					}
				}
				catch (SemanticException e) {}
			}
			return b.type(t);
		}
		if (op == SUB && xts.isRegion(l)) { // region.difference(region r)
			if (!xts.isRegion(r)) {
				throw new SemanticException("The " + op +
						" operator must have a region operands.", right.position());
			}
			Type t = checkRanks(l,r, ts.region());
			X10Binary_c b = (X10Binary_c) type(t);
			XConstraint c = X10TypeMixin.xclause(t);
			if (c != null) {
				try {
					XTerm v = xts.xtypeTranslator().trans(b);
					c = c.copy();
					try {
						c.addSelfBinding(v);
						t = X10TypeMixin.xclause(t, c);
					}
					catch (XFailure f) {
						// cannot translate the constraint; ignore
					}
				}
				catch (SemanticException e) {}
			}
			return b.type(t);
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) &&
				xts.isPrimitiveTypeArray(l)) {
			// FIXME: allow strings here
			// pointwise numerical operations. TODO: Check that one type can be numerically coerced to the other.
			if (!xts.typeBaseEquals(l, r)) {
				throw new SemanticException("The types of operands to " + op
						+ " are " + l + " and " + r + "; these types should be equal.", right.position());
			}
			// vj: Check that they are defined over the same region?
			checkDistributions(l, r, ts.distribution());
			return type(l);
		}

		if ((op == SUB || op == ADD || op == MUL || op == DIV) &&
				xts.isPoint(l) && !ts.isSubtype(r, ts.String()))
		{
			if (!xts.isPoint(r) && !r.isIntOrLess())
				throw new SemanticException("The " + op +
						" operator instance must have a point or integer operand.", right.position());
			return type(l);
		}

		if ((op == SUB || op == ADD || op == MUL || op == DIV) &&
				xts.isPoint(r) && !ts.isSubtype(l, ts.String()))
		{
			if (!l.isIntOrLess())
				throw new SemanticException("The " + op +
						" operator instance must have a point or integer operand.", left.position());
			return type(r);
		}

		X10Binary_c n = (X10Binary_c) super.typeCheck(tc);

		Type resultType = n.type();
		resultType = ts.performBinaryOperation(resultType, l, r, op);
		if (resultType != n.type()) {
			n = (X10Binary_c) n.type(resultType);
		}

		return n;
	}
	
	public Type checkRanks(Type l, Type r, Type result) throws SemanticException {
		Type lType =  l;
		Type rType =  r;
		
		XTerm lRank = X10ArraysMixin.rank(lType);
		XTerm rRank = X10ArraysMixin.rank(rType);
		
		if (lRank==null || ! lRank.equals(rRank)) {
			// Slow test:
			X10TypeSystem xts = (X10TypeSystem) l.typeSystem();

			X10LocalDef_c lx = new X10LocalDef_c(xts, left.position(), Flags.FINAL, Types.ref(l), "left");
			X10LocalDef_c ly = new X10LocalDef_c(xts, right.position(), Flags.FINAL, Types.ref(r), "right");
			
			// Don't use EQVs since we want them to span multiple constraints and I'm not sure it will work.--nystrom
			XLocal x = xts.xtypeTranslator().trans(lx.asInstance());
			XLocal y = xts.xtypeTranslator().trans(ly.asInstance());
			
			XConstraint_c c1 = new XConstraint_c();
			XConstraint_c c2 = new XConstraint_c();

			try {
				XConstraint cx = X10TypeMixin.realX(lType).substitute(x, XSelf.Self);
				XConstraint cy = X10TypeMixin.realX(rType).substitute(y, XSelf.Self);

				c1.addIn(cx);
				c1.addIn(cy);

				FieldInstance fx = X10ArraysMixin.getProperty(lType, "rank");
				FieldInstance fy = X10ArraysMixin.getProperty(rType, "rank");
				XTerm eq = XTerms.makeEquals(xts.xtypeTranslator().trans(x, fx), xts.xtypeTranslator().trans(y, fy));
				c2.addTerm(eq);

				if (c1.entails(c2)) {
					result = X10ArraysMixin.setRank(result, lRank);
					if (rRank != null) {
						XConstraint c = X10TypeMixin.xclause(result).copy();
						XTerm eq2 = XTerms.makeEquals(XSelf.Self, rRank);
						result = X10TypeMixin.xclause(result, c);
						c = c.addTerm(eq2);
					}

					//Report.report(1, "X10Binary_c: exiting lRank=" + lRank + " rRank=" + rRank);
					return result;
				}
			}
			catch (XFailure e) {
				throw new SemanticException(e.getMessage());
			}
		}
				
		if (lRank==null || ! lRank.equals(rRank)) {
				
		//Report.report(1, "X10Binary_c: entering lRank=" + lRank + " rRank=" + rRank);
		
		//	Report.report(1, "X10Binary_c: lRank=" + lRank + " rRank=" + rRank);
			throw new SemanticException("The argument " + left +  " (of type " +  l + " " + l.getClass() + ") has rank " 
					+ lRank + " and " +
					right + " (of type " + r + ") has rank " + rRank + "; these must be equal.");
		}
		
		result = X10ArraysMixin.setRank(result, lRank);
		//Report.report(1, "X10Binary_c: exiting lRank=" + lRank + " rRank=" + rRank);
		return result;
	}
	
	public Type checkDistributions(Type l, Type r, Type result) throws SemanticException {
		Type lType = l;
		Type rType = r;
		
		XTerm lDist = X10ArraysMixin.distribution(lType);
		XTerm rDist = X10ArraysMixin.distribution(rType);
		//Report.report(1, "X10Binary_c: entering lRank=" + lRank + " rRank=" + rRank);
		if (lDist==null || ! lDist.equals(rDist)) {
		//	Report.report(1, "X10Binary_c: lRank=" + lRank + " rRank=" + rRank);
			throw new SemanticException("The argument " + left +  " (of type " +  l + " " + l.getClass() + ") has distribution " 
					+ lDist + " and " +
					right + " (of type " + r + ") has distribution " + rDist + "; these must be equal.");
		}
		
		result = X10ArraysMixin.setDistribution(result, lDist);
		//Report.report(1, "X10Binary_c: exiting lRank=" + lRank + " rRank=" + rRank);
		return result;
	}


	 /** Flatten the expressions in place and body, creating stmt if necessary.
     * The place field must be visited by the given flattener since those statements must be executed outside
     * the future. Howeever, the body must be visited in a new flattener and the statements produced
     * captured and stored in stmt. 
     * Note that this works by side-effecting the current node. This is necessary
     * because the method is called from within an enter call for a Visitor. I dont know
     * of any way of making the enter call return a copy of the node. 
     * @param fc
     * @return
     */
	public Expr flatten(ExprFlattener.Flattener fc) {
		//Report.report(1, "X10Binary_c: entering X10Binary " + this);
		assert (op== Binary.COND_AND || op==Binary.COND_OR);
		X10Context xc = (X10Context) fc.context();
		
		
		final NodeFactory nf = fc.nodeFactory();
		final TypeSystem ts = fc.typeSystem();
		final Position pos = position();
		final Id resultVarName = nf.Id(pos, xc.getNewVarName());
		final TypeNode tn = nf.CanonicalTypeNode(pos,type);
		Flags flags = Flags.NONE;
		
		final LocalDef li = ts.localDef(pos, flags, Types.ref(type), resultVarName.id());
		// Evaluate the left.
		Expr nLeft = (Expr) left.visit(fc);
		final LocalDecl ld = nf.LocalDecl(pos, nf.FlagsNode(pos, flags), tn, resultVarName, nLeft).localDef(li);
		fc.add(ld);
		
		final Local ldRef = (Local) nf.Local(pos,resultVarName).localInstance(li.asInstance()).type(type);
		Flattener newVisitor = (Flattener) new ExprFlattener.Flattener(fc.job(), ts, nf, this).context(xc);
		Expr nRight = (Expr) right.visit(newVisitor);
		List condBody = newVisitor.stmtList(); 
		Expr assign = nf.Assign(pos, ldRef, Assign.ASSIGN, nRight ).type(type);
		Stmt eval = nf.Eval(pos, assign);
		condBody.add(eval);
		final Local ldRef2 = (Local) nf.Local(pos,resultVarName).localInstance(li.asInstance()).type(type);
		
		if (op == Binary.COND_AND) {
			final Stmt ifStm = nf.If(pos, ldRef2, nf.Block(pos, condBody));
			fc.add(ifStm);
		} else {
			Expr cond = nf.Unary(pos, ldRef2, Unary.NOT).type(type);
			final Stmt ifStm = nf.If(pos, cond, nf.Block(pos, condBody));
			fc.add(ifStm);
		}
		
		final Local ldRef3 = (Local) nf.Local(pos,resultVarName).localInstance(li.asInstance()).type(type);
		//Report.report(1, "X10Binary_c: returning " + ldRef3);
		return ldRef3;
		
	}
	public List acceptCFG(CFGBuilder v, List succs) {
		if ((op == COND_OR && (left instanceof dist || left instanceof region))
				|| (op == COND_AND && left instanceof region))
		{
			v.visitCFG(left, right, ENTRY);
			v.visitCFG(right, this, EXIT);
			return succs;
		}
		return super.acceptCFG(v, succs);
	}
}

