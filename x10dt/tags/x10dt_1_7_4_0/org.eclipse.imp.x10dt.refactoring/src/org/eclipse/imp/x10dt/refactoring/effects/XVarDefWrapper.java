/**
 * 
 */
package org.eclipse.imp.x10dt.refactoring.effects;

import polyglot.ast.NamedVariable;
import polyglot.types.VarDef;
import x10.constraint.XName;

public class XVarDefWrapper implements XName {
    private final VarDef fVarDef;
    public XVarDefWrapper(VarDef vd) {
        fVarDef= vd;
    }
    public XVarDefWrapper(NamedVariable var) {
        this((VarDef) var.varInstance().def());
    }
    @Override
    public int hashCode() {
        return fVarDef.name().toString().hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof XVarDefWrapper)) return false;
        XVarDefWrapper other= (XVarDefWrapper) obj;
        if (fVarDef == other.fVarDef) return true;
        // Probably should never need the following...
        if (!fVarDef.position().equals(other.fVarDef.position()))
            return false;
        if (!fVarDef.name().toString().equals(other.fVarDef.name().toString()))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return fVarDef.name().toString();
    }
}