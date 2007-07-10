package com.ibm.domo.ast.x10.client;

import org.eclipse.jdt.core.IJavaProject;

import com.ibm.domo.ast.x10.translator.polyglot.X10ClassLoaderFactory;
import com.ibm.domo.ast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.eclipse.util.EclipseProjectSourceAnalysisEngine;

public class X10EclipseSourceAnalysisEngine extends EclipseProjectSourceAnalysisEngine {

	private IRTranslatorExtension fExt;
	
	public X10EclipseSourceAnalysisEngine(IJavaProject javaProject) {
	super(javaProject, "x10");
	setCallGraphBuilderFactory(new X10ZeroXCFACallGraphBuilderFactory());
		fExt = new X10IRTranslatorExtension();
    }

    @Override
    public IRTranslatorExtension getTranslatorExtension() {
        return fExt;
    }

    @Override
    protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions, IRTranslatorExtension extInfo) {
	return new X10ClassLoaderFactory(exclusions, extInfo);
    }
}
