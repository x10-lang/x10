package polyglot.ext.x10.types;

import polyglot.ext.x10.types.constr.C_Var;
import polyglot.types.MemberDef;
import polyglot.types.Type;

public interface TypeProperty extends MemberDef {
	enum Variance {
		CONTRAVARIANT, INVARIANT, COVARIANT
	};
	
	Variance variance();
	void setVariance(Variance variance);
	
	String name();
	void setName(String name);
	
	Type asType();
	C_Var asVar();
}
