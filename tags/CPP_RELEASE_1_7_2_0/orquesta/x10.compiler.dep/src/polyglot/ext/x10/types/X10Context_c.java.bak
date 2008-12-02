/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

/**
 * This class extends the jl notion of Context to keep track of
 * the current deptype, if any, and its set of properties. These
 * properties can be referenced inside a deptype, i.e. in
 * the depClause in  [[Foo(: depClause)]].
 * 
 * We implement as follows. Since we want to reuse the mechanism for pushing
 * popping scopes as we enter a depClause, we shall implement pushDepType
 * as a pushing of a context, rather than as adding extra structure to 
 * the current context.
 * 
 * To push a deptype we push a class. However
 * we delegate certain methods, such as currentClass() to outer, since
 * pushing a deptype does not change the meaning of "this", only introduces
 * a meaning for "self". Thus jl code should continue to work -- it does not "see" 
 * the deptype pushed onto the context.
 * 
 * While processing depClause the only variables of the surrounding scope 
 * that are visible are the final variables. Inside depClause no new scopes
 * can be entered, e.g. inner classes, or method declarations or even depTypes.
 * This is a property of the X10 language.
 * 
 * Certain methods should not be called if depType is set, e.g. methods to add names,
 * push scopes etc. These through an assertion error.
 * 
 * Certain methods can be alled within a deptype, but the result should be as if they are called 
 * on the outer context. So this is easily dealt with using the pattern
 * depType == null ? super.Foo(..) : pop.Foo(...)
 * That is, if this context is not  deptype context, run the usual code. Otherwise
 * delegate to the outer context. 
 * 
 * 
 * 
 * @author vj
 * @see Context
 */
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Stmt;
import polyglot.types.Context_c;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.ImportTable;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.ParsedClassType;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import polyglot.util.CollectionUtil;

public class X10Context_c extends Context_c implements X10Context {
	
	public X10Context_c(TypeSystem ts) {
		super(ts);
	}
	
	
	// Invariant: isDepType => outer != null.
	
	protected X10NamedType depType = null;
	protected VarInstance varWhoseTypeIsBeingElaborated = null;
	public boolean isDepType() { return depType !=null; }
	public boolean inDepType() { return depType !=null;}
	
	protected boolean inSafeCode;
	protected boolean inSequentialCode;
	protected boolean inLocalCode;
	protected boolean inNonBlockingCode;
	protected boolean inAnnotation;
	
	public boolean inSafeCode() { return inSafeCode;}
	public boolean inSequentialCode() { return inSequentialCode;}
	public boolean inNonBlockingCode() { return inNonBlockingCode;}
	public boolean inLocalCode() { return inLocalCode;}
	public boolean inAnnotation() { return inAnnotation; }
	
	public void setSafeCode() { inSafeCode = true;}
	public void setSequentialCode() { inSequentialCode=true;}
	public void setNonBlockingCode() { inNonBlockingCode=true;}
	public void setLocalCode() { inLocalCode=true;}
	public void setAnnotation() { inAnnotation=true;}
	public void clearAnnotation() { inAnnotation=false;}

	protected Context_c push() {
        X10Context_c v = (X10Context_c) super.push();
        v.depType = null;
        v.varWhoseTypeIsBeingElaborated = null;
        // Do not set the inXXXCode attributes to false, inherit them from parent.
        return v;
    }
	
	public X10NamedType currentDepType() {
		return depType;
	}
	public VarInstance varWhoseTypeIsBeingElaborated() {
		return varWhoseTypeIsBeingElaborated;
	}
	private static final Collection TOPICS = 
		CollectionUtil.list(Report.types, Report.context);
	
	/**
	 * Returns whether the particular symbol is defined locally.  If it isn't
	 * in this scope, we ask the parent scope, but don't traverse to enclosing
	 * classes.
	 */
	public boolean isLocal(String name) {
		return depType == null ? super.isLocal(name): pop().isLocal(name) ;
	}
	
	/**
	 * Looks up a method with name "name" and arguments compatible with
	 * "argTypes".
	 */
	public MethodInstance findMethod(String name, List argTypes) throws SemanticException {
		MethodInstance result  = depType == null ? super.findMethod(name, argTypes) :pop().findMethod(name, argTypes);
		return result;
	}
	
	/**
	 * Gets a local variable of a particular name.
	 */
	public LocalInstance findLocal(String name) throws SemanticException {
		return depType ==null ? super.findLocal(name) : pop().findLocal(name) ;
	}
	
	public ClassType type() { return type;}
	/**
	 * Finds the class which added a field to the scope.
	 */
	public ClassType findFieldScope(String name) throws SemanticException {
		ClassType result = super.findFieldScope(name);
		if (result == null) {
			// hack. This is null when this context is in a deptype, and the deptype
			// is not a classtype, and the field belongs to the outer type, e.g.
			// class Foo { int(:v=0) v;}
			ClassType r = type;
			result = ((X10Context_c) pop()).type();
		}
		return result;
	}
	
	/** Finds the class which added a method to the scope. Do not
	 * search the current scope if depType !=null, since that does not contribute methods.
	 * In fact, it should be an error for this method to be called when
	 * deptype is true.
	 */
	public ClassType findMethodScope(String name) throws SemanticException {
		ClassType result = super.findMethodScope(name);
		if (result == null) {
			// hack. This is null when this context is in a deptype, and the deptype
			// is not a classtype, and the field belongs to the outer type, e.g.
			// class Foo { int(:v=0) v;}
			ClassType r = type;
			result = ((X10Context_c) pop()).type();
		}
		return result;
	}
	
