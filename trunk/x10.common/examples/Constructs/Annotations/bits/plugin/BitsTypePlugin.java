package bits.plugin;

import java.util.List;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.IntLit;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10Instanceof;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.ext.x10.plugin.SimpleTypeAnnotationPlugin;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.util.Position;

public class BitsTypePlugin extends SimpleTypeAnnotationPlugin {

	public BitsTypePlugin() {
		super();
	}

	@Override
	public Expr checkImplicitCoercion(X10Type toType, Expr fromExpr, X10Context context, X10TypeSystem ts, X10NodeFactory nf) throws SemanticException {
		Position pos = fromExpr.position();
		
		if (fromExpr == null) {
			return fromExpr;
		}

		if (! toType.isLongOrLess()) {
			return fromExpr;
		}
		
		int maxBits = bitsInType(toType);
		
		X10ClassType bitsType = (X10ClassType) ts.systemResolver().find("bits.Bits");
		List<X10ClassType> toATs = toType.annotationMatching(bitsType);

		if (toATs.isEmpty()) {
			return fromExpr;
		}

		X10ClassType toAT = toATs.get(0);
		
		int bits = getBitsFromAnnotation(toAT, maxBits);
		
		List<X10ClassType> fromATs = ((X10Type) fromExpr.type()).annotationMatching(bitsType);
		
		if (! fromATs.isEmpty()) {
			// OK!
			int rbits = getBitsFromAnnotation(fromATs.get(0), 64);
			if (rbits > bits) {
				throw new SemanticException("Cannot assign to Bits(" + bits + ") variable; unknown width.", pos);
			}
		}			
		else if (fromExpr.isConstant()) {
			if (fromExpr.type().isLongOrLess()) {
				long x = ((Number) fromExpr.constantValue()).longValue();
				long mask = (0xffffffffffffffffL << bits);
				if ((x & mask) != 0L) {
					throw new SemanticException("Cannot assign to Bits(" + bits + ") variable; too wide.", pos);
				}
			}
			else {
				throw new SemanticException("Cannot assign to Bits(" + bits + ") variable; unknown width.", pos);
			}
		}
		
		return fromExpr;
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
		
		X10ClassType bitsType = (X10ClassType) toType.typeSystem().systemResolver().find("bits.Bits");
		List<X10ClassType> toATs = toType.annotationMatching(bitsType);

		if (toATs.isEmpty()) {
			return maxBits;
		}

		X10ClassType at = toATs.get(0);
		
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
		Expr lhsBits = at.propertyExpr(0);
		if (lhsBits.isConstant()) {
			Object val = lhsBits.constantValue();
			if (val instanceof Integer || val instanceof Long) {
				long bits = ((Number) val).longValue();
				
				if (bits < 1l || bits > maxBits) {
					throw new SemanticException("Bits property must be between 1 and " + maxBits + ".");
				}
				
				return (int) bits;
			}
		}
		
		throw new SemanticException("Bits property must be an integer constant.");
	}
	
}
