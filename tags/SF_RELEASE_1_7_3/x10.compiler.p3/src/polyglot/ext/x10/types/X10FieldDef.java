/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.types;

import polyglot.types.FieldDef;

public interface X10FieldDef extends FieldDef, X10Def, X10MemberDef {
    boolean isProperty();
    void setProperty();
}
