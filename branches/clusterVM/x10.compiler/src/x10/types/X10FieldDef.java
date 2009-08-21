/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import polyglot.types.FieldDef;
import polyglot.types.Ref;
import x10.constraint.XConstraint;

public interface X10FieldDef extends FieldDef, X10Def, X10MemberDef {
    boolean isProperty();
    void setProperty();
}
