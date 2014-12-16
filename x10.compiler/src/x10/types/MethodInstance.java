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

package x10.types;

import java.util.List;

import polyglot.types.Context;
import polyglot.types.FunctionInstance;
import polyglot.types.MemberInstance;
import polyglot.types.MethodDef;

import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Use;
import x10.constraint.XTerm;

/**
 * @author vj
 *
 */
public interface MethodInstance 
   extends FunctionInstance<MethodDef>,
           MemberInstance<MethodDef>, 
           Use<MethodDef>,
           X10ProcedureInstance<MethodDef>, 
           X10Use<X10MethodDef> 
{
    MethodInstance error(SemanticException e);
    MethodInstance name(Name name);
    MethodInstance returnType(Type returnType);
    MethodInstance formalTypes(List<Type> formalTypes);
    MethodInstance throwTypes(List<Type> throwTypes);
    /**
     * The method's name.
     */
    Name name();

  
    
    /**
     * Get the list of methods this method (potentially) overrides, in order
     * from this class (i.e., including <code>this</code>) to super classes.
     * @param context TODO
     * @return A list of <code>MethodInstance</code>, starting with
     * <code>this</code>. Note that this list does not include methods declared
     * in interfaces. Use <code>implemented</code> for that.
     * @see polyglot.types.MethodDef
     */
    List<MethodInstance> overrides(Context context);

    /**
     * Return true if this method can override <code>mi</code>, false otherwise.
     * @param context TODO
     */
    boolean canOverride(MethodInstance mi, Context context);

    /**
     * Return true if this method can override <code>mi</code>, throws
     * a SemanticException otherwise.
     * @param context TODO
     */
    void checkOverride(MethodInstance mi, Context context) throws SemanticException;

    /**
     * Get the set of methods this method implements.  No ordering is
     * specified since the superinterfaces need not form a linear list
     * (i.e., they can form a tree).  
     * @param context TODO
     * @return List[MethodInstance]
     */
    List<MethodInstance> implemented(Context context); 

    /**
     * Return true if this method has the same signature as <code>mi</code>.
     * @param context TODO
     */
    boolean isSameMethod(MethodInstance mi, Context context);

    MethodInstance returnTypeRef(Ref<? extends Type> returnType);
    
    void setOrigMI(MethodInstance orig);
    MethodInstance origMI();
    

    /** Type to use in a RHS context rather than the return type. */
    Type rightType();

    XTerm body();
    MethodInstance body(XTerm body);
}
