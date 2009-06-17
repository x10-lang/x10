/*
 * Created on Mar 1, 2007
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.types.Resolver;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Type_c;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

public class ClosureType_c extends Type_c implements ClosureType {
    private static final long serialVersionUID= 2768150875334536668L;

    protected Type returnType;
    protected List<Type> argumentTypes;
    protected List<Type> throwTypes;

    public ClosureType_c() {
	super();
    }

    public ClosureType_c(TypeSystem ts) {
	super(ts);
    }

    public ClosureType_c(TypeSystem ts, Position pos) {
	super(ts, pos);
    }

    public ClosureType_c(TypeSystem ts, Position pos, Type returnType, List<Type> argTypes) {
	this(ts, pos, returnType, argTypes, new ArrayList<Type>());
    }

    public ClosureType_c(TypeSystem ts, Position pos, Type returnType, List<Type> argTypes, List<Type> throwTypes) {
	this(ts, pos);
	this.returnType= returnType;
	this.argumentTypes= argTypes;
	this.throwTypes= throwTypes;
    }

    public Type returnType() {
	return returnType;
    }

    public void returnType(Type returnType) {
	this.returnType= returnType;
    }

    public List<Type> argumentTypes() {
	return Collections.unmodifiableList(argumentTypes);
    }

    public void argumentTypes(List<Type> argTypes) {
	this.argumentTypes= argTypes;
    }

    public List<Type> throwTypes() {
	return Collections.unmodifiableList(throwTypes);
    }

    public void throwTypes(List<Type> argTypes) {
	this.throwTypes= argTypes;
    }

    @Override
    public boolean isCanonical() {
	boolean result = true;
	result &= returnType.isCanonical();

	if (argumentTypes != null) {
	    for(Iterator it = argumentTypes.iterator(); it.hasNext() && result; ) {
		Type t = (Type) it.next();
		result &= t.isCanonical();
	    }
	}
	if (throwTypes != null) {
	    for(Iterator it = throwTypes.iterator(); it.hasNext() && result; ) {
		Type t = (Type) it.next();
		result &= t.isCanonical();
	    }
	}
	return result;
    }

    @Override
    public String translate(Resolver c) {
	// Just combine the result of calling translate() on each of the component types?
        throw new InternalCompilerError("Fix Me: cannot translate() a closure type yet.");
    }

    @Override
    public boolean descendsFromImpl(Type t) {
	// Permit covariance in the return type, so that a closure that returns a more
	// specific type can be assigned to a closure variable with a less specific
	// return type. Don't permit covariance in the throw types or argument types.
	if (!(t instanceof ClosureType))
	    return false;
	ClosureType other= (ClosureType) t;
	if (!typeListEquals(argumentTypes, other.argumentTypes()))
	    return false;
	if (!typeListEquals(throwTypes, other.throwTypes()))
	    return false;
	return ts.descendsFrom(returnType, other.returnType());
    }

    @Override
    public boolean typeEqualsImpl(Type t) {
	if (!(t instanceof ClosureType))
	    return false;
	ClosureType other= (ClosureType) t;
	if (!ts.typeEquals(returnType, other.returnType()))
	    return false;
	if (!typeListEquals(argumentTypes, other.argumentTypes()))
	    return false;
	if (!typeListEquals(throwTypes, other.throwTypes()))
	    return false;
        return true;
    }

    protected boolean typeListEquals(List<Type> l1, List<Type> l2) {
	if (l1.size() != l2.size())
	    return false;
	for(int i=0; i < l1.size(); i++) {
	    if (!ts.typeEquals(l1.get(i), l2.get(i)))
		return false;
	}
	return true;
    }

    @Override
    public String toString() {
	StringBuffer buff= new StringBuffer();
	buff.append(returnType.toString())
	    .append('(');
	for(Iterator<Type> iter= argumentTypes.iterator(); iter.hasNext(); ) {
	    Type type= iter.next();
	    buff.append(type.toString());
	    if (iter.hasNext()) buff.append(',');
	}
	buff.append(')');
	if (throwTypes.size() > 0) {
	    buff.append(" throws ");
	    for(Iterator<Type> iter= throwTypes.iterator(); iter.hasNext(); ) {
		Type type= iter.next();
		buff.append(type.toString());
		if (iter.hasNext()) buff.append(',');
	    }
	}
	return buff.toString();
    }
}
