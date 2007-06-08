package bits.plugin;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.IntLit;
import polyglot.ast.Node;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10Instanceof;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.plugin.SimpleTypeAnnotationPlugin;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeObject;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Lit;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;

public class BitsTypePlugin extends SimpleTypeAnnotationPlugin {

	public BitsTypePlugin() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Expr checkImplicitCoercion(X10Type toType, Expr e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10Type lhsType = toType;
		Expr rhs = e;
		Position pos = e.position();
		
		if (rhs == null) {
			return e;
		}

		if (! lhsType.isLongOrLess()) {
			return e;
		}
		
		int maxBits = bitsInType(lhsType);
		
		X10ClassType at = toType.annotationNamed("Bits");

		if (at == null) {
			return e;
		}
		
		int bits = getBitsFromAnnotation(at, maxBits);
		
		X10ClassType rhsAT = ((X10Ext) rhs.ext()).annotationNamed("Bits");
		
		if (rhsAT != null) {
			// OK!
			int rbits = getBitsFromAnnotation(rhsAT, 64);
			if (rbits > bits) {
				throw new SemanticException("Cannot assign to Bits(" + bits + ") variable; unknown width.", pos);
			}
		}			
		else if (rhs.isConstant()) {
			if (rhs.type().isLongOrLess()) {
				long x = ((Number) rhs.constantValue()).longValue();
				long mask = (0xffffffffffffffffL << bits);
				if ((x & mask) != 0L) {
					throw new SemanticException("Cannot assign to Bits(" + bits + ") variable; too wide.", pos);
				}
			}
			else {
				throw new SemanticException("Cannot assign to Bits(" + bits + ") variable; unknown width.", pos);
			}
		}
		
		return e;
	}

	@Override
	protected Expr rewriteCast(X10Cast e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10Type toType = (X10Type) e.castType();
		
		if (e == null) {
			return e;
		}
		
		if (! toType.isLongOrLess()) {
			return e;
		}
		
		int bits = bitsInType(toType);

		long mask = ~(~0L << bits);
		
		if (e.type().isLong()) {
			Expr maskExpr = nf.IntLit(e.position(), IntLit.LONG, mask).type(ts.Long());
			Expr and = nf.Binary(e.position(), e.expr(), Binary.BIT_AND, maskExpr).type(ts.Long());
			return e.expr(and);
		}
		else if (e.type().isIntOrLess()) {
			Expr maskExpr = nf.IntLit(e.position(), IntLit.INT, mask).type(ts.Int());
			Expr and = nf.Binary(e.position(), e.expr(), Binary.BIT_AND, maskExpr).type(ts.Int());
			return e.expr(and);
		}
		
		return e;
	}

	public int bitsInType(X10Type toType) throws SemanticException {
		int maxBits = 64;
		
		if (toType.isInt()) {
			maxBits = 32;
		}
		else if (toType.isShort() || toType.isChar()) {
			maxBits = 16;
		}
		else if (toType.isByte()) {
			maxBits = 8;
		}
		
		X10ClassType at = toType.annotationNamed("Bits");
		
		if (at == null)
			return maxBits;
		
		int bits = getBitsFromAnnotation(at, maxBits);
		
		return bits;
	}

	@Override
	protected Expr rewriteInstanceof(X10Instanceof e, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		X10Type toType = (X10Type) e.compareType();
		
		if (e == null) {
			return e;
		}
		
		if (! toType.isLongOrLess()) {
			return e;
		}
		
		int bits = bitsInType(toType);

		long mask = (~0L << bits);
		
		if (e.type().isLong()) {
			Expr maskExpr = nf.IntLit(e.position(), IntLit.LONG, mask).type(ts.Long());
			Expr zero = nf.IntLit(e.position(), IntLit.LONG, 0).type(ts.Long());
			Expr and = nf.Binary(e.position(), e.expr(), Binary.BIT_AND, maskExpr).type(ts.Long());
			Expr eq = nf.Binary(e.position(), and, Binary.EQ, zero);
			return e.expr(and);
		}
		else if (e.type().isIntOrLess()) {
			Expr maskExpr = nf.IntLit(e.position(), IntLit.INT, mask).type(ts.Int());
			Expr zero = nf.IntLit(e.position(), IntLit.INT, 0).type(ts.Int());
			Expr and = nf.Binary(e.position(), e.expr(), Binary.BIT_AND, maskExpr).type(ts.Int());
			Expr eq = nf.Binary(e.position(), and, Binary.EQ, zero);
			return e.expr(and);
		}
		
		return e;
	}

	protected int getBitsFromAnnotation(X10ClassType at, int maxBits) throws SemanticException {
		DepParameterExpr dep = at.dep();
		if (dep.args().size() == 1) {
			Expr lhsBits = (Expr) dep.args().get(0);
			if (lhsBits instanceof IntLit) {
				IntLit lhsLit = (IntLit) lhsBits;
				Object val = lhsLit.constantValue();
				if (val instanceof Number) {
					int bits = ((Number) val).intValue();
					
					if (bits < 1 || bits > maxBits) {
						throw new SemanticException("bits property must be between 1 and " + maxBits + ".");
					}
					
					return bits;
				}
			}
		}
		
		throw new SemanticException("bits property must be an integer literal.");
	}
	
}
