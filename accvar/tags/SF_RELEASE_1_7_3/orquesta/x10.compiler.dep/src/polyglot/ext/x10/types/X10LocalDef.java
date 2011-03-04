package polyglot.ext.x10.types;

import polyglot.types.LocalDef;

public interface X10LocalDef extends LocalDef, X10Def {
    int positionInArgList();
    void setPositionInArgList(int pos);
}
