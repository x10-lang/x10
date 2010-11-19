package x10.visit;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;
import x10.types.X10ClassDef;
import x10.types.constraints.TypeConstraint;

public class X10TypeBuilder extends TypeBuilder {

    public X10TypeBuilder(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    protected ClassDef createAnonClass(Position pos) {
        X10ClassDef def = (X10ClassDef) super.createAnonClass(pos);

        TypeSystem ts = typeSystem();

        def.setThisDef(typeSystem().thisDef(pos, Types.ref(def.asType())));
        def.setTypeBounds(Types.ref(new TypeConstraint()));

        return def;
    }

}
