/*
 * Created on Apr 28, 2006
 */
package com.ibm.domo.ast.x10.translator.polyglot;

import java.io.IOException;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotClassLoaderFactory;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.wala.classLoader.ClassLoaderImpl;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.eclipse.util.*;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;

public class X10ClassLoaderFactory extends PolyglotClassLoaderFactory {

    public X10ClassLoaderFactory(SetOfClasses exclusions, IRTranslatorExtension extInfo) {
	super(exclusions, extInfo);
    }

    protected IClassLoader makeNewClassLoader(ClassLoaderReference classLoaderReference, IClassHierarchy cha, IClassLoader parent, AnalysisScope scope) throws IOException {
 	if (classLoaderReference.equals(EclipseProjectPath.SOURCE_REF)) {
	    ClassLoaderImpl cl = new X10SourceLoaderImpl(classLoaderReference, parent, getExclusions(), cha, fExtInfo);
	    cl.init( scope.getModules( classLoaderReference ));
	    return cl;
	} else {
	    return super.makeNewClassLoader(classLoaderReference, cha, parent, scope);
	}
    }
}
