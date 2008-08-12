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
import polyglot.types.LazyRef;
import polyglot.types.LocalInstance;
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
    private static final long serialVersionUID = 2768150875334536668L;

    protected ClosureInstance ci;

    // public ClosureType_c() {
    // super();
    // }
    //
    // public ClosureType_c(TypeSystem ts, Position pos) {
    // super(ts, pos);
    // }

    public ClosureType_c(final X10TypeSystem ts, Position pos, final ClosureInstance ci) {
	super(ts, pos, null);
	final LazyRef<ClassDef> cd = Types.lazyRef(null);
	cd.setResolver(new Runnable() {
	    public void run() {
		cd.update(ts.closureInterfaceDef(ci.def()));
	    }
	});
	this.def = cd;
	this.ci = ci;
    }
    
    public ClosureInstance closureInstance() {
	return ci;
    }

    public ClosureType closureInstance(ClosureInstance ci) {
	ClosureType_c n = (ClosureType_c) copy();
	n.ci = ci;
	return n;
    }
    
    public ClosureDef closureDef() {
	return ci.def();
    }

    public Type returnType() {
	return ci.returnType();
    }

    public XConstraint whereClause() {
	return ci.whereClause();
    }

    public List<Type> typeParameters() {
	return ci.typeParameters();
    }

    public List<LocalInstance> formalNames() {
	return ci.formalNames();
    }
    
    public List<Type> argumentTypes() {
	return ci.formalTypes();
    }

    public List<Type> throwTypes() {
	return ci.throwTypes();
    }

    @Override
    public String translate(Resolver c) {
	// Just combine the result of calling translate() on each of the
	// component types?
	throw new InternalCompilerError("Fix Me: cannot translate() a closure type yet.");
    }

    public boolean safe() {
	return true;
    }

    @Override
    public String toString() {
	return ci.signature() + " => " + ci.returnType();
    }
}
