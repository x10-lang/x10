package x10.constraint.bapat;

public class TranslateVar extends BinaryFormulaVar {
	
	public TranslateVar(Var left, Var right) {
		super(left, right);
	}

	@Override
	public String toLongString() {
		return left.toLongString() + " + " + right.toLongString();
	}

	@Override
	public String toString() {
		return left.toString() + " + " + right.toString();
	}

}
