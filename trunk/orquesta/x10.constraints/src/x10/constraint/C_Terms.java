package x10.constraint;

public class C_Terms {
	public static final C_Lit NULL = new C_Lit_c(null);
	public static final C_Lit TRUE = new C_Lit_c(true);
	public static final C_Lit FALSE = new C_Lit_c(false);
	public static final C_Lit OPERATOR = new C_Lit_c(new Object()) { public String toString() { return "o"; } };

	static final C_Name equalsName = new C_NameWrapper<String>("===");
	static final C_Name andName = new C_NameWrapper<String>("&&&");
	static final C_Name notName = new C_NameWrapper<String>("!!!");

	public static final C_Local makeLocal(C_Name name) {
		return new C_Local_c(name);
	}

	public static final C_Field makeField(C_Var receiver, C_Name field) {
		return new C_Field_c(receiver, field);
	}
	
	public static final C_Lit makeLit(Object o) {
		if (o == null) return NULL;
		if (o.equals(true)) return TRUE;
		if (o.equals(false)) return FALSE;
		return new C_Lit_c(o);
	}
	
	public static C_Term makeAtom(C_Name op, C_Term... terms) {
		assert op != null;
		assert terms != null;
		C_Formula f = new C_Formula_c(op, terms);
		f.markAsAtomicFormula();
		return f;
	}

	public static C_Term makeEquals(C_Term left, C_Term right) {
		assert left != null;
		assert right != null;
		return new C_Equals_c(left, right);
	}

	public static C_Term makeAnd(C_Term left, C_Term right) {
		assert left != null;
		assert right != null;
		return new C_And_c(left, right);
	}

	public static C_Term makeNot(C_Term arg) {
		assert arg != null;
		return new C_Not_c(arg);
	}
}
