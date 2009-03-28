/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10cpp.types;

/**
 * This class extends the X10 notion of Context to keep track of
 * the translation state for the C++ backend.
 *
 * @author igor
 * @author pvarma
 * @author nvk
 * @author Dave Cunningham
 * @see X10Context_c
 */
import static polyglot.ext.x10cpp.visit.Emitter.mangled_non_method_name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import polyglot.ast.ClassMember;
import polyglot.ast.Stmt;
import polyglot.ext.x10.ast.PropertyDecl;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import x10c.util.ClassifiedStream;

public class X10CPPContext_c extends polyglot.ext.x10.types.X10Context_c implements X10Context {

    // The global object is fresh for each brand new instance of the context,
    // but is aliased for each clone of the context (cloned via copy()).
    // The only thing that belongs in here is primitive data that needs a level
    // of indirection to allow aliasing after cloning.

    protected static class Global {
        public int closureId;
    }
    protected Global g = new Global();

    public void advanceClosureId() { g.closureId++; }
    public int closureId() { return g.closureId; }
    protected void resetClosureId() { g.closureId = -1; }

    protected ArrayList<ClassMember> pendingStaticDecls;
    protected ArrayList<PropertyDecl> classProperties;
    
    public List<PropertyDecl> classProperties() { return classProperties; }
    public List<ClassMember> pendingStaticDecls() { return pendingStaticDecls; }

    // Here, for each new class we reset the above structures, ready for fresh accumulation of data.
    public void resetStateForClass(List<PropertyDecl> props) {
        assert kind == SOURCE;
        pendingStaticDecls = new ArrayList<ClassMember>();
        classProperties = new ArrayList<PropertyDecl>(props);
        resetClosureId();
    }

    
    private String label;
    public String getLabel() { return label; }

    private Stmt stmt;
    public Stmt getLabeledStatement() { return stmt; }

    public void setLabel(String label, Stmt stmt) {
        this.label = label;
        this.stmt = stmt;
    }

    
    private String excVar;
    public void setExceptionVar(String var) { this.excVar = var; }
    public String getExceptionVar() { return excVar; }

    // used internally, shallow
	protected boolean inClosure;
    public void setInClosure() { inClosure = true; }

    // used externally, deep
	protected boolean insideClosure;
    public boolean isInsideClosure() { return insideClosure; }
    public void setInsideClosure(boolean b) { insideClosure = b; }

    
    public boolean hasInits = false;

    
    public ClassifiedStream templateFunctions = null;

	public ArrayList<VarInstance> variables = new ArrayList<VarInstance>();

    
    public X10CPPContext_c(TypeSystem ts) {
        super(ts);
    }

    public boolean inTemplate() {
        X10ClassDef cd = (X10ClassDef)currentClassDef();
        // following 2 lines are needed for constraints
        if (inSuperTypeDeclaration())
            cd = supertypeDeclarationType();
        X10MethodDef md = currentCode() instanceof X10MethodDef ? (X10MethodDef) currentCode() : null;
        boolean genericClass = cd.typeParameters().size() != 0;
        boolean staticMethod = md != null && md.flags().isStatic();
        boolean genericMethod = md != null && md.typeParameters().size() != 0;
        //[DC] FIXME: what if we've in a static initialiser of a generic class
        //should return false, but does return true?
        //[IP] The above should also check for an initializer
        return (!staticMethod && genericClass) || genericMethod;
    }

    public X10MethodDef currentMethod() {
        if (currentCode() instanceof X10MethodDef)
            return (X10MethodDef) currentCode();
        return null;
    }

    
	public void saveEnvVariableInfo(String name) {
		VarInstance vi = findVariableInThisScope(Name.make(name));
		if (vi != null) {  // found declaration 
			// local variable (do nothing)
		} else if (inClosure) {
			addVar(name); // local variable
		} else {  // Captured Variable
			((X10CPPContext_c) outer).saveEnvVariableInfo(name);
		}
	}

    
    private VarInstance lookup(String name) {
        VarInstance vi = findVariableInThisScope(Name.make(name));
        if (vi != null) return vi;
        else if (outer != null) return ((X10CPPContext_c) outer).lookup(name);
        else return null;
    }

    private void addVar(String name) {
		VarInstance vi = lookup(name);
		boolean contains = false;
        for (VarInstance vi2 : variables) {
            // [DC]: what is wrong with vi2.equals(vi)?
            if (vi2.name().toString().equals(vi.name().toString())) {
                contains = true;
                break;
            }
        }
		if (!contains) variables.add(vi);
	}

	public void finalizeClosureInstance() {
		// remove all from current context. Move those pertinent to an outer
		// closure to the outer closure.
//		for (int i = variables.size() - 1; i >= 0; i--) {
//			VarInstance v = (VarInstance) variables.remove(i);
//			((X10Context_c) outer).saveEnvVariableInfo(v.name());
//		}
		for (int i = 0; i < variables.size(); i++) {
			VarInstance v = (VarInstance) variables.get(i);
			VarInstance cvi = findVariableInThisScope(v.name());
			if (cvi == null)   // declaration not found 
				((X10CPPContext_c) outer).saveEnvVariableInfo(v.name().toString());
		}
	}
    
    
    

	public Object copy() {
		X10CPPContext_c res = (X10CPPContext_c) super.copy();
		res.variables = new ArrayList<VarInstance>();  // or whatever the initial value is
		res.inClosure = false;
		return res;
	}

}
//vim:tabstop=4:shiftwidth=4:expandtab
