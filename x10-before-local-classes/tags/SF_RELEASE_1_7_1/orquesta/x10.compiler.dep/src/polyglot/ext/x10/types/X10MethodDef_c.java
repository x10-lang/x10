/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.types.constr.C_Special;
import polyglot.ext.x10.types.constr.C_Special_c;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.main.Report;
import polyglot.types.Flags;
import polyglot.types.MethodDef;
import polyglot.types.MethodDef_c;
import polyglot.types.MethodInstance;
import polyglot.types.MethodInstance_c;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.util.Position;
import polyglot.util.TypedList;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10MethodDef_c extends MethodDef_c implements X10MethodDef {
    protected Ref<? extends Constraint> whereClause;

    public X10MethodDef_c(TypeSystem ts, Position pos,
            Ref<? extends ReferenceType> container,
            Flags flags, 
            Ref<? extends Type> returnType,
            String name,
            List<Ref<? extends Type>> formalTypes, List<Ref<? extends Type>> excTypes) {
        super(ts, pos, container, flags, returnType, name, formalTypes, excTypes);
    }

    // BEGIN ANNOTATION MIXIN
    List<Ref<? extends X10ClassType>> annotations;

    public List<Ref<? extends X10ClassType>> defAnnotations() {
        return Collections.unmodifiableList(annotations);
    }
    
    public void setDefAnnotations(List<Ref<? extends X10ClassType>> annotations) {
        this.annotations = TypedList.<Ref<? extends X10ClassType>>copyAndCheck(annotations, Ref.class, true);
    }
    
    public List<X10ClassType> annotations() {
        return X10TypeObjectMixin.annotations(this);
    }
    
    public List<X10ClassType> annotationsMatching(Type t) {
        return X10TypeObjectMixin.annotationsMatching(this, t);
    }
    
    public List<X10ClassType> annotationsNamed(String fullName) {
        return X10TypeObjectMixin.annotationsNamed(this, fullName);
    }
    // END ANNOTATION MIXIN
    
    /** Constraint on formal parameters. */
    public Ref<? extends Constraint> whereClause() {
        return whereClause;
    }

    public void setWhereClause(Ref<? extends Constraint> s) {
        this.whereClause = s;
    }
	
    public String signature() {
        return returnType.toString() + " " + container.toString() + "."
        + name + "(" + X10TypeSystem_c.listToString(formalTypes) + 
        (whereClause != null ? ": " + whereClause.toString() : "") + ")";
    }

    @Override
    public MethodInstance asInstance() {
        if (asInstance == null) {
            asInstance = new X10MethodInstance_c(ts, position(), Types.<X10MethodDef>ref(this));
        }
        return asInstance;
    }
    

    public X10MethodInstance instantiateForThis(ReferenceType t) throws SemanticException {
        X10MethodInstance_c proto = (X10MethodInstance_c) asInstance();
        X10Type thisType = (X10Type) t;
        
        boolean needed = false;
        Type rt = Types.get(returnType());
        if (rt instanceof UnknownType) throw new SemanticException();
        X10Type retType = (X10Type) rt;
        X10Type newRetType = retType;
        Constraint rc = X10TypeMixin.realClause(retType);
        C_Special THIS = C_Special_c.This;
        C_Var selfVar = X10TypeMixin.selfVar(thisType);

        for (Ref<? extends Type> tref : formalTypes()) {
            X10Type type = (X10Type) tref.get();
            rc = X10TypeMixin.realClause(type);
            if (rc != null)
                needed = rc.hasVar(THIS);
            if (needed) break;
        }

        // Set flag to true if assertions enabled
        boolean assertionsEnabled = false;
        assert assertionsEnabled = true;
        
        if (assertionsEnabled) {
            for (Ref<? extends Type> tref : throwTypes()) {
                X10Type type = (X10Type) tref.get();
                rc = X10TypeMixin.realClause(type);
                assert ! rc.hasVar(THIS);
            }
        }

        if (!needed)
            return proto;
        
        List<Type> newFormalTypes = new ArrayList<Type>(formalTypes.size());
        for (Ref<? extends Type> tref : formalTypes()) {
            X10Type type = (X10Type) tref.get();
            rc = X10TypeMixin.realClause(type);
            Type newType = type;
            if (rc!=null && rc.hasVar(THIS)) {
                C_Var myVar = selfVar;
                if (myVar == null)
                    myVar = rc.genEQV(thisType, true); // not thisType for the args.
                try {
                    Constraint rc2 = rc.substitute(myVar, THIS, false, new HashSet<C_Term>());
                    newType = X10TypeMixin.depClauseDeref(type, rc2);
                }
                catch (Failure f) {
                    throw new SemanticException("Could not instantiate constraint " + rc + " on receiver type " + thisType + ".");
                }
            }
            newFormalTypes.add(newType);
        }
        
        X10MethodInstance result = (X10MethodInstance) proto.copy();
        result = (X10MethodInstance) result.returnType(newRetType);
        result = (X10MethodInstance) result.formalTypes(newFormalTypes);
        result = (X10MethodInstance) result.container(t);
        return result;
    }
}
