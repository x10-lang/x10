package x10.types;

import java.io.Serializable;

import polyglot.types.Type;
import polyglot.util.Copy;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

public interface SubtypeConstraint extends Copy, Serializable {

	static int SUBTYPE_KIND=0; // <:
	static int EQUAL_KIND =1;  // ==
	
	
    boolean isEqualityConstraint();
    boolean isSubtypeConstraint();
    boolean isKind(int kind);

    Type subtype();
    Type supertype();

    SubtypeConstraint subst(XTerm y, XRoot x);

}