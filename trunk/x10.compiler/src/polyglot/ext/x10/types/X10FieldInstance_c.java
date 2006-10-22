package polyglot.ext.x10.types;

import java.util.Iterator;
import java.util.List;

import polyglot.ext.jl.types.FieldInstance_c;
import polyglot.main.Report;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.ReferenceType;
import polyglot.types.Type;
import polyglot.types.TypeObject;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * An implementation of PropertyInstance
 * @author vj
 *
 */
public class X10FieldInstance_c extends FieldInstance_c implements X10FieldInstance {

    public X10FieldInstance_c() {
        super();
    }

    public X10FieldInstance_c(TypeSystem ts, Position pos,
            ReferenceType container, Flags flags, Type type, String name) {
        super(ts, pos, container, flags, type, name);
        
    }
    
    public X10FieldInstance_c(TypeSystem ts, Position pos,
			   ReferenceType container,
	                   Flags flags,  String name, String initValue) {
     super(ts, pos, container, flags, ts.String(), name);
     setConstantValue(initValue);
 }
    /**
     * A PropertyInstance is equal to another TypeObject only if the other TypeObject
     * represents a property, and the two are equal when viewed as fields.
     */
    public boolean equalsImpl(TypeObject o) {
        if (o instanceof X10FieldInstance) {
        X10FieldInstance i = (X10FieldInstance) o;
        return super.equalsImpl(i);
    }

    return false;
    }
    
    boolean isPropertyInitialized = false;
    boolean isProperty = false;
    
    public void setProperty() {
    	isPropertyInitialized = isProperty = true;
    }
    public boolean isProperty() {
    	
    	if (isPropertyInitialized) return isProperty;
    	
    	if (!(container instanceof X10ParsedClassType)) 
    		return isProperty=false;
    	
    	X10ParsedClassType p = (X10ParsedClassType) container;
    	List props = p.properties();
    	// this must occur after the call to properties(), which may throw an exception.
    	isPropertyInitialized = true;
    	for (Iterator i=props.iterator(); i.hasNext(); ) {
    		
    		FieldInstance f = (FieldInstance) i.next();
    		
    		if (f.equals(this)) 
    			return isProperty=true;
    		
    	}
    	return isProperty=false;
    }
    
}
