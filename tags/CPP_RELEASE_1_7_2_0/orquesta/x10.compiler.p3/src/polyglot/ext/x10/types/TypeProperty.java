package polyglot.ext.x10.types;

import polyglot.types.MemberDef;
import polyglot.types.Name;
import polyglot.types.Type;
import x10.constraint.XVar;

public interface TypeProperty extends MemberDef {
	enum Variance {
		CONTRAVARIANT, INVARIANT, COVARIANT
	};
	
	Variance variance();
	void setVariance(Variance variance);
	
	Name name();
	void setName(Name name);
	
	PathType asType();
	XVar asVar();
}
