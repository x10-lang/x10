package com.ibm.wala.cast.x10.client;

import java.io.IOException;
import java.util.Set;

import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.x10.classLoader.X10ClassLoaderFactoryImpl;
import com.ibm.wala.cast.x10.client.impl.X10ZeroXCFABuilderFactory;
import com.ibm.wala.cast.x10.ipa.callgraph.X10SourceAnalysisScope;
import com.ibm.wala.cast.x10.ipa.cha.X10ClassHierarchy;
import com.ibm.wala.cast.x10.loader.X10SourceLoaderImpl;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.HashSetFactory;

public class X10SourceAnalysisEngine extends JavaSourceAnalysisEngine {
    @Override
    protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions) {
	return new X10ClassLoaderFactoryImpl(exclusions);
    }

    @Override
    protected AnalysisScope makeSourceAnalysisScope() {
        return new X10SourceAnalysisScope();
    }

    @Override
    protected CallGraphBuilder getCallGraphBuilder(IClassHierarchy cha, AnalysisOptions options, AnalysisCache cache) {
        return new X10ZeroXCFABuilderFactory().make(options, cache, cha, scope, false);
    }

    @Override
    public void buildAnalysisScope() throws IOException {
        super.buildAnalysisScope();

        X10SourceAnalysisScope x10Scope= (X10SourceAnalysisScope) scope;
        ClassLoaderReference x10SourceLoader = x10Scope.getSourceLoader();
    }

    public CallGraph buildCallGraph(Iterable<Entrypoint> eps) throws IllegalArgumentException, CancelException, IOException {
        AnalysisOptions options = getDefaultOptions(eps);
        return buildCallGraph(getClassHierarchy(), options, true, null).makeCallGraph(options, null);
    }

    @Override
    public CallGraph buildDefaultCallGraph() throws IllegalArgumentException, CancelException, IOException {
        buildAnalysisScope();
        IClassHierarchy cha = buildClassHierarchy();
        setClassHierarchy(cha);
        return buildCallGraph(makeDefaultEntrypoints(scope, cha));
    }

    public X10ClassHierarchy initClassHierarchy() {
        X10ClassHierarchy cha = null;
        ClassLoaderFactory factory = getClassLoaderFactory(scope.getExclusions());
        try {
            cha = X10ClassHierarchy.make(getScope(), factory);
        } catch (ClassHierarchyException e) {
            System.err.println("Class Hierarchy construction failed");
        }
        return cha;
    }

    public void consolidateClassHierarchy() {
        try {
            ((X10ClassHierarchy) getClassHierarchy()).consolidate();
        } catch (ClassHierarchyException e) {
            System.err.println("Class Hierarchy construction failed");
        }
    }

    @Override
    public IClassHierarchy buildClassHierarchy() {
        X10ClassHierarchy cha = initClassHierarchy();
        try {
            cha.getLoader(X10SourceLoaderImpl.X10SourceLoader).init(cha.getScope().getModules(X10SourceLoaderImpl.X10SourceLoader));
            ((X10ClassHierarchy) cha).consolidate();
        } catch (ClassHierarchyException e) {
            System.err.println("Class Hierarchy construction failed");
        } catch (IOException e) {
            System.err.println("Class Hierarchy construction failed");
        }
        return cha;
    }
}
