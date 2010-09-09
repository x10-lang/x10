/*
 * Created on Apr 28, 2006
 */
package com.ibm.wala.cast.x10.translator.polyglot;

import java.io.IOException;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.java.loader.JavaSourceLoaderImpl;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotClassLoaderFactory;
import com.ibm.wala.cast.x10.loader.X10PrimordialClassLoader;
import com.ibm.wala.cast.x10.translator.JavaFilteredSourceLoaderImpl;
import com.ibm.wala.classLoader.ClassLoaderImpl;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ide.util.EclipseProjectPath;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;

public class X10ClassLoaderFactory extends PolyglotClassLoaderFactory {

    public X10ClassLoaderFactory(SetOfClasses exclusions, IRTranslatorExtension x10ExtInfo, IRTranslatorExtension javaExtInfo) {
	super(exclusions, javaExtInfo);
	fExtensionMap.put(X10SourceLoaderImpl.X10SourceLoader, x10ExtInfo);
    }

    protected IClassLoader makeNewClassLoader(ClassLoaderReference classLoaderReference, IClassHierarchy cha, IClassLoader parent, AnalysisScope scope) throws IOException {
 	if (classLoaderReference.equals(X10SourceLoaderImpl.X10SourceLoader)) {
	    ClassLoaderImpl cl = new X10SourceLoaderImpl(classLoaderReference, parent, getExclusions(), cha, getExtensionFor(classLoaderReference));
	    cl.init( scope.getModules( classLoaderReference ));
	    return cl;
 	} else if (classLoaderReference.equals(X10PrimordialClassLoader.X10Primordial)) {
	    ClassLoaderImpl cl = new X10PrimordialClassLoader(classLoaderReference, scope.getArrayClassLoader(), parent, getExclusions(), cha);
	    cl.init( scope.getModules( classLoaderReference ));
	    return cl;
    } else if (classLoaderReference.equals(JavaSourceAnalysisScope.SOURCE)) {
		// Don't let the JavaSourceLoaderImpl handle Java source that's generated from
		// X10 source; that would create pseudo-duplicate classes. The following variant
		// just skips over such source files.
		ClassLoaderImpl cl = new JavaFilteredSourceLoaderImpl(classLoaderReference, parent, getExclusions(), cha, getExtensionFor(classLoaderReference));
        cl.init( scope.getModules( classLoaderReference ));
		return cl;
	} else {
	    return super.makeNewClassLoader(classLoaderReference, cha, parent, scope);
	}
    }
}
