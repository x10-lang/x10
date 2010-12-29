package x10.wala.client;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import x10.wala.classLoader.X10ClassLoaderFactoryImpl;
import x10.wala.client.impl.X10ZeroXCFABuilderFactory;
import x10.wala.ipa.callgraph.X10SourceAnalysisScope;
import x10.wala.ipa.cha.X10ClassHierarchy;

import com.ibm.wala.cast.ir.ssa.AstIRFactory;
import com.ibm.wala.cast.java.client.JavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.classLoader.ClassLoaderFactory;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.SetOfClasses;
import com.ibm.wala.ipa.callgraph.propagation.PointerAnalysis;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.DefaultIRFactory;
import com.ibm.wala.ssa.SSAOptions;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.FileOfClasses;

public class X10SourceAnalysisEngine {
    /**
     * Governing call graph
     */
    protected CallGraph cg;

    /**
     * Results of pointer analysis
     */
    protected PointerAnalysis pointerAnalysis;

    /**
     * A representation of the analysis scope
     */
    protected AnalysisScope scope;

    /**
     * Governing class hierarchy
     */
    private IClassHierarchy cha;

    public IClassHierarchy getClassHierarchy() {
      return cha;
    }

    protected void setClassHierarchy(IClassHierarchy cha) {
      this.cha = cha;
    }

    protected AnalysisScope getScope() {
      return scope;
    }

    /**
     * A cache of IRs and stuff
     */
    private AnalysisCache cache = makeDefaultCache();

    public AnalysisCache makeDefaultCache() {
        return new AnalysisCache(AstIRFactory.makeDefaultFactory());
      }

    protected CallGraphBuilder buildCallGraph(IClassHierarchy cha, AnalysisOptions options, boolean savePointerAnalysis,
        IProgressMonitor monitor) throws IllegalArgumentException, CancelException {
      CallGraphBuilder builder = getCallGraphBuilder(cha, options, cache);

      cg = builder.makeCallGraph(options, monitor);

      if (savePointerAnalysis) {
        pointerAnalysis = builder.getPointerAnalysis();
      }

      return builder;
    }

    public AnalysisOptions getDefaultOptions(Iterable<Entrypoint> entrypoints) {
      AnalysisOptions options = new AnalysisOptions(getScope(), entrypoints);

      SSAOptions ssaOptions = new SSAOptions();
      ssaOptions.setDefaultValues(new SSAOptions.DefaultValues() {
        public int getDefaultValue(SymbolTable symtab, int valueNumber) {
          return symtab.getDefaultValue(valueNumber);
        }
      });

      options.setSSAOptions(ssaOptions);

      return options;
    }

    public IClassHierarchy buildClassHierarchy() {
        IClassHierarchy cha = null;
        ClassLoaderFactory factory = getClassLoaderFactory(scope.getExclusions());

        try {
          cha = ClassHierarchy.make(getScope(), factory);
        } catch (ClassHierarchyException e) {
          System.err.println("Class Hierarchy construction failed");
          System.err.println(e.toString());
          e.printStackTrace();
        }
        return cha;
      }

    protected ClassLoaderFactory getClassLoaderFactory(SetOfClasses exclusions) {
        return new X10ClassLoaderFactoryImpl(exclusions);
    }

    protected AnalysisScope makeSourceAnalysisScope() {
        return new X10SourceAnalysisScope();
    }

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


    public void buildAnalysisScope() throws IOException {
      scope = makeSourceAnalysisScope();
    }
}
