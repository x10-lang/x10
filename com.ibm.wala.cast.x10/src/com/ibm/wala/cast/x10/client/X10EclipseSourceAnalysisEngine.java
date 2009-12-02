package com.ibm.wala.cast.x10.client;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.*;

import com.ibm.wala.cast.x10.analysis.X10EclipseProjectPath;
import com.ibm.wala.cast.x10.loader.*;
import com.ibm.wala.cast.x10.translator.polyglot.X10ClassLoaderFactory;
import com.ibm.wala.cast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.wala.cast.java.client.polyglot.EclipseProjectSourceAnalysisEngine;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.JavaIRTranslatorExtension;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.ide.util.EclipseProjectPath;

import java.io.*;
import java.util.Set;

public class X10EclipseSourceAnalysisEngine extends EclipseProjectSourceAnalysisEngine {

    private IRTranslatorExtension fX10ExtInfo;

    private IRTranslatorExtension fJavaExtInfo;

    /**
     * Modules which contain X10 system or library code
     */
    private final Set<Module> x10SystemEntries = HashSetFactory.make();

    /**
     * Modules which contain X10 source code
     */
    private final Set<Module> x10SourceEntries = HashSetFactory.make();

    public X10EclipseSourceAnalysisEngine(IJavaProject javaProject) 
	throws IOException, CoreException
    {
	super(javaProject, "x10");
	fX10ExtInfo = new X10IRTranslatorExtension();
	fJavaExtInfo= new JavaIRTranslatorExtension();
    }

    @Override
    public IRTranslatorExtension getTranslatorExtension() {
	return fX10ExtInfo;
    }

    @Override
    protected AnalysisScope makeSourceAnalysisScope() {
        return new X10AnalysisScope();
    }

    @Override
    protected CallGraphBuilder getCallGraphBuilder(IClassHierarchy cha, AnalysisOptions options, AnalysisCache cache) {
        return new X10ZeroXCFACallGraphBuilderFactory().make(options, cache, cha, scope, false);
    }

    /**
     * Create a variant of EclipseProjectPath that prevents the Primordial loader from
     * handling the X10 runtime, which is handled instead by the X10PrimordialClassLoader.
     * @throws CoreException 
     */
    @Override
    protected EclipseProjectPath createProjectPath(IJavaProject project) throws IOException, CoreException {
        return new X10EclipseProjectPath(project);
    }

    @Override
    protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions, IRTranslatorExtension extInfo) {
	return new X10ClassLoaderFactory(exclusions, fX10ExtInfo, fJavaExtInfo);
    }

    @Override
    public PointerAnalysis getPointerAnalysis() {
	return super.getPointerAnalysis();
    }

    /**
     * Adds the given module to the X10 primordial loader's module list. Clients
     * should/may call this method if they don't supply an IJavaProject to the
     * constructor.
     */
    public void addX10SystemModule(Module M) {
      x10SystemEntries.add(M);
    }

    /**
     * Adds the given module to the X10 source loader's module list. Clients
     * should/may call this method if they don't supply an IJavaProject to the
     * constructor.
     */
    public void addX10SourceModule(Module M) {
      x10SourceEntries.add(M);
    }

    @Override
	public void buildAnalysisScope() {
        super.buildAnalysisScope();

        X10AnalysisScope x10Scope= (X10AnalysisScope) scope;

        for( Module M : this.x10SystemEntries) {
            scope.addToScope(x10Scope.getX10PrimordialLoader(), M);
        }

        for( Module M : this.x10SourceEntries) {
            scope.addToScope(x10Scope.getX10SourceLoader(), M);
        }
    }
}
