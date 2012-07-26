package x10.types.constraints.smt;

import java.util.List;

import polyglot.ast.IntLit.Kind;
import polyglot.types.Def;
import polyglot.types.Type;
import x10.constraint.XDef;
import x10.constraint.XExpr;
import x10.constraint.XLit;
import x10.constraint.XLocal;
import x10.constraint.XOp;
import x10.constraint.XTerm;
import x10.constraint.XVar;
import x10.constraint.smt.XSmtConstraintSystem;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraintSystem;
import x10.types.constraints.CField;
import x10.types.constraints.CSelf;
import x10.types.constraints.CThis;
import x10.types.constraints.XTypeLit;

public class CSmtConstraintSystem extends XSmtConstraintSystem<Type> implements CConstraintSystem {

	@Override
	public XExpr<Type> makeExpr(XOp<Type> op, XTerm<Type> t1, XTerm<Type> t2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XExpr<Type> makeExpr(XOp<Type> op, XTerm<Type> t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CSelf makeSelf(Type t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CThis makeThis(Type t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CField makeField(XTerm<Type> receiver, XDef<Type> field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CField makeFakeField(XTerm<Type> receiver, XDef<Type> field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <D extends XDef<Type>> XLocal<Type, D> makeLocal(D ld, String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XExpr<Type> makeApply(Def md, List<XTerm<Type>> t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XExpr<Type> makeOpaque(Object op, boolean isatom,
			XTerm<Type> target, List<XTerm<Type>> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> XLit<Type, V> makeLit(V val, Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XLit<Type, Object> makeZero(Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTypeLit makeTypeLit(Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Kind getIntLitKind(Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CConstraint makeCConstraint(Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CConstraint makeCConstraint(XVar<Type> self) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CField makeFakeField(XTerm<Type> receiver, XOp<Type> op) {
		// TODO Auto-generated method stub
		return null;
	}

}
