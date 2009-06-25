/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.X10MethodInstance_c.NoClauseVariant;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.*;
import polyglot.util.Position;
import polyglot.util.TransformingList;

public class ClosureInstance_c extends FunctionInstance_c<ClosureDef> implements ClosureInstance {
    private static final long serialVersionUID= 2804222307728697502L;

    public ClosureInstance_c(TypeSystem ts, Position pos, Ref<? extends ClosureDef> def) {
        super(ts, pos, def);
    }

    public ClosureType type() {
        return new ClosureType_c(ts, position(), def().returnType(), def().formalTypes(), def().throwTypes());
    }
    
    public CodeInstance<?> methodContainer() {
        return Types.get(def().methodContainer());
    }

    public ClassType typeContainer() {
        return Types.get(def().typeContainer());
    }

    public boolean closureCallValid(List<Type> actualTypes) {
        return callValid(actualTypes);
    }
    
    public boolean callValid(List<Type> actualTypes) {
        return X10MethodInstance_c.callValidImpl(this, actualTypes);
    }

    public String signature() {
        return def().signature();
    }

    public String designator() {
        return def().designator();
    }

    public String toString() {
	return designator() + " " + this.returnType() + " " + signature();
    }

    @Override
    public ClosureInstance returnType(Type returnType) {
        return (ClosureInstance) super.returnType(returnType);
    }
    
    @Override
    public ClosureInstance formalTypes(List<Type> formalTypes) {
        return (ClosureInstance) super.formalTypes(formalTypes);
    }
    
    @Override
    public ClosureInstance throwTypes(List<Type> throwTypes) {
        return (ClosureInstance) super.throwTypes(throwTypes);
    }

    public boolean callValidNoClauses(List<Type> argTypes) {
        ClosureInstance me = this.formalTypes(new TransformingList<Type,Type>(this.formalTypes(), new NoClauseVariant()));
        return me.callValid(new TransformingList<Type,Type>(argTypes, new NoClauseVariant()));
    }

    Constraint whereClause;
    
    public Constraint whereClause() {
        return whereClause;
    }
    
    public ClosureInstance whereClause(Constraint where) {
        ClosureInstance_c n = (ClosureInstance_c) copy();
        n.whereClause = where;
        return n;
    }
}

