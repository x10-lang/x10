package com.ibm.wala.cast.x10.loader;

import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.types.ClassLoaderReference;

import java.util.*;

public class X10AnalysisScope extends JavaSourceAnalysisScope {
    private static final Set<Language> languages = new HashSet<Language>(2);

    static {
      languages.add(Language.JAVA);
      languages.add(X10Language.X10Lang);
    }

    public X10AnalysisScope() {
	super(languages);

	X10SourceLoaderImpl.X10SourceLoader.setParent(X10PrimordialClassLoader.X10Primordial);
	X10PrimordialClassLoader.X10Primordial.setParent(ClassLoaderReference.Application);
	getSyntheticLoader().setParent(X10SourceLoaderImpl.X10SourceLoader);

	loadersByName.put(X10PrimordialClassLoader.X10PrimordialName, X10PrimordialClassLoader.X10Primordial);
	loadersByName.put(X10SourceLoaderImpl.X10SourceLoaderName, X10SourceLoaderImpl.X10SourceLoader);
	
	setLoaderImpl(X10SourceLoaderImpl.X10SourceLoader, "com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl");
	setLoaderImpl(X10PrimordialClassLoader.X10Primordial, "com.ibm.wala.cast.x10.loader.X10PrimordialClassLoader");
    }

    public ClassLoaderReference getX10PrimordialLoader() {
	return X10PrimordialClassLoader.X10Primordial;
    }

    public ClassLoaderReference getX10SourceLoader() {
	return X10SourceLoaderImpl.X10SourceLoader;
    }
}
