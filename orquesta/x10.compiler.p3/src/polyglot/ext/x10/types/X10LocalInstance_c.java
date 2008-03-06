/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.types.constr.C_Local_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.types.DerefTransform;
import polyglot.types.Flags;
import polyglot.types.LocalInstance_c;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TransformingList;

/**
 * @author vj
 *
 */
public class X10LocalInstance_c extends LocalInstance_c implements X10LocalInstance {

    public X10LocalInstance_c(TypeSystem ts, Position pos, Ref<? extends X10LocalDef> def) {
        super(ts, pos, def);
    }
    
    public X10LocalDef x10Def() {
        return (X10LocalDef) def();
    }
    
    public List<X10ClassType> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    public List<X10ClassType> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }

    public int positionInArgList() {
        return x10Def().positionInArgList();
    }
    
    public Type type() {
        // If the local variable is final, replace T by T(:self==t), 
        // do this even if depclause==null
        Flags flags = flags();

        if (flags.isFinal()) {
            X10Type t = (X10Type) super.type();
            try {
                Constraint c = Constraint_c.addSelfBinding(C_Local_c.makeSelfVar(x10Def()), X10TypeMixin.depClause(t), (X10TypeSystem) ts);
                return X10TypeMixin.depClauseDeref(t, c);
            }
            catch (Failure f) {
                throw new InternalCompilerError("Could not add self binding.", f);
            }
        }
        
        return super.type();
    }
}
