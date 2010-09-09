package com.ibm.wala.cast.x10.client;

import java.io.IOException;
import java.util.Set;

import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.cast.java.translator.polyglot.PolyglotJavaSourceAnalysisEngine;
import com.ibm.wala.cast.x10.ipa.cha.X10ClassHierarchy;
import com.ibm.wala.cast.x10.loader.X10AnalysisScope;
import com.ibm.wala.cast.x10.loader.X10PrimordialClassLoader;
import com.ibm.wala.cast.x10.loader.X10SyntheticLoaderImpl;
import com.ibm.wala.cast.x10.translator.polyglot.X10ClassLoaderFactory;
import com.ibm.wala.cast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.wala.cast.x10.translator.polyglot.X10SourceLoaderImpl;
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
        ClassLoaderReference x10PrimordialLoader = x10Scope.getX10PrimordialLoader();
        ClassLoaderReference x10SourceLoader = x10Scope.getX10SourceLoader();

        for( Module M : this.x10SystemEntries) {
            scope.addToScope(x10PrimordialLoader, M);
        }

        for( Module M : this.x10SourceEntries) {
            scope.addToScope(x10SourceLoader, M);
        }
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
            cha.getLoader(X10PrimordialClassLoader.X10Primordial).init(cha.getScope().getModules(X10PrimordialClassLoader.X10Primordial));
            cha.getLoader(X10SourceLoaderImpl.X10SourceLoader).init(cha.getScope().getModules(X10SourceLoaderImpl.X10SourceLoader));
            cha.getLoader(X10SyntheticLoaderImpl.X10SyntheticLoader).init(cha.getScope().getModules(X10SyntheticLoaderImpl.X10SyntheticLoader));
            ((X10ClassHierarchy) cha).consolidate();
        } catch (ClassHierarchyException e) {
            System.err.println("Class Hierarchy construction failed");
        } catch (IOException e) {
            System.err.println("Class Hierarchy construction failed");
        }
        return cha;
    }
}
