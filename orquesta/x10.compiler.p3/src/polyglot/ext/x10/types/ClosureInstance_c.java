/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.X10MethodInstance_c.NoClauseVariant;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.DerefTransform;
import polyglot.types.FunctionInstance_c;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TransformingList;
import x10.constraint.XConstraint;

public class ClosureInstance_c extends FunctionInstance_c<ClosureDef> implements ClosureInstance {
    private static final long serialVersionUID= 2804222307728697502L;

    public ClosureInstance_c(TypeSystem ts, Position pos, Ref<? extends ClosureDef> def) {
        super(ts, pos, def);
    }
    
    public ClosureDef x10Def() {
	    return def();
    }
    
    ClosureType type;

    public ClosureType type() {
	    X10TypeSystem xts = (X10TypeSystem) ts;
	    if (type == null)
		    type = xts.closure(position(), def().returnType(), def().typeParameters(), def().formalTypes(), def().whereClause(), def().throwTypes());
	    return type;
    }
    
    public CodeInstance<?> methodContainer() {
        return Types.get(def().methodContainer());
    }

    public ClassType typeContainer() {
        return Types.get(def().typeContainer());
    }

    public boolean closureCallValid(List<Type> actualTypes) {
        return callValid(type(), actualTypes);
    }
    
    public boolean callValid(Type thisType, List<Type> actualTypes) {
        return X10MethodInstance_c.callValidImpl(this, thisType, actualTypes);
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

    public boolean callValidNoClauses(Type thisType, List<Type> argTypes) {
        ClosureInstance me = this.formalTypes(new TransformingList<Type,Type>(this.formalTypes(), new NoClauseVariant()));
        return me.callValid(thisType, new TransformingList<Type,Type>(argTypes, new NoClauseVariant()));
    }

    XConstraint whereClause;
    
    public XConstraint whereClause() {
        return whereClause;
    }
    
    public ClosureInstance whereClause(XConstraint where) {
        ClosureInstance_c n = (ClosureInstance_c) copy();
        n.whereClause = where;
        return n;
    }

    public List<Type> typeParameters;

    public List<Type> typeParameters() {
	    if (this.typeParameters == null) {
		    this.typeParameters = new TransformingList<Ref<? extends Type>, Type>(x10Def().typeParameters(), new DerefTransform<Type>());
	    }

	    return typeParameters;
    }

    public ClosureInstance typeParameters(List<Type> typeParameters) {
	    ClosureInstance_c n = (ClosureInstance_c) copy();
	    n.typeParameters = typeParameters;
	    return n;
    }
}

