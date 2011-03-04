package polyglot.ext.x10.ast;

import polyglot.ast.Ext;
import polyglot.ext.jl.ast.AbstractExtFactory_c;
import polyglot.ext.x10.extension.X10BinaryExt_c;
import polyglot.ext.x10.extension.X10CastExt_c;
import polyglot.ext.x10.extension.X10Ext_c;
import polyglot.ext.x10.extension.X10InstanceofExt_c;

/**
 * ExtFactory for pao extension.
 */
public class X10ExtFactory_c extends AbstractExtFactory_c  {
    public X10ExtFactory_c() {
        super();
    }

    public Ext extNodeImpl() {
        return new X10Ext_c();
    }

    public Ext extInstanceofImpl() {
        return new X10InstanceofExt_c();
    }

    public Ext extCastImpl() {
        return new X10CastExt_c();
    }

    public Ext extBinaryImpl() {
        return new X10BinaryExt_c();
    }
}
