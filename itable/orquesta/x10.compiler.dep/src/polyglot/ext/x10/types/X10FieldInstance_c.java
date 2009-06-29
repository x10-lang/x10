/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.constr.C_Field;
import polyglot.ext.x10.types.constr.C_Field_c;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Type_c;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.types.DerefTransform;
import polyglot.types.FieldInstance_c;
import polyglot.types.Flags;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TransformingList;

/**
 * An implementation of PropertyInstance
 * @author vj
 *
 */
public class X10FieldInstance_c extends FieldInstance_c implements X10FieldInstance {

    public X10FieldInstance_c(TypeSystem ts, Position pos, Ref<? extends X10FieldDef> def) {
        super(ts, pos, def);
    }
    
    public X10FieldDef x10Def() {
        return (X10FieldDef) def();
    }
    
    public List<X10ClassType> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    public List<X10ClassType> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }

    public boolean isProperty() {
        return x10Def().isProperty();
    }
    
    public Type type() {
        X10TypeSystem xts = (X10TypeSystem) ts;
        
        Type t = super.type();
        
        if (! (t instanceof X10Type)) {
            return t;
        }

        // If the local variable is final, replace T by T(:self==t). 
        Flags flags = flags();

        if (flags.isFinal()) {
            Constraint rc = X10TypeMixin.depClause((X10Type) t);
            if (rc == null) rc = new Constraint_c(xts);

            try {
                C_Var receiver;
                
                if (flags.isStatic()) {
                    receiver = new C_Type_c((X10Type) container());
                }
                else {
                	receiver = new C_Special_c(X10Special.THIS, (X10Type) container());
                }
                
                // ### pass in the type rather than letting C_Field call fi.type();
                // otherwise, we'll get called recursively.
                C_Field self = new C_Field_c(t, this, receiver);

                Constraint c = Constraint_c.addSelfBinding(self, rc, xts);

                return X10TypeMixin.depClauseDeref((X10Type) t, c);
            }
            catch (Failure f) {
                throw new InternalCompilerError("Could not add self binding.", f);
            }
        }
        
        ReferenceType container = container();
        
        // HACK!
        if (name().equals("UNIQUE") && container.typeEquals(xts.distribution()) && flags.isStatic()) {
            X10ParsedClassType ud = (X10ParsedClassType) t;
            ud = ud.setUniqueDist();
            ud = ud.setRail();
            return ud;
        }        

        return t;
    }
    
    
}
