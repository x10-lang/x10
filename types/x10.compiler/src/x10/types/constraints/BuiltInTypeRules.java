package x10.types.constraints;

import polyglot.ast.Expr;
import polyglot.types.Context;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.types.ConstrainedType;
import x10.types.X10TypeMixin;

/**
 * Because the constraint system cannot handle conditional constraints, we build in a few special 
   constraint propagation rules for operators. 
   
   Our goal is to enrich the constraint system so that these rules can be expressed within usual operator definitions
   in X10.
 * @author vj
 *
 */
public class BuiltInTypeRules {

	/**
	 * Because the constraint system cannot handle conditional constraints, we build in a few special 
	 * constraint propagation rules for operators. 
	 * 
	 * For an IntRange left..right, if we can staticaly establish that left is zero, then we assert
	 *  self.isZeroBased and self.rail in the return type.
	 *
	 * class IntRange {
	 *    public static (left:Int) .. (right:Int) : IntRange{self.zeroBased==(left == 0)} {...}
	 * }
	 * @param left
	 * @param right
	 * @param type
	 * @param context
	 * @return
	 */
	public static Type adjustReturnTypeForIntRange(Expr left, Expr right, Type type, Context context) {
	    TypeSystem ts = (TypeSystem) context.typeSystem();
	    Type ltype = left.type();
	    XTerm selfTerm = X10TypeMixin.selfBinding(ltype);
	    if (selfTerm != null && selfTerm.equals(ts.ZERO())) {
	        if (!ts.isUnknown(type)) {
	        	ConstrainedType result = X10TypeMixin.toConstrainedType(type);
	            result = (ConstrainedType) X10TypeMixin.addTerm(type, X10TypeMixin.makeZeroBased(result));
	            result = (ConstrainedType) X10TypeMixin.addTerm(type, X10TypeMixin.makeRail(result));
	            return result;
	        }
	    }
	    return type;
	}

	/**
	 * 
	 * 
	 * For a region mult left*right, we build in that the rank of the result is l+r if we can statically
	 * establish that the rank of left is an value l, and the rank of right is a value r.
	 * 
	 * If both left and right are rect, then we establish that the result is rect.
	 * 
	 * If both left and right are zeroBased, then we establish that the result is zeroBased.
	 * @param left
	 * @param right
	 * @param type
	 * @param context
	 * @return
	 */
	public static Type adjustReturnTypeForRegionMult(Expr left, Expr right, Type type, Context context) {
		TypeSystem ts = (TypeSystem) context.typeSystem();
		ConstrainedType ltype = X10TypeMixin.toConstrainedType(left.type());
		ConstrainedType rtype = X10TypeMixin.toConstrainedType(right.type());
		XTerm lrank = X10TypeMixin.rank(ltype, context);
		XTerm rrank = X10TypeMixin.rank(rtype, context);
		type = X10TypeMixin.toConstrainedType(type);
		XVar selfVar = X10TypeMixin.selfVar((ConstrainedType) type);
		if (lrank instanceof XLit && rrank instanceof XLit) {
			int xr = (Integer) ((XLit) lrank).val();
			int yr = (Integer) ((XLit) rrank).val();
			type = X10TypeMixin.addRank(type, xr+yr);
		}
		if (X10TypeMixin.isRect(ltype, context) && X10TypeMixin.isRect(rtype, context)) {
			type = X10TypeMixin.addRect(type);
		}
		if (X10TypeMixin.isZeroBased(ltype, context) && X10TypeMixin.isZeroBased(rtype, context)) {
			type = X10TypeMixin.addZeroBased(type);
		}
		assert selfVar == X10TypeMixin.selfVar((ConstrainedType) type);
		return type;
	}
	/**
	 * 
	 * @param l
	 * @param r
	 * @param context
	 * @return
	 */
	public static Type adjustReturnTypeForConjunction(Type l, Type r, Context context) {
		TypeSystem xts = (TypeSystem) l.typeSystem();
		Type result = xts.Boolean();
		// Support conjunction of boolean terms.
		// Once we shift to Shostak we will have more comprehensive
		// support for all operators.
		if (l.isBoolean() && r.isBoolean()) {

			XTerm xt = X10TypeMixin.selfBinding(l);
			if (xt != null) {
				XTerm yt = X10TypeMixin.selfBinding(r);
				if (yt != null) {

					try {
						result = X10TypeMixin.addSelfBinding(result, 
								XTerms.makeAnd(xt, yt));
					} catch (XFailure z) {
						X10TypeMixin.setInconsistent(result);
					}
				}
			}
		}
		return result;
	}
}

