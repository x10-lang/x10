package x10.types;

import x10.constraint.XRoot;
import x10.types.constraints.XConstrainedTerm;

public interface X10MemberDef extends X10Def {
    XRoot thisVar();
    void setThisVar(XRoot thisVar);
}
