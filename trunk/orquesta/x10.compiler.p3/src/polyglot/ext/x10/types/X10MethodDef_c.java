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
import polyglot.ext.x10.types.constr.Constraint_c;
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
        return name + "(" + X10TypeSystem_c.listToString(formalTypes) + 
        (whereClause != null ? ": " + whereClause.toString() : "") + ")";
    }

    @Override
    public MethodInstance asInstance() {
        if (asInstance == null) {
            asInstance = new X10MethodInstance_c(ts, position(), Types.<X10MethodDef>ref(this));
        }
        return asInstance;
    }
    
    public X10MethodInstance instantiateForThis(ReferenceType t, List<Type> argTypes) throws SemanticException {
        X10Type thisType = (X10Type) t;
        
        if (thisType.selfVar() == null) {
        	Constraint c = thisType.depClause();
        	if (c == null)
        		c = new Constraint_c((X10TypeSystem) ts);
        	else
        		c = c.copy();
        	C_Var self = c.genEQV(t, true);
        	c.setSelfVar(self);
        	thisType = thisType.depClause(c);
        }
        
        boolean needed = false;
        Type rt = Types.get(returnType());
        if (rt instanceof UnknownType) throw new SemanticException();
        X10Type retType = (X10Type) rt;
        X10Type newRetType = retType;
        Constraint rc = retType.realClause();
        C_Special THIS = C_Special_c.This;
        C_Var selfVar = thisType.selfVar();

        for (Ref<? extends Type> tref : formalTypes()) {
            X10Type type = (X10Type) tref.get();
            if (type instanceof PathType) {
            	PathType pt = (PathType) type;
            	C_Var path = pt.base();
            	if (path instanceof C_Special && ((C_Special) path).kind() == C_Special.THIS) {
            		needed = true;
            		break;
            	}
            }
            rc = type.realClause();
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
                rc = type.realClause();
                assert ! rc.hasVar(THIS);
            }
        }

        X10MethodInstance_c proto = (X10MethodInstance_c) asInstance();

        if (!needed)
            return proto;
        
        // TODO: instantiate x.T when x is another formal
        // TODO: refactor and generalize this method and X10New_c.instantiateType
        List<Type> newFormalTypes = new ArrayList<Type>(formalTypes.size());
        for (Ref<? extends Type> tref : formalTypes()) {
            X10Type type = (X10Type) tref.get();
    	    Type newType = instantiateThis(type, thisType, THIS, selfVar);
            newFormalTypes.add(newType);
        }
        
        newRetType = (X10Type) instantiateThis(newRetType, thisType, THIS, selfVar);
        
        X10MethodInstance result = (X10MethodInstance) proto.copy();
        result = (X10MethodInstance) result.returnType(newRetType);
        result = (X10MethodInstance) result.formalTypes(newFormalTypes);
        result = (X10MethodInstance) result.container(t);
        return result;
    }

	private X10Type instantiateThis(X10Type type, X10Type thisType,
			C_Special THIS, C_Var selfVar) throws SemanticException {
		Constraint rc;
		if (type instanceof PathType) {
			PathType pt = (PathType) type;
			C_Var path = pt.base();
			X10Type receiverType = thisType;
			if (path instanceof C_Special && ((C_Special) path).kind() == C_Special.THIS && ((C_Special) path).qualifier() == null) {
				Type tp = X10TypeMixin.lookupTypeProperty(receiverType.depClause(), pt.property());
//				tp = null; // XXX###
				if (tp != null) {
					type = (X10Type) tp;
				}
				else {
					X10TypeSystem xts = (X10TypeSystem) pt.typeSystem();
					C_Var targetPath = thisType.selfVar();
					if (targetPath != null) {
						type = pt.base((C_Var) targetPath);
					}
					else {
						type = (X10Type) xts.X10Object();
					}
				}
			}
		}

		rc = type.realClause();
		X10Type newType = type;
		if (rc!=null && rc.hasVar(THIS)) {
		    C_Var myVar = selfVar;
		    if (myVar == null)
		        myVar = rc.genEQV(thisType, true); // not thisType for the args.
		    try {
		        Constraint rc2 = rc.substitute(myVar, THIS, false);
		        newType = type.depClause(rc2);
		    }
		    catch (Failure f) {
		        throw new SemanticException("Could not instantiate constraint " + rc + " on receiver type " + thisType + ".");
		    }
		}
		return newType;
	}
}
