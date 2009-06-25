package polyglot.ext.x10.types;

import polyglot.types.FieldDef;

public interface X10FieldDef extends FieldDef, X10Def {
    boolean isProperty();
    void setProperty();
}
