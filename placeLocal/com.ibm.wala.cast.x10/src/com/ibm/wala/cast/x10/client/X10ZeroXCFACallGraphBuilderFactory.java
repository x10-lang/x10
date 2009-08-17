package com.ibm.wala.cast.x10.client;

import com.ibm.wala.cast.x10.ipa.callgraph.X10ZeroXCFABuilder;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class X10ZeroXCFACallGraphBuilderFactory {
	public static String RUNTIME_XML = "x10-runtime-model.xml";
	
	// TODO Add policy argument to ctor so client can control precision of pointer analysis
    public CallGraphBuilder make(AnalysisOptions options, AnalysisCache cache, IClassHierarchy cha, AnalysisScope scope, boolean keepPointsTo) {
        Util.addDefaultSelectors(options, cha);
        Util.addDefaultBypassLogic(options, scope, Util.class.getClassLoader(), cha);
        Util.addBypassLogic(options, scope, this.getClass().getClassLoader(), RUNTIME_XML, cha);
        return new X10ZeroXCFABuilder(cha, options, cache, null, null, options.getReflectionSpec(), ZeroXInstanceKeys.ALLOCATIONS);
    }
}
