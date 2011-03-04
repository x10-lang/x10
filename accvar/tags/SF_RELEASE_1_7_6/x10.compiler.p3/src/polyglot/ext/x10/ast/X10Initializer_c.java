package polyglot.ext.x10.ast;

import polyglot.ast.Block;
import polyglot.ast.FlagsNode;
import polyglot.ast.Initializer_c;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10InitializerDef;
import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;

public class X10Initializer_c extends Initializer_c {

    public X10Initializer_c(Position pos, FlagsNode flags, Block body) {
        super(pos, flags, body);
    }

    public InitializerDef createInitializerDef(TypeSystem ts, ClassDef ct, Flags flags) {
        X10InitializerDef ii;
        ii = (X10InitializerDef) super.createInitializerDef(ts, ct , flags);
        ii.setThisVar(((X10ClassDef) ct).thisVar());
        return ii;
    }

}
