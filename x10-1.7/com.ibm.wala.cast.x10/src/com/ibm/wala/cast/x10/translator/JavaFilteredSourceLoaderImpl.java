package com.ibm.wala.cast.x10.translator;

import java.io.IOException;

import com.ibm.wala.cast.java.translator.SourceModuleTranslator;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotSourceLoaderImpl;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;

public class JavaFilteredSourceLoaderImpl extends PolyglotSourceLoaderImpl {

    public JavaFilteredSourceLoaderImpl(ClassLoaderReference loaderRef, IClassLoader parent, SetOfClasses exclusions, IClassHierarchy cha, IRTranslatorExtension extInfo) throws IOException {
	super(loaderRef, parent, exclusions, cha, extInfo);
    }

    @Override
    protected SourceModuleTranslator getTranslator() {
	return new FilteredPolyglotSourceModuleTranslator(cha.getScope(), getTranslatorExtension(), this);
    }
}
