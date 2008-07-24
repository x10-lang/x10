/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.types.ClassDef;
import polyglot.types.Flags;
import polyglot.types.Ref;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.constraint.XConstraint;
import polyglot.types.Types;

public class ClosureType_c extends X10ParsedClassType_c implements ClosureType {
    private static final long serialVersionUID= 2768150875334536668L;

    protected Ref<? extends Type> returnType;
    protected List<Ref<? extends Type>> typeParams;
    protected List<Ref<? extends Type>> argumentTypes;
    protected Ref<? extends XConstraint> whereClause;
    protected List<Ref<? extends Type>> throwTypes;

//    public ClosureType_c() {
//	super();
//    }
//
//    public ClosureType_c(TypeSystem ts, Position pos) {
//	super(ts, pos);
//    }

    public ClosureType_c(X10TypeSystem ts, Position pos, Ref<? extends Type> returnType, List<Ref<? extends Type>> argTypes) {
	this(ts, pos, returnType, new ArrayList<Ref<? extends Type>>(), argTypes, null, new ArrayList<Ref<? extends Type>>());
    }
    
    public ClosureType_c(X10TypeSystem ts, Position pos, Ref<? extends Type> returnType, List<Ref<? extends Type>> typeParams, List<Ref<? extends Type>> argTypes, Ref<? extends XConstraint> whereClause, List<Ref<? extends Type>> throwTypes) {
	super(ts, pos, Types.ref(ts.closureInterfaceDef(typeParams.size(), argTypes.size())));
	this.returnType = returnType;
	this.typeParams = typeParams;
        this.argumentTypes = argTypes;
        this.whereClause = whereClause;
        this.throwTypes = throwTypes;
    }
    
    public Ref<? extends Type> returnType() {
	return returnType;
    }

    public void returnType(Ref<? extends Type> returnType) {
	this.returnType= returnType;
    }
    
    public Ref<? extends XConstraint> whereClause() {
	    return whereClause;
    }
    
    public void setWhereClause(Ref<? extends XConstraint> whereClause) {
	    this.whereClause = whereClause;
    }
    
    public List<Ref<? extends Type>> typeParameters() {
	    return Collections.unmodifiableList(typeParams);
    }
    
    public void setTypeParameters(List<Ref<? extends Type>> typeParams) {
	    this.typeParams= typeParams;
    }
    

    public List<Ref<? extends Type>> argumentTypes() {
	return Collections.unmodifiableList(argumentTypes);
    }

    public void argumentTypes(List<Ref<? extends Type>> argTypes) {
	this.argumentTypes= argTypes;
    }

    public List<Ref<? extends Type>> throwTypes() {
	return Collections.unmodifiableList(throwTypes);
    }

    public void throwTypes(List<Ref<? extends Type>> argTypes) {
	this.throwTypes= argTypes;
    }

    @Override
    public String translate(Resolver c) {
	// Just combine the result of calling translate() on each of the component types?
        throw new InternalCompilerError("Fix Me: cannot translate() a closure type yet.");
    }

    public boolean safe() {
        return true;
    }
    
    @Override
    public String toString() {
	StringBuffer buff= new StringBuffer();
	buff.append('(');
	for(Iterator<Ref<? extends Type>> iter= argumentTypes.iterator(); iter.hasNext(); ) {
	    Ref<? extends Type> type= iter.next();
	    buff.append(type.toString());
	    if (iter.hasNext()) buff.append(',');
	}
	buff.append(')');
	if (throwTypes.size() > 0) {
	    buff.append(" throws ");
	    for(Iterator<Ref<? extends Type>> iter= throwTypes.iterator(); iter.hasNext(); ) {
	        Ref<? extends Type> type= iter.next();
		buff.append(type.toString());
		if (iter.hasNext()) buff.append(',');
	    }
	}
	buff.append(" => ");
	buff.append(returnType.toString());
	return buff.toString();
    }
}
