package com.ibm.domo.ast.x10.client;

import com.ibm.domo.ast.x10.ipa.callgraph.X10ZeroXCFABuilder;
import com.ibm.wala.client.CallGraphBuilderFactory;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.cfa.ZeroXInstanceKeys;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class X10ZeroXCFACallGraphBuilderFactory implements CallGraphBuilderFactory {
    // TODO Add policy argument to ctor so client can control precision of pointer analysis
    public CallGraphBuilder make(AnalysisOptions options, AnalysisCache cache, IClassHierarchy cha, AnalysisScope scope, boolean keepPointsTo) {
        Util.addDefaultSelectors(options, cha);
        Util.addDefaultBypassLogic(options, scope, Util.class.getClassLoader(), cha);
        return new X10ZeroXCFABuilder(cha, options, cache, null, null, options.getReflectionSpec(), ZeroXInstanceKeys.ALLOCATIONS);
    }
}
