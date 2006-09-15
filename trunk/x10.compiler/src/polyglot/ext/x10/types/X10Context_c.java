package polyglot.ext.x10.types;

/**
 * This class extends the jl notion of Context to keep track of
 * the current deptype, if any, and its set of properties. These
 * properties can be referenced inside a deptype, i.e. in
 * the depClause in  [[Foo(: depClause)]].
 * 
 * While processing depClause the only variables of the surrounding scope 
 * that are visible are the final variables. Inside depClause no new scopes
 * can be entered, e.g. inner classes, or method declarations or even depTypes.
 * This is a property of the X10 language.
 * 
 * @author vj
 * @see Context
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ext.jl.types.Context_c;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.CodeInstance;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.ImportTable;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Named;
import polyglot.types.NoMemberException;
import polyglot.types.ParsedClassType;
import polyglot.types.Resolver;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.VarInstance;
import polyglot.util.CollectionUtil;

public class X10Context_c extends Context_c implements X10Context {
	
	public X10Context_c(TypeSystem ts) {
		super(ts);
	}
	
	// Invariant: isDepType => outer != null.
	
	boolean isDepType = false;
	public boolean isDepType() { return isDepType; }
	
	protected Context_c push() {
        X10Context_c v = (X10Context_c) super.push();
        v.isDepType = false;
        return v;
    }
	
	public X10ParsedClassType currentDepType() {
		return isDepType ? (X10ParsedClassType) super.currentClass() : null;
	}
	private static final Collection TOPICS = 
		CollectionUtil.list(Report.types, Report.context);
	
	/**
	 * Returns whether the particular symbol is defined locally.  If it isn't
	 * in this scope, we ask the parent scope, but don't traverse to enclosing
	 * classes.
	 */
	public boolean isLocal(String name) {
		return isDepType ? pop().isLocal(name) : super.isLocal(name);
	}
	
	/**
	 * Looks up a method with name "name" and arguments compatible with
	 * "argTypes".
	 */
	public MethodInstance findMethod(String name, List argTypes) throws SemanticException {
		return isDepType ? pop().findMethod(name, argTypes) : super.findMethod(name, argTypes);
	}
	
	/**
	 * Gets a local variable of a particular name.
	 */
	public LocalInstance findLocal(String name) throws SemanticException {
		return isDepType ? pop().findLocal(name) : super.findLocal(name);
	}
	
	/**
	 * Finds the class which added a field to the scope.
	 */
	public ClassType findFieldScope(String name) throws SemanticException {
		return super.findFieldScope(name);
	}
	
	/** Finds the class which added a method to the scope. Do not
	 * search the current class if isDepType, since that does not contribute methods.
	 * In fact, it should be an error for this method to be called when
	 * deptype is true.
	 */
	public ClassType findMethodScope(String name) throws SemanticException {
		return isDepType ? pop().findMethodScope(name) : super.findMethodScope(name);
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
		if (name.startsWith("n")) 
		Report.report(1, "X10Context_c: GOLDEN:: findVariable | "  + name + "| = " + vi);
		return vi;
	}
	
	/**
	 * Gets a local or field of a particular name.
	 */
	public VarInstance findVariableSilent(String name) {
		return super.findVariableSilent(name);
	}
	
	
	/** TODO:vj
	 * Finds the definition of a particular type.
	 */
	public Named find(String name) throws SemanticException {
		assert (! isDepType);
		return super.find(name);
	}
	
	/**
	 * Push a source file scope.
	 */
	public Context pushSource(ImportTable it) { 
		assert (! isDepType);
		return super.pushSource(it);
	}
	
	public Context pushClass(ParsedClassType classScope, ClassType type) {
		assert (! isDepType);
		return super.pushClass(classScope, type);
	}
	
	/**
	 * pushes an additional block-scoping level.
	 */
	public Context pushBlock() {
		assert (! isDepType);
		return super.pushBlock();
	}
	
	/**
	 * pushes an additional static scoping level.
	 */
	public Context pushStatic() {
		assert (! isDepType);
		return super.pushStatic();
	}
	
	/**
	 * enters a method
	 */
	public Context pushCode(CodeInstance ci) {
		assert (! isDepType);
		return super.pushCode(ci);
	}
	
	/**
	 * Gets the current method
	 */
	public CodeInstance currentCode() {
		return isDepType ? pop().currentCode() : super.currentCode();
	}
	
	/**
	 * Return true if in a method's scope and not in a local class within the
	 * innermost method.
	 */
	public boolean inCode() {
		return isDepType ? pop().inCode() : super.inCode();
	}
	
	public boolean inStaticContext() {
		return isDepType ? pop().inStaticContext() : super.inStaticContext();
	}
	
	/**
	 * Gets current class
	 */
	public ClassType currentClass() {
		return isDepType ? pop().currentClass() : super.currentClass();
	}
	
	/**
	 * Gets current class scope
	 */
	public ParsedClassType currentClassScope() {
		return isDepType ? pop().currentClassScope() : super.currentClassScope();
	}
	
	/**
	 * Adds a symbol to the current scoping level.
	 */
	public void addVariable(VarInstance vi) {
		assert (! isDepType);
		super.addVariable(vi);
	}
	
	/**
	 * Adds a named type object to the current scoping level.
	 */
	public void addNamed(Named t) {
		assert (! isDepType);
		super.addNamed(t);
	}
	

	public Named findInThisScope(String name) {
		assert (! isDepType);
		return super.findInThisScope(name);
	}
	
	public void addNamedToThisScope(Named type) {
		assert (! isDepType);
		super.addNamedToThisScope(type);
	}
	
	public ClassType findMethodContainerInThisScope(String name) {
		assert (! isDepType);
		return super.findMethodContainerInThisScope(name);
	
	}
	
	public VarInstance findVariableInThisScope(String name) {
		if (isDepType) {
			VarInstance vi = ((X10Context_c) pop()).findVariableInThisScope(name);
			if (vi instanceof LocalInstance)
				return vi;
			// otherwise it is a FieldInstance (might be a PropertyInstance, which is a FieldInstance)
			// See if the currentDepType has a field of this name. If so, that gets priority
			// and should be returned. The receiver must treat it as the reference
			// self.name. 
			VarInstance myVi = super.findVariableInThisScope(name);
			return myVi != null ? myVi : vi;
		}
		// the usual case.
		return super.findVariableInThisScope(name);
	}
	
	public void addVariableToThisScope(VarInstance var) {
		assert (! isDepType);
		super.addVariableToThisScope(var);
	}
	
	// New lookup methods added for deptypes.
	
	public PropertyInstance findProperty(String name) throws SemanticException {
		PropertyInstance pi=null;
		FieldInstance fi = findField(name);
		if (fi instanceof PropertyInstance) {
			pi = (PropertyInstance) pi;
		}
		return pi;
	}
	
	public ClassType findPropertyScope(String name) throws SemanticException {
		return findFieldScope(name);
	}
	/**
	 * Pushes on a deptype. Treat this as pushing a class. 
	 *TODO: Make this work with any X10NamedType, not just X10ParsedClassType.
	 * 
	 */
	public Context pushDepType(X10ParsedClassType type) {
		X10Context_c v = (X10Context_c) pushClass(type, type);
		v.isDepType = true;
		v.inCode = false;
		return v;
	}
	 public String toString() {
	        return "(" + (isDepType ? "depType" : kind) + " " + mapsToString() + " " + outer + ")";
	    }
}
