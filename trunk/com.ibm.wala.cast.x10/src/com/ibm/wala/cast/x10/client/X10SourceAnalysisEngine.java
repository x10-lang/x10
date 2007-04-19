package com.ibm.domo.ast.x10.client;

import org.eclipse.jdt.core.IJavaProject;

import com.ibm.domo.ast.x10.translator.polyglot.X10ClassLoaderFactory;
import com.ibm.domo.ast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.wala.eclipse.util.EclipseProjectSourceAnalysisEngine;
import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.util.warnings.WarningSet;

public class X10SourceAnalysisEngine extends JavaSourceAnalysisEngine {

    public X10SourceAnalysisEngine() {
      setCallGraphBuilderFactory(new X10ZeroXCFACallGraphBuilderFactory());
    }

    @Override
    public IRTranslatorExtension getTranslatorExtension() {
        return new X10IRTranslatorExtension();
    }

    @Override
    protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions, WarningSet warnings, IRTranslatorExtension extInfo) {
	return new X10ClassLoaderFactory(exclusions, warnings, extInfo);
    }
}
