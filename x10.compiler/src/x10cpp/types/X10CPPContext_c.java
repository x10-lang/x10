/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10cpp.types;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import polyglot.ast.ClassMember;
import polyglot.ast.Stmt;
import polyglot.types.Name;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import x10.ast.PropertyDecl;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import polyglot.types.Context;
import polyglot.util.Position;
import x10.util.CollectionFactory;
import x10.types.X10MethodDef;
import x10.util.ClassifiedStream;
import x10cpp.visit.ITable;
import x10cpp.visit.StringLiteralManager;

public class X10CPPContext_c extends Context {

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

    /**
     * Every context has some arbitrary data, associated with string keys.
     * To get the data from the current context, use {@link #getData(String)}.
     * To find the data in the current or ancestor context, use {@link #findData(String)}.
     * To add data to the current context, use {@link #addData(String, Object)}.
     */
    protected Map<String, Object> data = CollectionFactory.newHashMap(1, 1.0f);

    /** @see #data. */
    public <T> void addData(String key, T value) { data.put(key, value); }
    /** @see #data. */
    @SuppressWarnings("unchecked") // Casting to a generic type parameter
    public <T> T getData(String key) { return (T) data.get(key); }
    /** @see #data. */
    public <T> T findData(String key) {
        T value = this.<T>getData(key);
        if (value == null && outer != null)
            return ((X10CPPContext_c) outer).<T>findData(key);
        return value;
    }

    protected ArrayList<ClassMember> pendingStaticDecls;
    protected ArrayList<PropertyDecl> classProperties;
    public StringLiteralManager stringManager;
    
    public List<PropertyDecl> classProperties() { return classProperties; }
    public List<ClassMember> pendingStaticDecls() { return pendingStaticDecls; }

    public Position lastLine;
    
    /**
     * For each new class reset the classProperties and pendingStaticDecls structures,
     * ready for fresh accumulation of data.
     * @param props the initial set of class properties
     */
    public void resetStateForClass(List<PropertyDecl> props) {
        assert kind == SOURCE;
        pendingStaticDecls = new ArrayList<ClassMember>();
        classProperties = new ArrayList<PropertyDecl>(props);
        stringManager = new StringLiteralManager();
    }

    /**
     * Clear the classProperties and pendingStaticDecls structures, to avoid AST node
     * leakage.
     */
    public void clearStateForClass() {
        assert kind == SOURCE;
        pendingStaticDecls = null;
        classProperties = null;
    }
    
    private String label;
    public String getLabel() { return label; }

    private Stmt stmt;
    public Stmt getLabeledStatement() { return stmt; }

    public void setLabel(String label, Stmt stmt) {
        this.label = label;
        this.stmt = stmt;
    }

    // used internally, shallow
	protected boolean inClosure;
    public void setInClosure() { inClosure = true; }

    // used externally, deep
	protected boolean insideClosure;
    public boolean isInsideClosure() { return insideClosure; }
    public void setInsideClosure(boolean b) { insideClosure = b; }

    // used externally, deep
    protected boolean insideTemplateClosure;
    public boolean isInsideTemplateClosure() { return insideTemplateClosure; }
    public void setInsideTemplateClosure(boolean b) { insideTemplateClosure = b; }

    // used internally, shallow
    public boolean stackAllocateClosure = false;
    public void setStackAllocateClosure(boolean val) { stackAllocateClosure = val; }
    // The outer context of the closure (if in closure)
    public X10CPPContext_c closureOuter = null;
    
    // used internally, shallow
    protected String stackAllocName = null;
    public String getStackAllocName() { return stackAllocName; }
    public void setStackAllocName(String s) { stackAllocName = s; }
    
    // used internally, shallow
    protected String embeddedFieldName = null;
    public String getEmbeddedFieldName() { return embeddedFieldName; }
    public void setEmbeddedFieldName(String s) { embeddedFieldName = s; }

    public ClassifiedStream genericFunctions = null;
    public ClassifiedStream genericFunctionClosures = null;
    public ClassifiedStream closures = null;
    public ClassifiedStream staticDefinitions = null;
    public ClassifiedStream staticClosureDefinitions = null;


	public ArrayList<VarInstance<?>> variables = new ArrayList<VarInstance<?>>();

    private void putYourVariablesHere(ArrayList<VarInstance<?>> vars) {
        vars.addAll(variables);
        if (inClosure) return;
        ((X10CPPContext_c) outer).putYourVariablesHere(vars);
    }

    public ArrayList<VarInstance<?>> variables() {
        ArrayList<VarInstance<?>> r = new ArrayList<VarInstance<?>>();
        putYourVariablesHere(r);
        return r;
    }

    
    private final Map<X10ClassType, ITable> cachedITables = CollectionFactory.newHashMap();
    /**
     * Find or construct the ITable instance for the argument X10 interface type.
     */
    public ITable getITable(X10ClassType interfaceType) {
        ITable ans = cachedITables.get(interfaceType);
        if (ans == null) {
            ans = new ITable(interfaceType);
            cachedITables.put(interfaceType, ans);
        }
        return ans;
    }
    
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
        boolean genericMethod = md != null && md.typeParameters().size() != 0;
        return (!inStaticContext() && genericClass) || genericMethod;
    }

    public X10MethodDef currentMethod() {
        if (currentCode() instanceof X10MethodDef)
            return (X10MethodDef) currentCode();
        return null;
    }

    
    public void saveEnvVariableInfo(String name) {
        saveEnvVariableInfo(name, false);
    }

	public void saveEnvVariableInfo(String name, boolean lval) {
		VarInstance<?> vi = findVariableInThisScope(Name.make(name));
		if (vi != null) {  // found declaration 
			// local variable (do nothing)
		} else if (inClosure) {
			addVar(name, lval); // local variable
		} else {  // Captured Variable
			((X10CPPContext_c) outer).saveEnvVariableInfo(name, lval);
		}
	}

    
    private VarInstance<?> lookup(String name, Context lookupContext) {
        VarInstance<?> vi = findVariableInThisScope(Name.make(name));
        if (vi != null) return vi;
        else if (outer != null) return ((X10CPPContext_c) outer).lookup(name, lookupContext);
        else return null;
    }

    private void addVar(String name, boolean lval) {
		VarInstance<?> vi = lookup(name, this);
		if (vi==null) {
		    System.out.println("Internal compiler error: this variable name could not be looked up: "+name);
		}
        assert vi != null : name.toString();
		boolean contains = false;
        for (VarInstance<?> vi2 : variables) {
            // [DC]: what is wrong with vi2.equals(vi)?
            if (vi2.name().toString().equals(vi.name().toString())) {
                contains = true;
                break;
            }
        }
        if (lval) vi = vi.lval(lval);
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
			VarInstance<?> v = variables.get(i);
			VarInstance<?> cvi = findVariableInThisScope(v.name());
			if (cvi == null)   // declaration not found 
				((X10CPPContext_c) outer).saveEnvVariableInfo(v.name().toString(), v.lval());
		}
	}
    
	public X10CPPContext_c shallowCopy() {
		X10CPPContext_c res = (X10CPPContext_c) super.shallowCopy();
		res.variables = new ArrayList<VarInstance<?>>();  // or whatever the initial value is
		res.inClosure = false;
		res.stackAllocateClosure = false;
		res.closureOuter = null;
		res.data = CollectionFactory.newHashMap(1, 1.0f);
		return res;
	}

}
//vim:tabstop=4:shiftwidth=4:expandtab
