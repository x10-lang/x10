package com.ibm.wala.cast.x10.classLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.cast.x10.ipa.summaries.X10SyntheticLoaderImpl;
import com.ibm.wala.cast.x10.loader.PolyglotSourceLoaderImpl;
import com.ibm.wala.cast.x10.loader.X10SourceLoaderImpl;
import com.ibm.wala.classLoader.ClassLoaderFactoryImpl;
import com.ibm.wala.classLoader.ClassLoaderImpl;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;

public class X10ClassLoaderFactoryImpl extends ClassLoaderFactoryImpl {

     public X10ClassLoaderFactoryImpl(SetOfClasses exclusions) {
	super(exclusions);
    }

    protected IClassLoader makeNewClassLoader(ClassLoaderReference classLoaderReference, IClassHierarchy cha, IClassLoader parent, AnalysisScope scope) throws IOException {
 	if (classLoaderReference.equals(X10SourceLoaderImpl.X10SourceLoader)) {
	    ClassLoaderImpl cl = new X10SourceLoaderImpl(classLoaderReference, parent, getExclusions(), cha);
//	    cl.init( scope.getModules( classLoaderReference ));
	    return cl;
 	} else if (classLoaderReference.equals(X10PrimordialClassLoaderImpl.X10Primordial)) {
	    ClassLoaderImpl cl = new X10PrimordialClassLoaderImpl(classLoaderReference, scope.getArrayClassLoader(), parent, getExclusions(), cha);
//	    cl.init( scope.getModules( classLoaderReference ));
	    return cl;
 	} else if (classLoaderReference.equals(X10SyntheticLoaderImpl.X10SyntheticLoader)) {
 	    IClassLoader cl = new X10SyntheticLoaderImpl(classLoaderReference, parent, getExclusions(), cha);

// 	    cl.init(scope.getModules(classLoaderReference));
 	    return cl;
 	} else      if (classLoaderReference.equals(JavaSourceAnalysisScope.SOURCE)) {
        ClassLoaderImpl cl = new PolyglotSourceLoaderImpl(classLoaderReference, parent, getExclusions(), cha);
        cl.init(scope.getModules(classLoaderReference));
        return cl;
      } else {
        return super.makeNewClassLoader(classLoaderReference, cha, parent, scope);
      }

    }
}
