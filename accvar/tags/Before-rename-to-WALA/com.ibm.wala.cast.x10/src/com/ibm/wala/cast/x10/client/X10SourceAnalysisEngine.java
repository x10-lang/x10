package com.ibm.domo.ast.x10.client;

import java.util.Set;

import com.ibm.domo.ast.x10.loader.X10AnalysisScope;
import com.ibm.domo.ast.x10.loader.X10Language;
import com.ibm.domo.ast.x10.translator.polyglot.X10ClassLoaderFactory;
import com.ibm.domo.ast.x10.translator.polyglot.X10IRTranslatorExtension;
import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.translator.polyglot.IRTranslatorExtension;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.util.collections.HashSetFactory;

public class X10SourceAnalysisEngine extends JavaSourceAnalysisEngine {
    /**
     * Modules which contain X10 system or library code
     */
    private final Set<Module> x10SystemEntries = HashSetFactory.make();

    /**
     * Modules which contain X10 source code
     */
    private final Set<Module> x10SourceEntries = HashSetFactory.make();

    public X10SourceAnalysisEngine() {
      setCallGraphBuilderFactory(new X10ZeroXCFACallGraphBuilderFactory());
    }

    @Override
    public IRTranslatorExtension getTranslatorExtension() {
        return new X10IRTranslatorExtension();
    }

    @Override
    protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions, IRTranslatorExtension extInfo) {
	return new X10ClassLoaderFactory(exclusions, extInfo, super.getTranslatorExtension());
    }

    @Override
    protected AnalysisScope makeSourceAnalysisScope() {
        return new X10AnalysisScope();
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
    protected void buildAnalysisScope() {
        super.buildAnalysisScope();

        X10AnalysisScope x10Scope= (X10AnalysisScope) scope;

        for( Module M : this.x10SystemEntries) {
            scope.addToScope(x10Scope.getX10PrimordialLoader(), M);
        }

        for( Module M : this.x10SourceEntries) {
            scope.addToScope(x10Scope.getX10SourceLoader(), M);
        }
        scope.addLanguageToScope(X10Language.X10Lang);
    }
}
