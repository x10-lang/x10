package x10.types;

import java.io.Serializable;

import polyglot.types.Type;
import polyglot.util.Copy;
import x10.constraint.XRoot;
import x10.constraint.XTerm;

public interface SubtypeConstraint extends Copy, Serializable {

    boolean isEqualityConstraint();

    Type subtype();
    Type supertype();

    SubtypeConstraint subst(XTerm y, XRoot x);

}