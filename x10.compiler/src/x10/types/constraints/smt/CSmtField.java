//package x10.types.constraints.smt;
//
//import java.util.List;
//
//import polyglot.types.Def;
//import polyglot.types.Type;
//import polyglot.types.Types;
//import x10.constraint.XLabeledOp;
//import x10.constraint.XOp;
//import x10.constraint.XVar;
//import x10.constraint.XDef;
//import x10.constraint.smt.XSmtExpr;
//import x10.constraint.smt.XSmtField;
//import x10.constraint.smt.XSmtTerm;
//import x10.types.X10ClassDef;
//import x10.types.X10FieldDef;
//import x10.types.constraints.CField;
//
//public class CSmtField extends XSmtField<Type, XDef<Type>> implements CField {
//	CSmtField(XDef<Type> d, XSmtTerm<Type> receiver) {
//		super(d, receiver,d.resultType());
//	}
//	CSmtField(XDef<Type> d, XSmtTerm<Type> receiver, boolean hidden) {
//		super(d, receiver,d.resultType(), hidden);
//	}
//	
//
//	@Override
//	public String toString() {
//		return get(0) + "." + field();
//	}
//
//}
