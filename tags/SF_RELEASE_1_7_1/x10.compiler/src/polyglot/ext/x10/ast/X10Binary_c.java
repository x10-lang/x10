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

import java.util.Collections;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
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
import polyglot.ast.Binary.Operator;
import polyglot.ast.Binary_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10ParsedClassType;
import polyglot.ext.x10.types.X10ReferenceType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Here_c;
import polyglot.ext.x10.types.constr.C_Local_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.ext.x10.visit.ExprFlattener;
import polyglot.ext.x10.visit.TypeElaborator;
import polyglot.ext.x10.visit.ExprFlattener.Flattener;
import polyglot.main.Report;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.NoMemberException;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
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
		X10Type l = (X10Type) left.type();
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
		X10Type lt = (X10Type) left.type();
		X10Type rt = (X10Type) right.type();
		X10TypeSystem xts = (X10TypeSystem) lt.typeSystem();
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
		X10TypeSystem xts = (X10TypeSystem) lt.typeSystem();

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
		X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
		X10Type l = (X10Type) left.type();
		X10Type r = (X10Type) right.type();
		//Report.report(1, "X10Binary_c: l=" + l + " r=" + r + " op=" + op);
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		if (op == EQ || op == NE) {
			l = l.makeNoClauseVariant();
			r = r.makeNoClauseVariant();
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
			l = l.makeNoClauseVariant();
			r = r.makeNoClauseVariant();
			if (!(l.equals(r))) {
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
			return type(checkRanks(l,r, (X10ParsedClassType) ts.region()));
		}
		if (op == COND_OR && xts.isDistribution(l)) { // || distribution.union(distribution r)
			if (!(xts.isDistribution(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			// Cannot return l since all the properties of l, e.g. region, may not be preserved.
			return type(checkRanks(l,r, (X10ParsedClassType) ts.distribution()));
		}
		if (op == COND_AND && xts.isRegion(l)) { // && region.intersection(region r)
			if (!(xts.isRegion(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a region operand.", right.position());
			}
			return type(checkRanks(l,r, (X10ParsedClassType) ts.region()));
		}
		if (op == COND_AND && xts.isDistribution(l)) { 
			// && distribution.intersection(distribution r)
			if (!(xts.isDistribution(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			
			return type(checkRanks(l,r, (X10ParsedClassType) ts.distribution()));
		}

		if (op == BIT_OR && xts.isDistributedArray(l)) { 
			// | array.restriction(distribution or region or place)
			if (!(xts.isDistribution(r) || xts.isRegion(r) || xts.isPlace(r))) {
				throw new SemanticException("This " + op +
						" operator instance must have a distribution operand.", right.position());
			}
			X10ParsedClassType lType = (X10ParsedClassType) l;
			X10ParsedClassType type = (X10ParsedClassType) lType.makeVariant();
			//Report.report(1, "X10Binary_c: array | r or p or d, lType = " + lType);
			C_Var rank = lType.rank();
			
			if (rank != null) type.setRank(rank);
			if (xts.isPlace(r) && r instanceof Here) 
				type.setOnePlace(xts.here());
			if (TypeTranslator.isPureTerm(right)) {
                                C_Var v = (C_Var)TypeTranslator.translate(right, xts);
				if (xts.isDistribution(r))
					type.setDistribution(v);
				if (xts.isRegion(r))
					type.setRegion(v);
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
			
			
			X10ParsedClassType lType = (X10ParsedClassType) l;
			X10ParsedClassType type = (X10ParsedClassType) ((X10ParsedClassType) ts.distribution()).makeVariant();
			//Report.report(1, "X10Binary_c: dist | r or p or d, lType = " + lType);
			C_Var rank = lType.rank();
			
			if (rank != null) type.setRank(rank);
			if (xts.isPlace(r) && r instanceof Here) 
				type.setOnePlace(xts.here());
			
			Expr result = type(type);
			//Report.report(1, "X10Binary_c: returning " + result + " of type " + result.type());
			return result;
		}

		if (op == SUB && xts.isDistribution(l)) { //distribution.difference(region r)
			if (!(xts.isRegion(r) || xts.isDistribution(r))) {
				throw new SemanticException("The " + op +
						" operator must have a region or distribution operand.", right.position());
			}
			return type(checkRanks(l,r, (X10ParsedClassType) ts.distribution()));
		}
		if (op == SUB && xts.isRegion(l)) { // region.difference(region r)
			if (!xts.isRegion(r)) {
				throw new SemanticException("The " + op +
						" operator must have a region operands.", right.position());
			}
			return type(checkRanks(l,r, (X10ParsedClassType) ts.region()));
		}
		if ((op == SUB || op == ADD || op == MUL || op == DIV) &&
				xts.isPrimitiveTypeArray(l)) {
			// FIXME: allow strings here
			// pointwise numerical operations. TODO: Check that one type can be numerically coerced to the other.
			if (!xts.equalsWithoutClause(l, r)) {
				throw new SemanticException("The types of operands to " + op
						+ " are " + l + " and " + r + "; these types should be equal.", right.position());
			}
			// vj: Check that they are defined over the same region?
			checkDistributions(l, r, (X10ParsedClassType) ts.distribution());
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

		return super.typeCheck(tc);
	}
	
	
	public X10Type checkRanks(X10Type l, X10Type r, X10ParsedClassType result) throws SemanticException {
		result = (X10ParsedClassType) result.makeVariant();
		X10ParsedClassType lType = (X10ParsedClassType) l;
		X10ParsedClassType rType = (X10ParsedClassType) r;
		
		C_Var lRank = lType.rank();
		C_Var rRank = rType.rank();
		//Report.report(1, "X10Binary_c: entering lRank=" + lRank + " rRank=" + rRank);
		if (lRank==null || ! lRank.equals(rRank)) {
		//	Report.report(1, "X10Binary_c: lRank=" + lRank + " rRank=" + rRank);
			throw new SemanticException("The argument " + left +  " (of type " +  l + " " + l.getClass() + ") has rank " 
					+ lRank + " and " +
					right + " (of type " + r + ") has rank " + rRank + "; these must be equal.");
		}
		
		result.setRank(lRank);
		//Report.report(1, "X10Binary_c: exiting lRank=" + lRank + " rRank=" + rRank);
		return result;
	}
	
	public X10Type checkDistributions(X10Type l, X10Type r, X10ParsedClassType result) throws SemanticException {
		result = (X10ParsedClassType) result.makeVariant();
		X10ParsedClassType lType = (X10ParsedClassType) l;
		X10ParsedClassType rType = (X10ParsedClassType) r;
		
		C_Var lDist = lType.distribution();
		C_Var rDist = rType.distribution();
		//Report.report(1, "X10Binary_c: entering lRank=" + lRank + " rRank=" + rRank);
		if (lDist==null || ! lDist.equals(rDist)) {
		//	Report.report(1, "X10Binary_c: lRank=" + lRank + " rRank=" + rRank);
			throw new SemanticException("The argument " + left +  " (of type " +  l + " " + l.getClass() + ") has distribution " 
					+ lDist + " and " +
					right + " (of type " + r + ") has distribution " + rDist + "; these must be equal.");
		}
		
		result.setDistribution(lDist);
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
		
		final LocalInstance li = ts.localInstance(pos, flags, type, resultVarName.id());
		// Evaluate the left.
		Expr nLeft = (Expr) left.visit(fc);
		final LocalDecl ld = nf.LocalDecl(pos, flags, tn, resultVarName, nLeft).localInstance(li);
		fc.add(ld);
		
		final Local ldRef = (Local) nf.Local(pos,resultVarName).localInstance(li).type(type);
		Flattener newVisitor = (Flattener) new ExprFlattener.Flattener(fc.job(), ts, nf, this).context(xc);
		Expr nRight = (Expr) right.visit(newVisitor);
		List condBody = newVisitor.stmtList(); 
		Expr assign = nf.Assign(pos, ldRef, Assign.ASSIGN, nRight ).type(type);
		Stmt eval = nf.Eval(pos, assign);
		condBody.add(eval);
		final Local ldRef2 = (Local) nf.Local(pos,resultVarName).localInstance(li).type(type);
		
		if (op == Binary.COND_AND) {
			final Stmt ifStm = nf.If(pos, ldRef2, nf.Block(pos, condBody));
			fc.add(ifStm);
		} else {
			Expr cond = nf.Unary(pos, ldRef2, Unary.NOT).type(type);
			final Stmt ifStm = nf.If(pos, cond, nf.Block(pos, condBody));
			fc.add(ifStm);
		}
		
		final Local ldRef3 = (Local) nf.Local(pos,resultVarName).localInstance(li).type(type);
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

