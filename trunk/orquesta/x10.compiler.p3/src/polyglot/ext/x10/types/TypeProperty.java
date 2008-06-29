package polyglot.ext.x10.types;

import polyglot.types.MemberDef;
import polyglot.types.Type;
import x10.constraint.XVar;

public interface TypeProperty extends MemberDef {
	enum Variance {
		CONTRAVARIANT, INVARIANT, COVARIANT
	};
	
	Variance variance();
	void setVariance(Variance variance);
	
	String name();
	void setName(String name);
	
	PathType asType();
	XVar asVar();
}
