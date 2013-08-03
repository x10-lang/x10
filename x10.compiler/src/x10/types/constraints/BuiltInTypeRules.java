package x10.types.constraints;

import polyglot.ast.Expr;
import polyglot.types.Context;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import x10.constraint.XFailure;
import x10.constraint.XLit;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.types.ConstrainedType;

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
     *  self.isZeroBased in the return type.
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
        XTerm selfTerm = Types.selfBinding(ltype);
        if (selfTerm != null && selfTerm.equals(ts.ZERO())) {
            if (!ts.isUnknown(type)) {
                ConstrainedType result = Types.toConstrainedType(type);
                XTerm zb = result.makeZeroBased();
                if (zb != null) result = (ConstrainedType) Types.addTerm(result, zb);
                result=result.addNonNull();
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
    public static ConstrainedType adjustReturnTypeForRegionMult(Expr left, Expr right, Type type, Context context) {
        TypeSystem ts =  context.typeSystem();
        ConstrainedType ltype = Types.toConstrainedType(left.type());
        ConstrainedType rtype = Types.toConstrainedType(right.type());
        XTerm lrank = ltype.rank(context);
        XTerm rrank = rtype.rank(context);
        ConstrainedType ct = Types.toConstrainedType(type);
        XVar selfVar = ct.selfVar();

        if (lrank instanceof XLit && rrank instanceof XLit) {
            long xr = (Long) ((XLit) lrank).val();
            long yr = (Long) ((XLit) rrank).val();
            ct = ct.addRank(xr+yr);
        }
        if (ltype.isRect(context) && rtype.isRect(context)) ct = ct.addRect();
        if (ltype.isZeroBased(context) && rtype.isZeroBased(context)) ct = ct.addZeroBased();
        assert selfVar == ct.selfVar();
        ct=ct.addNonNull();
        return ct;
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
    public static ConstrainedType adjustReturnTypeForRangeRangeMult(Expr left, Expr right, Type type, Context context) {
        TypeSystem ts =  context.typeSystem();
        ConstrainedType ltype = Types.toConstrainedType(left.type());
        ConstrainedType rtype = Types.toConstrainedType(right.type());
        ConstrainedType ct = Types.toConstrainedType(type);
        XVar selfVar = ct.selfVar();

        ct = ct.addRank(2);
        ct = ct.addRect();
        if (ltype.isZeroBased(context) && rtype.isZeroBased(context)) ct = ct.addZeroBased();
        assert selfVar == ct.selfVar();
        ct=ct.addNonNull();
        return ct;
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
        /*if (l.isBoolean() && r.isBoolean()) {

			XTerm xt = Types.selfBinding(l);
			if (xt != null) {
				XTerm yt = Types.selfBinding(r);
				if (yt != null) {
				    result = Types.addSelfBinding(result, XTerms.makeAnd(xt, yt));
				}
			}
		}*/

        return result;
    }
}

