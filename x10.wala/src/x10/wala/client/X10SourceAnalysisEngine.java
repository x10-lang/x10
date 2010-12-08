package x10.wala.client;

import java.io.IOException;
import java.util.Set;

import x10.wala.classLoader.X10ClassLoaderFactoryImpl;
import x10.wala.client.impl.X10ZeroXCFABuilderFactory;
import x10.wala.ipa.callgraph.X10SourceAnalysisScope;
import x10.wala.ipa.cha.X10ClassHierarchy;

import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.CancelException;

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

    public CallGraph buildCallGraph(Iterable<Entrypoint> eps) throws IllegalArgumentException, CancelException, IOException {
        AnalysisOptions options = getDefaultOptions(eps);
        return buildCallGraph(getClassHierarchy(), options, true, null).makeCallGraph(options, null);
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
}