	/**
	 * Gets a field of a particular name.
	 */
	public FieldInstance findField(String name) throws SemanticException {
		return super.findField(name);
	}
	
	
	/**
	 * Gets a local or field of a particular name.
	 */
	public VarInstance findVariable(String name) throws SemanticException {
		VarInstance vi = super.findVariable(name);
		return vi;
	}
	
	/**
	 * Gets a local or field of a particular name.
	 */
	public VarInstance findVariableSilent(String name) {
		return super.findVariableSilent(name);
	}
	
	
	/** 
	 * Finds the definition of a particular type.
	 */
	public Named find(String name) throws SemanticException {
//		assert (depType==null);
		return super.find(name);
	}
	
	/**
	 * Push a source file scope.
	 */
	public Context pushSource(ImportTable it) { 
		assert (depType == null);
		return super.pushSource(it);
	}
	
	public Context pushClass(ParsedClassType classScope, ClassType type) {
		assert (depType == null);
		return super.pushClass(classScope, type);
	}

	/**
	 * pushes an additional block-scoping level.
	 */
	public Context pushBlock() {
		assert (depType == null);
		return super.pushBlock();
	}
	public X10Context pushAtomicBlock() {
		assert (depType == null);
		X10Context c = (X10Context) super.pushBlock();
		c.setLocalCode();
		c.setNonBlockingCode();
		c.setSequentialCode();
		return c;
	}
	
	/**
	 * pushes an additional static scoping level.
	 */
	public Context pushStatic() {
		assert (depType==null);
		return super.pushStatic();
	}
	
	/**
	 * enters a method
	 */
	public Context pushCode(CodeInstance ci) {
		assert (depType==null);
		return super.pushCode(ci);
	}
	
	/**
	 * Gets the current method
	 */
	public CodeInstance currentCode() {
		return depType==null ? super.currentCode() : pop().currentCode() ;
	}
	
	/**
	 * Return true if in a method's scope and not in a local class within the
	 * innermost method.
	 */
	public boolean inCode() {
		return depType==null ? super.inCode() : pop().inCode();
	}
	
	public boolean inStaticContext() {
		return depType ==null?  super.inStaticContext() : pop().inStaticContext();
	}
	
	/**
	 * Gets current class
	 */
	public ClassType currentClass() {
		return depType==null ? super.currentClass() : pop().currentClass();
	}
	
	/**
	 * Gets current class scope
	 */
	public ParsedClassType currentClassScope() {
		return depType==null ? super.currentClassScope() : pop().currentClassScope();
	}
	
	/**
	 * Adds a symbol to the current scoping level.
	 */
	public void addVariable(VarInstance vi) {
		assert (depType==null);
		super.addVariable(vi);
	}
	
	/**
	 * Adds a named type object to the current scoping level.
	 */
	public void addNamed(Named t) {
		assert (depType==null);
		super.addNamed(t);
	}
	

	public Named findInThisScope(String name) {
//		assert (depType==null);
		return super.findInThisScope(name);
	}
	
	public void addNamedToThisScope(Named type) {
//		assert (depType==null);
		super.addNamedToThisScope(type);
	}
	
	public ClassType findMethodContainerInThisScope(String name) {
//		assert (depType==null);
		return super.findMethodContainerInThisScope(name);
	
	}
	
	public VarInstance findVariableInThisScope(String name) {
		//if (name.startsWith("val")) Report.report(1, "X10Context_c: searching for |" + name + " in " + this);
		if (depType ==null) return super.findVariableInThisScope(name);
		
		VarInstance vi =  ((X10Context_c) pop()).findVariableInThisScope(name);
		
		if (vi instanceof LocalInstance) return vi;
		// otherwise it is a FieldInstance (might be a PropertyInstance, which is a FieldInstance)
		// See if the currentDepType has a field of this name. If so, that gets priority
		// and should be returned. The receiver must treat it as the reference
		// self.name. 
		try {
			if (depType instanceof X10ClassType) {
				X10ClassType dep = (X10ClassType) this.depType;
				VarInstance myVi=ts.findField(dep, name, dep);
				if (myVi !=null) {
				//	if (name.equals("val")) Report.report(1, "X10Context_c: ==> " + myVi);
					return myVi;
				}
			} 
		} catch (SemanticException e) {}
		return   vi;
	}
		
	public void addVariableToThisScope(VarInstance var) {
		assert (depType==null);
		super.addVariableToThisScope(var);
	}
	public void setVarWhoseTypeIsBeingElaborated(VarInstance var) {
		varWhoseTypeIsBeingElaborated=var;
	}
	
	// New lookup methods added for deptypes.
	
	public X10FieldInstance findProperty(String name) throws SemanticException {
		X10FieldInstance pi=null;
		FieldInstance fi = findField(name);
		if (fi instanceof X10FieldInstance) {
			pi = (X10FieldInstance) pi;
		}
		return pi;
	}
	
	public ClassType findPropertyScope(String name) throws SemanticException {
		return findFieldScope(name);
	}
	/**
	 * Pushes on a deptype. Treat this as pushing a class. 
	 * 
	 */
	
	public X10Context pushDepType(X10NamedType type) {
		
		X10Context_c v = (X10Context_c) push();
		v.depType = type;
		v.varWhoseTypeIsBeingElaborated = varWhoseTypeIsBeingElaborated;
		v.type = type instanceof ClassType ? (ClassType) type : null;
		v.inCode = false;
		//Report.report(1, "X10Context_c: Pushing deptype |" + type + "|" + v.hashCode());
		return v;
	}
	 public String toString() {
	        return "(" + (depType !=null ? "depType" + depType : kind.toString()) + " " + mapsToString() + " " + outer + ")";
	    }
	 static protected int varCount=0;
	 public String getNewVarName() {
		 return MAGIC_VAR_PREFIX + (varCount++);
	 }
	
	 
}
