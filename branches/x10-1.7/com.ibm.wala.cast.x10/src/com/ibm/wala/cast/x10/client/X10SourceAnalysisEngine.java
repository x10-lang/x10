package com.ibm.wala.cast.x10.client;

import java.io.IOException;
import java.util.Set;

import com.ibm.wala.cast.x10.loader.X10AnalysisScope;
import com.ibm.wala.cast.x10.translator.polyglot.*;
import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.translator.polyglot.*;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.collections.HashSetFactory;

public class X10SourceAnalysisEngine extends PolyglotJavaSourceAnalysisEngine {
    /**
     * Modules which contain X10 system or library code
     */
    private final Set<Module> x10SystemEntries = HashSetFactory.make();

    /**
     * Modules which contain X10 source code
     */
    private final Set<Module> x10SourceEntries = HashSetFactory.make();

    public X10SourceAnalysisEngine() {
//    setCallGraphBuilderFactory(new X10ZeroXCFACallGraphBuilderFactory());
    }

    @Override
    public IRTranslatorExtension getTranslatorExtension() {
        return new X10IRTranslatorExtension();
    }

    @Override
    protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions) {
	return new X10ClassLoaderFactory(exclusions, getTranslatorExtension(), super.getTranslatorExtension());
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
     * Adds the given module to the X10 primordial loader's module list. Clients
     * should/may call this method if they don't supply an IJavaProject to the
     * constructor.
     */
    public void addX10SystemModule(Module M) {
      x10SystemEntries.add(M);
    }

    /**
     * Adds the given module to the X10 primordial loader's module list. Clients
     * should/may call this method if they don't supply an IJavaProject to the
     * constructor.
     */
    public void addX10SourceModule(Module M) {
      x10SourceEntries.add(M);
    }

    @Override
	public void buildAnalysisScope() throws IOException {
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
