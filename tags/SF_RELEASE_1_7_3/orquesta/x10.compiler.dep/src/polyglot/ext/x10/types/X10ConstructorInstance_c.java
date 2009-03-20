/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ConstructorInstance;
import polyglot.types.ConstructorInstance_c;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TransformingList;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10ConstructorInstance_c extends ConstructorInstance_c implements X10ConstructorInstance {

    public X10ConstructorInstance_c(TypeSystem ts, Position pos, Ref<? extends X10ConstructorDef> def) {
        super(ts, pos, def);
    }
    
    public X10ConstructorDef x10Def() {
        return (X10ConstructorDef) def();
    }
    
    public List<X10ClassType> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }

    public List<X10ClassType> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }

    /* (non-Javadoc)
     * @see polyglot.ext.x10.types.X10ConstructorInstance#depClause()
     */
    public Constraint constraint() { return X10TypeMixin.realClause(returnType()); }

    public X10ClassType returnType;
    
    public X10Type returnType() { 
        if (returnType == null) {
            returnType = (X10ClassType) x10Def().returnType().get();
        }
	return returnType;
    }
    
    public X10ConstructorInstance returnType(X10ClassType retType) {
        X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
        n.returnType = retType;
        return n;
    }

    /** Constraint on superclass constructor call return type. */
    public Constraint supClause() { 
        return Types.get(x10Def().supClause());
        }

    Constraint whereClause;
    
    /** Constraint on formal parameters. */
    public Constraint whereClause() {
        if (whereClause == null) 
            whereClause = Types.get(x10Def().whereClause());
        return whereClause;
    }

    public X10ConstructorInstance whereClause(Constraint c) {
        X10ConstructorInstance_c n = (X10ConstructorInstance_c) copy();
        n.whereClause = c;
        return n;
    }

    public boolean callValidNoClauses(List<Type> argTypes) {
        X10ConstructorInstance_c me = (X10ConstructorInstance_c) this.formalTypes(new TransformingList<Type,Type>(this.formalTypes(), new X10MethodInstance_c.NoClauseVariant()));
        return me.superCallValid(new TransformingList<Type,Type>(argTypes, new X10MethodInstance_c.NoClauseVariant()));
    }
    
    protected boolean superCallValid(List<Type> argTypes) {
        return super.callValid(argTypes);
    }
    
    @Override
    public boolean callValid(List<Type> argTypes) {
        return X10MethodInstance_c.callValidImpl(this, argTypes);
    }
}
