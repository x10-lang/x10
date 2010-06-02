package com.ibm.wala.cast.x10.client;

import java.io.InputStream;

import com.ibm.wala.cast.x10.ipa.callgraph.X10ZeroXCFABuilder;
import com.ibm.wala.cast.x10.loader.X10SyntheticLoaderImpl;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.ClassTargetSelector;
import com.ibm.wala.ipa.callgraph.MethodTargetSelector;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.summaries.BypassClassTargetSelector;
import com.ibm.wala.ipa.summaries.BypassMethodTargetSelector;
import com.ibm.wala.ipa.summaries.XMLMethodSummaryReader;

public class X10ZeroXCFACallGraphBuilderFactory {
	public static String RUNTIME_XML = "x10-runtime-model.xml";
	
	// TODO Add policy argument to ctor so client can control precision of pointer analysis
    public CallGraphBuilder make(AnalysisOptions options, AnalysisCache cache, IClassHierarchy cha, AnalysisScope scope, boolean keepPointsTo) {
        Util.addDefaultSelectors(options, cha);
//      Util.addDefaultBypassLogic(options, scope, Util.class.getClassLoader(), cha);
        addRuntimeModels(options, cha, scope);
        return new X10ZeroXCFABuilder(cha, options, cache, null, null, options.getReflectionSpec(), ZeroXInstanceKeys.ALLOCATIONS);
    }

    private void addRuntimeModels(AnalysisOptions options, IClassHierarchy cha, AnalysisScope scope) {
      // Adapted from Util.addBypassLogic(), which is hardwired to associate the standard
      // Java synthetic class loader with the ClassTargetSelector, but we need a synthetic
      // class loader that knows it's for X10. So we use the ClassLoaderReference from the
      // X10ClassLoaderFactory for Synthetic that's wired to the X10 language.
      InputStream s = getClass().getClassLoader().getResourceAsStream(RUNTIME_XML);
      XMLMethodSummaryReader summary = new XMLMethodSummaryReader(s, scope);

      MethodTargetSelector ms = new BypassMethodTargetSelector(options.getMethodTargetSelector(), summary.getSummaries(), summary
          .getIgnoredPackages(), cha);
      options.setSelector(ms);

      ClassTargetSelector cs = new BypassClassTargetSelector(options.getClassTargetSelector(), summary.getAllocatableClasses(), cha,
          cha.getLoader(X10SyntheticLoaderImpl.X10SyntheticLoader));
      options.setSelector(cs);
    }
}
