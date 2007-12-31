package com.ibm.wala.cast.x10.client;

import org.eclipse.jdt.core.*;

import com.ibm.wala.cast.x10.loader.*;
import com.ibm.wala.cast.x10.translator.polyglot.X10ClassLoaderFactory;
import com.ibm.wala.cast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.JavaIRTranslatorExtension;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.eclipse.util.EclipseProjectSourceAnalysisEngine;

import java.io.*;

public class X10EclipseSourceAnalysisEngine extends EclipseProjectSourceAnalysisEngine {

    private IRTranslatorExtension fX10ExtInfo;

    private IRTranslatorExtension fJavaExtInfo;

    public X10EclipseSourceAnalysisEngine(IJavaProject javaProject) 
	throws JavaModelException, IOException
    {
	super(javaProject, "x10");
	setCallGraphBuilderFactory(new X10ZeroXCFACallGraphBuilderFactory());
	fX10ExtInfo = new X10IRTranslatorExtension();
	fJavaExtInfo= new JavaIRTranslatorExtension();
    }

    @Override
    public IRTranslatorExtension getTranslatorExtension() {
	return fX10ExtInfo;
    }

    protected AnalysisScope makeSourceAnalysisScope() {
        return new X10AnalysisScope();
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
