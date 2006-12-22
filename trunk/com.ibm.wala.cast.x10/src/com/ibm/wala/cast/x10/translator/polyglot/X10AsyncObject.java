/*
 * Created on Apr 28, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import com.ibm.capa.ast.*;
import com.ibm.domo.ast.loader.AstFunctionClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.*;
import com.ibm.wala.types.TypeReference;

/**
 * Represents the synthesized type that contains a single synthesized method that houses the async code body
 * @author rfuhrer
 */
public class X10AsyncObject extends AstFunctionClass {
    private final ClassHierarchy cha;

    public X10AsyncObject(TypeReference reference, TypeReference superReference, IClassLoader loader, CAstSourcePositionMap.Position fileName, ClassHierarchy cha) {
	super(reference, superReference, loader, fileName);
	this.cha = cha;
    }

    public X10AsyncObject(TypeReference reference, IClassLoader loader, CAstSourcePositionMap.Position fileName, ClassHierarchy cha) {
	super(reference, loader, fileName);
	this.cha = cha;
    }

    public void setCodeBody(IMethod method) {
	functionBody= method;
    }

    public String toString() {
        return "Async" + super.toString();
    }

    public ClassHierarchy getClassHierarchy() {
      return cha;
    }
}
