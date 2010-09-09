package com.ibm.domo.ast.x10.client;

import org.eclipse.jdt.core.IJavaProject;

import com.ibm.domo.ast.x10.loader.X10Language;
import com.ibm.domo.ast.x10.translator.polyglot.X10ClassLoaderFactory;
import com.ibm.domo.ast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.JavaIRTranslatorExtension;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.eclipse.util.EclipseProjectSourceAnalysisEngine;

public class X10EclipseSourceAnalysisEngine extends EclipseProjectSourceAnalysisEngine {

    private IRTranslatorExtension fX10ExtInfo;

    private IRTranslatorExtension fJavaExtInfo;

    public X10EclipseSourceAnalysisEngine(IJavaProject javaProject) {
	super(javaProject, "x10");
	setCallGraphBuilderFactory(new X10ZeroXCFACallGraphBuilderFactory());
	fX10ExtInfo = new X10IRTranslatorExtension();
	fJavaExtInfo= new JavaIRTranslatorExtension();
    }

    @Override
    public IRTranslatorExtension getTranslatorExtension() {
	return fX10ExtInfo;
    }

    @Override
    protected void buildAnalysisScope() {
        super.buildAnalysisScope();
        this.scope.addLanguageToScope(X10Language.X10Lang);
    }

    @Override
    protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions, IRTranslatorExtension extInfo) {
	return new X10ClassLoaderFactory(exclusions, fX10ExtInfo, fJavaExtInfo);
    }

    @Override
    public PointerAnalysis getPointerAnalysis() {
	return super.getPointerAnalysis();
    }
}
