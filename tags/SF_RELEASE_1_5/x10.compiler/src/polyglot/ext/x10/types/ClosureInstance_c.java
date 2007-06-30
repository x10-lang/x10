/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.types.*;
import polyglot.util.Position;

public class ClosureInstance_c extends ProcedureInstance_c implements ClosureInstance {
    private static final long serialVersionUID= 2804222307728697502L;

    protected MethodInstance methodContainer; // do we need one of these?

    protected Type returnType;

    protected ClosureInstance decl;
    
    public ClosureInstance_c() {
	super();
    }

    public ClosureInstance_c(TypeSystem ts, Position pos, ClassType typeContainer, MethodInstance methodContainer, Type returnType, List formalTypes, List excTypes) {
	super(ts, pos, typeContainer, Flags.NONE, formalTypes, excTypes);
	this.returnType= returnType;
	this.methodContainer= methodContainer;
	this.decl= this;
    }

    public MethodInstance methodContainer() {
	return methodContainer;
    }

    public void setMethodContainer(MethodInstance container) {
	methodContainer= container;
    }

    public Type returnType() {
	return returnType;
    }

    public void setReturnType(Type returnType) {
	this.returnType= returnType;
    }

    public Declaration declaration() {
        return decl;
    }
    
    public void setDeclaration(Declaration decl) {
        this.decl= (ClosureInstance) decl;        
    }

    public boolean closureCallValid(List<Type> actualTypes) {

	return false;
    }

    public String signature() {
	return (methodContainer != null ? methodContainer : "") + "(" + X10TypeSystem_c.listToString(formalTypes) + ")";
    }

    public String designator() {
	return "closure";
    }

    public boolean isCanonical() {
	// TODO Auto-generated method stub
	return false;
    }

    public String toString() {
	return designator() + " " + flags.translate() + this.returnType() + " " + signature();
    }
}
